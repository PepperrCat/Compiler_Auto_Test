package backend.RegisterAlloc;

import backend.MIPSOperand.MIPSRegister;

import java.util.Objects;

public class Edge {
    MIPSRegister u;
    MIPSRegister v;

    public Edge(MIPSRegister u, MIPSRegister v) {
        this.u = u;
        this.v = v;
    }

    public Edge reverse() {
        return new Edge(v, u);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(u, edge.u) && Objects.equals(v, edge.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }

    public MIPSRegister getU() {
        return u;
    }

    public void setU(MIPSRegister u) {
        this.u = u;
    }

    public MIPSRegister getV() {
        return v;
    }

    public void setV(MIPSRegister v) {
        this.v = v;
    }
}
