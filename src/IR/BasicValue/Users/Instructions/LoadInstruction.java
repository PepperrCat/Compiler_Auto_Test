package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Value;

public class LoadInstruction extends Instruction {
    private Type dataType;

    public LoadInstruction(String name, BasicBlock parent, Value addr) {
        super(name, parent, ((PointerType) addr.getValueType()).getType(), addr);
        this.dataType = ((PointerType) addr.getValueType()).getType();
    }
    public Value getAddr()
    {
        return getValue(0);
    }

    public Type getDataType()
    {
        return dataType;
    }
    @Override
    public String toString()
    {
        return getValueName() + " = load " + getValueType() + ", " + getValue(0).getValueType() + " " + getValue(0).getValueName();
    }
}
