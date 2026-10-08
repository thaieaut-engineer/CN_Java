package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class HistoryEntry {
    private Timestamp visitDate;
    private String petName;
    private String species;
    private String customerName;
    private String phone;
    private String branchName;
    private String doctor;
    private String diagnosis;
    private String notes;
    private Timestamp revisitDate;
    private BigDecimal totalAmount;

    public HistoryEntry() {
    }

    public HistoryEntry(Timestamp visitDate, String petName, String species, String customerName,
            String phone, String branchName, String doctor, String diagnosis, String notes,
            Timestamp revisitDate, BigDecimal totalAmount) {
        this.visitDate = visitDate;
        this.petName = petName;
        this.species = species;
        this.customerName = customerName;
        this.phone = phone;
        this.branchName = branchName;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.revisitDate = revisitDate;
        this.totalAmount = totalAmount;
    }

    public Timestamp getVisitDate() { return visitDate; }
    public void setVisitDate(Timestamp visitDate) { this.visitDate = visitDate; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getRevisitDate() { return revisitDate; }
    public void setRevisitDate(Timestamp revisitDate) { this.revisitDate = revisitDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
