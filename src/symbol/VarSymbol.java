package symbol;

public class VarSymbol extends Symbol {
    public boolean isConst;
    public boolean isGlobal;
    public boolean isDefined;
    public int dimension;

    public VarSymbol(String name, boolean isConst, int dimension) {
        super(name);
        this.isConst = isConst;
        this.dimension = dimension;
    }

}
