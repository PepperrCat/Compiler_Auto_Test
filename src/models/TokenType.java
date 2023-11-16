package models;

import java.util.ArrayList;
import java.util.List;

public enum TokenType {
    IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK,
    CONTINUETK, IFTK, ELSETK, NOT, AND, OR, FORTK, GETINTTK,
    PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV, MOD,
    LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT,
    RPARENT, LBRACK, RBRACK, LBRACE, RBRACE,
    HINTCON, HINTTK,
    CHARTK, CHARCON;

    public static List<TokenType> varTypeTk() {
        List<TokenType> tokenTypes = new ArrayList<>();
        tokenTypes.add(INTTK);
        tokenTypes.add(HINTTK);
        tokenTypes.add(CHARTK);
        return tokenTypes;
    }


}
