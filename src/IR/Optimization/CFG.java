package IR.Optimization;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.BrInstruction;
import IR.Module;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 先做的后端优化，这里又要做一个CFG构建。。。。。
 */
public class CFG {
    public static void build() {
        isBuilt = new HashSet<>();
        for (Function function : Module.module.getFunctions()) {
            if (function.isBuilt()) {
                continue;
            } else {
                function.getBlocks().forEach(BasicBlock::clearPreAndSuc);
                buildCFG(function.getHeadBBlock());
                boolean flag = true;
                while (flag) {
                    flag = false;
                    BasicBlock entry = function.getHeadBBlock();
                    ArrayList<BasicBlock> tmp = new ArrayList<>(function.getBlocks());
                    for (BasicBlock basicBlock : tmp) {
                        if (!entry.equals(basicBlock) && basicBlock.isPreEmpty()) {
                            flag = true;
                            basicBlock.getSuccessors().forEach(suc -> {
                                suc.removePre(basicBlock);
                            });
                            function.removeBlock(basicBlock);
                        }
                    }
                }
            }
        }
    }

    private static HashSet<BasicBlock> isBuilt;

    // 构建CFG
    private static void buildCFG(BasicBlock block) {
        isBuilt.add(block);
        Instruction lastInstruction = block.getLastInstruction();
        if (lastInstruction instanceof BrInstruction jump) {
            if (jump.hasCondition()) {
                BasicBlock trueBB = (BasicBlock) jump.getTrueBBlock();
                BasicBlock falseBB = (BasicBlock) jump.getFalseBBlock();
                block.addSuc(trueBB);
                block.addSuc(falseBB);
                trueBB.addPre(block);
                falseBB.addPre(block);
                if (!isBuilt.contains(trueBB)) buildCFG(trueBB);
                if (!isBuilt.contains(falseBB)) buildCFG(falseBB);
            } else {
                BasicBlock trueBB = (BasicBlock) jump.getTrueBBlock();
                block.addSuc(trueBB);
                trueBB.addPre(block);
                if (!isBuilt.contains(trueBB)) buildCFG(trueBB);
            }
        }
    }
}
