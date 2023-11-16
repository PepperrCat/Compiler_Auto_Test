
import Nodes.CompUnitNode;
import back.GenCode;
import error.ErrorAnalyze;
import front.Laxer;
import front.Parser;
import middle.LLVMIR;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        Laxer.analyze();

        CompUnitNode compUnitNode = Parser.startAnalyze();

        ErrorAnalyze.errorEnalyze(compUnitNode);
        LLVMIR llvmir = new LLVMIR();
        llvmir.buildModule(compUnitNode);
        GenCode genCode=new GenCode(llvmir.module);
        genCode.genMips();

    }
}
