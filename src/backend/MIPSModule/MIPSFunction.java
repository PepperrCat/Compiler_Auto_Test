package backend.MIPSModule;

import backend.MIPSInfo.RegisterInfo;
import backend.MIPSIntstruction.MIPSInstruction;
import backend.MIPSIntstruction.RInstruction;
import backend.MIPSOperand.MIPSRegister;
import backend.MIPSOperand.PhysicalRegister;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MIPSFunction {
    private String name;
    private ArrayList<MIPSBBlock> blocks = new ArrayList<>();
    private int allocSize;
    private HashSet<Integer> savedRegisters = new HashSet<>();
    private ArrayList<MIPSInstruction> argLoadInstructions = new ArrayList<>();
    public void addArgInstructions(MIPSInstruction argLoadInstruction) {
        if (argLoadInstruction instanceof RInstruction || argLoadInstruction.getName().equals("lw   ")) {
            argLoadInstructions.add(argLoadInstruction);
        }
    }
    public MIPSFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getAllocSize() {
        return allocSize;
    }

    public void setAllocSize(int allocSize) {
        this.allocSize = allocSize;
    }

    public void addMIPSBlocks(MIPSBBlock mipsbBlock) {
        blocks.add(mipsbBlock);
    }

    public void setBlocks(ArrayList<MIPSBBlock> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public ArrayList<MIPSBBlock> getBlocks() {
        return blocks;
    }

    public void addAllocSize(int size) {
        allocSize += size;
    }
    public void addToSavedRegisters(int index) {
        savedRegisters.add(index);
    }
    public void refreshArgOff() {
        int stack = allocSize + savedRegisters.size() * 4;
        for (MIPSInstruction argload: argLoadInstructions){
            int oldsa = ((RInstruction)argload).getSa();
            ((RInstruction)argload).setSa(oldsa + stack);
        }
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(1)).append(":\n");
        if (!name.equals("@main")) {
            int stackOffset = -4;
            if (!savedRegisters.isEmpty()) {
                for (Integer savedRegIndex : savedRegisters) {
                    sb.append("\t").append("sw   \t$").append(RegisterInfo.getPhysicalRegisterById(savedRegIndex)).append(",\t")
                            .append(stackOffset).append("($sp)\n");
                    stackOffset -= 4;
                }
            }
        }
        int stack = allocSize + savedRegisters.size() * 4;
        if (stack != 0) {
            sb.append("\taddi \t$sp,\t$sp,\t").append(-stack).append("\n");
        }

        for (MIPSBBlock mipsbBlock : blocks) {
            sb.append(mipsbBlock);
        }
        return sb.toString();
    }

    public HashSet<MIPSRegister> getRegs() {
        HashSet<MIPSRegister> regs = new HashSet<>();
        blocks.forEach(b -> b.getInstructions().forEach(i -> regs.addAll(i.getRegs())));
        HashSet<MIPSRegister> regss = (HashSet<MIPSRegister>) regs.stream().filter(r->!(r instanceof PhysicalRegister)).collect(Collectors.toSet());
        return regss;
    }

    public String refresh() {
        StringBuilder sb = new StringBuilder();
        int stack = allocSize + savedRegisters.size() * 4;
        if (stack != 0) {
            sb.append("addi \t$sp,\t$sp,\t").append(stack).append("\n");
        }
        if (!name.equals("@main")) {
            int stackOffset = -4;
            if (!savedRegisters.isEmpty()) {
                for (Integer savedRegIndex : savedRegisters) {
                    sb.append("\t").append("lw   \t$").append(RegisterInfo.getPhysicalRegisterById(savedRegIndex)).append(",\t")
                            .append(stackOffset).append("($sp)\n");
                    stackOffset -= 4;
                }
            }
        }
        return sb.toString();
    }
}
