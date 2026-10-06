package view;

import config.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReminderPanel extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Loại nhắc", "Ngày đến hạn", "Thú cưng", "Khách hàng", "Điện thoại", "Chi nhánh", "Nội dung"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Đang tải lịch nhắc...");
    private final AtomicLong loadVersion = new AtomicLong();

    public ReminderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        UiTheme.stylePage(this);
        JLabel title = new JLabel("Lịch tiêm phòng & nhắc tái khám");
        title.setFont(title.getFont().deriveFont(18f).deriveFont(java.awt.Font.BOLD));
        title.setIcon(new ClinicIcon("reminder", UiTheme.BLUE, 22));
        title.setIconTextGap(10);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton reload = new JButton("Cập nhật danh sách");
        UiTheme.styleSecondary(reload);
        reload.setIcon(new ClinicIcon("history", UiTheme.BLUE, 15));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(reload);
        JLabel subtitle = new JLabel("Bao gồm lịch quá hạn 7 ngày và các lịch đến hạn trong 30 ngày tới.");
        subtitle.setForeground(UiTheme.MUTED);
        JPanel titleBlock = new JPanel(new BorderLayout(0, 4));
        titleBlock.setOpaque(false);
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);
        header.add(titleBlock, BorderLayout.CENTER);
        header.add(actions, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true);
        UiTheme.styleTable(table);
        status.setForeground(UiTheme.MUTED);
        status.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));
        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setOpaque(false);
        content.add(status, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        reload.addActionListener(event -> loadData());
        loadData();
    }

    private void loadData() {
        String sql = "SELECT reminder_type,due_date,pet_name,customer_name,phone,branch_name,details FROM ("
                + "SELECT N'Lịch hẹn' AS reminder_type,a.appointment_date AS due_date,p.name AS pet_name,"
                + "c.full_name AS customer_name,c.phone,b.name AS branch_name,COALESCE(a.notes,N'') AS details "
                + "FROM Appointment a JOIN Pet p ON p.pet_id=a.pet_id JOIN Customer c ON c.customer_id=a.customer_id "
                + "JOIN Branch b ON b.branch_id=a.branch_id WHERE a.status IN (N'Pending',N'Confirmed') "
                + "AND a.appointment_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE()) "
                + "UNION ALL SELECT N'Tái khám',mr.revisit_date,p.name,c.full_name,c.phone,b.name,COALESCE(mr.notes,N'') "
                + "FROM MedicalRecord mr JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id "
                + "WHERE mr.revisit_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE()) "
                + "UNION ALL SELECT N'Tiêm nhắc',v.next_due_date,p.name,c.full_name,c.phone,b.name,COALESCE(v.notes,N'') "
                + "FROM Vaccination v JOIN Pet p ON p.pet_id=v.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=v.branch_id "
                + "WHERE v.next_due_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE())"
                + ") reminders ORDER BY due_date";
        long version = loadVersion.incrementAndGet();
        status.setText("Đang tải lịch nhắc...");
        new javax.swing.SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws SQLException {
                List<Object[]> rows = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        rows.add(new Object[]{results.getString("reminder_type"), results.getTimestamp("due_date"),
                            results.getString("pet_name"), results.getString("customer_name"),
                            results.getString("phone"), results.getString("branch_name"),
                            results.getString("details")});
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
                    model.setRowCount(0);
                    rows.forEach(model::addRow);
                    status.setText(rows.size() + " lịch nhắc");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Đã hủy tải lịch nhắc.");
                    showError(e);
                } catch (ExecutionException e) {
                    status.setText("Không tải được lịch nhắc.");
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause instanceof Exception exception ? exception : new Exception(cause));
                }
            }
        }.execute();
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Không thể tải lịch nhắc:\n" + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
