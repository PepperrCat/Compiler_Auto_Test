package frontend.error;

import Config.Config;
import func.FunctionParam;
import func.FunctionType;
import io.IO;
import node.Node;
import node.NodeType;
import symbol.*;
import token.TokenType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class ErrorHandler {

    private static Map<Integer, Error> errorList = new TreeMap<>();
    private Node compUnitNode;

    public ErrorHandler(Node compUnitNode) {
        this.compUnitNode = compUnitNode;
    }

    public void printErrors() throws IOException {
        for (Map.Entry<Integer, Error> entry : errorList.entrySet()) {
            IO.write(Config.outFileMap.get("error"), entry.getValue().toString());
            System.out.print(entry.getValue().toString());
        }
    }

    public static boolean loop = false;

    public Map<Integer, Error> getErrorList() {
        return errorList;
    }

//    public static Stack<SymbolTable> symbolTables = new Stack<>();

    public SymbolTable curSymbolTable = new SymbolTable(null);

    public boolean isEndCompile() {
        return errorList.size() != 0;
    }
    public static void addError(Error e) {
        if (errorList.containsKey(e.getLine()))
            return;
        errorList.put(e.getLine(), e);
    }
    private static boolean inLoop() {
        return loop;
    }

    private static void setLoop(boolean loop) {
        ErrorHandler.loop = loop;
    }

    public void analyze() {
        travelNodeTree(compUnitNode);
    }

    public void travelNodeTree(Node node) {
        List<Node> subs = node.getSubNodes();
        if (subs != null && subs.size() != 0) {
            for (Node n : subs) {
                switch (n.getNodeType()) {
                    case FormatString -> {  // a
                        String fmt = n.getToken().getTokenContent();
                        for (int i = 1; i < fmt.length() - 1; i++) {
                            char nc = fmt.charAt(i);
                            if (nc == 32 || nc == 33 || nc >= 40 && nc <= 126) {
                                if (nc == '\\') {
                                    if (i + 1 > fmt.length() || fmt.charAt(i + 1) != 'n') {
                                        ErrorHandler.addError(new Error(n.getToken().getLine(), ErrorType.a));
                                    }
                                }
                            } else if (nc == '%') {
                                if (i + 1 > fmt.length() || fmt.charAt(i + 1) != 'd') {
                                    ErrorHandler.addError(new Error(n.getToken().getLine(), ErrorType.a));
                                }
                            } else {
                                ErrorHandler.addError(new Error(n.getToken().getLine(), ErrorType.a));
                            }
                        }
                    }
                    case ConstDef -> { // b
                        List<Node> constDefSubs = n.getSubNodes();
                        Node ident = constDefSubs.get(0);
                        Node constInitVal = constDefSubs.get(constDefSubs.size() - 1);
                        int dimension = (constDefSubs.size() - 3) / 3;
                        if (curSymbolTable.isSymbolInTable(ident))
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.b));
                        else
                            curSymbolTable.addConstArrSymbol(ident, dimension, constInitVal);
                    }
                    case VarDef -> { // b
                        List<Node> varDefSubs = n.getSubNodes();
                        Node ident = varDefSubs.get(0);
                        Node initVal = varDefSubs.get(varDefSubs.size() - 1);
                        int dimension = 0;
                        if (initVal.getNodeType() == NodeType.InitVal) {
                            dimension = varDefSubs.size() / 3 - 1;
                        } else {
                            dimension = (varDefSubs.size() - 1) / 3;
                            initVal = null;
                        }
                        if (curSymbolTable.isSymbolInTable(ident))
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.b));
                        else
                            curSymbolTable.addVarArrSymbol(ident, dimension, initVal);
                    }
                    case FuncDef -> { // b g
                        List<Node> funcDefSubs = n.getSubNodes();
                        Node funcType = funcDefSubs.get(0).getSubNodes().get(0);
                        Node ident = funcDefSubs.get(1);
                        Node funcFParams = funcDefSubs.get(3);
                        if (funcFParams.getNodeType() != NodeType.FuncFParams) {
                            funcFParams = null;
                        }
                        if (curSymbolTable.isSymbolInTable(ident))
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.b));
                        else
                            curSymbolTable.addFuncSymbol(ident, funcType.getToken().toFuncType(), funcFParams);
                        if (funcType.getToken().toFuncType() == FunctionType.INTTP) {
                            Node block = funcDefSubs.get(funcDefSubs.size() - 1);
                            int errLine = hasReturn(block);
                            if (errLine != -1) {
                                ErrorHandler.addError(new Error(errLine, ErrorType.g));
                            }
                        }
                        curSymbolTable = curSymbolTable.createANewSon();
                        travelNodeTree(n);
                        curSymbolTable = curSymbolTable.getFatherTable();
                        continue;
                    }
                    case FuncFParam -> { // b
                        List<Node> funcFParamSubs = n.getSubNodes();
                        Node ident = funcFParamSubs.get(1);
                        if (curSymbolTable.isSymbolInTable(ident))
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.b));
                        else {
                            int dimension = (funcFParamSubs.size() - 1) / 3;
                            curSymbolTable.addFParamSymbol(ident, dimension);
                        }
                    }
                    case LVal -> { // c
                        List<Node> lValSubs = n.getSubNodes();
                        Node ident = lValSubs.get(0);
                        if (curSymbolTable.isSymbolDefine(ident) == null)
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.c));
                    }
                    case UnaryExp -> { // c d e
                        List<Node> unaryExpSubs = n.getSubNodes();
                        Node ident = unaryExpSubs.get(0);
                        if (ident.getToken() != null && ident.getToken().getTokenType() == TokenType.IDENFR) {
                            Symbol symbol = curSymbolTable.isSymbolDefine(ident);
                            if (symbol == null) {
                                ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.c));
                            } else if (symbol.getFunctionFParams() != null) {
                                int RParamNum = 0;
                                Node funcRParams = unaryExpSubs.get(2);
                                if (unaryExpSubs.size() > 3) {
                                    RParamNum = (funcRParams.getSubNodes().size() + 1) / 2;
                                } else
                                    funcRParams = null;
                                if (RParamNum != symbol.getFunctionFParams().size())
                                    ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.d));
                                else {
                                    if (funcRParams != null) {
                                        List<FunctionParam> funcFParams = symbol.getFunctionFParams();
                                        List<Integer> funcRParamDimensions = new ArrayList<>();
                                        for (Node funcRParam : funcRParams.getSubNodes()) {
                                            if (funcRParam.getNodeType() == NodeType.Exp) {
                                                FunctionParam RParam = getFuncParamInExp(funcRParam);
                                                if (RParam != null) {
                                                    if (RParam.getName() == null) {
                                                        funcRParamDimensions.add(RParam.getD());
                                                    } else {
                                                        Symbol symbol2 = curSymbolTable.getSymbol(RParam.getName());
                                                        if (symbol2 != null && (symbol2.getSymbolType() == SymbolType.ArrSymbol || symbol2.getSymbolType() == SymbolType.FParam)) {
                                                            funcRParamDimensions.add(symbol2.getD() - RParam.getD());
                                                        } else if (symbol2 != null && symbol2.getSymbolType() == SymbolType.FuncSymbol) {
                                                            if (symbol2.getFunctionType() == FunctionType.VOIDTP)
                                                                funcRParamDimensions.add(-1);
                                                            else
                                                                funcRParamDimensions.add(0);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (funcFParams.size() != funcRParamDimensions.size()) {
                                            ErrorHandler.addError(new Error(unaryExpSubs.get(0).getToken().getLine(), ErrorType.d));
                                        } else {
                                            for (int i = 0; i < funcFParams.size(); i++) {
                                                if (funcFParams.get(i).getD() != funcRParamDimensions.get(i)) {
                                                    ErrorHandler.addError(new Error(unaryExpSubs.get(0).getToken().getLine(), ErrorType.e));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    case Stmt -> {  // f h l m
                        List<Node> stmtSubs = n.getSubNodes();
                        switch (stmtSubs.get(0).getNodeType()) {
                            case Block -> {
                                curSymbolTable = curSymbolTable.createANewSon();
                                travelNodeTree(n);
                                curSymbolTable = curSymbolTable.getFatherTable();
                                continue;
                            }
                            case LVal -> {
                                Node ident = stmtSubs.get(0).getSubNodes().get(0);
                                Symbol s = curSymbolTable.getSymbol(ident.getToken().getTokenContent());
                                if (s != null && s.isConst()) {
                                    ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.h));
                                }
                            }
                            case End -> {
                                switch (stmtSubs.get(0).getToken().getTokenType()) {
                                    case FORTK -> {
                                        if (stmtSubs.get(stmtSubs.size() - 1).getNodeType() == NodeType.Stmt) {
                                            boolean tag = inLoop();
                                            if (!tag)
                                                setLoop(true);
                                            travelNodeTree(n);
                                            setLoop(tag);
                                            continue;
                                        }
                                    }
                                    case CONTINUETK, BREAKTK -> {
                                        if (!ErrorHandler.loop) {
                                            ErrorHandler.addError(new Error(stmtSubs.get(0).getToken().getLine(), ErrorType.m));
                                        }
                                    }
                                    case RETURNTK -> {
                                        if (!curSymbolTable.isFuncNeedReturn() && stmtSubs.size() == 3) {
                                            ErrorHandler.addError(new Error(stmtSubs.get(0).getToken().getLine(), ErrorType.f));
                                        }
                                    }
                                    case PRINTFTK -> {
                                        int expNum = (stmtSubs.size() - 5) / 2;
                                        int FormatStringNum = 0;
                                        String fms = stmtSubs.get(2).getToken().getTokenContent();
                                        for (int i = 0; i < fms.length(); i++) {
                                            if (fms.charAt(i) == '%' && fms.charAt(i + 1) == 'd') {
                                                FormatStringNum++;
                                                i++;
                                            }
                                        }
                                        if (expNum != FormatStringNum) {
                                            ErrorHandler.addError(new Error(stmtSubs.get(2).getToken().getLine(), ErrorType.l));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    case ForStmt -> { // h
                        List<Node> forStmtSubs = n.getSubNodes();
                        Node ident = forStmtSubs.get(0).getSubNodes().get(0);
                        Symbol s = curSymbolTable.getSymbol(ident.getToken().getTokenContent());
                        if (s != null && s.isConst()) {
                            ErrorHandler.addError(new Error(ident.getToken().getLine(), ErrorType.h));
                        }
                    }
                    case MainFuncDef -> { // g
                        List<Node> mainDefSubs = n.getSubNodes();
                        Node main = mainDefSubs.get(1);
                        if (curSymbolTable.isSymbolInTable(main))   // 这个多半达不到
                            ErrorHandler.addError(new Error(main.getToken().getLine(), ErrorType.b));
                        else {
                            curSymbolTable.addFuncSymbol(main, FunctionType.INTTP, null);
                        }
                        int errLine = hasReturn(mainDefSubs.get(mainDefSubs.size() - 1));
                        if (errLine != -1) {
                            ErrorHandler.addError(new Error(errLine, ErrorType.g));
                        }
                        curSymbolTable = curSymbolTable.createANewSon();
                        travelNodeTree(n);
                        curSymbolTable = curSymbolTable.getFatherTable();
                        continue;
                    }

                }
                travelNodeTree(n);
            }
        }
    }

    private int hasReturn(Node block) {
        List<Node> blockItems = block.getSubNodes();
        Node lastBlockItem = blockItems.get(blockItems.size() - 2);
        int errLine = blockItems.get(blockItems.size() - 1).getToken().getLine();
        if (lastBlockItem.getNodeType() != NodeType.BlockItem) {
            return errLine;
        } else {
            Node lastStmt = lastBlockItem.getSubNodes().get(0);
            Node rettk = lastStmt.getSubNodes().get(0);
            if (rettk.getToken() == null || rettk.getToken().getTokenType() != TokenType.RETURNTK) {
                return errLine;
            }
        }
        return -1;
    }

    private FunctionParam getFuncParamInExp(Node expNode) {
        // Exp -> AddExp
        Node addExpNode = expNode.getSubNodes().get(0);
        Node mulExpNode = addExpNode.getSubNodes().get(0);
        Node unaryExpNode = mulExpNode.getSubNodes().get(0);
        return getFuncParamInUnaryExp(unaryExpNode);
    }

    private FunctionParam getFuncParamInUnaryExp(Node unaryExpNode) {
        // UnaryExp →
        // PrimaryExp
        // | Ident '(' [FuncRParams] ')'
        // | UnaryOp UnaryExp
        List<Node> subs = unaryExpNode.getSubNodes();
        if (subs.get(0).getNodeType() == NodeType.PrimaryExp) {
            return getFuncParamInPrimaryExp(subs.get(0));
        } else if (subs.get(0).getNodeType() == NodeType.End) {
            Symbol s = curSymbolTable.getSymbol(subs.get(0).getToken().getTokenContent());
            if (s != null && s.getSymbolType() == SymbolType.FuncSymbol) {
                return new FunctionParam(subs.get(0).getToken().getTokenContent(), 0);
            } else
                return null;
        } else {
            return getFuncParamInUnaryExp(subs.get(1));
        }
    }

    private FunctionParam getFuncParamInPrimaryExp(Node primaryExp) {
        // PrimaryExp -> '(' Exp ')' | LVal | Number
        List<Node> subs = primaryExp.getSubNodes();
        if (subs.get(0).getNodeType() == NodeType.End) {
            return getFuncParamInExp(subs.get(1));
        } else if (subs.get(0).getNodeType() == NodeType.LVal) {
            Node lValNode = subs.get(0);
            return new FunctionParam(lValNode.getSubNodes().get(0).getToken().getTokenContent(), (lValNode.getSubNodes().size() - 1) / 3);
        } else {
            return new FunctionParam(null, 0);
        }
    }
}