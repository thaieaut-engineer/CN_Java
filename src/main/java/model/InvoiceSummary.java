package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InvoiceSummary {
    private int invoiceId;
    private int recordId;
    private String petName;
    private String customerName;
    private String branchName;
    private Timestamp createdDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;

    public InvoiceSummary() {
    }

    public InvoiceSummary(int invoiceId, int recordId, String petName, String customerName,
            String branchName, Timestamp createdDate, BigDecimal totalAmount, String status,
            String paymentMethod) {
        this.invoiceId = invoiceId;
        this.recordId = recordId;
        this.petName = petName;
        this.customerName = customerName;
        this.branchName = branchName;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
