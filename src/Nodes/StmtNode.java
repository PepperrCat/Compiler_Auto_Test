package Nodes;

import Nodes.Method.Node;
import models.Pair;

import java.util.ArrayList;
import java.util.List;

public class StmtNode extends Node {
    LValNode lValNode = null;
    Pair assignCon = null;
    ExpNode lValExpNode = null;
    Pair getintCon = null;
    Pair getintLParent = null;
    Pair getintRParent = null;
    Pair lValSemicn = null;

    //  | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    Pair ifCon = null;
    Pair ifLparentCon = null;
    CondNode ifCondNode = null;
    Pair ifRparentCon = null;
    StmtNode ifStmtNode = null;
    Pair elseCon = null;
    StmtNode elseStmtNode = null;

    //  | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    Pair forCon = null;
    Pair forLparentCon = null;
    ForStmtNode startForStmtNode = null;
    Pair controlSemicn1 = null;
    CondNode forCondNode = null;
    Pair controlSemicn2 = null;
    ForStmtNode endForStmtNode = null;
    Pair forRparentCon = null;
    StmtNode forStmt = null;

    //  | 'break' ';'
    Pair breakCon = null;
    Pair breakSemicn = null;

    //  | 'continue' ';'
    Pair continueCon = null;
    Pair continueSemicn = null;

    //  | 'return' [Exp] ';'
    Pair returnCon = null;
    ExpNode returnExp = null;
    Pair returnSemicn = null;

    //  | 'printf''('FormatString{','Exp}')'';'
    Pair printfCon = null;
    Pair printfLparent = null;
    Pair formatString = null;
    List<Pair> printfCommaList = new ArrayList<>();
    List<ExpNode> printfExpList = new ArrayList<>();
    Pair printfRparent = null;
    Pair printfSemicn = null;

    //  | Block
    BlockNode blockNode = null;

    //  | [Exp] ';'
    ExpNode expNode = null;
    Pair expSemicn = null;

