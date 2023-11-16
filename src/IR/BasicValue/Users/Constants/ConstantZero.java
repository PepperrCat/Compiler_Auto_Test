package IR.BasicValue.Users.Constants;

import IR.BasicValue.Users.Constant;
import IR.Types.ArrayType;
import IR.Types.Type;

import java.util.ArrayList;


public class ConstantZero extends ConstantArray {

    public ConstantZero(Type arrayType) {
        super(arrayType);
        for (int i = 0; i < ((ArrayType) arrayType).getElementNum(); i++) {
            addElement(ConstantInt.ZERO);
        }
    }
    public int getArraySize() {
        return ((ArrayType) getValueType()).getSize();
    }
    @Override
    public String getValueName() {
        return toString();
    }

    @Override
    public String toString() {
        return "zeroinitializer";
    }
}
