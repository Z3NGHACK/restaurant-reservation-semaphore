package srr.srr.Reservation;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
public class ReservationDto {
    private Long id;
    @NotEmpty(message = "Name customer is empty")
    private String customerName;
    private Long tableId;
    @NotEmpty(message = "Phone number is empty")
    @Pattern(regexp = "\\d{10}", message = "Invalid phone number format")
    private String phoneNumber;
    @Future(message = "Time must be in the future")
    private LocalDateTime time;
    private ReservationStatus status;
    private String userType;

    public ReservationDto(@NotEmpty(message = "Name customer is empty") String customerName, Long tableId,
            @NotEmpty(message = "Phone number is empty") String phoneNumber,
            @NotEmpty(message = "Time is empty") LocalDateTime time, String status, String userType) {
        this.customerName = customerName;
        this.tableId = tableId;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.status = status != null ? ReservationStatus.valueOf(status) : ReservationStatus.PENDING;
        this.userType = userType;
    }

    public ReservationDto() {
    }

    public ReservationDto(ReservationEntity reservationEntity) {
        this.id = reservationEntity.getId();
        this.customerName = reservationEntity.getCustomerName();
        this.tableId = reservationEntity.getTableId() ;
        this.phoneNumber = reservationEntity.getPhoneNumber();
        this.time = reservationEntity.getTime();
        this.status = reservationEntity.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        try {
            this.status = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            this.status = ReservationStatus.PENDING; // Default value
        }
    }

    public Object getUserType() {
            return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
