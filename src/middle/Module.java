package middle;

import Nodes.Method.Print;
import middle.base.values.Function;
import middle.base.values.Instruction;
import middle.base.values.Var;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public List<Function> functions = new ArrayList<>();
    public List<Instruction> instructions = new ArrayList<>();
    public List<Var> globalVar = new ArrayList<>();
    public List<Instruction> declares = new ArrayList<>();

    public void print() {
        for (Instruction i : declares) {
            i.print();
        }
        for (Var v : globalVar) {
            if (!v.isConst) Print.printIR(String.format("%s = dso_local global %s", v.getName(), v.getInit()));
            else Print.printIR(String.format("%s = dso_local constant %s", v.getName(), v.getInit()));
        }
        for (Instruction i : instructions) {
            i.print();
        }
        for (Function f : functions) {
            f.print();
        }
    }


}
