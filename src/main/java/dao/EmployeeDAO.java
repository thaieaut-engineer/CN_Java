/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import config.DatabaseConnection;
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
        String sql = "SELECT employee_id, branch_id, full_name, role, username, password "
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
            return new Employee(employeeId, branchId, fullName, role, actualUsername, "");
        }
    }
}