package middle.base;

public enum Op {
    add,
    sub,
    mul,
    sdiv,
    sgt,
    sge,
    slt,
    sle,
    eq,
    ne,
    and,
    or,
    srem,
    Error;

    public static String op2str(Op op) {
        switch (op) {
            case add -> {
                return "+";
            }
            case sub -> {
                return "-";
            }
            case mul -> {
                return "*";
            }
            case sdiv -> {
                return "/";
            }
            case sgt -> {
                return ">";
            }
            case sge -> {
                return ">=";
            }
            case slt -> {
                return "<";
            }
            case sle -> {
                return "<=";
            }
            case eq -> {
                return "==";
            }
            case ne -> {
                return "!=";
            }
            case and -> {
                return "&&";
            }
            case or -> {
                return "||";
            }
            case srem -> {
                return "%";
            }
            default -> {
                return "Error";
            }
        }
    }

    public static Op str2op(String str) {
        switch (str) {
            case "+" -> {
                return add;
            }
            case "-" -> {
                return sub;
            }
            case "*" -> {
                return mul;
            }
            case "/" -> {
                return sdiv;
            }
            case ">" -> {
                return sgt;
            }
            case ">=" -> {
                return sge;
            }
            case "<" -> {
                return slt;
            }
            case "<=" -> {
                return sle;
            }
            case "==" -> {
                return eq;
            }
            case "!=" -> {
                return ne;
            }
            case "&&" -> {
                return and;
            }
            case "||" -> {
                return or;
            }
            case "%" -> {
                return srem;
            }
            default -> {
                return Error;
            }
        }
    }
}
