package back;

import java.util.*;

public class MemMan {
    String _none = "None";
    public static String[] registerList = {
            "$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
            "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$s0",
            "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9",
            "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"
    };
    int stackPinter;
    HashMap<String, StackEle> virtual2StackEle = new HashMap<>();
    HashSet<String> paramVirtualReg = new HashSet<>();
    public HashSet<String> arrayVirtualReg = new HashSet<>();
    public HashSet<String> globalSet = new HashSet<>();
    public HashMap<String, String> virtual2Global = new HashMap<>();
    public ArrayList<Register> tempRegPool = new ArrayList<>();

    public String[] temRegUseMap = new String[registerList.length];

    public MemMan() {

        for (int i = 0; i < registerList.length; i++) {
            tempRegPool.add(new Register(registerList[i]));
            temRegUseMap[i] = _none;
        }
    }


    int tempNum = 1;


    public void clear(){
        stackPinter = 0;
        virtual2StackEle.clear();
        virtual2Global.clear();
        arrayVirtualReg.clear();
        paramVirtualReg.clear();
        Arrays.fill(temRegUseMap, _none);
    }

    public StackEle getVirtualInStack(String virtualName) {
        StackEle ele = new StackEle(stackPinter);
        virtual2StackEle.put(virtualName, ele);
        stackPinter++;
        return ele;
    }

    public void getVirtualInStack(String virtualName, int memSize) {
        virtual2StackEle.put(virtualName, new StackEle(stackPinter));
        stackPinter += memSize;
    }

    public boolean isParam(String name) {
        return paramVirtualReg.contains(name);

    }

    public boolean isArrayVirtualReg(String name) {
        return arrayVirtualReg.contains(name);
    }

    public StackEle lookUpStack(String name) {
        return virtual2StackEle.getOrDefault(name, null);
    }

    public void addToArrayVirtualReg(String name) {
        arrayVirtualReg.add(name);
    }

    public boolean isGlobal(String name) {
        return globalSet.contains(name);
    }

    public void addToGlobal(String virtualRegister, String name) {
        virtual2Global.put(virtualRegister, name);
    }

    public Register getTempReg(String name) {
        for (int i = 8; i < 18; i++) {
            if (temRegUseMap[i].equals(_none)) {
                temRegUseMap[i] = name;
                return tempRegPool.get(i);
            }
        }
        throw new RuntimeException("name:"+name);
    }

    public String getTempNum() {
        return "temp" + tempNum++;
    }

    public void freeTempReg(Register arrayAddr) {
        temRegUseMap[arrayAddr.getNum()] = _none;

    }

    public Register lookUpTemp(String virtualNum) {
        Register res = null;
        for (int i = 0; i < temRegUseMap.length; i++) {
            if (temRegUseMap[i].equals(virtualNum)) {
                res = tempRegPool.get(i);
            }
        }
        return res;
    }

    public void addToParam(String name) {
        paramVirtualReg.add(name);
    }

    public String lookupGlobal(String name) {
        String s = virtual2Global.getOrDefault(name, null);
        if (s != null) s = s.substring(1);
        return s;
    }


    public List<Record> recordList = new ArrayList<>();

    public void record(Register tempReg, StackEle stack, String name) {
        recordList.add(new Record(tempReg, stack, name));
    }

    public void recordClear() {
        recordList.clear();
    }
}

class Record {
    Register tempReg;
    StackEle stack;
    String name;

    public Record(Register tempReg, StackEle stack, String name) {
        this.tempReg = tempReg;
        this.stack = stack;
        this.name = name;
    }

    public Register getTempReg() {
        return tempReg;
    }

    public void setTempReg(Register tempReg) {
        this.tempReg = tempReg;
    }

    public StackEle getStack() {
        return stack;
    }

    public void setStack(StackEle stack) {
        this.stack = stack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
