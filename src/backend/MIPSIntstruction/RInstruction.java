package backend.MIPSIntstruction;


import backend.MIPSOperand.MIPSRegister;

/**
 * add addu sub subu and or xor nor slt sltu sll srl sra sllv srav jr
 */
public class RInstruction extends MIPSInstruction {
    public enum RType {
        threeReg,
        twoReg,
        oneReg,
    }

    private MIPSRegister rs;    // register source
    private MIPSRegister rt;    // register target
    private MIPSRegister rd;    // register destination
    private int sa;             // shift offsets
    private RType RInsType;

    public RInstruction(String name, MIPSRegister rs, MIPSRegister rt, MIPSRegister rd) {   // 三个寄存器的R指令 add sub ...
        super(name);
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.RInsType = RType.threeReg;
        init();
    }

    public RInstruction(String name, MIPSRegister rs, MIPSRegister rd, int sa) {     // 俩寄存器的R指令 sll srl ...
        super(name);
        this.rs = rs;
        this.rd = rd;
        this.sa = sa;
        this.RInsType = RType.twoReg;
        init();
    }

    public RInstruction(String name, MIPSRegister rs) {  // 一个寄存器的R指令 jr
        super(name);
        this.rs = rs;
        this.RInsType = RType.oneReg;
        init();
    }

    private void init() {
        if (getName().equals("sw   "))
            addUse(rd);
        else
            addDef(rd);
        addUse(rt);
        addUse(rs);
    }

    public MIPSRegister getRs() {
        return rs;
    }

    public MIPSRegister getRt() {
        return rt;
    }

    public MIPSRegister getRd() {
        return rd;
    }

    public void setSa(int sa) {
        this.sa = sa;
    }

    public int getSa() {
        return sa;
    }

    public RType getRInsType() {
        return RInsType;
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
        if (oldr.equals(rt)) {
            rt = newr;
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
        if (oldr.equals(rt)) {
            rt = newr;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (RInsType) {
            case threeReg -> {
                sb.append(getName()).append("\t$").append(rd.getName()).append(",\t$").append(rs.getName()).append(",\t$").append(rt.getName()).append("\n");
            }
            case twoReg -> {
                sb.append(getName()).append("\t$").append(rd.getName()).append(",\t").append(sa).append("($").append(rs.getName()).append(")\n");
            }
            case oneReg -> {

            }
        }
        return sb.toString();
    }
}
