package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class FuncRParamsNode  extends Node {
    ExpNode expNode=null;
    List<Pair> semicnList = new ArrayList<>();
    List<ExpNode> expNodeList = new ArrayList<>();

    public FuncRParamsNode(ExpNode expNode, List<Pair> semicnList, List<ExpNode> expNodeList) {
        this.expNode = expNode;
        this.semicnList = semicnList;
        this.expNodeList = expNodeList;
    }

    public ExpNode getExpNode() {
        return expNode;
    }


    public void setExpNode(ExpNode expNode) {
        this.expNode = expNode;
    }

    public List<Pair> getSemicnList() {
        return semicnList;
    }

    public void setSemicnList(List<Pair> semicnList) {
        this.semicnList = semicnList;
    }

    public List<ExpNode> getExpNodeList() {
        return expNodeList;
    }
    public List<ExpNode> getAllExpNodeList() {
        List<ExpNode> list = new ArrayList<>();
        if (expNode != null) {
            list.add(expNode);
        }
        list.addAll(expNodeList);
        return list;
    }

    public void setExpNodeList(List<ExpNode> expNodeList) {
        this.expNodeList = expNodeList;
    }
}
