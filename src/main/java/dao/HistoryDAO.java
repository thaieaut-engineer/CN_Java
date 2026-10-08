package dao;

import config.DatabaseConnection;
import model.HistoryEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    public List<HistoryEntry> search(String term) throws SQLException {
        String normalizedTerm = term == null ? "" : term.trim();
        String sql = "SELECT mr.visit_date,p.name AS pet_name,p.species,c.full_name,c.phone,b.name AS branch_name,"
                + "e.full_name AS doctor,mr.diagnosis,mr.notes,mr.revisit_date,i.total_amount FROM MedicalRecord mr "
                + "JOIN Pet p ON p.pet_id=mr.pet_id JOIN Customer c ON c.customer_id=p.customer_id "
                + "JOIN Branch b ON b.branch_id=mr.branch_id JOIN Employee e ON e.employee_id=mr.employee_id "
                + "LEFT JOIN Invoice i ON i.record_id=mr.record_id "
                + (normalizedTerm.isEmpty() ? "" : "WHERE c.full_name LIKE ? OR c.phone LIKE ? OR p.name LIKE ? ")
                + "ORDER BY mr.visit_date DESC";
        List<HistoryEntry> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!normalizedTerm.isEmpty()) {
                String like = "%" + normalizedTerm + "%";
                statement.setNString(1, like);
                statement.setString(2, like);
                statement.setNString(3, like);
            }
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    rows.add(new HistoryEntry(results.getTimestamp("visit_date"), results.getString("pet_name"),
                            results.getString("species"), results.getString("full_name"),
                            results.getString("phone"), results.getString("branch_name"),
                            results.getString("doctor"), results.getString("diagnosis"),
                            results.getString("notes"), results.getTimestamp("revisit_date"),
                            results.getBigDecimal("total_amount")));
                }
            }
        }
        return rows;
    }
}
