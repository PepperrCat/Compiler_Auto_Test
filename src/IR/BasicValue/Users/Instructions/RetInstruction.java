package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.IntType;
import IR.Types.Type;
import IR.Types.VoidType;
import IR.Value;

public class RetInstruction extends Instruction {
    public RetInstruction(BasicBlock valueParent) {
        super("", valueParent, new VoidType());
    }

    public RetInstruction(BasicBlock valueParent, Value... vs) {
        super("", valueParent, new IntType(32), vs);
    }

    @Override
    public String toString() {
        if (getValueType() instanceof VoidType) {
            return "ret void";
        } else {
            return "ret " + getValue(0).getValueType() + " " + getValue(0).getValueName();
        }
    }
}
