import Nodes.BTypeNode;
import Nodes.ExpNode;
import Nodes.Method.Node;
import models.Pair;
import models.TokenType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Test {
    Pair ident = null;
    List<Pair> strings = new ArrayList<>();
    BTypeNode bTypeNode = null;
    List<BTypeNode> bTypeNodes;


    public Test(Pair ident, List<Pair> strings, BTypeNode bTypeNode, List<BTypeNode> bTypeNodes) {
        this.ident = ident;
        this.strings = strings;
        this.bTypeNode = bTypeNode;
        this.bTypeNodes = bTypeNodes;
    }

    public static void main(String[] args) throws IllegalAccessException {
//        System.out.print("hi\n");
        List<Pair> pairList = new ArrayList<>();
        List<BTypeNode> bTypeNodes = new ArrayList<>();
        bTypeNodes.add(new BTypeNode(new Pair(TokenType.INTTK, "int")));
        bTypeNodes.add(new BTypeNode(new Pair(TokenType.INTTK, "int2")));
        pairList.add(new Pair(TokenType.ASSIGN, "="));
        pairList.add(new Pair(TokenType.LEQ, "<="));

        BTypeNode bTypeNode1 = new BTypeNode(new Pair(TokenType.INTTK, "int"));
        Test test = new Test(new Pair(TokenType.ASSIGN, "!="), pairList, bTypeNode1, bTypeNodes);
        Field[] fields = test.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object Node = null;
            if (field.getType() == List.class) {
                Type fieldType = field.getGenericType();
                if (fieldType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    if (typeArguments.length == 1 && typeArguments[0] == Pair.class) {
                        // field 是 List<Pair> 类型
                        List<Pair> list = (List<Pair>) field.get(test);
                        for (Pair pair : list) {
                            // 在这里操作 Pair 对象
                            pair.print();
                        }
                    } else if (typeArguments.length == 1 && Nodes.Method.Node.class.isAssignableFrom((Class<?>) typeArguments[0])) {
                        // field 是 List<Node> 类型
                        List<BTypeNode> list = (List<BTypeNode>) field.get(test);
                        for (BTypeNode node : list) {
                            // 在这里操作 Node 对象
                            node.print();
                        }
                    }
                }

            } else if (Nodes.Method.Node.class.isAssignableFrom(field.getType())) {
                Nodes.Method.Node node = (Nodes.Method.Node) field.get(test);
                node.print();
            } else if (Pair.class.isAssignableFrom(field.getType())) {
                Pair pair = (Pair) field.get(test);
                pair.print();

            }
            ;


        }

    }
}
