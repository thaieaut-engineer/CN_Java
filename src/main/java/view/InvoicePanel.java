package view;

import config.DatabaseConnection;
import model.Employee;
import util.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class InvoicePanel extends JPanel {
    private final Employee employee;
    private final JTextField recordId = new JTextField(8);
    private final JTextField paymentMethod = new JTextField("Tiền mặt", 12);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "Mã HĐ", "Mã phiếu khám", "Thú cưng", "Khách hàng", "Chi nhánh", "Ngày tạo", "Tổng tiền", "Trạng thái", "Thanh toán"
    }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public InvoicePanel(Employee employee) {
        this.employee = employee;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("HÓA ĐƠN & THANH TOÁN");
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Mã phiếu khám:"));
        controls.add(recordId);
        controls.add(new JLabel("Phương thức:"));
        controls.add(paymentMethod);
        JButton create = new JButton("Tạo hóa đơn");
        JButton paid = new JButton("Xác nhận đã thanh toán");
        JButton print = new JButton("In hóa đơn");
        JButton reload = new JButton("Tải lại");
        JButton export = new JButton("Xuất Excel");
        controls.add(create);
        controls.add(paid);
        controls.add(print);
        controls.add(reload);
        controls.add(export);
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        create.addActionListener(event -> createInvoice());
        paid.addActionListener(event -> markPaid());
        print.addActionListener(event -> printInvoice());
        reload.addActionListener(event -> loadData());
        export.addActionListener(event -> export());
        loadData();
    }

    private void createInvoice() {
        try {
            int id = Integer.parseInt(recordId.getText().trim());
            String sql = "INSERT INTO Invoice (record_id, total_amount, status, payment_method) "
                    + "SELECT ?, COALESCE(SUM(d.quantity * d.unit_price), 0), N'Unpaid', ? "
                    + "FROM MedicalDetail d WHERE d.record_id = ? "
                    + "AND NOT EXISTS (SELECT 1 FROM Invoice i WHERE i.record_id = ?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.setNString(2, paymentMethod.getText().trim());
                statement.setInt(3, id);
                statement.setInt(4, id);
                int changed = statement.executeUpdate();
                if (changed == 0) throw new SQLException("Không tìm thấy phiếu khám, phiếu chưa có chi tiết hoặc đã có hóa đơn.");
            }
            loadData();
            JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn ở trạng thái chưa thanh toán.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã phiếu khám phải là số nguyên.", "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void markPaid() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn hóa đơn cần thanh toán.");
            return;
        }
        String sql = "UPDATE Invoice SET status=N'Paid', payment_method=? WHERE invoice_id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setNString(1, paymentMethod.getText().trim());
            statement.setObject(2, model.getValueAt(row, 0));
            if (statement.executeUpdate() != 1) throw new SQLException("Không tìm thấy hóa đơn.");
            loadData();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void printInvoice() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn hóa đơn cần in.");
            return;
        }
        int invoiceId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sql = "SELECT i.invoice_id,i.created_date,i.total_amount,i.status,i.payment_method,c.full_name,c.phone,"
                + "p.name AS pet_name,b.name AS branch_name,e.full_name AS doctor,d.quantity,d.unit_price,s.name AS item_name "
                + "FROM Invoice i JOIN MedicalRecord mr ON mr.record_id=i.record_id "
                + "JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id JOIN Employee e ON e.employee_id=mr.employee_id "
                + "LEFT JOIN MedicalDetail d ON d.record_id=mr.record_id LEFT JOIN Service s ON s.service_id=d.service_id "
                + "WHERE i.invoice_id=? ORDER BY d.detail_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceId);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) throw new SQLException("Không tìm thấy hóa đơn.");
                StringBuilder receipt = new StringBuilder("PHÒNG KHÁM THÚ Y PET CLINIC\n")
                        .append("Chi nhánh: ").append(results.getString("branch_name")).append('\n')
                        .append("HÓA ĐƠN #").append(invoiceId).append('\n')
                        .append("Ngày: ").append(results.getTimestamp("created_date")).append('\n')
                        .append("Khách hàng: ").append(results.getString("full_name"))
                        .append(" - ").append(results.getString("phone")).append('\n')
                        .append("Thú cưng: ").append(results.getString("pet_name"))
                        .append(" | Bác sĩ: ").append(results.getString("doctor")).append("\n\n")
                        .append(String.format("%-32s %5s %14s%n", "Dịch vụ/thuốc", "SL", "Thành tiền"));
                do {
                    String item = results.getString("item_name");
                    if (item != null) {
                        receipt.append(String.format("%-32s %5d %14s%n", item, results.getInt("quantity"),
                                results.getBigDecimal("unit_price").multiply(java.math.BigDecimal.valueOf(results.getInt("quantity")))));
                    }
                } while (results.next());
                receipt.append("\nTổng cộng: ").append(model.getValueAt(row, 6))
                        .append("\nTrạng thái: ").append(model.getValueAt(row, 7))
                        .append("\nPhương thức: ").append(model.getValueAt(row, 8))
                        .append("\n\nCảm ơn quý khách!");
                JTextArea printable = new JTextArea(receipt.toString());
                printable.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
                printable.print();
            }
        } catch (SQLException | java.awt.print.PrinterException e) {
            showError(e);
        }
    }

    private int selectedModelRow() {
        int selected = table.getSelectedRow();
        return selected < 0 ? -1 : table.convertRowIndexToModel(selected);
    }

    private void loadData() {
        String sql = "SELECT i.invoice_id,i.record_id,p.name AS pet_name,c.full_name AS customer_name,b.name AS branch_name,"
                + "i.created_date,i.total_amount,i.status,i.payment_method FROM Invoice i "
                + "JOIN MedicalRecord mr ON mr.record_id=i.record_id JOIN Pet p ON p.pet_id=mr.pet_id "
                + "JOIN Customer c ON c.customer_id=p.customer_id JOIN Branch b ON b.branch_id=mr.branch_id "
                + "ORDER BY i.created_date DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            model.setRowCount(0);
            while (results.next()) {
                model.addRow(new Object[]{results.getInt("invoice_id"), results.getInt("record_id"),
                    results.getString("pet_name"), results.getString("customer_name"), results.getString("branch_name"),
                    results.getTimestamp("created_date"), results.getBigDecimal("total_amount"),
                    results.getString("status"), results.getString("payment_method")});
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("hoa-don.xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        if (!path.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".xlsx")) path = Path.of(path + ".xlsx");
        try {
            ExcelExporter.write(table, path);
            JOptionPane.showMessageDialog(this, "Đã xuất Excel: " + path);
        } catch (IOException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Thao tác thất bại:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
