package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class UnaryOpNode  extends Node {
    Pair op = null;

    public UnaryOpNode(Pair op) {
        this.op = op;
    }

    public Pair getOp() {
        return op;
    }

    public void setOp(Pair op) {
        this.op = op;
    }
}
