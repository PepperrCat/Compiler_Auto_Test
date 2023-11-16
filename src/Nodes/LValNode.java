package Nodes;

import Nodes.Method.Node;
import models.Pair;
import models.TokenType;

import java.util.ArrayList;
import java.util.List;

public class LValNode extends Node {
    Pair ident;
    List<Pair> lbrakeList;
    List<ExpNode> expNodeList;
    List<Pair> rbrakeList;

    public LValNode(Pair ident, List<Pair> lbrakeList, List<ExpNode> expNodeList, List<Pair> rbrakeList) {
        this.ident = ident;
        this.lbrakeList = lbrakeList;
        this.expNodeList = expNodeList;
        this.rbrakeList = rbrakeList;
    }

    public Pair getIdent() {
        return ident;
    }

    public void setIdent(Pair ident) {
        this.ident = ident;
    }

    public List<Pair> getLbrakeList() {
        return lbrakeList;
    }

    public void setLbrakeList(List<Pair> lbrakeList) {
        this.lbrakeList = lbrakeList;
    }

    public List<ExpNode> getExpNodeList() {
        return expNodeList;
    }

    public void setExpNodeList(List<ExpNode> expNodeList) {
        this.expNodeList = expNodeList;
    }

    public List<Pair> getRbrakeList() {
        return rbrakeList;
    }

    public void setRbrakeList(List<Pair> rbrakeList) {
        this.rbrakeList = rbrakeList;
    }
}
