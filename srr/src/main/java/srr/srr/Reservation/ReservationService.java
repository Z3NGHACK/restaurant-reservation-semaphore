package srr.srr.Reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Semaphore;

@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final Semaphore semaphore = new Semaphore(1); // Allows only one concurrent reservation processing

    public ReservationService(ReservationRepo reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    /**
     * Fetch the reservation status by ID.
     */
    public ReservationStatus getReservationStatus(Long id) {
        ReservationEntity reservationEntity = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));
        return reservationEntity.getStatus(); // Returns the status of the reservation
    }

    /**
     * Save or update a reservation.
     */
    @Transactional
    public ReservationEntity saveReservation(ReservationDto reservationDto) {
        ReservationEntity reservationEntity = null;
        try {
            System.out.println("Attempting to acquire semaphore...");
            semaphore.acquire();
            System.out.println("Semaphore acquired. Available permits: " + semaphore.availablePermits());

            // Check if the customer already has an active reservation
            if (reservationRepo.existsByCustomerNameAndStatus(
                    reservationDto.getCustomerName(), ReservationStatus.PENDING)) {
                throw new IllegalStateException(
                        "Customer already has an active reservation.");
            }

            // Check if the reservation already exists or create a new one
            if (reservationDto.getTableId() != null) {
                reservationEntity = reservationRepo.findById(reservationDto.getTableId())
                        .orElse(new ReservationEntity());
            } else {
                reservationEntity = new ReservationEntity();
            }

            // Map DTO fields to Entity
            reservationEntity.setTableId(reservationDto.getTableId());
            reservationEntity.setCustomerName(reservationDto.getCustomerName());
            reservationEntity.setPhoneNumber(reservationDto.getPhoneNumber());
            reservationEntity.setTime(reservationDto.getTime());

            // Default status if not provided
            if (reservationEntity.getStatus() == null) {
                reservationEntity.setStatus(ReservationStatus.PENDING);
            }

            // Save the entity to the database
            reservationEntity = reservationRepo.save(reservationEntity);
            System.out.println("Reservation saved successfully.");

        } catch (InterruptedException e) {
            System.out.println("Semaphore acquisition interrupted.");
            e.printStackTrace();
        } finally {
            // Ensure the semaphore is always released
            semaphore.release();
            System.out.println("Semaphore released. Available permits: " + semaphore.availablePermits());
        }
        return reservationEntity;
    }
}
