package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.Type;
import IR.Types.VoidType;
import IR.Value;

public class BrInstruction extends Instruction {

    public BrInstruction(BasicBlock parent, BasicBlock target) {
        super("", parent, new VoidType(), target);
    }

    public BrInstruction(BasicBlock parent, Value condition, BasicBlock trueBBlock, BasicBlock falseBBlock) {
        super("", parent, new VoidType(), condition, trueBBlock, falseBBlock);
    }

    public boolean hasCondition() {
        return getNumOperands() == 3;
    }

    public Value getCondition() {
        return getValue(0);
    }

    public Value getTrueBBlock() {
        if (hasCondition())
            return getValue(1);
        else
            return getCondition();
    }

    public Value getFalseBBlock() {
        return getValue(2);
    }

    public String toString() {
        if (hasCondition()) {
            return "br " + getCondition().getValueType() + " " + getCondition().getValueName() + ", " +
                    getTrueBBlock().getValueType() + " " + getTrueBBlock().getValueName() + ", " +
                    getFalseBBlock().getValueType() + " " + getFalseBBlock().getValueName();
        }
        return "br " + getCondition().getValueType() + " " + getTrueBBlock().getValueName();
    }
}
