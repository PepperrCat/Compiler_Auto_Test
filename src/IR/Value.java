package IR;

import IR.BasicValue.User;
import IR.BasicValue.Users.Instructions.PhiInstruction;
import IR.Types.Type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public abstract class Value {
    private final int valueId;
    private static int globalValueId = 0;   // static记录valueId保证其递增且唯一
    private String valueName;
    private Value valueParent;
    private LinkedList<Use> uses = new LinkedList<>();
    private Type valueType;

    public Value(String valueName, Value valueParent, Type valueType) {
        this.valueName = valueName;
        this.valueParent = valueParent;
        this.valueType = valueType;
        valueId = globalValueId++;
    }

    public int userSize() {
        return uses.size();
    }

    public boolean isUserEmpty() {
        return uses.size() == 0;
    }

    public Use userBack() {
        return uses.getLast();
    }

    public Type getValueType() {
        return valueType;
    }

    public boolean hasName() {
        return valueName != null && !valueName.equals("");
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Value getValueParent() {
        return valueParent;
    }

    public void setValueParent(Value valueParent) {
        this.valueParent = valueParent;
    }

    public void addUse(User user) {
        uses.add(new Use(user, this));
    }

    public void addUse(Use use) {
        uses.add(use);
    }

    public void removeUser(User u) {
        for (Use use : uses) {
            if (use.getUser().equals(u)) {
                uses.remove(use);
                return;
            }
        }
    }

    public void replaceUses(Value re) {
        ArrayList<Use> tmp = new ArrayList<>(uses);
        uses.clear();
        for (Use use : tmp) {
            User u = use.getUser();
            for (int i = 0; i < u.getNumOperands(); i++) {
                Value v = u.getOperands().get(i).getValue();
                if (v.equals(this)) {
                    u.setValue(i, re);
                }
            }
        }
    }

    public Use getUse(int index) {
        return uses.get(index);
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        uses.forEach(use -> users.add(use.getUser()));
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return valueId == value.valueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueId);
    }


}
