package srr.srr.Reservation;

import jakarta.validation.constraints.NotEmpty;

public class ReservationDto {
    private Long id;
    @NotEmpty(message = "Name customer is empty")
    private String customerName;
    private Long tableId;
    @NotEmpty(message = "Phone number is empty")
    
    private String phoneNumber;

    private String time;
    private ReservationStatus status;
    private String userType;

    public ReservationDto(@NotEmpty(message = "Name customer is empty") String customerName, Long tableId,
            @NotEmpty(message = "Phone number is empty") String phoneNumber,
            @NotEmpty(message = "Time is empty") String time, String status, String userType) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = ReservationStatus.valueOf(status.toUpperCase());
    }

    public Object getUserType() {
            return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}