package backend.MIPSIntstruction;

public class MacroInstruction extends MIPSInstruction{
    public MacroInstruction(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName() + "\n";
    }
}
