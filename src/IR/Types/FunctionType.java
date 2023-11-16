package IR.Types;

import java.util.ArrayList;

public class FunctionType extends Type{
    private final ArrayList<Type> FArgs;
    private final Type returnType;
    public FunctionType(ArrayList<Type> FArgs, Type returnType) {
        super(TypeID.FunctionTyID);
        this.FArgs = FArgs;
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public ArrayList<Type> getFArgs() {
        return FArgs;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
