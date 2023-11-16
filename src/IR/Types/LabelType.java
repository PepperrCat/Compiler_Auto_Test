package IR.Types;

public class LabelType extends Type{

    public LabelType() {
        super(TypeID.LabelTyID);
    }

    @Override
    public int getSize() {
        return 0;
    }
    public String toString()
    {
        return "label";
    }
}
