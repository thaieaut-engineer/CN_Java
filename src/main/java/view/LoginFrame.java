/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private EmployeeDAO employeeDAO;

    public LoginFrame() {
        employeeDAO = new EmployeeDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("PetClinic | Đăng nhập");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(new LoginBrandPanel(), BorderLayout.WEST);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(55, 54, 48, 54));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel eyebrow = new JLabel("CỔNG QUẢN LÝ PHÒNG KHÁM");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 11));
        eyebrow.setForeground(UiTheme.BLUE);
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 10, 0);
        formPanel.add(eyebrow, constraints);

        JLabel lblTitle = new JLabel("Chào mừng trở lại");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 27));
        lblTitle.setForeground(UiTheme.TEXT);
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 0, 7, 0);
        formPanel.add(lblTitle, constraints);
        JLabel subtitle = new JLabel("Đăng nhập để tiếp tục vào hệ thống.");
        subtitle.setForeground(UiTheme.MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        constraints.gridy = 2;
        constraints.insets = new Insets(0, 0, 30, 0);
        formPanel.add(subtitle, constraints);

        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        constraints.gridy = 3;
        constraints.insets = new Insets(0, 0, 8, 0);
        formPanel.add(lblUsername, constraints);
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 42));
        UiTheme.styleTextField(txtUsername);
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập tên đăng nhập");
        constraints.gridy = 4;
        constraints.insets = new Insets(0, 0, 19, 0);
        formPanel.add(txtUsername, constraints);

        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        constraints.gridy = 5;
        constraints.insets = new Insets(0, 0, 8, 0);
        formPanel.add(lblPassword, constraints);
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 42));
        UiTheme.styleTextField(txtPassword);
        txtPassword.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu");
        constraints.gridy = 6;
        constraints.insets = new Insets(0, 0, 25, 0);
        formPanel.add(txtPassword, constraints);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        btnLogin = new JButton("Đăng nhập");
        btnCancel = new JButton("Thoát");
        JButton btnRegister = new JButton("Đăng ký nhân viên");
        UiTheme.stylePrimary(btnLogin);
        UiTheme.styleSecondary(btnCancel);
        UiTheme.styleSecondary(btnRegister);
        btnLogin.setIcon(new ClinicIcon("employee", Color.WHITE, 17));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        constraints.gridy = 7;
        constraints.insets = new Insets(0, 0, 0, 0);
        formPanel.add(buttonPanel, constraints);
        constraints.gridy = 8;
        constraints.insets = new Insets(12, 0, 0, 0);
        formPanel.add(btnRegister, constraints);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        btnLogin.addActionListener(e -> handleLogin());
        btnCancel.addActionListener(e -> System.exit(0));
        btnRegister.addActionListener(e -> new RegisterDialog(this, employeeDAO).setVisible(true));
        getRootPane().setDefaultButton(btnLogin);
    }

    private static final class LoginBrandPanel extends JPanel {
        private LoginBrandPanel() {
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(365, 0));
            setOpaque(false);
            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(10, 0, 10, 0);
            JLabel mark = new JLabel(new ClinicIcon("pet", Color.WHITE, 42));
            mark.setBorder(BorderFactory.createEmptyBorder(17, 17, 17, 17));
            JPanel markBox = new JPanel(new BorderLayout());
            markBox.setBackground(new Color(255, 255, 255, 35));
            markBox.add(mark);
            constraints.gridy = 0;
            content.add(markBox, constraints);
            JLabel title = new JLabel("<html>Chăm sóc tốt hơn,<br>quản lý thông minh hơn.</html>");
            title.setFont(new Font("Segoe UI", Font.BOLD, 29));
            title.setForeground(Color.WHITE);
            constraints.gridy = 1;
            constraints.insets = new Insets(24, 0, 7, 0);
            content.add(title, constraints);
            JLabel subtitle = new JLabel("<html>PetClinic kết nối đội ngũ, khách hàng<br>và thú cưng trên toàn chuỗi.</html>");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitle.setForeground(new Color(219, 234, 254));
            constraints.gridy = 2;
            constraints.insets = new Insets(4, 0, 0, 0);
            content.add(subtitle, constraints);
            add(content);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(30, 64, 175),
                    getWidth(), getHeight(), new Color(14, 165, 233)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 22));
            g.fillOval(-90, getHeight() - 200, 280, 280);
            g.fillOval(getWidth() - 105, -80, 210, 210);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private void handleLogin() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword());

    if (username.isEmpty() || password.isBlank()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Vô hiệu hóa nút đăng nhập & đổi con trỏ chuột thành dạng chờ (Loading)
    btnLogin.setEnabled(false);
    btnLogin.setText("Đang xử lý...");
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // Thực hiện truy vấn DB ngầm bằng SwingWorker
    SwingWorker<Employee, Void> worker = new SwingWorker<>() {
        @Override
        protected Employee doInBackground() throws Exception {
            return employeeDAO.login(username, password);
        }

        @Override
        protected void done() {
            // Khôi phục lại trạng thái giao diện
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng nhập");
            setCursor(Cursor.getDefaultCursor());

            try {
                Employee emp = get();
                if (emp != null) {
                    if (!hasSupportedRole(emp.getRole())) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Tài khoản chưa có vai trò được hỗ trợ. Vui lòng liên hệ quản trị viên.",
                            "Không đủ quyền", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        MainFrame mainFrame = new MainFrame(emp);
                        mainFrame.setVisible(true);
                        dispose();
                    } catch (RuntimeException startupError) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Đăng nhập đúng nhưng không thể mở màn hình chính:\n" + startupError.getMessage(),
                            "Lỗi khởi động", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Tên đăng nhập hoặc mật khẩu không chính xác!", 
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                Throwable cause = e.getCause() == null ? e : e.getCause();
                JOptionPane.showMessageDialog(LoginFrame.this, 
                    "Không thể đăng nhập: " + cause.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    worker.execute();
}

    private boolean hasSupportedRole(String role) {
        if (role == null) return false;
        String normalized = role.trim().toLowerCase(java.util.Locale.ROOT);
        return normalized.equals("admin") || normalized.equals("bacsi") || normalized.equals("bác sĩ")
                || normalized.equals("nhanvien") || normalized.equals("nhân viên");
    }

    public static void main(String[] args) {
        // Thiết lập Look and Feel giao diện hệ thống
        try {
        com.formdev.flatlaf.FlatLightLaf.setup(); // Bật giao diện FlatLaf
    } catch (Exception e) {
        e.printStackTrace();
    }
    UiTheme.install();

    SwingUtilities.invokeLater(() -> {
        new LoginFrame().setVisible(true);
    });
    }
}