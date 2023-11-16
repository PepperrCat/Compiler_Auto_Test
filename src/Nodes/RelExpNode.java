package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class RelExpNode  extends Node {
    AddExpNode addExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<AddExpNode> addExpNodeArrayList = new ArrayList<>();

    public RelExpNode(AddExpNode addExpNode, List<String> stringList, List<Pair> opList, List<AddExpNode> addExpNodeArrayList) {
        this.addExpNode = addExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.addExpNodeArrayList = addExpNodeArrayList;
    }
    public List<AddExpNode> getAllAddExpNode(){
        List<AddExpNode> list = new ArrayList<>();
        if(addExpNode!=null)list.add(addExpNode);
        list.addAll(addExpNodeArrayList);
        return list;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    public void setAddExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
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

    public List<AddExpNode> getAddExpNodeArrayList() {
        return addExpNodeArrayList;
    }

    public void setAddExpNodeArrayList(List<AddExpNode> addExpNodeArrayList) {
        this.addExpNodeArrayList = addExpNodeArrayList;
    }
}
