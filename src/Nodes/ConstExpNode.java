package Nodes;

import Nodes.Method.Node;

public class ConstExpNode  extends Node {
    AddExpNode addExpNode = null;

    public ConstExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public void setAddExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
}
