package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class MulExpNode extends Node {
    UnaryExpNode unaryExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<UnaryExpNode> unaryExpNodeList = new ArrayList<>();

    public MulExpNode(UnaryExpNode unaryExpNode, List<String> stringList, List<Pair> opList, List<UnaryExpNode> unaryExpNodeList) {
        this.unaryExpNode = unaryExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.unaryExpNodeList = unaryExpNodeList;
    }
    public List<UnaryExpNode> getAllUnaryExpNode(){
        List<UnaryExpNode> list = new ArrayList<>();
        if(unaryExpNode!=null)list.add(unaryExpNode);
        list.addAll(unaryExpNodeList);
        return list;
    }

    public UnaryExpNode getUnaryExpNode() {
        return unaryExpNode;
    }

    public void setUnaryExpNode(UnaryExpNode unaryExpNode) {
        this.unaryExpNode = unaryExpNode;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<Pair> getOpList() {
        return opList;
    }

    public void setOpList(List<Pair> opList) {
        this.opList = opList;
    }

    public List<UnaryExpNode> getUnaryExpNodeList() {
        return unaryExpNodeList;
    }

    public void setUnaryExpNodeList(List<UnaryExpNode> unaryExpNodeList) {
        this.unaryExpNodeList = unaryExpNodeList;
    }
}
