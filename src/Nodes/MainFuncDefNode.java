package Nodes;

import Nodes.Method.Node;
import models.Pair;
import models.TokenType;

public class MainFuncDefNode extends Node {
    Pair intCon = null;
    Pair mainCon = null;
    Pair lparent =null;
    Pair rparent =  null;
    BlockNode blockNode  = null;

    public MainFuncDefNode(Pair intCon, Pair mainCon, Pair lparent, Pair rparent, BlockNode blockNode) {
        this.intCon = intCon;
        this.mainCon = mainCon;
        this.lparent = lparent;
        this.rparent = rparent;
        this.blockNode = blockNode;
    }

    public Pair getIntCon() {
        return intCon;
    }

    public void setIntCon(Pair intCon) {
        this.intCon = intCon;
    }

    public Pair getMainCon() {
        return mainCon;
    }

    public void setMainCon(Pair mainCon) {
        this.mainCon = mainCon;
    }

    public Pair getLparent() {
        return lparent;
    }

    public void setLparent(Pair lparent) {
        this.lparent = lparent;
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
