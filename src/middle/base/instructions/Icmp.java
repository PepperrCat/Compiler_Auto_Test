package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Instruction;
import middle.base.values.OpValue;

public class Icmp extends Instruction {
    //<result> = icmp <cond> <ty> <op1>, <op2>
    ValueType type;
   public OpValue cond;



    public Icmp(Value res, ValueType type, OpValue cond, Value op1, Value op2) {
        super("icmp");
        this.result=res;
        this.type = type;
        this.cond = cond;
        this.value1 = op1;
        this.value2 = op2;
        write.add(res);
        read.add(op1);
        read.add(op2);
        read.add(cond);

    }

    @Override
    public String toString() {
        //<result> = icmp <cond> <ty> <op1>, <op2>
        return String.format("%s = icmp %s %s %s, %s", result.getName(), cond.op, type, value1.getName(), value2.getName());

    }
}
