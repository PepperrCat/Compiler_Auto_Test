package IR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
 * 以为之前存symboltable后面就不用了，但是好像还是要用，很难绷。。。。直接用栈式不用树得了..
 * */
public class SymbolTableInIR {
    private Stack<HashMap<String, Value>> symbolTable = new Stack<>();

    public SymbolTableInIR() {
        symbolTable.push(new HashMap<>());
    }

    public void pushToTable() {
        symbolTable.push(new HashMap<>());
    }

    public void popFromTable() {
        if (symbolTable.size() >= 2)
            symbolTable.pop();
    }

    public Value getValueFromST(String ident) {
        for (int i = symbolTable.size() - 1; i >=0; i--) {
            if (symbolTable.get(i).containsKey(ident)) {
                return symbolTable.get(i).get(ident);
            }
        }
        return null;
    }


    public HashMap<String, Value> peekFromTable() {
        return symbolTable.peek();
    }

    public void addValue(String name, Value value) {
        peekFromTable().put(name, value);
    }
}
