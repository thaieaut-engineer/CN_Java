package model;

public class LookupOption {
    private Object value;
    private String label;

    public LookupOption() {
    }

    public LookupOption(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @Override
    public String toString() {
        return label;
    }
}
