import Config.Config;
import IR.LLVMBuilder;
import IR.Module;
import IR.Optimization.IROptimizer;
import backend.LivenessAnalyze.LivenessAnalyzer;
import backend.MIPSBuilder;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSOperand.MIPSRegister;
import backend.Optimization.MulOptimizer;
import backend.Optimization.Optimizer;
import frontend.error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import io.IO;


import java.io.*;
import java.util.Map;

public class Compiler {
    static {
        for (Map.Entry<String,String> entry:Config.outFileMap.entrySet()) {
            File f = new File(entry.getValue());
            if (f.exists())
                f.delete();
//            try {
//                f.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
        if (Config.stdOut) {
            for (Map.Entry<String,String> entry:Config.outFileMap.entrySet()) {
                entry.setValue(null);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        if (Config.frontendSwitch) {
            Lexer lexer = new Lexer(IO.read("testfile.txt"));
            System.out.println("lexer analyzer start!");
            lexer.analyze();
            System.out.println("lexer analyzer end!");
            if (Config.lexerOut) {
                lexer.printTokens();
                System.out.println("lexer writen to lexer.txt!");
            }
            System.out.println("parser analyzer start!");
            Parser parser = new Parser(lexer.getTokens());
            parser.analyze();
            System.out.println("parser analyzer end!");
            if (Config.parserOut) {
                parser.printNodes();
                System.out.println("parser writen to parser.txt!");
            }
            if (Config.errorCheck) {
                ErrorHandler errorHandler = new ErrorHandler(parser.getCompUnitNode());
                System.out.println("error handler start!");
                errorHandler.analyze();
                System.out.println("error handler end!");
                if (errorHandler.isEndCompile()) {
                    System.out.println("errors as follows:");
                    errorHandler.printErrors();
                    System.out.println("compile error end!");
                    return;
                }
            }
            if (Config.middleendSwitch) {
                LLVMBuilder llvmBuilder = new LLVMBuilder(parser.getCompUnitNode());
                System.out.println("llvm builder start!");
                llvmBuilder.buildLLVM();
                System.out.println("llvm builder end!");
                if (Config.middleendOptimization) {
                    IROptimizer.getInstance().optimize();
                }
                if (Config.llvmOut) {
                    llvmBuilder.printLLVM();
                    System.out.println("llvm writen to llvm_ir.txt!");
                }
                if (Config.backendSwitch) {
                    MIPSBuilder mipsBuilder = new MIPSBuilder(Module.module);
                    mipsBuilder.buildMIPS();
                    if (Config.backendOptimization) {
                        Optimizer optimizer = new Optimizer(mipsBuilder.getMipsModule());
                        optimizer.optimize();
                    }
                    mipsBuilder.printMIPS("mips");
                }
            }
            System.out.println("compile success end!");
        } else {
            System.out.println("Config Error: frontend not start");
        }

    }

}
