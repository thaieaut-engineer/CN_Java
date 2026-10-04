package model;

import java.util.List;

public final class ModuleDefinition {
    public enum ValueType {
        TEXT, INTEGER, DECIMAL, DATETIME, PASSWORD
    }

    public static final class Field {
        private final String name;
        private final String label;
        private final ValueType type;
        private final boolean required;

        public Field(String name, String label, ValueType type, boolean required) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.required = required;
        }

        public String getName() { return name; }
        public String getLabel() { return label; }
        public ValueType getType() { return type; }
        public boolean isRequired() { return required; }
    }

    public static final class Column {
        private final String name;
        private final String label;

        public Column(String name, String label) {
            this.name = name;
            this.label = label;
        }

        public String getName() { return name; }
        public String getLabel() { return label; }
    }

    private final String title;
    private final String tableName;
    private final String idColumn;
    private final List<Field> fields;
    private final List<Column> columns;

    public ModuleDefinition(String title, String tableName, String idColumn,
            List<Field> fields, List<Column> columns) {
        this.title = title;
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.fields = List.copyOf(fields);
        this.columns = List.copyOf(columns);
    }

    public String getTitle() { return title; }
    public String getTableName() { return tableName; }
    public String getIdColumn() { return idColumn; }
    public List<Field> getFields() { return fields; }
    public List<Column> getColumns() { return columns; }
}
