package Nodes;

import Nodes.Method.Node;
import models.Pair;
import models.TokenType;

public class ForStmtNode  extends Node {
    LValNode lValNode;
    Pair assignCon ;
    ExpNode expNode ;

    public ForStmtNode(LValNode lValNode, Pair assignCon, ExpNode expNode) {
        this.lValNode = lValNode;
        this.assignCon = assignCon;
        this.expNode = expNode;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public void setlValNode(LValNode lValNode) {
        this.lValNode = lValNode;
    }

    public Pair getAssignCon() {
        return assignCon;
    }

    public void setAssignCon(Pair assignCon) {
        this.assignCon = assignCon;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public void setExpNode(ExpNode expNode) {
        this.expNode = expNode;
    }
}
