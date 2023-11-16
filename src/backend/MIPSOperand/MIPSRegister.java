package backend.MIPSOperand;

import java.util.Objects;

public abstract class MIPSRegister extends MIPSOperand{
    private String name;

    public MIPSRegister(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isAllocated();
    @Override
    public String toString() {
        return getName();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MIPSRegister that = (MIPSRegister) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
