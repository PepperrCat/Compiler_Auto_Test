package backend.MIPSModule;

import IR.BasicValue.Users.Instructions.PhiInstruction;
import backend.MIPSBuilder;
import backend.MIPSIntstruction.MIPSInstruction;
import backend.MIPSIntstruction.MacroInstruction;

import java.util.ArrayList;
import java.util.LinkedList;

public class MIPSBBlock {
    private String name;
    private LinkedList<MIPSInstruction> instructions = new LinkedList<>();
    private ArrayList<MIPSInstruction> phiMoveInstructions = new ArrayList<>();

    private ArrayList<String> pre = new ArrayList<>();

    public MIPSBBlock(String name) {
        this.name = name.substring(1);
    }

    public void addMIPSInstruction(MIPSInstruction mipsInstruction) {
        instructions.add(mipsInstruction);
    }

    public void addMIPSInstructionToHead(MIPSInstruction mipsInstruction) {
        instructions.addFirst(mipsInstruction);
    }

    public void addMIPSInstructionBefore(MIPSInstruction index, MIPSInstruction mipsInstruction) {
        instructions.add(instructions.indexOf(index), mipsInstruction);
    }

    public void addMIPSInstructionAfter(MIPSInstruction index, MIPSInstruction mipsInstruction) {
        instructions.add(instructions.indexOf(index) + 1, mipsInstruction);
    }

    public String getName() {
        return name;
    }

    public void removeJumpInstruction() {
        if (!(instructions.getLast() instanceof MacroInstruction))
            instructions.removeLast();
    }

    public void removeInstruction(MIPSInstruction mipsInstruction) {
        instructions.remove(mipsInstruction);
    }

    public LinkedList<MIPSInstruction> getInstructions() {
        return instructions;
    }

    public void addPhiMove(MIPSInstruction move) {
        if (move.getName().equals("move ") || move.getName().equals("li   ")) {
            phiMoveInstructions.add(move);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        for (MIPSInstruction instruction : instructions) {
            sb.append("\t").append(instruction);
        }
        return sb.toString();
    }

    public void fixPhiMove() {
        for (MIPSInstruction phi : phiMoveInstructions) {
            int size = instructions.size();
            MIPSBBlock[] succ = MIPSBuilder.getSucc(getName());
            if (succ[0] == null) {
                break;
            } else if (succ[1] == null) {
                instructions.add(size - 1, phi);
            } else {
                instructions.add(size - 2, phi);
            }
        }
    }
}
