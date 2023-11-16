package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Value;


/**
 * <result> = getelementptr <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
 * <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
 * <p>
 * 简化为
 * getelementptr baseType baseType* base, first, second;     返回值: baseType.getElementType*
 * getelementptr baseType baseType* base, first;             返回值: baseType*
 */
public class GEPInstruction extends Instruction {
    private Type baseType;

    public GEPInstruction(String name, BasicBlock parent, Value base, Value first) {
        super(name, parent, (PointerType) base.getValueType(), base, first);
        this.baseType = ((PointerType) base.getValueType()).getType();
    }

    public GEPInstruction(String name, BasicBlock parent, Value base, Value first, Value second) {
        // 这个地方即baseType.getElementType*，指的是baseType的数组元素类型，即[6 x i8]中的i8*而上面的只有first返回类型是 [6 x i8]*类型
        super(name, parent, new PointerType(((ArrayType) ((PointerType) base.getValueType()).getType()).getElementType()), base, first, second);
        this.baseType = ((PointerType) base.getValueType()).getType();
    }

    public Value getBase() {
        return getValue(0);
    }

    public Type getBaseType() {
        return baseType;
    }

    public Value getFirstOff() {
        return getValue(1);
    }

    public Value getSecondOff() {
        return getNumOperands() == 3 ? getValue(2) : null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getValueName() + " = getelementptr inbounds " + baseType + ", ");
        for (int i = 0; i < getNumOperands(); i++) {
            s.append(getValue(i).getValueType()).append(" ").append(getValue(i).getValueName()).append(", ");
        }
        s.delete(s.length() - 2, s.length());
        return s.toString();
    }
}
