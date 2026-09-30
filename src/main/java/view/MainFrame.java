/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import com.formdev.flatlaf.FlatLightLaf;
import model.Employee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private Employee currentEmployee;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton selectedButton = null; // Lưu nút đang được chọn

    public MainFrame(Employee employee) {
        this.currentEmployee = employee;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Phòng Khám Thú Y - [ " + currentEmployee.getFullName() + " | " + currentEmployee.getRole() + " ]");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // 1. Sidebar Panel (Thanh Menu bên trái)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(new Color(33, 43, 54)); // Màu xám xanh đậm sang trọng
        sidebarPanel.setPreferredSize(new Dimension(240, 0));

        // Header Sidebar (Logo / Tên thương hiệu)
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        JLabel lblBrand = new JLabel("PET CLINIC", JLabel.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(new Color(255, 255, 255));
        
        JLabel lblSubBrand = new JLabel("Chăm sóc & Khám chữa bệnh", JLabel.CENTER);
        lblSubBrand.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSubBrand.setForeground(new Color(145, 158, 171));
        
        brandPanel.add(lblBrand, BorderLayout.NORTH);
        brandPanel.add(lblSubBrand, BorderLayout.SOUTH);
        sidebarPanel.add(brandPanel, BorderLayout.NORTH);

        // Menu Buttons Container
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnPet = createSidebarButton(" Quản lý Thú cưng");
        JButton btnCustomer = createSidebarButton(" Quản lý Khách hàng");
        JButton btnMedical = createSidebarButton(" Hồ sơ Khám bệnh");
        JButton btnInvoice = createSidebarButton(" Hóa đơn & Thanh toán");
        JButton btnEmployee = createSidebarButton(" Quản lý Nhân viên");

        menuContainer.add(btnPet);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        menuContainer.add(btnCustomer);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        menuContainer.add(btnMedical);
        menuContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        menuContainer.add(btnInvoice);

        if ("Admin".equalsIgnoreCase(currentEmployee.getRole())) {
            menuContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            menuContainer.add(btnEmployee);
        }

        sidebarPanel.add(menuContainer, BorderLayout.CENTER);

        // Nút Đăng xuất ở dưới cùng Sidebar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JButton btnLogout = new JButton(" Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(225, 112, 85)); // Màu đỏ nhạt hiện đại
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(0, 40));
        btnLogout.addActionListener(e -> handleLogout());

        bottomPanel.add(btnLogout, BorderLayout.SOUTH);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);

        // 2. Content Panel (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(244, 246, 248));

        contentPanel.add(new PetPanel(), "PET");
        contentPanel.add(new CustomerPanel(), "CUSTOMER");
        contentPanel.add(createDummyPanel("MÀN HÌNH HỒ SƠ KHÁM BỆNH"), "MEDICAL");
        contentPanel.add(createDummyPanel("MÀN HÌNH HÓA ĐƠN & THANH TOÁN"), "INVOICE");
        contentPanel.add(createDummyPanel("MÀN HÌNH QUẢN LÝ NHÂN VIÊN"), "EMPLOYEE");

        add(contentPanel, BorderLayout.CENTER);

        // 3. Bắt sự kiện chuyển Tab & highlight nút
        btnPet.addActionListener(e -> switchTab(btnPet, "PET"));
        btnCustomer.addActionListener(e -> switchTab(btnCustomer, "CUSTOMER"));
        btnMedical.addActionListener(e -> switchTab(btnMedical, "MEDICAL"));
        btnInvoice.addActionListener(e -> switchTab(btnInvoice, "INVOICE"));
        btnEmployee.addActionListener(e -> switchTab(btnEmployee, "EMPLOYEE"));

        // Chọn mặc định tab đầu tiên
        switchTab(btnPet, "PET");
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(197, 208, 220));
        btn.setBackground(new Color(33, 43, 54));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != selectedButton) {
                    btn.setBackground(new Color(45, 58, 72));
                    btn.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != selectedButton) {
                    btn.setBackground(new Color(33, 43, 54));
                    btn.setForeground(new Color(197, 208, 220));
                }
            }
        });

        return btn;
    }

    private void switchTab(JButton btn, String cardName) {
        // Reset nút cũ
        if (selectedButton != null) {
            selectedButton.setBackground(new Color(33, 43, 54));
            selectedButton.setForeground(new Color(197, 208, 220));
            selectedButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        // Active nút mới
        selectedButton = btn;
        selectedButton.setBackground(new Color(24, 144, 255)); // Màu xanh dương Active
        selectedButton.setForeground(Color.WHITE);
        selectedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cardLayout.show(contentPanel, cardName);
    }

    private JPanel createDummyPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(244, 246, 248));
        
        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(99, 115, 129));
        
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        // Áp dụng FlatLaf cho toàn bộ ứng dụng
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test trực tiếp MainFrame với tài khoản Admin giả lập
        SwingUtilities.invokeLater(() -> {
            new MainFrame(new Employee(1, 1, "Quản trị viên", "Admin", "admin", "admin123")).setVisible(true);
        });
    }
}