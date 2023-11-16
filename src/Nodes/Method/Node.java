package Nodes.Method;

import front.Parser;
import models.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    public void print() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object object = field.get(this);

            if (field.getType() == List.class) {
                List<Field> fieldList = new ArrayList<>();

                for (; i < fields.length; i++) {
                    if (fields[i].getType() == List.class) {
                        fields[i].setAccessible(true);
                        fieldList.add(fields[i]);
                    } else {
                        break;
                    }
                }
                i--;
                List<?> rowList = (ArrayList<?>) field.get(this);
                int len = rowList.size();
                for (int index = 0; index < len; index++) {
                    for (Field tmpField : fieldList) {
                        Object object2 = tmpField.get(this);
                        Type fieldType = tmpField.getGenericType();
                        if (fieldType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                            Type[] typeArguments = parameterizedType.getActualTypeArguments();

                            if (typeArguments.length == 1 && typeArguments[0] == Pair.class) {
                                // field 是 List<Pair> 类型
                                List<Pair> list = (List<Pair>) object2;
                                if (index < list.size())
                                    list.get(index).print();
//                                list.get(index).print();
                            } else if (typeArguments.length == 1 && Nodes.Method.Node.class.isAssignableFrom((Class<?>) typeArguments[0])) {
                                // field 是 List<Node> 类型
                                List<Node> list = (List<Node>) object2;
                                list.get(index).print();
                            } else if (typeArguments.length == 1 && typeArguments[0] == String.class) {
                                // field 是 List<Node> 类型
                                List<String> list = (List<String>) object2;
                                Print.printParser(list.get(index));
                            }
                        }
                    }
                }


            } else if (Node.class.isAssignableFrom(field.getType())) {
                Node node = (Node) object;
                if (node != null) node.print();
            } else if (Pair.class.isAssignableFrom(field.getType())) {
                Pair pair = (Pair) object;
                if (pair != null)
                    pair.print();

            }
        }
        String notEnd = Parser.nodeClassMap.get(this.getClass());
        String[] special = {"<BlockItem>", "<Decl>", "<BType>"};
        if (!Arrays.asList(special).contains(notEnd))
            Print.printParser(Parser.nodeClassMap.get(this.getClass()));
    }
}
