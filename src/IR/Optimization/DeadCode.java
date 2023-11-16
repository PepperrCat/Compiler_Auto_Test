package IR.Optimization;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.BrInstruction;
import IR.BasicValue.Users.Instructions.CallInstruction;
import IR.BasicValue.Users.Instructions.RetInstruction;
import IR.BasicValue.Users.Instructions.StoreInstruction;
import IR.Module;
import IR.Use;
import IR.Value;

import java.util.HashSet;


public class DeadCode {

    public static void optimize() {
        for (Function function : Module.module.getFunctions()) {
            if (!function.isBuilt()) {
                ucode.clear();
                deleteDeadCode(function);
            }
        }
    }

    static HashSet<Instruction> ucode = new HashSet<>();

    private static void deleteDeadCode(Function function) {
        for (BasicBlock basicBlock : function.getBlocks()) {
            for (Instruction instruction : basicBlock.getAllInstructions()) {
                if (intoUCode(instruction)) {
                    addToUcode(instruction);
                }
            }
        }
        for (BasicBlock basicBlock : function.getBlocks()) {
            for (Instruction instruction : basicBlock.getInstructions()) {
                if (!ucode.contains(instruction)) {
                    basicBlock.removeInstruction(instruction);
                    instruction.getOperands().forEach(use -> {
                        use.getValue().removeUser(use.getUser());
                    });
                }
            }
        }
    }

    private static void addToUcode(Instruction instruction) {
        if (!ucode.contains(instruction)) {
            ucode.add(instruction);
            for (Use use : instruction.getOperands()) {
                Value operand = use.getValue();
                if (operand instanceof Instruction) {
                    addToUcode((Instruction) operand);
                }
            }
        }
    }

    private static boolean intoUCode(Instruction instruction) {
        return instruction instanceof StoreInstruction ||
                instruction instanceof CallInstruction ||
                instruction instanceof BrInstruction ||
                instruction instanceof RetInstruction;
    }
}
