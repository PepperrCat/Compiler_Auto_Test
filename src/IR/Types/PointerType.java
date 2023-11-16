package IR.Types;

public class PointerType extends Type{
    Type type;
    public PointerType(Type type) {
        super(TypeID.PointerTyID);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + "*";
    }

    @Override
    public int getSize() {
        return 4;
    }
}
