package bus;

import dao.CustomerDAO;
import java.sql.SQLException;
import java.util.List;
import model.Customer;

public class CustomerBUS {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAll() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public void create(Customer customer) throws SQLException {
        validate(customer);
        if (!customerDAO.addCustomer(customer)) {
            throw new SQLException("Không thể thêm khách hàng.");
        }
    }

    public void update(Customer customer) throws SQLException {
        validate(customer);
        if (customer.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        }
        if (!customerDAO.updateCustomer(customer)) {
            throw new SQLException("Không tìm thấy khách hàng cần cập nhật.");
        }
    }

    public void delete(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        }
        if (!customerDAO.deleteCustomer(customerId)) {
            throw new SQLException("Không thể xóa khách hàng.");
        }
    }

    private void validate(Customer customer) {
        if (customer == null || customer.getFullName() == null || customer.getFullName().isBlank()
                || customer.getFullName().trim().length() > 100
                || customer.getPhone() == null || customer.getPhone().isBlank()
                || customer.getPhone().trim().length() > 20) {
            throw new IllegalArgumentException("Họ tên và số điện thoại hợp lệ là bắt buộc.");
        }
    }
}
