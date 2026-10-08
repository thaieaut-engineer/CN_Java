package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InventoryMovement {
    private String movementType;
    private Timestamp movementDate;
    private int serviceId;
    private String serviceName;
    private int quantity;
    private BigDecimal unitPrice;
    private String employeeName;
    private String notes;

    public InventoryMovement() {
    }

    public InventoryMovement(String movementType, Timestamp movementDate, int serviceId,
            String serviceName, int quantity, BigDecimal unitPrice, String employeeName, String notes) {
        this.movementType = movementType;
        this.movementDate = movementDate;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.employeeName = employeeName;
        this.notes = notes;
    }

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public Timestamp getMovementDate() { return movementDate; }
    public void setMovementDate(Timestamp movementDate) { this.movementDate = movementDate; }
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
