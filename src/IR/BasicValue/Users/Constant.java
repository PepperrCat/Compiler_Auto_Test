package IR.BasicValue.Users;

import IR.BasicValue.User;
import IR.Types.Type;
import IR.Value;

public class Constant extends User {
    public Constant(Type valueType) {
        super(null, null, valueType);
    }
    public Constant(Type valueType, Value... values) {
        super(null, null, valueType);
        addValues(values);
    }
}
