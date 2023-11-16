package middle.base.instructions;

import middle.base.Value;
import middle.base.values.Instruction;
import middle.base.values.Var;

public class Load extends Instruction {
    //<result> = load <ty>, <ty>* <pointer>

    public Value pointer;

    public Load(Var result, Value pointer) {
        super("load");
        this.result = result;
        this.pointer = pointer;
        this.type = result.type;

        write.add(result);
        read.add(pointer);

    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s* %s", result.getName(), pointer.getTypeString(), pointer.getTypeString(), pointer.getName());
    }
}
