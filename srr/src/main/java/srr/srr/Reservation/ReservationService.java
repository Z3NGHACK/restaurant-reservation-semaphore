package srr.srr.Reservation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.Semaphore;

@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final Semaphore semaphore = new Semaphore(1); // Allows only one concurrent reservation

    public ReservationService(ReservationRepo reservationRepo) {
        this.reservationRepo = reservationRepo;
    }
    public ReservationStatus getReservationStatus(Long id) {
        ReservationEntity reservationEntity = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));
        return reservationEntity.getStatus();  // Returns the status of the reservation
    }
    

    @Transactional 
    public ReservationEntity saveReservation(ReservationDto reservationDto) {
        try {
            semaphore.acquire(); 
            ReservationEntity reservationEntity;

            if (reservationDto.getTableId() != null) {
                reservationEntity = reservationRepo.findById(reservationDto.getTableId())
                        .orElse(new ReservationEntity());
            } else {
                reservationEntity = new ReservationEntity();
            }
            reservationEntity.setTableId(reservationDto.getTableId());
            reservationEntity.setCustomerName(reservationDto.getCustomerName());
            reservationEntity.setPhoneNumber(reservationDto.getPhoneNumber());
            reservationEntity.setTime(reservationDto.getTime());

            if (reservationEntity.getStatus() == null) {
                reservationEntity.setStatus(ReservationStatus.PENDING);
            }
            reservationEntity = reservationRepo.save(reservationEntity);

            return reservationEntity; 

        } catch (InterruptedException e) {
            // Handle the interruption (e.g., log the error)
            e.printStackTrace();
            // Optionally: Release the semaphore even in case of interruption
            semaphore.release(); 
            return null; // Or handle the error appropriately
        } finally {
            semaphore.release(); 
        }
    }

    // ... other service methods (e.g., findAll, findById, deleteById) ...

}