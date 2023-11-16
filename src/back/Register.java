package back;

public class Register {
    String name;

    public Register(int i) {
        this.name = MemMan.registerList[i];
    }

    public Register(String name) {
        this.name = name;
    }

    public int getNum() {
        for (int i = 0; i < MemMan.registerList.length; i++) {
            if (MemMan.registerList[i].equals(this.name))
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
