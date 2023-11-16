package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.Types.IntType;
import IR.Types.Type;
import IR.Value;

public class MultInstruction extends BinaryInstruction{
    public MultInstruction(String name, BasicBlock parent, Value left, Value right) {
        super(name, parent, new IntType(32), left, right);
    }
    public String toString() {
        return getValueName() + " = mul " + getValueType() + " " + getValue(0).getValueName() + ", " + getValue(1).getValueName();
    }
}
