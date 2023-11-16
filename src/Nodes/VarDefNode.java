package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class VarDefNode extends Node {
    Pair ident = null;
    List<Pair> lbrackList = new ArrayList<>();
    List<ConstExpNode> constExpNodeList=new ArrayList<>();
    List<Pair> rbrackList = new ArrayList<>();
    Pair assignCon = null;
    InitValNode initValNode = null;

    public VarDefNode(Pair ident, List<Pair> lbrackList, List<ConstExpNode> constExpNodeList, List<Pair> rbrackList, Pair assignCon, InitValNode initValNode) {
        this.ident = ident;
        this.lbrackList = lbrackList;
        this.constExpNodeList = constExpNodeList;
        this.rbrackList = rbrackList;
        this.assignCon = assignCon;
        this.initValNode = initValNode;
    }

    public Pair getIdent() {
        return ident;
    }

    public void setIdent(Pair ident) {
        this.ident = ident;
    }

    public List<Pair> getLbrackList() {
        return lbrackList;
    }

    public void setLbrackList(List<Pair> lbrackList) {
        this.lbrackList = lbrackList;
    }

    public List<ConstExpNode> getConstExpNodeList() {
        return constExpNodeList;
    }

    public void setConstExpNodeList(List<ConstExpNode> constExpNodeList) {
        this.constExpNodeList = constExpNodeList;
    }

    public List<Pair> getRbrackList() {
        return rbrackList;
    }

    public void setRbrackList(List<Pair> rbrackList) {
        this.rbrackList = rbrackList;
    }

    public Pair getAssignCon() {
        return assignCon;
    }

    public void setAssignCon(Pair assignCon) {
        this.assignCon = assignCon;
    }

    public InitValNode getInitValNode() {
        return initValNode;
    }

    public void setInitValNode(InitValNode initValNode) {
        this.initValNode = initValNode;
    }
}
