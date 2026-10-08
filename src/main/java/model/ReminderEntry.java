package model;

import java.sql.Timestamp;

public class ReminderEntry {
    private String reminderType;
    private Timestamp dueDate;
    private String petName;
    private String customerName;
    private String phone;
    private String branchName;
    private String details;

    public ReminderEntry() {
    }

    public ReminderEntry(String reminderType, Timestamp dueDate, String petName, String customerName,
            String phone, String branchName, String details) {
        this.reminderType = reminderType;
        this.dueDate = dueDate;
        this.petName = petName;
        this.customerName = customerName;
        this.phone = phone;
        this.branchName = branchName;
        this.details = details;
    }

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }
    public Timestamp getDueDate() { return dueDate; }
    public void setDueDate(Timestamp dueDate) { this.dueDate = dueDate; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
