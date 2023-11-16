package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends Node {
    Pair lbrace = null;
    List<BlockItemNode> blockItemNodeList = new ArrayList<>();
    Pair rbrace = null;

    public BlockNode(Pair lbrace, List<BlockItemNode> blockItemNodeList, Pair rbrace) {
        this.lbrace = lbrace;
        this.blockItemNodeList = blockItemNodeList;
        this.rbrace = rbrace;
    }

    public Pair getLbrace() {
        return lbrace;
    }

    public void setLbrace(Pair lbrace) {
        this.lbrace = lbrace;
    }

    public List<BlockItemNode> getBlockItemNodeList() {
        return blockItemNodeList;
    }

    public void setBlockItemNodeList(List<BlockItemNode> blockItemNodeList) {
        this.blockItemNodeList = blockItemNodeList;
    }

    public Pair getRbrace() {
        return rbrace;
    }

    public void setRbrace(Pair rbrace) {
        this.rbrace = rbrace;
    }
}
