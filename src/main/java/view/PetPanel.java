/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import bus.CustomerBUS;
import bus.PetBUS;
import model.Customer;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PetPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtPetId, txtName, txtSpecies, txtBreed, txtAge;
    private JComboBox<Customer> cbCustomer; // Đổi sang JComboBox chứa đối tượng Customer
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    
    private PetBUS petBUS;
    private CustomerBUS customerBUS;

    public PetPanel() {
        petBUS = new PetBUS();
        customerBUS = new CustomerBUS();
        initComponents();
        loadCustomerComboBox();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(244, 246, 248));

        // 1. Header Title
        JLabel lblTitle = new JLabel("QUẢN LÝ THÚ CƯNG", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Form Container (Trái)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(340, 0));
        leftPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin Thú cưng"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Khởi tạo các Component
        txtPetId = createTextField(false);
        cbCustomer = new JComboBox<>();
        cbCustomer.setPreferredSize(new Dimension(160, 32));
        cbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtName = createTextField(true);
        txtSpecies = createTextField(true);
        txtBreed = createTextField(true);
        txtAge = createTextField(true);

        // Thêm trường dữ liệu vào form
        addFormField(formPanel, gbc, "Mã Thú Cưng (ID):", txtPetId, 0);
        addFormField(formPanel, gbc, "Chủ Nuôi:", cbCustomer, 1);
        addFormField(formPanel, gbc, "Tên Thú Cưng:", txtName, 2);
        addFormField(formPanel, gbc, "Loài (Chó/Mèo...):", txtSpecies, 3);
        addFormField(formPanel, gbc, "Giống:", txtBreed, 4);
        addFormField(formPanel, gbc, "Tuổi:", txtAge, 5);

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
            new String[]{"ID", "Chủ Nuôi ID", "Tên Thú Cưng", "Loài", "Giống", "Tuổi"}, 0
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
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.getSelectionModel().addListSelectionListener(e -> selectRow());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Sự kiện các nút
        btnAdd.addActionListener(e -> addPet());
        btnUpdate.addActionListener(e -> updatePet());
        btnDelete.addActionListener(e -> deletePet());
        btnClear.addActionListener(e -> clearForm());
    }

    private void loadCustomerComboBox() {
        cbCustomer.removeAllItems();
        try {
            List<Customer> customers = customerBUS.getAll();
            for (Customer c : customers) {
                cbCustomer.addItem(c);
            }
        } catch (java.sql.SQLException e) {
            showError("Không thể tải khách hàng", e);
        }
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
        try {
            List<Pet> list = petBUS.getAll();
            for (Pet p : list) {
                tableModel.addRow(new Object[]{
                    p.getPetId(), p.getCustomerId(), p.getName(), p.getSpecies(), p.getBreed(), p.getAge()
                });
            }
        } catch (java.sql.SQLException e) {
            showError("Không thể tải thú cưng", e);
        }
    }

    private void selectRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            txtPetId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            int customerId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());

            // Chọn item tương ứng trong JComboBox theo customerId
            for (int i = 0; i < cbCustomer.getItemCount(); i++) {
                Customer c = cbCustomer.getItemAt(i);
                if (c.getCustomerId() == customerId) {
                    cbCustomer.setSelectedIndex(i);
                    break;
                }
            }

            txtName.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtSpecies.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtBreed.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtAge.setText(tableModel.getValueAt(selectedRow, 5).toString());
        }
    }

    private void addPet() {
        Customer selectedCustomer = (Customer) cbCustomer.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Chủ nuôi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên thú cưng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Pet pet = new Pet(
                0,
                selectedCustomer.getCustomerId(),
                txtName.getText().trim(),
                txtSpecies.getText().trim(),
                txtBreed.getText().trim(),
                Integer.parseInt(txtAge.getText().trim())
            );

            petBUS.create(pet);
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
            clearForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | java.sql.SQLException e) {
            showError("Không thể thêm thú cưng", e);
        }
    }

    private void updatePet() {
        if (txtPetId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hàng trong bảng để sửa!");
            return;
        }

        Customer selectedCustomer = (Customer) cbCustomer.getSelectedItem();
        if (selectedCustomer == null) return;

        try {
            Pet pet = new Pet(
                Integer.parseInt(txtPetId.getText().trim()),
                selectedCustomer.getCustomerId(),
                txtName.getText().trim(),
                txtSpecies.getText().trim(),
                txtBreed.getText().trim(),
                Integer.parseInt(txtAge.getText().trim())
            );

            petBUS.update(pet);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            clearForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên!");
        } catch (IllegalArgumentException | java.sql.SQLException e) {
            showError("Không thể cập nhật thú cưng", e);
        }
    }

    private void deletePet() {
        if (txtPetId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thú cưng cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa thú cưng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int petId = Integer.parseInt(txtPetId.getText().trim());
            try {
                petBUS.delete(petId);
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
                clearForm();
            } catch (IllegalArgumentException | java.sql.SQLException e) {
                showError("Không thể xóa thú cưng", e);
            }
        }
    }

    private void showError(String title, Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        txtPetId.setText("");
        if (cbCustomer.getItemCount() > 0) cbCustomer.setSelectedIndex(0);
        txtName.setText("");
        txtSpecies.setText("");
        txtBreed.setText("");
        txtAge.setText("");
        table.clearSelection();
    }
}