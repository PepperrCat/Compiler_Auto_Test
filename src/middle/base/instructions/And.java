package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Instruction;

public class And extends Instruction {
    ValueType ty;

    //<result> = and <ty> <op1>, <op2>
    public And(Value result, Value value1, Value value2, ValueType ty) {
        super("and");
        this.result = result;
        this.value1 = value1;
        this.value2 = value2;
        this.ty = ty;
        write.add(result);
        read.add(value1);
        read.add(value2);
    }

    @Override
    public String toString() {
        return String.format("%s = and %s %s, %s", result.getName(), ty, value1.getName(), value2.getName());
    }
}
