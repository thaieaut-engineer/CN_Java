package bus;

import dao.ReportsDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import model.RevenueReport;

public class ReportsBUS {
    private final ReportsDAO reportsDAO = new ReportsDAO();

    public RevenueReport getRevenueByBranch(LocalDate from, LocalDate to) throws SQLException {
        if (from == null || to == null || to.isBefore(from)) {
            throw new IllegalArgumentException("Khoảng ngày báo cáo không hợp lệ.");
        }
        return reportsDAO.loadRevenueByBranch(from, to);
    }
}
