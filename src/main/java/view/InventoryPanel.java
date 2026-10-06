package view;

import config.DatabaseConnection;
import model.Employee;
import util.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class InventoryPanel extends JPanel {
    private final Employee employee;
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Loại", "Ngày", "Mã thuốc/dịch vụ", "Tên", "Số lượng", "Đơn giá nhập", "Nhân viên", "Ghi chú"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Đang tải giao dịch kho...");
    private final AtomicLong loadVersion = new AtomicLong();

    public InventoryPanel(Employee employee) {
        this.employee = employee;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiTheme.stylePage(this);
        JLabel title = new JLabel("QUẢN LÝ TỒN KHO - NHẬP / XUẤT THUỐC");
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        title.setIcon(new ClinicIcon("inventory", UiTheme.BLUE, 22));
        title.setIconTextGap(10);
        JButton receive = new JButton("Ghi nhận nhập kho");
        JButton issue = new JButton("Ghi nhận xuất kho");
        JButton reload = new JButton("Tải lại");
        JButton export = new JButton("Xuất Excel");
        UiTheme.stylePrimary(receive);
        UiTheme.styleDanger(issue);
        UiTheme.styleSecondary(reload);
        UiTheme.styleSecondary(export);
        receive.setIcon(new ClinicIcon("inventory", Color.WHITE, 16));
        issue.setIcon(new ClinicIcon("logout", new Color(185, 28, 28), 16));
        reload.setIcon(new ClinicIcon("history", UiTheme.BLUE, 15));
        export.setIcon(new ClinicIcon("report", UiTheme.BLUE, 15));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);
        buttons.add(receive);
        buttons.add(issue);
        buttons.add(reload);
        buttons.add(export);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(title, BorderLayout.NORTH);
        header.add(buttons, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        status.setForeground(UiTheme.MUTED);
        status.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));
        table.setAutoCreateRowSorter(true);
        UiTheme.styleTable(table);
        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setOpaque(false);
        content.add(status, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        receive.addActionListener(event -> openMovementDialog(true));
        issue.addActionListener(event -> openMovementDialog(false));
        reload.addActionListener(event -> loadData());
        export.addActionListener(event -> export());
        loadData();
    }

    private void openMovementDialog(boolean incoming) {
        JComboBox<StockOption> service = new JComboBox<>();
        service.addItem(new StockOption(0, "— Chọn thuốc / vắc-xin —", 0));
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT service_id, name, stock_quantity FROM Service "
                             + "WHERE type IN (N'Thuoc', N'TiemPhong') ORDER BY name");
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                service.addItem(new StockOption(results.getInt("service_id"),
                        results.getString("name"), results.getInt("stock_quantity")));
            }
        } catch (SQLException e) {
            showError(e);
            return;
        }
        if (service.getItemCount() == 1) {
            JOptionPane.showMessageDialog(this, "Chưa có thuốc hoặc vắc-xin để chọn.",
                    "Danh mục trống", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        service.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        service.setPreferredSize(new java.awt.Dimension(300, 36));
        service.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        JTextField quantity = new JTextField(18);
        JTextField unitPrice = new JTextField(18);
        JTextField notes = new JTextField(18);
        JPanel form = new JPanel(new GridBagLayout());
        UiTheme.styleSurface(form);
        UiTheme.styleTextField(quantity);
        UiTheme.styleTextField(unitPrice);
        UiTheme.styleTextField(notes);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        addField(form, constraints, 0, "Thuốc / vắc-xin:", service);
        addField(form, constraints, 1, "Số lượng:", quantity);
        if (incoming) {
            addField(form, constraints, 2, "Đơn giá nhập:", unitPrice);
        } else {
            addField(form, constraints, 2, "Ghi chú:", notes);
        }
        int result = JOptionPane.showConfirmDialog(this, form,
                incoming ? "Nhập kho" : "Xuất kho",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            StockOption selected = (StockOption) service.getSelectedItem();
            record(incoming, selected == null ? null : selected.serviceId, quantity.getText(),
                    unitPrice.getText(), notes.getText());
        }
    }

    private void addField(JPanel form, GridBagConstraints constraints, int row,
            String label, JComponent input) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(fieldLabel.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        form.add(fieldLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        form.add(input, constraints);
    }

    private void record(boolean incoming, Integer serviceId, String quantityText,
            String unitPriceText, String notesText) {
        try {
            if (serviceId == null || serviceId <= 0) {
                throw new IllegalArgumentException("Hãy chọn thuốc hoặc vắc-xin.");
            }
            int amount = Integer.parseInt(quantityText.trim());
            if (amount <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
            BigDecimal price = incoming ? new BigDecimal(unitPriceText.trim()) : BigDecimal.ZERO;
            if (incoming && price.signum() < 0) throw new IllegalArgumentException("Đơn giá nhập không được âm.");
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
            new javax.swing.SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws SQLException {
                    if (incoming) receiveStock(serviceId, amount, price);
                    else issueStock(serviceId, amount, notesText.trim());
                    return null;
                }

                @Override
                protected void done() {
                    setCursor(java.awt.Cursor.getDefaultCursor());
                    try {
                        get();
                        JOptionPane.showMessageDialog(InventoryPanel.this,
                                incoming ? "Đã nhập kho và cập nhật tồn." : "Đã xuất kho và cập nhật tồn.");
                        loadData();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        showError(e);
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause() == null ? e : e.getCause();
                        showError(cause instanceof Exception exception ? exception : new Exception(cause));
                    }
                }
            }.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải đúng định dạng số.", "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void receiveStock(int serviceId, int amount, BigDecimal price) throws SQLException {
        String updateSql = "UPDATE Service SET stock_quantity = ISNULL(stock_quantity, 0) + ? "
                + "WHERE service_id = ? AND type IN (N'Thuoc', N'TiemPhong')";
        String insertSql = "INSERT INTO InventoryReceipt (service_id, employee_id, quantity, import_price) "
                + "VALUES (?, ?, ?, ?)";
        inTransaction(connection -> {
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setInt(1, amount);
                update.setInt(2, serviceId);
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Không tìm thấy thuốc/vắc-xin có mã " + serviceId + ".");
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, serviceId);
                insert.setInt(2, employee.getEmployeeId());
                insert.setInt(3, amount);
                insert.setBigDecimal(4, price);
                insert.executeUpdate();
            }
        });
    }

    private void issueStock(int serviceId, int amount, String reason) throws SQLException {
        String updateSql = "UPDATE Service SET stock_quantity = stock_quantity - ? "
                + "WHERE service_id = ? AND type IN (N'Thuoc', N'TiemPhong') AND stock_quantity >= ?";
        String insertSql = "INSERT INTO InventoryIssue (service_id, employee_id, quantity, notes) VALUES (?, ?, ?, ?)";
        inTransaction(connection -> {
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setInt(1, amount);
                update.setInt(2, serviceId);
                update.setInt(3, amount);
                if (update.executeUpdate() != 1) {
                    throw new SQLException("Không đủ tồn kho hoặc mã thuốc/vắc-xin không hợp lệ.");
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, serviceId);
                insert.setInt(2, employee.getEmployeeId());
                insert.setInt(3, amount);
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

    private void loadData() {
        String sql = "SELECT movement_type, movement_date, service_id, service_name, quantity, unit_price, "
                + "employee_name, notes FROM ("
                + "SELECT N'Nhập' AS movement_type, r.import_date AS movement_date, s.service_id, s.name AS service_name, "
                + "r.quantity, r.import_price AS unit_price, e.full_name AS employee_name, N'' AS notes "
                + "FROM InventoryReceipt r JOIN Service s ON s.service_id=r.service_id JOIN Employee e ON e.employee_id=r.employee_id "
                + "UNION ALL SELECT N'Xuất', i.issue_date, s.service_id, s.name, i.quantity, NULL, e.full_name, i.notes "
                + "FROM InventoryIssue i JOIN Service s ON s.service_id=i.service_id JOIN Employee e ON e.employee_id=i.employee_id"
                + ") movements ORDER BY movement_date DESC";
        long version = loadVersion.incrementAndGet();
        status.setText("Đang tải giao dịch kho...");
        new javax.swing.SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws SQLException {
                List<Object[]> rows = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        rows.add(new Object[]{results.getString("movement_type"), results.getTimestamp("movement_date"),
                            results.getInt("service_id"), results.getString("service_name"), results.getInt("quantity"),
                            results.getBigDecimal("unit_price"), results.getString("employee_name"),
                            results.getString("notes")});
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                if (version != loadVersion.get()) {
                    return;
                }
                try {
                    List<Object[]> rows = get();
                    model.setRowCount(0);
                    rows.forEach(model::addRow);
                    status.setText(rows.size() + " giao dịch");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Đã hủy tải giao dịch.");
                    showError(e);
                } catch (ExecutionException e) {
                    status.setText("Không tải được giao dịch.");
                    showError(e.getCause() instanceof Exception cause ? cause : e);
                }
            }
        }.execute();
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("ton-kho.xlsx"));
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

    private static final class StockOption {
        private final int serviceId;
        private final String name;
        private final int stock;

        private StockOption(int serviceId, String name, int stock) {
            this.serviceId = serviceId;
            this.name = name;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name + "  ·  Tồn kho: " + stock;
        }
    }

    @FunctionalInterface
    private interface SqlWork {
        void execute(Connection connection) throws SQLException;
    }
}
