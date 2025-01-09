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

            reservationEntity.setCustomerName(reservationDto.getCustomerName());
            reservationEntity.setPhoneNumber(reservationDto.getPhoneNumber());
            reservationEntity.setTime(reservationDto.getTime());

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