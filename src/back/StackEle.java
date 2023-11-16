package back;

public class StackEle {
    int pos;

    public StackEle(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos*4;
    }

    @Override
    public String toString() {
        return Integer.toString(getPos());
    }
}
