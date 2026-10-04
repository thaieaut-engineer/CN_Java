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
import java.awt.Font;
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

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(33, 43, 54));
        sidebar.setPreferredSize(new Dimension(245, 0));
        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        JLabel name = new JLabel("PET CLINIC", SwingConstants.CENTER);
        name.setFont(new Font("Segoe UI", Font.BOLD, 23));
        name.setForeground(Color.WHITE);
        JLabel user = new JLabel("<html><center>" + escape(currentEmployee.getFullName())
                + "<br>" + escape(currentEmployee.getRole()) + "</center></html>", SwingConstants.CENTER);
        user.setForeground(new Color(190, 202, 214));
        brand.add(name, BorderLayout.NORTH);
        brand.add(user, BorderLayout.SOUTH);
        sidebar.add(brand, BorderLayout.NORTH);

        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(new Color(33, 43, 54));
        menuContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
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
        sidebar.add(new JScrollPane(menuContainer), BorderLayout.CENTER);
        JButton logout = new JButton("Đăng xuất");
        logout.setForeground(Color.WHITE);
        logout.setBackground(new Color(198, 76, 65));
        logout.addActionListener(event -> handleLogout());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 10));
        bottom.add(logout);
        sidebar.add(bottom, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        contentPanel.setBackground(new Color(244, 246, 248));
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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setForeground(new Color(205, 214, 222));
        button.setBackground(new Color(33, 43, 54));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.putClientProperty("card", card);
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
            selectedButton.setBackground(new Color(33, 43, 54));
            selectedButton.setForeground(new Color(205, 214, 222));
            selectedButton.setFont(selectedButton.getFont().deriveFont(Font.PLAIN));
        }
        selectedButton = button;
        button.setBackground(new Color(24, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
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
                fields(integer("branch_id", "Mã chi nhánh", true), text("full_name", "Họ tên", true),
                        text("role", "Vai trò (Admin/BacSi/NhanVien)", true), text("username", "Tên đăng nhập", true),
                        new Field("password", "Mật khẩu (để trống khi giữ nguyên)", ValueType.PASSWORD, true)),
                columns(col("employee_id", "Mã"), col("branch_id", "Chi nhánh"), col("full_name", "Họ tên"),
                        col("role", "Vai trò"), col("username", "Tài khoản")));
    }

    private ModuleDefinition createCustomerDefinition() {
        return definition("Quản lý khách hàng", "Customer", "customer_id",
                fields(text("full_name", "Họ tên", true), text("phone", "Điện thoại", true), text("address", "Địa chỉ", false)),
                columns(col("customer_id", "Mã"), col("full_name", "Họ tên"),
                        col("phone", "Điện thoại"), col("address", "Địa chỉ")));
    }

    private ModuleDefinition createPetDefinition() {
        return definition("Quản lý thú cưng", "Pet", "pet_id",
                fields(integer("customer_id", "Mã khách hàng", true), text("name", "Tên thú cưng", true),
                        text("species", "Loài", false), text("breed", "Giống", false), integer("age", "Tuổi", false)),
                columns(col("pet_id", "Mã"), col("customer_id", "Mã chủ nuôi"), col("name", "Tên"),
                        col("species", "Loài"), col("breed", "Giống"), col("age", "Tuổi")));
    }

    private ModuleDefinition createAppointmentDefinition() {
        return definition("Quản lý lịch hẹn", "Appointment", "appointment_id",
                fields(integer("customer_id", "Mã khách hàng", true), integer("pet_id", "Mã thú cưng", true),
                        integer("branch_id", "Mã chi nhánh", true), integer("employee_id", "Mã nhân viên (có thể trống)", false),
                        dateTime("appointment_date", "Ngày giờ (yyyy-MM-dd HH:mm:ss)", true),
                        text("status", "Trạng thái (Pending/Confirmed/Completed/Cancelled)", false),
                        text("notes", "Ghi chú", false)),
                columns(col("appointment_id", "Mã"), col("customer_id", "Khách"), col("pet_id", "Thú cưng"),
                        col("branch_id", "Chi nhánh"), col("employee_id", "Nhân viên"), col("appointment_date", "Ngày giờ"),
                        col("status", "Trạng thái"), col("notes", "Ghi chú")));
    }

    private ModuleDefinition createMedicalDefinition() {
        return definition("Quản lý phiếu khám", "MedicalRecord", "record_id",
                fields(integer("pet_id", "Mã thú cưng", true), integer("employee_id", "Mã bác sĩ", true),
                        integer("branch_id", "Mã chi nhánh", true), dateTime("visit_date", "Ngày khám (có thể trống)", false),
                        text("diagnosis", "Chẩn đoán", false), text("notes", "Ghi chú", false),
                        dateTime("revisit_date", "Ngày tái khám (có thể trống)", false)),
                columns(col("record_id", "Mã"), col("pet_id", "Thú cưng"), col("employee_id", "Bác sĩ"),
                        col("branch_id", "Chi nhánh"), col("visit_date", "Ngày khám"), col("diagnosis", "Chẩn đoán"),
                        col("notes", "Ghi chú"), col("revisit_date", "Ngày tái khám")));
    }

    private ModuleDefinition createMedicalDetailDefinition() {
        return definition("Chi tiết khám bệnh / kê thuốc, dịch vụ", "MedicalDetail", "detail_id",
                fields(integer("record_id", "Mã phiếu khám", true), integer("service_id", "Mã thuốc/dịch vụ", true),
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
                fields(integer("record_id", "Mã phiếu khám (có thể trống)", false),
                        integer("pet_id", "Mã thú cưng", true), integer("service_id", "Mã vắc-xin", true),
                        integer("employee_id", "Mã nhân viên", true), integer("branch_id", "Mã chi nhánh", true),
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
