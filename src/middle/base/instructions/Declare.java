package middle.base.instructions;

import middle.base.values.FuncParam;
import middle.base.values.Function;
import middle.base.values.Instruction;

public class Declare extends Instruction {
    Function function;

    //declare Functype func(type)
    public Declare(Function function) {
        super("declare");
        this.function = function;


    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(String.format("declare %s %s(", function.getFuncType(), function.getName()));
        boolean f = true;
        for (FuncParam param : function.funcParams) {
            if (f) {
                f = false;
            } else {
                s.append(", ");
            }
            s.append(String.format("%s", param.type));
        }
        s.append(")");
        return s.toString();
    }
}
