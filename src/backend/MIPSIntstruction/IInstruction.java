package backend.MIPSIntstruction;

import backend.MIPSModule.MIPSBBlock;
import backend.MIPSOperand.MIPSImmediate;
import backend.MIPSOperand.MIPSRegister;

/**
 * addi addiu andi ori xori lui lw sw beq bne slti sltiu
 */
public class IInstruction extends MIPSInstruction {
    private MIPSRegister rs;
    private MIPSRegister rd;
    private MIPSImmediate immediate;
    private MIPSBBlock target;

    public IInstruction(String name, MIPSRegister rs, MIPSRegister rd, MIPSImmediate immediate) {
        super(name);
        this.rs = rs;
        this.rd = rd;
        this.immediate = immediate;
        init();
    }

    public IInstruction(String name, MIPSRegister rs, MIPSRegister rd, MIPSBBlock target) {
        super(name);
        this.rs = rs;
        this.rd = rd;
        this.target = target;
        init();
    }

    private void init() {
        addDef(rd);
        addUse(rs);
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

    public MIPSRegister getRs() {
        return rs;
    }

    public MIPSRegister getRd() {
        return rd;
    }

    public MIPSImmediate getImmediate() {
        return immediate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t$").append(rd.getName()).append(",\t$").append(rs.getName()).append(",\t").append(immediate).append("\n");
        return sb.toString();
    }
}
