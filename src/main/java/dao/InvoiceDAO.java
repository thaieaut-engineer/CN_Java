package dao;

import config.DatabaseConnection;
import model.InvoiceLine;
import model.InvoicePrintData;
import model.InvoiceRecordOption;
import model.InvoiceSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    public List<InvoiceSummary> findInvoices() throws SQLException {
        String sql = "SELECT i.invoice_id,i.record_id,p.name AS pet_name,c.full_name AS customer_name,"
                + "b.name AS branch_name,i.created_date,i.total_amount,i.status,i.payment_method "
                + "FROM Invoice i JOIN MedicalRecord mr ON mr.record_id=i.record_id "
                + "JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id ORDER BY i.created_date DESC";
        List<InvoiceSummary> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                rows.add(new InvoiceSummary(results.getInt("invoice_id"), results.getInt("record_id"),
                        results.getString("pet_name"), results.getString("customer_name"),
                        results.getString("branch_name"), results.getTimestamp("created_date"),
                        results.getBigDecimal("total_amount"), results.getString("status"),
                        results.getString("payment_method")));
            }
        }
        return rows;
    }

    public List<InvoiceRecordOption> findInvoiceCandidates() throws SQLException {
        String sql = "SELECT r.record_id, p.name AS pet_name, "
                + "CONVERT(varchar(16), r.visit_date, 120) AS visit_time "
                + "FROM MedicalRecord r JOIN Pet p ON p.pet_id = r.pet_id "
                + "WHERE EXISTS (SELECT 1 FROM MedicalDetail d WHERE d.record_id = r.record_id) "
                + "AND NOT EXISTS (SELECT 1 FROM Invoice i WHERE i.record_id = r.record_id) "
                + "ORDER BY r.visit_date DESC";
        List<InvoiceRecordOption> options = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                options.add(new InvoiceRecordOption(results.getInt("record_id"),
                        results.getString("pet_name"), results.getString("visit_time")));
            }
        }
        return options;
    }

    public void createInvoice(int recordId, String paymentMethod) throws SQLException {
        String sql = "INSERT INTO Invoice (record_id, total_amount, status, payment_method) "
                + "SELECT ?, COALESCE(SUM(d.quantity * d.unit_price), 0), N'Unpaid', ? "
                + "FROM MedicalDetail d WHERE d.record_id = ? "
                + "AND NOT EXISTS (SELECT 1 FROM Invoice i WHERE i.record_id = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recordId);
            statement.setNString(2, paymentMethod);
            statement.setInt(3, recordId);
            statement.setInt(4, recordId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Không tìm thấy phiếu khám, phiếu chưa có chi tiết hoặc đã có hóa đơn.");
            }
        }
    }

    public void markPaid(int invoiceId, String paymentMethod) throws SQLException {
        String sql = "UPDATE Invoice SET status=N'Paid', payment_method=? WHERE invoice_id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setNString(1, paymentMethod);
            statement.setInt(2, invoiceId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Không tìm thấy hóa đơn.");
            }
        }
    }

    public InvoicePrintData findPrintData(int invoiceId) throws SQLException {
        String sql = "SELECT i.invoice_id,i.created_date,i.total_amount,i.status,i.payment_method,"
                + "c.full_name,c.phone,p.name AS pet_name,b.name AS branch_name,e.full_name AS doctor,"
                + "d.quantity,d.unit_price,s.name AS item_name "
                + "FROM Invoice i JOIN MedicalRecord mr ON mr.record_id=i.record_id "
                + "JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id JOIN Employee e ON e.employee_id=mr.employee_id "
                + "LEFT JOIN MedicalDetail d ON d.record_id=mr.record_id "
                + "LEFT JOIN Service s ON s.service_id=d.service_id "
                + "WHERE i.invoice_id=? ORDER BY d.detail_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceId);
            try (ResultSet results = statement.executeQuery()) {
                int foundInvoiceId = 0;
                java.sql.Timestamp createdDate = null;
                java.math.BigDecimal totalAmount = null;
                String status = null;
                String paymentMethod = null;
                String customerName = null;
                String phone = null;
                String petName = null;
                String branchName = null;
                String doctor = null;
                List<InvoiceLine> lines = new ArrayList<>();
                while (results.next()) {
                    if (foundInvoiceId == 0) {
                        foundInvoiceId = results.getInt("invoice_id");
                        createdDate = results.getTimestamp("created_date");
                        totalAmount = results.getBigDecimal("total_amount");
                        status = results.getString("status");
                        paymentMethod = results.getString("payment_method");
                        customerName = results.getString("full_name");
                        phone = results.getString("phone");
                        petName = results.getString("pet_name");
                        branchName = results.getString("branch_name");
                        doctor = results.getString("doctor");
                    }
                    String itemName = results.getString("item_name");
                    if (itemName != null) {
                        lines.add(new InvoiceLine(itemName, results.getInt("quantity"),
                                results.getBigDecimal("unit_price")));
                    }
                }
                if (foundInvoiceId == 0) {
                    throw new SQLException("Không tìm thấy hóa đơn.");
                }
                return new InvoicePrintData(foundInvoiceId, createdDate, totalAmount, status, paymentMethod,
                        customerName, phone, petName, branchName, doctor, lines);
            }
        }
    }
}
