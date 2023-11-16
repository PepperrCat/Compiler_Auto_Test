package models;

import Nodes.Method.Print;

public class Pair {
    TokenType tokenType;
    String string;
    int line;

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public Pair(TokenType tokenType, String string, int line) {
        this.tokenType = tokenType;
        this.string = string;
        this.line = line;
    }

    public Pair(TokenType tokenType, String string) {
        this.tokenType = tokenType;
        this.string = string;
    }

    public Pair(TokenType tokenType, int num) {
        this.tokenType = tokenType;
        this.string = String.valueOf(num);
    }

    public TokenType getKey() {
        return tokenType;
    }

    public String getValue() {
        return string;
    }

    public void print() {
        Print.printParser(this.getKey().toString() + " " + this.getValue());
    }

    @Override
    public String toString() {
        return tokenType.toString() + " " + string;

    }
}
