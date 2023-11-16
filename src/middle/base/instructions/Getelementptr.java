package middle.base.instructions;

import middle.base.Value;
import middle.base.values.Instruction;
import middle.base.values.Var;

public class Getelementptr extends Instruction {
   public Value array;
    public Value index;
    public boolean getOut;

    public Getelementptr(Var res, Var Array, Value index) {
        super("getelementptr");
        this.array = Array;
        this.result = res;
        this.index = index;
        write.add(res);
        read.add(Array);
        read.add(index);
    }

    public Getelementptr(Value res, Value Array) {
        super("getelementptr");
        this.array = Array;
        this.result = res;
        this.getOut = true;

        write.add(res);
        read.add(Array);
    }

    @Override
    public String toString() {
        if (getOut) {
            return String.format("%s = getelementptr %s, %s* %s,i32 0",
                    result.getName(), array.getTypeString(), array.getTypeString(), array.getName());

        }
        if (!array.isPtr)
            return String.format("%s = getelementptr %s, %s* %s,i32 0, i32 %s",
                    result.getName(), array.getTypeString(), array.getTypeString(), array.getName(), index.getName());
        return String.format("%s = getelementptr %s, %s* %s, i32 %s",
                result.getName(), array.type, array.type, array.getName(), index.getName());
    }
}
