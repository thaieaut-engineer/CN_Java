package dao;

import config.DatabaseConnection;
import model.ReminderEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {
    public List<ReminderEntry> findDueReminders() throws SQLException {
        String sql = "SELECT reminder_type,due_date,pet_name,customer_name,phone,branch_name,details FROM ("
                + "SELECT N'Lịch hẹn' AS reminder_type,a.appointment_date AS due_date,p.name AS pet_name,"
                + "c.full_name AS customer_name,c.phone,b.name AS branch_name,COALESCE(a.notes,N'') AS details "
                + "FROM Appointment a JOIN Pet p ON p.pet_id=a.pet_id JOIN Customer c ON c.customer_id=a.customer_id "
                + "JOIN Branch b ON b.branch_id=a.branch_id WHERE a.status IN (N'Pending',N'Confirmed') "
                + "AND a.appointment_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE()) "
                + "UNION ALL SELECT N'Tái khám',mr.revisit_date,p.name,c.full_name,c.phone,b.name,COALESCE(mr.notes,N'') "
                + "FROM MedicalRecord mr JOIN Pet p ON p.pet_id=mr.pet_id "
                + "JOIN Customer c ON c.customer_id=p.customer_id JOIN Branch b ON b.branch_id=mr.branch_id "
                + "WHERE mr.revisit_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE()) "
                + "UNION ALL SELECT N'Tiêm nhắc',v.next_due_date,p.name,c.full_name,c.phone,b.name,COALESCE(v.notes,N'') "
                + "FROM Vaccination v JOIN Pet p ON p.pet_id=v.pet_id "
                + "JOIN Customer c ON c.customer_id=p.customer_id JOIN Branch b ON b.branch_id=v.branch_id "
                + "WHERE v.next_due_date BETWEEN DATEADD(day,-7,GETDATE()) AND DATEADD(day,30,GETDATE())"
                + ") reminders ORDER BY due_date";
        List<ReminderEntry> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                rows.add(new ReminderEntry(results.getString("reminder_type"), results.getTimestamp("due_date"),
                        results.getString("pet_name"), results.getString("customer_name"),
                        results.getString("phone"), results.getString("branch_name"),
                        results.getString("details")));
            }
        }
        return rows;
    }
}
