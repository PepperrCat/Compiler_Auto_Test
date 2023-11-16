package IR.BasicValue;

import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.AllocInstruction;
import IR.BasicValue.Users.Instructions.PhiInstruction;
import IR.Types.LabelType;
import IR.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class BasicBlock extends Value {
    private final LinkedList<Instruction> instructions = new LinkedList<>();
    private final LinkedList<AllocInstruction> allocaInstructions = new LinkedList<>();
    private final LinkedList<PhiInstruction> phiInstructions = new LinkedList<>();


    public BasicBlock() {
        super("%LoopOut", new Function(), new LabelType());
    }

    public static final String nameEnd = generateRandomString(5);

    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public BasicBlock(String valueName, Value valueParent) {
        super(valueName + nameEnd, valueParent, new LabelType());

    }


    public Instruction getLastInstruction() {
        if (instructions.size() == 0)
            return null;
        return instructions.getLast();
    }

    public void insertInstruction(Instruction instruction) {
        instructions.addLast(instruction);
    }

    public void insertInstructionToHead(Instruction instruction) {
        instructions.addFirst(instruction);
    }

    public void insertAllocaInstruction(AllocInstruction allocInstruction) {
        allocaInstructions.add(allocInstruction);
    }

    @Override
    public Function getValueParent() {
        return (Function) super.getValueParent();
    }

    public LinkedList<Instruction> getInstructions() {
        return instructions;
    }

    public LinkedList<Instruction> getAllInstructions() {
        LinkedList<Instruction> ret = new LinkedList<>(phiInstructions);
        ret.addAll(allocaInstructions);
        ret.addAll(instructions);
        return ret;
    }

    public void removeInstruction(Instruction instruction) {
        if (instruction instanceof AllocInstruction)
            allocaInstructions.remove(instruction);
        else
            instructions.remove(instruction);
    }

    public LinkedList<AllocInstruction> getAllocaInstructions() {
        return allocaInstructions;
    }
    public void insertPhiInstruction(PhiInstruction phi) {
        phiInstructions.add(phi);
    }

    public LinkedList<PhiInstruction> getPhiInstructions() {
        return phiInstructions;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getValueName().substring(1)).append(":\n");
        for (PhiInstruction phi : phiInstructions) {
            s.append('\t').append(phi.toString()).append('\n');
        }
        for (AllocInstruction allocInstruction : allocaInstructions) {
            s.append('\t').append(allocInstruction.toString()).append('\n');
        }
        for (Instruction instruction : instructions) {
            s.append('\t').append(instruction.toString()).append('\n');
        }
        if (!instructions.isEmpty()) {
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }

    private ArrayList<BasicBlock> predecessors = new ArrayList<>();
    private ArrayList<BasicBlock> successors = new ArrayList<>();

    public void addPre(BasicBlock preBB) {
        predecessors.add(preBB);
    }

    public void addSuc(BasicBlock sucBB) {
        successors.add(sucBB);
    }

    public void clearPreAndSuc() {
        predecessors.clear();
        successors.clear();
    }

    public void removePre(BasicBlock preBB) {
        predecessors.remove(preBB);
    }

    public void removeSuc(BasicBlock sucBB) {
        predecessors.remove(sucBB);
    }

    public boolean containPre(BasicBlock preBB) {
        return predecessors.contains(preBB);
    }

    public boolean containSuc(BasicBlock sucBB) {
        return successors.contains(sucBB);
    }

    public boolean isPreEmpty() {
        return predecessors.isEmpty();
    }

    public boolean isSucEmpty() {
        return successors.isEmpty();
    }

    public ArrayList<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public ArrayList<BasicBlock> getSuccessors() {
        return successors;
    }

    private ArrayList<BasicBlock> dominators = new ArrayList<>();
    private ArrayList<BasicBlock> immDominates = new ArrayList<>();

    private BasicBlock immDominator;

    public void setImmDominator(BasicBlock immDominator) {
        this.immDominator = immDominator;
    }

    private int DTreeDepth;

    public void setDTreeDepth(int DTreeDepth) {
        this.DTreeDepth = DTreeDepth;
    }

    private final HashSet<BasicBlock> dominanceFrontier = new HashSet<>();

    public void clearDominators() {
        dominators.clear();
    }

    public void clearImmDominates() {
        immDominates.clear();
    }

    public void clearDominanceFrontier() {
        dominanceFrontier.clear();
    }

    public void addToDominators(BasicBlock basicBlock) {
        dominators.add(basicBlock);
    }

    public void addToImmDominates(BasicBlock basicBlock) {
        immDominates.add(basicBlock);
    }

    public void addToDominanceFrontier(BasicBlock basicBlock) {
        dominanceFrontier.add(basicBlock);
    }

    public ArrayList<BasicBlock> getDominators() {
        return dominators;
    }

    public ArrayList<BasicBlock> getImmDominates() {
        return immDominates;
    }

    public BasicBlock getImmDominator() {
        return immDominator;
    }

    public int getDTreeDepth() {
        return DTreeDepth;
    }

    public HashSet<BasicBlock> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public boolean dominate(BasicBlock other) {
        return other.getDominators().contains(this);
    }

}
