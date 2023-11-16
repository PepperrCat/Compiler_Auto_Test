package backend.MIPSInfo;


import backend.MIPSOperand.MIPSRegister;
import backend.MIPSOperand.PhysicalRegister;

import java.util.ArrayList;
import java.util.HashSet;


public class RegisterInfo {
    private static ArrayList<PhysicalRegister> registers = new ArrayList<>(){{
        add(new PhysicalRegister("zero", 0));
        add(new PhysicalRegister("at", 1));
        add(new PhysicalRegister("v0", 2));
        add(new PhysicalRegister("v1", 3));
        add(new PhysicalRegister("a0", 4));
        add(new PhysicalRegister("a1", 5));
        add(new PhysicalRegister("a2", 6));
        add(new PhysicalRegister("a3", 7));
        add(new PhysicalRegister("t0", 8));
        add(new PhysicalRegister("t1", 9));
        add(new PhysicalRegister("t2", 10));
        add(new PhysicalRegister("t3", 11));
        add(new PhysicalRegister("t4", 12));
        add(new PhysicalRegister("t5", 13));
        add(new PhysicalRegister("t6", 14));
        add(new PhysicalRegister("t7", 15));
        add(new PhysicalRegister("s0", 16));
        add(new PhysicalRegister("s1", 17));
        add(new PhysicalRegister("s2", 18));
        add(new PhysicalRegister("s3", 19));
        add(new PhysicalRegister("s4", 20));
        add(new PhysicalRegister("s5", 21));
        add(new PhysicalRegister("s6", 22));
        add(new PhysicalRegister("s7", 23));
        add(new PhysicalRegister("t8", 24));
        add(new PhysicalRegister("t9", 25));
        add(new PhysicalRegister("k0", 26));
        add(new PhysicalRegister("k1", 27));
        add(new PhysicalRegister("gp", 28));
        add(new PhysicalRegister("sp", 29));
        add(new PhysicalRegister("fp", 30));
        add(new PhysicalRegister("ra", 31));
    }};

    public static ArrayList<PhysicalRegister> getRegisters() {
        return registers;
    }

    private static HashSet<PhysicalRegister> needeeSaveRegister = new HashSet<>(){{
        for (int i = 0; i < 32; i++) {
            if (i == 3 || (i >= 8 && i <= 28) || i == 30 || i == 31)
                add(registers.get(i));
        }
    }};
    private static HashSet<PhysicalRegister> canAllocRegister = new HashSet<>(){{
        for (int i = 0; i < 32; i++) {
            if (i != 0 && i != 1 && i != 29)
                add(registers.get(i));
        }
    }};

    public static HashSet<PhysicalRegister> getNeedeeSaveRegister() {
        return needeeSaveRegister;
    }

    public static HashSet<PhysicalRegister> getCanAllocRegister() {
        return canAllocRegister;
    }
    public static boolean needSave(int index){
        return needeeSaveRegister.contains(registers.get(index));
    }
    public static HashSet<Integer> getCanIndexAllocRegister() {
        HashSet<Integer> ret = new HashSet<>();
        canAllocRegister.forEach(r->ret.add(r.getRegisterNum()));
        return ret;
    }

    public static PhysicalRegister getPhysicalRegisterById(int index) {
        return registers.get(index);
    }
    public static int getIndexByRegisterName(String name) {
        return registers.indexOf(new PhysicalRegister(name, 0));
    }
    public static boolean isneedeeSave(int index) {
        return needeeSaveRegister.contains(index);
    }
    public static boolean canAlloc(int index) {
        return canAllocRegister.contains(index);
    }
    public static boolean isneedeeSave(PhysicalRegister physicalRegister) {
        return needeeSaveRegister.contains(physicalRegister.getRegisterNum());
    }
    public static boolean canAlloc(PhysicalRegister physicalRegister) {
        return canAllocRegister.contains(physicalRegister.getRegisterNum());
    }
    public static boolean isneedeeSave(String physicalRegister) {
        return needeeSaveRegister.contains(getIndexByRegisterName(physicalRegister));
    }
    public static boolean canAlloc(String physicalRegister) {
        return canAllocRegister.contains(getIndexByRegisterName(physicalRegister));
    }
}
