package Nodes;

import Nodes.Method.Node;

public class CondNode  extends Node {
    LOrExpNode lOrExpNode;

    public CondNode(LOrExpNode lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }

    public LOrExpNode getlOrExpNode() {
        return lOrExpNode;
    }

    public void setlOrExpNode(LOrExpNode lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }
}
