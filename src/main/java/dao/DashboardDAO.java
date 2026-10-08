package dao;

import config.DatabaseConnection;
import model.AppointmentSummary;
import model.DashboardData;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {
    public DashboardData loadDashboard() throws SQLException {
        DashboardData data = new DashboardData();
        String metricsSql = "SELECT "
                + "(SELECT COUNT(*) FROM Branch) AS branches, "
                + "(SELECT COUNT(*) FROM Employee) AS employees, "
                + "(SELECT COUNT(*) FROM Customer) AS customers, "
                + "(SELECT COUNT(*) FROM Pet) AS pets, "
                + "(SELECT COUNT(*) FROM Appointment WHERE appointment_date >= CONVERT(date,GETDATE()) "
                + "AND appointment_date < DATEADD(day,1,CONVERT(date,GETDATE())) "
                + "AND status <> N'Cancelled') AS today_appointments, "
                + "(SELECT COALESCE(SUM(total_amount),0) FROM Invoice WHERE status=N'Paid' "
                + "AND created_date >= DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1) "
                + "AND created_date < DATEADD(month,1,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1))) AS month_revenue, "
                + "(SELECT COALESCE(SUM(total_amount),0) FROM Invoice WHERE status=N'Unpaid') AS unpaid_amount, "
                + "(SELECT COUNT(*) FROM Service WHERE type IN (N'Thuoc',N'TiemPhong') "
                + "AND COALESCE(stock_quantity,0) <= 5) AS low_stock";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(metricsSql);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                data.setBranches(result.getLong("branches"));
                data.setEmployees(result.getLong("employees"));
                data.setCustomers(result.getLong("customers"));
                data.setPets(result.getLong("pets"));
                data.setTodayAppointments(result.getLong("today_appointments"));
                data.setMonthRevenue(result.getBigDecimal("month_revenue"));
                data.setUnpaidAmount(result.getBigDecimal("unpaid_amount"));
                data.setLowStock(result.getLong("low_stock"));
            }
            loadAppointments(connection, data);
            loadMonthlyRevenue(connection, data);
        }
        return data;
    }

    private void loadAppointments(Connection connection, DashboardData data) throws SQLException {
        String sql = "SELECT TOP 8 a.appointment_date,p.name AS pet_name,c.full_name AS customer_name,"
                + "b.name AS branch_name,a.status FROM Appointment a "
                + "JOIN Pet p ON p.pet_id=a.pet_id JOIN Customer c ON c.customer_id=a.customer_id "
                + "JOIN Branch b ON b.branch_id=a.branch_id "
                + "WHERE a.appointment_date >= CONVERT(date,GETDATE()) AND a.status IN (N'Pending',N'Confirmed') "
                + "ORDER BY a.appointment_date";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                data.addAppointment(new AppointmentSummary(results.getTimestamp("appointment_date"),
                        results.getString("pet_name"), results.getString("customer_name"),
                        results.getString("branch_name"), results.getString("status")));
            }
        }
    }

    private void loadMonthlyRevenue(Connection connection, DashboardData data) throws SQLException {
        String sql = "SELECT DATEFROMPARTS(YEAR(created_date),MONTH(created_date),1) AS revenue_month,"
                + "COALESCE(SUM(total_amount),0) AS revenue FROM Invoice "
                + "WHERE status=N'Paid' AND created_date >= DATEADD(month,-5,"
                + "DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1)) "
                + "AND created_date < DATEADD(month,1,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1)) "
                + "GROUP BY DATEFROMPARTS(YEAR(created_date),MONTH(created_date),1) ORDER BY revenue_month";
        YearMonth firstMonth = YearMonth.now().minusMonths(5);
        for (int index = 0; index < 6; index++) {
            data.addMonthLabel(firstMonth.plusMonths(index).toString());
            data.addMonthRevenue(BigDecimal.ZERO);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                java.sql.Date date = results.getDate("revenue_month");
                YearMonth month = YearMonth.from(date.toLocalDate());
                int index = (int) ChronoUnit.MONTHS.between(firstMonth, month);
                if (index >= 0 && index < data.getMonthRevenues().size()) {
                    data.setMonthRevenue(index, results.getBigDecimal("revenue"));
                }
            }
        }
    }

}
