package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class EqExpNode  extends Node {
    RelExpNode relExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<RelExpNode> relExpNodeArrayList = new ArrayList<>();

    public EqExpNode(RelExpNode relExpNode, List<String> stringList, List<Pair> opList, List<RelExpNode> relExpNodeArrayList) {
        this.relExpNode = relExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.relExpNodeArrayList = relExpNodeArrayList;
    }
    public List<RelExpNode> getAllRelExpNode(){
        List<RelExpNode> list = new ArrayList<>();
        if(relExpNode!=null)list.add(relExpNode);
        list.addAll(relExpNodeArrayList);
        return list;
    }

    public RelExpNode getRelExpNode() {
        return relExpNode;
    }

    public void setRelExpNode(RelExpNode relExpNode) {
        this.relExpNode = relExpNode;
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

    public List<RelExpNode> getRelExpNodeArrayList() {
        return relExpNodeArrayList;
    }

    public void setRelExpNodeArrayList(List<RelExpNode> relExpNodeArrayList) {
        this.relExpNodeArrayList = relExpNodeArrayList;
    }
}
