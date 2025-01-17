package srr.srr.Reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 6)
    private Long tableId;

    @Column(nullable = false)
    private String customerName;
    private String phoneNumber;
    private String time;
    @Enumerated(EnumType.STRING)  // Store enum as string in DB
    private ReservationStatus status = ReservationStatus.PENDING; // Default status

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public ReservationEntity() {
    }

    public ReservationEntity(Long tableId, String customerName, String phoneNumber, String time, ReservationStatus status) {
        this.tableId = tableId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.status = status != null ? status : ReservationStatus.PENDING; 
    }
    public Long getTableId() {
        return tableId;
    }
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void updateFromDTO(ReservationDto reservationDto) {
        this.tableId = reservationDto.getTableId();
        this.customerName = reservationDto.getCustomerName();
        this.phoneNumber = reservationDto.getPhoneNumber();
        this.time = reservationDto.getTime();
        if (reservationDto.getStatus() != null) {
            this.status = reservationDto.getStatus();
        } else {
            this.status = ReservationStatus.PENDING; // Default to PENDING
        }
    }
}