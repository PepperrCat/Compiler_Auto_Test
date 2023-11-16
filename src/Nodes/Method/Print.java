package Nodes.Method;

import config.config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Print {
    static FileWriter parserFw = null;
    static PrintWriter parserPw = null;

    static FileWriter errorFw = null;
    static PrintWriter errorPw = null;

    static FileWriter laxerFw = null;
    static PrintWriter laxerPw = null;

    static FileWriter midFw = null;
    static PrintWriter midPw = null;

    static FileWriter mipsFw = null;
    static PrintWriter mipsPw = null;

    public static void printIR(String string) {
        if (!config.ir) return;
        try {
            if (midFw == null)
                midFw = new FileWriter(config.irOutputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (midPw == null)
            midPw = new PrintWriter(midFw);
        print(string, midPw);
    }

    public static void printLaxer(String string) {
        if (!config.laxer) return;
        try {
            if (laxerFw == null)
                laxerFw = new FileWriter(config.laxerOutputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (laxerPw == null)
            laxerPw = new PrintWriter(laxerFw);
        print(string, laxerPw);
    }


    public static void printParser(String string) {
        if (!config.parser) return;
        try {
            if (parserFw == null)
                parserFw = new FileWriter(config.parserOutputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (parserPw == null)
            parserPw = new PrintWriter(parserFw);
        print(string, parserPw);
    }

    public static void printMips(String string) {
        if (!config.mips) return;
        try {
            if (mipsFw == null)
                mipsFw = new FileWriter(config.mipsOutputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (mipsPw == null)
            mipsPw = new PrintWriter(mipsFw);
        print(string, mipsPw);
    }

    public static void printError(String string) {
        if (!config.error) return;
        try {
            if (errorFw == null)
                errorFw = new FileWriter(config.errorOutputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (errorPw == null)
            errorPw = new PrintWriter(errorFw);
        print(string, errorPw);
    }

    private static void print(String string, PrintWriter pw) {

        if (config.debug) {
            System.out.println(string);
            return;
        }
        pw.println(string);
        pw.flush();
    }


}
