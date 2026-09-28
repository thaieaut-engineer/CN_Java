/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Employee;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Employee currentEmployee;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(Employee employee) {
        this.currentEmployee = employee;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Phòng Khám Thú Y - [Xin chào: " + currentEmployee.getFullName() + " - " + currentEmployee.getRole() + "]");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // 1. Sidebar Panel (Thanh menu bên trái)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridLayout(8, 1, 5, 10));
        sidebarPanel.setBackground(new Color(44, 62, 80));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Các nút menu
        JButton btnPet = createSidebarButton(" Quản lý Thú cưng");
        JButton btnCustomer = createSidebarButton(" Quản lý Khách hàng");
        JButton btnMedical = createSidebarButton(" Hồ sơ Khám bệnh");
        JButton btnInvoice = createSidebarButton(" Hóa đơn & Thanh toán");
        JButton btnEmployee = createSidebarButton(" Quản lý Nhân viên");
        JButton btnLogout = createSidebarButton(" Đăng xuất");

        // Styling nút Đăng xuất khác biệt
        btnLogout.setBackground(new Color(192, 57, 43));

        sidebarPanel.add(btnPet);
        sidebarPanel.add(btnCustomer);
        sidebarPanel.add(btnMedical);
        sidebarPanel.add(btnInvoice);

        // Chỉ Admin mới thấy nút Quản lý Nhân viên
        if ("Admin".equalsIgnoreCase(currentEmployee.getRole())) {
            sidebarPanel.add(btnEmployee);
        }

        sidebarPanel.add(btnLogout);

        add(sidebarPanel, BorderLayout.WEST);

        // 2. Content Panel chính (Sử dụng CardLayout để chuyển tab)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Các panel màn hình con (Tạm thời là các Panel giao diện chờ)
        contentPanel.add(createDummyPanel("Màn hình Quản lý Thú cưng"), "PET");
        contentPanel.add(createDummyPanel("Màn hình Quản lý Khách hàng"), "CUSTOMER");
        contentPanel.add(createDummyPanel("Màn hình Quản lý Khám bệnh"), "MEDICAL");
        contentPanel.add(createDummyPanel("Màn hình Quản lý Hóa đơn"), "INVOICE");
        contentPanel.add(createDummyPanel("Màn hình Quản lý Nhân viên"), "EMPLOYEE");

        add(contentPanel, BorderLayout.CENTER);

        // 3. Xử lý sự kiện chuyển giao diện
        btnPet.addActionListener(e -> cardLayout.show(contentPanel, "PET"));
        btnCustomer.addActionListener(e -> cardLayout.show(contentPanel, "CUSTOMER"));
        btnMedical.addActionListener(e -> cardLayout.show(contentPanel, "MEDICAL"));
        btnInvoice.addActionListener(e -> cardLayout.show(contentPanel, "INVOICE"));
        btnEmployee.addActionListener(e -> cardLayout.show(contentPanel, "EMPLOYEE"));

        btnLogout.addActionListener(e -> handleLogout());
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JPanel createDummyPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setForeground(new Color(127, 140, 141));
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
}