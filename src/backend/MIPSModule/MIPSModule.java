package backend.MIPSModule;

import java.util.ArrayList;
import java.util.LinkedList;

public class MIPSModule {
    private ArrayList<MIPSGlobalVar> globarVars = new ArrayList<>();
    private LinkedList<MIPSFunction> functions = new LinkedList<>();

    public void addGlobalVariable(MIPSGlobalVar globalVar) {
        globarVars.add(globalVar);
    }

    public void addFunction(MIPSFunction function) {
        if (function.getName().equals("@main")) {
            functions.addFirst(function);
        } else
            functions.add(function);
    }

    public LinkedList<MIPSFunction> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // .data
        sb.append("\n.data\n");
        int namelen = 0;
        for (MIPSGlobalVar globalVariable : globarVars) {
            namelen = Math.max(globalVariable.getName().length(), namelen);
        }
        for (MIPSGlobalVar globalVariable : globarVars) {
            if (globalVariable.hasInit() && !globalVariable.isStr()) {
                sb.append(globalVariable.toString(namelen - globalVariable.getName().length()));
            } else if (!globalVariable.hasInit()) {    // 没有初始化了一定不是str
                sb.append(globalVariable.toString(namelen - globalVariable.getName().length()));
            } else if (globalVariable.isStr()) {
                sb.append(globalVariable.toString(namelen - globalVariable.getName().length()));
            }
            sb.append("\n");
        }
        sb.append("\n.text\n");
        for (MIPSFunction function : functions) {
            sb.append(function);
        }
        return sb.toString();
    }
}
