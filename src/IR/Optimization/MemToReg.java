package IR.Optimization;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.User;
import IR.BasicValue.Users.Constants.ConstantInt;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.AllocInstruction;
import IR.BasicValue.Users.Instructions.LoadInstruction;
import IR.BasicValue.Users.Instructions.PhiInstruction;
import IR.BasicValue.Users.Instructions.StoreInstruction;
import IR.LLVMBuilder;
import IR.Module;
import IR.Value;


import java.util.*;

public class MemToReg {
    private static StoreInstruction onlyStore;
    private static BasicBlock onlyBlock;
    private static BasicBlock entryBlock;
    private static HashSet<BasicBlock> usingBlocks = new HashSet<>();
    private static HashSet<BasicBlock> definingBlocks = new HashSet<>();
    private static ArrayList<AllocInstruction> allocInstructions = new ArrayList<>();
    private static final HashMap<PhiInstruction, AllocInstruction> phi2Alloca = new HashMap<>();

    public static void optimize() {
        for (Function function : Module.module.getFunctions()) {
            if (function.isBuilt()) {
                continue;
            } else
                mem2reg(function);
        }
    }

    private static void mem2reg(Function function) {
        entryBlock = function.getHeadBBlock();
        for (Instruction instr : entryBlock.getAllInstructions()) {
            if (instr instanceof AllocInstruction && ((AllocInstruction) instr).toReg()) {
                allocInstructions.add((AllocInstruction) instr);
            }
        }
        for (BasicBlock block : function.getBlocks()) {
            HashMap<AllocInstruction, StoreInstruction> alloca2store = new HashMap<>();

            for (Instruction instruction : block.getAllInstructions()) {
                if (instruction instanceof StoreInstruction && instruction.getValue(1) instanceof AllocInstruction) {
                    alloca2store.put((AllocInstruction) instruction.getValue(1), (StoreInstruction) instruction);
                } else if (instruction instanceof LoadInstruction && instruction.getValue(0) instanceof AllocInstruction alloca) {
                    StoreInstruction StoreInstruction = alloca2store.get(alloca);
                    if (StoreInstruction == null && block.equals(entryBlock)) {
                        instruction.replaceUses(ConstantInt.ZERO);
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    } else if (StoreInstruction != null) {
                        instruction.replaceUses(StoreInstruction.getValue());
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    }
                }
            }
            alloca2store.clear();
            for (int i = block.getAllInstructions().size() - 1; i >= 0; i--) {
                Instruction instruction = block.getAllInstructions().get(i);
                if (instruction instanceof StoreInstruction && instruction.getValue(1) instanceof AllocInstruction alloca) {
                    StoreInstruction StoreInstruction = alloca2store.get(alloca);
                    if (StoreInstruction != null) {
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    } else {
                        alloca2store.put(alloca, (StoreInstruction) instruction);
                    }
                }
            }
        }
        ArrayList<AllocInstruction> tmp = new ArrayList<>(allocInstructions);
        for (AllocInstruction alloca : tmp) {
            if (alloca.getUsers().isEmpty()) {
                alloca.clearOperands();
                ((BasicBlock) alloca.getValueParent()).removeInstruction(alloca);
                allocInstructions.remove(alloca);
                continue;
            }
            definingBlocks.clear();
            usingBlocks.clear();
            onlyBlock = null;
            onlyStore = null;
            int cnt = 0;
            for (Value user : alloca.getUsers()) {
                // 如果是 StoreInstruction
                if (user instanceof StoreInstruction) {
                    definingBlocks.add((BasicBlock) user.getValueParent());
                    if (cnt == 0) {
                        onlyStore = (StoreInstruction) user;
                    }
                    cnt++;
                } else if (user instanceof LoadInstruction) {
                    usingBlocks.add((BasicBlock) user.getValueParent());
                }
            }
            if (cnt > 1) {
                onlyStore = null;
            }
            if (definingBlocks.size() == 1 && definingBlocks.equals(usingBlocks)) {
                onlyBlock = definingBlocks.iterator().next();
            }
            if (definingBlocks.size() == 0) {
                ArrayList<User> loadClone = new ArrayList<>(alloca.getUsers());
                for (User user : loadClone) {
                    LoadInstruction LoadInstruction = (LoadInstruction) user;
                    LoadInstruction.replaceUses(ConstantInt.ZERO);
                    LoadInstruction.clearOperands();
                    ((BasicBlock) LoadInstruction.getValueParent()).removeInstruction(LoadInstruction);
                }
                alloca.clearOperands();
                ((BasicBlock) alloca.getValueParent()).removeInstruction(alloca);
                allocInstructions.remove(alloca);
                continue;
            }
            if (onlyStore != null) {
                boolean flag = true;
                usingBlocks.clear();
                Value replaceValue = onlyStore.getValue();
                ArrayList<User> users = new ArrayList<>(alloca.getUsers());
                for (User user : users) {
                    if (user instanceof StoreInstruction) {
                        if (!user.equals(onlyStore)) {
                            flag = false;
                            break;
                        }
                    } else if (user instanceof LoadInstruction LoadInstruction) {
                        if (!onlyStore.getValueParent().equals(LoadInstruction.getValueParent()) && ((BasicBlock) onlyStore.getValueParent()).dominate((BasicBlock) LoadInstruction.getValueParent())) {
                            LoadInstruction.replaceUses(replaceValue);
                            LoadInstruction.clearOperands();
                            ((BasicBlock) LoadInstruction.getValueParent()).removeInstruction(LoadInstruction);
                        } else {
                            usingBlocks.add((BasicBlock) LoadInstruction.getValueParent());
                        }
                    }
                }
                if (usingBlocks.isEmpty()) {
                    onlyStore.clearOperands();
                    ((BasicBlock) onlyStore.getValueParent()).removeInstruction(onlyStore);
                    alloca.clearOperands();
                    ((BasicBlock) alloca.getValueParent()).removeInstruction(alloca);
                    allocInstructions.remove(alloca);
                    continue;
                }
            }
            if (onlyBlock != null) {
                boolean encounterStore = false;
                for (Instruction instruction : onlyBlock.getAllInstructions()) {
                    if (instruction instanceof LoadInstruction && instruction.getValue(0) == alloca && !encounterStore) {
                        instruction.replaceUses(ConstantInt.ZERO);
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    } else if (instruction instanceof StoreInstruction && instruction.getValue(1) == alloca) {
                        if (encounterStore) {
                            instruction.clearOperands();
                            ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                        } else {
                            encounterStore = true;
                        }
                    }
                }
                alloca.clearOperands();
                ((BasicBlock) alloca.getValueParent()).removeInstruction(alloca);
                allocInstructions.remove(alloca);
                continue;
            }
            HashSet<BasicBlock> phiBlocks = new HashSet<>();
            for (BasicBlock definingBlock : definingBlocks) {
                phiBlocks.addAll(definingBlock.getDominanceFrontier());
            }
            boolean flag = true;
            while (flag) {
                flag = false;
                HashSet<BasicBlock> newAns = new HashSet<>(phiBlocks);
                for (BasicBlock block : phiBlocks) {
                    newAns.addAll(block.getDominanceFrontier());
                }
                if (newAns.size() > phiBlocks.size()) {
                    flag = true;
                    phiBlocks = newAns;
                }
            }
            HashSet<BasicBlock> tmpphi = new HashSet<>(phiBlocks);
            for (BasicBlock phiBlock : tmpphi) {
                for (Instruction instruction : phiBlock.getAllInstructions()) {
                    if (instruction instanceof LoadInstruction && instruction.getValue(0) == alloca) {
                        break;
                    } else if (instruction instanceof StoreInstruction && instruction.getValue(1) == alloca) {
                        phiBlocks.remove(phiBlock);
                    }
                }
            }
            for (BasicBlock phiBlock : phiBlocks) {
                PhiInstruction phi = LLVMBuilder.irBuilder.buildPhi(alloca.getAllocatedType(), phiBlock);
                phi2Alloca.put(phi, alloca);
            }
        }
        if (allocInstructions.isEmpty()) {
            return;
        }
        HashMap<BasicBlock, Boolean> visitMap = new HashMap<>();
        HashMap<AllocInstruction, Value> variableVersion = new HashMap<>();
        function.getBlocks().forEach(basicBlock -> visitMap.put(basicBlock, false));
        for (int i = 0; i < allocInstructions.size(); i++) {
            variableVersion.put(allocInstructions.get(i), ConstantInt.ZERO);
        }
        Stack<Map.Entry<BasicBlock, HashMap<AllocInstruction, Value>>> bbStack = new Stack<>();
        bbStack.push(new AbstractMap.SimpleEntry<>(entryBlock, variableVersion));
        for (; !bbStack.isEmpty(); ) {
            Map.Entry<BasicBlock, HashMap<AllocInstruction, Value>> tmp1 = bbStack.pop();
            BasicBlock currentBlock = tmp1.getKey();
            variableVersion = tmp1.getValue();
            if (visitMap.get(currentBlock)) {
                continue;
            }
            int i = 0;
            LinkedList<Instruction> instructions = currentBlock.getAllInstructions();
            for (i = 0; instructions.get(i) instanceof PhiInstruction; i++) {
                variableVersion.put(phi2Alloca.get((PhiInstruction) instructions.get(i)), instructions.get(i));
            }
            for (; i < instructions.size(); i++) {
                Instruction instruction = instructions.get(i);
                if (instruction instanceof LoadInstruction LoadInstruction) {
                    if (LoadInstruction.getAddr() instanceof AllocInstruction) {
                        instruction.replaceUses(variableVersion.get((AllocInstruction) ((LoadInstruction) instruction).getAddr()));
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    }
                } else if (instruction instanceof StoreInstruction StoreInstruction) {
                    if (StoreInstruction.getAddr() instanceof AllocInstruction) {
                        variableVersion.put((AllocInstruction) StoreInstruction.getAddr(), StoreInstruction.getValue());
                        instruction.clearOperands();
                        ((BasicBlock) instruction.getValueParent()).removeInstruction(instruction);
                    }
                }
            }
            for (int i1 = 0; i1 < currentBlock.getSuccessors().size(); i1++) {
                instructions = currentBlock.getSuccessors().get(i1).getAllInstructions();
                for (i = 0; instructions.get(i) instanceof PhiInstruction; i++) {
                    PhiInstruction phi = (PhiInstruction) (instructions.get(i));
                    AllocInstruction ai = phi2Alloca.get(phi);
                    phi.setEntry(variableVersion.get(ai), currentBlock);
                }
                if (!visitMap.get(currentBlock.getSuccessors().get(i1))) {
                    bbStack.push(new AbstractMap.SimpleEntry<>(currentBlock.getSuccessors().get(i1), new HashMap<>(variableVersion)));
                }
            }
            visitMap.put(currentBlock, true);
        }
        for (AllocInstruction alloca : allocInstructions) {
            alloca.clearOperands();
            ((BasicBlock) alloca.getValueParent()).removeInstruction(alloca);
        }
        allocInstructions.clear();
        phi2Alloca.clear();
    }


}

