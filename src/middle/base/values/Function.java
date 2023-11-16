package middle.base.values;

import Nodes.Method.Print;
import middle.base.BasicBlock;
import middle.base.Value;
import middle.base.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Function extends Value {

    public List<FuncParam> funcParams = new ArrayList<>();
    public List<BasicBlock> basicBlocks = new ArrayList<>();
    ValueType funcType;

    public ValueType getFuncType() {
        return funcType;
    }

    public Function(String name, ValueType type) {
        super(name);
        this.isGlobal = true;
        this.funcType = type;
        this.type = type;
    }

    public void print() {

        String s = "";
        boolean f = true;
        for (FuncParam funcParam : funcParams) {
            if (f) {

                f = false;
            } else {
                s += ", ";
            }
            s += String.format("%s %s", funcParam.getTypeString(), funcParam.getName());
        }
        Print.printIR("define dso_local "+funcType+" " + this.getName() + "(" + s + "){");
//        res.append("(");
//        for (int i = 0; i < funcFParams.size(); i++) {
//            res.append(funcFParams.get(i).getType());
//            if (i != funcFParams.size() - 1) res.append(", ");
//        }
//        res.append("){\n");
//        funcParams.forEach(funcParam -> Print.printIR(funcParam.toString()));
        for (BasicBlock basicBlock : basicBlocks) basicBlock.print();
        Print.printIR("}");

    }

    @Override
    public String getName() {
        return "@" + name;
    }
}
