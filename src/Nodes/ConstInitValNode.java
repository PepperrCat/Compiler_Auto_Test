package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConstInitValNode  extends Node {
    //  //ConstInitVal → ConstExp
    //    //    | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    ConstExpNode constExpNode = null;
    Pair lbrace = null;
    ConstInitValNode constInitValNode = null;
    List<Pair> commaList = new ArrayList<>();
    List<ConstInitValNode> constInitValNodeList = new ArrayList<>();
    Pair rbrace = null;

    public ConstInitValNode(ConstExpNode constExpNode, Pair lbrace, ConstInitValNode constInitValNode, List<Pair> commaList, List<ConstInitValNode> constInitValNodeList, Pair rbrace) {
        this.constExpNode = constExpNode;
        this.lbrace = lbrace;
        this.constInitValNode = constInitValNode;
        this.commaList = commaList;
        this.constInitValNodeList = constInitValNodeList;
        this.rbrace = rbrace;
    }
    public List<ConstInitValNode> getAllConstInitValNode(){
        List<ConstInitValNode> list = new ArrayList<>();
        if(constInitValNode!=null)list.add(constInitValNode);
        list.addAll(constInitValNodeList);
        return list;
    }

    public ConstExpNode getConstExpNode() {
        return constExpNode;
    }

    public void setConstExpNode(ConstExpNode constExpNode) {
        this.constExpNode = constExpNode;
    }

    public Pair getLbrace() {
        return lbrace;
    }

    public void setLbrace(Pair lbrace) {
        this.lbrace = lbrace;
    }

    public ConstInitValNode getConstInitValNode() {
        return constInitValNode;
    }

    public void setConstInitValNode(ConstInitValNode constInitValNode) {
        this.constInitValNode = constInitValNode;
    }

    public List<Pair> getCommaList() {
        return commaList;
    }

    public void setCommaList(List<Pair> commaList) {
        this.commaList = commaList;
    }

    public List<ConstInitValNode> getConstInitValNodeList() {
        return constInitValNodeList;
    }

    public void setConstInitValNodeList(List<ConstInitValNode> constInitValNodeList) {
        this.constInitValNodeList = constInitValNodeList;
    }

    public Pair getRbrace() {
        return rbrace;
    }

    public void setRbrace(Pair rbrace) {
        this.rbrace = rbrace;
    }
}
