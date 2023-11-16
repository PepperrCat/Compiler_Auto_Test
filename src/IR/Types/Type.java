package IR.Types;

public abstract class Type {
    private TypeID typeID;

    public Type(TypeID typeID) {
        this.typeID = typeID;
    }

    public TypeID getTypeID() {
        return typeID;
    }

    public boolean isVoidTy() {
        return typeID == TypeID.VoidTyID;
    }
    public boolean isLabelTy() {
        return typeID == TypeID.LabelTyID;
    }
    public boolean isIntegerTy() {
        return typeID == TypeID.IntegerTyID;
    }
    public boolean isFunctionTy() {
        return typeID == TypeID.FunctionTyID;
    }
    public boolean isArrayTy() {
        return typeID == TypeID.ArrayTyID;
    }
    public boolean isPointerTy() {
        return typeID == TypeID.PointerTyID;
    }

    public abstract int getSize();
}
