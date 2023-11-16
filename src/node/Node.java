package node;

import Config.Config;
import IR.BasicValue.BasicBlock;
import io.IO;
import token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private NodeType nodeType;

    private List<Node> subNodes;

    private Token token;

    private BasicBlock trueBBlock;
    private BasicBlock falseBBlock;

    private ArrayList<Integer> dimensions;

    public BasicBlock getTrueBBlock() {
        return trueBBlock;
    }

    public void setTrueBBlock(BasicBlock trueBBlock) {
        if (isCondNode())
            this.trueBBlock = trueBBlock;
    }

    public BasicBlock getFalseBBlock() {
        return falseBBlock;
    }

    public void setFalseBBlock(BasicBlock falseBBlock) {
        if (isCondNode())
            this.falseBBlock = falseBBlock;
    }


    public Node(NodeType nodeType, List<Node> subNodes) {
        this.nodeType = nodeType;
        this.subNodes = subNodes;
    }

    public void setToken(Token token) {
        if (nodeType == NodeType.End || nodeType == NodeType.FormatString)
            this.token = token;
        else
            this.token = null;
    }

    public Token getToken() {
        return token;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public List<Node> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<Node> subNodes) {
        this.subNodes = subNodes;
    }

    public void print() throws IOException { // 更改文法的节点有MulExp、AddExp、RelExp、EqExp、LAndExp、LOrExp，由于本人将子树左右调换，因此输出的时候同样反着输出即可
        if (nodeType == NodeType.End || nodeType == NodeType.FormatString) {
            IO.write(Config.outFileMap.get("parser"), token.toString());
            return;
        }
        if (subNodes == null)
            return;
        if (isChangedNode()) {
            subNodes.get(0).print();
            IO.write(Config.outFileMap.get("parser"), '<' + nodeType.toString() + '>' + '\n');
            if (subNodes.size() == 3) {
                subNodes.get(1).print();
                subNodes.get(2).print();
            }
        } else {
            for (Node node : subNodes) {
                node.print();
            }
            if (nodeType != NodeType.Decl && nodeType != NodeType.BlockItem && nodeType != NodeType.BType)
                IO.write(Config.outFileMap.get("parser"), '<' + nodeType.toString() + '>' + '\n');
        }

    }

    private boolean isChangedNode() {
        return nodeType == NodeType.MulExp
                || nodeType == NodeType.AddExp
                || nodeType == NodeType.RelExp
                || nodeType == NodeType.EqExp
                || nodeType == NodeType.LAndExp
                || nodeType == NodeType.LOrExp;
    }

    private boolean isCondNode() {
        return nodeType == NodeType.Cond
                || nodeType == NodeType.LAndExp
                || nodeType == NodeType.LOrExp;
    }

    public void setDimensions(ArrayList<Integer> dimensions) {
        this.dimensions = dimensions;
    }

    public ArrayList<Integer> getDimensions() {
        return dimensions;
    }
}


