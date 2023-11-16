package IR.Types;

public class VoidType extends Type{

    public VoidType() {
        super(TypeID.VoidTyID);
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public int getSize() {
        return 0;
    }
}
