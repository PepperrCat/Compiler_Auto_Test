package token;

import func.FunctionType;

public class Token {
    private TokenType tokenType;
    private int line;
    private String tokenContent;

    public Token(TokenType tokenType, int line, String tokenContent) {
        this.tokenType = tokenType;
        this.line = line;
        this.tokenContent = tokenContent;
    }

    @Override
    public String toString() {
        return tokenType.toString() + " " + tokenContent + '\n';
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getTokenContent() {
        return tokenContent;
    }

    public void setTokenContent(String tokenContent) {
        this.tokenContent = tokenContent;
    }
    public FunctionType toFuncType() {
        if (this.tokenType == TokenType.INTTK) {
            return FunctionType.INTTP;
        } else if (this.tokenType == TokenType.VOIDTK){
            return FunctionType.VOIDTP;
        }
        return null;
    }
}
