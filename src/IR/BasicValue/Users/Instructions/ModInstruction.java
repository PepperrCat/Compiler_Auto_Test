package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.Types.IntType;
import IR.Types.Type;
import IR.Value;

public class ModInstruction extends BinaryInstruction{
    public ModInstruction(String name, BasicBlock parent, Value left, Value right) {
        super(name, parent, new IntType(32), left, right);
    }
    public String toString() {
        return getValueName() + " = srem " + getValueType() + " " + getValue(0).getValueName() + ", " + getValue(1).getValueName();
    }
}
