package srr.srr.Reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {
    @Query("SELECT r FROM ReservationEntity r ORDER BY r.time DESC")
    List<ReservationEntity> findAllByOrderByTimeDesc();

    boolean existsByCustomerNameAndStatus(String customerName, ReservationStatus pending);

    ReservationEntity findByTableIdAndCustomerName(Long tableId, String customerName);

    
}