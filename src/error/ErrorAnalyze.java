package error;

import Nodes.*;
import Nodes.Method.Print;
import models.Pair;
import symbol.*;

import java.util.*;


public class ErrorAnalyze {
    private int loopCount = 0;

    private void loopAdd() {
        ++loopCount;
    }

    private void loopSub() {
        if (loopCount == 0) {
            addError(0, ErrorType.m);

        }
        --loopCount;
    }

    int tokenIndex = 0;

    void addToken() {
        tokenIndex++;
    }

    public static ErrorAnalyze errorAnalyze = new ErrorAnalyze();


    List<Error> errorList = new ArrayList<>();

    public void addError(int line, ErrorType errorType) {
        errorList.add(new Error(line, errorType));
    }

    public void printError() {
        Collections.sort(errorList, Comparator.comparingInt(Error::getLine));
        for (Error error : errorList) {
            Print.printError(error.toString());
        }
    }

    public static ErrorAnalyze getInstance() {
        return errorAnalyze;
    }

    public static void errorEnalyze(CompUnitNode compUnitNode) {
        getInstance().analyze(compUnitNode);
        getInstance().printError();
    }


    public void analyze(CompUnitNode compUnitNode) {
        CompUnitError(compUnitNode);
    }

    public void push(boolean isFunc, FuncType funcType) {
        SymbolStack.getInstance().push(new SymbolTable(new HashMap<>(), isFunc, funcType));
    }

    public SymbolTable pop() {
        return SymbolStack.getInstance().pop();
    }

    public SymbolTable top() {
        return SymbolStack.getInstance().getTop();
    }

    public void add(String name, Symbol symbol) {
        SymbolStack.getInstance().getTop().symbols.put(name, symbol);
    }

    public Symbol get(String name) {
        for (int i = SymbolStack.getInstance().stack.size() - 1; i >= 0; i--) {
            if (SymbolStack.getInstance().stack.get(i).symbols.containsKey(name)) {
                return SymbolStack.getInstance().stack.get(i).symbols.get(name);
            }
        }
        return null;
    }

    public Symbol getFieldSymbol(Pair ident) {
        if (SymbolStack.getInstance().getTop() != null)
            if (SymbolStack.getInstance().getTop().symbols.containsKey(ident.getValue())) {
                return SymbolStack.getInstance().getTop().symbols.get(ident.getValue());
            }
        return null;
    }

    public FuncType getFuncType() {
        for (int i = SymbolStack.getInstance().stack.size() - 1; i >= 0; i--) {
            if (SymbolStack.getInstance().stack.get(i).isFunc) {
                return SymbolStack.getInstance().stack.get(i).funcType;
            }
        }
        return null;
    }

    //CompUnit → {Decl} {FuncDef} MainFuncDef
    public void CompUnitError(CompUnitNode compUnitNode) {
        push(false, null);
//        add("getint", new FuncSymbol("getint", FuncType.INT, null));
//        add("printf", new FuncSymbol("printf", FuncType.INT, null));
        for (DeclNode declNode : compUnitNode.getDeclNodeList()) {
            DeclError(declNode);
        }
        for (FuncDefNode funcDefNode : compUnitNode.getFuncDefNodeList()) {
            FuncDefError(funcDefNode);
        }
        MainFuncDefError(compUnitNode.getMainFuncDefNode());
        pop();
    }

