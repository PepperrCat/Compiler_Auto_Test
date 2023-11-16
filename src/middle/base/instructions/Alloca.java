package middle.base.instructions;

import middle.base.Value;
import middle.base.values.Instruction;

public class Alloca extends Instruction {
    //<result> = alloca <type>

    public Alloca(Value result) {
        super("alloca");
        this.result = result;
        write.add(result);
    }

    @Override
    public String toString() {
        return String.format("%s = alloca %s", result.getName(), result.getTypeString());
    }


}
