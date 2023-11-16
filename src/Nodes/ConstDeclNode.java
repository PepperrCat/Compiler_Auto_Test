package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConstDeclNode extends Node {
    Pair constCon =null;
    BTypeNode bType = null;
    ConstDefNode constDef = null;
    List<Pair> commaList=new ArrayList<>();
    List<ConstDefNode> constDefNodeList = new ArrayList<>();
    Pair semicnCon =null;

    public ConstDeclNode(Pair constCon, BTypeNode bType, ConstDefNode constDef, List<Pair> commaList, List<ConstDefNode> constDefNodeList, Pair semicnCon) {
        this.constCon = constCon;
        this.bType = bType;
        this.constDef = constDef;
        this.commaList = commaList;
        this.constDefNodeList = constDefNodeList;
        this.semicnCon = semicnCon;
    }
    public List<ConstDefNode> getAllConstDefNode(){
        List<ConstDefNode> list = new ArrayList<>();
        if(constDef!=null)list.add(constDef);
        list.addAll(constDefNodeList);
        return list;
    }
    public Pair getConstCon() {
        return constCon;
    }

    public void setConstCon(Pair constCon) {
        this.constCon = constCon;
    }

    public BTypeNode getbType() {
        return bType;
    }

    public void setbType(BTypeNode bType) {
        this.bType = bType;
    }

    public ConstDefNode getConstDef() {
        return constDef;
    }

    public void setConstDef(ConstDefNode constDef) {
        this.constDef = constDef;
    }

    public List<Pair> getCommaList() {
        return commaList;
    }

    public void setCommaList(List<Pair> commaList) {
        this.commaList = commaList;
    }

    public List<ConstDefNode> getConstDefNodeList() {
        return constDefNodeList;
    }

    public void setConstDefNodeList(List<ConstDefNode> constDefNodeList) {
        this.constDefNodeList = constDefNodeList;
    }

    public Pair getSemicnCon() {
        return semicnCon;
    }

    public void setSemicnCon(Pair semicnCon) {
        this.semicnCon = semicnCon;
    }
}
