package IR.Types;

public class IntType extends Type{
    public static final IntType I32 = new IntType(32);
    public static final IntType I1 = new IntType(1);
    public static final IntType I8 = new IntType(8);
    private final int bit;
    public IntType(int bit) {
        super(TypeID.IntegerTyID);
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }

    @Override
    public String toString() { return "i" + bit; }

    @Override
    public int getSize() {
        return bit / 8;
    }
}
