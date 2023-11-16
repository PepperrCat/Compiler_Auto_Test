package IR.BasicValue.Users.Constants;

import IR.BasicValue.Users.Constant;
import IR.Types.ArrayType;
import IR.Types.Type;
import IR.Value;

import java.util.ArrayList;

public class ConstantArray extends Constant {
    private final ArrayList<Constant> elements = new ArrayList<>();

    public ConstantArray(ArrayList<Constant> elements) {
        super(new ArrayType(elements.get(0).getValueType(), elements.size()), elements.toArray(new Constant[0]));
        this.elements.addAll(elements); // 不好直接赋值，可能导致修改或者访存的时候有问题，虽然常数数组不能修改）））））
    }

    public ConstantArray(Type valueType) {
        super(valueType);
    }

    public void addElement(Constant e) {
        elements.add(e);
    }
    public int getSize() {
        return ((ArrayType) getValueType()).getSize();
    }

    public ArrayList<Constant> getElements() {
        return elements;
    }

    public ArrayList<Constant> getElementList() {
        ArrayList<Constant> elemList = new ArrayList<>();
        if (elements.get(0) instanceof ConstantInt) {
            elemList.addAll(elements);
        } else {
            for (Constant element : elements) {
                elemList.addAll(((ConstantArray) element).getElementList());
            }
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

    public Constant getElement(int index) {
        return elements.get(index);
    }
}
