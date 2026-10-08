package dao;

import config.DatabaseConnection;
import model.ModuleDefinition;
import model.ModuleDefinition.Column;
import model.ModuleDefinition.Field;
import model.LookupOption;
import model.CrudRow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CrudDAO {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    public List<CrudRow> findAll(ModuleDefinition definition) throws SQLException {
        String table = identifier(definition.getTableName());
        String idColumn = identifier(definition.getIdColumn());
        String columns = definition.getColumns().stream()
                .map(column -> "data_row." + identifier(column.getName()))
                .reduce((left, right) -> left + ", " + right).orElseThrow();
        String sql = "SELECT " + columns + " FROM dbo." + table + " AS data_row"
                + " ORDER BY data_row." + idColumn + " DESC";
        List<CrudRow> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                List<Object> values = new ArrayList<>(definition.getColumns().size());
                for (Column column : definition.getColumns()) {
                    values.add(results.getObject(column.getName()));
                }
                rows.add(new CrudRow(values));
            }
        }
        return rows;
    }

    public List<LookupOption> findOptions(ModuleDefinition definition, Field field) throws SQLException {
        LookupSpec lookup = LookupSpec.forField(field.getName());
        if (lookup == null) {
            return List.of();
        }
        String source = "dbo." + lookup.table + " AS lookup_row";
        String label = "lookup_row." + lookup.labelColumn;
        if ("record_id".equals(field.getName())) {
            source += " LEFT JOIN dbo.Pet AS related_pet ON related_pet.pet_id = lookup_row.pet_id";
            label = "CONCAT(N'Phiếu khám #', lookup_row.record_id, N' - ', "
                    + "COALESCE(related_pet.name, N'Chưa rõ thú cưng'))";
        }
        String sql = "SELECT lookup_row." + lookup.idColumn + " AS lookup_id, "
                + label + " AS lookup_label FROM " + source
                + ("MedicalDetail".equals(definition.getTableName()) && "record_id".equals(field.getName())
                        ? " WHERE lookup_row.record_status = N'InProgress' "
                        : " ")
                + " ORDER BY lookup_row." + lookup.idColumn + " DESC";
        List<LookupOption> options = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                options.add(new LookupOption(results.getObject("lookup_id"),
                        results.getString("lookup_label")));
            }
        }
        return options;
    }

    public int completeMedicalRecord(Object recordId) throws SQLException {
        String sql = "UPDATE dbo.MedicalRecord SET record_status = N'Completed' "
                + "WHERE record_id = ? AND record_status = N'InProgress'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, recordId);
            return statement.executeUpdate();
        }
    }

    public void insert(ModuleDefinition definition, Map<Field, Object> values, byte[] photo,
            boolean photoColumnAvailable) throws SQLException {
        List<String> columns = new ArrayList<>();
        values.keySet().forEach(field -> columns.add(identifier(field.getName())));
        if (photoColumnAvailable) {
            columns.add("photo");
        }
        boolean medicalDetail = "MedicalDetail".equals(definition.getTableName());
        String sql = "INSERT INTO dbo." + identifier(definition.getTableName())
                + " (" + String.join(", ", columns) + ") "
                + (medicalDetail ? "SELECT " : "VALUES (")
                + String.join(", ", java.util.Collections.nCopies(columns.size(), "?"))
                + (medicalDetail ? " WHERE EXISTS (SELECT 1 FROM dbo.MedicalRecord "
                        + "WHERE record_id = ? AND record_status = N'InProgress')" : ")");
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            if (photoColumnAvailable) {
                statement.setBytes(values.size() + 1, photo);
            }
            if (medicalDetail) {
                Object recordId = values.entrySet().stream()
                        .filter(entry -> "record_id".equals(entry.getKey().getName()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElseThrow(() -> new SQLException("Hãy chọn phiếu khám đang thực hiện."));
                statement.setObject(values.size() + 1, recordId);
                if (statement.executeUpdate() == 0) {
                    throw new SQLException("Không thể thêm chi tiết: phiếu khám đã hoàn tất.");
                }
            } else {
                statement.executeUpdate();
            }
        }
    }

    public void update(ModuleDefinition definition, Object id, Map<Field, Object> values,
            byte[] photo, boolean photoChanged) throws SQLException {
        List<String> assignments = new ArrayList<>();
        values.keySet().forEach(field -> assignments.add(identifier(field.getName()) + " = ?"));
        if (photoChanged) {
            assignments.add("photo = ?");
        }
        if (assignments.isEmpty()) {
            return;
        }
        boolean medicalDetail = "MedicalDetail".equals(definition.getTableName());
        Object targetRecordId = null;
        if (medicalDetail) {
            targetRecordId = values.entrySet().stream()
                    .filter(entry -> "record_id".equals(entry.getKey().getName()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow(() -> new SQLException("Hãy chọn phiếu khám đang thực hiện."));
        }
        String sql = medicalDetail
                ? "UPDATE detail_row SET " + String.join(", ", assignments)
                        + " FROM dbo.MedicalDetail AS detail_row WHERE detail_row.detail_id = ? "
                        + "AND EXISTS (SELECT 1 FROM dbo.MedicalRecord AS record_row "
                        + "WHERE record_row.record_id = detail_row.record_id "
                        + "AND record_row.record_status = N'InProgress') "
                        + "AND EXISTS (SELECT 1 FROM dbo.MedicalRecord AS target_record "
                        + "WHERE target_record.record_id = ? "
                        + "AND target_record.record_status = N'InProgress')"
                : "UPDATE dbo." + identifier(definition.getTableName()) + " SET "
                        + String.join(", ", assignments) + " WHERE "
                        + identifier(definition.getIdColumn()) + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, values);
            int nextIndex = values.size() + 1;
            if (photoChanged) {
                statement.setBytes(nextIndex++, photo);
            }
            statement.setObject(nextIndex, id);
            if (medicalDetail) {
                statement.setObject(nextIndex + 1, targetRecordId);
            }
            int changed = statement.executeUpdate();
            if (medicalDetail && changed == 0) {
                throw new SQLException("Không thể sửa chi tiết: phiếu khám đã hoàn tất hoặc chi tiết không còn tồn tại.");
            }
        }
    }

    public int delete(ModuleDefinition definition, Object id) throws SQLException {
        boolean medicalDetail = "MedicalDetail".equals(definition.getTableName());
        String sql = medicalDetail
                ? "DELETE detail_row FROM dbo.MedicalDetail AS detail_row WHERE detail_row.detail_id = ? "
                        + "AND EXISTS (SELECT 1 FROM dbo.MedicalRecord AS record_row "
                        + "WHERE record_row.record_id = detail_row.record_id "
                        + "AND record_row.record_status = N'InProgress')"
                : "DELETE FROM dbo." + identifier(definition.getTableName())
                        + " WHERE " + identifier(definition.getIdColumn()) + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            int changed = statement.executeUpdate();
            if (medicalDetail && changed == 0) {
                throw new SQLException("Không thể xóa chi tiết của phiếu khám đã hoàn tất.");
            }
            return changed;
        }
    }

    public boolean hasPhotoColumn(ModuleDefinition definition) throws SQLException {
        String sql = "SELECT data_row.photo FROM dbo." + identifier(definition.getTableName())
                + " AS data_row WHERE 1 = 0";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet ignored = statement.executeQuery()) {
            return true;
        }
    }

    public byte[] findPhoto(ModuleDefinition definition, Object id) throws SQLException {
        String sql = "SELECT data_row.photo FROM dbo." + identifier(definition.getTableName())
                + " AS data_row WHERE data_row." + identifier(definition.getIdColumn()) + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBytes("photo") : null;
            }
        }
    }

    private static void bind(PreparedStatement statement, Map<Field, Object> values) throws SQLException {
        int index = 1;
        for (Object value : values.values()) {
            statement.setObject(index++, value);
        }
    }

    private static String identifier(String value) {
        if (!IDENTIFIER.matcher(value).matches()) {
            throw new IllegalArgumentException("Tên bảng hoặc cột không hợp lệ: " + value);
        }
        return value;
    }

    private record LookupSpec(String table, String idColumn, String labelColumn) {
        private static LookupSpec forField(String fieldName) {
            return switch (fieldName) {
                case "branch_id" -> new LookupSpec("Branch", "branch_id", "name");
                case "customer_id" -> new LookupSpec("Customer", "customer_id", "full_name");
                case "pet_id" -> new LookupSpec("Pet", "pet_id", "name");
                case "employee_id" -> new LookupSpec("Employee", "employee_id", "full_name");
                case "service_id" -> new LookupSpec("Service", "service_id", "name");
                case "record_id" -> new LookupSpec("MedicalRecord", "record_id", "record_id");
                default -> null;
            };
        }
    }
}
