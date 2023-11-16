package backend.MIPSOperand;

public class MIPSImmediate extends MIPSOperand{
    private int immediate;

    public MIPSImmediate(int immediate) {
        this.immediate = immediate;
    }

    public int getImmediate() {
        return immediate;
    }

    public void setImmediate(int immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return Integer.toString(immediate);
    }

    @Override
    public String getName() {
        return toString();
    }
}
