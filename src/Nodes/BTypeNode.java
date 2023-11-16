package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class BTypeNode extends Node {
    Pair intCon = null;

    public BTypeNode(Pair intCon) {
        this.intCon = intCon;
    }

    public Pair getIntCon() {
        return intCon;
    }

    public void setIntCon(Pair intCon) {
        this.intCon = intCon;
    }
}
