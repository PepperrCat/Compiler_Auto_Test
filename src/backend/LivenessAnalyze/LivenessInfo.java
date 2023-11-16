package backend.LivenessAnalyze;

import backend.MIPSOperand.MIPSRegister;

import java.util.HashSet;

public class LivenessInfo {
    private HashSet<MIPSRegister>use = new HashSet<>();
    private HashSet<MIPSRegister>def = new HashSet<>();
    private HashSet<MIPSRegister>in = new HashSet<>();
    private HashSet<MIPSRegister>out = new HashSet<>();

    public HashSet<MIPSRegister> getUse() {
        return use;
    }

    public HashSet<MIPSRegister> getDef() {
        return def;
    }

    public HashSet<MIPSRegister> getIn() {
        return in;
    }

    public HashSet<MIPSRegister> getOut() {
        return out;
    }

    public void setUse(HashSet<MIPSRegister> use) {
        this.use = use;
    }

    public void setDef(HashSet<MIPSRegister> def) {
        this.def = def;
    }

    public void setIn(HashSet<MIPSRegister> in) {
        this.in = in;
    }

    public void setOut(HashSet<MIPSRegister> out) {
        this.out = out;
    }
}
