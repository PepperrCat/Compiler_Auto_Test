package middle.base.values;

import middle.base.ValueType;

public class FuncParam extends Var {
    public FuncParam(String name, int dim, ValueType type) {
        super(name, false, null, type);
        this.dim = dim;
    }

    public FuncParam(int dim, ValueType type) {
        super("FuncParam");
        this.dim = dim;
        this.type = type;
    }


    public String getTypeString() {
        String s;
        if (dim == 0) s = type.toString();
        else s = String.format("%s*", type.toString());
        return s;
    }

}
