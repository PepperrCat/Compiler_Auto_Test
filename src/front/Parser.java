package front;

import Nodes.*;
import error.ErrorAnalyze;
import error.ErrorType;
import models.NodeType;
import models.Pair;
import models.TokenType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    static ArrayList<Pair> tokens = Laxer.tokens;
    static int index = 0;


    public static Map<NodeType, String> nodeTypeMap = new HashMap<NodeType, String>() {{
        put(NodeType.CompUnit, "<CompUnit>");
        put(NodeType.Decl, "<Decl>");
        put(NodeType.ConstDecl, "<ConstDecl>");
        put(NodeType.BType, "<BType>");
        put(NodeType.ConstDef, "<ConstDef>");
        put(NodeType.ConstInitVal, "<ConstInitVal>");
        put(NodeType.VarDecl, "<VarDecl>");
        put(NodeType.VarDef, "<VarDef>");
        put(NodeType.InitVal, "<InitVal>");
        put(NodeType.FuncDef, "<FuncDef>");
        put(NodeType.MainFuncDef, "<MainFuncDef>");
        put(NodeType.FuncType, "<FuncType>");
        put(NodeType.FuncFParams, "<FuncFParams>");
        put(NodeType.FuncFParam, "<FuncFParam>");
        put(NodeType.Block, "<Block>");
        put(NodeType.BlockItem, "<BlockItem>");
        put(NodeType.Stmt, "<Stmt>");
        put(NodeType.Exp, "<Exp>");
        put(NodeType.Cond, "<Cond>");
        put(NodeType.LVal, "<LVal>");
        put(NodeType.PrimaryExp, "<PrimaryExp>");
        put(NodeType.Number, "<Number>");
        put(NodeType.UnaryExp, "<UnaryExp>");
        put(NodeType.UnaryOp, "<UnaryOp>");
        put(NodeType.FuncRParams, "<FuncRParams>");
        put(NodeType.MulExp, "<MulExp>");
        put(NodeType.AddExp, "<AddExp>");
        put(NodeType.RelExp, "<RelExp>");
        put(NodeType.EqExp, "<EqExp>");
        put(NodeType.LAndExp, "<LAndExp>");
        put(NodeType.LOrExp, "<LOrExp>");
        put(NodeType.ConstExp, "<ConstExp>");
    }};
    public static Map<Type, String> nodeClassMap = new HashMap<Type, String>() {{
        put(CompUnitNode.class, "<CompUnit>");
        put(DeclNode.class, "<Decl>");
        put(ConstDeclNode.class, "<ConstDecl>");
        put(BTypeNode.class, "<BType>");
        put(ConstDefNode.class, "<ConstDef>");
        put(ConstInitValNode.class, "<ConstInitVal>");
        put(VarDeclNode.class, "<VarDecl>");
        put(VarDefNode.class, "<VarDef>");
        put(InitValNode.class, "<InitVal>");
        put(FuncDefNode.class, "<FuncDef>");
        put(MainFuncDefNode.class, "<MainFuncDef>");
        put(FuncTypeNode.class, "<FuncType>");
        put(FuncFParamsNode.class, "<FuncFParams>");
        put(FuncFParamNode.class, "<FuncFParam>");
        put(BlockNode.class, "<Block>");
        put(BlockItemNode.class, "<BlockItem>");
        put(StmtNode.class, "<Stmt>");
        put(ForStmtNode.class, "<ForStmt>");
        put(ExpNode.class, "<Exp>");
        put(CondNode.class, "<Cond>");
        put(LValNode.class, "<LVal>");
        put(PrimaryExpNode.class, "<PrimaryExp>");
        put(NumberNode.class, "<Number>");
        put(UnaryExpNode.class, "<UnaryExp>");
        put(UnaryOpNode.class, "<UnaryOp>");
        put(FuncRParamsNode.class, "<FuncRParams>");
        put(MulExpNode.class, "<MulExp>");
        put(AddExpNode.class, "<AddExp>");
        put(RelExpNode.class, "<RelExp>");
        put(EqExpNode.class, "<EqExp>");
        put(LAndExpNode.class, "<LAndExp>");
        put(LOrExpNode.class, "<LOrExp>");
        put(ConstExpNode.class, "<ConstExp>");
    }};


    static Pair getToken() {
        return tokens.get(index);
    }

    static TokenType getTokenType() {
        return tokens.get(index).getKey();
    }

    static TokenType getTokenType(int offset) {
        return tokens.get(index + offset).getKey();
    }

    static Pair getToken(int offset) {
        return tokens.get(index + offset);
    }


    static void nextToken() {
        index++;
    }

    private void addTokenEnd(List<Pair> list, TokenType tokenType) {
        Pair pair = tokenEnd(tokenType);
        if (pair != null) {

            list.add(pair);
        }

    }

    static Pair tokenEnd(TokenType tokenType) {
        Pair pair = null;
        if (getTokenType() == tokenType) {
            pair = getToken();
            nextToken();
            return pair;
        } else if (tokenType == TokenType.SEMICN) {
            ErrorAnalyze.errorAnalyze.addError(getToken(-1).getLine(), ErrorType.i);
            return null;
        } else if (tokenType == TokenType.RPARENT) {
            ErrorAnalyze.errorAnalyze.addError(getToken(-1).getLine(), ErrorType.j);
            return null;
        } else if (tokenType == TokenType.RBRACK) {
            ErrorAnalyze.errorAnalyze.addError(getToken(-1).getLine(), ErrorType.k);
            return null;
        } else
            throw new RuntimeException("error:" + getToken(-1) + " " + getToken() + " " + getToken(1) + " id:" + index + ",line" + getToken().getLine());
    }


    static boolean nowCon(String string) {
        return getToken().getValue().equals(string);
    }

    public static CompUnitNode startAnalyze() throws IllegalAccessException {
        Parser parser = new Parser();
        return parser.analyze();

    }

    private CompUnitNode analyze() throws IllegalAccessException {
        CompUnitNode compUnitNode = CompUnit();
        compUnitNode.print();

        return compUnitNode;
    }

    private CompUnitNode CompUnit() {
        //  CompUnit → {Decl} {FuncDef} MainFuncDef
        List<DeclNode> declNodeList = new ArrayList<>();
        List<FuncDefNode> funcDefNodeList = new ArrayList<>();
        MainFuncDefNode mainFuncDefNode = null;
        while (getTokenType(1) != TokenType.MAINTK && getTokenType(2) != TokenType.LPARENT) {
            DeclNode decl = Decl();
            declNodeList.add(decl);
        }
        while (getTokenType(1) != TokenType.MAINTK) {
            funcDefNodeList.add(FuncDef());
        }
        mainFuncDefNode = MainFuncDef();
        return new CompUnitNode(declNodeList, funcDefNodeList, mainFuncDefNode);
    }

    private DeclNode Decl() {
        //Decl → ConstDecl | VarDecl
        ConstDeclNode constDeclNode = null;
        VarDeclNode varDeclNode = null;
        if (getTokenType() == TokenType.CONSTTK) {
            constDeclNode = ConstDecl();
        } else varDeclNode = VarDecl();
        return new DeclNode(constDeclNode, varDeclNode);
    }

    private ConstDeclNode ConstDecl() {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        Pair constCon = null;
        BTypeNode bType = null;
        ConstDefNode constDef = null;
        List<Pair> commaList = new ArrayList<>();
        List<ConstDefNode> constDefNodeList = new ArrayList<>();
        Pair semicnCon = null;


        constCon = tokenEnd(TokenType.CONSTTK);

        bType = BType();
        constDef = ConstDef();
        while (getTokenType() == TokenType.COMMA) {


//            commaList.add(tokenEnd(TokenType.COMMA));
            addTokenEnd(commaList, TokenType.COMMA);
            ConstDefNode constDefNode = ConstDef();
            constDefNodeList.add(constDefNode);
        }

        semicnCon = tokenEnd(TokenType.SEMICN);

        return new ConstDeclNode(constCon, bType, constDef, commaList, constDefNodeList, semicnCon);

    }

    private BTypeNode BType() {

        Pair intCon = null;
        if (getTokenType() == TokenType.INTTK)
            intCon = tokenEnd(TokenType.INTTK);
        else if (getTokenType() == TokenType.HINTTK)
            intCon = tokenEnd(TokenType.HINTTK);
        else if (getTokenType() == TokenType.CHARTK)
            intCon = tokenEnd(TokenType.CHARTK);

        return new BTypeNode(intCon);
    }

    private ConstDefNode ConstDef() {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        Pair ident = null;
        List<Pair> lBrackList = new ArrayList<>();
        List<ConstExpNode> constExpNodeList = new ArrayList<>();
        List<Pair> rBrackList = new ArrayList<>();
        Pair assignCon = null;
        ConstInitValNode constInitValNode = null;

        ident = tokenEnd(TokenType.IDENFR);

        while (getTokenType() == TokenType.LBRACK) {
//            lBrackList.add(tokenEnd(TokenType.LBRACK));
            addTokenEnd(lBrackList, TokenType.LBRACK);
            constExpNodeList.add(ConstExp());
//            rBrackList.add(tokenEnd(TokenType.RBRACK));
            addTokenEnd(rBrackList, TokenType.RBRACK);
        }
        assignCon = tokenEnd(TokenType.ASSIGN);
        constInitValNode = ConstInitVal();

        return new ConstDefNode(ident, lBrackList, constExpNodeList, rBrackList, assignCon, constInitValNode);

    }

    private ConstInitValNode ConstInitVal() {
        //  ConstInitVal → ConstExp |
        //  '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        ConstExpNode constExpNode = null;

        Pair lbrace = null;
        ConstInitValNode constInitValNode = null;
        List<Pair> commaList = new ArrayList<>();
        List<ConstInitValNode> constInitValNodeList = new ArrayList<>();
        Pair rbrace = null;


        if (getTokenType() != TokenType.LBRACE) constExpNode = ConstExp();
        else {
            lbrace = tokenEnd(TokenType.LBRACE);
            if (getTokenType() != TokenType.RBRACE) {
                //  '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
                constInitValNode = ConstInitVal();
                while (getTokenType() == TokenType.COMMA) {
//                    commaList.add(tokenEnd(TokenType.COMMA));
                    addTokenEnd(commaList, TokenType.COMMA);
                    ConstInitValNode constInitValNode1 = ConstInitVal();
                    constInitValNodeList.add(constInitValNode1);
                }
            }
            rbrace = tokenEnd(TokenType.RBRACE);

        }
        return new ConstInitValNode(constExpNode, lbrace, constInitValNode, commaList, constInitValNodeList, rbrace);
    }

    private VarDeclNode VarDecl() {
        //VarDecl → BType VarDef { ',' VarDef } ';'
        BTypeNode bTypeNode = null;
        VarDefNode varDefNode = null;
        List<Pair> commaList = new ArrayList<>();
        List<VarDefNode> varDefNodeList = new ArrayList<>();
        Pair semicn = null;

        bTypeNode = BType();
        varDefNode = VarDef();
        while (getTokenType() == TokenType.COMMA) {
//            commaList.add(tokenEnd(TokenType.COMMA));
            addTokenEnd(commaList, TokenType.COMMA);
            varDefNodeList.add(VarDef());
        }
        semicn = tokenEnd(TokenType.SEMICN);
        return new VarDeclNode(bTypeNode, varDefNode, commaList, varDefNodeList, semicn);
    }

    private VarDefNode VarDef() {
        //VarDef → Ident { '[' ConstExp ']' }
        //       | Ident { '[' ConstExp ']' } '=' InitVal
        //VarDef → Ident { '[' ConstExp ']' }('=' InitVal | e)
        Pair ident;
        List<Pair> lbrackList = new ArrayList<>();
        List<ConstExpNode> constExpNodeList = new ArrayList<>();
        List<Pair> rbrackList = new ArrayList<>();
        Pair assignCon = null;
        InitValNode initValNode = null;

        ident = tokenEnd(TokenType.IDENFR);
        while (getTokenType() == TokenType.LBRACK) {
//            lbrackList.add(tokenEnd(TokenType.LBRACK));
            addTokenEnd(lbrackList, TokenType.LBRACK);
            constExpNodeList.add(ConstExp());
//            rbrackList.add(tokenEnd(TokenType.RBRACK));
            addTokenEnd(rbrackList, TokenType.RBRACK);
        }
        if (getTokenType() == TokenType.ASSIGN) {
            assignCon = tokenEnd(TokenType.ASSIGN);
            initValNode = InitVal();
        }
        return new VarDefNode(ident, lbrackList, constExpNodeList, rbrackList, assignCon, initValNode);


    }

    private InitValNode InitVal() {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        ExpNode expNode = null;
        Pair lbrace = null;
        InitValNode initValNode = null;
        List<Pair> commaList = new ArrayList<>();
        List<InitValNode> initValNodeList = new ArrayList<>();
        Pair rbrace = null;

        if (getTokenType() != TokenType.LBRACE)
            expNode = Exp();
        else {
            lbrace = tokenEnd(TokenType.LBRACE);
            if (getTokenType() != TokenType.RBRACE) {
                initValNode = InitVal();
                while (getTokenType() == TokenType.COMMA) {
//                    commaList.add(tokenEnd(TokenType.COMMA));
                    addTokenEnd(commaList, TokenType.COMMA);
                    initValNodeList.add(InitVal());
                }
            }
            rbrace = tokenEnd(TokenType.RBRACE);
        }
        return new InitValNode(expNode, lbrace, initValNode, commaList, initValNodeList, rbrace);


    }

    private FuncDefNode FuncDef() {
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        FuncTypeNode funcTypeNode = null;
        Pair ident = null;
        Pair lparent = null;
        FuncFParamsNode funcFParamsNode = null;
        Pair rparent = null;
        BlockNode blockNode = null;

        funcTypeNode = FuncType();
        ident = tokenEnd(TokenType.IDENFR);
        lparent = tokenEnd(TokenType.LPARENT);
        if (getTokenType() != TokenType.RPARENT &&
//                getTokenType() == TokenType.INTTK&&
                TokenType.varTypeTk().contains(getTokenType())
        ) funcFParamsNode = FuncFParams();
        rparent = tokenEnd(TokenType.RPARENT);
        blockNode = Block();
        return new FuncDefNode(funcTypeNode, ident, lparent, funcFParamsNode, rparent, blockNode);
    }

    private MainFuncDefNode MainFuncDef() {
        //MainFuncDef → 'int' 'main' '(' ')' Block
        Pair intCon = tokenEnd(TokenType.INTTK);
        Pair mainCon = tokenEnd(TokenType.MAINTK);
        Pair lparent = tokenEnd(TokenType.LPARENT);
        Pair rparent = tokenEnd(TokenType.RPARENT);
        BlockNode blockNode = Block();
        return new MainFuncDefNode(intCon, mainCon, lparent, rparent, blockNode);
    }

    private FuncTypeNode FuncType() {
        //FuncType → 'void' | 'int'
        Pair voidCon = null;
        Pair intCon = null;
        Pair charCon = null;
        if (getTokenType() == TokenType.VOIDTK) {
            voidCon = tokenEnd(TokenType.VOIDTK);
        } else if (getTokenType() == TokenType.CHARTK) {
            charCon = tokenEnd(TokenType.CHARTK);
        } else intCon = tokenEnd(TokenType.INTTK);
        return new FuncTypeNode(voidCon, intCon, charCon);
    }

    private FuncFParamsNode FuncFParams() {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        FuncFParamNode funcFParamNode = null;
        List<Pair> commaList = new ArrayList<>();
        List<FuncFParamNode> funcFParamNodeList = new ArrayList<>();
        funcFParamNode = FuncFParam();
        while (getTokenType() == TokenType.COMMA) {
//            commaList.add(tokenEnd(TokenType.COMMA));
            addTokenEnd(commaList, TokenType.COMMA);
            funcFParamNodeList.add(FuncFParam());
        }
        return new FuncFParamsNode(funcFParamNode, commaList, funcFParamNodeList);
    }

    private FuncFParamNode FuncFParam() {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        BTypeNode bTypeNode = null;
        Pair ident = null;
        Pair lbrack = null;
        Pair rbrack = null;
        List<Pair> lbrackList = new ArrayList<>();

        List<ConstExpNode> constExpNodeList = new ArrayList<>();
        List<Pair> rbrackList = new ArrayList<>();

        bTypeNode = BType();
        ident = tokenEnd(TokenType.IDENFR);
        if (getTokenType() == TokenType.LBRACK) {
            lbrack = tokenEnd(TokenType.LBRACK);
            rbrack = tokenEnd(TokenType.RBRACK);
            while (getTokenType() == TokenType.LBRACK) {
//                lbrackList.add(tokenEnd(TokenType.LBRACK));
                addTokenEnd(lbrackList, TokenType.LBRACK);
                constExpNodeList.add(ConstExp());
//                rbrackList.add(tokenEnd(TokenType.RBRACK));
                addTokenEnd(rbrackList, TokenType.RBRACK);
            }
        }
        return new FuncFParamNode(bTypeNode, ident, lbrack, rbrack, lbrackList, constExpNodeList, rbrackList);
    }

    private BlockNode Block() {
        //Block → '{' { BlockItem } '}'
        Pair lbrace = null;
        List<BlockItemNode> blockItemNodeList = new ArrayList<>();
        Pair rbrace = null;

        lbrace = tokenEnd(TokenType.LBRACE);
        while (getTokenType() != TokenType.RBRACE)
            blockItemNodeList.add(BlockItem());
        rbrace = tokenEnd(TokenType.RBRACE);
        return new BlockNode(lbrace, blockItemNodeList, rbrace);

    }

    private BlockItemNode BlockItem() {
        //BlockItem → Decl | Stmt
        DeclNode declNode = null;
        StmtNode stmtNode = null;
        if (getTokenType() == TokenType.CONSTTK || TokenType.varTypeTk().contains(getTokenType())) declNode = Decl();
        else stmtNode = Stmt();
        return new BlockItemNode(declNode, stmtNode);
    }

    private StmtNode Stmt() {
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

        LValNode lValNode = null;
        Pair assignCon = null;
        ExpNode lValExpNode = null;
        Pair getintCon = null;
        Pair getintLParent = null;
        Pair getintRParent = null;
        Pair lValSemicn = null;

        //  | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        Pair ifCon = null;
        Pair ifLparentCon = null;
        CondNode ifCondNode = null;
        Pair ifRparentCon = null;
        StmtNode ifStmtNode = null;
        Pair elseCon = null;
        StmtNode elseStmtNode = null;

        //  | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        Pair forCon = null;
        Pair forLparentCon = null;
        ForStmtNode startForStmtNode = null;
        Pair controlSemicn1 = null;
        CondNode forCondNode = null;
        Pair controlSemicn2 = null;
        ForStmtNode endForStmtNode = null;
        Pair forRparentCon = null;
        StmtNode forStmt = null;

        //  | 'break' ';'
        Pair breakCon = null;
        Pair breakSemicn = null;

        //  | 'continue' ';'
        Pair continueCon = null;
        Pair continueSemicn = null;

        //  | 'return' [Exp] ';'
        Pair returnCon = null;
        ExpNode returnExp = null;
        Pair returnSemicn = null;

        //  | 'printf''('FormatString{','Exp}')'';'
        Pair printfCon = null;
        Pair printfLparent = null;
        Pair formatString = null;
        List<Pair> printfCommaList = new ArrayList<>();
        List<ExpNode> printfExpList = new ArrayList<>();
        Pair printfRparent = null;
        Pair printfSemicn = null;

        //  | Block
        BlockNode blockNode = null;

        //  | [Exp] ';'
        ExpNode expNode = null;
        Pair expSemicn = null;

        boolean flag = false;
        int tmpIndex = index;

        if (getTokenType() == TokenType.IDENFR && getTokenType(1) != TokenType.LPARENT && getTokenType(1) != TokenType.SEMICN) {

            //  | LVal '=' (Exp | 'getint''('')') ';'
            lValNode = LVal();


            if (getTokenType() != TokenType.ASSIGN) {
                index = tmpIndex;
                flag = true;
            } else {
                assignCon = tokenEnd(TokenType.ASSIGN);
                if (getTokenType() != TokenType.GETINTTK) {
                    lValExpNode = Exp();
                } else {
                    getintCon = tokenEnd(TokenType.GETINTTK);
                    getintLParent = tokenEnd(TokenType.LPARENT);
                    getintRParent = tokenEnd(TokenType.RPARENT);
                }
                lValSemicn = tokenEnd(TokenType.SEMICN);
            }
        } else if (getTokenType() == TokenType.IFTK) {
            ifCon = tokenEnd(TokenType.IFTK);
            ifLparentCon = tokenEnd(TokenType.LPARENT);
            ifCondNode = Cond();
            ifRparentCon = tokenEnd(TokenType.RPARENT);
            ifStmtNode = Stmt();
            if (getTokenType() == TokenType.ELSETK) {
                elseCon = tokenEnd(TokenType.ELSETK);
                elseStmtNode = Stmt();
            }
        } else if (getTokenType() == TokenType.FORTK) {
            forCon = tokenEnd(TokenType.FORTK);
            forLparentCon = tokenEnd(TokenType.LPARENT);
            if (getTokenType() != TokenType.SEMICN) startForStmtNode = ForStmt();
            controlSemicn1 = tokenEnd(TokenType.SEMICN);
            if (getTokenType() != TokenType.SEMICN) forCondNode = Cond();
            controlSemicn2 = tokenEnd(TokenType.SEMICN);
            if (getTokenType() != TokenType.RPARENT) endForStmtNode = ForStmt();
            forRparentCon = tokenEnd(TokenType.RPARENT);
            forStmt = Stmt();
        } else if (getTokenType() == TokenType.BREAKTK) {
            breakCon = tokenEnd(TokenType.BREAKTK);
            breakSemicn = tokenEnd(TokenType.SEMICN);
        } else if (getTokenType() == TokenType.CONTINUETK) {
            continueCon = tokenEnd(TokenType.CONTINUETK);
            continueSemicn = tokenEnd(TokenType.SEMICN);
        } else if (getTokenType() == TokenType.RETURNTK) {
            returnCon = tokenEnd(TokenType.RETURNTK);
            if (getTokenType() != TokenType.SEMICN) returnExp = Exp();
            returnSemicn = tokenEnd(TokenType.SEMICN);
        } else if (getTokenType() == TokenType.PRINTFTK) {
            printfCon = tokenEnd(TokenType.PRINTFTK);
            printfLparent = tokenEnd(TokenType.LPARENT);
            formatString = tokenEnd(TokenType.STRCON);
            while (getTokenType() == TokenType.COMMA) {
//                printfCommaList.add(tokenEnd(TokenType.COMMA));
                addTokenEnd(printfCommaList, TokenType.COMMA);
                printfExpList.add(Exp());
            }
            printfRparent = tokenEnd(TokenType.RPARENT);
            printfSemicn = tokenEnd(TokenType.SEMICN);
        } else if (getTokenType() == TokenType.LBRACE) {
            blockNode = Block();
        } else {
            //  | [Exp] ';'
            if (getTokenType() != TokenType.SEMICN) expNode = Exp();
            expSemicn = tokenEnd(TokenType.SEMICN);
        }
        if (flag) {
            //  | [Exp] ';'
            lValNode = null;
            if (getTokenType() != TokenType.SEMICN) expNode = Exp();
            expSemicn = tokenEnd(TokenType.SEMICN);
        }
        return new StmtNode(
                lValNode,
                assignCon,
                lValExpNode,
                getintCon,
                getintLParent,
                getintRParent,
                lValSemicn,

                ifCon,
                ifLparentCon,
                ifCondNode,
                ifRparentCon,
                ifStmtNode,
                elseCon,
                elseStmtNode,

                forCon,
                forLparentCon,
                startForStmtNode,
                controlSemicn1,
                forCondNode,
                controlSemicn2,
                endForStmtNode,
                forRparentCon,
                forStmt,

                breakCon,
                breakSemicn,

                continueCon,
                continueSemicn,

                returnCon,
                returnExp,
                returnSemicn,

                printfCon,
                printfLparent,
                formatString,
                printfCommaList,
                printfExpList,
                printfRparent,
                printfSemicn,

                blockNode,

                expNode,
                expSemicn

        );


    }

    private ForStmtNode ForStmt() {
        //ForStmt → LVal '=' Exp
        LValNode lValNode = LVal();
        Pair assignCon = tokenEnd(TokenType.ASSIGN);
        ExpNode expNode = Exp();
        return new ForStmtNode(lValNode, assignCon, expNode);
    }

    private ExpNode Exp() {
        //Exp → AddExp 注：SysY 表达式是int 型表达式
        AddExpNode addExpNode = AddExp();
        return new ExpNode(addExpNode);
    }

    private CondNode Cond() {
        //Cond → LOrExp
        LOrExpNode lOrExpNode = LOrExp();
        return new CondNode(lOrExpNode);
    }

    private LValNode LVal() {
        //LVal → Ident {'[' Exp ']'}
        Pair ident = tokenEnd(TokenType.IDENFR);
        List<Pair> lbrakeList = new ArrayList<>();
        List<ExpNode> expNodeList = new ArrayList<>();
        List<Pair> rbrakeList = new ArrayList<>();
        while (getTokenType() == TokenType.LBRACK) {
//            lbrakeList.add(tokenEnd(TokenType.LBRACK));
            addTokenEnd(lbrakeList, TokenType.LBRACK);
            expNodeList.add(Exp());
//            rbrakeList.add(tokenEnd(TokenType.RBRACK));
            addTokenEnd(rbrakeList, TokenType.RBRACK);
        }
        return new LValNode(ident, lbrakeList, expNodeList, rbrakeList);
    }

    private PrimaryExpNode PrimaryExp() {
        //PrimaryExp → '(' Exp ')' | LVal | Number
        Pair lparent = null;
        ExpNode expNode = null;
        Pair rparent = null;
        LValNode lValNode = null;
        NumberNode numberNode = null;

        if (getTokenType() == TokenType.LPARENT) {
            lparent = tokenEnd(TokenType.LPARENT);
            expNode = Exp();
            rparent = tokenEnd(TokenType.RPARENT);
        } else if (getTokenType() == TokenType.IDENFR) {
            lValNode = LVal();
        } else numberNode = Number();
        return new PrimaryExpNode(
                lparent,
                expNode,
                rparent,
                lValNode,
                numberNode
        );

    }

    private NumberNode Number() {
        //Number → IntConst
        Pair intConst = null;
        if (getTokenType() == TokenType.INTCON)
            intConst = tokenEnd(TokenType.INTCON);
        else if (getTokenType() == TokenType.CHARCON)
            intConst = tokenEnd(TokenType.CHARCON);
        else intConst = tokenEnd(TokenType.HINTCON);
        return new NumberNode(intConst);
    }

    private UnaryExpNode UnaryExp() {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
        //  | UnaryOp UnaryExp
        PrimaryExpNode primaryExpNode = null;
        Pair ident = null;
        Pair lparent = null;
        FuncRParamsNode funcRParamsNode = null;
        Pair rparent = null;
        UnaryOpNode unaryOpNode = null;
        UnaryExpNode unaryExpNode = null;

        if (getTokenType() == TokenType.IDENFR && getTokenType(1) == TokenType.LPARENT) {
            ident = tokenEnd(TokenType.IDENFR);
            lparent = tokenEnd(TokenType.LPARENT);
            if (getTokenType() != TokenType.RPARENT && getTokenType() != TokenType.SEMICN)
                funcRParamsNode = FuncRParams();
            rparent = tokenEnd(TokenType.RPARENT);
        }

        else if (getTokenType() == TokenType.PLUS || getTokenType() == TokenType.MINU || getTokenType() == TokenType.NOT) {
            unaryOpNode = UnaryOp();
            unaryExpNode = UnaryExp();
        } else {
            primaryExpNode = PrimaryExp();
        }
        return new UnaryExpNode(primaryExpNode, ident, lparent, funcRParamsNode, rparent, unaryOpNode, unaryExpNode);


    }

    private UnaryOpNode UnaryOp() {
        //UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
        Pair op = null;
        if (getTokenType() == TokenType.PLUS) op = tokenEnd(TokenType.PLUS);
        else if (getTokenType() == TokenType.MINU) op = tokenEnd(TokenType.MINU);
        else op = tokenEnd(TokenType.NOT);
        return new UnaryOpNode(op);
    }

    private FuncRParamsNode FuncRParams() {
        //FuncRParams → Exp { ',' Exp }
        ExpNode expNode = null;
        List<Pair> semicnList = new ArrayList<>();
        List<ExpNode> expNodeList = new ArrayList<>();
        expNode = Exp();
        while (getTokenType() == TokenType.COMMA) {
//            semicnList.add(tokenEnd(TokenType.COMMA));
            addTokenEnd(semicnList, TokenType.COMMA);
            expNodeList.add(Exp());
        }
        return new FuncRParamsNode(expNode, semicnList, expNodeList);
    }

    private MulExpNode MulExp() {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        UnaryExpNode unaryExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<UnaryExpNode> unaryExpNodeList = new ArrayList<>();

        unaryExpNode = UnaryExp();
        while (getTokenType() == TokenType.MULT || getTokenType() == TokenType.MOD || getTokenType() == TokenType.DIV) {
            stringList.add("<MulExp>");
            if (getTokenType() == TokenType.MULT) //opList.add(tokenEnd(TokenType.MULT));
                addTokenEnd(opList, TokenType.MULT);
            else if (getTokenType() == TokenType.DIV)// opList.add(tokenEnd(TokenType.DIV));
                addTokenEnd(opList, TokenType.DIV);
            else //opList.add(tokenEnd(TokenType.MOD));
                addTokenEnd(opList, TokenType.MOD);
            unaryExpNodeList.add(UnaryExp());
        }
        return new MulExpNode(unaryExpNode, stringList, opList, unaryExpNodeList);


    }

    private AddExpNode AddExp() {
        //AddExp → MulExp | AddExp ('+' | '−') MulExp
        //AddExp → MulExp { ('+' | '−') MulExp }

        MulExpNode mulExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<MulExpNode> mulExpNodeArrayList = new ArrayList<>();

        mulExpNode = MulExp();
        while (getTokenType() == TokenType.PLUS || getTokenType() == TokenType.MINU) {
            stringList.add("<AddExp>");
            if (getTokenType() == TokenType.PLUS) //opList.add(tokenEnd(TokenType.PLUS));
                addTokenEnd(opList, TokenType.PLUS);
            else //opList.add(tokenEnd(TokenType.MINU));
                addTokenEnd(opList, TokenType.MINU);
            mulExpNodeArrayList.add(MulExp());
        }
        return new AddExpNode(mulExpNode, stringList, opList, mulExpNodeArrayList);
    }

    private RelExpNode RelExp() {
        //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        AddExpNode addExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<AddExpNode> addExpNodeArrayList = new ArrayList<>();

        addExpNode = AddExp();
        while (getTokenType() == TokenType.LSS || getTokenType() == TokenType.LEQ || getTokenType() == TokenType.GRE || getTokenType() == TokenType.GEQ) {
            stringList.add("<RelExp>");
            if (getTokenType() == TokenType.LSS) //opList.add(tokenEnd(TokenType.LSS));
                addTokenEnd(opList, TokenType.LSS);
            else if (getTokenType() == TokenType.LEQ) //opList.add(tokenEnd(TokenType.LEQ));
                addTokenEnd(opList, TokenType.LEQ);
            else if (getTokenType() == TokenType.GRE) //opList.add(tokenEnd(TokenType.GRE));
                addTokenEnd(opList, TokenType.GRE);
            else //opList.add(tokenEnd(TokenType.GEQ));
                addTokenEnd(opList, TokenType.GEQ);

            addExpNodeArrayList.add(AddExp());
        }
        return new RelExpNode(addExpNode, stringList, opList, addExpNodeArrayList);

    }

    private EqExpNode EqExp() {
        //EqExp → RelExp | EqExp ('==' | '!=') RelExp
        //EqExp → RelExp { ('==' | '!=') RelExp }
        RelExpNode relExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<RelExpNode> relExpNodeArrayList = new ArrayList<>();

        relExpNode = RelExp();
        while (getTokenType() == TokenType.EQL || getTokenType() == TokenType.NEQ) {
            stringList.add("<EqExp>");
            if (getTokenType() == TokenType.EQL) //opList.add(tokenEnd(TokenType.EQL));
                addTokenEnd(opList, TokenType.EQL);
            else //opList.add(tokenEnd(TokenType.NEQ));
                addTokenEnd(opList, TokenType.NEQ);
            relExpNodeArrayList.add(RelExp());
        }
        return new EqExpNode(relExpNode, stringList, opList, relExpNodeArrayList);
    }

    private LAndExpNode LAndExp() {
        //LAndExp → EqExp | LAndExp '&&' EqExp
        //LAndExp → EqExp {'&&' EqExp }

        EqExpNode eqExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<EqExpNode> eqExpNodeArrayList = new ArrayList<>();

        eqExpNode = EqExp();
        while (getTokenType() == TokenType.AND) {
            stringList.add("<LAndExp>");
            // opList.add(tokenEnd(TokenType.AND));
            addTokenEnd(opList, TokenType.AND);
            eqExpNodeArrayList.add(EqExp());
        }
        return new LAndExpNode(eqExpNode, stringList, opList, eqExpNodeArrayList);

    }

    private LOrExpNode LOrExp() {
        //LOrExp → LAndExp | LOrExp '||' LAndExp
        //LOrExp → LAndExp  { '||' LAndExp }

        LAndExpNode lAndExpNode = null;
        List<String> stringList = new ArrayList<>();
        List<Pair> opList = new ArrayList<>();
        List<LAndExpNode> lAndExpNodeArrayList = new ArrayList<>();

        lAndExpNode = LAndExp();
        while (getTokenType() == TokenType.OR) {
            stringList.add("<LOrExp>");
            // opList.add(tokenEnd(TokenType.OR));
            addTokenEnd(opList, TokenType.OR);
            lAndExpNodeArrayList.add(LAndExp());
        }
        return new LOrExpNode(lAndExpNode, stringList, opList, lAndExpNodeArrayList);
    }

    private ConstExpNode ConstExp() {
        // ConstExp → AddExp
        AddExpNode addExpNode = null;
        addExpNode = AddExp();
        return new ConstExpNode(addExpNode);
    }


}
