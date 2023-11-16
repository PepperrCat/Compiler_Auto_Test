package backend.MIPSIntstruction;

import backend.MIPSModule.MIPSBBlock;
import backend.MIPSModule.MIPSFunction;

/**
 * j jal
 */
public class JInstruction extends MIPSInstruction {
    private String target;
    private boolean type;
    private MIPSFunction funcForRefresh;

    public JInstruction(String name, String target) {   // jal
        super(name);
        this.target = target;
    }

    public JInstruction(String name, boolean type, MIPSFunction funcForRefresh) {    // jr
        super(name);
        this.type = type;
        this.funcForRefresh = funcForRefresh;
    }

    public void setFuncForRefresh(MIPSFunction funcForRefresh) {
        this.funcForRefresh = funcForRefresh;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type) {
            sb.append(funcForRefresh.refresh()).append("\t");
        }
        sb.append(getName()).append("\t").append(type ? "$ra" : target.substring(1)).append("\n");
        return sb.toString();
    }
}
