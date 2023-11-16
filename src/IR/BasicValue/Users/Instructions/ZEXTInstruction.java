package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.IntType;
import IR.Value;

public class ZEXTInstruction extends Instruction {
    public ZEXTInstruction(String name, BasicBlock parent, Value changee)
    {
        super(name, parent, new IntType(32), changee);
    }
    public Value getChangee() {
        return getValue(0);
    }


    public String toString()
    {
        return getValueName() + " = zext i1 " + getChangee().getValueName() + " to i32";
    }
}
