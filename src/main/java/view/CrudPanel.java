package view;

import config.DatabaseConnection;
import model.ModuleDefinition;
import model.ModuleDefinition.Column;
import model.ModuleDefinition.Field;
import model.ModuleDefinition.ValueType;
import util.ExcelExporter;
import util.PasswordUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CrudPanel extends JPanel {
    private static final int MAX_PHOTO_BYTES = 5 * 1024 * 1024;
    private static final Map<String, LookupSpec> LOOKUPS = Map.of(
            "branch_id", new LookupSpec("Branch", "branch_id", "name"),
            "customer_id", new LookupSpec("Customer", "customer_id", "full_name"),
            "pet_id", new LookupSpec("Pet", "pet_id", "name"),
            "employee_id", new LookupSpec("Employee", "employee_id", "full_name"),
            "service_id", new LookupSpec("Service", "service_id", "name"),
            "record_id", new LookupSpec("MedicalRecord", "record_id", "record_id"));

    private final ModuleDefinition definition;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JButton updateButton;
    private final JLabel dataStatus = new JLabel("Đang tải dữ liệu...");
    private final AtomicLong loadVersion = new AtomicLong();

    public CrudPanel(ModuleDefinition definition) {
        this.definition = definition;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiTheme.stylePage(this);

        JLabel title = new JLabel(definition.getTitle());
        title.setFont(title.getFont().deriveFont(20f).deriveFont(java.awt.Font.BOLD));
        title.setIcon(new ClinicIcon(iconForTable(definition.getTableName()), UiTheme.BLUE, 22));
        title.setIconTextGap(10);
        add(title, BorderLayout.NORTH);

        JButton add = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        JButton delete = new JButton("Xóa");

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
        UiTheme.styleTable(table);
        table.setAutoCreateRowSorter(true);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
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
        UiTheme.stylePrimary(add);
        UiTheme.styleSecondary(updateButton);
        UiTheme.styleDanger(delete);
        UiTheme.styleSecondary(reload);
        UiTheme.styleSecondary(export);
        add.setIcon(new ClinicIcon("employee", Color.WHITE, 15));
        updateButton.setIcon(new ClinicIcon("detail", UiTheme.BLUE, 15));
        delete.setIcon(new ClinicIcon("logout", new Color(185, 28, 28), 15));
        reload.setIcon(new ClinicIcon("history", UiTheme.BLUE, 15));
        export.setIcon(new ClinicIcon("report", UiTheme.BLUE, 15));
        UiTheme.styleTextField(search);
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableActions.setOpaque(false);
        tableActions.add(add);
        tableActions.add(updateButton);
        tableActions.add(delete);
        tableActions.add(search);
        tableActions.add(reload);
        tableActions.add(export);
        dataStatus.setForeground(UiTheme.MUTED);
        dataStatus.setFont(dataStatus.getFont().deriveFont(11f));
        tableActions.add(dataStatus);
        JPanel body = new JPanel(new BorderLayout(6, 6));
        body.add(tableActions, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        add.addActionListener(event -> showEditor(true));
        updateButton.addActionListener(event -> showEditor(false));
        delete.addActionListener(event -> deleteSelected());
        reload.addActionListener(event -> loadData());
        export.addActionListener(event -> export());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent event) {
                int clickedRow = table.rowAtPoint(event.getPoint());
                if (clickedRow >= 0 && javax.swing.SwingUtilities.isLeftMouseButton(event)) {
                    table.setRowSelectionInterval(clickedRow, clickedRow);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int clickedRow = table.rowAtPoint(event.getPoint());
                if (event.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(event)
                        && clickedRow >= 0) {
                    table.setRowSelectionInterval(clickedRow, clickedRow);
                    showEditor(false);
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(event ->
                updateButton.setEnabled(table.getSelectedRow() >= 0));
        updateButton.setEnabled(false);
        table.getInputMap().put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "edit-selected-row");
        table.getActionMap().put("edit-selected-row", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (table.getSelectedRow() >= 0) {
                    showEditor(false);
                }
            }
        });
        loadData();
    }

    private String iconForTable(String tableName) {
        switch (tableName) {
            case "Branch": return "branch";
            case "Employee": return "employee";
            case "Customer": return "customer";
            case "Pet": return "pet";
            case "Appointment": return "appointment";
            case "MedicalRecord": return "medical";
            case "MedicalDetail": return "detail";
            case "Service": return "service";
            case "Vaccination": return "vaccination";
            default: return "document";
        }
    }

    private void loadData() {
        String columns = definition.getColumns().stream()
                .map(column -> "data_row." + column.getName())
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String sql = "SELECT " + columns + " FROM dbo." + definition.getTableName() + " AS data_row"
                + " ORDER BY data_row." + definition.getIdColumn() + " DESC";
        long version = loadVersion.incrementAndGet();
        dataStatus.setText("Đang tải dữ liệu...");
        new javax.swing.SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws SQLException {
                List<Object[]> rows = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        Object[] row = new Object[definition.getColumns().size()];
                        for (int column = 0; column < row.length; column++) {
                            row[column] = results.getObject(definition.getColumns().get(column).getName());
                        }
                        rows.add(row);
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
                    tableModel.setRowCount(0);
                    for (Object[] row : rows) {
                        tableModel.addRow(row);
                    }
                    dataStatus.setText(rows.size() + " bản ghi");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    dataStatus.setText("Đã hủy tải dữ liệu.");
                    showError("Không thể tải dữ liệu", e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    dataStatus.setText("Không tải được dữ liệu.");
                    showError("Không thể tải dữ liệu", cause);
                }
            }
        }.execute();
    }

    private int columnIndex(String name) {
        for (int index = 0; index < definition.getColumns().size(); index++) {
            if (definition.getColumns().get(index).getName().equals(name)) {
                return index;
            }
        }
        return -1;
    }

    private void showEditor(boolean insert) {
        int row = table.getSelectedRow();
        if (!insert && row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn bản ghi cần cập nhật.");
            return;
        }
        Map<Field, JComponent> editorInputs = new LinkedHashMap<>();
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        int modelRow = insert ? -1 : table.convertRowIndexToModel(row);
        for (int index = 0; index < definition.getFields().size(); index++) {
            Field field = definition.getFields().get(index);
            JLabel label = new JLabel(field.getLabel() + (field.isRequired() ? "  *" : ""));
            label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            label.setForeground(UiTheme.TEXT);
            JComponent input;
            try {
                input = createInput(field);
            } catch (SQLException e) {
                showError("Không thể tải danh sách cho trường " + field.getLabel(), e);
                return;
            }
            editorInputs.put(field, input);
            if (!insert && field.getType() != ValueType.PASSWORD) {
                int column = columnIndex(field.getName());
                if (column >= 0) {
                    Object value = tableModel.getValueAt(modelRow, column);
                    setInputValue(input, value);
                }
            }
            constraints.gridx = 0;
            constraints.gridy = index;
            constraints.weightx = 0.42;
            form.add(label, constraints);
            constraints.gridx = 1;
            constraints.weightx = 0.58;
            form.add(input, constraints);
        }

        boolean photoColumnAvailable = hasPhotoField() && isPhotoColumnAvailable();
        PhotoEditor photoEditor = hasPhotoField() ? new PhotoEditor(photoColumnAvailable) : null;
        if (!insert && photoEditor != null && photoColumnAvailable) {
            try {
                photoEditor.setInitialPhoto(loadPhoto(tableModel.getValueAt(modelRow, 0)));
            } catch (SQLException e) {
                showError("Không thể tải ảnh hiện tại", e);
            }
        }

        javax.swing.JDialog dialog = new javax.swing.JDialog(
                SwingUtilities.getWindowAncestor(this),
                insert ? "Thêm " + definition.getTitle() : "Cập nhật " + definition.getTitle(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(UiTheme.PAGE);
        dialog.setLayout(new BorderLayout(0, 14));
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setBackground(UiTheme.NAV);
        heading.setBorder(BorderFactory.createEmptyBorder(17, 20, 17, 20));
        JLabel headingIcon = new JLabel(new ClinicIcon(iconForTable(definition.getTableName()), Color.WHITE, 25));
        JLabel headingText = new JLabel(insert ? "Tạo thông tin mới" : "Chỉnh sửa thông tin");
        headingText.setForeground(Color.WHITE);
        headingText.setFont(headingText.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        JPanel headingCopy = new JPanel();
        headingCopy.setOpaque(false);
        headingCopy.setLayout(new BoxLayout(headingCopy, BoxLayout.Y_AXIS));
        headingCopy.add(headingText);
        JLabel subtitle = new JLabel(definition.getTitle());
        subtitle.setForeground(new Color(191, 219, 254));
        subtitle.setFont(subtitle.getFont().deriveFont(12f));
        headingCopy.add(Box.createVerticalStrut(4));
        headingCopy.add(subtitle);
        heading.add(headingIcon, BorderLayout.WEST);
        heading.add(headingCopy, BorderLayout.CENTER);
        dialog.add(heading, BorderLayout.NORTH);

        JPanel editorBody = new JPanel(new BorderLayout(16, 0));
        editorBody.setOpaque(false);
        editorBody.add(form, BorderLayout.CENTER);
        if (photoEditor != null) {
            editorBody.add(photoEditor, BorderLayout.EAST);
        }
        JScrollPane contentScroll = new JScrollPane(editorBody);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.getViewport().setBackground(UiTheme.PAGE);
        contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dialog.add(contentScroll, BorderLayout.CENTER);

        JButton save = new JButton(insert ? "Thêm" : "Lưu thay đổi");
        JButton cancel = new JButton("Hủy");
        save.setIcon(new ClinicIcon("detail", Color.WHITE, 15));
        cancel.setIcon(new ClinicIcon("logout", UiTheme.MUTED, 14));
        UiTheme.stylePrimary(save);
        UiTheme.styleSecondary(cancel);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(save);
        dialog.add(actions, BorderLayout.SOUTH);
        save.addActionListener(event -> save(insert, editorInputs, modelRow,
                photoEditor, photoColumnAvailable, save, dialog));
        cancel.addActionListener(event -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(save);
        dialog.setMinimumSize(photoEditor == null ? new Dimension(650, 440) : new Dimension(790, 500));
        dialog.setSize(photoEditor == null ? new Dimension(700, Math.min(680, 300 + definition.getFields().size() * 48))
                : new Dimension(850, Math.min(680, 320 + definition.getFields().size() * 48)));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JComponent createInput(Field field) throws SQLException {
        JComboBox<ComboOption> choices = createChoices(field);
        if (choices != null) {
            choices.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            choices.setPreferredSize(new Dimension(240, 38));
            choices.putClientProperty("JComponent.roundRect", Boolean.TRUE);
            return choices;
        }
        JComponent input = field.getType() == ValueType.PASSWORD
                ? new JPasswordField(20) : new JTextField(20);
        input.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(240, 38));
        input.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        if (field.getType() == ValueType.DATETIME) {
            ((JTextField) input).putClientProperty("JTextField.placeholderText", "yyyy-MM-dd HH:mm:ss");
        }
        return input;
    }

    private JComboBox<ComboOption> createChoices(Field field) throws SQLException {
        LookupSpec lookup = LOOKUPS.get(field.getName());
        if (lookup != null) {
            JComboBox<ComboOption> combo = new JComboBox<>();
            combo.addItem(new ComboOption(null,
                    field.isRequired() ? "— Vui lòng chọn —" : "— Không chọn —"));
            String source = "dbo." + lookup.tableName + " AS lookup_row";
            String labelExpression = "lookup_row." + lookup.labelColumn;
            if ("record_id".equals(field.getName())) {
                source += " LEFT JOIN dbo.Pet AS related_pet ON related_pet.pet_id = lookup_row.pet_id";
                labelExpression = "CONCAT(N'Phiếu khám #', lookup_row.record_id, N' - ', "
                        + "COALESCE(related_pet.name, N'Chưa rõ thú cưng'))";
            }
            String sql = "SELECT lookup_row." + lookup.idColumn + " AS lookup_id, "
                    + labelExpression + " AS lookup_label FROM " + source
                    + " ORDER BY lookup_row." + lookup.idColumn + " DESC";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Object value = results.getObject("lookup_id");
                    String label = results.getString("lookup_label");
                    combo.addItem(new ComboOption(value, label + "  ·  #" + value));
                }
            }
            return combo;
        }

        List<ComboOption> options = switch (field.getName()) {
            case "role" -> List.of(new ComboOption("Admin", "Quản trị viên"),
                    new ComboOption("BacSi", "Bác sĩ"), new ComboOption("NhanVien", "Nhân viên"));
            case "status" -> List.of(new ComboOption("Pending", "Chờ xác nhận"),
                    new ComboOption("Confirmed", "Đã xác nhận"), new ComboOption("Completed", "Hoàn thành"),
                    new ComboOption("Cancelled", "Đã hủy"));
            case "type" -> List.of(new ComboOption("KhamBenh", "Khám bệnh"),
                    new ComboOption("Spa", "Spa"), new ComboOption("TiemPhong", "Tiêm phòng"),
                    new ComboOption("Thuoc", "Thuốc"));
            case "species" -> List.of(new ComboOption("Chó", "Chó"), new ComboOption("Mèo", "Mèo"),
                    new ComboOption("Thỏ", "Thỏ"), new ComboOption("Chim", "Chim"),
                    new ComboOption("Hamster", "Hamster"), new ComboOption("Khác", "Khác"));
            case "payment_method" -> List.of(new ComboOption("Tiền mặt", "Tiền mặt"),
                    new ComboOption("Chuyển khoản", "Chuyển khoản"));
            case "unit" -> List.of(new ComboOption("Lần", "Lần"), new ComboOption("Liều", "Liều"),
                    new ComboOption("Viên", "Viên"), new ComboOption("Chai", "Chai"),
                    new ComboOption("Gói", "Gói"), new ComboOption("Khác", "Khác"));
            default -> null;
        };
        if (options == null) {
            return null;
        }
        List<ComboOption> choices = new ArrayList<>();
        choices.add(new ComboOption(null,
                field.isRequired() ? "— Vui lòng chọn —" : "— Không chọn —"));
        choices.addAll(options);
        JComboBox<ComboOption> combo = new JComboBox<>(choices.toArray(ComboOption[]::new));
        if ("species".equals(field.getName()) || "unit".equals(field.getName())) {
            combo.setEditable(true);
        }
        return combo;
    }

    private void setInputValue(JComponent input, Object value) {
        if (input instanceof JComboBox<?> rawCombo) {
            @SuppressWarnings("unchecked")
            JComboBox<ComboOption> combo = (JComboBox<ComboOption>) rawCombo;
            for (int index = 0; index < combo.getItemCount(); index++) {
                ComboOption option = combo.getItemAt(index);
                if (value == null ? option.value == null
                        : option.value != null && value.toString().equals(option.value.toString())) {
                    combo.setSelectedIndex(index);
                    return;
                }
            }
            if (combo.isEditable()) {
                combo.setSelectedItem(value == null ? "" : value.toString());
            }
        } else if (input instanceof JTextField textField) {
            textField.setText(value == null ? "" : value.toString());
        }
    }

    private void save(boolean insert, Map<Field, JComponent> editorInputs, int modelRow,
            PhotoEditor photoEditor, boolean photoColumnAvailable, JButton saveButton,
            javax.swing.JDialog dialog) {
        Map<Field, Object> values;
        try {
            values = readValues(insert, editorInputs);
            if (values.isEmpty()) {
                throw new IllegalArgumentException("Hãy nhập ít nhất một trường dữ liệu.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object id = insert ? null : tableModel.getValueAt(modelRow, 0);
        byte[] photo = photoEditor == null ? null : photoEditor.getPhoto();
        boolean photoChanged = photoColumnAvailable && photoEditor != null && photoEditor.isChanged();
        saveButton.setEnabled(false);
        saveButton.setText("Đang lưu...");
        new javax.swing.SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws SQLException {
                if (insert) {
                    insert(values, photo, photoColumnAvailable);
                } else {
                    update(id, values, photo, photoChanged);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadData();
                    dialog.dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    saveButton.setEnabled(true);
                    saveButton.setText(insert ? "Thêm" : "Lưu thay đổi");
                    showError("Không thể lưu dữ liệu", e);
                } catch (ExecutionException e) {
                    saveButton.setEnabled(true);
                    saveButton.setText(insert ? "Thêm" : "Lưu thay đổi");
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError("Không thể lưu dữ liệu", cause);
                }
            }
        }.execute();
    }

    private Map<Field, Object> readValues(boolean insert, Map<Field, JComponent> editorInputs) {
        Map<Field, Object> values = new LinkedHashMap<>();
        for (Field field : definition.getFields()) {
            JComponent input = editorInputs.get(field);
            Object rawValue;
            if (input instanceof JComboBox<?> combo) {
                Object selected = combo.getSelectedItem();
                rawValue = selected instanceof ComboOption option ? option.value
                        : combo.isEditable() ? combo.getEditor().getItem() : selected;
            } else if (input instanceof JPasswordField password) {
                rawValue = new String(password.getPassword());
            } else {
                rawValue = ((JTextField) input).getText();
            }
            String value = rawValue == null ? "" : rawValue.toString().trim();
            if (field.getType() == ValueType.PASSWORD) {
                if (value.isBlank() && !insert) {
                    continue;
                }
            }
            if (value.isBlank()) {
                if (field.isRequired()) {
                    throw new IllegalArgumentException("Vui lòng nhập " + field.getLabel() + ".");
                }
                values.put(field, field.getType() == ValueType.TEXT ? "" : null);
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

    private void insert(Map<Field, Object> values, byte[] photo, boolean photoColumnAvailable) throws SQLException {
        List<String> names = new ArrayList<>();
        values.keySet().forEach(field -> names.add(field.getName()));
        if (photoColumnAvailable) {
            names.add("photo");
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(names.size(), "?"));
        String sql = "INSERT INTO " + definition.getTableName() + " (" + String.join(", ", names)
                + ") VALUES (" + placeholders + ")";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            if (photoColumnAvailable) {
                statement.setBytes(values.size() + 1, photo);
            }
            statement.executeUpdate();
        }
    }

    private void update(Object id, Map<Field, Object> values, byte[] photo,
            boolean photoChanged) throws SQLException {
        List<String> assignments = new ArrayList<>();
        values.keySet().forEach(field -> assignments.add(field.getName() + " = ?"));
        if (photoChanged) {
            assignments.add("photo = ?");
        }
        if (assignments.isEmpty()) {
            return;
        }
        String sql = "UPDATE " + definition.getTableName() + " SET " + assignments
                .stream().reduce((left, right) -> left + ", " + right).orElseThrow()
                + " WHERE " + definition.getIdColumn() + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            int nextIndex = values.size() + 1;
            if (photoChanged) {
                statement.setBytes(nextIndex++, photo);
            }
            statement.setObject(nextIndex, id);
            statement.executeUpdate();
        }
    }

    private boolean hasPhotoField() {
        return "Pet".equals(definition.getTableName()) || "Employee".equals(definition.getTableName());
    }

    private boolean isPhotoColumnAvailable() {
        String sql = "SELECT data_row.photo FROM dbo." + definition.getTableName()
                + " AS data_row WHERE 1 = 0";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeQuery().close();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Cửa sổ vẫn mở được nhưng chức năng ảnh chưa sẵn sàng.\n"
                    + "Hãy chạy photo_columns.sql trong cơ sở dữ liệu PetClinicDB.\n\n"
                    + "Chi tiết: " + e.getMessage(),
                    "Cần cập nhật cơ sở dữ liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private byte[] loadPhoto(Object id) throws SQLException {
        String sql = "SELECT data_row.photo FROM dbo." + definition.getTableName()
                + " AS data_row WHERE data_row." + definition.getIdColumn() + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBytes("photo") : null;
            }
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
        table.setEnabled(false);
        new javax.swing.SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws SQLException {
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, id);
                    return statement.executeUpdate();
                }
            }

            @Override
            protected void done() {
                table.setEnabled(true);
                try {
                    if (get() == 0) {
                        throw new SQLException("Bản ghi đã bị xóa hoặc không còn tồn tại.");
                    }
                    loadData();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showError("Không thể xóa bản ghi", e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError("Không thể xóa bản ghi", cause);
                } catch (SQLException e) {
                    showError("Không thể xóa bản ghi", e);
                }
            }
        }.execute();
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
        String details = error.getMessage();
        if (error instanceof SQLException sqlError && sqlError.getSQLState() != null) {
            details += "\nSQL State: " + sqlError.getSQLState()
                    + " | Mã lỗi: " + sqlError.getErrorCode();
        }
        JOptionPane.showMessageDialog(this, action + ":\n" + details,
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String action, Throwable error) {
        String details = error.getMessage();
        if (error instanceof SQLException sqlError && sqlError.getSQLState() != null) {
            details += "\nSQL State: " + sqlError.getSQLState()
                    + " | Mã lỗi: " + sqlError.getErrorCode();
        }
        JOptionPane.showMessageDialog(this, action + ":\n" + details,
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static final class LookupSpec {
        private final String tableName;
        private final String idColumn;
        private final String labelColumn;

        private LookupSpec(String tableName, String idColumn, String labelColumn) {
            this.tableName = tableName;
            this.idColumn = idColumn;
            this.labelColumn = labelColumn;
        }
    }

    private static final class ComboOption {
        private final Object value;
        private final String label;

        private ComboOption(Object value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final class PhotoEditor extends JPanel {
        private final JLabel preview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        private byte[] photo;
        private boolean changed;

        private PhotoEditor(boolean photoColumnAvailable) {
            setLayout(new BorderLayout(0, 12));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.BORDER),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            setPreferredSize(new Dimension(220, 300));
            JLabel title = new JLabel("ẢNH ĐẠI DIỆN");
            title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 12f));
            title.setForeground(UiTheme.MUTED);
            add(title, BorderLayout.NORTH);
            preview.setOpaque(true);
            preview.setBackground(UiTheme.BLUE_PALE);
            preview.setForeground(UiTheme.MUTED);
            preview.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
            add(preview, BorderLayout.CENTER);

            JButton choose = new JButton("Chọn ảnh");
            JButton remove = new JButton("Gỡ ảnh");
            UiTheme.styleSecondary(choose);
            UiTheme.styleSecondary(remove);
            JPanel controls = new JPanel(new GridLayout(1, 2, 8, 0));
            controls.setOpaque(false);
            controls.add(choose);
            controls.add(remove);
            JLabel hint = new JLabel(photoColumnAvailable
                    ? "<html>JPG, PNG, GIF hoặc BMP<br>Tối đa 5 MB</html>"
                    : "<html>Chạy photo_columns.sql<br>để bật chức năng ảnh.</html>");
            hint.setForeground(UiTheme.MUTED);
            hint.setFont(hint.getFont().deriveFont(11f));
            JPanel footer = new JPanel(new BorderLayout(0, 8));
            footer.setOpaque(false);
            footer.add(controls, BorderLayout.NORTH);
            footer.add(hint, BorderLayout.SOUTH);
            add(footer, BorderLayout.SOUTH);
            choose.setEnabled(photoColumnAvailable);
            remove.setEnabled(photoColumnAvailable);
            choose.addActionListener(event -> choosePhoto());
            remove.addActionListener(event -> {
                photo = null;
                changed = true;
                renderPhoto();
            });
            remove.setToolTipText("Xóa ảnh đang gắn với hồ sơ");
        }

        private void setInitialPhoto(byte[] value) {
            photo = value == null ? null : Arrays.copyOf(value, value.length);
            renderPhoto();
        }

        private byte[] getPhoto() {
            return photo == null ? null : Arrays.copyOf(photo, photo.length);
        }

        private boolean isChanged() {
            return changed;
        }

        private void choosePhoto() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter(
                    "Tệp ảnh (JPG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp"));
            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                Path path = chooser.getSelectedFile().toPath();
                long size = Files.size(path);
                if (size == 0 || size > MAX_PHOTO_BYTES) {
                    throw new IllegalArgumentException("Ảnh phải có dung lượng từ 1 byte đến 5 MB.");
                }
                if (ImageIO.read(path.toFile()) == null) {
                    throw new IllegalArgumentException("Tệp đã chọn không phải ảnh được hỗ trợ.");
                }
                photo = Files.readAllBytes(path);
                changed = true;
                renderPhoto();
            } catch (IOException | IllegalArgumentException e) {
                showError("Không thể tải ảnh", e);
            }
        }

        private void renderPhoto() {
            if (photo == null) {
                preview.setIcon(null);
                preview.setText("Chưa có ảnh");
                return;
            }
            try {
                java.awt.image.BufferedImage image = ImageIO.read(new ByteArrayInputStream(photo));
                if (image == null) {
                    throw new IOException("Dữ liệu ảnh không hợp lệ.");
                }
                int width = preview.getWidth() > 0 ? preview.getWidth() - 16 : 180;
                int height = preview.getHeight() > 0 ? preview.getHeight() - 16 : 210;
                double scale = Math.min((double) width / image.getWidth(), (double) height / image.getHeight());
                int scaledWidth = Math.max(1, (int) (image.getWidth() * scale));
                int scaledHeight = Math.max(1, (int) (image.getHeight() * scale));
                Image scaled = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                preview.setText("");
                preview.setIcon(new ImageIcon(scaled));
            } catch (IOException e) {
                showError("Không thể hiển thị ảnh", e);
                photo = null;
                preview.setIcon(null);
                preview.setText("Ảnh không hợp lệ");
            }
        }
    }
}
