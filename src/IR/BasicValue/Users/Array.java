package IR.BasicValue.Users;

import IR.BasicValue.User;
import IR.BasicValue.Users.Constants.ConstantArray;
import IR.BasicValue.Users.Constants.ConstantInt;
import IR.Types.ArrayType;
import IR.Types.Type;
import IR.Value;

import java.util.ArrayList;

public class Array extends User {
    private final ArrayList<Value> elements = new ArrayList<>();

    public Array(ArrayList<Value> elements) {
        super(null, null, new ArrayType(elements.get(0).getValueType(), elements.size()));
        addValues(elements.toArray(new Value[0]));
        this.elements.addAll(elements);
    }

    public int getSize() {
        return ((ArrayType) getValueType()).getSize();
    }

    public ArrayList<Value> getElements() {
        return elements;
    }

    public ArrayList<Value> getElementList() {
        ArrayList<Value> elemList = new ArrayList<>();
        for (Value element : elements) {
            if (!(element instanceof Array)) {
                elemList.addAll(elements);
                return elemList;
            }
        }
        for (Value element : elements) {
            elemList.addAll(((Array) element).getElementList());
        }
        return elemList;
    }

    @Override
    public String getValueName() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i < ((ArrayType) getValueType()).getElementNum(); i++) {
            s.append(getValue(i).getValueType()).append(" ").append(getValue(i)).append(", ");
        }
        s.delete(s.length() - 2, s.length());
        s.append("]");
        return s.toString();
    }
}
