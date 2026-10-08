package dao;

import config.DatabaseConnection;
import model.InventoryMovement;
import model.StockOption;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    public List<StockOption> findStockOptions() throws SQLException {
        String sql = "SELECT service_id, name, stock_quantity FROM Service "
                + "WHERE type IN (N'Thuoc', N'TiemPhong') ORDER BY name";
        List<StockOption> options = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                options.add(new StockOption(results.getInt("service_id"), results.getString("name"),
                        results.getInt("stock_quantity")));
            }
        }
        return options;
    }

    public List<InventoryMovement> findMovements() throws SQLException {
        String sql = "SELECT movement_type, movement_date, service_id, service_name, quantity, unit_price, "
                + "employee_name, notes FROM ("
                + "SELECT N'Nhập' AS movement_type, r.import_date AS movement_date, s.service_id, s.name AS service_name, "
                + "r.quantity, r.import_price AS unit_price, e.full_name AS employee_name, N'' AS notes "
                + "FROM InventoryReceipt r JOIN Service s ON s.service_id=r.service_id "
                + "JOIN Employee e ON e.employee_id=r.employee_id "
                + "UNION ALL SELECT N'Xuất', i.issue_date, s.service_id, s.name, i.quantity, NULL, e.full_name, i.notes "
                + "FROM InventoryIssue i JOIN Service s ON s.service_id=i.service_id "
                + "JOIN Employee e ON e.employee_id=i.employee_id"
                + ") movements ORDER BY movement_date DESC";
        List<InventoryMovement> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                rows.add(new InventoryMovement(results.getString("movement_type"),
                        results.getTimestamp("movement_date"), results.getInt("service_id"),
                        results.getString("service_name"), results.getInt("quantity"),
                        results.getBigDecimal("unit_price"), results.getString("employee_name"),
                        results.getString("notes")));
            }
        }
        return rows;
    }

    public void receive(int serviceId, int employeeId, int quantity, BigDecimal price) throws SQLException {
        String updateSql = "UPDATE Service SET stock_quantity = ISNULL(stock_quantity, 0) + ? "
                + "WHERE service_id = ? AND type IN (N'Thuoc', N'TiemPhong')";
        String insertSql = "INSERT INTO InventoryReceipt (service_id, employee_id, quantity, import_price) "
                + "VALUES (?, ?, ?, ?)";
        inTransaction(connection -> {
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setInt(1, quantity);
                update.setInt(2, serviceId);
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Không tìm thấy thuốc/vắc-xin có mã " + serviceId + ".");
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, serviceId);
                insert.setInt(2, employeeId);
                insert.setInt(3, quantity);
                insert.setBigDecimal(4, price);
                insert.executeUpdate();
            }
        });
    }

    public void issue(int serviceId, int employeeId, int quantity, String reason) throws SQLException {
        String updateSql = "UPDATE Service SET stock_quantity = stock_quantity - ? "
                + "WHERE service_id = ? AND type IN (N'Thuoc', N'TiemPhong') AND stock_quantity >= ?";
        String insertSql = "INSERT INTO InventoryIssue (service_id, employee_id, quantity, notes) "
                + "VALUES (?, ?, ?, ?)";
        inTransaction(connection -> {
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setInt(1, quantity);
                update.setInt(2, serviceId);
                update.setInt(3, quantity);
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Không đủ tồn kho hoặc mã thuốc/vắc-xin không hợp lệ.");
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, serviceId);
                insert.setInt(2, employeeId);
                insert.setInt(3, quantity);
                insert.setNString(4, reason);
                insert.executeUpdate();
            }
        });
    }

    private void inTransaction(SqlWork work) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                work.execute(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    @FunctionalInterface
    private interface SqlWork {
        void execute(Connection connection) throws SQLException;
    }

}
