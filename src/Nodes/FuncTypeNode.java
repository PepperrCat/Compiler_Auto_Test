package Nodes;

import Nodes.Method.Node;
import middle.base.ValueType;
import models.Pair;
import symbol.FuncType;

public class FuncTypeNode extends Node {
    Pair voidCon = null;
    Pair intCon = null;
    Pair charCon = null;

    public FuncTypeNode(Pair voidCon, Pair intCon, Pair charCon) {
        this.voidCon = voidCon;
        this.intCon = intCon;
        this.charCon = charCon;
    }

    public FuncType getFuncType() {
        if (voidCon != null) return FuncType.VOID;
        else if (intCon != null) return FuncType.INT;
        else return FuncType.CHAR;
    }

    public ValueType getFuncValueType() {
        if (voidCon != null) return ValueType.VOID;
        else return ValueType.i32;

    }

    public Pair getVoidCon() {
        return voidCon;
    }

    public void setVoidCon(Pair voidCon) {
        this.voidCon = voidCon;
    }

    public Pair getIntCon() {
        return intCon;
    }

    public void setIntCon(Pair intCon) {
        this.intCon = intCon;
    }

    public Pair getCharCon() {
        return charCon;
    }

    public void setCharCon(Pair charCon) {
        this.charCon = charCon;
    }
}
