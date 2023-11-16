package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class FuncFParamsNode  extends Node {
    //FuncFParams → FuncFParam { ',' FuncFParam }
    FuncFParamNode funcFParamNode=null;
    List<Pair> commaList = new ArrayList<>();
    List<FuncFParamNode> funcFParamNodeList = new ArrayList<>();

    public FuncFParamsNode(FuncFParamNode funcFParamNode, List<Pair> commaList, List<FuncFParamNode> funcFParamNodeList) {
        this.funcFParamNode = funcFParamNode;
        this.commaList = commaList;
        this.funcFParamNodeList = funcFParamNodeList;
    }
    public List<FuncFParamNode> getAllFuncFParamNode(){
        List<FuncFParamNode> list = new ArrayList<>();
        if(funcFParamNode!=null)list.add(funcFParamNode);
        list.addAll(funcFParamNodeList);
        return list;
    }
    public FuncFParamNode getFuncFParamNode() {
        return funcFParamNode;
    }

    public void setFuncFParamNode(FuncFParamNode funcFParamNode) {
        this.funcFParamNode = funcFParamNode;
    }

    public List<Pair> getCommaList() {
        return commaList;
    }

    public void setCommaList(List<Pair> commaList) {
        this.commaList = commaList;
    }

    public List<FuncFParamNode> getFuncFParamNodeList() {
        return funcFParamNodeList;
    }

    public void setFuncFParamNodeList(List<FuncFParamNode> funcFParamNodeList) {
        this.funcFParamNodeList = funcFParamNodeList;
    }
}
