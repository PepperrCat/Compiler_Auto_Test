package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Instruction;

public class Or extends Instruction {
    ValueType ty;

    //<result> = and <ty> <op1>, <op2>
    public Or(Value result, Value value1, Value value2, ValueType ty) {
        super("or");
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
        return String.format("%s = or %s %s, %s", result.getName(), ty, value1.getName(), value2.getName());
    }
}
