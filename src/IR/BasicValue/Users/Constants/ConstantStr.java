package IR.BasicValue.Users.Constants;

import IR.BasicValue.Users.Constant;
import IR.Types.ArrayType;
import IR.Types.IntType;

import java.util.Objects;

public class ConstantStr extends Constant {
    private String content;

    public ConstantStr(String str) {
        super(new ArrayType(IntType.I8, str.length() + 1));
        this.content = str;
    }

    @Override
    public String toString() {
        return "c\"" + content.replace("\n", "\\0a") + "\\00\"";
    }

    public String getContent() {
        return content.replace("\n", "\\n");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConstantStr that = (ConstantStr) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content);
    }
    @Override
    public String getValueName() {
        return toString();
    }
}
