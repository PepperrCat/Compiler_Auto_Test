package middle.base;


import java.util.ArrayList;
import java.util.List;

public class ValueStack {
    static ValueStack stack1 = new ValueStack();

    public static ValueStack getInstance() {
        return stack1;
    }


    public List<ValueTable> stack = new ArrayList<>();

    public ValueTable getTop() {
        if (!stack.isEmpty())
            return stack.get(stack.size() - 1);
        return null;
    }


    public void push(ValueTable symbolTable) {
        stack1.stack.add(symbolTable);
    }
    public void setZero(){this.stack.clear();}

    public ValueTable pop() {
        return stack1.stack.remove(stack1.stack.size() - 1);
    }
}
