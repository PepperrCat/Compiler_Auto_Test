package IR.Types;

import java.util.ArrayList;

public class ArrayType extends Type {
    private Type elementType;
    private int elementNum;

    public ArrayType(Type elementType, int elementNum) {
        super(TypeID.ArrayTyID);
        this.elementType = elementType;
        this.elementNum = elementNum;
    }

    public ArrayType(Type elementType, ArrayList<Integer> dimensions) {
        super(TypeID.ArrayTyID);
        this.elementNum = dimensions.get(0);
        if (dimensions.size() == 1) {
            this.elementType = elementType;
        } else {
            this.elementType = new ArrayType(elementType, new ArrayList<>(dimensions.subList(1,dimensions.size())));  // 递归可以到三维及以上.....
        }
    }


    @Override
    public int getSize() {
        return elementType.getSize() * elementNum;
    }

    public Type getElementType() {
        return elementType;
    }

    public int getElementNum() {
        return elementNum;
    }

    @Override
    public String toString() {
        return "[" + elementNum + " x " + elementType + "]";
    }
}
