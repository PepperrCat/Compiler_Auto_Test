package frontend;

import frontend.error.Error;
import frontend.error.ErrorHandler;
import frontend.error.ErrorType;
import node.Node;
import node.NodeType;
import token.Token;
import token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;

    private int flag = 0;

    private Node compUnit;

    public Node getCompUnitNode() {
        return compUnit;
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analyze() {
        this.compUnit = parserCompUnit();
    }

    private Node parserCompUnit() { // CompUnit -> {Decl} {FuncDef} MainFuncDef
        List<Node> subs = new ArrayList<Node>();
        while (tokens.get(flag + 1).getTokenType() != TokenType.MAINTK && tokens.get(flag + 2).getTokenType() != TokenType.LPARENT) {
            subs.add(parserDecl());
        }
        while (tokens.get(flag + 1).getTokenType() != TokenType.MAINTK) {
            subs.add(parserFuncDef());
        }
        subs.add(parserMainFuncDef());
        return new Node(NodeType.CompUnit, subs);
    }

    private Node parserDecl() { // Decl -> ConstDecl | VarDecl
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.CONSTTK) {
            subs.add(parserConstDecl());
        } else {
            subs.add(parserVarDecl());
        }
        return new Node(NodeType.Decl, subs);
    }

    private Node parserConstDecl() { // ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.CONSTTK));
        subs.add(parserBType());
        subs.add(parserConstDef());
        while (tokens.get(flag).getTokenType() == TokenType.COMMA) {
            subs.add(match(TokenType.COMMA));
            subs.add(parserConstDef());
        }
        subs.add(match(TokenType.SEMICN));
        return new Node(NodeType.ConstDecl, subs);
    }

    private Node parserBType() { // BType -> 'int'
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.INTTK));
        return new Node(NodeType.BType, subs);
    }

    private Node parserConstDef() { // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.IDENFR));
        while (tokens.get(flag).getTokenType() == TokenType.LBRACK) {
            subs.add(match(TokenType.LBRACK));
            subs.add(parserConstExp());
            subs.add(match(TokenType.RBRACK));
        }
        subs.add(match(TokenType.ASSIGN));
        subs.add(parserConstInitVal());
        return new Node(NodeType.ConstDef, subs);
    }

    private Node parserConstInitVal() { // ConstInitVal -> ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.LBRACE) {
            subs.add(match(TokenType.LBRACE));
            if (tokens.get(flag).getTokenType() != TokenType.RBRACE) {
                subs.add(parserConstInitVal());
                while (tokens.get(flag).getTokenType() != TokenType.RBRACE) {
                    subs.add(match(TokenType.COMMA));
                    subs.add(parserConstInitVal());
                }
            }
            subs.add(match(TokenType.RBRACE));
        } else {
            subs.add(parserConstExp());
        }
        return new Node(NodeType.ConstInitVal, subs);
    }

    private Node parserVarDecl() {  // VarDecl -> BType VarDef { ',' VarDef } ';'
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserBType());
        subs.add(parserVarDef());
        while (tokens.get(flag).getTokenType() == TokenType.COMMA) {
            subs.add(match(TokenType.COMMA));
            subs.add(parserVarDef());
        }
        subs.add(match(TokenType.SEMICN));
        return new Node(NodeType.VarDecl, subs);
    }

    private Node parserVarDef() { // VarDef -> Ident { '[' ConstExp ']' } [ '=' InitVal ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.IDENFR));
        while (tokens.get(flag).getTokenType() == TokenType.LBRACK) {
            subs.add(match(TokenType.LBRACK));
            subs.add(parserConstExp());
            subs.add(match(TokenType.RBRACK));
        }
        if (tokens.get(flag).getTokenType() == TokenType.ASSIGN) {
            subs.add(match(TokenType.ASSIGN));
            subs.add(parserInitVal());
        }
        return new Node(NodeType.VarDef, subs);
    }

    private Node parserInitVal() { // InitVal -> Exp | '{' [ InitVal { ',' InitVal } ] '}'
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.LBRACE) {
            subs.add(match(TokenType.LBRACE));
            if (tokens.get(flag).getTokenType() != TokenType.RBRACE) {
                subs.add(parserInitVal());
                while (tokens.get(flag).getTokenType() != TokenType.RBRACE) {
                    subs.add(match(TokenType.COMMA));
                    subs.add(parserInitVal());
                }
            }
            subs.add(match(TokenType.RBRACE));
        } else {
            subs.add(parserExp());
        }
        return new Node(NodeType.InitVal, subs);
    }

    private Node parserFuncDef() { // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserFuncType());
        subs.add(match(TokenType.IDENFR));
        subs.add(match(TokenType.LPARENT));
        if (tokens.get(flag).getTokenType() == TokenType.INTTK) {
            subs.add(parserFuncFParams());
        }
        subs.add(match(TokenType.RPARENT));
        subs.add(parserBlock());
        return new Node(NodeType.FuncDef, subs);
    }

    private Node parserMainFuncDef() { // MainFuncDef -> 'int' 'main' '(' ')' Block
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.INTTK));
        subs.add(match(TokenType.MAINTK));
        subs.add(match(TokenType.LPARENT));
        subs.add(match(TokenType.RPARENT));
        subs.add(parserBlock());
        return new Node(NodeType.MainFuncDef, subs);
    }

    private Node parserFuncType() { // FuncType -> 'void' | 'int'
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.VOIDTK) {
            subs.add(match(TokenType.VOIDTK));
        } else {
            subs.add(match(TokenType.INTTK));
        }
        return new Node(NodeType.FuncType, subs);
    }

    private Node parserFuncFParams() { // FuncFParams -> FuncFParam { ',' FuncFParam }
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserFuncFParam());
        while (tokens.get(flag).getTokenType() == TokenType.COMMA) {
            subs.add(match(TokenType.COMMA));
            subs.add(parserFuncFParam());
        }
        return new Node(NodeType.FuncFParams, subs);
    }

    private Node parserFuncFParam() { // FuncFParam -> BType Ident [ '[' ']' { '[' ConstExp ']' }]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserBType());
        subs.add(match(TokenType.IDENFR));
        if (tokens.get(flag).getTokenType() == TokenType.LBRACK) {
            subs.add(match(TokenType.LBRACK));
            subs.add(match(TokenType.RBRACK));
            while (tokens.get(flag).getTokenType() == TokenType.LBRACK) {
                subs.add(match(TokenType.LBRACK));
                subs.add(parserConstExp());
                subs.add(match(TokenType.RBRACK));
            }
        }
        return new Node(NodeType.FuncFParam, subs);
    }

    private Node parserBlock() { // Block -> '{' { BlockItem } '}'
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.LBRACE));
        while (tokens.get(flag).getTokenType() != TokenType.RBRACE) {
            subs.add(parserBlockItem());
        }
        subs.add(match(TokenType.RBRACE));
        return new Node(NodeType.Block, subs);
    }

    private Node parserBlockItem() { // BlockItem -> Decl | Stmt
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.CONSTTK || tokens.get(flag).getTokenType() == TokenType.INTTK) {
            subs.add(parserDecl());
        } else {
            subs.add(parserStmt());
        }
        return new Node(NodeType.BlockItem, subs);
    }

    private Node parserStmt() {
        // Stmt -> LVal '=' Exp ';'
        //	| [Exp] ';'
        //	| Block
        //	| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //	| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        //	| 'break' ';' | 'continue' ';'
        //	| 'return' [Exp] ';'
        //	| LVal '=' 'getint' '(' ')' ';'
        //	| 'printf' '(' FormatString { ',' Exp } ')' ';'
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.LBRACE) { // Block
            subs.add(parserBlock());
        } else if (tokens.get(flag).getTokenType() == TokenType.PRINTFTK) { // 'printf' '(' FormatString { ',' Exp } ')' ';'
            subs.add(match(TokenType.PRINTFTK));
            subs.add(match(TokenType.LPARENT));
            subs.add(match(TokenType.STRCON));
            while (tokens.get(flag).getTokenType() == TokenType.COMMA) {
                subs.add(match(TokenType.COMMA));
                subs.add(parserExp());
            }
            subs.add(match(TokenType.RPARENT));
            subs.add(match(TokenType.SEMICN));
        } else if (tokens.get(flag).getTokenType() == TokenType.IFTK) { // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            subs.add(match(TokenType.IFTK));
            subs.add(match(TokenType.LPARENT));
            subs.add(parserCond());
            subs.add(match(TokenType.RPARENT));
            subs.add(parserStmt());
            if (tokens.get(flag).getTokenType() == TokenType.ELSETK) {
                subs.add(match(TokenType.ELSETK));
                subs.add(parserStmt());
            }
        } else if (tokens.get(flag).getTokenType() == TokenType.FORTK) { // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            subs.add(match(TokenType.FORTK));
            subs.add(match(TokenType.LPARENT));
            if (tokens.get(flag).getTokenType() != TokenType.SEMICN) {
                subs.add(parserForStmt());
            }
            subs.add(match(TokenType.SEMICN));
            if (tokens.get(flag).getTokenType() != TokenType.SEMICN) {
                subs.add(parserCond());
            }
            subs.add(match(TokenType.SEMICN));
            if (tokens.get(flag).getTokenType() != TokenType.RPARENT) {
                subs.add(parserForStmt());
            }
            subs.add(match(TokenType.RPARENT));
            subs.add(parserStmt());
        } else if (tokens.get(flag).getTokenType() == TokenType.BREAKTK) { // 'break' ';'
            subs.add(match(TokenType.BREAKTK));
            subs.add(match(TokenType.SEMICN));
        } else if (tokens.get(flag).getTokenType() == TokenType.CONTINUETK) { // 'continue' ';'
            subs.add(match(TokenType.CONTINUETK));
            subs.add(match(TokenType.SEMICN));
        } else if (tokens.get(flag).getTokenType() == TokenType.RETURNTK) { // 'return' [Exp] ';'
            subs.add(match(TokenType.RETURNTK));
            if (isExp()) {
                subs.add(parserExp());
            }
            subs.add(match(TokenType.SEMICN));
        } else {
            if (tokens.get(flag).getTokenType() == TokenType.IDENFR) {
                Token tmptoken = tokens.get(flag);
                int tmpflag = flag;
                Node n = parserLVal();
                if (tokens.get(flag).getTokenType() == TokenType.ASSIGN) {
                    // LVal '=' Exp ';'
                    // LVal '=' 'getint' '(' ')' ';'
                    subs.add(n);
                    subs.add(match(TokenType.ASSIGN));
                    if (tokens.get(flag).getTokenType() == TokenType.GETINTTK) {
                        subs.add(match(TokenType.GETINTTK));
                        subs.add(match(TokenType.LPARENT));
                        subs.add(match(TokenType.RPARENT));
                        subs.add(match(TokenType.SEMICN));
                    } else {
                        subs.add(parserExp());
                        subs.add(match(TokenType.SEMICN));
                    }
                } else {
                    flag = tmpflag;
                    // [Exp] ';'
                    subs.add(parserExp());
                    subs.add(match(TokenType.SEMICN));
                }
            } else {
                if (isExp()) {
                    subs.add(parserExp());
                }
                subs.add(match(TokenType.SEMICN));
            }
        }
        return new Node(NodeType.Stmt, subs);
    }

    private Node parserForStmt() {  // ForStmt → LVal '=' Exp
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserLVal());
        subs.add(match(TokenType.ASSIGN));
        subs.add(parserExp());
        return new Node(NodeType.ForStmt, subs);
    }

    private Node parserExp() { // Exp -> AddExp
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserAddExp());
        return new Node(NodeType.Exp, subs);
    }

    private Node parserCond() { // Cond -> LOrExp
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserLOrExp());
        return new Node(NodeType.Cond, subs);
    }

    private Node parserLVal() { // LVal → Ident {'[' Exp ']'}
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.IDENFR));
        while (tokens.get(flag).getTokenType() == TokenType.LBRACK) {
            subs.add(match(TokenType.LBRACK));
            subs.add(parserExp());
            subs.add(match(TokenType.RBRACK));
        }
        return new Node(NodeType.LVal, subs);
    }

    private Node parserPrimaryExp() { // PrimaryExp -> '(' Exp ')' | LVal | Number
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.LPARENT) {
            subs.add(match(TokenType.LPARENT));
            subs.add(parserExp());
            subs.add(match(TokenType.RPARENT));
        } else if (tokens.get(flag).getTokenType() == TokenType.INTCON) {
            subs.add(parserNumber());
        } else {
            subs.add(parserLVal());
        }
        return new Node(NodeType.PrimaryExp, subs);
    }

    private Node parserNumber() { // Number -> IntConst
        List<Node> subs = new ArrayList<Node>();
        subs.add(match(TokenType.INTCON));
        return new Node(NodeType.Number, subs);
    }

    private Node parserUnaryExp() { // UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.IDENFR && tokens.get(flag + 1).getTokenType() == TokenType.LPARENT) {  // Ident()
            subs.add(match(TokenType.IDENFR));
            subs.add(match(TokenType.LPARENT));
            if (isExp()) {
                subs.add(parserFuncRParams());
            }
            subs.add(match(TokenType.RPARENT));
        } else if (tokens.get(flag).getTokenType() == TokenType.PLUS
                || tokens.get(flag).getTokenType() == TokenType.MINU
                || tokens.get(flag).getTokenType() == TokenType.NOT) {
            subs.add(parserUnaryOp());
            subs.add(parserUnaryExp());

        } else {
            subs.add(parserPrimaryExp());
        }
        return new Node(NodeType.UnaryExp, subs);
    }

    private Node parserUnaryOp() { // UnaryOp -> '+' | '−' | '!'
        List<Node> subs = new ArrayList<Node>();
        if (tokens.get(flag).getTokenType() == TokenType.PLUS) {
            subs.add(match(TokenType.PLUS));
        } else if (tokens.get(flag).getTokenType() == TokenType.MINU) {
            subs.add(match(TokenType.MINU));
        } else {
            subs.add(match(TokenType.NOT));
        }
        return new Node(NodeType.UnaryOp, subs);
    }

    private Node parserFuncRParams() { // FuncRParams -> Exp { ',' Exp }
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserExp());
        while (tokens.get(flag).getTokenType() == TokenType.COMMA) {
            subs.add(match(TokenType.COMMA));
            subs.add(parserExp());
        }
        return new Node(NodeType.FuncRParams, subs);
    }

    private Node parserMulExp() { // MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp 改成 MulExp ->  UnaryExp [('*' | '/' | '%') MulExp]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserUnaryExp());
        if (tokens.get(flag).getTokenType() == TokenType.MULT || tokens.get(flag).getTokenType() == TokenType.DIV || tokens.get(flag).getTokenType() == TokenType.MOD) {
            subs.add(match(tokens.get(flag).getTokenType()));
            subs.add(parserMulExp());
        }
        return new Node(NodeType.MulExp, subs);
    }

    private Node parserAddExp() { // AddExp -> MulExp | AddExp ('+' | '−') MulExp 改成 AddExp -> MulExp [ ('+' | '−') AddExp ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserMulExp());
        if (tokens.get(flag).getTokenType() == TokenType.PLUS || tokens.get(flag).getTokenType() == TokenType.MINU) {
            subs.add(match(tokens.get(flag).getTokenType()));
            subs.add(parserAddExp());
        }
        return new Node(NodeType.AddExp, subs);
    }

    private Node parserRelExp() { // RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp 改成 RelExp -> AddExp [ ('<' | '>' | '<=' | '>=') RelExp ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserAddExp());
        if (tokens.get(flag).getTokenType() == TokenType.LSS || tokens.get(flag).getTokenType() == TokenType.GRE || tokens.get(flag).getTokenType() == TokenType.GEQ || tokens.get(flag).getTokenType() == TokenType.LEQ) {
            subs.add(match(tokens.get(flag).getTokenType()));
            subs.add(parserRelExp());
        }
        return new Node(NodeType.RelExp, subs);
    }

    private Node parserEqExp() { // EqExp -> RelExp | EqExp ('==' | '!=') RelExp 改成 EqExp -> RelExp [ ('==' | '!=') EqExp ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserRelExp());
        if (tokens.get(flag).getTokenType() == TokenType.EQL || tokens.get(flag).getTokenType() == TokenType.NEQ) {
            subs.add(match(tokens.get(flag).getTokenType()));
            subs.add(parserEqExp());
        }
        return new Node(NodeType.EqExp, subs);
    }

    private Node parserLAndExp() { // LAndExp -> EqExp | LAndExp '&&' EqExp 改成 LAndExp -> EqExp [ '&&' LAndExp ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserEqExp());
        if (tokens.get(flag).getTokenType() == TokenType.AND) {
            subs.add(match(TokenType.AND));
            subs.add(parserLAndExp());
        }
        return new Node(NodeType.LAndExp, subs);
    }

    private Node parserLOrExp() { // LOrExp -> LAndExp | LOrExp '||' LAndExp 改成 LOrExp -> LAndExp [ '||' LOrExp ]
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserLAndExp());
        if (tokens.get(flag).getTokenType() == TokenType.OR) {
            subs.add(match(TokenType.OR));
            subs.add(parserLOrExp());
        }
        return new Node(NodeType.LOrExp, subs);
    }

    private Node parserConstExp() { // ConstExp -> AddExp
        List<Node> subs = new ArrayList<Node>();
        subs.add(parserAddExp());
        return new Node(NodeType.ConstExp, subs);
    }
    boolean random = false;
    private Node match(TokenType tokenType) {
//        random = !random;
//        if (random) {
//            random = true;
//            System.out.print("jc1");
//            if (random) {
//                random = true;
//                System.out.print("jc11");
//                if (random) {
//                    random = true;
//                    System.out.print("jc111");
//                }
//            }
//        }
        if (tokens.get(flag).getTokenType() == tokenType) {
//            if (!random) {
//                random = false;
//                System.out.print("jc2");
//                if (!random) {
//                    random = false;
//                    System.out.print("jc22");
//                    if (!random) {
//                        random = false;
//                        System.out.print("jc222");
//                    }
//                }
//            }
            Token tmp = tokens.get(flag);
            if (flag < tokens.size() - 1) {
                ++flag;
            }
            Node endNode;
            if (tmp.getTokenType() == TokenType.STRCON)
                endNode = new Node(NodeType.FormatString, null);
            else
                endNode = new Node(NodeType.End, null);
            endNode.setToken(tmp);
            return endNode;
        } else if (tokenType == TokenType.SEMICN) {
            if (random) {
                random = true;
                System.out.print("jc3");
            }
            ErrorHandler.addError(new Error(tokens.get(flag - 1).getLine(), ErrorType.i));
            Node endNode = new Node(NodeType.End, null);
            endNode.setToken(new Token(TokenType.SEMICN, tokens.get(flag - 1).getLine(), ";"));
            return endNode;
        } else if (tokenType == TokenType.RPARENT) {
            if (!random) {
                random = false;
                System.out.print("jc4");
            }
            ErrorHandler.addError(new Error(tokens.get(flag - 1).getLine(), ErrorType.j));
            Node endNode = new Node(NodeType.End, null);
            endNode.setToken(new Token(TokenType.RPARENT, tokens.get(flag - 1).getLine(), ")"));
            return endNode;
        } else if (tokenType == TokenType.RBRACK) {
            if (random) {
                random = true;
                System.out.print("jc5");
            }
            ErrorHandler.addError(new Error(tokens.get(flag - 1).getLine(), ErrorType.k));
            Node endNode = new Node(NodeType.End, null);
            endNode.setToken(new Token(TokenType.RBRACK, tokens.get(flag - 1).getLine(), "]"));
            return endNode;
        } else {
            throw new RuntimeException("line " + tokens.get(flag).getLine() + ": " + tokens.get(flag).getTokenContent() + tokenType);
        }
    }

    private boolean isExp() {
        return tokens.get(flag).getTokenType() == TokenType.IDENFR ||
                tokens.get(flag).getTokenType() == TokenType.PLUS ||
                tokens.get(flag).getTokenType() == TokenType.MINU ||
                tokens.get(flag).getTokenType() == TokenType.NOT ||
                tokens.get(flag).getTokenType() == TokenType.LPARENT ||
                tokens.get(flag).getTokenType() == TokenType.INTCON;
    }

    public void printNodes() throws IOException {
        compUnit.print();
    }
}
