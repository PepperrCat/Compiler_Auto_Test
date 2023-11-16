package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.VoidType;
import IR.Value;

public class StoreInstruction extends Instruction {
    public StoreInstruction(BasicBlock parent, Value value, Value addr) {
        super("", parent, new VoidType(), value, addr);
    }

    public Value getValue() {
        return getValue(0);
    }

    public Value getAddr() {
        return getValue(1);
    }
    @Override
    public String toString()
    {
        return "store " + getValue().getValueType() + " " + getValue().getValueName() + ", " +
                getAddr().getValueType() + " " + getValue(1).getValueName();
    }
}
