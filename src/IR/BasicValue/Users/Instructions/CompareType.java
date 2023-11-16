package IR.BasicValue.Users.Instructions;

public enum CompareType {
    LSS,
    LEQ,
    GRE,
    GEQ,
    EQL,
    NEQ,
    ;

    @Override
    public String toString() {
        switch (this) {
            case EQL -> {
                return "eq";
            }
            case GEQ -> {
                return "sge";
            }
            case GRE -> {
                return "sgt";
            }
            case LEQ -> {
                return "sle";
            }
            case LSS -> {
                return "slt";
            }
            case NEQ -> {
                return "ne";
            }
        }
        return "";
    }
    public String toMIPSSet() {
        switch (this) {
            case EQL -> {
                return "seq";
            }
            case GEQ -> {
                return "sge";
            }
            case GRE -> {
                return "sgt";
            }
            case LEQ -> {
                return "sle";
            }
            case LSS -> {
                return "slt";
            }
            case NEQ -> {
                return "sne";
            }
        }
        return "";
    }

    public String toOPMIPSSet() {
        switch (this) {
            case EQL -> {
                return "seq";
            }
            case GEQ -> {
                return "sle";
            }
            case GRE -> {
                return "slt";
            }
            case LEQ -> {
                return "sge";
            }
            case LSS -> {
                return "sgt";
            }
            case NEQ -> {
                return "sne";
            }
        }
        return "";
    }
}
