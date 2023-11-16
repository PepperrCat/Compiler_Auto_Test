package IR;

import Config.Config;
import IR.BasicValue.Argument;
import IR.BasicValue.BasicBlock;
import IR.BasicValue.Users.Array;
import IR.BasicValue.Users.Constant;
import IR.BasicValue.Users.Constants.ConstantArray;
import IR.BasicValue.Users.Constants.ConstantInt;
import IR.BasicValue.Users.Constants.ConstantStr;
import IR.BasicValue.Users.Constants.ConstantZero;
import IR.BasicValue.Users.GlobalValues.Function;
import IR.BasicValue.Users.GlobalValues.GlobalVar;
import IR.BasicValue.Users.Instruction;
import IR.BasicValue.Users.Instructions.*;
import IR.Types.*;
import io.IO;
import node.Node;
import node.NodeType;
import token.Token;
import token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import static IR.Module.module;

public class LLVMBuilder {
    private final Node compUnit;
    private boolean global;
    private static Function curFunction;
    private static BasicBlock curBBlock;
    public static IRBuilder irBuilder;
    private static boolean isLastFunc = false;
    private static boolean pointerLoad = false;
    private static boolean I32Switch = false;
    private static boolean isCal = false;
    private static Stack<ArrayList<BasicBlock>> loopBlock = new Stack<>();
    private static SymbolTableInIR stack = new SymbolTableInIR();

    public LLVMBuilder(Node compUnit) {
        this.compUnit = compUnit;
        irBuilder = new IRBuilder();
    }

    public boolean isGlobal() {
        return global;
    }

    public static boolean isIsCal() {
        return isCal;
    }

    private static void setIsCal(boolean isCal) {
        LLVMBuilder.isCal = isCal;
    }

    public void buildLLVM() {
        ArrayList<Type> printfArgs = new ArrayList<>();
        printfArgs.add(new PointerType(new IntType(8)));
        Function.putstr = irBuilder.buildFunction("putstr", new FunctionType(printfArgs, new VoidType()), true);
        ArrayList<Type> putintArgs = new ArrayList<>();
        putintArgs.add(new IntType(32));
        Function.putint = irBuilder.buildFunction("putint", new FunctionType(putintArgs, new VoidType()), true);
        Function.getint = irBuilder.buildFunction("getint", new FunctionType(new ArrayList<>(), new IntType(32)), true);
        visitAST(compUnit);
    }

