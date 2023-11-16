package middle.base;

public enum ValueType {
    i32, VOID,i8,i1;

    @Override
    public String toString() {
        if (this == VOID) return "void";
        return super.toString();
    }
}
