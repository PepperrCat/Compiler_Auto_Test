package middle.base.instructions;

import middle.base.BasicBlock;
import middle.base.Value;
import middle.base.values.Instruction;

public class Br extends Instruction {
    //br label <dest>
    //br i1 <cond>, label <iftrue>, label <iffalse>
  public   BasicBlock outBlock = null;
    public  BasicBlock trueBlock, falseBlock = null;

    public Br(BasicBlock outBlock) {
        super("br");
        this.outBlock = outBlock;
        read.add(outBlock);
    }

    public void setOutBlock(BasicBlock outBlock) {
        this.outBlock = outBlock;
    }

    public void setTrueBlock(BasicBlock trueBlock) {
        this.trueBlock = trueBlock;
    }

    public void setFalseBlock(BasicBlock falseBlock) {
        this.falseBlock = falseBlock;
    }

    public Br(Value value, BasicBlock trueBlock, BasicBlock falseBlock) {
        super("br");
        this.value1=value;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
        read.add(value1);
        read.add(trueBlock);
        read.add(falseBlock);
    }

    @Override
    public String toString() {
        //br label <dest>
        //br i1 <cond>, label <iftrue>, label <iffalse>
        if (outBlock != null) {
            return String.format("br label %s", outBlock.getName());
        } else {
            return String.format("br i1 %s, label %s, label %s", value1.getName(), trueBlock.getName(), falseBlock.getName());
        }
    }
}
