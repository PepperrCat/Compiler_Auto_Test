package backend;

import Config.Config;
import IR.BasicValue.Argument;
import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Constant;
import IR.BasicValue.Users.Constants.ConstantArray;
import IR.BasicValue.Users.Constants.ConstantInt;
import IR.BasicValue.Users.Constants.ConstantStr;
import IR.BasicValue.Users.Constants.ConstantZero;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.GlobalValues.GlobalVar;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.*;
import IR.Module;
import IR.Types.ArrayType;
import IR.Types.PointerType;
import IR.Types.Type;
import IR.Types.VoidType;
import IR.Value;
import backend.LivenessAnalyze.LivenessAnalyzer;
import backend.MIPSInfo.RegisterInfo;
import backend.MIPSIntstruction.*;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSModule.MIPSFunction;
import backend.MIPSModule.MIPSGlobalVar;
import backend.MIPSModule.MIPSModule;
import backend.MIPSOperand.*;
import backend.Optimization.MulOptimizer;
import backend.RegisterAlloc.RegisterAllocer;
import io.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MIPSBuilder {
    private final Module llvmModule;
    private final MIPSModule mipsModule = new MIPSModule();

    public MIPSModule getMipsModule() {
        return mipsModule;
    }

    private HashMap<Value, MIPSOperand> v2mMap = new HashMap<>();

    private static HashMap<String, MIPSBBlock> n2mbMap = new HashMap<>();

    private static HashMap<String, ArrayList<String>> preMap = new HashMap<>();
    private static HashMap<String, String[]> succMap = new HashMap<>();

    public static ArrayList<MIPSBBlock> getPre(String name) {
        ArrayList<MIPSBBlock> blocks = new ArrayList<>();
        ArrayList<String> blocknames = preMap.get(name);
        for (String bname : blocknames) {
            blocks.add(n2mbMap.get(bname));
        }
        return blocks;
    }

    public static MIPSBBlock[] getSucc(String name) {
        MIPSBBlock[] succs = new MIPSBBlock[2];
        String[] succnames = succMap.get(name);
        if (succnames[0] != null)
            succs[0] = n2mbMap.get(succnames[0]);
        if (succnames[1] != null)
            succs[1] = n2mbMap.get(succnames[1]);
        return succs;
    }

    private boolean containInv2mMap(Value v, MIPSBBlock mipsbBlock) {
        return v2mMap.containsKey(v);
    }

    private void putInv2mMap(Value v, MIPSOperand mipsRegister) {
        v2mMap.put(v, mipsRegister);
    }

    public MIPSBuilder(Module llvmModule) {
        this.llvmModule = llvmModule;
    }

    public void buildMIPS() throws IOException {
        buildModule();
        printMIPS("vmips");
        RegisterAllocer rar = new RegisterAllocer(mipsModule);
        rar.alloc();
    }

    private void buildModule() {
        for (GlobalVar globalVar : llvmModule.getGlobalVariables()) {
            mipsModule.addGlobalVariable(buildGlobalVar(globalVar));
        }
        firstPass();
        for (Function function : llvmModule.getFunctions()) {
            if (!function.isBuilt()) {
                MIPSFunction mipsFunction = buildFunction(function);
                mipsModule.addFunction(mipsFunction);
                buildPhi(function, mipsFunction);
            }
        }
    }

    private void firstPass() {
        for (Function function : llvmModule.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                preMap.put(block.getValueName().substring(1), new ArrayList<>());
                succMap.put(block.getValueName().substring(1), new String[2]);
            }
        }
    }

    private MIPSGlobalVar buildGlobalVar(GlobalVar globalVar) {
        Constant initVal = globalVar.getInitVal();
        if (initVal instanceof ConstantZero) {
            return new MIPSGlobalVar(globalVar.getValueName(), initVal.getValueType().getSize());
        } else if (initVal instanceof ConstantInt) {
            ArrayList<Integer> elements = new ArrayList<>();
            elements.add(((ConstantInt) initVal).getValue());
            return new MIPSGlobalVar(globalVar.getValueName(), elements);
        } else if (initVal instanceof ConstantArray) {
            ArrayList<Integer> elements = new ArrayList<>();
            for (Constant dataElement : ((ConstantArray) initVal).getElementList()) {
                if (dataElement instanceof ConstantInt)
                    elements.add(((ConstantInt) dataElement).getValue());
                else {
                    for (int i = 0; i < ((ConstantZero) dataElement).getArraySize(); i++) {
                        elements.add(0);
                    }
                }
            }
            return new MIPSGlobalVar(globalVar.getValueName(), elements);
        }
        return new MIPSGlobalVar(globalVar.getValueName(), ((ConstantStr) initVal).getContent());
    }

    private MIPSFunction buildFunction(Function function) {
        MIPSFunction mipsFunction = new MIPSFunction(function.getValueName());
        for (BasicBlock basicBlock : function.getBlocks()) {
            mipsFunction.addMIPSBlocks(buildBasicBlock(mipsFunction, basicBlock));
        }
        return mipsFunction;
    }

    private MIPSBBlock buildBasicBlock(MIPSFunction function, BasicBlock basicBlock) {
        MIPSBBlock mipsbBlock = new MIPSBBlock(basicBlock.getValueName());
        n2mbMap.put(mipsbBlock.getName(), mipsbBlock);
        for (AllocInstruction allocInstruction : basicBlock.getAllocaInstructions()) {
            mipsbBlock.addMIPSInstruction(buildAllocInstruction(allocInstruction, function, mipsbBlock)); // 函数和块都得向下传递，否则跨块不好处理？
        }
        for (Instruction instruction : basicBlock.getInstructions()) {
            MIPSInstruction mipsInstruction = buildInstruction(instruction, mipsbBlock, function);
            if (mipsInstruction != null)
                mipsbBlock.addMIPSInstruction(mipsInstruction);
        }
        return mipsbBlock;
    }

    private MIPSInstruction buildAllocInstruction(AllocInstruction allocInstruction, MIPSFunction function, MIPSBBlock mipsbBlock) {
        Type type = ((PointerType) allocInstruction.getValueType()).getType();
        MIPSRegister rd = putNewVGtoMap(allocInstruction, function, mipsbBlock);
        IInstruction add = new IInstruction("addiu", RegisterInfo.getPhysicalRegisterById(29), rd, new MIPSImmediate(function.getAllocSize()));
        function.addAllocSize(type.getSize());
        return add;
    }

    private MIPSRegister putNewVGtoMap(Value irValue, MIPSFunction mipsFunction, MIPSBBlock mipsbBlock) {
        VirtualRegister vg = new VirtualRegister();
        if (irValue instanceof ConstantInt) {
            return vg;
        } else if (irValue instanceof Argument arg) {
            return buildArgument(arg, mipsFunction, mipsbBlock);
        } else {
            putInv2mMap(irValue, vg);
            return vg;
        }
    }

    private MIPSInstruction buildInstruction(Instruction instruction, MIPSBBlock mipsbBlock, MIPSFunction mipsFunction) {
        if (instruction instanceof AddInstruction) {
            Value l = ((AddInstruction) instruction).getLeft(), r = ((AddInstruction) instruction).getRight();
            MIPSRegister rd = new VirtualRegister();
            putInv2mMap(instruction, rd);
            MIPSRegister rs = containInv2mMap(l, mipsbBlock) ? (MIPSRegister) v2mMap.get(l) : putNewVGtoMap(l, mipsFunction, mipsbBlock);
            MIPSRegister rt = containInv2mMap(r, mipsbBlock) ? (MIPSRegister) v2mMap.get(r) : putNewVGtoMap(r, mipsFunction, mipsbBlock);

            if (l instanceof ConstantInt && r instanceof ConstantInt) {
                return new EInsrtruction("li   ", rd, new MIPSImmediate(((ConstantInt) l).getValue() + ((ConstantInt) r).getValue()));
            } else if (l instanceof ConstantInt) {
                return new IInstruction("addiu", rt, rd, new MIPSImmediate(((ConstantInt) l).getValue()));
            } else if (r instanceof ConstantInt) {
                return new IInstruction("addiu", rs, rd, new MIPSImmediate(((ConstantInt) r).getValue()));
            } else return new RInstruction("addu ", rs, rt, rd);

        } else if (instruction instanceof BrInstruction) {
            if (!((BrInstruction) instruction).hasCondition() || ((BrInstruction) instruction).getCondition() instanceof ConstantInt) {
                succMap.get(mipsbBlock.getName())[0] = ((BrInstruction) instruction).getTrueBBlock().getValueName().substring(1);
                preMap.get(((BrInstruction) instruction).getTrueBBlock().getValueName().substring(1)).add(mipsbBlock.getName());
                return new JInstruction("j    ", ((BrInstruction) instruction).getTrueBBlock().getValueName());
            } else {
                Value condition = ((BrInstruction) instruction).getCondition();
                MIPSRegister rs = containInv2mMap(condition, mipsbBlock) ? (MIPSRegister) v2mMap.get(condition) : putNewVGtoMap(condition, mipsFunction, mipsbBlock);
                mipsbBlock.addMIPSInstruction(new EInsrtruction("bnez ", rs, new MIPSLabel(((BrInstruction) instruction).getTrueBBlock().getValueName())));
                succMap.get(mipsbBlock.getName())[0] = ((BrInstruction) instruction).getTrueBBlock().getValueName().substring(1);
                succMap.get(mipsbBlock.getName())[1] = ((BrInstruction) instruction).getFalseBBlock().getValueName().substring(1);
                preMap.get(((BrInstruction) instruction).getTrueBBlock().getValueName().substring(1)).add(mipsbBlock.getName());
                preMap.get(((BrInstruction) instruction).getFalseBBlock().getValueName().substring(1)).add(mipsbBlock.getName());
                return new JInstruction("j    ", ((BrInstruction) instruction).getFalseBBlock().getValueName());
            }
        } else if (instruction instanceof CallInstruction) {    // 由于Call和Ret指令都是宏指令或者J指令，因此都需要手动添加use和def
            Function func = ((CallInstruction) instruction).getFunction();
            MIPSInstruction mipscall;
            MIPSRegister rd = new VirtualRegister();
            if ((func).isBuilt()) {
                mipscall = new MacroInstruction(func.getValueName().substring(1));
                mipscall.addDef(RegisterInfo.getPhysicalRegisterById(2));   // def v0
            } else {
                mipscall = new JInstruction("jal  ", func.getValueName());
                mipscall.addDef(RegisterInfo.getPhysicalRegisterById(31));  // def ra
            }
            for (int i = 0; i < 4; i++) { // def a0~3
                mipscall.addDef(RegisterInfo.getPhysicalRegisterById(i + 4));
            }
            int argnum = ((CallInstruction) instruction).getArgs().size();
            for (int i = 0; i < argnum; i++) {
                Value argument = ((CallInstruction) instruction).getArgs().get(i);
                if (i < 4) {
                    if (argument instanceof ConstantInt) {
                        MIPSImmediate rs = new MIPSImmediate(((ConstantInt) argument).getValue());
                        mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", RegisterInfo.getPhysicalRegisterById(4 + i), rs));
                    } else {
                        MIPSRegister rs = containInv2mMap(argument, mipsbBlock) ? (MIPSRegister) v2mMap.get(argument) : putNewVGtoMap(argument, mipsFunction, mipsbBlock);
                        mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", RegisterInfo.getPhysicalRegisterById(4 + i), rs));
                    }
                    mipscall.addUse(RegisterInfo.getPhysicalRegisterById(4 + i));
                } else {
                    MIPSRegister rs = containInv2mMap(argument, mipsbBlock) ? (MIPSRegister) v2mMap.get(argument) : putNewVGtoMap(argument, mipsFunction, mipsbBlock);
                    if (argument instanceof ConstantInt)
                        mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", rs, new MIPSImmediate(((ConstantInt) argument).getValue())));
                    int offset = -(argnum - i) * 4;
                    mipsbBlock.addMIPSInstruction(new RInstruction("sw   ", RegisterInfo.getPhysicalRegisterById(29), rs, offset));
                }
            }
            if (argnum > 4) {
                mipsbBlock.addMIPSInstruction(new IInstruction("subu ", RegisterInfo.getPhysicalRegisterById(29), RegisterInfo.getPhysicalRegisterById(29), new MIPSImmediate(4 * (argnum - 4))));
            }
            mipsbBlock.addMIPSInstruction(mipscall);
            if (argnum > 4) {
                mipsbBlock.addMIPSInstruction(new IInstruction("addu ", RegisterInfo.getPhysicalRegisterById(29), RegisterInfo.getPhysicalRegisterById(29), new MIPSImmediate(4 * (argnum - 4))));
            }
            Type returnType = ((((CallInstruction) instruction).getFunction())).getReturnType();
            mipscall.addDef(RegisterInfo.getPhysicalRegisterById(2));
            if (!(returnType instanceof VoidType)) {
                mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", rd, RegisterInfo.getPhysicalRegisterById(2)));
                putInv2mMap(instruction, rd);
            }
        } else if (instruction instanceof CompareInstruction) {
            Value l = ((CompareInstruction) instruction).getLeft(), r = ((CompareInstruction) instruction).getRight();
            MIPSOperand rl, rr;
            CompareType cond = ((CompareInstruction) instruction).getCompareType();
            String condname = cond.toMIPSSet();
            MIPSRegister rd = putNewVGtoMap(instruction, mipsFunction, mipsbBlock);
            if (l instanceof ConstantInt && !(r instanceof ConstantInt)) {
                rl = containInv2mMap(r, mipsbBlock) ? v2mMap.get(r) : putNewVGtoMap(r, mipsFunction, mipsbBlock);
                rr = new VirtualRegister();
                condname = cond.toOPMIPSSet();
                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", (MIPSRegister) rr, new MIPSImmediate(((ConstantInt) l).getValue())));
                return new RInstruction(condname + "  ", (MIPSRegister) rl, (MIPSRegister) rr, rd);
            } else {
                if (l instanceof ConstantInt) {
                    rl = new VirtualRegister();
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", (MIPSRegister) rl, new MIPSImmediate(((ConstantInt) l).getValue())));
                } else
                    rl = containInv2mMap(l, mipsbBlock) ? v2mMap.get(l) : putNewVGtoMap(l, mipsFunction, mipsbBlock);
                if (r instanceof ConstantInt) {
                    rr = new VirtualRegister();
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", (MIPSRegister) rr, new MIPSImmediate(((ConstantInt) r).getValue())));
                } else
                    rr = containInv2mMap(r, mipsbBlock) ? v2mMap.get(r) : putNewVGtoMap(r, mipsFunction, mipsbBlock);
                return new RInstruction(condname + "  ", (MIPSRegister) rl, (MIPSRegister) rr, rd);
            }
        } else if (instruction instanceof DivInstruction) {
            Value l = ((DivInstruction) instruction).getLeft(), r = ((DivInstruction) instruction).getRight();
            return buildMDMInstruction(instruction, mipsFunction, mipsbBlock, l, r, "div  ");
        } else if (instruction instanceof GEPInstruction) {
            Value base = ((GEPInstruction) instruction).getBase();
            MIPSOperand rs = containInv2mMap(base, mipsbBlock) ? v2mMap.get(base) : putNewVGtoMap(base, mipsFunction, mipsbBlock);
            if (base instanceof GlobalVar) {
                mipsbBlock.addMIPSInstruction(new EInsrtruction("la   ", (MIPSRegister) rs, new MIPSLabel(base.getValueName())));
            }
            if (((GEPInstruction) instruction).getSecondOff() == null) {    // gep ... 1
                Type baseType = ((GEPInstruction) instruction).getBaseType();
                Value off1 = ((GEPInstruction) instruction).getFirstOff();
                if (off1 instanceof ConstantInt) {
                    if (((ConstantInt) off1).getValue() != 0) {
                        MIPSRegister rd = putNewVGtoMap(instruction, mipsFunction, mipsbBlock);
                        return new IInstruction("addiu", (MIPSRegister) rs, rd, new MIPSImmediate(((ConstantInt) off1).getValue() * baseType.getSize()));
                    } else {
                        putInv2mMap(instruction, rs);
                    }
                } else {
                    MIPSRegister rd = putNewVGtoMap(instruction, mipsFunction, mipsbBlock);
                    MIPSRegister off = containInv2mMap(off1, mipsbBlock) ? (MIPSRegister) v2mMap.get(off1) : putNewVGtoMap(off1, mipsFunction, mipsbBlock);
//                    mipsbBlock.addMIPSInstruction(new IInstruction("mul  ", off, rd, new MIPSImmediate(baseType.getSize())));
                    MulOptimizer.mulOptimization(mipsbBlock, rd, off, new MIPSImmediate(baseType.getSize()));
                    return new RInstruction("addu ", rd, (MIPSRegister) rs, rd);
                }
            } else {    // gep ... 1 2
                Type baseType = ((GEPInstruction) instruction).getBaseType();
                Type elementType = ((ArrayType) baseType).getElementType();
                Value off1 = ((GEPInstruction) instruction).getFirstOff();
                Value off2 = ((GEPInstruction) instruction).getSecondOff();
                MIPSRegister rd = new VirtualRegister();
                v2mMap.put(instruction, rd);
                MIPSRegister tmp = new VirtualRegister();
                if (off1 instanceof ConstantInt) {
                    if (((ConstantInt) off1).getValue() != 0) {
                        mipsbBlock.addMIPSInstruction(new IInstruction("addiu", (MIPSRegister) rs, tmp, new MIPSImmediate(((ConstantInt) off1).getValue() * baseType.getSize())));
//                        rs = rd;
//                        rd = null;
//                        v2mMap.replace(instruction, rs);
                    } else {
                        mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", tmp, (MIPSRegister) rs));
                    }
                } else {
                    MIPSRegister off = containInv2mMap(off1, mipsbBlock) ? (MIPSRegister) v2mMap.get(off1) : putNewVGtoMap(off1, mipsFunction, mipsbBlock);
//                    mipsbBlock.addMIPSInstruction(new IInstruction("mul  ", off, off, new MIPSImmediate(baseType.getSize())));
                    MulOptimizer.mulOptimization(mipsbBlock, tmp, off, new MIPSImmediate(baseType.getSize()));
                    mipsbBlock.addMIPSInstruction(new RInstruction("addu ", (MIPSRegister) rs, tmp, tmp));
//                    rs = rd;
//                    rd = null;
//                    v2mMap.replace(instruction, rs);
                }
                if (off2 instanceof ConstantInt) {
                    if (((ConstantInt) off2).getValue() != 0) {
                        mipsbBlock.addMIPSInstruction(new IInstruction("addiu", tmp, rd, new MIPSImmediate(((ConstantInt) off2).getValue() * elementType.getSize())));
//                        v2mMap.replace(instruction, rd);
                    } else {
                        mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", rd, tmp));
                    }
                } else {
                    MIPSRegister off = containInv2mMap(off2, mipsbBlock) ? (MIPSRegister) v2mMap.get(off2) : putNewVGtoMap(off2, mipsFunction, mipsbBlock);
//                    mipsbBlock.addMIPSInstruction(new IInstruction("mul  ", off, off, new MIPSImmediate(elementType.getSize())));
                    MIPSRegister tmp2 = new VirtualRegister();
                    MulOptimizer.mulOptimization(mipsbBlock, tmp2, off, new MIPSImmediate(elementType.getSize()));
//                    v2mMap.replace(instruction, rd);
                    return new RInstruction("addu ", tmp2, tmp, rd);
                }
            }
        } else if (instruction instanceof LoadInstruction) {
            Value addr = ((LoadInstruction) instruction).getAddr();
            MIPSOperand rs = containInv2mMap(addr, mipsbBlock) ? v2mMap.get(addr) : putNewVGtoMap(addr, mipsFunction, mipsbBlock);
            if (addr instanceof GlobalVar) {
                mipsbBlock.addMIPSInstruction(new EInsrtruction("la   ", (MIPSRegister) rs, new MIPSLabel(addr.getValueName())));
            }
            MIPSRegister rd = new VirtualRegister();
            putInv2mMap(instruction, rd);
            return new RInstruction("lw   ", (MIPSRegister) rs, rd, 0);
        } else if (instruction instanceof ModInstruction) {
            Value l = ((ModInstruction) instruction).getLeft(), r = ((ModInstruction) instruction).getRight();
            return buildMDMInstruction(instruction, mipsFunction, mipsbBlock, l, r, "rem  ");
        } else if (instruction instanceof MultInstruction) {
            Value l = ((MultInstruction) instruction).getLeft(), r = ((MultInstruction) instruction).getRight();
            return buildMDMInstruction(instruction, mipsFunction, mipsbBlock, l, r, "mul  ");
        } else if (instruction instanceof RetInstruction) {
            if (mipsFunction.getName().equals("@main")) {
                return new MacroInstruction("end");
            } else if (!instruction.getValueType().isVoidTy()) {
                Value irRetValue = instruction.getValue(0);
                MIPSOperand rs = containInv2mMap(irRetValue, mipsbBlock) ? v2mMap.get(irRetValue) : putNewVGtoMap(irRetValue, mipsFunction, mipsbBlock);
                if (irRetValue instanceof ConstantInt) {
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", (MIPSRegister) rs, new MIPSImmediate(((ConstantInt) irRetValue).getValue())));
                }
                if (irRetValue != null) {
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", RegisterInfo.getPhysicalRegisterById(2), (MIPSRegister) rs));
                }
                putInv2mMap(instruction, (MIPSRegister) rs);
            }
            return new JInstruction("jr   ", true, mipsFunction);
        } else if (instruction instanceof StoreInstruction) {
            Value addr = ((StoreInstruction) instruction).getAddr();
            Value value = ((StoreInstruction) instruction).getValue();
            MIPSOperand rd, rs = containInv2mMap(addr, mipsbBlock) ? v2mMap.get(addr) : putNewVGtoMap(addr, mipsFunction, mipsbBlock);
            if (addr instanceof GlobalVar) {
                mipsbBlock.addMIPSInstruction(new EInsrtruction("la   ", (MIPSRegister) rs, new MIPSLabel(addr.getValueName())));
            }
            if (value instanceof ConstantInt) {
                rd = containInv2mMap(value, mipsbBlock) ? v2mMap.get(value) : putNewVGtoMap(value, mipsFunction, mipsbBlock);
                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", (MIPSRegister) rd, new MIPSImmediate(((ConstantInt) ((StoreInstruction) instruction).getValue()).getValue())));
            } else
                rd = containInv2mMap(value, mipsbBlock) ? v2mMap.get(value) : putNewVGtoMap(value, mipsFunction, mipsbBlock);
            return new RInstruction("sw   ", (MIPSRegister) rs, (MIPSRegister) rd, 0);
        } else if (instruction instanceof SubInstruction) {
            Value l = ((SubInstruction) instruction).getLeft(), r = ((SubInstruction) instruction).getRight();
            MIPSRegister rd = new VirtualRegister();
            putInv2mMap(instruction, rd);
            MIPSRegister rs = containInv2mMap(l, mipsbBlock) ? (MIPSRegister) v2mMap.get(l) : putNewVGtoMap(l, mipsFunction, mipsbBlock);
            MIPSRegister rt = containInv2mMap(r, mipsbBlock) ? (MIPSRegister) v2mMap.get(r) : putNewVGtoMap(r, mipsFunction, mipsbBlock);
            if (l instanceof ConstantInt && r instanceof ConstantInt) {
                return new EInsrtruction("li    ", rd, new MIPSImmediate(((ConstantInt) l).getValue() - ((ConstantInt) r).getValue()));
            } else if (r instanceof ConstantInt) {
                return new IInstruction("addiu", rs, rd, new MIPSImmediate(-((ConstantInt) r).getValue()));
            } else if (l instanceof ConstantInt) {
                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", rs, new MIPSImmediate(((ConstantInt) l).getValue())));
                return new RInstruction("subu ", rs, rt, rd);
            } else {
                return new RInstruction("subu ", rs, rt, rd);
            }
        } else if (instruction instanceof ZEXTInstruction) {
            putInv2mMap(instruction, (MIPSRegister) v2mMap.get(((ZEXTInstruction) instruction).getChangee()));
        }
//        else if (instruction instanceof PhiInstruction) {
//
//        }  // phi 要跨块处理
        return null;
    }

    private MIPSInstruction buildMDMInstruction(Instruction instruction, MIPSFunction mipsFunction, MIPSBBlock mipsbBlock, Value l, Value r, String op) {
        MIPSRegister rd = new VirtualRegister();
        putInv2mMap(instruction, rd);
        MIPSRegister rs = containInv2mMap(l, mipsbBlock) ? (MIPSRegister) v2mMap.get(l) : putNewVGtoMap(l, mipsFunction, mipsbBlock);
        MIPSRegister rt = containInv2mMap(r, mipsbBlock) ? (MIPSRegister) v2mMap.get(r) : putNewVGtoMap(r, mipsFunction, mipsbBlock);
        if (l instanceof ConstantInt && r instanceof ConstantInt) {
            return new EInsrtruction("li   ", rd, new MIPSImmediate(calc(((ConstantInt) l).getValue(), op, ((ConstantInt) r).getValue())));
        } else if (l instanceof ConstantInt || r instanceof ConstantInt) {
            MIPSImmediate imm = null;
            if (l instanceof ConstantInt) {
                imm = new MIPSImmediate(((ConstantInt) l).getValue());
//                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", rs, imm));
                if (op.equals("mul  "))
                    MulOptimizer.mulOptimization(mipsbBlock, rd, rt, imm);
                else {
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", rs, imm));
                    return new RInstruction(op, rs, rt, rd);
                }
            } else {
                imm = new MIPSImmediate(((ConstantInt) r).getValue());
//                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", rt, imm));
                if (op.equals("mul  "))
                    MulOptimizer.mulOptimization(mipsbBlock, rd, rs, imm);
                else if (op.equals("div  ")) {
                    MulOptimizer.divOptimization(mipsbBlock, rd, rs, imm);
                } else if (op.equals("rem  ")) {
                    MulOptimizer.remOptimization(mipsbBlock, rd, rs, imm);
                } else
                    return new IInstruction(op, rs, rd, imm);
            }
            return null;
        } else {
            return new RInstruction(op, rs, rt, rd);
        }
    }

    private MIPSRegister buildArgument(Argument irArgument, MIPSFunction function, MIPSBBlock mipsbBlock) {
        if (v2mMap.containsKey(irArgument))
            return (MIPSRegister) v2mMap.get(irArgument);
        int rank = Integer.parseInt(irArgument.getValueName().substring(2));
        MIPSRegister rd = new VirtualRegister();
        putInv2mMap(irArgument, rd);
        if (rank < 4) {
            if (function.getBlocks().size() > 0)
                function.getBlocks().get(0).addMIPSInstructionToHead(new EInsrtruction("move ", rd, RegisterInfo.getPhysicalRegisterById(4 + rank)));
            else
                mipsbBlock.addMIPSInstructionToHead(new EInsrtruction("move ", rd, RegisterInfo.getPhysicalRegisterById(4 + rank)));
        } else {
            int stackPos = rank - 4;
            MIPSInstruction loadArg = new RInstruction("lw   ", RegisterInfo.getPhysicalRegisterById(29), rd, stackPos * 4);
            if (function.getBlocks().size() > 0)
                function.getBlocks().get(0).addMIPSInstructionToHead(loadArg);
            else
                mipsbBlock.addMIPSInstructionToHead(loadArg);
            function.addArgInstructions(loadArg);
        }
        return rd;
    }

    private int calc(int value, String op, int value1) {
        switch (op) {
            case "mul  " -> {
                return value * value1;
            }
            case "div  " -> {
                return value / value1;
            }
            case "rem  " -> {
                return value % value1;
            }
            case "add  " -> {
                return value + value1;
            }
            case "sub  " -> {
                return value - value1;
            }
            default -> {
                return 0;
            }
        }
    }

    private void buildPhi(Function irFunction, MIPSFunction mipsFunction) {
        for (BasicBlock block : irFunction.getBlocks()) {
            for (PhiInstruction phi : block.getPhiInstructions()) {
                ArrayList<Map.Entry<Value, BasicBlock>> entries = phi.getEntry();
                MIPSRegister tmpreg = new VirtualRegister();
                MIPSBBlock curBlock = n2mbMap.get(block.getValueName().substring(1));
                MIPSRegister nreg = containInv2mMap(phi, curBlock) ? (MIPSRegister) v2mMap.get(phi) : putNewVGtoMap(phi, mipsFunction, curBlock);
                curBlock.addMIPSInstructionToHead(new EInsrtruction("move ", nreg, tmpreg));
                for (Map.Entry<Value, BasicBlock> entry : entries) {
                    Value phiv = entry.getKey();
                    if (!(phiv instanceof ConstantInt)) {
                        MIPSRegister mphiv = containInv2mMap(phiv, curBlock) ? (MIPSRegister) v2mMap.get(phiv) : putNewVGtoMap(phiv, mipsFunction, curBlock);
                        MIPSBBlock mphib = n2mbMap.get(entry.getValue().getValueName().substring(1));
                        mphib.addPhiMove(new EInsrtruction("move ", tmpreg, mphiv));
                    } else {
                        MIPSBBlock mphib = n2mbMap.get(entry.getValue().getValueName().substring(1));
                        mphib.addPhiMove(new EInsrtruction("li   ", tmpreg, new MIPSImmediate(((ConstantInt) phiv).getValue())));
                    }

                }
            }
        }
        for (MIPSBBlock block : mipsFunction.getBlocks()) {
            block.fixPhiMove();
        }
    }

    public void printMIPS(String loc) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(".macro getint\n");
        sb.append("\tli $v0, 5\n");
        sb.append("\tsyscall\n");
        sb.append(".end_macro\n");
        sb.append(".macro putint\n");
        sb.append("\tli $v0, 1\n");
        sb.append("\tsyscall\n");
        sb.append(".end_macro\n");
        sb.append(".macro putstr\n");
        sb.append("\tli $v0, 4\n");
        sb.append("\tsyscall\n");
        sb.append(".end_macro\n");
        sb.append(".macro end\n");
        sb.append("\tli $v0, 10\n");
        sb.append("\tsyscall\n");
        sb.append(".end_macro\n");
        sb.append(mipsModule);
        IO.write(Config.outFileMap.get(loc), sb.toString());
//        System.out.println(sb.toString());
    }
}
