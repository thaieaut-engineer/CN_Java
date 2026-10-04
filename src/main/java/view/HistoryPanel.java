package view;

import config.DatabaseConnection;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class HistoryPanel extends JPanel {
    private final JTextField query = new JTextField(24);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{
        "Ngày khám", "Thú cưng", "Loài", "Khách hàng", "Điện thoại", "Chi nhánh", "Bác sĩ", "Chẩn đoán", "Ghi chú", "Tái khám", "Hóa đơn"
    }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public HistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("TRA CỨU LỊCH SỬ KHÁM TOÀN CHUỖI");
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Tên khách, số điện thoại hoặc tên thú cưng:"));
        controls.add(query);
        JButton search = new JButton("Tra cứu");
        JButton export = new JButton("Xuất Excel");
        controls.add(search);
        controls.add(export);
        table.setAutoCreateRowSorter(true);
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        search.addActionListener(event -> search());
        query.addActionListener(event -> search());
        export.addActionListener(event -> export());
    }

    private void search() {
        String term = query.getText().trim();
        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên khách hàng, số điện thoại hoặc tên thú cưng.");
            return;
        }
        String sql = "SELECT mr.visit_date,p.name AS pet_name,p.species,c.full_name,c.phone,b.name AS branch_name,"
                + "e.full_name AS doctor,mr.diagnosis,mr.notes,mr.revisit_date,i.total_amount FROM MedicalRecord mr "
                + "JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id JOIN Employee e ON e.employee_id=mr.employee_id "
                + "LEFT JOIN Invoice i ON i.record_id=mr.record_id "
                + "WHERE c.full_name LIKE ? OR c.phone LIKE ? OR p.name LIKE ? ORDER BY mr.visit_date DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + term + "%";
            statement.setNString(1, like);
            statement.setString(2, like);
            statement.setNString(3, like);
            try (ResultSet results = statement.executeQuery()) {
                model.setRowCount(0);
                while (results.next()) {
                    model.addRow(new Object[]{results.getTimestamp("visit_date"), results.getString("pet_name"),
                        results.getString("species"), results.getString("full_name"), results.getString("phone"),
                        results.getString("branch_name"), results.getString("doctor"), results.getString("diagnosis"),
                        results.getString("notes"), results.getTimestamp("revisit_date"), results.getBigDecimal("total_amount")});
                }
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("lich-su-kham.xlsx"));
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
        JOptionPane.showMessageDialog(this, "Tra cứu thất bại:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
