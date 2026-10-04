package view;

import config.DatabaseConnection;
import model.ModuleDefinition;
import model.ModuleDefinition.Column;
import model.ModuleDefinition.Field;
import model.ModuleDefinition.ValueType;
import util.ExcelExporter;
import util.PasswordUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CrudPanel extends JPanel {
    private final ModuleDefinition definition;
    private final Map<Field, JComponent> inputs = new LinkedHashMap<>();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public CrudPanel(ModuleDefinition definition) {
        this.definition = definition;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel(definition.getTitle());
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 7, 5, 7);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        int row = 0;
        for (Field field : definition.getFields()) {
            JLabel label = new JLabel(field.getLabel() + (field.isRequired() ? " *" : ""));
            JComponent input = field.getType() == ValueType.PASSWORD
                    ? new JPasswordField(18) : new JTextField(18);
            inputs.put(field, input);
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 0;
            form.add(label, constraints);
            constraints.gridx = 1;
            constraints.weightx = 1;
            form.add(input, constraints);
            row++;
        }
        JPanel actions = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
        JButton add = new JButton("Thêm");
        JButton update = new JButton("Cập nhật");
        JButton delete = new JButton("Xóa");
        JButton clear = new JButton("Làm mới");
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        actions.add(clear);
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        form.add(actions, constraints);
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.setPreferredSize(new Dimension(340, 0));
        left.add(new JScrollPane(form), BorderLayout.CENTER);

        List<String> headers = new ArrayList<>();
        for (Column column : definition.getColumns()) {
            headers.add(column.getLabel());
        }
        tableModel = new DefaultTableModel(headers.toArray(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                selectRow();
            }
        });
        JTextField search = new JTextField(18);
        search.putClientProperty("JTextField.placeholderText", "Tìm trong danh sách...");
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void applyFilter() {
                String text = search.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        JButton reload = new JButton("Tải lại");
        JButton export = new JButton("Xuất Excel");
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableActions.add(search);
        tableActions.add(reload);
        tableActions.add(export);
        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.add(tableActions, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        add.addActionListener(event -> save(true));
        update.addActionListener(event -> save(false));
        delete.addActionListener(event -> deleteSelected());
        clear.addActionListener(event -> clearForm());
        reload.addActionListener(event -> loadData());
        export.addActionListener(event -> export());
        loadData();
    }

    private void loadData() {
        String columns = definition.getColumns().stream().map(Column::getName)
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String sql = "SELECT " + columns + " FROM " + definition.getTableName()
                + " ORDER BY " + definition.getIdColumn() + " DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            tableModel.setRowCount(0);
            while (results.next()) {
                Object[] row = new Object[definition.getColumns().size()];
                for (int column = 0; column < row.length; column++) {
                    row[column] = results.getObject(definition.getColumns().get(column).getName());
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Không thể tải dữ liệu", e);
        }
    }

    private void selectRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        for (Field field : definition.getFields()) {
            JComponent input = inputs.get(field);
            if (field.getType() == ValueType.PASSWORD) {
                ((JPasswordField) input).setText("");
                continue;
            }
            int column = columnIndex(field.getName());
            if (column >= 0) {
                Object value = tableModel.getValueAt(row, column);
                ((JTextField) input).setText(value == null ? "" : value.toString());
            }
        }
    }

    private int columnIndex(String name) {
        for (int index = 0; index < definition.getColumns().size(); index++) {
            if (definition.getColumns().get(index).getName().equals(name)) {
                return index;
            }
        }
        return -1;
    }

    private void save(boolean insert) {
        int row = table.getSelectedRow();
        if (!insert && row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn bản ghi cần cập nhật.");
            return;
        }
        try {
            Map<Field, Object> values = readValues(insert);
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Hãy nhập ít nhất một trường dữ liệu.");
            }
            if (insert) {
                insert(values);
            } else {
                int modelRow = table.convertRowIndexToModel(row);
                Object id = tableModel.getValueAt(modelRow, 0);
                update(id, values);
            }
            clearForm();
            loadData();
            JOptionPane.showMessageDialog(this, insert ? "Đã thêm bản ghi." : "Đã cập nhật bản ghi.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            showError("Không thể lưu dữ liệu", e);
        }
    }

    private Map<Field, Object> readValues(boolean insert) {
        Map<Field, Object> values = new LinkedHashMap<>();
        for (Field field : definition.getFields()) {
            String value;
            if (field.getType() == ValueType.PASSWORD) {
                value = new String(((JPasswordField) inputs.get(field)).getPassword());
                if (value.isBlank() && !insert) {
                    continue;
                }
            } else {
                value = ((JTextField) inputs.get(field)).getText().trim();
            }
            if (value.isBlank()) {
                if (field.isRequired()) {
                    throw new IllegalArgumentException("Vui lòng nhập " + field.getLabel() + ".");
                }
                continue;
            }
            values.put(field, parseValue(field, value));
        }
        return values;
    }

    private Object parseValue(Field field, String value) {
        try {
            switch (field.getType()) {
                case INTEGER:
                    int number = Integer.parseInt(value);
                    if ("age".equals(field.getName()) ? number < 0 : number <= 0) {
                        throw new IllegalArgumentException(field.getLabel()
                                + ("age".equals(field.getName()) ? " không được âm." : " phải lớn hơn 0."));
                    }
                    return number;
                case DECIMAL:
                    BigDecimal decimal = new BigDecimal(value);
                    if (decimal.signum() < 0) {
                        throw new IllegalArgumentException(field.getLabel() + " không được âm.");
                    }
                    return decimal;
                case DATETIME:
                    return Timestamp.valueOf(LocalDateTime.parse(value.replace(' ', 'T')));
                case PASSWORD:
                    return PasswordUtil.hash(value);
                default:
                    if ("role".equals(field.getName())) {
                        if (value.equalsIgnoreCase("admin")) return "Admin";
                        if (value.equalsIgnoreCase("bacsi") || value.equalsIgnoreCase("bác sĩ")) return "BacSi";
                        if (value.equalsIgnoreCase("nhanvien") || value.equalsIgnoreCase("nhân viên")) return "NhanVien";
                        throw new IllegalArgumentException("Vai trò phải là Admin, BacSi hoặc NhanVien.");
                    }
                    if ("status".equals(field.getName())) {
                        for (String status : List.of("Pending", "Confirmed", "Completed", "Cancelled")) {
                            if (value.equalsIgnoreCase(status)) return status;
                        }
                        throw new IllegalArgumentException("Trạng thái phải là Pending, Confirmed, Completed hoặc Cancelled.");
                    }
                    if ("type".equals(field.getName())) {
                        for (String type : List.of("KhamBenh", "Spa", "TiemPhong", "Thuoc")) {
                            if (value.equalsIgnoreCase(type)) return type;
                        }
                        throw new IllegalArgumentException("Loại dịch vụ phải là KhamBenh, Spa, TiemPhong hoặc Thuoc.");
                    }
                    return value;
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException(field.getLabel() + " không đúng định dạng.");
        }
    }

    private void insert(Map<Field, Object> values) throws SQLException {
        String names = values.keySet().stream().map(Field::getName)
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String placeholders = String.join(", ", java.util.Collections.nCopies(values.size(), "?"));
        String sql = "INSERT INTO " + definition.getTableName() + " (" + names + ") VALUES (" + placeholders + ")";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            statement.executeUpdate();
        }
    }

    private void update(Object id, Map<Field, Object> values) throws SQLException {
        String assignments = values.keySet().stream().map(field -> field.getName() + " = ?")
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String sql = "UPDATE " + definition.getTableName() + " SET " + assignments
                + " WHERE " + definition.getIdColumn() + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            statement.setObject(values.size() + 1, id);
            statement.executeUpdate();
        }
    }

    private void bind(PreparedStatement statement, Map<Field, Object> values) throws SQLException {
        int index = 1;
        for (Object value : values.values()) {
            statement.setObject(index++, value);
        }
    }

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn bản ghi cần xóa.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa bản ghi đang chọn?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        Object id = tableModel.getValueAt(row, 0);
        String sql = "DELETE FROM " + definition.getTableName() + " WHERE " + definition.getIdColumn() + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
            clearForm();
            loadData();
        } catch (SQLException e) {
            showError("Không thể xóa bản ghi", e);
        }
    }

    private void clearForm() {
        for (JComponent input : inputs.values()) {
            if (input instanceof JPasswordField) {
                ((JPasswordField) input).setText("");
            } else {
                ((JTextField) input).setText("");
            }
        }
        table.clearSelection();
    }

    private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(definition.getTableName() + ".xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        if (!path.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".xlsx")) {
            path = Path.of(path + ".xlsx");
        }
        try {
            ExcelExporter.write(table, path);
            JOptionPane.showMessageDialog(this, "Đã xuất Excel: " + path);
        } catch (IOException e) {
            showError("Không thể xuất Excel", e);
        }
    }

    private void showError(String action, Exception error) {
        JOptionPane.showMessageDialog(this, action + ":\n" + error.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
