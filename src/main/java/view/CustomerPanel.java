/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCustomerId, txtFullName, txtPhone, txtAddress;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private CustomerDAO customerDAO;

    public CustomerPanel() {
        customerDAO = new CustomerDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(244, 246, 248));

        // 1. Header Title
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG (CHỦ NUÔI)", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Form Container (Trái)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(340, 0));
        leftPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin Khách hàng"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCustomerId = createTextField(false);
        txtFullName = createTextField(true);
        txtPhone = createTextField(true);
        txtAddress = createTextField(true);

        addFormField(formPanel, gbc, "Mã Khách Hàng (ID):", txtCustomerId, 0);
        addFormField(formPanel, gbc, "Họ và Tên:", txtFullName, 1);
        addFormField(formPanel, gbc, "Số Điện Thoại:", txtPhone, 2);
        addFormField(formPanel, gbc, "Địa Chỉ:", txtAddress, 3);

        // Nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = createStyledButton("Thêm mới", new Color(46, 204, 113));
        btnUpdate = createStyledButton("Cập nhật", new Color(52, 152, 219));
        btnDelete = createStyledButton("Xóa", new Color(231, 76, 60));
        btnClear = createStyledButton("Làm mới", new Color(149, 165, 166));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // 3. Table Container (Phải)
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Họ và Tên", "Số Điện Thoại", "Địa Chỉ"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 235, 240));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getSelectionModel().addListSelectionListener(e -> selectRow());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Sự kiện các nút
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());
    }

    private JTextField createTextField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(160, 32));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setEditable(editable);
        if (!editable) {
            tf.setBackground(new Color(240, 240, 240));
        }
        return tf;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        return btn;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent comp, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.getAllCustomers();
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(), c.getFullName(), c.getPhone(), c.getAddress()
            });
        }
    }

    private void selectRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtCustomerId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtFullName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            txtAddress.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
        }
    }

    private void addCustomer() {
        if (txtFullName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer c = new Customer(
            0,
            txtFullName.getText().trim(),
            txtPhone.getText().trim(),
            txtAddress.getText().trim()
        );

        if (customerDAO.addCustomer(c)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Số điện thoại có thể đã bị trùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (txtCustomerId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }

        Customer c = new Customer(
            Integer.parseInt(txtCustomerId.getText().trim()),
            txtFullName.getText().trim(),
            txtPhone.getText().trim(),
            txtAddress.getText().trim()
        );

        if (customerDAO.updateCustomer(c)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void deleteCustomer() {
        if (txtCustomerId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khách hàng sẽ xóa toàn bộ Thú cưng liên quan. Bạn có chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtCustomerId.getText().trim());
            if (customerDAO.deleteCustomer(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }
}