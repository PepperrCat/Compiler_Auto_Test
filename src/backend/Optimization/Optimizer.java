package backend.Optimization;

import backend.MIPSBuilder;
import backend.MIPSIntstruction.EInsrtruction;
import backend.MIPSIntstruction.IInstruction;
import backend.MIPSIntstruction.MIPSInstruction;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSModule.MIPSFunction;
import backend.MIPSModule.MIPSModule;
import backend.MIPSOperand.MIPSRegister;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Optimizer {
    public Optimizer(MIPSModule mipsModule) {
        this.mipsModule = mipsModule;
    }

    private MIPSModule mipsModule;
    private ArrayList<MIPSBBlock> blocks;
    private HashSet<MIPSBBlock> blockSet;

    public void optimize() {
        bblockMerge();
        peepHole();
    }

    private void bblockMerge() {
        for (MIPSFunction func : mipsModule.getFunctions()) {
            blocks = new ArrayList<>();
            blockSet = new HashSet<>();
            for (MIPSBBlock block : func.getBlocks()) {
                merge(block);
            }
            func.setBlocks(blocks);
        }
    }

    private void merge(MIPSBBlock block) {
        if (blockSet.contains(block))
            return;
        blockSet.add(block);
        blocks.add(block);
        MIPSBBlock[] succs = MIPSBuilder.getSucc(block.getName());
        if (succs[0] == null && succs[1] == null) {
            // do nothing
        } else if (succs[1] == null) {  // 只有一个后继基本块，考虑合并
            if (!blockSet.contains(succs[0])) {
                block.removeJumpInstruction();
                merge(succs[0]);
            }
        } else {    // 俩基本快，因为不存在0为null，1不为null
            if (!blockSet.contains(succs[1])) { //先假后真，省一个j
                block.removeJumpInstruction();
                merge(succs[1]);
            }
            if (!blockSet.contains(succs[0])) {
                merge(succs[0]);
            }
        }
    }

    private void peepHole() {
        for (MIPSFunction function : mipsModule.getFunctions()) {
            for (MIPSBBlock block : function.getBlocks()) {
                LinkedList<MIPSInstruction> TMP = new LinkedList<>(block.getInstructions());
                for (MIPSInstruction mipsInstruction : TMP) {
                    PeepHolePatten php = matchPatten(mipsInstruction);
                    peepHoleOpt(block, mipsInstruction, php);
                }
            }
        }
    }


    private PeepHolePatten matchPatten(MIPSInstruction mipsInstruction) {
        if (mipsInstruction.getName().equals("move ")) {
            EInsrtruction move = (EInsrtruction) mipsInstruction;
            if (move.getRd() != null && move.getRd().equals(move.getRs())) {
                return PeepHolePatten.moveRegToSelf;
            }
        } else if (mipsInstruction.getName().equals("addiu")) {
            IInstruction addiu = (IInstruction) mipsInstruction;
            if (addiu.getImmediate().getImmediate() == 0) {
                return PeepHolePatten.addorsub0;
            }
        }
        return PeepHolePatten.nothing;
    }

    private void peepHoleOpt(MIPSBBlock block, MIPSInstruction mipsInstruction, PeepHolePatten php) {
        switch (php) {
            case moveRegToSelf -> {
                block.removeInstruction(mipsInstruction);
            }
            case addorsub0 -> {
                MIPSRegister rd = ((IInstruction) mipsInstruction).getRd();
                MIPSRegister rs = ((IInstruction) mipsInstruction).getRs();
                if (rd.equals(rs)) {
                    block.removeInstruction(mipsInstruction);
                } else {
                    MIPSInstruction move = new EInsrtruction("move ", rd, rs);
                    block.addMIPSInstructionAfter(mipsInstruction, move);
                    block.removeInstruction(mipsInstruction);
                }
            }
            default -> {
                return;
            }
        }
    }

}
