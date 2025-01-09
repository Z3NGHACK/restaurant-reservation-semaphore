package srr.srr.Reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    private String customerName;
    private String phoneNumber;
    private String time;
    private String status;
    public ReservationEntity() {
    }
    public ReservationEntity(String customerName, String phoneNumber, String time, String status) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.status = status;
    }
    public ReservationEntity(Long tableId, String customerName, String phoneNumber, String time, String status) {
        this.tableId = tableId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.status = status;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
