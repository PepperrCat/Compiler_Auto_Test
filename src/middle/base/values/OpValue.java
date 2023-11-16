package middle.base.values;

import middle.base.Op;
import middle.base.Value;

public class OpValue extends Value {
    public Op op;

    public OpValue(Op op) {
        super(Op.op2str(op));
        this.op = op;
    }

    public OpValue(String op) {
        super(op);
        this.op = Op.str2op(op);
    }

    @Override
    public String toString() {
       return op.toString();
    }
}
