package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class UnaryExpNode extends Node {
    PrimaryExpNode primaryExpNode = null;
    Pair ident = null;
    Pair lparent = null;
    FuncRParamsNode funcRParamsNode = null;
    Pair rparent = null;
    UnaryOpNode unaryOpNode = null;
    UnaryExpNode unaryExpNode = null;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Pair ident, Pair lparent, FuncRParamsNode funcRParamsNode, Pair rparent, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        this.primaryExpNode = primaryExpNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcRParamsNode = funcRParamsNode;
        this.rparent = rparent;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    public PrimaryExpNode getPrimaryExpNode() {
        return primaryExpNode;
    }

    public void setPrimaryExpNode(PrimaryExpNode primaryExpNode) {
        this.primaryExpNode = primaryExpNode;
    }

    public Pair getIdent() {
        return ident;
    }

    public void setIdent(Pair ident) {
        this.ident = ident;
    }

    public Pair getLparent() {
        return lparent;
    }

    public void setLparent(Pair lparent) {
        this.lparent = lparent;
    }

    public FuncRParamsNode getFuncRParamsNode() {
        return funcRParamsNode;
    }


    public void setFuncRParamsNode(FuncRParamsNode funcRParamsNode) {
        this.funcRParamsNode = funcRParamsNode;
    }

    public Pair getRparent() {
        return rparent;
    }

    public void setRparent(Pair rparent) {
        this.rparent = rparent;
    }

    public UnaryOpNode getUnaryOpNode() {
        return unaryOpNode;
    }

    public void setUnaryOpNode(UnaryOpNode unaryOpNode) {
        this.unaryOpNode = unaryOpNode;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }

    public void setUnaryExpNode(UnaryExpNode unaryExpNode) {
        this.unaryExpNode = unaryExpNode;
    }
}
