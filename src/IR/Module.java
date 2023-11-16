package IR;

import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.GlobalValues.GlobalVar;
import IR.Types.Type;

import java.util.LinkedList;

public class Module extends Value{
    private final LinkedList<Function> functions = new LinkedList<>();
    private final LinkedList<GlobalVar> globalVars = new LinkedList<>();
    public static Module module = new Module();

    public Module() {
        super("module", null, null);
    }

    public LinkedList<Function> getFunctions() {
        return functions;
    }

    public LinkedList<GlobalVar> getGlobalVariables() {
        return globalVars;
    }

    public void addFunction(Function f) {
        functions.add(f);
    }
    public void addGlobalValue(GlobalVar g) {
        globalVars.add(g);
    }
}
