package IR.BasicValue.Users;

import IR.BasicValue.User;
import IR.Types.Type;
import IR.Value;

public class GlobalValue extends User {
    public GlobalValue(String valueName, Value valueParent, Type valueType) {
        super(valueName, valueParent, valueType);
    }
}
