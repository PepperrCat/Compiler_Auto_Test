package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.User;
import IR.BasicValue.Users.Constants.ConstantArray;
import IR.BasicValue.Users.Instruction;
import IR.Types.PointerType;
import IR.Types.Type;

public class AllocInstruction extends Instruction {

    private ConstantArray initialVal;  // 用于单独记录常数数组的初值，又不能改变，后面也许可一当常数优化

    public AllocInstruction(String name, BasicBlock parent, Type allocatedType) {
        super(name, parent, new PointerType(allocatedType));
    }

    public AllocInstruction(String name, BasicBlock parent, Type allocatedType, ConstantArray initialVal) {
        super(name, parent, new PointerType(allocatedType));
        this.initialVal = initialVal;
    }

    public ConstantArray getInitialVal() {
        return initialVal;
    }

    public Type getAllocatedType() {
        return ((PointerType) getValueType()).getType();
    }

    public boolean toReg() {
        for (User user : getUsers()) {
            if (user instanceof GEPInstruction) {
                if (user.getValue(0).equals(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getValueName() + " = alloca " + getAllocatedType();
    }
}
