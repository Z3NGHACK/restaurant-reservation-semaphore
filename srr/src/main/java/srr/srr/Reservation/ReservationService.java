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
    
            // Retrieve and delete the current reservation
            Optional<ReservationEntity> currentReservation = reservationRepo.findById(currentProcessingReservationId);
            if (currentReservation.isPresent()) {
                ReservationEntity reservation = currentReservation.get();
    
                // Log and delete based on the status of the reservation
                if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                    System.out.println("Deleting confirmed reservation: " + reservation.getId());
                } else {
                    System.out.println("Deleting pending reservation: " + reservation.getId());
                }
                reservationRepo.delete(reservation);
            }
            currentProcessingReservationId = null; // Reset the current reservation ID
    
            // Auto-confirm the next pending reservation with the lowest tableId
            Optional<ReservationEntity> nextReservation = reservationRepo.findAll().stream()
                    .filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING)
                    .sorted((r1, r2) -> r1.getId().compareTo(r2.getId())) // Sort by lowest tableId
                    .findFirst();
    
            if (nextReservation.isPresent()) {
                ReservationEntity next = nextReservation.get();
    
                // Confirm the next reservation
                next.setStatus(ReservationStatus.CONFIRMED);
                reservationRepo.save(next); // Save changes to the database
    
                // Log the auto-confirmation
                System.out.println("Auto-confirmed reservation ID: " + next.getId());
                currentProcessingReservationId = next.getId(); // Update the currently processed reservation ID
                return "Released current reservation. Auto-confirmed reservation ID: " + next.getTableId();
            }
    
            return "Released current reservation. No pending reservations to auto-confirm.";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while releasing the reservation.";
        }
        finally {
            // Ensure semaphore is released if held
            semaphore.release();
        }
    }
    

}
