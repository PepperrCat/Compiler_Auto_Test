package IR.BasicValue;

import IR.Types.Type;
import IR.Use;
import IR.Value;

import java.util.ArrayList;
import java.util.LinkedList;

public class User extends Value {
    private final LinkedList<Use> operands = new LinkedList<>();

    public User(String valueName, Value valueParent, Type valueType) {
        super(valueName, valueParent, valueType);
    }

    public void addValue(Value v) {
        Use use = new Use(this, v);
        operands.add(use);
        if (v != null)
            v.addUse(use);
    }


    public void addValues(Value... vs) {
        for (Value v : vs) {
            addValue(v);
        }
    }

    public Value getValue(int index) {
        return operands.get(index).getValue();
    }

    public void setValue(int index, Value value) {
        operands.get(index).setValue(value);
    }

    public int getNumOperands() {
        return operands.size();
    }

    public LinkedList<Use> getOperands() {
        return operands;
    }

    public void clearOperands() {
        for (Use use : operands) {
            use.getValue().removeUser(this);
        }
        operands.clear();
    }

}
