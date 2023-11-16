package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.Types.IntType;
import IR.Value;

public class CompareInstruction extends BinaryInstruction {
    private CompareType type;

    public CompareInstruction(String name, BasicBlock parent, CompareType type, Value left, Value right) {
        super(name, parent, new IntType(1), left, right);
        this.type = type;
    }

    public CompareType getCompareType() {
        return type;
    }

    @Override
    public String toString() {
        return getValueName() + " = icmp " + type.toString() + " " +
                getLeft().getValueType() + " " + getLeft().getValueName() + ", " + getRight().getValueName();
    }
}
