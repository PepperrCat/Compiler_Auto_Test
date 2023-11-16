package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class InitValNode extends Node {
    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    ExpNode expNode = null;
    Pair lbrace = null;
    InitValNode initValNode = null;
    List<Pair> commaList = new ArrayList<>();
    List<InitValNode> initValNodeList = new ArrayList<>();
    Pair rbrace = null;

    public InitValNode(ExpNode expNode, Pair lbrace, InitValNode initValNode, List<Pair> commaList, List<InitValNode> initValNodeList, Pair rbrace) {
        this.expNode = expNode;
        this.lbrace = lbrace;
        this.initValNode = initValNode;
        this.commaList = commaList;
        this.initValNodeList = initValNodeList;
        this.rbrace = rbrace;
    }
    public List<InitValNode> getAllInitValNode(){
        List<InitValNode> list = new ArrayList<>();
        if(initValNode!=null)list.add(initValNode);
        list.addAll(initValNodeList);
        return list;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public void setExpNode(ExpNode expNode) {
        this.expNode = expNode;
    }

    public Pair getLbrace() {
        return lbrace;
    }

    public void setLbrace(Pair lbrace) {
        this.lbrace = lbrace;
    }

    public InitValNode getInitValNode() {
        return initValNode;
    }

    public void setInitValNode(InitValNode initValNode) {
        this.initValNode = initValNode;
    }

    public List<Pair> getCommaList() {
        return commaList;
    }

    public void setCommaList(List<Pair> commaList) {
        this.commaList = commaList;
    }

    public List<InitValNode> getInitValNodeList() {
        return initValNodeList;
    }

    public void setInitValNodeList(List<InitValNode> initValNodeList) {
        this.initValNodeList = initValNodeList;
    }

    public Pair getRbrace() {
        return rbrace;
    }

    public void setRbrace(Pair rbrace) {
        this.rbrace = rbrace;
    }
}
