package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class NumberNode extends Node {
    Pair intConst = null;

    public NumberNode(Pair intConst) {
        this.intConst = intConst;
    }

    public Pair getIntConst() {
        return intConst;
    }

    public void setIntConst(Pair intConst) {
        this.intConst = intConst;
    }
}
