package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RevenueReport {
    private List<RevenueBranchRow> rows = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private List<BigDecimal> amounts = new ArrayList<>();

    public RevenueReport() {
    }

    public RevenueReport(List<RevenueBranchRow> rows, List<String> labels, List<BigDecimal> amounts) {
        setRows(rows);
        setLabels(labels);
        setAmounts(amounts);
    }

    public List<RevenueBranchRow> getRows() { return rows; }
    public void setRows(List<RevenueBranchRow> rows) {
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) {
        this.labels = Collections.unmodifiableList(new ArrayList<>(labels));
    }
    public List<BigDecimal> getAmounts() { return amounts; }
    public void setAmounts(List<BigDecimal> amounts) {
        this.amounts = Collections.unmodifiableList(new ArrayList<>(amounts));
    }
}
