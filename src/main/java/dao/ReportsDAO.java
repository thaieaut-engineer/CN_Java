package dao;

import config.DatabaseConnection;
import model.RevenueBranchRow;
import model.RevenueReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {
    public RevenueReport loadRevenueByBranch(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT b.name AS branch_name, COUNT(i.invoice_id) AS invoice_count, "
                + "COALESCE(SUM(i.total_amount), 0) AS revenue FROM Branch b "
                + "LEFT JOIN MedicalRecord mr ON mr.branch_id=b.branch_id "
                + "LEFT JOIN Invoice i ON i.record_id=mr.record_id AND i.status=N'Paid' "
                + "AND i.created_date >= ? AND i.created_date < ? "
                + "GROUP BY b.branch_id,b.name ORDER BY b.name";
        List<String> labels = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<RevenueBranchRow> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        long invoices = 0;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(from));
            statement.setDate(2, Date.valueOf(to.plusDays(1)));
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    String branch = results.getString("branch_name");
                    long count = results.getLong("invoice_count");
                    BigDecimal revenue = results.getBigDecimal("revenue");
                    labels.add(branch);
                    amounts.add(revenue);
                    rows.add(new RevenueBranchRow(branch, count, revenue));
                    total = total.add(revenue);
                    invoices += count;
                }
            }
        }
        rows.add(new RevenueBranchRow("TỔNG CỘNG", invoices, total));
        return new RevenueReport(rows, labels, amounts);
    }
}
