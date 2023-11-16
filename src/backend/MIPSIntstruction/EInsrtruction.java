package backend.MIPSIntstruction;

import backend.MIPSOperand.MIPSImmediate;
import backend.MIPSOperand.MIPSLabel;
import backend.MIPSOperand.MIPSRegister;

public class EInsrtruction extends MIPSInstruction { // extend instruction move li ...

    private int type;
    private MIPSLabel label;

    public EInsrtruction(String name, MIPSRegister rd, MIPSRegister rs) {
        super(name);
        this.rs = rs;
        this.rd = rd;
        type = 0;
        init();
    }

    public EInsrtruction(String name, MIPSRegister rd, MIPSImmediate immediate) {
        super(name);
        this.rd = rd;
        this.immediate = immediate;
        type = 1;
        init();
    }

    public EInsrtruction(String name, MIPSRegister rd, MIPSLabel label) {
        super(name);
        this.rd = rd;
        this.label = label;
        type = 2;
        if (name.equals("bnez ")) {
            addUse(this.rd);
        } else
            init();
    }

    public EInsrtruction(String name, MIPSRegister rd) {
        super(name);
        this.rd = rd;
        type = 2;
        if (name.equals("mfhi "))
            addDef(this.rd);
        else
            addUse(this.rd);
    }

    @Override
    public void replaceDef(MIPSRegister oldr, MIPSRegister newr) {
        super.replaceDef(oldr, newr);
        if (oldr.equals(rd)) {
            rd = newr;
        }
        if (oldr.equals(rs)) {
            rs = newr;
        }
    }

    @Override
    public void replaceUse(MIPSRegister oldr, MIPSRegister newr) {
        super.replaceUse(oldr, newr);
        if (oldr.equals(rd)) {
            rd = newr;
        }
        if (oldr.equals(rs)) {
            rs = newr;
        }
    }

    private MIPSRegister rs;
    private MIPSRegister rd;
    private MIPSImmediate immediate;

    private void init() {
        addDef(rd);
        addUse(rs);
    }

    public MIPSRegister getRs() {
        return rs;
    }

    public void setRs(MIPSRegister rs) {
        this.rs = rs;
    }

    public MIPSRegister getRd() {
        return rd;
    }

    public void setRd(MIPSRegister rd) {
        this.rd = rd;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type == 0)
            sb.append(getName()).append("\t$").append(rd.getName()).append(",\t$").append(rs.getName()).append("\n");
        else if (type == 1)
            sb.append(getName()).append("\t$").append(rd.getName()).append(",\t").append(immediate).append("\n");
        else
            sb.append(getName()).append("\t$").append(rd.getName()).append((label == null) ? "\t" : ",\t").append(label == null ? "" : label).append("\n");
        return sb.toString();
    }
}
