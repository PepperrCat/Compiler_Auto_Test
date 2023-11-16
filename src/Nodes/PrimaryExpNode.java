package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class PrimaryExpNode extends Node {
    Pair lparent = null;
    ExpNode expNode = null;
    Pair rparent = null;
    LValNode lValNode = null;
    NumberNode numberNode = null;

    public PrimaryExpNode(Pair lparent, ExpNode expNode, Pair rparent, LValNode lValNode, NumberNode numberNode) {
        this.lparent = lparent;
        this.expNode = expNode;
        this.rparent = rparent;
        this.lValNode = lValNode;
        this.numberNode = numberNode;
    }

    public Pair getLparent() {
        return lparent;
    }

    public void setLparent(Pair lparent) {
        this.lparent = lparent;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public void setExpNode(ExpNode expNode) {
        this.expNode = expNode;
    }

    public Pair getRparent() {
        return rparent;
    }

    public void setRparent(Pair rparent) {
        this.rparent = rparent;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public void setlValNode(LValNode lValNode) {
        this.lValNode = lValNode;
    }

    public NumberNode getNumberNode() {
        return numberNode;
    }

    public void setNumberNode(NumberNode numberNode) {
        this.numberNode = numberNode;
    }
}
