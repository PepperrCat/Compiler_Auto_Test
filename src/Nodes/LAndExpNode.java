package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class LAndExpNode  extends Node {
    EqExpNode eqExpNode = null;
    List<String> stringList = new ArrayList<>();
    List<Pair> opList = new ArrayList<>();
    List<EqExpNode> eqExpNodeArrayList = new ArrayList<>();

    public LAndExpNode(EqExpNode eqExpNode, List<String> stringList, List<Pair> opList, List<EqExpNode> eqExpNodeArrayList) {
        this.eqExpNode = eqExpNode;
        this.stringList = stringList;
        this.opList = opList;
        this.eqExpNodeArrayList = eqExpNodeArrayList;
    }
    public List<EqExpNode> getAllEqExpNode(){
        List<EqExpNode> list = new ArrayList<>();
        if(eqExpNode!=null)list.add(eqExpNode);
        list.addAll(eqExpNodeArrayList);
        return list;
    }

    public EqExpNode getEqExpNode() {
        return eqExpNode;
    }

    public void setEqExpNode(EqExpNode eqExpNode) {
        this.eqExpNode = eqExpNode;
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

    public List<EqExpNode> getEqExpNodeArrayList() {
        return eqExpNodeArrayList;
    }

    public void setEqExpNodeArrayList(List<EqExpNode> eqExpNodeArrayList) {
        this.eqExpNodeArrayList = eqExpNodeArrayList;
    }
}
