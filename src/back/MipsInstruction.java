package back;

import Nodes.Method.Print;

public class MipsInstruction {
    public boolean isMem;
    public String label;
    public String opStr;

    public String reg1;
    public String reg2;
    public String reg3;
    public int offset=-1;

    public String comment;


    int getRegSize() {
        int i = 0;
        if (reg1 != null) i++;
        if (reg2 != null) i++;
        if (reg3 != null) i++;
        return i;
    }

    public MipsInstruction(String label) {
        this.label = label;
    }

    public MipsInstruction(String opStr, String reg1) {
        this.opStr = opStr;
        this.reg1 = reg1;
    }

    public MipsInstruction(String opStr, String reg1, String reg2) {
        this.opStr = opStr;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    public MipsInstruction(String opStr, String reg1, String reg2, String reg3) {
        this.opStr = opStr;
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.reg3 = reg3;
    }

    public MipsInstruction(String opStr, String reg1, String reg2, int offset) {
        this.opStr = opStr;
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.offset = offset;
    }

    public MipsInstruction() {
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void print() {
        Print.printMips(toString());
    }

    @Override
    public String toString() {
        if (comment != null) return "\t\t\t\t\t\t\t\t\t# " + comment;
        // 如果是个标志
        if (label != null) return label;
        int regSize = getRegSize();

        if (regSize == 1)
            return String.format("\t%s %s", opStr, reg1);
        if (regSize == 2) {
            if (offset != -1) return String.format("\t%s %s, %d(%s)", opStr, reg1, offset, reg2);
            return String.format("\t%s %s, %s", opStr, reg1, reg2);
        }
        if (regSize == 3)
            return String.format("\t%s %s, %s, %s", opStr, reg1, reg2, reg3);
        // 一个操作数
        return "\t" + opStr + " " + reg1;
    }
}