    public void printLLVM() throws IOException {
        for (GlobalVar globalVar : module.getGlobalVariables()) {
            IO.write(Config.outFileMap.get("llvm"), globalVar.toString());
        }
        for (Function function : module.getFunctions()) {
            IO.write(Config.outFileMap.get("llvm"), function.toString());
        }
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    private Value visitAST(Node node) {
        switch (node.getNodeType()) {
            case CompUnit -> {
                for (Node node1 : node.getSubNodes()) {
                    if (node1.getNodeType() == NodeType.Decl) {
                        setGlobal(true);
                        visitAST(node1);
                        setGlobal(false);
                    } else {
                        visitAST(node1);
                    }
                }
            }
            case Decl, ConstDecl, BType, VarDecl -> {
                for (Node node1 : node.getSubNodes())
                    visitAST(node1);
            }
            case ConstDef -> {
                String ident = node.getSubNodes().get(0).getToken().getTokenContent();
                if (node.getSubNodes().size() <= 3) {
//                    GlobalVar globalVar = irBuilder.buildGlobalVariable(ident, (ConstantInt) visitAST(node.getSubNodes().get(2)), true);
                    stack.addValue(ident, visitAST(node.getSubNodes().get(2)));  // 常量不需要定义，数组才可能需要
                } else { // 数组没写
                    ArrayList<Integer> dimensions = new ArrayList<>();
                    for (int i = 0; i < node.getSubNodes().size() / 3 - 1; i++) {
                        dimensions.add(((ConstantInt) visitAST(node.getSubNodes().get(i * 3 + 2))).getValue());
                    }
                    Node constInit = node.getSubNodes().get(node.getSubNodes().size() - 1);
                    constInit.setDimensions(dimensions);
                    Value constInitVal = visitAST(constInit);
                    if (isGlobal()) {
                        // 全局
                        stack.addValue(ident, irBuilder.buildGlobalVariable(ident, (Constant) constInitVal, true));
                    } else {
                        AllocInstruction alloca = irBuilder.buildConstAlloca(curBBlock, new ArrayType(IntType.I32, dimensions), (ConstantArray) constInitVal);
                        stack.addValue(ident, alloca);
                        int dim = 0;
                        Instruction gep = alloca;
                        while (dim < dimensions.size()) {   // 递推将高维的数组指针降维成初地址
                            gep = irBuilder.buildGEPIns(curBBlock, gep, ConstantInt.ZERO, ConstantInt.ZERO);
                            dim++;
                        }
                        ArrayList<Constant> elemlist = ((ConstantArray) constInitVal).getElementList();
                        irBuilder.buildStore(curBBlock, elemlist.get(0), gep);

                        for (int i = 1; i < ((ConstantArray) constInitVal).getSize() / 4; i++) {
                            GEPInstruction gepcur = irBuilder.buildGEPIns(curBBlock, gep, new ConstantInt(i, 32));
                            irBuilder.buildStore(curBBlock, elemlist.get(i), gepcur);
                        }
                    }
                }
            }
            case ConstInitVal -> {
                if (node.getSubNodes().size() == 1) {
                    return visitAST(node.getSubNodes().get(0));
                } else {   // 数组处理一下，全局还是非全局好像没啥区别0.0
                    ArrayList<Integer> dims = node.getDimensions();
                    ArrayList<Constant> array = new ArrayList<>();
                    if (dims.size() == 1) {
                        for (int i = 0; i < (node.getSubNodes().size() - 1) / 2; i++) {
                            array.add((ConstantInt) visitAST(node.getSubNodes().get(i * 2 + 1)));
                        }
                    } else {    // 高维降维继续visitAST
                        for (int i = 0; i < (node.getSubNodes().size() - 1) / 2; i++) {
                            node.getSubNodes().get(i * 2 + 1).setDimensions(new ArrayList<>(dims.subList(1, dims.size())));
                            array.add((ConstantArray) visitAST(node.getSubNodes().get(i * 2 + 1)));
                        }
                    }
                    return new ConstantArray(array);
                }
            }
            case VarDef -> {
                String ident = node.getSubNodes().get(0).getToken().getTokenContent();
                if (node.getSubNodes().size() <= 3) {
                    if (isGlobal()) {
                        if (node.getSubNodes().size() == 3) {
                            ConstantInt c = (ConstantInt) visitAST(node.getSubNodes().get(2));
                            GlobalVar globalVar = irBuilder.buildGlobalVariable(ident, c, false);
                            stack.addValue(ident, globalVar);
                        } else {
                            GlobalVar globalVariable = irBuilder.buildGlobalVariable(ident, new ConstantInt(0, 32), false);
                            stack.addValue(ident, globalVariable);
                        }
                    } else {
                        // 为这个变量分配空间
                        AllocInstruction alloca = irBuilder.buildAlloca(curBBlock, IntType.I32);
                        // 从这里可以看出，可以从符号表这种查询到的东西是一个指针，即 int*
                        stack.addValue(ident, alloca);
                        // "当不含有 '=' 和初始值时,其运行时实际初值未定义"
                        if (node.getSubNodes().size() == 3) {
                            irBuilder.buildStore(curBBlock, visitAST(node.getSubNodes().get(2)), alloca);
                        }
                    }
                } else {// 数组
                    ArrayList<Integer> dimensions = new ArrayList<>();
                    for (int i = 0; i < node.getSubNodes().size(); i++) {
                        Node node1 = node.getSubNodes().get(i);
                        if (node1.getNodeType() == NodeType.ConstExp)
                            dimensions.add(((ConstantInt) visitAST(node1)).getValue());
                    }
                    Node initVal = node.getSubNodes().get(node.getSubNodes().size() - 1);
                    initVal.setDimensions(new ArrayList<>(dimensions.subList(0, dimensions.size()))); // 千万不能改变dimensions
                    Value initArray = visitAST(initVal);
                    if (isGlobal()) {
                        // 全局
                        if (initVal.getNodeType() == NodeType.InitVal) {

                            if (dimensions.size() == 1) {
                                stack.addValue(ident, irBuilder.buildGlobalVariable(ident, (Constant) initArray, false));
                            } else {
                                ArrayList<Constant> colArray = new ArrayList<>();
                                for (int i = 0; i < dimensions.get(0); i++) {
                                    ArrayList<Constant> rowArray = new ArrayList<>();
                                    boolean checkzero = true;
                                    for (int j = 0; j < dimensions.get(1); j++) {
                                        Constant add = ((ConstantArray) initArray).getElementList().get(dimensions.get(1) * i + j);
                                        rowArray.add(add);
                                        if (((ConstantInt) add).getValue() != 0)
                                            checkzero = false;
                                    }
                                    if (checkzero)
                                        colArray.add(new ConstantZero(new ArrayType(rowArray.get(0).getValueType(), rowArray.size())));
                                    else
                                        colArray.add(new ConstantArray(rowArray));
                                }
                                initArray = new ConstantArray(colArray);
                                stack.addValue(ident, irBuilder.buildGlobalVariable(ident, (Constant) initArray, false));
                            }
                        }
                        // 全局无初始值的数组，那么就初始化为 0
                        else {
                            ConstantZero zeroInitializer = new ConstantZero(new ArrayType(IntType.I32, dimensions));
                            GlobalVar globalVariable = irBuilder.buildGlobalVariable(ident, zeroInitializer, false);
                            stack.addValue(ident, globalVariable);
                        }
                    } else {
                        AllocInstruction alloca = irBuilder.buildAlloca(curBBlock, new ArrayType(IntType.I32, dimensions));
                        stack.addValue(ident, alloca);
                        if (initVal.getNodeType() == NodeType.InitVal) {
                            int dim = 0;
                            Instruction gep = alloca;
                            while (dim < dimensions.size()) {   // 递推将高维的数组指针降维成初地址
                                gep = irBuilder.buildGEPIns(curBBlock, gep, ConstantInt.ZERO, ConstantInt.ZERO);
                                dim++;
                            }
                            ArrayList<Value> elemlist = ((Array) initArray).getElementList();
                            irBuilder.buildStore(curBBlock, elemlist.get(0), gep);
                            for (int i = 1; i < ((Array) initArray).getSize() / 4; i++) {
                                GEPInstruction gepcur = irBuilder.buildGEPIns(curBBlock, gep, new ConstantInt(i, 32));
                                irBuilder.buildStore(curBBlock, elemlist.get(i), gepcur);
                            }
                        }
                    }
                }
            }
            case InitVal -> {
                if (node.getSubNodes().size() == 1) {
                    if (isGlobal()) {
                        setIsCal(true);
                        Value ret = visitAST(node.getSubNodes().get(0));
                        setIsCal(false);
                        return ret;
                    } else
                        return visitAST(node.getSubNodes().get(0));
                } else {   // 数组处理一下，全局还是非全局好像没啥区别0.0
                    ArrayList<Integer> dims = node.getDimensions();
                    ArrayList<Value> array = new ArrayList<>();
                    if (dims.size() == 1) {
                        int i;
                        for (i = 0; i < (node.getSubNodes().size() - 1) / 2; i++) {
                            if (isGlobal()) {
                                setIsCal(true);
                                array.add(visitAST(node.getSubNodes().get(i * 2 + 1)));
                                setIsCal(false);
                            } else
                                array.add(visitAST(node.getSubNodes().get(i * 2 + 1)));
                        }
                        for (; i < dims.get(0); i++) {
                            array.add(ConstantInt.ZERO);
                        }
                    } else {    // 高维降维继续visitAST
                        for (int i = 0; i < (node.getSubNodes().size() - 1) / 2; i++) {
                            node.getSubNodes().get(i * 2 + 1).setDimensions(new ArrayList<>(dims.subList(1, dims.size())));
                            array.add(visitAST(node.getSubNodes().get(i * 2 + 1)));
                        }
                    }
                    if (isGlobal()) {
                        ArrayList<Constant> constants = new ArrayList<>();
                        for (Value v : array) {
                            constants.add((Constant) v);
                        }
                        return new ConstantArray(constants);
                    } else {
                        return new Array(array);
                    }
                }
            }
            case FuncDef -> {
                String ident = node.getSubNodes().get(1).getToken().getTokenContent();
                Type returnType = node.getSubNodes().get(0).getSubNodes().get(0).getToken().getTokenType() == TokenType.VOIDTK ? new VoidType() : new IntType(32);
                ArrayList<Type> argsType = new ArrayList<>();
                if (node.getSubNodes().size() > 5) {
                    Node fparams = node.getSubNodes().get(3);
                    for (int i = 0; i < fparams.getSubNodes().size(); i += 2) {
                        Node fparam = fparams.getSubNodes().get(i);
                        if (fparam.getSubNodes().size() == 2)
                            argsType.add(new IntType(32));
                        else {
                            Type argType = new IntType(32);
                            for (int j = fparam.getSubNodes().size() - 1; j >= 0; j--) {
                                if (fparam.getSubNodes().get(j).getNodeType() == NodeType.ConstExp) {
                                    setIsCal(true);
                                    argType = new ArrayType(argType, ((ConstantInt) visitAST(fparam.getSubNodes().get(j))).getValue());
                                    setIsCal(false);
                                }
                            }
                            argType = new PointerType(argType);
                            argsType.add(argType);
                        }

                    }
                }
                curFunction = irBuilder.buildFunction(ident, new FunctionType(argsType, returnType));
                stack.addValue(ident, curFunction);
                BasicBlock entryBlock = irBuilder.buildBlock(curFunction);
                stack.pushToTable();
                curBBlock = entryBlock;
                if (node.getSubNodes().size() > 5) {
                    Node fparams = node.getSubNodes().get(3);
                    ArrayList<Argument> args = curFunction.getArguments();
                    for (int i = 0; i < fparams.getSubNodes().size(); i += 2) {
                        String paramname = fparams.getSubNodes().get(i).getSubNodes().get(1).getToken().getTokenContent();
                        Argument argument = args.get(i / 2);
                        AllocInstruction alloca = irBuilder.buildAlloca(curBBlock, argument.getValueType());
                        irBuilder.buildStore(curBBlock, argument, alloca);
                        stack.addValue(paramname, alloca);
                    }
                }
                isLastFunc = true;
                visitAST(node.getSubNodes().get(node.getSubNodes().size() - 1));
                isLastFunc = false;
                Instruction lastInstruction = curBBlock.getLastInstruction();
                // 和main函数一样
                if (!(lastInstruction instanceof RetInstruction || lastInstruction instanceof BrInstruction)) {
                    if (curFunction.getReturnType() instanceof VoidType) {
                        irBuilder.buildRet(curBBlock);
                    } else {
                        irBuilder.buildRet(curBBlock, ConstantInt.ZERO);
                    }
                }
                stack.popFromTable();
            }
            case MainFuncDef -> {
                ArrayList<Type> argsType = new ArrayList<>();
                curFunction = irBuilder.buildFunction("main", new FunctionType(argsType, new IntType(32)));
                stack.addValue("main", curFunction);
                BasicBlock eBBlock = irBuilder.buildBlock(curFunction);
                stack.pushToTable();
                curBBlock = eBBlock;
                isLastFunc = true;
                visitAST(node.getSubNodes().get(4));
                Instruction lastInstruction = curBBlock.getLastInstruction();
                if (!(lastInstruction instanceof RetInstruction || lastInstruction instanceof BrInstruction)) {
                    irBuilder.buildRet(curBBlock, new ConstantInt(0, 32));
                }
                stack.popFromTable();
            }
            case FuncType -> {
            }
            case FuncFParams -> {

            }
            case FuncFParam -> {
            }
            case Block -> {
                if (!isLastFunc) {
                    stack.pushToTable();
                }
                for (Node node1 : node.getSubNodes()) {
                    if (node1.getNodeType() == NodeType.BlockItem) {
                        visitAST(node1);
                    }
                }
                stack.popFromTable();
            }
            case Stmt -> {
                Node node1 = node.getSubNodes().get(0);
                if (node1.getNodeType() == NodeType.End) {
                    switch (node1.getToken().getTokenType()) {
                        case RETURNTK -> {
                            if (node.getSubNodes().size() == 3) {
                                Value v = visitAST(node.getSubNodes().get(1));
                                irBuilder.buildRet(curBBlock, v);
                            } else {
                                irBuilder.buildRet(curBBlock);
                            }
                            curBBlock = new BasicBlock();
                        }
                        case PRINTFTK -> {
                            ArrayList<Value> putintArgs = new ArrayList<>();
                            for (Node node2 : node.getSubNodes()) {
                                Value v = visitAST(node2);
                                if (v != null)
                                    putintArgs.add(v);
                            }
                            String formatString = node.getSubNodes().get(2).getToken().getTokenContent();
                            formatString = formatString.replace("\"", "");
                            String d = Character.toString(200);
                            formatString = formatString.replace("%d", d);
                            StringTokenizer tokenizer = new StringTokenizer(formatString, d, true);
                            int argIndex = 0;
                            while (tokenizer.hasMoreTokens()) {
                                String token = tokenizer.nextToken();
                                if (token.equals(d)) {
                                    ArrayList<Value> putintArg = new ArrayList<>();
                                    putintArg.add(putintArgs.get(argIndex++));
                                    irBuilder.buildCall(curBBlock, Function.putint, putintArg);
                                } else {   // str
                                    GlobalVar globalStr = irBuilder.buildGlobalStr(new ConstantStr(token.replace("\\n", "\n")));
                                    // putstr 要求i8*, 通过GEP返回i8*才能使用
                                    GEPInstruction elementPtr = irBuilder.buildGEPIns(curBBlock, globalStr, ConstantInt.ZERO, ConstantInt.ZERO);
                                    irBuilder.buildCall(curBBlock, Function.putstr, new ArrayList<>() {{
                                        add(elementPtr);
                                    }});
                                }
                            }
                        }
                        case IFTK -> {
                            BasicBlock trueBBlock = irBuilder.buildBlock(curFunction);
                            BasicBlock nextBBlock = irBuilder.buildBlock(curFunction);
                            BasicBlock falseBBlock = null;
                            if (node.getSubNodes().size() == 5) {
                                falseBBlock = nextBBlock;
                            } else {
                                falseBBlock = irBuilder.buildBlock(curFunction);
                            }
                            node.getSubNodes().get(2).setFalseBBlock(falseBBlock);
                            node.getSubNodes().get(2).setTrueBBlock(trueBBlock);
                            visitAST(node.getSubNodes().get(2));
                            curBBlock = trueBBlock;
                            visitAST(node.getSubNodes().get(4));
                            irBuilder.buildBr(curBBlock, nextBBlock);   // 跳转

                            if (node.getSubNodes().size() == 7) {
                                curBBlock = falseBBlock;
                                visitAST(node.getSubNodes().get(6));
                                irBuilder.buildBr(curBBlock, nextBBlock);
                            }
                            curBBlock = nextBBlock;
                        }
                        case FORTK -> {
                            Node startforstmt = node.getSubNodes().get(2);
                            Node cond = startforstmt.getNodeType() == NodeType.ForStmt ? node.getSubNodes().get(4) : node.getSubNodes().get(3);
                            Node endforstmt = node.getSubNodes().get(node.getSubNodes().size() - 3);
                            Node stmt = node.getSubNodes().get(node.getSubNodes().size() - 1);
                            if (startforstmt.getNodeType() == NodeType.ForStmt)
                                visitAST(startforstmt);
                            BasicBlock condBBlock = null;
                            BasicBlock bodyBBlock = irBuilder.buildBlock(curFunction);
                            BasicBlock endBBlock = irBuilder.buildBlock(curFunction);

                            BasicBlock nextBBlock = irBuilder.buildBlock(curFunction);
                            // 此处需要记录nextBBlock和endBBlock以便break和continue使用，最好用栈式记录，这样就知道是break到哪了
                            loopBlock.push(new ArrayList<>() {{
                                add(nextBBlock);
                                add(endBBlock);
                            }});
                            if (cond.getNodeType() == NodeType.Cond) {
                                condBBlock = irBuilder.buildBlock(curFunction);
                                irBuilder.buildBr(curBBlock, condBBlock);
                                curBBlock = condBBlock;
                                cond.setTrueBBlock(bodyBBlock);
                                cond.setFalseBBlock(nextBBlock);
                                visitAST(cond);
                            } else {
                                irBuilder.buildBr(curBBlock, bodyBBlock);
                            }
                            curBBlock = bodyBBlock;
                            visitAST(stmt);
//                            if (endforstmt.getNodeType() == NodeType.ForStmt) {
                            irBuilder.buildBr(curBBlock, endBBlock);
                            curBBlock = endBBlock;
                            visitAST(endforstmt);
//                            }
                            if (cond.getNodeType() == NodeType.Cond) {
                                irBuilder.buildBr(curBBlock, condBBlock);
                            } else {
                                irBuilder.buildBr(curBBlock, bodyBBlock);
                            }
                            loopBlock.pop();
                            curBBlock = nextBBlock;
                        }
                        case BREAKTK -> {
                            irBuilder.buildBr(curBBlock, loopBlock.peek().get(0));
                            curBBlock = new BasicBlock();
                        }
                        case CONTINUETK -> {
                            irBuilder.buildBr(curBBlock, loopBlock.peek().get(1));
                            curBBlock = new BasicBlock();
                        }
                    }
                } else if (node1.getNodeType() == NodeType.LVal) {
                    if (node.getSubNodes().size() == 4) {
                        Value target = visitAST(node.getSubNodes().get(0));
                        Value source = visitAST(node.getSubNodes().get(2));
                        irBuilder.buildStore(curBBlock, source, target);
                    } else {    // getint
                        Value target = visitAST(node1);
                        CallInstruction source = irBuilder.buildCall(curBBlock, Function.getint, new ArrayList<>());
                        irBuilder.buildStore(curBBlock, source, target);
                    }
                } else if (node1.getNodeType() == NodeType.Block) {
                    isLastFunc = false;
                    for (Node node2 : node.getSubNodes()) {
                        visitAST(node2);
                    }
                    isLastFunc = true;
                } else if (node1.getNodeType() == NodeType.Exp) {
                    return visitAST(node1);
                }
            }
            case ForStmt -> {   // 简单store即可
                Value target = visitAST(node.getSubNodes().get(0));
                Value source = visitAST(node.getSubNodes().get(2));
                irBuilder.buildStore(curBBlock, source, target);
            }
            case Cond -> {
                Node node1 = node.getSubNodes().get(0);
                node1.setTrueBBlock(node.getTrueBBlock());
                node1.setFalseBBlock(node.getFalseBBlock());
                return visitAST(node1);
            }
            case LVal -> {
                String name = node.getSubNodes().get(0).getToken().getTokenContent();
                Value lVal = stack.getValueFromST(name);
                if (lVal.getValueType() instanceof IntType) {
                    return lVal;
                } else {    // 指针代表非常量
                    if (((PointerType) lVal.getValueType()).getType().isIntegerTy()) {
                        if (isIsCal() && lVal instanceof GlobalVar) {
                            ConstantInt initVal = (ConstantInt) ((GlobalVar) lVal).getInitVal();
                            return initVal;
                        } else {
                            return lVal;
                        }
                    } else if (((PointerType) lVal.getValueType()).getType().isPointerTy()) {
                        Value ptr = irBuilder.buildLoad(curBBlock, lVal);
                        if (node.getSubNodes().size() == 1) {
                            return ptr;
                        } else if (node.getSubNodes().size() == 4) {
                            ptr = irBuilder.buildGEPIns(curBBlock, ptr, visitAST(node.getSubNodes().get(2)));   // 不降维
                            while (((PointerType) ptr.getValueType()).getType() instanceof ArrayType) { // 降维
                                ptr = irBuilder.buildGEPIns(curBBlock, ptr, ConstantInt.ZERO, ConstantInt.ZERO);
                            }
                            return ptr;
                        } else {
                            Value index = visitAST(node.getSubNodes().get(2));
                            for (int i = 1; i < (node.getSubNodes().size() - 1) / 3; i++) {
                                Value tmp = index;
                                index = visitAST(node.getSubNodes().get(i * 3 + 2));
                                ptr = irBuilder.buildGEPIns(curBBlock, ptr, tmp, index);
                            }
                            return ptr;
                        }
                    } else {
                        // 数组
                        if (isIsCal() && lVal instanceof GlobalVar) {
                            Constant initVal = ((GlobalVar) lVal).getInitVal();

                            for (int i = 0; i < (node.getSubNodes().size() - 1) / 3; i++) {
                                Value exp = visitAST(node.getSubNodes().get(3 * i + 2));
                                initVal = ((ConstantArray) initVal).getElement(((ConstantInt) exp).getValue());
                            }
                            return initVal;
                        } else if (isIsCal() && lVal instanceof AllocInstruction && ((AllocInstruction) lVal).getInitialVal() != null) {
                            Constant initVal = ((AllocInstruction) lVal).getInitialVal();
                            for (int i = 0; i < (node.getSubNodes().size() - 1) / 3; i++) {
                                Value exp = visitAST(node.getSubNodes().get(3 * i + 2));
                                initVal = ((ConstantArray) initVal).getElement(((ConstantInt) exp).getValue());
                            }
                            return initVal;

                        } else {
                            Value gep = lVal;
                            for (int i = 0; i < (node.getSubNodes().size() - 1) / 3; i++) {
                                Value exp = visitAST(node.getSubNodes().get(3 * i + 2));
                                gep = irBuilder.buildGEPIns(curBBlock, gep, ConstantInt.ZERO, exp);
                            }
                            if (((PointerType) gep.getValueType()).getType() instanceof ArrayType) {    // 经过降维后仍然是数组，说明必是实参
                                gep = irBuilder.buildGEPIns(curBBlock, gep, ConstantInt.ZERO, ConstantInt.ZERO);
                            }
                            return gep;
                        }
                    }
                }
            }
            case PrimaryExp -> {
                if (node.getSubNodes().get(0).getNodeType() == NodeType.Number) {
                    return visitAST(node.getSubNodes().get(0));
                } else if (node.getSubNodes().size() == 3) {
                    return visitAST(node.getSubNodes().get(1));
                } else {
                    if (pointerLoad) {
                        pointerLoad = false;
                        return visitAST(node.getSubNodes().get(0));
                    }
                    Value lVal = visitAST(node.getSubNodes().get(0));
                    if (lVal != null && !(lVal.getValueType() instanceof IntType)) {
                        lVal = irBuilder.buildLoad(curBBlock, lVal);
                    }
                    return lVal;
                }
            }
            case Number -> {
                return new ConstantInt(Integer.parseInt(node.getSubNodes().get(0).getToken().getTokenContent()), 32);
            }
            case UnaryExp -> {
                if (node.getSubNodes().get(0).getNodeType() == NodeType.UnaryOp) {
                    Value unary = visitAST(node.getSubNodes().get(1));
                    Token op = node.getSubNodes().get(0).getSubNodes().get(0).getToken();
                    if (unary instanceof ConstantInt) {
                        return calculateToVal(new ConstantInt(0, 32), op, unary);
                    } else if (op.getTokenType() == TokenType.MINU) {
                        return irBuilder.buildAddExp(curBBlock, new ConstantInt(0, 32), op, unary);
                    } else if (op.getTokenType() == TokenType.NOT) {
                        return irBuilder.buildCompareIns(curBBlock, CompareType.EQL, unary, new ConstantInt(0, 32));
                    } else if (op.getTokenType() == TokenType.PLUS) {
                        return unary;
                    }
                } else if (node.getSubNodes().get(0).getNodeType() == NodeType.End) {
                    Function function = (Function) stack.getValueFromST(node.getSubNodes().get(0).getToken().getTokenContent());
                    ArrayList<Value> rArgs = new ArrayList<>();
                    if (node.getSubNodes().size() == 4) {
                        Node rparams = node.getSubNodes().get(2);
                        ArrayList<Type> fArgs = ((FunctionType) function.getValueType()).getFArgs();
                        for (int i = 0; i < rparams.getSubNodes().size(); i += 2) {
                            Node rparam = rparams.getSubNodes().get(i);
                            Type argType = fArgs.get(i / 2);
                            pointerLoad = !(argType instanceof IntType);
                            rArgs.add(visitAST(rparam));
                            pointerLoad = false;
                        }
                    }
                    return irBuilder.buildCall(curBBlock, function, rArgs);
                } else {
                    return visitAST(node.getSubNodes().get(0));
                }

            }
            case UnaryOp -> {
            }
            case FuncRParams -> {
            }
            case MulExp -> {
                Value unary = visitAST(node.getSubNodes().get(0));
                for (int i = 2; i < node.getSubNodes().size(); i += 2) {
                    Token op = node.getSubNodes().get(i - 1).getToken();
                    Node mulNode = node.getSubNodes().get(i);
                    while (mulNode.getSubNodes().size() == 3) {
                        Value unary2 = visitAST(mulNode.getSubNodes().get(0));
                        if (unary instanceof ConstantInt && unary2 instanceof ConstantInt)
                            unary = calculateToVal(unary, op, unary2);
                        else {
                            unary = irBuilder.buildMulExp(curBBlock, unary, op, unary2);
                        }
                        op = mulNode.getSubNodes().get(1).getToken();
                        mulNode = mulNode.getSubNodes().get(2);
                    }
                    Value unary2 = visitAST(mulNode.getSubNodes().get(0));
                    if (unary instanceof ConstantInt && unary2 instanceof ConstantInt)
                        unary = calculateToVal(unary, op, unary2);
                    else {
                        unary = irBuilder.buildMulExp(curBBlock, unary, op, unary2);
                    }
                }
                return unary;
            }
            case AddExp -> {
                Value unary = visitAST(node.getSubNodes().get(0));
                for (int i = 2; i < node.getSubNodes().size(); i += 2) {
                    Token op = node.getSubNodes().get(i - 1).getToken();
                    Node mulNode = node.getSubNodes().get(i);
                    while (mulNode.getSubNodes().size() == 3) {
                        Value unary2 = visitAST(mulNode.getSubNodes().get(0));
                        if (unary instanceof ConstantInt && unary2 instanceof ConstantInt)
                            unary = calculateToVal(unary, op, unary2);
                        else {
                            unary = irBuilder.buildAddExp(curBBlock, unary, op, unary2);
                        }
                        op = mulNode.getSubNodes().get(1).getToken();
                        mulNode = mulNode.getSubNodes().get(2);
                    }
                    Value unary2 = visitAST(mulNode.getSubNodes().get(0));
                    if (unary instanceof ConstantInt && unary2 instanceof ConstantInt)
                        unary = calculateToVal(unary, op, unary2);
                    else {
                        unary = irBuilder.buildAddExp(curBBlock, unary, op, unary2);
                    }
                }
                return unary;
            }
            case RelExp, EqExp -> {
                Node addExp = node.getSubNodes().get(0);
                Value value = visitAST(addExp);
                for (int i = 1; i < (node.getSubNodes().size() + 1) / 2; i++) {
                    I32Switch = false;
                    Node relNode = node.getSubNodes().get(i * 2);
                    Token op = node.getSubNodes().get(i * 2 - 1).getToken();
                    while (relNode.getSubNodes().size() == 3) {
                        Value value1 = visitAST(relNode.getSubNodes().get(0));
                        if (value.getValueType().isIntegerTy() && ((IntType) value.getValueType()).getBit() == 1) {
                            value = irBuilder.buildZextIns(curBBlock, value);
                        }
                        if (value1.getValueType().isIntegerTy() && ((IntType) value1.getValueType()).getBit() == 1) {
                            value1 = irBuilder.buildZextIns(curBBlock, value1);
                        }
                        value = irBuilder.buildIcmpIns(curBBlock, op.getTokenType().toCompareType(), value, value1);
                        op = relNode.getSubNodes().get(1).getToken();
                        relNode = relNode.getSubNodes().get(2);
                    }
                    Value value1 = visitAST(relNode.getSubNodes().get(0));
                    if (value.getValueType().isIntegerTy() && ((IntType) value.getValueType()).getBit() == 1) {
                        value = irBuilder.buildZextIns(curBBlock, value);
                    }
                    if (value1.getValueType().isIntegerTy() && ((IntType) value1.getValueType()).getBit() == 1) {
                        value1 = irBuilder.buildZextIns(curBBlock, value1);
                    }
                    value = irBuilder.buildIcmpIns(curBBlock, op.getTokenType().toCompareType(), value, value1);
                }
                return value;
            }
            case LAndExp -> {
                Node tmpnode = node;
                while (tmpnode.getSubNodes().size() == 3) {
                    dealLAndExp(node, tmpnode);
                    tmpnode = tmpnode.getSubNodes().get(2);
                }
                dealLAndExp(node, tmpnode);
                irBuilder.buildBr(curBBlock, node.getTrueBBlock());
            }
            case LOrExp -> {
                if (node.getSubNodes().size() == 3) {
                    Node lAndExp = node.getSubNodes().get(0);
                    lAndExp.setTrueBBlock(node.getTrueBBlock());
                    BasicBlock nextBlock = irBuilder.buildBlock(curFunction);
                    lAndExp.setFalseBBlock(nextBlock);
                    visitAST(lAndExp);
                    curBBlock = nextBlock;
                }
                Node lOrExp = node.getSubNodes().get(node.getSubNodes().size() - 1);
                lOrExp.setTrueBBlock(node.getTrueBBlock());
                lOrExp.setFalseBBlock(node.getFalseBBlock());
                visitAST(lOrExp);
            }
            case ConstExp -> {
                setIsCal(true);
                Value ret = visitAST(node.getSubNodes().get(0));
                setIsCal(false);
                return ret;
            }
            case FormatString -> {
            }
            case Exp -> {
                return visitAST(node.getSubNodes().get(0));
            }
            case BlockItem -> {
                for (Node node1 : node.getSubNodes()) {
                    visitAST(node1);
                }
            }
            case End -> {
            }
        }
        return null;
    }

    private void dealLAndExp(Node node, Node tmpnode) {
        Node eqExp = tmpnode.getSubNodes().get(0);
        BasicBlock nextBBlock = irBuilder.buildBlock(curFunction);
        I32Switch = true;   // EQEXP返回Int32需要使用Icmp进行类型转换得到I1才能使用Br
        Value value = visitAST(eqExp);
        if (I32Switch) {
            value = irBuilder.buildIcmpIns(curBBlock, CompareType.NEQ, value, ConstantInt.ZERO);
            I32Switch = false;
        }
        irBuilder.buildBr(curBBlock, value, nextBBlock, node.getFalseBBlock());
        curBBlock = nextBBlock;
    }

    private int calculate(int l, Token op, int r) {
        int retval = 0;
        switch (op.getTokenType()) {
            case PLUS -> {
                retval = l + r;
            }
            case MINU -> {
                retval = l - r;
            }
            case MULT -> {
                retval = l * r;
            }
            case DIV -> {
                retval = l / r;
            }
            case MOD -> {
                retval = l % r;
            }
            case NOT -> {
                retval = r == 0 ? 1 : 0;
            }
        }
        return retval;
    }

    private Value calculateToVal(Value l, Token op, Value r) {
        return new ConstantInt(calculate(((ConstantInt) l).getValue(), op, ((ConstantInt) r).getValue()), 32);
    }
}
