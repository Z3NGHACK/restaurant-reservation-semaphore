package srr.srr.Reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final Semaphore semaphore = new Semaphore(1); // Allows only one concurrent reservation processing
    private Long currentProcessingReservationId = null; // Tracks the currently processed reservation

    public ReservationService(ReservationRepo reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    /**
     * Confirm a reservation by ID.
     */
    @Transactional
    public String confirmReservation(Long id) {
        try {
            // Attempt to acquire the semaphore
            System.out.println("Attempting to acquire semaphore...");
            if (!semaphore.tryAcquire()) {
                return "Another reservation is being processed. Please wait.";
            }
            System.out.println("Semaphore acquired. Processing reservation ID: " + id);

            // Fetch the reservation and confirm it
            ReservationEntity reservationEntity = reservationRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));

            if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
                semaphore.release(); // Release semaphore if reservation is not pending
                return "Reservation is not in a pending state.";
            }

            reservationEntity.setStatus(ReservationStatus.CONFIRMED);
            reservationRepo.save(reservationEntity);
            currentProcessingReservationId = id;

            return "Reservation ID " + id + " has been confirmed successfully.";

        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while confirming the reservation.";
        }
    }

    /**
     * Release the current reservation and auto-confirm the lowest ID.
     */
    @Transactional
    public String releaseAndAutoConfirm() {
        try {
            if (currentProcessingReservationId == null) {
                return "No reservation is currently being processed.";
            }

            System.out.println("Releasing semaphore for reservation ID: " + currentProcessingReservationId);
            currentProcessingReservationId = null; // Clear the currently processed reservation
            semaphore.release(); // Release the semaphore

            // Auto-confirm the reservation with the lowest ID
            Optional<ReservationEntity> nextReservation = reservationRepo.findAll().stream()
                    .filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING)
                    .sorted((r1, r2) -> r1.getTableId().compareTo(r2.getTableId())) // Sort by lowest ID
                    .findFirst();

            if (nextReservation.isPresent()) {
                ReservationEntity reservationEntity = nextReservation.get();
                confirmReservation(reservationEntity.getTableId());
                return "Released current reservation. Auto-confirmed reservation ID: " 
                        + reservationEntity.getTableId();
            }

            return "Released current reservation. No pending reservations to auto-confirm.";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while releasing the reservation.";
        }
    }
}
