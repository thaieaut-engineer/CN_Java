package view;

import bus.EmployeeBUS;
import model.Branch;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class RegisterDialog extends JDialog {
    private final EmployeeBUS employeeBUS;
    private final JTextField fullName = new JTextField(22);
    private final JTextField username = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);
    private final JPasswordField confirmPassword = new JPasswordField(22);
    private final JComboBox<Branch> branch = new JComboBox<>();
    private final JButton register = new JButton("Gửi yêu cầu đăng ký");
    private final JButton cancel = new JButton("Hủy");
    private final JLabel status = new JLabel("Đang tải danh sách chi nhánh...");

    public RegisterDialog(LoginFrame owner, EmployeeBUS employeeBUS) {
        super(owner, "Đăng ký tài khoản nhân viên", ModalityType.APPLICATION_MODAL);
        this.employeeBUS = employeeBUS;
        buildForm();
        loadBranches();
    }

    private void buildForm() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JLabel title = new JLabel("Đăng ký tài khoản nhân viên");
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        title.setForeground(UiTheme.BLUE_DARK);
        JLabel subtitle = new JLabel("Tài khoản sẽ chờ quản trị viên phê duyệt trước khi đăng nhập.");
        subtitle.setForeground(UiTheme.MUTED);
        JPanel heading = new JPanel(new BorderLayout(0, 6));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        UiTheme.styleSurface(form);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(7, 8, 7, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.35;
        addField(form, constraints, 0, "Họ và tên:", fullName);
        addField(form, constraints, 1, "Chi nhánh:", branch);
        addField(form, constraints, 2, "Tên đăng nhập:", username);
        addField(form, constraints, 3, "Mật khẩu:", password);
        addField(form, constraints, 4, "Nhập lại mật khẩu:", confirmPassword);
        UiTheme.styleTextField(fullName);
        UiTheme.styleTextField(username);
        UiTheme.styleTextField(password);
        UiTheme.styleTextField(confirmPassword);
        branch.setPreferredSize(new Dimension(250, 38));
        branch.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(form, BorderLayout.CENTER);
        status.setForeground(UiTheme.MUTED);
        center.add(status, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        UiTheme.stylePrimary(register);
        UiTheme.styleSecondary(cancel);
        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(register);
        add(actions, BorderLayout.SOUTH);
        register.addActionListener(event -> submit());
        cancel.addActionListener(event -> dispose());
        getRootPane().setDefaultButton(register);
        setMinimumSize(new Dimension(560, 390));
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void addField(JPanel form, GridBagConstraints constraints, int row,
            String label, java.awt.Component input) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0.35;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(fieldLabel.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        form.add(fieldLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 0.65;
        form.add(input, constraints);
    }

    private void loadBranches() {
        register.setEnabled(false);
        new SwingWorker<List<Branch>, Void>() {
            @Override
            protected List<Branch> doInBackground() throws Exception {
                return employeeBUS.getBranches();
            }

            @Override
            protected void done() {
                try {
                    List<Branch> branches = get();
                    branch.removeAllItems();
                    branches.forEach(branch::addItem);
                    if (branches.isEmpty()) {
                        status.setText("Chưa có chi nhánh để đăng ký. Vui lòng liên hệ quản trị viên.");
                    } else {
                        status.setText("Vai trò mặc định: Nhân viên · Trạng thái: Chờ duyệt");
                        register.setEnabled(true);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Đã hủy tải chi nhánh.");
                    showError(e);
                } catch (ExecutionException e) {
                    status.setText("Không tải được danh sách chi nhánh.");
                    showError(e.getCause() instanceof Exception cause ? cause : e);
                }
            }
        }.execute();
    }

    private void submit() {
        String name = fullName.getText().trim();
        String account = username.getText().trim();
        char[] passwordChars = password.getPassword();
        char[] confirmationChars = confirmPassword.getPassword();
        String plainPassword = new String(passwordChars);
        String confirmation = new String(confirmationChars);
        Branch selectedBranch = (Branch) branch.getSelectedItem();

        if (name.isBlank() || name.length() > 100) {
            showValidationError("Họ và tên không được để trống hoặc dài quá 100 ký tự.");
            return;
        }
        if (!account.matches("[A-Za-z0-9._-]{4,50}")) {
            showValidationError("Tên đăng nhập phải dài 4–50 ký tự, chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.");
            return;
        }
        if (plainPassword.length() < 6) {
            showValidationError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        if (!plainPassword.equals(confirmation)) {
            showValidationError("Mật khẩu xác nhận không khớp.");
            return;
        }
        if (selectedBranch == null) {
            showValidationError("Vui lòng chọn chi nhánh.");
            return;
        }

        setFormEnabled(false);
        status.setText("Đang gửi yêu cầu đăng ký...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                employeeBUS.register(name, account, plainPassword, selectedBranch.getBranchId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Đã gửi yêu cầu đăng ký. Bạn có thể đăng nhập sau khi quản trị viên duyệt tài khoản.",
                            "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    setFormEnabled(true);
                    showError(e);
                } catch (ExecutionException e) {
                    setFormEnabled(true);
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause instanceof Exception exception ? exception : new Exception(cause));
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        fullName.setEnabled(enabled);
        branch.setEnabled(enabled);
        username.setEnabled(enabled);
        password.setEnabled(enabled);
        confirmPassword.setEnabled(enabled);
        register.setEnabled(enabled);
        cancel.setEnabled(enabled);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, "Không thể đăng ký tài khoản:\n" + exception.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
