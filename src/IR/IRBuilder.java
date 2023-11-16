package IR;

import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Constant;
import IR.BasicValue.Users.Constants.ConstantArray;
import IR.BasicValue.Users.Constants.ConstantStr;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.GlobalValues.GlobalVar;
import IR.BasicValue.Users.Instructions.*;
import IR.Types.*;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

public class IRBuilder {
    private static int bbname = 0;
    private static int iname = 0;
    private static final HashMap<String, GlobalVar> globalStrMap = new HashMap<>();
    private static int strname = 0;

    public GlobalVar buildGlobalVariable(String ident, Constant initValue, boolean isConst) {
        GlobalVar globalVariable = new GlobalVar('@' + ident, initValue, isConst);
        Module.module.addGlobalValue(globalVariable);
        return globalVariable;
    }

    public Function buildFunction(String functionName, FunctionType functionType) {
        Function f = new Function('@' + functionName, functionType);
        Module.module.addFunction(f);
        return f;
    }

    public Function buildFunction(String functionName, FunctionType functionType, boolean isBuilt) {
        Function f = new Function('@' + functionName, functionType, isBuilt);
        Module.module.addFunction(f);
        return f;
    }

    public BasicBlock buildBlock(Function function) {
        BasicBlock bb = new BasicBlock("%bb" + bbname++, function);
        function.insertBBlock(bb);
        return bb;
    }

    public void buildRet(BasicBlock bb) {
        RetInstruction ret = new RetInstruction(bb);
        bb.insertInstruction(ret);
    }

    public void buildRet(BasicBlock bb, Value retvalue) {
        RetInstruction ret = new RetInstruction(bb, retvalue);
        bb.insertInstruction(ret);
    }

    public Value buildAddExp(BasicBlock curBBlock, Value mul, Token op, Value add) {
        BinaryInstruction bi = null;
        if (op.getTokenType() == TokenType.PLUS) {
            bi = new AddInstruction("%ins_" + iname++, curBBlock, mul, add);
            curBBlock.insertInstruction(bi);
        } else if (op.getTokenType() == TokenType.MINU) {
            bi = new SubInstruction("%ins_" + iname++, curBBlock, mul, add);
            curBBlock.insertInstruction(bi);
        }
        return bi;
    }

    public Value buildMulExp(BasicBlock curBBlock, Value unary, Token op, Value mul) {
        BinaryInstruction bi = null;
        if (op.getTokenType() == TokenType.MULT) {
            bi = new MultInstruction("%ins_" + iname++, curBBlock, unary, mul);
            curBBlock.insertInstruction(bi);
        } else if (op.getTokenType() == TokenType.DIV) {
            bi = new DivInstruction("%ins_" + iname++, curBBlock, unary, mul);
            curBBlock.insertInstruction(bi);
        } else if (op.getTokenType() == TokenType.MOD) {
            bi = new ModInstruction("%ins_" + iname++, curBBlock, unary, mul);
            curBBlock.insertInstruction(bi);
        }

        return bi;
    }


    public Value buildCompareIns(BasicBlock parent, CompareType cmptp, Value left, Value right) {
        CompareInstruction compareInstruction = new CompareInstruction("%ins_" + iname++, parent, cmptp, left, right);
        parent.insertInstruction(compareInstruction);
        return compareInstruction;
    }

    public AllocInstruction buildAlloca(BasicBlock parent, Type allocatedType) {
        BasicBlock realParent = parent.getValueParent().getHeadBBlock();
        AllocInstruction alloca = new AllocInstruction("%ins_" + iname++, realParent, allocatedType);
        realParent.insertAllocaInstruction(alloca);
        return alloca;
    }

    public void buildStore(BasicBlock parent, Value content, Value addr) {
        StoreInstruction store = new StoreInstruction(parent, content, addr);
        parent.insertInstruction(store);
    }

    public LoadInstruction buildLoad(BasicBlock parent, Value addr) {
        LoadInstruction load = new LoadInstruction("%ins_" + iname++, parent, addr);
        parent.insertInstruction(load);
        return load;
    }

    public CallInstruction buildCall(BasicBlock parent, Function function, ArrayList<Value> args) {
        String name = "";
        if (function.getReturnType() instanceof VoidType) {
        } else {
            name += ("%ins_" + iname++);
        }
        CallInstruction ci = new CallInstruction(name, parent, function, args);
        parent.insertInstruction(ci);
        return ci;
    }

    public GlobalVar buildGlobalStr(ConstantStr constantStr) {
        if (globalStrMap.containsKey(constantStr.getContent())) {
            return globalStrMap.get(constantStr.getContent());
        } else {
            GlobalVar globalVariable = new GlobalVar("@str" + strname++, constantStr, true);
            Module.module.addGlobalValue(globalVariable);
            globalStrMap.put(constantStr.getContent(), globalVariable);
            return globalVariable;
        }
    }

    public GEPInstruction buildGEPIns(BasicBlock parent, Value base, Value... offs) {
        GEPInstruction gep = null;
        if (offs.length == 1) {
            gep = new GEPInstruction("%ins_" + iname++, parent, base, offs[0]);
        } else {
            gep = new GEPInstruction("%ins_" + iname++, parent, base, offs[0], offs[1]);
        }
        parent.insertInstruction(gep);
        return gep;
    }

    public BrInstruction buildBr(BasicBlock parent, BasicBlock target) {    // 无条件
        BrInstruction br = new BrInstruction(parent, target);
        parent.insertInstruction(br);
        return br;
    }

    public BrInstruction buildBr(BasicBlock parent, Value condition, BasicBlock trueBlock, BasicBlock falseBlock) {  // 有条件
        BrInstruction br = new BrInstruction(parent, condition, trueBlock, falseBlock);
        parent.insertInstruction(br);
        return br;
    }

    public CompareInstruction buildIcmpIns(BasicBlock parent, CompareType condition, Value src1, Value src2) {
        CompareInstruction icmp = new CompareInstruction("%ins_" + iname++, parent, condition, src1, src2);
        parent.insertInstruction(icmp);
        return icmp;
    }

    public ZEXTInstruction buildZextIns(BasicBlock parent, Value changee) {
        ZEXTInstruction zext = new ZEXTInstruction("%ins_" + iname++, parent, changee);
        parent.insertInstruction(zext);
        return zext;
    }

    public AllocInstruction buildConstAlloca(BasicBlock parent, Type allocated, ConstantArray initVal) {
        BasicBlock realParent = parent.getValueParent().getHeadBBlock();    // 与buildAlloca类似,不同的是加上了常数数组的初值，方便记录
        AllocInstruction ans = new AllocInstruction("%ins_" + iname++, realParent, allocated, initVal);
        realParent.insertAllocaInstruction(ans);
        return ans;
    }

    private static int phiname = 0;

    public PhiInstruction buildPhi(Type allocated, BasicBlock parent) {
        int from = parent.getPredecessors().size();
        PhiInstruction phi = new PhiInstruction("%phi_" + phiname++, parent, allocated, from, new Value[from * 2]);
        parent.insertPhiInstruction(phi);
        return phi;
    }
}
