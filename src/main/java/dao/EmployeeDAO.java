/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
import model.Branch;
import model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.PasswordUtil;

public class EmployeeDAO {

    /**
     * Kiểm tra thông tin đăng nhập của nhân viên
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Đối tượng Employee nếu đúng thông tin, ngược lại trả về null
     */
    public Employee login(String username, String password) throws SQLException {
        String sql = "SELECT employee_id, branch_id, full_name, role, username, password, account_status "
                + "FROM Employee WHERE LTRIM(RTRIM(username)) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int employeeId;
            int branchId;
            String fullName;
            String role;
            String actualUsername;
            String storedPassword;
            String accountStatus;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                employeeId = rs.getInt("employee_id");
                branchId = rs.getInt("branch_id");
                fullName = rs.getString("full_name");
                role = rs.getString("role");
                actualUsername = rs.getString("username");
                storedPassword = rs.getString("password");
                accountStatus = rs.getString("account_status");
            }
            if (!PasswordUtil.verify(password, storedPassword)) {
                return null;
            }
            if (!PasswordUtil.isHashed(storedPassword)) {
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE Employee SET password = ? WHERE employee_id = ?")) {
                    update.setString(1, PasswordUtil.hash(password));
                    update.setInt(2, employeeId);
                    update.executeUpdate();
                }
            }
            return new Employee(employeeId, branchId, fullName, role, actualUsername, "", accountStatus);
        } catch (SQLException e) {
            throw explainMissingApprovalColumn(e);
        }
    }

    public java.util.List<Branch> findBranches() throws SQLException {
        String sql = "SELECT branch_id, name, address, phone FROM Branch ORDER BY name";
        java.util.List<Branch> branches = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                branches.add(new Branch(results.getInt("branch_id"), results.getString("name"),
                        results.getString("address"), results.getString("phone")));
            }
        }
        return branches;
    }

    public void register(String fullName, String username, String password, int branchId) throws SQLException {
        String sql = "INSERT INTO Employee (branch_id, full_name, role, username, password, account_status) "
                + "VALUES (?, ?, N'NhanVien', ?, ?, N'Pending')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, branchId);
            statement.setNString(2, fullName.trim());
            statement.setString(3, username.trim());
            statement.setString(4, PasswordUtil.hash(password));
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2601 || e.getErrorCode() == 2627) {
                throw new SQLException("Tên đăng nhập đã được sử dụng. Hãy chọn tên khác.", e);
            }
            throw explainMissingApprovalColumn(e);
        }
    }

    private SQLException explainMissingApprovalColumn(SQLException exception) {
        for (SQLException cause = exception; cause != null; cause = cause.getNextException()) {
            String message = cause.getMessage();
            if (cause.getErrorCode() == 207
                    || (message != null && message.toLowerCase(java.util.Locale.ROOT)
                            .contains("invalid column name 'account_status'"))) {
                return new SQLException("Database chưa được cập nhật cho chức năng đăng ký. "
                        + "Hãy mở account_approval.sql trong thư mục dự án, chọn cơ sở dữ liệu PetClinicDB "
                        + "trong SQL Server Management Studio và thực thi script, sau đó khởi động lại ứng dụng.",
                        exception);
            }
        }
        return exception;
    }
}