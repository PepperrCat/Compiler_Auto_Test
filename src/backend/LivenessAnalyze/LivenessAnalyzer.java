package backend.LivenessAnalyze;

import backend.MIPSBuilder;
import backend.MIPSIntstruction.MIPSInstruction;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSModule.MIPSFunction;
import backend.MIPSOperand.MIPSRegister;

import java.util.HashMap;
import java.util.HashSet;

public class LivenessAnalyzer {
    private MIPSFunction mipsFunction;

    private LivenessAnalyzer() {
    }

    private static LivenessAnalyzer instance = new LivenessAnalyzer();

    public static LivenessAnalyzer getInstance() {
        return instance;
    }

    public HashMap<MIPSBBlock, LivenessInfo> analyze(MIPSFunction mipsFunction) {
        this.mipsFunction = mipsFunction;
        HashMap<MIPSBBlock, LivenessInfo> liveInfoMap = new HashMap<>();
        for (MIPSBBlock block : mipsFunction.getBlocks()) { // 计算 use 和 def
            LivenessInfo livenessInfo = new LivenessInfo();
            liveInfoMap.put(block, livenessInfo);
            for (MIPSInstruction instr : block.getInstructions()) {
                HashSet<MIPSRegister> use = instr.getUse();
                HashSet<MIPSRegister> def = instr.getDef();
                for (MIPSRegister r : use) {
                    if (!r.isAllocated() && !livenessInfo.getDef().contains(r)) {
                        livenessInfo.getUse().add(r);
                    }
                }
                for (MIPSRegister r : def) {
                    if (!r.isAllocated()) {
                        livenessInfo.getDef().add(r);
                    }
                }
            }
            livenessInfo.getIn().addAll(livenessInfo.getUse());
        }

        // in[B] = use[B] ∪ (out[B] - def[B])    out[B] = ∪succ IN[S];
        /**
         * while(out 发生变化)
         * */
        boolean changed = true;
        while (changed) {
            changed = false;
            for (MIPSBBlock block : mipsFunction.getBlocks()) {
                LivenessInfo livenessInfo = liveInfoMap.get(block);
                HashSet<MIPSRegister> newLiveOut = new HashSet<>();
                MIPSBBlock[] succs = MIPSBuilder.getSucc(block.getName());
                if (succs[0] != null) {
                    LivenessInfo succBlockInfo = liveInfoMap.get(succs[0]);
                    newLiveOut.addAll(succBlockInfo.getIn());
                }
                if (succs[1] != null) {
                    LivenessInfo succBlockInfo = liveInfoMap.get(succs[1]);
                    newLiveOut.addAll(succBlockInfo.getIn());
                }
                HashSet<MIPSRegister> oldin = new HashSet<>(livenessInfo.getIn());
                livenessInfo.setOut(newLiveOut);
                livenessInfo.setIn(new HashSet<>(livenessInfo.getUse()));
                livenessInfo.getOut().stream()
                        .filter(objOperand -> !livenessInfo.getDef().contains(objOperand))
                        .forEach(livenessInfo.getIn()::add);
                if (!oldin.equals(livenessInfo.getIn())) {
                    changed = true;
                }
            }
        }

        return liveInfoMap;
    }
}
