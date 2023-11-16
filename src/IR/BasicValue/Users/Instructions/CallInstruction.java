package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.Instruction;
import IR.Types.Type;
import IR.Value;

import java.util.ArrayList;

public class CallInstruction extends Instruction {

    public CallInstruction(String name, BasicBlock parent, Function function, ArrayList<Value> args) {
        super(name, parent, function.getReturnType(), new ArrayList<Value>() {{
            add(function);
            addAll(args);
        }}.toArray(new Value[0]));

    }

    public Function getFunction() {
        return (Function) getValue(0);
    }

    public ArrayList<Value> getArgs() {
        ArrayList<Value> args = new ArrayList<>();
        for (int i = 1; i < getNumOperands(); i++) {
            args.add(getValue(i));
        }
        return args;
    }

    @Override
    public String toString() {
        Function function = getFunction();
        StringBuilder s = new StringBuilder(getValueName()).append(getValueName() != null && !getValueName().equals("") ? " = call " : "call ").append(function.getReturnType()).append(' ')
                .append(function.getValueName()).append('(');
        for (int i = 1; i <= function.getArgsSize(); i++) {
            s.append(getValue(i).getValueType()).append(' ').append(getValue(i).getValueName()).append(", ");
        }
        if (function.getArgsSize() > 0) {
            s.delete(s.length() - 2, s.length());
        }
        s.append(')');
        return s.toString();
    }
}
