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
        setTitle("Hệ Thống Quản Lý Phòng Khám Thú Y - Đăng Nhập");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        setResizable(false);

        // Panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(41, 128, 185));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form nhập thông tin
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword = new JPasswordField();

        formPanel.add(lblUsername);
        formPanel.add(txtUsername);
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnLogin = new JButton("Đăng nhập");
        btnCancel = new JButton("Thoát");

        // Styling cho button
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 13));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Bắt sự kiện nút bấm
        btnLogin.addActionListener(e -> handleLogin());
        btnCancel.addActionListener(e -> System.exit(0));

        // Cho phép nhấn Enter để đăng nhập
        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
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
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Đăng nhập thành công!\nXin chào: " + emp.getFullName() + " (" + emp.getRole() + ")", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    
                    dispose(); // Đóng màn hình đăng nhập
                    
                    // TODO: Mở Màn hình chính (MainFrame) tại đây
                    // Mở MainFrame truyền đối tượng Employee vào
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame(emp);
                        mainFrame.setVisible(true);
                    });
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                        "Tên đăng nhập hoặc mật khẩu không chính xác!", 
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(LoginFrame.this, 
                    "Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    worker.execute();
}

    public static void main(String[] args) {
        // Thiết lập Look and Feel giao diện hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}