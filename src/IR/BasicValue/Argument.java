package IR.BasicValue;

import IR.Types.Type;
import IR.Value;

public class Argument extends Value {
    public Argument(String valueName, Value valueParent, Type valueType) {
        super(valueName, valueParent, valueType);
    }
}
