package symbol;

import func.FunctionParam;
import func.FunctionType;
import node.Node;

import java.util.List;

public class Symbol {
    private String name;
    private int line;
    private SymbolType symbolType;
    private Node initVal;
    private Node constInitVal;
    private boolean isConst = false;
    private boolean isFParam;
    private int d; // 数组维度:0，1，2
    private List<FunctionParam> functionFParams;
    private FunctionType functionType;

    public Symbol(String name, int line, SymbolType symbolType, Node initVal, boolean isConst, int d) {
        this.name = name;
        this.line = line;
        this.symbolType = symbolType;
        this.initVal = initVal;
        this.isConst = isConst;
        this.d = d;
    }

    public Symbol(String name, int line, SymbolType symbolType, boolean isConst, Node constInitVal, int d) {
        this.name = name;
        this.line = line;
        this.symbolType = symbolType;
        this.constInitVal = constInitVal;
        this.isConst = isConst;
        this.d = d;
    }

    public Symbol(String name, int line, SymbolType symbolType, List<FunctionParam> functionFParams, FunctionType functionType) {
        this.name = name;
        this.line = line;
        this.symbolType = symbolType;
        this.functionFParams = functionFParams;
        this.functionType = functionType;
    }

    public Symbol(String name, int line, SymbolType symbolType, boolean isFParam, int dimension) {
        this.name = name;
        this.line = line;
        this.symbolType = symbolType;
        this.isFParam = isFParam;
        this.d = dimension;
    }

    public boolean isFParam() {
        return isFParam;
    }

    public void setFParam(boolean FParam) {
        isFParam = FParam;
    }
    public void setSymbolType(SymbolType symbolType) {
        this.symbolType = symbolType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }

    public List<FunctionParam> getFunctionFParams() {
        return functionFParams;
    }

    public void setFunctionFParams(List<FunctionParam> functionFParams) {
        this.functionFParams = functionFParams;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }
}
