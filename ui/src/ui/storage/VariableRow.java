package ui.storage;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableRow {
    private final StringProperty variable;
    private final IntegerProperty _value;

    public VariableRow(String left, Integer right) {
        this.variable = new SimpleStringProperty(left);
        this._value = new SimpleIntegerProperty(right);
    }

    public StringProperty variableProperty() { return variable; }
    public IntegerProperty rightProperty() { return _value; }

    public String getVariable() { return variable.get(); }
    public void setVariable(String value) { variable.set(value); }

    public int getValue() { return _value.get(); }
    public void setValue(int value) { _value.set(value); }
}
