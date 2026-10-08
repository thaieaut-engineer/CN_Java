package view;

import model.Employee;
import model.ModuleDefinition;
import model.ModuleDefinition.Column;
import model.ModuleDefinition.Field;
import model.ModuleDefinition.ValueType;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.util.function.Supplier;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private final Employee currentEmployee;
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final JPanel menuContainer = new JPanel();
    private final List<JButton> menuButtons = new ArrayList<>();
    private final Map<String, Supplier<JPanel>> panelSuppliers = new HashMap<>();
    private final Set<String> loadedCards = new java.util.HashSet<>();
    private JButton selectedButton;
    private int cardNumber;

    public MainFrame(Employee employee) {
        this.currentEmployee = employee;
        initComponents();
    }

    private void initComponents() {
        setTitle("PetClinic - " + currentEmployee.getFullName() + " (" + currentEmployee.getRole() + ")");
        setSize(1320, 800);
        setMinimumSize(new Dimension(1050, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.PAGE);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UiTheme.NAV);
        sidebar.setPreferredSize(new Dimension(258, 0));
        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(22, 18, 18, 14));
        JPanel brandIdentity = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandIdentity.setOpaque(false);
        JLabel mark = new JLabel(new ClinicIcon("pet", Color.WHITE, 24));
        JPanel markBox = new JPanel(new BorderLayout());
        markBox.setBackground(UiTheme.BLUE);
        markBox.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
        markBox.add(mark);
        JLabel name = new JLabel("PHÒNG KHÁM");
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(Color.WHITE);
        JLabel subName = new JLabel("CHĂM SÓC THÚ CƯNG");
        subName.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        subName.setForeground(new Color(159, 183, 219));
        JPanel brandText = new JPanel(new GridLayout(0, 1, 0, 2));
        brandText.setOpaque(false);
        brandText.add(name);
        brandText.add(subName);
        brandIdentity.add(markBox);
        brandIdentity.add(brandText);
        brand.add(brandIdentity, BorderLayout.NORTH);
        JPanel userCard = new JPanel(new BorderLayout(10, 0));
        userCard.setOpaque(false);
        userCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(48, 73, 117)),
                BorderFactory.createEmptyBorder(14, 2, 2, 2)));
        JLabel avatar = new JLabel(new ClinicIcon("employee", new Color(191, 219, 254), 22));
        avatar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel avatarBox = new JPanel(new BorderLayout());
        avatarBox.setBackground(new Color(30, 62, 115));
        avatarBox.add(avatar);
        JLabel userName = new JLabel(escape(currentEmployee.getFullName()));
        userName.setForeground(Color.WHITE);
        userName.setFont(userName.getFont().deriveFont(Font.BOLD, 12f));
        JLabel userRole = new JLabel(escape(currentEmployee.getRole()));
        userRole.setForeground(new Color(159, 183, 219));
        JPanel userText = new JPanel(new GridLayout(0, 1, 0, 4));
        userText.setOpaque(false);
        userText.add(userName);
        userText.add(userRole);
        userCard.add(avatarBox, BorderLayout.WEST);
        userCard.add(userText, BorderLayout.CENTER);
        brand.add(userCard, BorderLayout.SOUTH);
        sidebar.add(brand, BorderLayout.NORTH);

        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(UiTheme.NAV);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(8, 10, 12, 10));
        addMenu("Tổng quan", "DASHBOARD", () -> new DashboardPanel(currentEmployee), Set.of("admin"));
        addMenu("Chi nhánh", "BRANCH", createBranchDefinition(), Set.of("admin"));
        addMenu("Nhân viên & tài khoản", "EMPLOYEE", createEmployeeDefinition(), Set.of("admin"));
        addMenu("Khách hàng", "CUSTOMER", createCustomerDefinition(), Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        addMenu("Thú cưng", "PET", createPetDefinition(), Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        addMenu("Lịch hẹn", "APPOINTMENT", createAppointmentDefinition(), Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        addMenu("Phiếu khám", "MEDICAL", createMedicalDefinition(), Set.of("admin", "bác sĩ", "bacsi"));
        addMenu("Chi tiết khám / kê đơn", "DETAIL", createMedicalDetailDefinition(), Set.of("admin", "bác sĩ", "bacsi"));
        addMenu("Thuốc & dịch vụ", "SERVICE", createServiceDefinition(), Set.of("admin", "bác sĩ", "bacsi"));
        addMenu("Tiêm phòng", "VACCINATION", this::createVaccinationPanel, Set.of("admin", "bác sĩ", "bacsi"));
        addMenu("Nhập - xuất tồn kho", "INVENTORY", () -> new InventoryPanel(currentEmployee), Set.of("admin", "bác sĩ", "bacsi"));
        addMenu("Hóa đơn & thanh toán", "INVOICE", () -> new InvoicePanel(currentEmployee),
                Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        addMenu("Báo cáo doanh thu", "REPORT", ReportsPanel::new, Set.of("admin"));
        addMenu("Nhắc lịch", "REMINDER", ReminderPanel::new,
                Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        addMenu("Lịch sử khám toàn chuỗi", "HISTORY", HistoryPanel::new,
                Set.of("admin", "bác sĩ", "bacsi", "nhân viên", "nhanvien"));
        JScrollPane navigationScroll = new JScrollPane(menuContainer);
        navigationScroll.setBorder(BorderFactory.createEmptyBorder());
        navigationScroll.setOpaque(false);
        navigationScroll.getViewport().setOpaque(false);
        sidebar.add(navigationScroll, BorderLayout.CENTER);
        JButton logout = new JButton("Đăng xuất");
        logout.setIcon(new ClinicIcon("logout", new Color(254, 202, 202), 18));
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setIconTextGap(12);
        logout.setMargin(new Insets(10, 12, 10, 12));
        logout.setForeground(new Color(254, 226, 226));
        logout.setBackground(new Color(127, 45, 58));
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        logout.addActionListener(event -> handleLogout());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 14, 16, 14));
        bottom.add(logout);
        sidebar.add(bottom, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        contentPanel.setBackground(UiTheme.PAGE);
        add(contentPanel, BorderLayout.CENTER);
        if (!menuButtons.isEmpty()) {
            switchTab(menuButtons.get(0), (String) menuButtons.get(0).getClientProperty("card"));
        }
    }

    private void addMenu(String label, String id, ModuleDefinition definition, Set<String> roles) {
        addMenu(label, id, () -> new CrudPanel(definition), roles);
    }

    private void addMenu(String label, String id, Supplier<JPanel> panelSupplier, Set<String> roles) {
        String role = currentEmployee.getRole() == null ? "" : currentEmployee.getRole().trim().toLowerCase(Locale.ROOT);
        if (!roles.contains(role)) {
            return;
        }
        String card = id + "_" + cardNumber++;
        panelSuppliers.put(card, panelSupplier);
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIcon(new ClinicIcon(id.toLowerCase(Locale.ROOT), new Color(159, 183, 219), 18));
        button.putClientProperty("iconType", id.toLowerCase(Locale.ROOT));
        button.setIconTextGap(13);
        button.setMargin(new Insets(10, 12, 10, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setForeground(new Color(205, 218, 238));
        button.setBackground(UiTheme.NAV);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.putClientProperty("card", card);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (button != selectedButton) {
                    button.setBackground(UiTheme.NAV_LIGHT);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (button != selectedButton) {
                    button.setBackground(UiTheme.NAV);
                    button.setForeground(new Color(205, 218, 238));
                }
            }
        });
        button.addActionListener(event -> switchTab(button, card));
        menuButtons.add(button);
        menuContainer.add(button);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void switchTab(JButton button, String card) {
        if (!loadedCards.contains(card)) {
            Supplier<JPanel> supplier = panelSuppliers.get(card);
            if (supplier == null) {
                throw new IllegalStateException("Không tìm thấy màn hình: " + card);
            }
            JPanel panel = supplier.get();
            contentPanel.add(panel, card);
            loadedCards.add(card);
        }
        if (selectedButton != null) {
            selectedButton.setBackground(UiTheme.NAV);
            selectedButton.setForeground(new Color(205, 218, 238));
            selectedButton.setFont(selectedButton.getFont().deriveFont(Font.PLAIN));
            selectedButton.setIcon(new ClinicIcon((String) selectedButton.getClientProperty("iconType"),
                    new Color(159, 183, 219), 18));
        }
        selectedButton = button;
        button.setBackground(UiTheme.BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setIcon(new ClinicIcon((String) button.getClientProperty("iconType"), Color.WHITE, 18));
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, card);
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private ModuleDefinition createBranchDefinition() {
        return definition("Quản lý chi nhánh", "Branch", "branch_id",
                fields(text("name", "Tên chi nhánh", true), text("address", "Địa chỉ", false),
                        text("phone", "Điện thoại", false)),
                columns(col("branch_id", "Mã"), col("name", "Tên chi nhánh"), col("address", "Địa chỉ"), col("phone", "Điện thoại")));
    }

    private ModuleDefinition createEmployeeDefinition() {
        return definition("Quản lý nhân viên & tài khoản", "Employee", "employee_id",
                fields(integer("branch_id", "Chi nhánh", true), text("full_name", "Họ tên", true),
                        text("role", "Vai trò", true), text("username", "Tên đăng nhập", true),
                        text("account_status", "Trạng thái tài khoản", true),
                        new Field("password", "Mật khẩu (để trống khi giữ nguyên)", ValueType.PASSWORD, true)),
                columns(col("employee_id", "Mã"), col("branch_id", "Chi nhánh"), col("full_name", "Họ tên"),
                        col("role", "Vai trò"), col("username", "Tài khoản"),
                        col("account_status", "Trạng thái")));
    }

    private ModuleDefinition createCustomerDefinition() {
        return definition("Quản lý khách hàng", "Customer", "customer_id",
                fields(text("full_name", "Họ tên", true), text("phone", "Điện thoại", true), text("address", "Địa chỉ", false)),
                columns(col("customer_id", "Mã"), col("full_name", "Họ tên"),
                        col("phone", "Điện thoại"), col("address", "Địa chỉ")));
    }

    private ModuleDefinition createPetDefinition() {
        return definition("Quản lý thú cưng", "Pet", "pet_id",
                fields(integer("customer_id", "Chủ nuôi", true), text("name", "Tên thú cưng", true),
                        text("species", "Loài", false), text("breed", "Giống", false), integer("age", "Tuổi", false)),
                columns(col("pet_id", "Mã"), col("customer_id", "Mã chủ nuôi"), col("name", "Tên"),
                        col("species", "Loài"), col("breed", "Giống"), col("age", "Tuổi")));
    }

    private ModuleDefinition createAppointmentDefinition() {
        return definition("Quản lý lịch hẹn", "Appointment", "appointment_id",
                fields(integer("customer_id", "Khách hàng", true), integer("pet_id", "Thú cưng", true),
                        integer("branch_id", "Chi nhánh", true), integer("employee_id", "Nhân viên phụ trách (có thể trống)", false),
                        dateTime("appointment_date", "Ngày giờ (yyyy-MM-dd HH:mm:ss)", true),
                        text("status", "Trạng thái", true),
                        text("notes", "Ghi chú", false)),
                columns(col("appointment_id", "Mã"), col("customer_id", "Khách"), col("pet_id", "Thú cưng"),
                        col("branch_id", "Chi nhánh"), col("employee_id", "Nhân viên"), col("appointment_date", "Ngày giờ"),
                        col("status", "Trạng thái"), col("notes", "Ghi chú")));
    }

    private ModuleDefinition createMedicalDefinition() {
        return definition("Quản lý phiếu khám", "MedicalRecord", "record_id",
                fields(integer("pet_id", "Thú cưng", true), integer("employee_id", "Bác sĩ", true),
                        integer("branch_id", "Chi nhánh", true), dateTime("visit_date", "Ngày khám (có thể trống)", false),
                        text("diagnosis", "Chẩn đoán", false), text("notes", "Ghi chú", false),
                        dateTime("revisit_date", "Ngày tái khám (có thể trống)", false)),
                columns(col("record_id", "Mã"), col("pet_id", "Thú cưng"), col("employee_id", "Bác sĩ"),
                        col("branch_id", "Chi nhánh"), col("visit_date", "Ngày khám"), col("diagnosis", "Chẩn đoán"),
                        col("notes", "Ghi chú"), col("revisit_date", "Ngày tái khám")));
    }

    private ModuleDefinition createMedicalDetailDefinition() {
        return definition("Chi tiết khám bệnh / kê thuốc, dịch vụ", "MedicalDetail", "detail_id",
                fields(integer("record_id", "Phiếu khám", true), integer("service_id", "Thuốc / dịch vụ", true),
                        integer("quantity", "Số lượng", true), decimal("unit_price", "Đơn giá", true)),
                columns(col("detail_id", "Mã"), col("record_id", "Phiếu khám"), col("service_id", "Thuốc/dịch vụ"),
                        col("quantity", "Số lượng"), col("unit_price", "Đơn giá")));
    }

    private ModuleDefinition createServiceDefinition() {
        return definition("Quản lý thuốc và dịch vụ", "Service", "service_id",
                fields(text("name", "Tên thuốc/dịch vụ", true), decimal("price", "Giá bán", true),
                        text("type", "Loại (Thuoc/TiemPhong/KhamBenh/Spa)", true), text("unit", "Đơn vị", false)),
                columns(col("service_id", "Mã"), col("name", "Tên"), col("price", "Giá"),
                        col("type", "Loại"), col("unit", "Đơn vị"), col("stock_quantity", "Tồn kho")));
    }

    private JPanel createVaccinationPanel() {
        return new CrudPanel(definition("Theo dõi tiêm phòng và lịch nhắc", "Vaccination", "vaccination_id",
                fields(integer("record_id", "Phiếu khám (có thể trống)", false),
                        integer("pet_id", "Thú cưng", true), integer("service_id", "Vắc-xin", true),
                        integer("employee_id", "Nhân viên", true), integer("branch_id", "Chi nhánh", true),
                        dateTime("administered_date", "Ngày tiêm (yyyy-MM-dd HH:mm:ss)", true),
                        dateTime("next_due_date", "Ngày tiêm nhắc (yyyy-MM-dd HH:mm:ss)", true),
                        text("notes", "Ghi chú", false)),
                columns(col("vaccination_id", "Mã"), col("record_id", "Phiếu khám"), col("pet_id", "Thú cưng"),
                        col("service_id", "Vắc-xin"), col("employee_id", "Nhân viên"), col("branch_id", "Chi nhánh"),
                        col("administered_date", "Ngày tiêm"), col("next_due_date", "Ngày nhắc"), col("notes", "Ghi chú"))));
    }

    private static ModuleDefinition definition(String title, String table, String id,
            List<Field> fields, List<Column> columns) {
        return new ModuleDefinition(title, table, id, fields, columns);
    }

    private static List<Field> fields(Field... values) { return List.of(values); }
    private static List<Column> columns(Column... values) { return List.of(values); }
    private static Field text(String name, String label, boolean required) { return new Field(name, label, ValueType.TEXT, required); }
    private static Field integer(String name, String label, boolean required) { return new Field(name, label, ValueType.INTEGER, required); }
    private static Field decimal(String name, String label, boolean required) { return new Field(name, label, ValueType.DECIMAL, required); }
    private static Field dateTime(String name, String label, boolean required) { return new Field(name, label, ValueType.DATETIME, required); }
    private static Column col(String name, String label) { return new Column(name, label); }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
