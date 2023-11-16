package symbol;

import func.FunctionParam;
import func.FunctionType;
import node.Node;
import node.NodeType;
import token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private List<SymbolTable> sonTables = new ArrayList<>();
    private SymbolTable fatherTable;
    private Map<String, Symbol> symbols = new HashMap<>();
    private List<Symbol> symbolStack = new ArrayList<>();

    public SymbolTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
    }

    public SymbolTable createANewSon() {
        sonTables.add(new SymbolTable(this));
        return sonTables.get(sonTables.size() - 1);
    }

    public Symbol addConstArrSymbol(Node ident, int dimension, Node constInitVal) {
        Token identTK = ident.getToken();
        Symbol s = new Symbol(identTK.getTokenContent(), identTK.getLine(), SymbolType.ArrSymbol, true, constInitVal, dimension);
        symbols.put(identTK.getTokenContent(), s);
        symbolStack.add(s);
        return s;
    }

    public Symbol addVarArrSymbol(Node ident, int dimension, Node initVal) {
        Token identTK = ident.getToken();
        Symbol s = new Symbol(identTK.getTokenContent(), identTK.getLine(), SymbolType.ArrSymbol, initVal, false, dimension);
        symbols.put(identTK.getTokenContent(), s);
        symbolStack.add(s);
        return s;
    }

    public Symbol addFuncSymbol(Node ident, FunctionType functionType, Node functionParams) {
        Token identTK = ident.getToken();
        List<FunctionParam> functionParamList = new ArrayList<>();
        if (functionParams != null) {
            List<Node> FParams = functionParams.getSubNodes();
            for (Node FParam : FParams) {
                if (FParam.getNodeType() == NodeType.FuncFParam) {
                    Node paramIdent = FParam.getSubNodes().get(1);
                    int dimension = (FParam.getSubNodes().size() - 1) / 3;
                    functionParamList.add(new FunctionParam(paramIdent.getToken().getTokenContent(), dimension));
                }
            }
        }
        Symbol s = new Symbol(identTK.getTokenContent(), identTK.getLine(), SymbolType.FuncSymbol, functionParamList, functionType);
        symbols.put(identTK.getTokenContent(), s);
        symbolStack.add(s);
        return s;
    }

    public Symbol addFParamSymbol(Node ident, int dimension) {
        Token identTK = ident.getToken();
        Symbol s = new Symbol(identTK.getTokenContent(), identTK.getLine(), SymbolType.FParam, true, dimension);
        symbols.put(identTK.getTokenContent(), s);
        symbolStack.add(s);
        return s;
    }

    public List<SymbolTable> getSonTables() {
        return sonTables;
    }

    public void setSonTables(List<SymbolTable> sonTables) {
        this.sonTables = sonTables;
    }

    public SymbolTable getFatherTable() {
        return fatherTable;
    }

    public void setFatherTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }


    public boolean isSymbolInTable(Node ident) {
        if (ident.getToken() == null)
            return false;
        return symbols.containsKey(ident.getToken().getTokenContent());
    }

    public Symbol isSymbolDefine(Node ident) {
        if (ident.getToken() == null)
            return null;
        SymbolTable curTable = this;
        while (curTable != null) {
            for (Symbol symbol : curTable.symbolStack) {
                if (symbol.getName().equals(ident.getToken().getTokenContent())) {
                    return symbol;
                }
            }
            curTable = curTable.fatherTable;
        }
        return null;
    }

    public Symbol getSymbol(String name) {
        SymbolTable table = this;
        while (table != null) {
            for (Map.Entry<String, Symbol> entry : table.symbols.entrySet()) {
                if (entry.getKey().equals(name)) {
                    return entry.getValue();
                }
            }
            table = table.fatherTable;
        }
        return null;
    }

    public boolean isFuncNeedReturn() {
        SymbolTable table = this.fatherTable;
        while (table != null) {
            int listSize = table.symbolStack.size() - 1;
            if (listSize >= 0) {
                Symbol lastItem = table.symbolStack.get(listSize);
                if (lastItem.getFunctionType() == FunctionType.VOIDTP)
                    return false;
                else
                    return true;
            }
            table = table.fatherTable;
        }
        return false;
    }
}
