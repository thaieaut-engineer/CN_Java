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

public class EmployeeDAO {

    /**
     * Kiểm tra thông tin đăng nhập của nhân viên
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Đối tượng Employee nếu đúng thông tin, ngược lại trả về null
     */
    public Employee login(String username, String password) {
        String sql = "SELECT * FROM Employee WHERE LTRIM(RTRIM(username)) = ? AND LTRIM(RTRIM(password)) = ?";
        
        // In ra để debug (kiểm tra chuỗi truyền vào)
    System.out.println("DEBUG - Username gui len: [" + username + "]");
    System.out.println("DEBUG - Password gui len: [" + password + "]");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("employee_id"),
                        rs.getInt("branch_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra đăng nhập!");
            e.printStackTrace();
        }
        return null;
    }
}