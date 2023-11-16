package middle.base.instructions;

import middle.base.Value;
import middle.base.values.Instruction;

public class Store extends Instruction {
    //store <ty> <value>, <ty>* <pointer> (value2)

    public Store(Value value1, Value value2) {
        super("store");
        this.value1 = value1;
        this.value2 = value2;
        write.add(value2);
        read.add(value1);
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s* %s", value2.getTypeString(), value2.getName(), value2.getTypeString(), value1.getName());
    }

}
