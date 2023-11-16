package Nodes;

import Nodes.Method.Node;
import Nodes.Method.Print;
import front.Parser;

import java.util.ArrayList;
import java.util.List;

public class CompUnitNode extends Node {
    List<DeclNode> declNodeList = new ArrayList<>();
    List<FuncDefNode> funcDefNodeList = new ArrayList<>();
    MainFuncDefNode mainFuncDefNode = null;

    public CompUnitNode(List<DeclNode> declNodeList, List<FuncDefNode> funcDefNodeList, MainFuncDefNode mainFuncDefNode) {
        this.declNodeList = declNodeList;
        this.funcDefNodeList = funcDefNodeList;
        this.mainFuncDefNode = mainFuncDefNode;
    }

    @Override
    public void print() throws IllegalAccessException {
        for (DeclNode decl : declNodeList) {
            decl.print();
        }
        for (FuncDefNode func : funcDefNodeList) {
            func.print();
        }
        if(mainFuncDefNode!=null) mainFuncDefNode.print();
        Print.printParser(Parser.nodeClassMap.get(this.getClass()));
    }

    public List<DeclNode> getDeclNodeList() {
        return declNodeList;
    }

    public void setDeclNodeList(List<DeclNode> declNodeList) {
        this.declNodeList = declNodeList;
    }

    public List<FuncDefNode> getFuncDefNodeList() {
        return funcDefNodeList;
    }

    public void setFuncDefNodeList(List<FuncDefNode> funcDefNodeList) {
        this.funcDefNodeList = funcDefNodeList;
    }

    public MainFuncDefNode getMainFuncDefNode() {
        return mainFuncDefNode;
    }

    public void setMainFuncDefNode(MainFuncDefNode mainFuncDefNode) {
        this.mainFuncDefNode = mainFuncDefNode;
    }
}
