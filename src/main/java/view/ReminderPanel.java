package view;

import config.DatabaseConnection;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        add(new JScrollPane(table), BorderLayout.CENTER);
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
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            model.setRowCount(0);
            while (results.next()) {
                model.addRow(new Object[]{results.getString("reminder_type"), results.getTimestamp("due_date"),
                    results.getString("pet_name"), results.getString("customer_name"), results.getString("phone"),
                    results.getString("branch_name"), results.getString("details")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải lịch nhắc:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
