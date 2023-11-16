package backend.MIPSModule;

import java.util.ArrayList;

public class MIPSGlobalVar {
    private String name;
    private boolean hasInit;    // 有初始化用.space 没有用.word
    private boolean isStr;
    private String content; //str
    private ArrayList<Integer> elements;
    private int size;

    public MIPSGlobalVar(String name, int size) {
        this.name = name.replace("@", "");
        this.size = size;
    }

    public MIPSGlobalVar(String name, String content) { // str
        this.name = name.replace("@", "");
        this.hasInit = true;
        this.isStr = true;
        this.size = content.length() + 1;   // '\0'
        this.content = content;
    }

    public MIPSGlobalVar(String name, ArrayList<Integer> elements) {
        this.name = name.replace("@", "");
        this.elements = elements;
        this.size = 4 * elements.size();
        this.hasInit = true;
    }

    public String getName() {
        return name;
    }

    public boolean hasInit() {
        return hasInit;
    }

    public boolean isStr() {
        return isStr;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public String toString(int space) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        for (int i = 0; i < space; i++) {
            sb.append(" ");
        }
        if (isStr) {
            sb.append(".asciiz \"").append(content).append("\"");
        } else if (hasInit) {
            sb.append(".word ");
            for (int i = 0; i <= elements.size() - 1; i++) {
                sb.append(elements.get(i) + " ");
            }
        } else {
            sb.append(".space ").append(size);
        }
        return sb.toString();
    }
}
