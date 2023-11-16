package middle.base;

import middle.base.values.Const;

public class Value {
    public String name;

    public ValueType type;
    public boolean isGlobal;
    public boolean isConst;
    public int dim;
    public int size, size1, size2;
    public boolean isPtr;


    public Value(String name) {
        this.name = name;
    }

    public String getName() {
        if (this instanceof Const c) return Integer.toString(c.value);
        if (!isGlobal) return "%" + name;
        return "@" + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getType() {
        return type;
    }


    public String getTypeString() {
//        String s;
//        if (dim == 0) s = type.toString();
//        else s = String.format("[%d x %s]", size, type.toString());
//        if (isPtr) return String.format("%s*", s);
//        else return s;
        if (isPtr) return String.format("%s*", type.toString());
        String s;
        if (dim == 0) s = type.toString();
        else s = String.format("[%d x %s]", size, type.toString());
        return s;
    }


    public void setType(ValueType type) {
        this.type = type;
    }

    public void setType(int size, ValueType type) {
        this.size = size;
        this.type = type;
    }

    public void setType(int size1, int size2, ValueType type) {
        this.size = size1 * size2;
        this.type = type;
        this.size1 = size1;
        this.size2 = size2;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof Value)
//            return this.getName().equals(((Value) obj).getName());
//        return false;
//    }
}
