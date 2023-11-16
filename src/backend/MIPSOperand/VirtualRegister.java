package backend.MIPSOperand;

import backend.LivenessAnalyze.LivenessAnalyzer;
import backend.MIPSBuilder;
import backend.MIPSModule.MIPSBBlock;

import java.util.Objects;

public class VirtualRegister extends MIPSRegister {
    protected static int rname = 0;

    public VirtualRegister() {
        super("x" + rname++);
    }

    @Override
    public boolean isAllocated() {
        return false;
    }

}
