package Config;

import java.util.HashMap;
import java.util.Map;

public interface Config {
    boolean stdOut = false;
    boolean frontendSwitch = true;
    boolean middleendSwitch = true;
    boolean backendSwitch = true;
    boolean lexerOut = false;
    boolean parserOut = false;
    boolean errorCheck = true;
    boolean llvmOut = true;
    Map<String, String> outFileMap = new HashMap<>(){{
       put("lexer", "lexer.txt");
        put("parser", "parser.txt");
        put("error", "error.txt");
        put("llvm", "llvm_ir.txt");
        put("mips", "mips.txt");
        put("vmips", "mips_vir.txt");
    }};
    ///...some optimization switch
    boolean middleendOptimization = true;
    boolean backendOptimization = true;
    boolean mulOptimization = true;
    boolean divOptimization = true;

    boolean modOptimization = true;
}