    //Decl → ConstDecl | VarDecl
    public void DeclError(DeclNode declNode) {
        if (declNode.getConstDeclNode() != null) {
            ConstDeclError(declNode.getConstDeclNode());
        } else VarDeclError(declNode.getVarDeclNode());
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i
    public void ConstDeclError(ConstDeclNode constDeclNode) {
        BTypeError(constDeclNode.getbType());
        ConstDefError(constDeclNode.getConstDef());
        for (ConstDefNode constDefNode : constDeclNode.getConstDefNodeList()) {
            ConstDefError(constDefNode);
        }

    }

    //BType → 'int'
    public void BTypeError(BTypeNode bTypeNode) {
    }

    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal  // b k
    public void ConstDefError(ConstDefNode constDefNode) {
        if (getFieldSymbol(constDefNode.getIdent()) != null) {
            addError(constDefNode.getIdent().getLine(), ErrorType.b);
        }
        int d = 0;
        for (ConstExpNode constExpNode : constDefNode.getConstExpNodeList()) {
            ConstExpError(constExpNode);
            d++;
        }
        add(constDefNode.getIdent().getValue(), new VarSymbol(constDefNode.getIdent().getValue(), true, d));
        ConstInitValError(constDefNode.getConstInitValNode());
    }
//ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'

    public void ConstInitValError(ConstInitValNode constInitValNode) {
        if (constInitValNode.getConstExpNode() != null) {
            ConstExpError(constInitValNode.getConstExpNode());
        } else {
            for (ConstInitValNode constInitValNode1 : constInitValNode.getConstInitValNodeList()) {
                ConstInitValError(constInitValNode1);
            }
        }
    }

    //VarDecl → BType VarDef { ',' VarDef } ';' // i
    public void VarDeclError(VarDeclNode varDeclNode) {
        VarDefError(varDeclNode.getVarDefNode());
        for (VarDefNode varDefNode : varDeclNode.getVarDefNodeList()) {
            VarDefError(varDefNode);
        }
    }

    //VarDef → Ident { '[' ConstExp ']' } // b
//    | Ident { '[' ConstExp ']' } '=' InitVal // k
    public void VarDefError(VarDefNode varDefNode) {
        if (SymbolStack.getInstance().getTop().symbols.containsKey(varDefNode.getIdent().getValue())) {
            addError(varDefNode.getIdent().getLine(), ErrorType.b);
        }
        int d = 0;
        for (ConstExpNode constExpNode : varDefNode.getConstExpNodeList()) {
            ConstExpError(constExpNode);
            d++;
        }
        add(varDefNode.getIdent().getValue(), new VarSymbol(varDefNode.getIdent().getValue(), false, d));
        if (varDefNode.getInitValNode() != null) {
            InitValError(varDefNode.getInitValNode());
        }
    }

    // InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    public void InitValError(InitValNode initValNode) {
        if (initValNode.getExpNode() != null) {
            ExpError(initValNode.getExpNode());
        } else {
            if (initValNode.getInitValNode() != null) {
                InitValError(initValNode.getInitValNode());
                for (InitValNode initValNode1 : initValNode.getInitValNodeList()) {
                    InitValError(initValNode1);
                }
            }

        }
    }

    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b g j
    public void FuncDefError(FuncDefNode funcDefNode) {
        FuncTypeError(funcDefNode.getFuncTypeNode());
        if (getFieldSymbol(funcDefNode.getIdent()) != null) {
            addError(funcDefNode.getIdent().getLine(), ErrorType.b);
        }
        if (funcDefNode.getFuncFParamsNode() == null) {
            add(funcDefNode.getIdent().getValue(), new FuncSymbol(funcDefNode.getIdent().getValue(),
                    funcDefNode.getFuncTypeNode().getFuncType(),
                    new ArrayList<>()));
        } else {
            List<FuncParam> params = new ArrayList<>();
//            params.add(new )
            if (funcDefNode.getFuncFParamsNode().getFuncFParamNode() != null)
                params.add(new FuncParam(
                        funcDefNode.getFuncFParamsNode().getFuncFParamNode().getIdent().getValue(),
                        funcDefNode.getFuncFParamsNode().getFuncFParamNode().getDimension()));
            for (FuncFParamNode funcFParamNode : funcDefNode.getFuncFParamsNode().getFuncFParamNodeList()) {
                params.add(new FuncParam(funcFParamNode.getIdent().getValue(), funcFParamNode.getDimension()));
            }
            add(funcDefNode.getIdent().getValue(), new FuncSymbol(funcDefNode.getIdent().getValue(),
                    funcDefNode.getFuncTypeNode().getFuncType(),
                    params));
        }
//        if (funcDefNode.getRparent() == null) {
//            addError(funcDefNode.getIdent().getLine(), ErrorType.e);
//        }
        push(true, funcDefNode.getFuncTypeNode().getFuncType());
        if (funcDefNode.getFuncFParamsNode() != null) {
            FuncFParamsError(funcDefNode.getFuncFParamsNode());
        }
        BlockError(funcDefNode.getBlockNode());
        pop();
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block // g j
    public void MainFuncDefError(MainFuncDefNode mainFuncDefNode) {
        add("main", new FuncSymbol("main", FuncType.INT, new ArrayList<>()));
        push(true, FuncType.INT);
        BlockError(mainFuncDefNode.getBlockNode());
        pop();
    }

    //FuncType → 'void' | 'int'
    private void FuncTypeError(FuncTypeNode funcTypeNode) {
    }

    //FuncFParams → FuncFParam { ',' FuncFParam }
    public void FuncFParamsError(FuncFParamsNode FuncFParamsNode) {
        FuncFParamError(FuncFParamsNode.getFuncFParamNode());
        for (FuncFParamNode funcFParamNode : FuncFParamsNode.getFuncFParamNodeList()) {
            FuncFParamError(funcFParamNode);
        }
    }

    // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]  //   b k
    public void FuncFParamError(FuncFParamNode funcFParamNode) {
        if (SymbolStack.getInstance().getTop().symbols.containsKey(funcFParamNode.getIdent().getValue())) {
            addError(funcFParamNode.getIdent().getLine(), ErrorType.b);
        }
        add(funcFParamNode.getIdent().getValue(), new VarSymbol(funcFParamNode.getIdent().getValue(), false, funcFParamNode.getDimension()));
    }

    //Block → '{' {BlockItem} '}'
    private void BlockError(BlockNode blockNode) {
        for (BlockItemNode blockItemNode : blockNode.getBlockItemNodeList()) {
            BlockItemError(blockItemNode);
        }
        if (top().isFunc) {
            if (top().funcType == FuncType.INT) {
                if (blockNode.getBlockItemNodeList().isEmpty()
                        || (blockNode.getBlockItemNodeList().get(blockNode.getBlockItemNodeList().size() - 1).getStmtNode() == null
                        || blockNode.getBlockItemNodeList().get(blockNode.getBlockItemNodeList().size() - 1).getStmtNode().getReturnCon() == null
                )

                ) {
                    addError(blockNode.getRbrace().getLine(), ErrorType.g);
                }
            }
        }
    }

    // BlockItem → Decl | Stmt
    private void BlockItemError(BlockItemNode blockItemNode) {
        if (blockItemNode.getDeclNode() != null) {
            DeclError(blockItemNode.getDeclNode());
        } else StmtError(blockItemNode.getStmtNode());
    }

    //Stmt →
    //  | LVal '=' (Exp | 'getint''('')') ';'
    //  | [Exp] ';'
    //  | Block

    //  | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    //  | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //  | 'break' ';'
    //  | 'continue' ';'
    //  | 'return' [Exp] ';'
    //  | 'printf''('FormatString{','Exp}')'';'
    private void StmtError(StmtNode stmtNode) {
        if (stmtNode.getlValNode() != null) {
            LValError(stmtNode.getlValNode());
            if (stmtNode.getGetintCon() != null) {
                if (get(stmtNode.getlValNode().getIdent().getValue()) instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) get(stmtNode.getlValNode().getIdent().getValue());
                    if (varSymbol.isConst) {
                        addError(stmtNode.getlValNode().getIdent().getLine(), ErrorType.h);
                    }
                }
            } else {
                if (get(stmtNode.getlValNode().getIdent().getValue()) instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) get(stmtNode.getlValNode().getIdent().getValue());
                    if (varSymbol.isConst) {
                        addError(stmtNode.getlValNode().getIdent().getLine(), ErrorType.h);
                    }
                }
                ExpError(stmtNode.getlValExpNode());
            }

        } else if (stmtNode.getExpNode() != null) {
            ExpError(stmtNode.getExpNode());
        } else if (stmtNode.getBlockNode() != null) {
            push(false, null);
            BlockError(stmtNode.getBlockNode());
            pop();
        } else if (stmtNode.getIfCon() != null) {
            CondError(stmtNode.getIfCondNode());
            StmtError(stmtNode.getIfStmtNode());
            if (stmtNode.getElseStmtNode() != null) {
                StmtError(stmtNode.getElseStmtNode());
            }
        } else if (stmtNode.getForCon() != null) {

            if (stmtNode.getStartForStmtNode() != null) {
                ForStmtError(stmtNode.getStartForStmtNode());
            }
            if (stmtNode.getForCondNode() != null) {
                CondError(stmtNode.getForCondNode());
            }
            if (stmtNode.getStartForStmtNode() != null) {
                ForStmtError(stmtNode.getStartForStmtNode());
            }
//            if (stmtNode.getForRparentCon() == null) {
//                addError(stmtNode.getForCon().getLine(), ErrorType.e);
//            }
            loopCount++;
            StmtError(stmtNode.getForStmt());
            loopCount--;
        } else if (stmtNode.getReturnCon() != null) {
            if (getFuncType() == FuncType.VOID && stmtNode.getReturnExp() != null) {
                addError(stmtNode.getReturnCon().getLine(), ErrorType.f);
            } else if (getFuncType() == FuncType.INT && stmtNode.getReturnExp() == null) {
                addError(stmtNode.getReturnCon().getLine(), ErrorType.g);
            }
            if (stmtNode.getExpNode() != null) {
                ExpError(stmtNode.getReturnExp());
            }
        } else if (stmtNode.getContinueCon() != null) {
            if (loopCount == 0) {
                addError(stmtNode.getContinueCon().getLine(), ErrorType.m);
            }

        } else if (stmtNode.getBreakCon() != null) {
            if (loopCount == 0) {
                addError(stmtNode.getBreakCon().getLine(), ErrorType.m);
            }
        } else if (stmtNode.getPrintfCon() != null) {
            int num = 0;
            for (int i = 0; i < stmtNode.getFormatString().getValue().length() - 1; i++) {
                if (stmtNode.getFormatString().getValue().charAt(i) == '%' &&
                        stmtNode.getFormatString().getValue().charAt(i + 1) == 'd')
                    num++;
            }
            if (num != stmtNode.getPrintfExpList().size()) {
                addError(stmtNode.getPrintfCon().getLine(), ErrorType.l);
            }
            for (ExpNode expNode : stmtNode.getPrintfExpList()) {
                ExpError(expNode);
            }
        }
    }

    //ForStmt → LVal '=' Exp   //h
    private void ForStmtError(ForStmtNode forStmtNode) {
        LValError(forStmtNode.getlValNode());
        if (get(forStmtNode.getlValNode().getIdent().getValue()) instanceof VarSymbol) {
            VarSymbol varSymbol = (VarSymbol) get(forStmtNode.getlValNode().getIdent().getValue());
            if (varSymbol.isConst) {
                addError(forStmtNode.getlValNode().getIdent().getLine(), ErrorType.h);
            }
        }
        ExpError(forStmtNode.getExpNode());
    }

    //Exp → AddExp
    private void ExpError(ExpNode expNode) {
        AddExpError(expNode.getAddExpNode());
    }

    //Cond → LOrExp
    private void CondError(CondNode condNode) {
        LOrExpError(condNode.getlOrExpNode());
    }

    //LVal → Ident {'[' Exp ']'} // c k
    private void LValError(LValNode lValNode) {
        if (get(lValNode.getIdent().getValue()) == null) {

            addError(lValNode.getIdent().getLine(), ErrorType.c);
        }
        for (ExpNode expNode : lValNode.getExpNodeList()) {
            ExpError(expNode);
        }
    }

    //PrimaryExp → '(' Exp ')' | LVal | Number
    private void PrimaryExpError(PrimaryExpNode primaryExpNode) {
        if (primaryExpNode.getExpNode() != null) {
            ExpError(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getlValNode() != null) {
            LValError(primaryExpNode.getlValNode());
        } else if (primaryExpNode.getNumberNode() != null) {
            NumberError(primaryExpNode.getNumberNode());
        }
    }

    // Number → IntConst
    private void NumberError(NumberNode numberNode) {
    }

    //UnaryExp → PrimaryExp
    //        | Ident '(' [FuncRParams] ')' // c d e j
    //        | UnaryOp UnaryExp
    private void UnaryExpError(UnaryExpNode unaryExpNode) {
        if (unaryExpNode.getPrimaryExpNode() != null) {
            PrimaryExpError(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getUnaryOpNode() != null) {
            UnaryOpError(unaryExpNode.getUnaryOpNode());
            UnaryExpError(unaryExpNode.getUnaryExpNode());

        } else {
            if (get(unaryExpNode.getIdent().getValue()) == null) {
                addError(unaryExpNode.getIdent().getLine(), ErrorType.c);
            }
            FuncSymbol funcSymbol = (FuncSymbol) get(unaryExpNode.getIdent().getValue());
            if (unaryExpNode.getFuncRParamsNode() != null) {
                if (funcSymbol.getFuncParams().size() != unaryExpNode.getFuncRParamsNode().getExpNodeList().size() + 1) {
                    addError(unaryExpNode.getIdent().getLine(), ErrorType.d);
                } else {
                    List<Integer> funcFParamDimensions = new ArrayList<>();
                    for (FuncParam funcParam : funcSymbol.getFuncParams()) {
                        funcFParamDimensions.add(funcParam.getDimension());
                    }
                    List<Integer> funcRParamDimensions = new ArrayList<>();
                    if (unaryExpNode.getFuncRParamsNode() != null) {
                        FuncRParamsError(unaryExpNode.getFuncRParamsNode());

                        for (ExpNode expNode : unaryExpNode.getFuncRParamsNode().getAllExpNodeList()) {
                            FuncParam funcRParam = getFuncParamInExp(expNode);
                            if (funcRParam != null) {
                                if (funcRParam.getName() == null) {
                                    funcRParamDimensions.add(funcRParam.getDimension());
                                } else {
                                    Symbol symbol2 = get(funcRParam.getName());
                                    if (symbol2 instanceof VarSymbol) {
                                        funcRParamDimensions.add(((VarSymbol) symbol2).dimension - funcRParam.getDimension());
                                    } else if (symbol2 instanceof FuncSymbol) {
                                        funcRParamDimensions.add(((FuncSymbol) symbol2).getType() == FuncType.VOID ? -1 : 0);
                                    }
                                }
                            }
                        }
                        if (!Objects.equals(funcFParamDimensions, funcRParamDimensions)) {
                            addError(unaryExpNode.getIdent().getLine(), ErrorType.e);
                        }
                    }
                }
                FuncRParamsError(unaryExpNode.getFuncRParamsNode());
            }
        }
    }

    private FuncParam getFuncParamInExp(ExpNode expNode) {
        // Exp -> AddExp
        return getFuncParamInAddExp(expNode.getAddExpNode());
    }

    private FuncParam getFuncParamInAddExp(AddExpNode addExpNode) {
        // AddExp -> MulExp | MulExp ('+' | '-') AddExp
        return getFuncParamInMulExp(addExpNode.getMulExpNode());
    }

    private FuncParam getFuncParamInMulExp(MulExpNode mulExpNode) {
        return getFuncParamInUnaryExp(mulExpNode.getUnaryExpNode());
    }

    private FuncParam getFuncParamInUnaryExp(UnaryExpNode unaryExpNode) {
        if (unaryExpNode.getPrimaryExpNode() != null) {
            return getFuncParamInPrimaryExp(unaryExpNode.getPrimaryExpNode());
        } else if (unaryExpNode.getIdent() != null) {
            return get(unaryExpNode.getIdent().getValue()) instanceof FuncSymbol ? new FuncParam(unaryExpNode.getIdent().getValue(), 0) : null;
        } else {
            return getFuncParamInUnaryExp(unaryExpNode.getUnaryExpNode());
        }
    }

    private FuncParam getFuncParamInPrimaryExp(PrimaryExpNode primaryExpNode) {
        // PrimaryExp -> '(' Exp ')' | LVal | Number
        if (primaryExpNode.getExpNode() != null) {
            return getFuncParamInExp(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getlValNode() != null) {
            return getFuncParamInLVal(primaryExpNode.getlValNode());
        } else {
            return new FuncParam(null, 0);
        }
    }

    private FuncParam getFuncParamInLVal(LValNode lValNode) {
        return new FuncParam(lValNode.getIdent().getValue(), lValNode.getExpNodeList().size());
    }

    // UnaryOp → '+' | '−' | '!'
    private void UnaryOpError(UnaryOpNode unaryOpNode) {
    }

    //FuncRParams → Exp { ',' Exp }
    private void FuncRParamsError(FuncRParamsNode funcRParamsNode) {
        ExpError(funcRParamsNode.getExpNode());
        for (ExpNode expNode : funcRParamsNode.getExpNodeList()) {
            ExpError(expNode);
        }
    }

    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private void MulExpError(MulExpNode mulExpNode) {
        if (mulExpNode.getUnaryExpNode() != null) {
            UnaryExpError(mulExpNode.getUnaryExpNode());
        }
        for (int i = 0; i < mulExpNode.getUnaryExpNodeList().size(); i++) {
            UnaryExpError(mulExpNode.getUnaryExpNodeList().get(i));
        }
    }

    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    private void AddExpError(AddExpNode addExpNode) {
        if (addExpNode.getMulExpNode() != null) {
            MulExpError(addExpNode.getMulExpNode());
        }
        for (int i = 0; i < addExpNode.getMulExpNodeArrayList().size(); i++) {
            MulExpError(addExpNode.getMulExpNodeArrayList().get(i));
        }
    }

    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private void RelExpError(RelExpNode relExpNode) {
        if (relExpNode.getAddExpNode() != null) {
            AddExpError(relExpNode.getAddExpNode());
        }
        for (int i = 0; i < relExpNode.getAddExpNodeArrayList().size(); i++) {
            AddExpError(relExpNode.getAddExpNodeArrayList().get(i));
        }
    }

    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    private void EqExpError(EqExpNode eqExpNode) {
        if (eqExpNode.getRelExpNode() != null) {
            RelExpError(eqExpNode.getRelExpNode());
        }
        for (int i = 0; i < eqExpNode.getRelExpNodeArrayList().size(); i++) {
            RelExpError(eqExpNode.getRelExpNodeArrayList().get(i));
        }
    }

    //LAndExp → EqExp | LAndExp '&&' EqExp
    private void LAndExpError(LAndExpNode lAndExpNode) {
        if (lAndExpNode.getEqExpNode() != null) {
            EqExpError(lAndExpNode.getEqExpNode());
        }
        for (int i = 0; i < lAndExpNode.getEqExpNodeArrayList().size(); i++) {
            EqExpError(lAndExpNode.getEqExpNodeArrayList().get(i));
        }
    }

    //LOrExp → LAndExp | LOrExp '||' LAndExp
    private void LOrExpError(LOrExpNode lOrExpNode) {
        if (lOrExpNode.getlAndExpNode() != null) {
            LAndExpError(lOrExpNode.getlAndExpNode());
        }
        for (int i = 0; i < lOrExpNode.getlAndExpNodeArrayList().size(); i++) {
            LAndExpError(lOrExpNode.getlAndExpNodeArrayList().get(i));
        }
    }

    //ConstExp → AddExp
    private void ConstExpError(ConstExpNode constExpNode) {
        AddExpError(constExpNode.getAddExpNode());
    }


}