    public StmtNode(LValNode lValNode, Pair assignCon, ExpNode lValExpNode, Pair getintCon, Pair getintLParent, Pair getintRParent, Pair lValSemicn, Pair ifCon, Pair ifLparentCon, CondNode ifCondNode, Pair ifRparentCon, StmtNode ifStmtNode, Pair elseCon, StmtNode elseStmtNode, Pair forCon, Pair forLparentCon, ForStmtNode startForStmtNode, Pair controlSemicn1, CondNode forCondNode, Pair controlSemicn2, ForStmtNode endForStmtNode, Pair forRparentCon, StmtNode forStmt, Pair breakCon, Pair breakSemicn, Pair continueCon, Pair continueSemicn, Pair returnCon, ExpNode returnExp, Pair returnSemicn, Pair printfCon, Pair printfLparent, Pair formatString, List<Pair> printfCommaList, List<ExpNode> printfExpList, Pair printfRparent, Pair printfSemicn, BlockNode blockNode, ExpNode expNode, Pair expSemicn) {
        this.lValNode = lValNode;
        this.assignCon = assignCon;
        this.lValExpNode = lValExpNode;
        this.getintCon = getintCon;
        this.getintLParent = getintLParent;
        this.getintRParent = getintRParent;
        this.lValSemicn = lValSemicn;
        this.ifCon = ifCon;
        this.ifLparentCon = ifLparentCon;
        this.ifCondNode = ifCondNode;
        this.ifRparentCon = ifRparentCon;
        this.ifStmtNode = ifStmtNode;
        this.elseCon = elseCon;
        this.elseStmtNode = elseStmtNode;
        this.forCon = forCon;
        this.forLparentCon = forLparentCon;
        this.startForStmtNode = startForStmtNode;
        this.controlSemicn1 = controlSemicn1;
        this.forCondNode = forCondNode;
        this.controlSemicn2 = controlSemicn2;
        this.endForStmtNode = endForStmtNode;
        this.forRparentCon = forRparentCon;
        this.forStmt = forStmt;
        this.breakCon = breakCon;
        this.breakSemicn = breakSemicn;
        this.continueCon = continueCon;
        this.continueSemicn = continueSemicn;
        this.returnCon = returnCon;
        this.returnExp = returnExp;
        this.returnSemicn = returnSemicn;
        this.printfCon = printfCon;
        this.printfLparent = printfLparent;
        this.formatString = formatString;
        this.printfCommaList = printfCommaList;
        this.printfExpList = printfExpList;
        this.printfRparent = printfRparent;
        this.printfSemicn = printfSemicn;
        this.blockNode = blockNode;
        this.expNode = expNode;
        this.expSemicn = expSemicn;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public void setlValNode(LValNode lValNode) {
        this.lValNode = lValNode;
    }

    public Pair getAssignCon() {
        return assignCon;
    }

    public void setAssignCon(Pair assignCon) {
        this.assignCon = assignCon;
    }

    public ExpNode getlValExpNode() {
        return lValExpNode;
    }

    public void setlValExpNode(ExpNode lValExpNode) {
        this.lValExpNode = lValExpNode;
    }

    public Pair getGetintCon() {
        return getintCon;
    }

    public void setGetintCon(Pair getintCon) {
        this.getintCon = getintCon;
    }

    public Pair getGetintLParent() {
        return getintLParent;
    }

    public void setGetintLParent(Pair getintLParent) {
        this.getintLParent = getintLParent;
    }

    public Pair getGetintRParent() {
        return getintRParent;
    }

    public void setGetintRParent(Pair getintRParent) {
        this.getintRParent = getintRParent;
    }

    public Pair getlValSemicn() {
        return lValSemicn;
    }

    public void setlValSemicn(Pair lValSemicn) {
        this.lValSemicn = lValSemicn;
    }

    public Pair getIfCon() {
        return ifCon;
    }

    public void setIfCon(Pair ifCon) {
        this.ifCon = ifCon;
    }

    public Pair getIfLparentCon() {
        return ifLparentCon;
    }

    public void setIfLparentCon(Pair ifLparentCon) {
        this.ifLparentCon = ifLparentCon;
    }

    public CondNode getIfCondNode() {
        return ifCondNode;
    }

    public void setIfCondNode(CondNode ifCondNode) {
        this.ifCondNode = ifCondNode;
    }

    public Pair getIfRparentCon() {
        return ifRparentCon;
    }

    public void setIfRparentCon(Pair ifRparentCon) {
        this.ifRparentCon = ifRparentCon;
    }

    public StmtNode getIfStmtNode() {
        return ifStmtNode;
    }

    public void setIfStmtNode(StmtNode ifStmtNode) {
        this.ifStmtNode = ifStmtNode;
    }

    public Pair getElseCon() {
        return elseCon;
    }

    public void setElseCon(Pair elseCon) {
        this.elseCon = elseCon;
    }

    public StmtNode getElseStmtNode() {
        return elseStmtNode;
    }

    public void setElseStmtNode(StmtNode elseStmtNode) {
        this.elseStmtNode = elseStmtNode;
    }

    public Pair getForCon() {
        return forCon;
    }

    public void setForCon(Pair forCon) {
        this.forCon = forCon;
    }

    public Pair getForLparentCon() {
        return forLparentCon;
    }

    public void setForLparentCon(Pair forLparentCon) {
        this.forLparentCon = forLparentCon;
    }

    public ForStmtNode getStartForStmtNode() {
        return startForStmtNode;
    }

    public void setStartForStmtNode(ForStmtNode startForStmtNode) {
        this.startForStmtNode = startForStmtNode;
    }

    public Pair getControlSemicn1() {
        return controlSemicn1;
    }

    public void setControlSemicn1(Pair controlSemicn1) {
        this.controlSemicn1 = controlSemicn1;
    }

    public CondNode getForCondNode() {
        return forCondNode;
    }

    public void setForCondNode(CondNode forCondNode) {
        this.forCondNode = forCondNode;
    }

    public Pair getControlSemicn2() {
        return controlSemicn2;
    }

    public void setControlSemicn2(Pair controlSemicn2) {
        this.controlSemicn2 = controlSemicn2;
    }

    public ForStmtNode getEndForStmtNode() {
        return endForStmtNode;
    }

    public void setEndForStmtNode(ForStmtNode endForStmtNode) {
        this.endForStmtNode = endForStmtNode;
    }

    public Pair getForRparentCon() {
        return forRparentCon;
    }

    public void setForRparentCon(Pair forRparentCon) {
        this.forRparentCon = forRparentCon;
    }

    public StmtNode getForStmt() {
        return forStmt;
    }

    public void setForStmt(StmtNode forStmt) {
        this.forStmt = forStmt;
    }

    public Pair getBreakCon() {
        return breakCon;
    }

    public void setBreakCon(Pair breakCon) {
        this.breakCon = breakCon;
    }

    public Pair getBreakSemicn() {
        return breakSemicn;
    }

    public void setBreakSemicn(Pair breakSemicn) {
        this.breakSemicn = breakSemicn;
    }

    public Pair getContinueCon() {
        return continueCon;
    }

    public void setContinueCon(Pair continueCon) {
        this.continueCon = continueCon;
    }

    public Pair getContinueSemicn() {
        return continueSemicn;
    }

    public void setContinueSemicn(Pair continueSemicn) {
        this.continueSemicn = continueSemicn;
    }

    public Pair getReturnCon() {
        return returnCon;
    }

    public void setReturnCon(Pair returnCon) {
        this.returnCon = returnCon;
    }

    public ExpNode getReturnExp() {
        return returnExp;
    }

    public void setReturnExp(ExpNode returnExp) {
        this.returnExp = returnExp;
    }

    public Pair getReturnSemicn() {
        return returnSemicn;
    }

    public void setReturnSemicn(Pair returnSemicn) {
        this.returnSemicn = returnSemicn;
    }

    public Pair getPrintfCon() {
        return printfCon;
    }

    public void setPrintfCon(Pair printfCon) {
        this.printfCon = printfCon;
    }

    public Pair getPrintfLparent() {
        return printfLparent;
    }

    public void setPrintfLparent(Pair printfLparent) {
        this.printfLparent = printfLparent;
    }

    public Pair getFormatString() {
        return formatString;
    }

    public void setFormatString(Pair formatString) {
        this.formatString = formatString;
    }

    public List<Pair> getPrintfCommaList() {
        return printfCommaList;
    }

    public void setPrintfCommaList(List<Pair> printfCommaList) {
        this.printfCommaList = printfCommaList;
    }

    public List<ExpNode> getPrintfExpList() {
        return printfExpList;
    }

    public void setPrintfExpList(List<ExpNode> printfExpList) {
        this.printfExpList = printfExpList;
    }

    public Pair getPrintfRparent() {
        return printfRparent;
    }

    public void setPrintfRparent(Pair printfRparent) {
        this.printfRparent = printfRparent;
    }

    public Pair getPrintfSemicn() {
        return printfSemicn;
    }

    public void setPrintfSemicn(Pair printfSemicn) {
        this.printfSemicn = printfSemicn;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public void setBlockNode(BlockNode blockNode) {
        this.blockNode = blockNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public void setExpNode(ExpNode expNode) {
        this.expNode = expNode;
    }

    public Pair getExpSemicn() {
        return expSemicn;
    }

    public void setExpSemicn(Pair expSemicn) {
        this.expSemicn = expSemicn;
    }
//    @Override
//    public void print() throws IllegalAccessException {
//        System.out.println(this.getClass().toString());
//    }
}
