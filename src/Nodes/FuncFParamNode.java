package Nodes;

import Nodes.Method.Node;
import middle.base.ValueType;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class FuncFParamNode extends Node {
    BTypeNode bTypeNode = null;
    Pair ident = null;
    Pair lbrack = null;
    Pair rbrack = null;
    List<Pair> lbrackList = new ArrayList<>();

    List<ConstExpNode> constExpNodeList = new ArrayList<>();
    List<Pair> rbrackList = new ArrayList<>();

    public int getDimension() {
        return lbrackList.size() + (lbrack == null ? 0 : 1);
    }

    public ValueType getValueType() {
        if (bTypeNode.intCon.equals("int"))
            return ValueType.i32;
        return ValueType.i32;
    }


    public FuncFParamNode(BTypeNode bTypeNode, Pair ident, Pair lbrack, Pair rbrack, List<Pair> lbrackList, List<ConstExpNode> constExpNodeList, List<Pair> rbrackList) {
        this.bTypeNode = bTypeNode;
        this.ident = ident;
        this.lbrack = lbrack;
        this.rbrack = rbrack;
        this.lbrackList = lbrackList;
        this.constExpNodeList = constExpNodeList;
        this.rbrackList = rbrackList;
    }

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public void setbTypeNode(BTypeNode bTypeNode) {
        this.bTypeNode = bTypeNode;
    }

    public Pair getIdent() {
        return ident;
    }

    public void setIdent(Pair ident) {
        this.ident = ident;
    }

    public Pair getLbrack() {
        return lbrack;
    }

    public void setLbrack(Pair lbrack) {
        this.lbrack = lbrack;
    }

    public Pair getRbrack() {
        return rbrack;
    }

    public void setRbrack(Pair rbrack) {
        this.rbrack = rbrack;
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
}
