package symbol;


import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public Map<String,Symbol> symbols = new HashMap<>();
    public boolean isFunc ;
    public FuncType funcType;

    public SymbolTable(Map<String, Symbol> symbols, boolean isFunc, FuncType funcType) {
        this.symbols = symbols;
        this.isFunc = isFunc;
        this.funcType = funcType;
    }

    public SymbolTable() {

    }
}
