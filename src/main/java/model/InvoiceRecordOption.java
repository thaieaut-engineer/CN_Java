package model;

public class InvoiceRecordOption {
    private int recordId;
    private String petName;
    private String visitTime;

    public InvoiceRecordOption() {
    }

    public InvoiceRecordOption(int recordId, String petName, String visitTime) {
        this.recordId = recordId;
        this.petName = petName;
        this.visitTime = visitTime;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }

    @Override
    public String toString() {
        return "#" + recordId + "  ·  " + petName + "  ·  "
                + (visitTime == null ? "Chưa có ngày khám" : visitTime);
    }
}
