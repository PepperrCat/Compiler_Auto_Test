package middle.base;

import java.util.HashMap;
import java.util.Map;

public class ValueTable {

    public Map<String, Value> symbols = new HashMap<>();
    public boolean isFunc;
    public ValueType funcType;

    public ValueTable(Map<String, Value> symbols, boolean isFunc, ValueType funcType) {
        this.symbols = symbols;
        this.isFunc = isFunc;
        this.funcType = funcType;
    }
}
