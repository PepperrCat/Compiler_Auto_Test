package middle.base.instructions;

import middle.base.Value;
import middle.base.values.Function;
import middle.base.values.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Call extends Instruction {
    //<result> = call [ret attrs] <ty> <fnptrval>(<function args>)
    public Value result = null;
    public List<Value> params = new ArrayList<>();
    public Function function;

    public String getParamsString() {
        String s = "";
        boolean f = true;
        for (Value v :
                params) {
            if (f) {

                f = false;
            } else {
                s += ", ";
            }
            s += String.format("%s %s", v.getTypeString(), v.getName());
        }
        return s;
    }

    public Call(Function function) {
        super("call");
        this.function = function;
    }

    public Call(Value result, Function function) {
        super("call");
        this.result = result;
        this.function = function;
        write.add(result);
    }

    public Call(Value result, Function function, List<Value> params) {
        super("call");
        this.result = result;
        this.function = function;
        this.params = params;
        write.add(result);
        read.addAll(params);
    }

    public void addPara(Value value) {
        params.add(value);
    }

    @Override
    public String toString() {
        if (result == null)
            return String.format("call %s %s(%s)", function.getFuncType(), function.getName(), getParamsString());
        else
            return String.format("%s = call %s %s(%s)", result.getName(), function.getFuncType(), function.getName(), getParamsString());
    }
}
