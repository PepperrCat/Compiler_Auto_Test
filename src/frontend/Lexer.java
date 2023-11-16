package frontend;

import Config.Config;
import frontend.error.Error;
import frontend.error.ErrorHandler;
import frontend.error.ErrorType;
import token.Token;
import token.TokenType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private static Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("main", TokenType.MAINTK);
        keywords.put("const", TokenType.CONSTTK);
        keywords.put("int", TokenType.INTTK);
        keywords.put("break", TokenType.BREAKTK);
        keywords.put("continue", TokenType.CONTINUETK);
        keywords.put("if", TokenType.IFTK);
        keywords.put("else", TokenType.ELSETK);
        keywords.put("for", TokenType.FORTK);
        keywords.put("getint", TokenType.GETINTTK);
        keywords.put("printf", TokenType.PRINTFTK);
        keywords.put("return", TokenType.RETURNTK);
        keywords.put("void", TokenType.VOIDTK);
    }

    public Lexer(String fileData) {
        this.fileData = fileData;
    }

    private String fileData;
    private List<Token> tokens = new ArrayList<>();

    public List<Token> getTokens() {
        return tokens;
    }

    public void analyze() throws IOException {
        int line = 1;
        int len = fileData.length();

        for (int i = 0; i < len; i++) {
            char c = fileData.charAt(i);
            while (c == ' ' || c == '\t') {
                i++;
                c = fileData.charAt(i);
            }
            char nextc = i + 1 < len ? fileData.charAt(i + 1) : '\0';
            if (c == '\n') line++;
            else if (Character.isLetter(c) || c == '_') {   // 字母下划线对应标识符
                String key = "";
                for (int j = i; j < len; j++) { // 读完标识符
                    char nc = fileData.charAt(j);
                    if (Character.isLetter(nc) || nc == '_' || Character.isDigit(nc)) key += nc;
                    else {
                        i = j - 1;  // 指针前进
                        break;
                    }
                }
                tokens.add(new Token(keywords.getOrDefault(key, TokenType.IDENFR), line, key));
            } else if (c >= '0' && c <= '9') {  // DIGIT
                String num = "";
                for (int j = i; j < len; j++) {
                    char nc = fileData.charAt(j);
                    if (Character.isDigit(nc)) num += nc;
                    else {
                        i = j - 1;
                        break;
                    }
                }
                tokens.add(new Token(TokenType.INTCON, line, num));
            } else {
                switch (c) {
                    case '\"' -> {
                        String str = "" + c;
                        for (int j = i + 1; j < len; j++) {
                            char nc = fileData.charAt(j);
                            if (nc != '\"') {
                                str += nc;
                            } else {
                                i = j;
                                str += "\"";
                                break;
                            }
                        }
                        tokens.add(new Token(TokenType.STRCON, line, str));
                    }
                    case '&' -> {
                        if (nextc == '&') {
                            tokens.add(new Token(TokenType.AND, line, "&&"));
                            i++;
                        }
                    }
                    case '|' -> {
                        if (nextc == '|') {
                            tokens.add(new Token(TokenType.OR, line, "||"));
                            i++;
                        }
                    }
                    case '!' -> {
                        if (nextc != '=') {
                            tokens.add(new Token(TokenType.NOT, line, "!"));
                        }
                        else {
                            tokens.add(new Token(TokenType.NEQ, line, "!="));
                            i++;
                        }
                    }
                    case '+' -> {
                        tokens.add(new Token(TokenType.PLUS, line, "+"));
                    }
                    case '-' -> {
                        tokens.add(new Token(TokenType.MINU, line, "-"));
                    }
                    case '*' -> {
                        tokens.add(new Token(TokenType.MULT, line, "*"));
                    }
                    case '/' -> {
                        if (nextc == '/') {
                            int j = fileData.indexOf('\n', i + 2);
                            if (j == -1) j = len;   // OVER
                            i = j - 1;
                        } else if (nextc == '*') {
                            for (int j = i + 2; j < len; j++) {
                                char e = fileData.charAt(j);
                                if (e == '\n') line++;
                                else if (e == '*' && fileData.charAt(j + 1) == '/') {
                                    i = j + 1;
                                    break;
                                }
                            }
                        } else tokens.add(new Token(TokenType.DIV, line, "/"));
                    }
                    case '%' -> {
                        tokens.add(new Token(TokenType.MOD, line, "%"));
                    }
                    case '<' -> {
                        if (nextc != '=') {
                            tokens.add(new Token(TokenType.LSS, line, "<"));
                        } else {
                            tokens.add(new Token(TokenType.LEQ, line, "<="));
                            i++;
                        }
                    }
                    case '>' -> {
                        if (nextc != '=') {
                            tokens.add(new Token(TokenType.GRE, line, ">"));
                        } else {
                            tokens.add(new Token(TokenType.GEQ, line, ">="));
                            i++;
                        }
                    }
                    case '='->{
                        if (nextc != '=') tokens.add(new Token(TokenType.ASSIGN, line, "="));
                        else {
                            tokens.add(new Token(TokenType.EQL, line, "=="));
                            i++;
                        }
                    }
                    case '(' -> {
                        tokens.add(new Token(TokenType.LPARENT, line, "("));
                    }case ')' -> {
                        tokens.add(new Token(TokenType.RPARENT, line, ")"));
                    }case '[' -> {
                        tokens.add(new Token(TokenType.LBRACK, line, "["));
                    }case ']' -> {
                        tokens.add(new Token(TokenType.RBRACK, line, "]"));
                    }case '{' -> {
                        tokens.add(new Token(TokenType.LBRACE, line, "{"));
                    }case '}' -> {
                        tokens.add(new Token(TokenType.RBRACE, line, "}"));
                    }case ';' -> {
                        tokens.add(new Token(TokenType.SEMICN, line, ";"));
                    }case ',' -> {
                        tokens.add(new Token(TokenType.COMMA, line, ","));
                    }
                }
            }
        }
    }

    public void printTokens() throws IOException {
        for (Token token : tokens) {
            write(Config.outFileMap.get("lexer"),token.toString());
        }
    }
    public void write(String fileName, String fileData) throws IOException {
//        System.out.println(fileData);
        if (fileName == null)
            fileName = "output.txt";
        File outputFile = new File(fileName);
        if(!outputFile.exists()){
            outputFile.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile, true);
        fileOutputStream.write(fileData.getBytes());
        fileOutputStream.close();
    }

}
