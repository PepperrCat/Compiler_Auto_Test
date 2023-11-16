package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Instruction;

public class Sdiv extends Instruction {
    //<result> = sdiv <ty> <op1>, <op2>
    ValueType ty;

    public Sdiv(Value result, Value value1, Value value2, ValueType ty) {
        super("sdiv");
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
        return String.format("%s = sdiv %s %s, %s",result.getName(), ty, value1.getName(), value2.getName());
    }
}
