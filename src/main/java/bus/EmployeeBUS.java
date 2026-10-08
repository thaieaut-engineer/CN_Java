package bus;

import dao.EmployeeDAO;
import java.sql.SQLException;
import java.util.List;
import model.Branch;
import model.Employee;

public class EmployeeBUS {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public Employee login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập và mật khẩu.");
        }
        Employee employee = employeeDAO.login(username.trim(), password);
        if (employee != null && !"Active".equalsIgnoreCase(employee.getAccountStatus())) {
            if ("Pending".equalsIgnoreCase(employee.getAccountStatus())) {
                throw new SQLException("Tài khoản đang chờ Admin phê duyệt.");
            }
            throw new SQLException("Tài khoản chưa được kích hoạt. Vui lòng liên hệ Admin.");
        }
        return employee;
    }

    public List<Branch> getBranches() throws SQLException {
        return employeeDAO.findBranches();
    }

    public void register(String fullName, String username, String password, int branchId) throws SQLException {
        if (fullName == null || fullName.isBlank() || fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Họ tên không được để trống hoặc vượt quá 100 ký tự.");
        }
        if (username == null || !username.trim().matches("[A-Za-z0-9._-]{4,50}")) {
            throw new IllegalArgumentException("Tên đăng nhập phải dài 4–50 ký tự, gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (branchId <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn chi nhánh.");
        }
        employeeDAO.register(fullName.trim(), username.trim(), password, branchId);
    }
}
