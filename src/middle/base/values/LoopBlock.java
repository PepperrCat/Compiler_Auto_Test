package middle.base.values;

import middle.base.BasicBlock;

public class LoopBlock extends BasicBlock {
    public boolean isLoop =true;
    public BasicBlock outBlock;
    public BasicBlock startBlock;

    public LoopBlock(BasicBlock startBlock , BasicBlock outBlock) {
        this.outBlock = outBlock;
        this.startBlock = startBlock;
    }
}
