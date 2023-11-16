package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class AddExpNode  extends Node {
    MulExpNode mulExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<MulExpNode> mulExpNodeArrayList = new ArrayList<>();

    public AddExpNode(MulExpNode mulExpNode, List<String> stringList, List<Pair> opList, List<MulExpNode> mulExpNodeArrayList) {
        this.mulExpNode = mulExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.mulExpNodeArrayList = mulExpNodeArrayList;
    }
    public List<MulExpNode> getAllMulExpNode(){
        List<MulExpNode> list = new ArrayList<>();
        if(mulExpNode!=null)list.add(mulExpNode);
        list.addAll(mulExpNodeArrayList);
        return list;
    }

    public MulExpNode getMulExpNode() {
        return mulExpNode;
    }

    public void setMulExpNode(MulExpNode mulExpNode) {
        this.mulExpNode = mulExpNode;
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

    public List<MulExpNode> getMulExpNodeArrayList() {
        return mulExpNodeArrayList;
    }

    public void setMulExpNodeArrayList(List<MulExpNode> mulExpNodeArrayList) {
        this.mulExpNodeArrayList = mulExpNodeArrayList;
    }
}
