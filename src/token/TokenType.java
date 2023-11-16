package token;

import IR.BasicValue.Users.Instructions.CompareType;

public enum TokenType {
    IDENFR,
    INTCON,
    STRCON,
    MAINTK,
    CONSTTK,
    INTTK,
    BREAKTK,
    CONTINUETK,
    IFTK,
    ELSETK,
    NOT,
    AND,
    OR,
    FORTK,
    GETINTTK,
    PRINTFTK,
    RETURNTK,
    PLUS,
    MINU,
    VOIDTK,
    MULT,
    DIV,
    MOD,
    LSS,
    LEQ,
    GRE,
    GEQ,
    EQL,
    NEQ,
    ASSIGN,
    SEMICN,
    COMMA,
    LPARENT,
    RPARENT,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,
    ;

    public CompareType toCompareType() {
        switch (this) {
            case LSS -> {
                return CompareType.LSS;
            }
            case LEQ -> {
                return CompareType.LEQ;
            }
            case GRE -> {
                return CompareType.GRE;
            }
            case GEQ -> {
                return CompareType.GEQ;
            }
            case NEQ -> {
                return CompareType.NEQ;
            }
            case EQL -> {
                return CompareType.EQL;
            }
        }
        return null;
    }
}
