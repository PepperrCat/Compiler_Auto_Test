package middle.base.values;

import middle.base.Value;
import middle.base.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Var extends Value {
    public static class Ptr extends Var {

        public Ptr(Var var) {
            super(var.name);
            this.type = var.type;
            this.dim = var.dim;
            this.isPtr = true;
        }
    }

    public List<Object> initValues = new ArrayList<>();
    public List<Value> resetInit = new ArrayList<>();


    public Var(String name, boolean isConst, List<Object> initValues, ValueType type) {
        super(name);
        this.isConst = isConst;
        this.initValues = initValues;
        this.type = type;

    }

    public Var(String funcParam) {
        super(funcParam);
    }


    @Override
    public void setGlobal(boolean global) {
        super.setGlobal(global);
    }

    public void ReSetArray() {
        for (int i = 0; i < size; i++) {
            if (i < initValues.size()) {
                resetInit.add((Value) ((List<?>) initValues.get(i)).get(0));
            } else resetInit.add(new Const(0, ValueType.i32));
        }

    }

    public void ReSetArray(int size1, int size2) {
        for (int i = 0; i < size1; i++) {
            if (i < initValues.size()) {
                List<?> list1 = (List<?>) initValues.get(i);
                for (int j = 0; j < size2; ++j) {
                    if (j < list1.size()) {
                        List<?> list2 = (List<?>) list1.get(j);
                        resetInit.add((Value) list2.get(0));
                    } else resetInit.add(new Const(0, ValueType.i32));
                }
            } else for (int j = 0; j < size2; ++j) resetInit.add(new Const(0, ValueType.i32));
        }

    }

    public Value getInitValue(int i) {
        if (i < resetInit.size()) {
            return (Const) resetInit.get(i);
        } else return new Const(0, ValueType.i32);
    }

    public String getInit() {
        if (dim == 0) {
            if (initValues.isEmpty()) return String.format("%s 0", getTypeString());
            else return String.format("%s %s", getTypeString(), initValues.get(0).toString());
        } else {
            if (initValues.isEmpty()) return String.format("%s zeroinitializer", getTypeString());
            else {
                StringBuilder s = new StringBuilder();
                boolean f = true;
                for (Object o : resetInit) {
                    if (f) {
                        f = false;
                    } else {
                        s.append(", ");
                    }
                    s.append(String.format("%s %s", getType(), o.toString()));
                }
                return String.format("%s [%s]", getTypeString(), s.toString());
            }

        }

    }
}
