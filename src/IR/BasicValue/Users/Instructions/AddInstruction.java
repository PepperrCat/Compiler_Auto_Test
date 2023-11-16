package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.IntType;
import IR.Types.Type;
import IR.Value;

public class AddInstruction extends BinaryInstruction {

    public AddInstruction(String name, BasicBlock parent, Value left, Value right) {
        super(name, parent, new IntType(32), left, right);
    }

    @Override
    public String toString() {
        return getValueName() + " = add " + getValueType() + " " + getValue(0).getValueName() + ", " + getValue(1).getValueName();
    }
}
