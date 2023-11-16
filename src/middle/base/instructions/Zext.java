package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Instruction;

public class Zext extends Instruction {
    //<result> = zext <ty> <value> to <ty2>
    public Value result;
    public Value value;
    ValueType type;

    public Zext(Value result, Value value, ValueType type) {
        super("zext");
        this.result = result;
        this.value = value;
        this.type = type;
        write.add(result);
        read.add(value);
    }

    @Override
    public String toString() {
        //<result> = zext <ty> <value> to <ty2>
        return String.format("%s = zext %s %s to %s", result.getName(), value.getTypeString(), value.getName(), type);
    }
}
