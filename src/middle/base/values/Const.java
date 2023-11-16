package middle.base.values;

import middle.base.Value;
import middle.base.ValueType;

public class Const extends Value {
    public int value;

    public Const(int num) {
        super(Integer.toString(num));
        this.isConst = true;
        this.value = num;
        this.type = ValueType.i32;
    }
    public Const(int num, ValueType type) {
        super(Integer.toString(num));
        this.isConst = true;
        this.value = num;
        this.type = type;
    }

    public Const(String num) {
        super(num);
        this.isConst = true;
        this.value = Integer.parseInt(num);
        this.type = ValueType.i32;
    }

    public Object getValue() {
        return Integer.parseInt(name);
    }
//
//    public static Const add(Const c1, Const c2) {
//        return new Const(c1.value + c2.value);
//    }

    @Override
    public String toString() {
        return this.value + "";
    }
}
