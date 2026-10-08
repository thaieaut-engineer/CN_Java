package model;

import java.sql.Timestamp;

public class AppointmentSummary {
    private Timestamp appointmentDate;
    private String petName;
    private String customerName;
    private String branchName;
    private String status;

    public AppointmentSummary() {
    }

    public AppointmentSummary(Timestamp appointmentDate, String petName, String customerName,
            String branchName, String status) {
        this.appointmentDate = appointmentDate;
        this.petName = petName;
        this.customerName = customerName;
        this.branchName = branchName;
        this.status = status;
    }

    public Timestamp getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Timestamp appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
