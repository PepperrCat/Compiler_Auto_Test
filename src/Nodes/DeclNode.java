package Nodes;

import Nodes.Method.Node;

public class DeclNode  extends Node {
    ConstDeclNode constDeclNode = null;
    VarDeclNode varDeclNode = null;

    public DeclNode(ConstDeclNode constDeclNode, VarDeclNode varDeclNode) {
        this.constDeclNode = constDeclNode;
        this.varDeclNode = varDeclNode;
    }

    public ConstDeclNode getConstDeclNode() {
        return constDeclNode;
    }

    public void setConstDeclNode(ConstDeclNode constDeclNode) {
        this.constDeclNode = constDeclNode;
    }

    public VarDeclNode getVarDeclNode() {
        return varDeclNode;
    }

    public void setVarDeclNode(VarDeclNode varDeclNode) {
        this.varDeclNode = varDeclNode;
    }
}
