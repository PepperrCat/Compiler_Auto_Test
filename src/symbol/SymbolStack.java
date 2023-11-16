package symbol;

import java.util.ArrayList;
import java.util.List;

public class SymbolStack {
    static SymbolStack stack1 = new SymbolStack();

    public static SymbolStack getInstance() {
        return stack1;
    }


    public List<SymbolTable> stack = new ArrayList<>();

    public SymbolTable getTop() {
        if (!stack.isEmpty())
            return stack.get(stack.size() - 1);
        return null;
    }


    public void push(SymbolTable symbolTable) {
        stack1.stack.add(symbolTable);
    }
    public void setZero(){this.stack.clear();}

    public SymbolTable pop() {
        return stack1.stack.remove(stack1.stack.size() - 1);
    }
}
