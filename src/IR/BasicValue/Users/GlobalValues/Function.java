package IR.BasicValue.Users.GlobalValues;

import IR.BasicValue.Argument;
import IR.BasicValue.BasicBlock;
import IR.Module;
import IR.Types.FunctionType;
import IR.Types.Type;
import IR.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Function extends Value {
    public static Function putstr = null;
    public static Function putint = null;
    public static Function getint = null;
    private final ArrayList<Argument> arguments = new ArrayList<>();
    private final LinkedList<BasicBlock> blocks = new LinkedList<>();
    private Type returnType;
    private boolean isBuilt = false;
    private HashMap<String, Value> valueTable = new HashMap<>();

    public Function(String valueName, Type valueType) {
        super(valueName, Module.module, valueType);
        FunctionType functionType = (FunctionType) getValueType();
        this.returnType = functionType.getReturnType();
        for (int i = 0; i < functionType.getFArgs().size(); i++) {
            Argument argument = new Argument("%a" + i, this, functionType.getFArgs().get(i));
            arguments.add(argument);
            valueTable.put(argument.getValueName(), argument);
        }
    }

    public Function(String valueName, Type valueType, boolean isBuilt) {
        super(valueName, Module.module, valueType);
        FunctionType functionType = (FunctionType) getValueType();
        this.returnType = functionType.getReturnType();
        for (int i = 0; i < functionType.getFArgs().size(); i++) {
            Argument argument = new Argument("%a" + i, this, functionType.getFArgs().get(i));
            arguments.add(argument);
            valueTable.put(argument.getValueName(), argument);
        }
        this.isBuilt = isBuilt;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Function() {
        super("@LoopOut", null, null);
    }

    public void addFunctionSymbol(Value value) {
        valueTable.put(value.getValueName(), value);
    }

    public void insertBBlock(BasicBlock basicBlock) {
        blocks.addLast(basicBlock);
    }
    public BasicBlock getHeadBBlock() {
        return blocks.getFirst();
    }
    public LinkedList<BasicBlock> getBlocks() {
        return blocks;
    }
    public void removeBlock(BasicBlock basicBlock) {
        blocks.remove(basicBlock);
    }
    public int getArgsSize() {
        return arguments.size();
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(isBuilt ? "declare dso_local " : "define dso_local ").append(returnType).append(" ").append(getValueName()).append('(');
        for (Argument argument : arguments) {
            s.append(argument.getValueType()).append(" ").append(argument.getValueName()).append(", ");
        }
        if (!arguments.isEmpty()) {
            s.delete(s.length() - 2, s.length());
        }
        s.append(") ");
        if (isBuilt) {
            s.append("\n");
            return s.toString();
        }
        s.append("{\n");
        for (BasicBlock basicBlock : blocks) {
            s.append(basicBlock.toString()).append('\n');
        }
        s.append("}\n");
        return s.toString();
    }
}
