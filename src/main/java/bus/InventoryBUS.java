package bus;

import dao.InventoryDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import model.InventoryMovement;
import model.StockOption;

public class InventoryBUS {
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    public List<StockOption> getStockOptions() throws SQLException {
        return inventoryDAO.findStockOptions();
    }

    public List<InventoryMovement> getMovements() throws SQLException {
        return inventoryDAO.findMovements();
    }

    public void receive(int serviceId, int employeeId, int quantity, BigDecimal price) throws SQLException {
        if (serviceId <= 0 || employeeId <= 0 || quantity <= 0 || price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Thông tin nhập kho không hợp lệ.");
        }
        inventoryDAO.receive(serviceId, employeeId, quantity, price);
    }

    public void issue(int serviceId, int employeeId, int quantity, String reason) throws SQLException {
        if (serviceId <= 0 || employeeId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Thông tin xuất kho không hợp lệ.");
        }
        inventoryDAO.issue(serviceId, employeeId, quantity, reason == null ? "" : reason.trim());
    }
}
