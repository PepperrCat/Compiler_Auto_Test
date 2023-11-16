package IR.Optimization;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.Module;


import java.util.*;

public class DTree {
    public static void build() {
        for (Function function : Module.module.getFunctions()) {
            if (function.isBuilt()) {
                continue;
            } else {
                BasicBlock entry = function.getHeadBBlock();
                ArrayList<BitSet> domers = new ArrayList<>(function.getBlocks().size());    // 这里使用位图表示，很simple，值得使用！
                ArrayList<BasicBlock> blocks = new ArrayList<>();
                int index = 0;
                for (BasicBlock curBlock : function.getBlocks()) {
                    curBlock.clearDominators();
                    curBlock.clearImmDominates();
                    blocks.add(curBlock);
                    domers.add(new BitSet());
                    if (curBlock == entry) {
                        domers.get(index).set(index);
                    } else {
                        domers.get(index).set(0, function.getBlocks().size());
                    }
                    index++;
                }
                boolean flag = true;
                int tmp = 0;
                for (tmp = 0; flag; tmp++) {
                    flag = false;
                    index = 0;
                    for (BasicBlock curBlock : function.getBlocks()) {
                        if (curBlock != entry) {
                            BitSet bs = new BitSet();
                            bs.set(0, function.getBlocks().size());
                            for (BasicBlock preBlock : curBlock.getPredecessors())
                                bs.and(domers.get(blocks.indexOf(preBlock)));
                            bs.set(index);
                            if (!bs.equals(domers.get(index))) {
                                domers.get(index).clear();
                                domers.get(index).or(bs);
                                flag = true;
                            }
                        }
                        index++;
                    }
                }
                for (int i = 0; i < function.getBlocks().size(); i++) {
                    BasicBlock curBlock = blocks.get(i);
                    BitSet domerInfo = domers.get(i);
                    for (int j = domerInfo.nextSetBit(0); j >= 0; j = domerInfo.nextSetBit(j + 1)) {
                        curBlock.addToDominators(blocks.get(j));
                    }
                }
                for (int i = 0; i < blocks.size(); i++) {
                    BasicBlock curBlock = blocks.get(i);
                    for (BasicBlock maybeIdomerbb : curBlock.getDominators()) {
                        if (maybeIdomerbb != curBlock) {
                            boolean isIdom = true;
                            for (BasicBlock domerbb : curBlock.getDominators()) {
                                if (domerbb != curBlock && domerbb != maybeIdomerbb && domerbb.getDominators().contains(maybeIdomerbb)) {
                                    isIdom = false;
                                    break;
                                }
                            }
                            if (isIdom) {
                                curBlock.setImmDominator(maybeIdomerbb);
                                maybeIdomerbb.addToImmDominates(curBlock);
                                break;
                            }
                        }
                    }
                }
                registerDTreeDepth(entry, 0);
                for (BasicBlock block : function.getBlocks()) {
                    block.getDominanceFrontier().clear();
                }
                for (BasicBlock block : function.getBlocks()) {
                    for (BasicBlock succBlock : block.getSuccessors()) {
                        BasicBlock cur = block;
                        while (!succBlock.getDominators().contains(cur) || cur == succBlock) {
                            cur.getDominanceFrontier().add(succBlock);
                            cur = cur.getImmDominator();
                        }
                    }
                }
            }

        }
    }

    private static void registerDTreeDepth(BasicBlock basicBlock, Integer depth) {
        basicBlock.setDTreeDepth(depth);
        for (BasicBlock succ : basicBlock.getImmDominates()) {
            registerDTreeDepth(succ, depth++);
        }
    }

}
