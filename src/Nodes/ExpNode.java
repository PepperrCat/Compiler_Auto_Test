package Nodes;

import Nodes.Method.Node;

public class ExpNode  extends Node {
    AddExpNode addExpNode ;

    public ExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public void setAddExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
}
