package middle.base.values;

import Nodes.Method.Print;
import middle.base.Value;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Instruction extends Value implements Comparator<Instruction> {
    public static int globalId = 0;
    public Value result, value1, value2, head, op;
    public Set<Value> read = new HashSet<>();
    public Set<Value> write = new HashSet<>();
    public int id;


    public Instruction(String name) {
        super(name);
        id = globalId++;
    }

    public void print() {
        Print.printIR(toString());
    }

    public void print(int i) {
        StringBuilder s = new StringBuilder();
        while (i > 0) {
            s.append("\t");
            i--;
        }
        Print.printIR(s + toString());
    }

    @Override
    public int compare(Instruction i1, Instruction i2) {
        if (i2.write.isEmpty()) {
            for (Value v : i2.read) {
                if (i1.write.contains(v)) {
                    return i2.id - i1.id;
                }
            }
        } else {
            Value r2 = (Value) i2.write.toArray()[0];
            //写后写
            if (i1.write.contains(r2)) {
                return i2.id - i1.id;
            }
            //写后读

            if (i1.read.contains(r2)) {
                return i2.id - i1.id;
            }


        }
        return i2.id - i1.id;
    }
}
