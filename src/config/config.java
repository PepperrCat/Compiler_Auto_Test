package config;

public class config {
    public static String inputPath = "testfile.txt";
    public static String laxerOutputPath = "laxerOutput.txt";
    public static String parserOutputPath = "output.txt";
    public static String irOutputPath = "llvm_ir.txt";
    public static String errorOutputPath = "error.txt";
    public static String mipsOutputPath = "mips.txt";
    public static boolean debug = false;

    public static boolean laxer = false;
    public static boolean mips = true;
    public static boolean ir = true;

    public static boolean parser = false;
    public static boolean error = false;

}
