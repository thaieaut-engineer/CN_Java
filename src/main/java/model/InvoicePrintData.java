package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvoicePrintData {
    private int invoiceId;
    private Timestamp createdDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String customerName;
    private String phone;
    private String petName;
    private String branchName;
    private String doctor;
    private List<InvoiceLine> lines = new ArrayList<>();

    public InvoicePrintData() {
    }

    public InvoicePrintData(int invoiceId, Timestamp createdDate, BigDecimal totalAmount,
            String status, String paymentMethod, String customerName, String phone,
            String petName, String branchName, String doctor, List<InvoiceLine> lines) {
        this.invoiceId = invoiceId;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.phone = phone;
        this.petName = petName;
        this.branchName = branchName;
        this.doctor = doctor;
        setLines(lines);
    }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    public List<InvoiceLine> getLines() { return lines; }
    public void setLines(List<InvoiceLine> lines) {
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
    }
}
