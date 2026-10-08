package bus;

import dao.DashboardDAO;
import java.sql.SQLException;
import model.DashboardData;

public class DashboardBUS {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardData getDashboard() throws SQLException {
        return dashboardDAO.loadDashboard();
    }
}
