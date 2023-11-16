package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class LOrExpNode  extends Node {
    LAndExpNode lAndExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<LAndExpNode> lAndExpNodeArrayList = new ArrayList<>();

    public LOrExpNode(LAndExpNode lAndExpNode, List<String> stringList, List<Pair> opList, List<LAndExpNode> lAndExpNodeArrayList) {
        this.lAndExpNode = lAndExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.lAndExpNodeArrayList = lAndExpNodeArrayList;
    }
    public List<LAndExpNode> getAllLAndExpNode(){
        List<LAndExpNode> list = new ArrayList<>();
        if(lAndExpNode!=null)list.add(lAndExpNode);
        list.addAll(lAndExpNodeArrayList);
        return list;
    }
    public LAndExpNode getlAndExpNode() {
        return lAndExpNode;
    }

    public void setlAndExpNode(LAndExpNode lAndExpNode) {
        this.lAndExpNode = lAndExpNode;
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

    public List<LAndExpNode> getlAndExpNodeArrayList() {
        return lAndExpNodeArrayList;
    }

    public void setlAndExpNodeArrayList(List<LAndExpNode> lAndExpNodeArrayList) {
        this.lAndExpNodeArrayList = lAndExpNodeArrayList;
    }
}
