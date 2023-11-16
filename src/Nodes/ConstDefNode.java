package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConstDefNode  extends Node {
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    Pair ident = null;
    List<Pair> lBrackList = new ArrayList<>();
    List<ConstExpNode> constExpNodeList = new ArrayList<>();
    List<Pair> rBrackList = new ArrayList<>();
    Pair assignCon=null;
    ConstInitValNode constInitValNode = null;

    public ConstDefNode(Pair ident, List<Pair> lBrackList, List<ConstExpNode> constExpNodeList, List<Pair> rBrackList, Pair assignCon, ConstInitValNode constInitValNode) {
        this.ident = ident;
        this.lBrackList = lBrackList;
        this.constExpNodeList = constExpNodeList;
        this.rBrackList = rBrackList;
        this.assignCon = assignCon;
        this.constInitValNode = constInitValNode;
    }


    public Pair getIdent() {
        return ident;
    }

    public void setIdent(Pair ident) {
        this.ident = ident;
    }

    public List<Pair> getlBrackList() {
        return lBrackList;
    }

    public void setlBrackList(List<Pair> lBrackList) {
        this.lBrackList = lBrackList;
    }

    public List<ConstExpNode> getConstExpNodeList() {
        return constExpNodeList;
    }

    public void setConstExpNodeList(List<ConstExpNode> constExpNodeList) {
        this.constExpNodeList = constExpNodeList;
    }

    public List<Pair> getrBrackList() {
        return rBrackList;
    }

    public void setrBrackList(List<Pair> rBrackList) {
        this.rBrackList = rBrackList;
    }

    public Pair getAssignCon() {
        return assignCon;
    }

    public void setAssignCon(Pair assignCon) {
        this.assignCon = assignCon;
    }

    public ConstInitValNode getConstInitValNode() {
        return constInitValNode;
    }

    public void setConstInitValNode(ConstInitValNode constInitValNode) {
        this.constInitValNode = constInitValNode;
    }
}
