package model;

import java.math.BigDecimal;

public class RevenueBranchRow {
    private String branchName;
    private long invoiceCount;
    private BigDecimal revenue;

    public RevenueBranchRow() {
    }

    public RevenueBranchRow(String branchName, long invoiceCount, BigDecimal revenue) {
        this.branchName = branchName;
        this.invoiceCount = invoiceCount;
        this.revenue = revenue;
    }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public long getInvoiceCount() { return invoiceCount; }
    public void setInvoiceCount(long invoiceCount) { this.invoiceCount = invoiceCount; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
