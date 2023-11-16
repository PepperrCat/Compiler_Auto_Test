package middle;

import Nodes.*;
import middle.base.*;
import middle.base.instructions.*;
import middle.base.values.*;
import models.Pair;
import models.TokenType;
import symbol.FuncType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class LLVMIR {
    CompUnitNode rootNode = null;
    public Module module = null;
    Function thisFunction = null;
    BasicBlock thisBlock = null;
    Stack<LoopBlock> loopBlockStack = new Stack<>();


    class Register {
        static int virtualId = 0;

        static void setZero() {
            virtualId = 0;
        }

        static void addOne() {
            virtualId++;
        }

        static String getRegister() {

            return "r" + Integer.toString(virtualId++);
        }

    }

    boolean isGlobalField() {
        return ValueStack.getInstance().stack.size() == 1;
    }


    void push(boolean isFunc, ValueType funcType) {
        ValueStack.getInstance().push(new ValueTable(new HashMap<>(), isFunc, funcType));
    }

    ValueTable pop() {
        return ValueStack.getInstance().pop();
    }

    public ValueTable top() {
        return ValueStack.getInstance().getTop();
    }

    void clear() {
        ValueStack.getInstance().setZero();
    }

    void add(String name, Value symbol) {
        ValueStack.getInstance().getTop().symbols.put(name, symbol);
    }

    public Value get(String name) {
        for (int i = ValueStack.getInstance().stack.size() - 1; i >= 0; i--) {
            if (ValueStack.getInstance().stack.get(i).symbols.containsKey(name)) {
                return ValueStack.getInstance().stack.get(i).symbols.get(name);
            }
        }
        return null;
    }

    public Value getFieldSymbol(Pair ident) {
        if (ValueStack.getInstance().getTop() != null)
            if (ValueStack.getInstance().getTop().symbols.containsKey(ident.getValue())) {
                return ValueStack.getInstance().getTop().symbols.get(ident.getValue());
            }
        return null;
    }

    public Value getFieldSymbol(String ident) {
        if (ValueStack.getInstance().getTop() != null)
            if (ValueStack.getInstance().getTop().symbols.containsKey(ident)) {
                return ValueStack.getInstance().getTop().symbols.get(ident);
            }
        return null;
    }

    public ValueType getFuncType() {
        for (int i = ValueStack.getInstance().stack.size() - 1; i >= 0; i--) {
            if (ValueStack.getInstance().stack.get(i).isFunc) {
                return ValueStack.getInstance().stack.get(i).funcType;
            }
        }
        return null;
    }


    public void buildModule(CompUnitNode compUnitNode) {
        module = new Module();
        rootNode = compUnitNode;

        clear();
        push(false, null);
        declare();
        visitCompUnit(rootNode);
        check();
        print();
    }

    private void check() {
        for (Function function : module.functions) {
            for (int i = 0; i < function.basicBlocks.size() - 1; i++) {
                BasicBlock basicBlock = function.basicBlocks.get(i);
                if (!basicBlock.isEnded) {
                    basicBlock.instructions.add(new Br(function.basicBlocks.get(i + 1)));
                }
            }
        }
    }

    private Function addFunc(String name, ValueType type, List<FuncParam> funcParams) {
        Function function = new Function(name, type);
        function.funcParams.addAll(funcParams);
        return function;
    }

    void addInstruction(Instruction instruction) {
        if (!isGlobalField()) {
            if (!thisBlock.isEnded) thisBlock.instructions.add(instruction);
        } else module.instructions.add(instruction);
    }

    private void declare() {
        Function getint = addFunc("getint", ValueType.i32, new ArrayList<>());
        Function putint = addFunc("putint", ValueType.VOID, new ArrayList<>());
        Function putch = addFunc("putch", ValueType.VOID, new ArrayList<>());
//        Function putstr = addFunc("putstr", ValueType.VOID, new ArrayList<>());
        add("getint", getint);
        add("putint", putint);
        add("putch", putch);
//        add("putstr", putstr);
        putint.funcParams.add(new FuncParam(1, ValueType.i32));
        putch.funcParams.add(new FuncParam(1, ValueType.i32));
//        putstr.funcParams.add(new FuncParam(1, ValueType.i8));
        module.declares.add(new Declare(getint));
        module.declares.add(new Declare(putint));
        module.declares.add(new Declare(putch));
//        module.declares.add(new Declare(putstr));
    }

    public void print() {
        module.print();

    }

    //编译单元    CompUnit → {Decl} {FuncDef} MainFuncDef
    void visitCompUnit(CompUnitNode compUnitNode) {
        for (DeclNode declNode : compUnitNode.getDeclNodeList()) {
            visitDecl(declNode);
        }
        for (FuncDefNode funcDefNode : compUnitNode.getFuncDefNodeList()) {
            visitFuncDef(funcDefNode);
        }
        visitMainFuncDef(compUnitNode.getMainFuncDefNode());
    }

    //Decl → ConstDecl | VarDecl
    void visitDecl(DeclNode declNode) {
        if (declNode != null) {
            if (declNode.getConstDeclNode() != null) {
                visitConstDecl(declNode.getConstDeclNode());
            }
            if (declNode.getVarDeclNode() != null) {
                visitVarDecl(declNode.getVarDeclNode());
            }
        }
    }

    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    void visitConstDecl(ConstDeclNode constDeclNode) {
        for (ConstDefNode constDefNode : constDeclNode.getAllConstDefNode()) {
            visitConstDef(constDefNode);
        }
    }

    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    void visitConstDef(ConstDefNode constDefNode) {
        if (isGlobalField()) {
            ArrayList<Const> consts = new ArrayList<>();
            List<Object> initValues = buildInit(constDefNode.getConstInitValNode(), consts);
            Var var = new Var(constDefNode.getIdent().getValue(), true, initValues, ValueType.i32);
            var.setGlobal(true);
            var.setDim(constDefNode.getConstExpNodeList().size());
            if (var.dim == 0) {
//                var.setType(ValueType.i32);
                add(constDefNode.getIdent().getValue(), var);
                module.globalVar.add(var);
            }
            if (var.dim == 1) {
                Value Size = visitConstExp(constDefNode.getConstExpNodeList().get(0));
                assert Size instanceof Const;
                int size = ((Const) Size).value;
                var.setType(size, ValueType.i32);
                var.ReSetArray();
                add(constDefNode.getIdent().getValue(), var);
                module.globalVar.add(var);
            }
            if (var.dim == 2) {
                Const Size1 = (Const) visitConstExp(constDefNode.getConstExpNodeList().get(0));
                Const Size2 = (Const) visitConstExp(constDefNode.getConstExpNodeList().get(1));

                var.setType(Size1.value, Size2.value, ValueType.i32);
                var.ReSetArray(Size1.value, Size2.value);
                add(constDefNode.getIdent().getValue(), var);
                module.globalVar.add(var);
            }


        } else {
            ArrayList<Const> consts = new ArrayList<>();
            List<Object> initValues = new ArrayList<>();

            Var var = new Var(Register.getRegister(), true, initValues, ValueType.i32);
            var.setGlobal(false);
            var.setDim(constDefNode.getConstExpNodeList().size());
            add(constDefNode.getIdent().getValue(), var);
            if (var.dim == 0) {
                var.setType(ValueType.i32);
                addInstruction(new Alloca(var));
                addInstruction(new Store(var, visitAddExp(constDefNode.getConstInitValNode().getConstExpNode().getAddExpNode())));

            } else {
                if (var.dim == 1) {
                    Const Size = (Const) visitConstExp(constDefNode.getConstExpNodeList().get(0));
                    var.setType(Size.value, ValueType.i32);
                    addInstruction(new Alloca(var));
//                    var.ReSetArray();
//                    for (Value value : var.resetInit) {
//                        storeInArray(new Const(var.resetInit.indexOf(value)), var, value);
//                    }
                } else if (var.dim == 2) {
                    Const Size1 = (Const) visitConstExp(constDefNode.getConstExpNodeList().get(0));
                    Const Size2 = (Const) visitConstExp(constDefNode.getConstExpNodeList().get(1));
                    var.setType(Size1.value, Size2.value, ValueType.i32);
                    addInstruction(new Alloca(var));
//                    var.ReSetArray(Size1.value, Size2.value);
//                    for (Value value : var.resetInit) {
//                        storeInArray(new Const(var.resetInit.indexOf(value)), var, value);
//                    }
                }
                if (constDefNode.getConstInitValNode() != null) {
                    var.initValues = buildInit(var, constDefNode.getConstInitValNode(), consts);
                }
            }
        }

    }


    void storeInArray(Value index, Var array, Value var) {
        Var result = new Var(Register.getRegister(), false, null, ValueType.i32);
        addInstruction(new Getelementptr(result, array, index));
        addInstruction(new Store(result, var));
    }

    /**
     * 获取数组中的元素,从数组array中获取第index个元素的地址
     *
     * @param index
     * @param array
     * @return
     */
    Value getInArray(Value index, Var array) {
        Var arrEle = new Var(Register.getRegister(), false, null, ValueType.i32);
        arrEle.isPtr = true;
        arrEle.dim = 0;
        //返回一个指向int类型的指针
        addInstruction(new Getelementptr(arrEle, array, index));
        return arrEle;
    }

    Value getInArray(Value index, Var array, int getDim, int srcDim) {
        Var arrEle = new Var(Register.getRegister(), false, null, ValueType.i32);
        if (getDim == srcDim)
            addInstruction(new Getelementptr(arrEle, array, index));
        else {
//            Value index2=new Var(Register.getRegister());
            Value index2 = addBinaryInstruction(index, new Const(array.size2), new OpValue(Op.mul));
            arrEle.dim = srcDim - getDim;
            addInstruction(new Getelementptr(arrEle, array, index2));
            //转为ptr
            arrEle.isPtr = true;
        }
        return arrEle;
    }

    public List<Object> buildInit(ConstInitValNode constInitVal, ArrayList<Const> constants) {

        //ConstInitVal → ConstExp
        //    | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ArrayList<Object> res = new ArrayList<>();
        if (constInitVal.getConstExpNode() != null) {

            Value initConst = visitConstExp(constInitVal.getConstExpNode());

            if (initConst instanceof Const c)
                constants.add(c);
            res.add(initConst);
        } else {
            for (ConstInitValNode syntaxNode : constInitVal.getAllConstInitValNode())
                res.add(buildInit(syntaxNode, constants));
        }
        return res;
    }

    public List<Object> buildInit(Var var, ConstInitValNode constInitVal, ArrayList<Const> constants) {

        //ConstInitVal → ConstExp
        //    | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ArrayList<Object> res = new ArrayList<>();
        if (constInitVal.getConstExpNode() != null) {

            Value initConst = visitConstExp(constInitVal.getConstExpNode());
            var.resetInit.add(initConst);
            storeInArray(new Const(var.resetInit.indexOf(initConst)), var, initConst);

            if (initConst instanceof Const c)
                constants.add(c);
            res.add(initConst);
        } else {
            for (ConstInitValNode syntaxNode : constInitVal.getAllConstInitValNode())
                res.add(buildInit(var,syntaxNode, constants));
        }
        return res;
    }


    public List<Object> buildInit(InitValNode constInitVal, ArrayList<Const> constants) {
        ArrayList<Object> res = new ArrayList<>();
        if (constInitVal.getExpNode() != null) {

            Value initConst = visitExp(constInitVal.getExpNode());

            if (initConst instanceof Const c)
                constants.add(c);
            res.add(initConst);
        } else {
            for (InitValNode syntaxNode : constInitVal.getAllInitValNode())
                res.add(buildInit(syntaxNode, constants));
        }
        return res;
    }

    public List<Object> buildInit(Var var, InitValNode constInitVal, ArrayList<Const> constants, boolean bool) {
        ArrayList<Object> res = new ArrayList<>();
        if (constInitVal.getExpNode() != null) {

            Value initConst = visitExp(constInitVal.getExpNode());
            var.resetInit.add(initConst);
            storeInArray(new Const(var.resetInit.indexOf(initConst)), var, initConst);

            if (initConst instanceof Const c)
                constants.add(c);
            res.add(initConst);
        } else {
            for (InitValNode syntaxNode : constInitVal.getAllInitValNode())
                res.add(buildInit(var, syntaxNode, constants, true));
        }
        return res;
    }


    //ConstInitVal → ConstExp
    //    | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    void visitConstInitVal(ConstInitValNode constInitValNode) {
        if (constInitValNode.getConstExpNode() != null) {
            visitConstExp(constInitValNode.getConstExpNode());
        } else
            for (ConstInitValNode initValNode : constInitValNode.getAllConstInitValNode()) {
                visitConstInitVal(initValNode);
            }
    }

    //VarDecl → BType VarDef { ',' VarDef } ';'
    void visitVarDecl(VarDeclNode varDeclNode) {
        for (VarDefNode varDefNode : varDeclNode.getAllVarDefNode()) {
            visitVarDef(varDefNode);
        }
    }

    //VarDef → Ident { '[' ConstExp ']' } ( '=' InitVal | e)
    void visitVarDef(VarDefNode varDefNode) {
        if (isGlobalField()) {
            ArrayList<Const> consts = new ArrayList<>();
            List<Object> initValues = new ArrayList<>();
            if (varDefNode.getInitValNode() != null) {
                initValues = buildInit(varDefNode.getInitValNode(), consts);
            }
            Var var = new Var(varDefNode.getIdent().getValue(), false, initValues, ValueType.i32);
            var.setGlobal(true);
            var.setDim(varDefNode.getConstExpNodeList().size());
            if (var.dim == 0) {
//                var.setType(ValueType.i32);
                add(varDefNode.getIdent().getValue(), var);
                module.globalVar.add(var);
            }
            if (var.dim > 0) {
                if (varDefNode.getInitValNode() != null) {
                    initValues = buildInit(varDefNode.getInitValNode(), consts);
                }
                var.initValues = initValues;
                if (var.dim == 1) {

                    Value Size = visitConstExp(varDefNode.getConstExpNodeList().get(0));
                    assert Size instanceof Const;
                    int size = ((Const) Size).value;
                    var.setType(size, ValueType.i32);
                    var.ReSetArray();
                    add(varDefNode.getIdent().getValue(), var);
                    module.globalVar.add(var);
                }
                if (var.dim == 2) {
                    Const Size1 = (Const) visitConstExp(varDefNode.getConstExpNodeList().get(0));
                    Const Size2 = (Const) visitConstExp(varDefNode.getConstExpNodeList().get(1));

                    var.setType(Size1.value, Size2.value, ValueType.i32);
                    var.ReSetArray(Size1.value, Size2.value);
                    add(varDefNode.getIdent().getValue(), var);
                    module.globalVar.add(var);
                }


            }


        } else {
            ArrayList<Const> consts = new ArrayList<>();
            List<Object> initValues = new ArrayList<>();

            Var var = new Var(Register.getRegister(), true, initValues, ValueType.i32);
            var.setGlobal(false);
            var.setDim(varDefNode.getConstExpNodeList().size());
            add(varDefNode.getIdent().getValue(), var);
            if (var.dim == 0) {
//                var.initValues = buildInit(varDefNode.getInitValNode(), consts);
                var.setType(ValueType.i32);
                addInstruction(new Alloca(var));
                if (varDefNode.getInitValNode() != null) {
                    initValues = buildInit(varDefNode.getInitValNode(), consts);
                    addInstruction(new Store(var, (Value) initValues.get(0)));
                }

            } else {
                if (var.dim == 1) {
                    Const Size = (Const) visitConstExp(varDefNode.getConstExpNodeList().get(0));
                    var.setType(Size.value, ValueType.i32);
                    addInstruction(new Alloca(var));
//                    var.ReSetArray();
//                    for (Value value : var.resetInit) {
//                        storeInArray(new Const(var.resetInit.indexOf(value)), var, value);
//                    }
                } else if (var.dim == 2) {
                    Const Size1 = (Const) visitConstExp(varDefNode.getConstExpNodeList().get(0));
                    Const Size2 = (Const) visitConstExp(varDefNode.getConstExpNodeList().get(1));
                    var.setType(Size1.value, Size2.value, ValueType.i32);
                    addInstruction(new Alloca(var));
//                    var.ReSetArray(Size1.value, Size2.value);
//                    for (Value value : var.resetInit) {
//                        storeInArray(new Const(var.resetInit.indexOf(value)), var, value);
//                    }
                }
                if (varDefNode.getInitValNode() != null) {
                    var.initValues = buildInit(var, varDefNode.getInitValNode(), consts, true);
                }
            }
        }

    }

    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    Value visitInitVal(InitValNode initValNode) {
        if (initValNode.getExpNode() != null) {
            return visitExp(initValNode.getExpNode());
        } else
            for (InitValNode initValNode1 : initValNode.getAllInitValNode()) {
                visitInitVal(initValNode1);
            }
        return null;
    }

    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    void visitFuncDef(FuncDefNode funcDefNode) {
        Register.setZero();
        ValueType valueType;
        if (funcDefNode.getFuncTypeNode().getFuncType() == FuncType.VOID) valueType = ValueType.VOID;
        else valueType = ValueType.i32;

        thisFunction = new Function(funcDefNode.getIdent().getValue(), valueType);
        module.functions.add(thisFunction);
        thisBlock = new BasicBlock();
        thisFunction.basicBlocks.add(thisBlock);

        add(funcDefNode.getIdent().getValue(), thisFunction);
        if (funcDefNode.getFuncFParamsNode() != null)
            for (FuncFParamNode funcFParamNode : funcDefNode.getFuncFParamsNode().getAllFuncFParamNode()) {
                thisFunction.funcParams.add(new middle.base.values.FuncParam(Register.getRegister(), funcFParamNode.getDimension(), funcFParamNode.getValueType()));
            }
        thisBlock.setName(Register.getRegister());
        push(true, funcDefNode.getFuncTypeNode().getFuncValueType());
        initParams(thisFunction.funcParams, funcDefNode.getFuncFParamsNode());
        visitBlock(funcDefNode.getBlockNode());
        if (!thisBlock.isEnded && thisFunction.type == ValueType.VOID) {
            addInstruction(new Ret(ValueType.VOID, null));
        }
        pop();
    }

    private void initParams(List<FuncParam> funcParams, FuncFParamsNode funcFParamsNode) {
        int i = 0;
        for (FuncParam funcParam : funcParams) {
            Var temp = new Var(Register.getRegister(), false, null, funcParam.type);
            temp.setType(funcParam.getType());
            temp.dim = funcParam.dim;
            if (funcParam.dim == 0) {
                add(funcFParamsNode.getAllFuncFParamNode().get(i++).getIdent().getValue(), temp);
                addInstruction(new Alloca(temp));
                addInstruction(new Store(temp, funcParam));
            } else if (funcParam.dim == 1) {
                add(funcFParamsNode.getAllFuncFParamNode().get(i++).getIdent().getValue(), temp);
                temp.isPtr = true;
                addInstruction(new Alloca(temp));
                addInstruction(new Store(temp, funcParam));
            } else if (funcParam.dim == 2) {
                add(funcFParamsNode.getAllFuncFParamNode().get(i).getIdent().getValue(), temp);
                temp.isPtr = true;
                temp.size2 =
                        ((Const) visitConstExp(funcFParamsNode.getAllFuncFParamNode().get(i++).getConstExpNodeList().get(0))).value;
                funcParam.size2 = temp.size2;
                addInstruction(new Alloca(temp));
                addInstruction(new Store(temp, funcParam));
            }

        }
    }

    //MainFuncDef → 'int' 'main' '(' ')' Block
    void visitMainFuncDef(MainFuncDefNode mainFuncDefNode) {
        Register.setZero();
        thisFunction = new Function("main", ValueType.i32);
        module.functions.add(thisFunction);
        thisBlock = new BasicBlock();
        thisFunction.basicBlocks.add(thisBlock);
        thisBlock.setName(Register.getRegister());
        add("main", new Function("main", ValueType.i32));
        push(true, ValueType.i32);
        visitBlock(mainFuncDefNode.getBlockNode());
        pop();
    }

    //FuncFParams → FuncFParam { ',' FuncFParam }

    void visitFuncFParams(FuncFParamsNode funcFParamsNode) {
        for (FuncFParamNode funcFParamNode : funcFParamsNode.getAllFuncFParamNode()) {
            visitFuncFParam(funcFParamNode);
        }

    }

    //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    void visitFuncFParam(FuncFParamNode funcFParamNode) {
        for (ConstExpNode constExpNode : funcFParamNode.getConstExpNodeList()) {
            visitConstExp(constExpNode);
        }
    }

    //Block → '{' { BlockItem } '}'
    void visitBlock(BlockNode blockNode) {

        for (BlockItemNode blockItemNode : blockNode.getBlockItemNodeList()) {
            visitBlockItem(blockItemNode);
        }
    }

    //BlockItem → Decl | Stmt
    void visitBlockItem(BlockItemNode blockItemNode) {
        if (blockItemNode.getDeclNode() != null) {
            visitDecl(blockItemNode.getDeclNode());
        }
        if (blockItemNode.getStmtNode() != null) {
            visitStmt(blockItemNode.getStmtNode());
        }
    }

    //    //Stmt →
    //    //  | LVal '=' (Exp | 'getint''('')') ';'
    //    //  | [Exp] ';'
    //    //  | Block
    //
    //    //  | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    //    //  | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //    //  | 'break' ';'
    //    //  | 'continue' ';'
    //    //  | 'return' [Exp] ';'
    //    //  | 'printf''('FormatString{','Exp}')'';'
    void visitStmt(StmtNode stmtNode) {

        if (stmtNode.getReturnCon() != null) {
            visitReturn(stmtNode);
        } else if (stmtNode.getIfCon() != null) {
            visitIf(stmtNode);
        } else if (stmtNode.getlValNode() != null) {
            //向上查找变量名
            Value value1 = visitLVal(stmtNode.getlValNode());

            if (stmtNode.getlValExpNode() != null) {
                Value value2 = visitExp(stmtNode.getlValExpNode());
                addInstruction(new Store(value1, value2));
            } else if (stmtNode.getGetintCon() != null) {
                Function getint = (Function) get("getint");
                Var result = new Var(Register.getRegister(), false, null, ValueType.i32);
                addInstruction(new Call(result, getint));
                addInstruction(new Store(value1, result));
            }

        } else if (stmtNode.getExpNode() != null) {
            visitExp(stmtNode.getExpNode());
        } else if (stmtNode.getPrintfCon() != null) {
            //  | 'printf''('FormatString{','Exp}')'';'
            visitPrintf(stmtNode);
        } else if (stmtNode.getBlockNode() != null) {
            push(false, null);
            BasicBlock block = new BasicBlock();
            block.setName(Register.getRegister());
            thisFunction.basicBlocks.add(block);
            thisBlock = block;
            visitBlock(stmtNode.getBlockNode());
            pop();


        } else if (stmtNode.getForCon() != null) {
            visitFor(stmtNode);
        } else if (stmtNode.getBreakCon() != null) {
            thisBlock.addBr(new Br(loopBlockStack.peek().outBlock));

        } else if (stmtNode.getContinueCon() != null) {
            assert thisBlock instanceof LoopBlock;
            thisBlock.addBr((new Br(loopBlockStack.peek().startBlock)));
        }
    }

    private void visitFor(StmtNode stmtNode) {
        //  | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt

        BasicBlock outBlock = new BasicBlock();
        BasicBlock condBlock = new BasicBlock();


        BasicBlock endForStmtBlock = new BasicBlock();
        LoopBlock inBlock = new LoopBlock(endForStmtBlock, outBlock);

        loopBlockStack.push(inBlock);
        if (stmtNode.getStartForStmtNode() != null) visitForStmt(stmtNode.getStartForStmtNode());


        thisBlock = condBlock;
        thisFunction.basicBlocks.add(condBlock);
        condBlock.setName(Register.getRegister());
        visitCond(stmtNode.getForCondNode(), inBlock, null, outBlock);


        thisFunction.basicBlocks.add(inBlock);
        thisBlock = inBlock;
        inBlock.setName(Register.getRegister());
        visitStmt(stmtNode.getForStmt());
        thisBlock.addBr(new Br(endForStmtBlock));

        thisBlock = endForStmtBlock;
        thisFunction.basicBlocks.add(thisBlock);
        thisBlock.setName(Register.getRegister());
        if (stmtNode.getEndForStmtNode() != null) visitForStmt(stmtNode.getEndForStmtNode());
        thisBlock.addBr((new Br(condBlock)));

        thisFunction.basicBlocks.add(outBlock);
        thisBlock = outBlock;
        outBlock.setName(Register.getRegister());

    }

    Value visitForStmt(ForStmtNode forStmtNode) {
        //ForStmt → LVal '=' Exp
        Value res = visitLVal(forStmtNode.getlValNode());
        Value value = visitExp(forStmtNode.getExpNode());
        addInstruction(new Store(res, value));
        return res;
    }

    private void visitIf(StmtNode stmtNode) {
        //  | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        BasicBlock inBlock = new BasicBlock();
        BasicBlock outBlock = new BasicBlock();
        BasicBlock elseBlock = null;
        if (stmtNode.getElseCon() != null) {
            elseBlock = new BasicBlock();
        }
        visitCond(stmtNode.getIfCondNode(), inBlock, elseBlock, outBlock);
//        addInstruction(new Br(inBlock));
        inBlock.name = Register.getRegister();
        thisBlock = inBlock;
        thisFunction.basicBlocks.add(inBlock);
        visitStmt(stmtNode.getIfStmtNode());
        //跳转到outBlock
        thisBlock.addBr((new Br(outBlock)));
        if (elseBlock != null) {
            elseBlock.name = Register.getRegister();
            thisBlock = elseBlock;
            thisFunction.basicBlocks.add(elseBlock);
            visitStmt(stmtNode.getElseStmtNode());
            Br out = new Br(null);
            thisBlock.addBr(out);
            outBlock.name = Register.getRegister();
            out.setOutBlock(outBlock);
        } else outBlock.setName(Register.getRegister());
        thisBlock = outBlock;
        thisFunction.basicBlocks.add(outBlock);
    }

    private void visitPrintf(StmtNode stmtNode) {
        //  | 'printf''('FormatString{','Exp}')'';'
        int i = 0;
        String temp = stmtNode.getFormatString().getValue().substring(1, stmtNode.getFormatString().getValue().length() - 1);
        for (int j = 0; j < temp.length(); j++) {
            char ch = temp.charAt(j);
            if (ch != '%') {
                if (ch == '\\' && j + 1 < temp.length() && temp.charAt(j + 1) == 'n') {
                    j++;
                    ch = '\n';
                }
                if (ch == '\\' && j + 1 < temp.length() && temp.charAt(j + 1) == 't') {
                    j++;
                    ch = '\t';
                }

                List<Value> params = new ArrayList<>();
                params.add(new Const((int) ch, ValueType.i32));
                addInstruction(new Call(null, (Function) get("putch"), params));
            } else {
                j++;
                ch = temp.charAt(j);
                if (ch == 'd') {
                    List<Value> params = new ArrayList<>();
                    params.add(visitExp(stmtNode.getPrintfExpList().get(i++)));
                    addInstruction(new Call(null, (Function) get("putint"), params));
                }

            }
        }
        Function printf = (Function) get("printf");
    }

    //'return' [Exp] ';'
    void visitReturn(StmtNode stmtNode) {


        if (stmtNode.getReturnExp() != null) {
            addInstruction(new Ret(ValueType.i32, visitExp(stmtNode.getReturnExp())));
        } else addInstruction(new Ret(ValueType.VOID, null));
        thisBlock.isEnded = true;
    }

    //ForStmt → LVal '=' Exp


    //Exp → AddExp
    Value visitExp(ExpNode expNode) {
        return visitAddExp(expNode.getAddExpNode());
    }

    //Cond → LOrExp 
    void visitCond(CondNode condNode, BasicBlock inBlock, BasicBlock elseBlock, BasicBlock outBlock) {
        if (condNode == null) thisBlock.addBr(new Br(inBlock));
        else
            visitLOrExp(condNode.getlOrExpNode(), inBlock, elseBlock, outBlock);
    }

    //LVal → Ident {'[' Exp ']'}
    Value visitLVal(LValNode lValNode) {


        Value array = get(lValNode.getIdent().getValue());
        if (isGlobalField()) {
            if (array.dim == 0) return (Const) ((Var) array).initValues.get(0);
            if (array.dim == 1) {
                Const index = (Const) visitExp(lValNode.getExpNodeList().get(0));
                return ((Var) array).getInitValue(index.value);
            }
            if (array.dim == 2) {
                Const index1 = (Const) visitExp(lValNode.getExpNodeList().get(0));
                Const index2 = (Const) visitExp(lValNode.getExpNodeList().get(1));
                Const index = new Const(index1.value * ((Var) array).size2 + index2.value);
                return ((Var) array).getInitValue(index.value);
            }
        }
        int dim = array.dim;
        Value result = array;
        int size = lValNode.getExpNodeList().size();
        int diff = dim - size;
        //如果得到的符号为1维
        if (dim == 1) {
            //若降维为0
            if (size == 0) {
                if (array.isPtr) {
                    Var ptr = new Var(Register.getRegister(), false, null, ValueType.i32);
                    ptr.isPtr = array.isPtr;
                    addInstruction(new Load(ptr, array));
                    return getInArray(new Const(0), (Var) ptr, 0, 1);
                }

//                if (array.isPtr) result = new Value(Register.getRegister());
                //直接返回原一维数组
                return getInArray(new Const(0), (Var) array, 0, 1);

            }
            if (size == 1) {
                //找到索引对应元素的地址
                if (array.isPtr) {
                    Var ptr = new Var(Register.getRegister(), false, null, ValueType.i32);
                    ptr.isPtr = array.isPtr;
                    addInstruction(new Load(ptr, array));
                    Value index = visitExp(lValNode.getExpNodeList().get(0));
                    result = getInArray(index, ptr);
                    result.isPtr = false;
                } else {
                    Value index = visitExp(lValNode.getExpNodeList().get(0));
                    result = getInArray(index, (Var) array);
                    result.isPtr = false;
                }
            }
        }
        if (dim == 2) {


            if (size == 0) {
                //直接返回数组对应的符号
                if (array.isPtr) {
                    Var ptr = new Var(Register.getRegister(), false, null, ValueType.i32);
                    ptr.isPtr = array.isPtr;
                    ptr.size2 = array.size2;
                    if (array.isPtr) addInstruction(new Load(ptr, array));
                    return getInArray(new Const(0), ptr, 0, 2);
                } else return getInArray((Value) new Const(0), (Var) array, 0, 2);
            }
            if (size == 1) {

                Value index = visitExp(lValNode.getExpNodeList().get(0));
                //todo：完成给一维数组参数类型传指针
                if (array.isPtr) {
                    Var ptr = new Var(Register.getRegister(), false, null, ValueType.i32);
                    ptr.isPtr = array.isPtr;
                    ptr.size2 = array.size2;
                    if (array.isPtr) addInstruction(new Load(ptr, array));
                    return getInArray(index, ptr, 1, 2);
                } else return getInArray((Value) index, (Var) array, 1, 2);

            } else {

                if (array.isPtr) {
                    Var ptr = new Var(Register.getRegister(), false, null, ValueType.i32);
                    ptr.isPtr = array.isPtr;
                    ptr.size2 = array.size2;
                    if (array.isPtr) addInstruction(new Load(ptr, array));
                    Value index1 = visitExp(lValNode.getExpNodeList().get(0));
                    Value index2 = visitExp(lValNode.getExpNodeList().get(1));
                    Value index3 = addBinaryInstruction(index1, new Const(array.size2), new OpValue(Op.mul));
                    Value index4 = addBinaryInstruction(index3, index2, new OpValue(Op.add));
                    result = getInArray(index4, ptr);
                    result.isPtr = false;
                } else {
                    Value index1 = visitExp(lValNode.getExpNodeList().get(0));
                    Value index2 = visitExp(lValNode.getExpNodeList().get(1));
                    Value index3 = addBinaryInstruction(index1, new Const(array.size2), new OpValue(Op.mul));
                    Value index4 = addBinaryInstruction(index3, index2, new OpValue(Op.add));
                    result = getInArray(index4, (Var) array);
                    result.isPtr = false;
                }
            }
        }
        return result;
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number
    Value visitPrimaryExp(PrimaryExpNode primaryExpNode) {
        if (primaryExpNode.getExpNode() != null) {
            return visitExp(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getlValNode() != null) {


            Value ptr = visitLVal(primaryExpNode.getlValNode());
            if (isGlobalField()) return ptr;

            if (ptr.dim > 0 && !ptr.isPtr) {
                //说明是数组并且还未转化为指针
                Value tempPtr = new Value(Register.getRegister());
                tempPtr.isPtr = true;
                tempPtr.setType(ptr.type);

                addInstruction(new Getelementptr(tempPtr, ptr));
                ptr = tempPtr;
            }
            if (ptr.dim > 0) return ptr;
            Var value = new Var(Register.getRegister(), false, null, ptr.type);

            if (!isGlobalField())
                addInstruction(new Load(value, ptr));
            return value;

        }
        if (primaryExpNode.getNumberNode() != null) {
            return visitNumber(primaryExpNode.getNumberNode());
        }
        return null;


    }


    //Number → IntConst
    Value visitNumber(NumberNode numberNode) {
        return new Const(numberNode.getIntConst().getValue());
    }

    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
    //        | UnaryOp UnaryExp

    Value visitUnaryExp(UnaryExpNode unaryExpNode) {
        if (unaryExpNode.getPrimaryExpNode() != null) {
            return visitPrimaryExp(unaryExpNode.getPrimaryExpNode());
        }
        if (unaryExpNode.getIdent() != null) {
//            visitFuncRParams(unaryExpNode.getFuncRParamsNode());
            Function funcSymbol = (Function) get(unaryExpNode.getIdent().getValue());
            Var result = null;
            if (funcSymbol.type != ValueType.VOID)
                result = new Var(Register.getRegister(), false, null, funcSymbol.type);
            if (!funcSymbol.funcParams.isEmpty()) if (result != null) {
                result.setType(funcSymbol.funcParams.get(0).getType());
            }
            Call call = new Call(result, funcSymbol);

            if (unaryExpNode.getFuncRParamsNode() != null)
                for (ExpNode expNode : unaryExpNode.getFuncRParamsNode().getAllExpNodeList()) {
                    call.addPara(visitExp(expNode));
                }

            addInstruction(call);
            return result;


        }
        if (unaryExpNode.getUnaryExpNode() != null) {
            //UnaryOp UnaryExp
            OpValue op = null;
            if (unaryExpNode.getUnaryOpNode().getOp().getKey() == TokenType.PLUS) {
                op = new OpValue(Op.add);
            } else if (unaryExpNode.getUnaryOpNode().getOp().getKey() == TokenType.MINU) {
                op = new OpValue(Op.sub);
            } else if (unaryExpNode.getUnaryOpNode().getOp().getKey() == TokenType.NOT) {
                op = new OpValue(Op.ne);
            }
            return addBinaryInstruction(new Const(0), visitUnaryExp(unaryExpNode.getUnaryExpNode()), op);

        }
        return null;
    }

    //FuncRParams → Exp { ',' Exp }
    void visitFuncRParams(FuncRParamsNode funcRParamsNode) {
        for (ExpNode expNode : funcRParamsNode.getAllExpNodeList()) {
            visitExp(expNode);
        }
    }

    // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    Value visitMulExp(MulExpNode mulExpNode) {
        if (mulExpNode.getAllUnaryExpNode().size() == 1) return visitUnaryExp(mulExpNode.getAllUnaryExpNode().get(0));
        else {
            Value value1 = visitUnaryExp(mulExpNode.getAllUnaryExpNode().get(0));
            Value value2 = visitUnaryExp(mulExpNode.getAllUnaryExpNode().get(1));
            OpValue opValue = new OpValue(mulExpNode.getOpList().get(0).getValue());
            Value res = addBinaryInstruction(value1, value2, opValue);

            for (int i = 2; i < mulExpNode.getAllUnaryExpNode().size(); i++) {
                value1 = res;
                value2 = visitUnaryExp(mulExpNode.getAllUnaryExpNode().get(i));
                opValue = new OpValue(mulExpNode.getOpList().get(i - 1).getValue());
                res = addBinaryInstruction(value1, value2, opValue);
            }
            return res;
        }
    }

    public Value addBinaryInstruction(Value value1, Value value2, OpValue op) {
        if (isGlobalField()) {
            if (value1 instanceof Var) value1 = new Const(String.valueOf(((Var) value1).getInitValue(0)));
            if (value2 instanceof Var) value2 = new Const(String.valueOf(((Var) value2).getInitValue(0)));
        }
        if (value1 instanceof Const v1 && value2 instanceof Const v2) {
            switch (op.op) {
                case add -> {
                    return new Const(v1.value + v2.value);

                }
                case sub -> {
                    return new Const(v1.value - v2.value);
                }
                case mul -> {
                    return new Const(v1.value * v2.value);
                }
                case sdiv -> {
                    return new Const(v1.value / v2.value);
                }
                case srem -> {
                    return new Const(v1.value % v2.value);
                }
                case ne -> {

                    return new Const(v1.value == v2.value ? 1 : 0);
                }
            }
            throw new RuntimeException("op wrong");
        } else {
            Var var = new Var(Register.getRegister(), false, null, ValueType.i32);
            switch (op.op) {
                case add -> {
                    addInstruction(new Add(var, value1, value2, ValueType.i32));
                }
                case sub -> {
                    addInstruction(new Sub(var, value1, value2, ValueType.i32));
                }
                case mul -> {
                    addInstruction(new Mul(var, value1, value2, ValueType.i32));
                }
                case sdiv -> {
                    addInstruction(new Sdiv(var, value1, value2, ValueType.i32));
                }
                case srem -> {
                    addInstruction(new Srem(var, value1, value2, ValueType.i32));
                }
                case ne -> {
                    var.setType(ValueType.i1);
                    addInstruction(new Icmp(var, value1.getType(), new OpValue(Op.eq), value1, value2));
                }
            }

            return var;
        }
    }

    //AddExp → MulExp { ('+' | '−') MulExp }
    Value visitAddExp(AddExpNode addExpNode) {

        if (addExpNode.getAllMulExpNode().size() == 1) return visitMulExp(addExpNode.getAllMulExpNode().get(0));
        else {
            Value value1 = visitMulExp(addExpNode.getAllMulExpNode().get(0));
            Value value2 = visitMulExp(addExpNode.getAllMulExpNode().get(1));
            OpValue opValue = new OpValue(addExpNode.getOpList().get(0).getValue());
            Value res = addBinaryInstruction(value1, value2, opValue);

            for (int i = 2; i < addExpNode.getAllMulExpNode().size(); i++) {
                value1 = res;
                value2 = visitMulExp(addExpNode.getAllMulExpNode().get(i));
                opValue = new OpValue(addExpNode.getOpList().get(i - 1).getValue());
                res = addBinaryInstruction(value1, value2, opValue);
            }
            return res;
        }
    }

    Value zextTo32(Value value) {
        if (value.type != ValueType.i32) {
            Var newVar = new Var(Register.getRegister(), false, null, ValueType.i32);
            addInstruction(new Zext(newVar, value, ValueType.i32));
            return newVar;
        }
        return value;
    }


    //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    Value visitRelExp(RelExpNode relExpNode) {
        if (relExpNode.getAllAddExpNode().size() == 1) return visitAddExp(relExpNode.getAllAddExpNode().get(0));
        else {
            Value value1 = zextTo32(visitAddExp(relExpNode.getAllAddExpNode().get(0)));
            Value value2 = zextTo32(visitAddExp(relExpNode.getAllAddExpNode().get(1)));
            OpValue opValue = new OpValue(relExpNode.getOpList().get(0).getValue());
            Value res = new Var(Register.getRegister(), false, null, ValueType.i1);
            addInstruction(new Icmp(res, ValueType.i32, opValue, value1, value2));
            for (int i = 2; i < relExpNode.getAllAddExpNode().size(); i++) {
                value1 = zextTo32(res);
                value2 = zextTo32(visitAddExp(relExpNode.getAllAddExpNode().get(i)));
                opValue = new OpValue(relExpNode.getOpList().get(i - 1).getValue());
                res = new Var(Register.getRegister(), false, null, ValueType.i1);
                addInstruction(new Icmp(res, ValueType.i32, opValue, value1, value2));
            }
            return res;
        }

    }

    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    Value visitEqExp(EqExpNode eqExpNode) {
        if (eqExpNode.getAllRelExpNode().size() == 1) return visitRelExp(eqExpNode.getRelExpNode());
        else {
            Value value1 = zextTo32(visitRelExp(eqExpNode.getAllRelExpNode().get(0)));
            Value value2 = zextTo32(visitRelExp(eqExpNode.getAllRelExpNode().get(1)));
            OpValue opValue = new OpValue(eqExpNode.getOpList().get(0).getValue());

            Value res = new Var(Register.getRegister(), false, null, ValueType.i1);
            addInstruction(new Icmp(res, ValueType.i32, opValue, value1, value2));
            for (int i = 2; i < eqExpNode.getAllRelExpNode().size(); i++) {
                value1 = zextTo32(res);
                value2 = visitRelExp(eqExpNode.getAllRelExpNode().get(i));
                opValue = new OpValue(eqExpNode.getOpList().get(i - 1).getValue());
                res = new Var(Register.getRegister(), false, null, ValueType.i1);
                addInstruction(new Icmp(res, ValueType.i32, opValue, value1, value2));
            }
            return res;
        }
    }

    //LAndExp → EqExp { '&&' EqExp }
    Value visitLAndExp(LAndExpNode lAndExpNode, BasicBlock ifBlock, BasicBlock elseBlock, BasicBlock outBlock, BasicBlock fatherNextBlock) {
        Value value = null;
        if (lAndExpNode.getAllEqExpNode().size() == 1) return visitEqExp(lAndExpNode.getAllEqExpNode().get(0));
        for (int i = 0; i < lAndExpNode.getAllEqExpNode().size(); i++) {

            EqExpNode eqExpNode = lAndExpNode.getAllEqExpNode().get(i);
            BasicBlock nextBlock = (i == lAndExpNode.getAllEqExpNode().size() - 1 ? null : new BasicBlock());

            value = visitEqExp(eqExpNode);
            // 处理 if(0) if(a) 这种单个数字判断的情况
            if (value.getType() == ValueType.i32) {
                Var res = new Var(Register.getRegister(), false, null, ValueType.i1);
                addInstruction(new Icmp(res, ValueType.i32, new OpValue(Op.ne), value, new Const(0)));
                value = res;
            }
            BasicBlock trueBlock;
            // 对于与来说
            // 如果是最后一块儿，执行体作为正确跳转, 出判断作为错误跳转
            // 如果不是最后一块儿，新建下一块儿作为正确跳转，出判断作为错误跳转
            if (i == lAndExpNode.getAllEqExpNode().size() - 1) {
                trueBlock = ifBlock;
            } else {
                trueBlock = new BasicBlock();
                trueBlock.setName(Register.getRegister());
                thisFunction.basicBlocks.add(trueBlock);
            }
            // 如果是最后一个判断，下面还有或判断的话说明如果正确就跳到执行体，如果错误就跳到或
            if (fatherNextBlock != null) {
                thisBlock.addBr((new Br(value, trueBlock, fatherNextBlock)));
            } else if (elseBlock == null) {
                thisBlock.addBr((new Br(value, trueBlock, outBlock)));
            } else {
                thisBlock.addBr((new Br(value, trueBlock, elseBlock)));
            }
            if (i != lAndExpNode.getAllEqExpNode().size() - 1) thisBlock = trueBlock;


        }
        return value;
    }

    //LOrExp → LAndExp { '||' LAndExp }
    void visitLOrExp(LOrExpNode lOrExpNode, BasicBlock ifBlock, BasicBlock elseBlock, BasicBlock outBlock) {
        //只要有一个为真，就为真
        for (int i = 0; i < lOrExpNode.getAllLAndExpNode().size(); i++) {
            LAndExpNode lAndExpNode = lOrExpNode.getAllLAndExpNode().get(i);
            BasicBlock nextBlock = (i == lOrExpNode.getAllLAndExpNode().size() - 1 ? null : new BasicBlock());
            Value value = visitLAndExp(lAndExpNode, ifBlock, elseBlock, outBlock, nextBlock);
            // 处理 if(0) if(a) 这种单个数字判断的情况
            if (value.getType() == ValueType.i32) {
                Var res = new Var(Register.getRegister(), false, null, ValueType.i1);
                addInstruction(new Icmp(res, ValueType.i32, new OpValue(Op.ne), value, new Const(0)));
                value = res;
            }
            BasicBlock falseBlock;
            // 如果不是最后一块儿，执行体作为正确跳转，新建下一块儿判断作为错误跳转
            if (i == lOrExpNode.getAllLAndExpNode().size() - 1) {
                if (elseBlock != null) {
                    falseBlock = elseBlock;
                } else {
                    falseBlock = outBlock;
                }
            } else {
                falseBlock = nextBlock;
                nextBlock.setName(Register.getRegister());
                thisFunction.basicBlocks.add(nextBlock);
            }
            if (i != lOrExpNode.getAllLAndExpNode().size() - 1 || lAndExpNode.getAllEqExpNode().size() == 1) {
                thisBlock.addBr((new Br(value, ifBlock, falseBlock)));
            }
            thisBlock = falseBlock;
        }
    }

    //ConstExp → AddExp
    Value visitConstExp(ConstExpNode constExpNode) {
        return visitAddExp(constExpNode.getAddExpNode());
    }

}
