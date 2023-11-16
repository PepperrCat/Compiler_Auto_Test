package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class VarDeclNode  extends Node {
    //VarDecl → BType VarDef { ',' VarDef } ';'
    BTypeNode bTypeNode =null;
    VarDefNode varDefNode = null;
    List<Pair> commaList = new ArrayList<>();
    List<VarDefNode> varDefNodeList = new ArrayList<>();
    Pair semicn = null;

    public VarDeclNode(BTypeNode bTypeNode, VarDefNode varDefNode, List<Pair> commaList, List<VarDefNode> varDefNodeList, Pair semicn) {
        this.bTypeNode = bTypeNode;
        this.varDefNode = varDefNode;
        this.commaList = commaList;
        this.varDefNodeList = varDefNodeList;
        this.semicn = semicn;
    }
    public List<VarDefNode> getAllVarDefNode(){
        List<VarDefNode> list = new ArrayList<>();
        if(varDefNode!=null)list.add(varDefNode);
        list.addAll(varDefNodeList);
        return list;
    }

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public void setbTypeNode(BTypeNode bTypeNode) {
        this.bTypeNode = bTypeNode;
    }

    public VarDefNode getVarDefNode() {
        return varDefNode;
    }

    public void setVarDefNode(VarDefNode varDefNode) {
        this.varDefNode = varDefNode;
    }

    public List<Pair> getCommaList() {
        return commaList;
    }

    public void setCommaList(List<Pair> commaList) {
        this.commaList = commaList;
    }

    public List<VarDefNode> getVarDefNodeList() {
        return varDefNodeList;
    }

    public void setVarDefNodeList(List<VarDefNode> varDefNodeList) {
        this.varDefNodeList = varDefNodeList;
    }

    public Pair getSemicn() {
        return semicn;
    }

    public void setSemicn(Pair semicn) {
        this.semicn = semicn;
    }
}
