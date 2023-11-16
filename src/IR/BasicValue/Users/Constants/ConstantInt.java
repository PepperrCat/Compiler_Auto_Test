package IR.BasicValue.Users.Constants;

import IR.BasicValue.Users.Constant;
import IR.Types.IntType;
import IR.Types.Type;
import IR.Value;

import java.util.Objects;

public class ConstantInt extends Constant {
    public static final ConstantInt ZERO = new ConstantInt(0,32);
    private int value;
    private int bits;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstantInt that = (ConstantInt) o;
        return value == that.value && bits == that.bits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, bits);
    }

    public ConstantInt(int value, int bits) {
        super(new IntType(bits));
        this.value = value;
        this.bits = bits;

    }

    @Override
    public String getValueName() {
        return "" + value;
    }
    @Override
    public String toString() {
        return getValueName();
    }
}
