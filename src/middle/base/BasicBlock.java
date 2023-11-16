package middle.base;

import Nodes.Method.Print;
import middle.base.instructions.Br;
import middle.base.values.Instruction;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock extends Value {
    public List<Instruction> instructions = null;
    public boolean isEnded = false;

    public void addBr(Br br) {
        if (!isEnded) instructions.add(br);
        isEnded=true;
    }

    public BasicBlock() {
        super(null);
        this.instructions = new ArrayList<>();
    }

    @Override
    public String toString() {
        String s = "";
        for (Instruction i :
                instructions) {
            s += i.toString();

        }
        return s;
    }

    public void print() {
        if (name != null) Print.printIR(name + ":");
        for (Instruction i :
                instructions) {
            i.print(1);
        }
    }
}
