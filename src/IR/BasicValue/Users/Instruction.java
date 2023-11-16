package IR.BasicValue.Users;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.User;
import IR.Types.Type;
import IR.Value;

public class Instruction extends User {
    public Instruction(String name, BasicBlock parent, Type type, Value... vs) {
        super(name, parent, type);
        addValues(vs);
        if (name != null && !name.equals("")) {
            parent.getValueParent().addFunctionSymbol(this);
        }
    }

}
