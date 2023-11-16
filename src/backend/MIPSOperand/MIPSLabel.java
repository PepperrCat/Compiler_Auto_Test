package backend.MIPSOperand;

public class MIPSLabel extends MIPSOperand{
    private String name;

    public MIPSLabel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName().substring(1);
    }
}
