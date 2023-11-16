package IR.BasicValue.Users.Instructions;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Instruction;
import IR.Types.Type;
import IR.Use;
import IR.Value;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class PhiInstruction extends Instruction {
    private int from;

    public PhiInstruction(String name, BasicBlock parent, Type type, int from, Value... vs) {
        super(name, parent, type, vs);
        this.from = from;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setEntry(Value value, BasicBlock block) {
        int index = 0;
        for (index = 0; index < from && getValue(index) != null; index++) ;
        if (index >= from) {
            System.out.println("phi entry exceed!");
            return;
        }
        setValue(index, value);
        setValue(index + from, block);
        value.addUse(this);
        block.addUse(this);
    }

    public void addEntry(Value value, BasicBlock block) {
//        int index = 0;
//        for (index = 0; index < from && getValue(index) != null; index++) ;
//        if (index < from) {
//            System.out.println("phi entry error!");
//            return;
//        }
        Use usev = new Use(this, value);
        Use useb = new Use(this, block);
        getOperands().add(from, usev);
        from++;
        getOperands().add(useb);
        value.addUse(usev);
        block.addUse(useb);
    }
    public ArrayList<Map.Entry<Value, BasicBlock>> getEntry() {
        ArrayList<Map.Entry<Value, BasicBlock>> entryPairs = new ArrayList<>();
        for (int i = 0; i < from; i++) {
            if (getValue(i) == null) break;
            entryPairs.add(new AbstractMap.SimpleEntry<>(getValue(i), (BasicBlock) getValue(i + from)));
        }
        return entryPairs;
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getValueName() + " = phi ").append(getValueType());
        for (int i = 0; i < from; i++) {
            if (getValue(i) == null) break;
            s.append(" [ ").append(getValue(i).getValueName()).append(", ")
                    .append(getValue(i + from).getValueName()).append(" ], ");
        }
        s.delete(s.length() - 2, s.length());
        return s.toString();
    }
}
