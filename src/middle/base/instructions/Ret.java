package middle.base.instructions;

import middle.base.Value;
import middle.base.ValueType;
import middle.base.values.Const;
import middle.base.values.Instruction;

public class Ret extends Instruction {
    ValueType type;
   public Value value;

    public Ret(ValueType type, Value value) {
        super("ret");
        this.type = type;
        this.value = value;
        read.add(value);
    }

    @Override
    public String toString() {
        if (value == null) return "ret void";
        if (value instanceof Const c) {
            return "ret i32 " + Integer.toString(c.value);
        }
        return "ret i32 " + value.getName();
    }


}
