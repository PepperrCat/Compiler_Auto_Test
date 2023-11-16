package Nodes;

import Nodes.Method.Node;
import models.Pair;

public class FuncDefNode  extends Node {
    FuncTypeNode funcTypeNode=null;
    Pair ident =null;
    Pair lparent = null;
    FuncFParamsNode funcFParamsNode = null;
    Pair rparent = null;
    BlockNode blockNode=null;

    public FuncDefNode(FuncTypeNode funcTypeNode, Pair ident, Pair lparent, FuncFParamsNode funcFParamsNode, Pair rparent, BlockNode blockNode) {
        this.funcTypeNode = funcTypeNode;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParamsNode = funcFParamsNode;
        this.rparent = rparent;
        this.blockNode = blockNode;
    }

    public FuncTypeNode getFuncTypeNode() {
        return funcTypeNode;
    }

    public void setFuncTypeNode(FuncTypeNode funcTypeNode) {
        this.funcTypeNode = funcTypeNode;
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

    public FuncFParamsNode getFuncFParamsNode() {
        return funcFParamsNode;
    }

    public void setFuncFParamsNode(FuncFParamsNode funcFParamsNode) {
        this.funcFParamsNode = funcFParamsNode;
    }

    public Pair getRparent() {
        return rparent;
    }

    public void setRparent(Pair rparent) {
        this.rparent = rparent;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public void setBlockNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

}
