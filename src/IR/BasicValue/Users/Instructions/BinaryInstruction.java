package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.Type;
import IR.Value;

public class BinaryInstruction extends Instruction {
    public BinaryInstruction(String name, BasicBlock parent, Type type, Value left, Value right) {
        super(name, parent, type, left, right);
    }
    public Value getLeft() {
        return getValue(0);
    }
    public Value getRight() {
        return getValue(1);
    }
}
