package front;

import Nodes.Method.Print;
import config.config;
import error.ErrorAnalyze;
import error.ErrorType;
import models.Pair;
import models.TokenType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Laxer {
    static ErrorAnalyze errorAnalyze = ErrorAnalyze.getInstance();
    static String[] keyWord = {"for", "getint", "printf", "return", "void",
            "main", "const", "int", "break", "continue",
            "if", "else", "hint", "char"};
    static String[] operation = {"+", "-", "*", "/", "%", "++", "--", "-=", "*=", "/=", "&", "|", "^", "~", "<<", ">>", ">>>", "==", "!=",
            ">", "<", "=", ">=", "<=", "&&", "||", "!", "."};
    static int p, lines;
    static Scanner scanner = new Scanner(System.in);
    static TokenType tokenType = null;
    static char ch;
    static String token = new String("");
    static int num;
    static ArrayList<Pair> tokens = new ArrayList<>();

    static boolean inContent = false;
    static int contentId = 0;

    public static void analyze() throws IOException {

        File file = new File(config.inputPath);
        lines = 1;
        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                String str = input.nextLine();
                getsym(str);
                lines++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        FileWriter fw = new FileWriter("output.txt");
//        PrintWriter pw = new PrintWriter(fw);
        for (Pair pair : tokens) {
//            System.out.println(pair.getKey().toString() + " " + pair.getValue());
            Print.printLaxer(pair.getKey().toString() + " " + pair.getValue());
        }
//        pw.flush();
    }


    public static void add(TokenType a, String b) {
        tokens.add(new Pair(a, b, lines));
    }

    public static void letterCase(String str) {
        token = "";
        // true
        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            if (!Character.isLetterOrDigit(ch) && ch != '_') {
                break;
            } else {
                token += ch;
            }
        }
        if (Arrays.asList(keyWord).contains(token)) {
            if (token.equals("main")) add(TokenType.MAINTK, "main");
            else if (token.equals("const")) add(TokenType.CONSTTK, "const");
            else if (token.equals("int")) add(TokenType.INTTK, "int");
            else if (token.equals("continue")) add(TokenType.CONTINUETK, "continue");
            else if (token.equals("if")) add(TokenType.IFTK, "if");
            else if (token.equals("else")) add(TokenType.ELSETK, "else");
            else if (token.equals("for")) add(TokenType.FORTK, "for");
            else if (token.equals("getint")) add(TokenType.GETINTTK, "getint");
            else if (token.equals("printf")) add(TokenType.PRINTFTK, "printf");
            else if (token.equals("return")) add(TokenType.RETURNTK, "return");
            else if (token.equals("void")) add(TokenType.VOIDTK, "void");
            else if (token.equals("break")) add(TokenType.BREAKTK, "break");
            else if (token.equals("hint")) add(TokenType.HINTTK, "hint");
            else if (token.equals("char")) add(TokenType.CHARTK, "char");
        } else add(TokenType.IDENFR, token);
        p--;
    }


    public static void digitCase(String str) {
        token = "";
        if (str.charAt(p) == '0' && p + 1 < str.length() && str.charAt(p + 1) == 'x') {
            token += "0x";
            p += 2;
            for (; p < str.length(); p++) {
                ch = str.charAt(p);
                if (Character.isDigit(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F') {
                    token += ch;
                } else break;
            }
            add(TokenType.HINTCON, token);
            p--;
        } else {
            for (; p < str.length(); p++) {
                ch = str.charAt(p);
                if (Character.isDigit(ch)) {
                    token += ch;
                } else break;
            }
            add(TokenType.INTCON, token);
            p--;
        }
    }

    public static void symbolCase(String str) {
        token = "";
        ch = str.charAt(p);
        if (ch == '\"') {
            boolean start = false;
            boolean added = false;
            for (; p < str.length(); p++) {
                ch = str.charAt(p);
                if (ch == 32 || ch == 33 || ch == 34 || ch >= 40 && ch <= 126) {
                    if (ch == 92 && str.charAt(p + 1) != 'n') {
                        if (!added) {
                            errorAnalyze.addError(lines, ErrorType.a);
                            added = true;
                        }
                    }
                } else if (ch == 37) {
                    if (str.charAt(p + 1) != 'd') {
                        if (!added) {
                            errorAnalyze.addError(lines, ErrorType.a);
                            added = true;
                        }
                    }
                } else {
                    if (!added) {
                        errorAnalyze.addError(lines, ErrorType.a);
                        added = true;
                    }
                }
                token += ch;
                if (start && ch == '\"') break;
                start = true;
            }
            add(TokenType.STRCON, token);
        } else if (ch == '\'') {
            if (p + 2 < str.length() && str.charAt(p + 2) == '\'') {
                add(TokenType.CHARCON, "" + str.charAt(p + 1));
                p += 2;
            }

        } else if (ch == '!') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '=') {
                add(TokenType.NEQ, "!=");
                p++;
            } else add(TokenType.NOT, "!");
        } else if (ch == '&') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '&') {
                add(TokenType.AND, "&&");
                p++;
            }
        } else if (ch == '|') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '|') {
                add(TokenType.OR, "||");
                p++;
            }
        } else if (ch == '+') add(TokenType.PLUS, "+");
        else if (ch == '-') add(TokenType.MINU, "-");
        else if (ch == '*') add(TokenType.MULT, "*");
        else if (ch == '/') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '*') {
                contentId = 1;
                inContent = true;
            } else if (p + 1 < str.length() && str.charAt(p + 1) == '/') {
                contentId = 2;
                inContent = true;
            } else
                add(TokenType.DIV, "/");

        } else if (ch == '%') add(TokenType.MOD, "%");
        else if (ch == ';') add(TokenType.SEMICN, ";");
        else if (ch == ',') add(TokenType.COMMA, ",");
        else if (ch == '(') add(TokenType.LPARENT, "(");
        else if (ch == ')') add(TokenType.RPARENT, ")");
        else if (ch == '[') add(TokenType.LBRACK, "[");
        else if (ch == ']') add(TokenType.RBRACK, "]");
        else if (ch == '{') add(TokenType.LBRACE, "{");
        else if (ch == '}') add(TokenType.RBRACE, "}");
        else if (ch == '<') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '=') {
                add(TokenType.LEQ, "<=");
                p++;
            } else add(TokenType.LSS, "<");
        } else if (ch == '>') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '=') {
                add(TokenType.GEQ, ">=");
                p++;
            } else add(TokenType.GRE, ">");
        } else if (ch == '=') {
            if (p + 1 < str.length() && str.charAt(p + 1) == '=') {
                add(TokenType.EQL, "==");
                p++;
            } else add(TokenType.ASSIGN, "=");
        }
//        p++;
    }

    public static void getsym(String str) {
        p = 0;
        str = str.trim();
        token = "";

        for (; p < str.length(); p++) {
            ch = str.charAt(p);
            if (inContent) {
                if (contentId == 1) {
                    if (ch == '*' && p + 1 < str.length() && str.charAt(p + 1) == '/') {
                        inContent = false;
                        p++;
                        contentId = 0;
                    }
                } else if (contentId == 2) {
                    inContent = false;
                    contentId = 0;
                    return;

                }
            } else if (Character.isLetter(ch) || ch == '_') {
                letterCase(str);
            } else if (Character.isDigit(ch)) {

                digitCase(str);
            } else {
                symbolCase(str);
            }
        }
        return;
    }

    public static void error() {
        System.out.println("ERROR!");
    }
    //运算符
}