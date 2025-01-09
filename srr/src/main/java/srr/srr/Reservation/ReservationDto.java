package srr.srr.Reservation;

import jakarta.validation.constraints.NotEmpty;

public class ReservationDto {
    @NotEmpty(message = "Name customer is empty")
    private String customerName;

    private Long tableId;

    @NotEmpty(message = "Phone number is empty")
    private String phoneNumber;

    @NotEmpty(message = "Time is empty")
    private String time;
    
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
