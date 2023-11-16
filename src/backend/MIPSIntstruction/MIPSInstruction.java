package backend.MIPSIntstruction;

import backend.MIPSOperand.MIPSRegister;

import java.util.HashSet;

public class MIPSInstruction {
    private String name;
    private HashSet<MIPSRegister> def = new HashSet<>();
    private HashSet<MIPSRegister> use = new HashSet<>();

    public MIPSInstruction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addDef(MIPSRegister mipsRegister) {
        if (mipsRegister != null)
            def.add(mipsRegister);
    }

    public void addUse(MIPSRegister mipsRegister) {
        if (mipsRegister != null)
            use.add(mipsRegister);
    }

    public void replaceDef(MIPSRegister oldr, MIPSRegister newr) {
        if (oldr != null)
            def.remove(oldr);
        if (newr != null)
            def.add(newr);
    }

    public void replaceUse(MIPSRegister oldr, MIPSRegister newr) {
        if (oldr != null)
            use.remove(oldr);
        if (newr != null)
            use.add(newr);
    }

    public void removeDef(MIPSRegister oldr) {
        if (oldr != null)
            def.remove(oldr);
    }

    public void removeUse(MIPSRegister oldr) {
        if (oldr != null)
            use.remove(oldr);
    }

    public HashSet<MIPSRegister> getDef() {
        return def;
    }

    public HashSet<MIPSRegister> getUse() {
        return use;
    }
    public HashSet<MIPSRegister> getRegs() {
        HashSet<MIPSRegister> regs = new HashSet<>(def);
        regs.addAll(use);
        return regs;
    }
}
