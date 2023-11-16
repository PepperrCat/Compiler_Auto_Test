package backend.MIPSOperand;

public class PhysicalRegister extends MIPSRegister{
    private int registerNum;
    private boolean isAlloc;
    public PhysicalRegister(String name, int num) {
        super(name);
        if (num >= 32 || num < 0)
            throw new RuntimeException("register outof index");
        registerNum = num;
        this.isAlloc = false;
    }

    public PhysicalRegister(String name, int num, boolean isAlloc) {
        super(name);
        if (num >= 32 || num < 0)
            throw new RuntimeException("register outof index");
        this.registerNum = num;
        this.isAlloc = isAlloc;
    }

    public void setRegisterNum(int registerNum) {
        this.registerNum = registerNum;
    }

    public int getRegisterNum() {
        return registerNum;
    }

    public void setAlloc(boolean alloc) {
        isAlloc = alloc;
    }

    @Override
    public boolean isAllocated() {
        return isAlloc;
    }
}
