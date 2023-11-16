package Nodes;

import Nodes.Method.Node;

public class BlockItemNode  extends Node {
    DeclNode declNode;
    StmtNode stmtNode;

    public BlockItemNode(DeclNode declNode, StmtNode stmtNode) {
        this.declNode = declNode;
        this.stmtNode = stmtNode;
    }

    public DeclNode getDeclNode() {
        return declNode;
    }

    public void setDeclNode(DeclNode declNode) {
        this.declNode = declNode;
    }

    public StmtNode getStmtNode() {
        return stmtNode;
    }

    public void setStmtNode(StmtNode stmtNode) {
        this.stmtNode = stmtNode;
    }
}
