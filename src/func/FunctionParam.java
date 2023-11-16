package func;

public class FunctionParam {
    private String name;
    private int d; // 维度

    public FunctionParam(String name, int d) {
        this.name = name;
        this.d = d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

}
