package IR.BasicValue.Users.GlobalValues;

import IR.BasicValue.User;
import IR.BasicValue.Users.Constant;
import IR.Module;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Value;

public class GlobalVar extends User {
    private boolean isConstant;

    public GlobalVar(String valueName, Constant initVal, boolean isConstant) {
        super(valueName, Module.module, new PointerType(initVal.getValueType()));
        addValue(initVal);
        this.isConstant = isConstant;
    }

    public Constant getInitVal() {
        return (Constant) getValue(0);
    }

    @Override
    public String toString() {
        return getValueName() + " = dso_local " + ((isConstant) ? "constant " : "global ") + ((PointerType) getValueType()).getType() + " " + getValue(0).getValueName() + '\n';
    }
}
