package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrudRow {
    private List<Object> values = new ArrayList<>();

    public CrudRow() {
    }

    public CrudRow(List<Object> values) {
        setValues(values);
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
    }
}
