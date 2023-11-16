package IR.Optimization;


public class IROptimizer {
    private IROptimizer() {
    }

    private static IROptimizer irOptimizer = new IROptimizer();

    public static IROptimizer getInstance() {
        return irOptimizer;
    }

    public void optimize() {
        CFG.build();
        DTree.build();
        MemToReg.optimize();
//        DeadCode.optimize();
    }
}
