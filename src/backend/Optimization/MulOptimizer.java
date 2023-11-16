package backend.Optimization;

import Config.Config;
import backend.MIPSInfo.RegisterInfo;
import backend.MIPSIntstruction.EInsrtruction;
import backend.MIPSIntstruction.IInstruction;
import backend.MIPSIntstruction.RInstruction;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSOperand.MIPSImmediate;
import backend.MIPSOperand.MIPSLabel;
import backend.MIPSOperand.MIPSRegister;
import backend.MIPSOperand.VirtualRegister;

import java.util.ArrayList;
import java.util.HashMap;

public class MulOptimizer {
    private MulOptimizer() {
    }

    private static MulOptimizer mulOptimizer = new MulOptimizer();
    private static HashMap<Integer, Mul> mulMap = new HashMap<>();

    private static class Mul {
        private int num;
        private ArrayList<String[]> offList = new ArrayList<>();

        public Mul(int... offs) {
            for (int off : offs) {
                String[] tmp = new String[2];
                if (off >= 0) {
                    num += (1 << off);
                    tmp[0] = "+";
                    tmp[1] = Integer.toString(off);
                    offList.add(0, tmp);
                } else {
                    num -= (1 << (-off));
                    tmp[0] = "-";
                    tmp[1] = Integer.toString(-off);
                    offList.add(tmp);
                }
            }
        }

        public int getScore() {
            int score = 0;
            boolean flag = false;
            for (String[] off : offList) {
                if (Integer.parseInt(off[1]) != 0)
                    score++;
                if (off[0].equals("+")) {
                    flag = true;
                }
            }
            if (flag) {
                score += offList.size() - 1;
            } else {
                score += offList.size();
            }
            return score;
        }

        public int getNum() {
            return num;
        }
    }

    static {
        ArrayList<Mul> tmp = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            tmp.add(new Mul(i));
            tmp.add(new Mul(-i));
            for (int j = 0; j < 32; j++) {
                tmp.add(new Mul(i, j));
                tmp.add(new Mul(-i, j));
                tmp.add(new Mul(i, -j));
                tmp.add(new Mul(-i, -j));
                for (int k = 0; k < 32; k++) {
                    tmp.add(new Mul(i, j, k));
                    tmp.add(new Mul(-i, j, k));
                    tmp.add(new Mul(i, -j, k));
                    tmp.add(new Mul(-i, -j, k));
                    tmp.add(new Mul(i, j, -k));
                    tmp.add(new Mul(-i, j, -k));
                    tmp.add(new Mul(i, -j, -k));
                    tmp.add(new Mul(-i, -j, -k));
                }
            }
        }
        for (Mul mul : tmp) {
            int score = mul.getScore();
            if (score < 5) {
                if (mulMap.containsKey(mul.getNum())) {
                    if (mulMap.get(mul.getNum()).getScore() > score) {
                        mulMap.replace(mul.getNum(), mul);
                    }
                } else mulMap.put(mul.getNum(), mul);
            }
        }
    }

    public static MulOptimizer getInstance() {
        return mulOptimizer;
    }

    public ArrayList<String[]> mulOptimize(int mulConst) {
        if (mulMap.containsKey(mulConst))
            return mulMap.get(mulConst).offList;
        else
            return new ArrayList<>();
    }

    public static void mulOptimization(MIPSBBlock mipsbBlock, MIPSRegister rd, MIPSRegister rs, MIPSImmediate imm) {
        if (!Config.mulOptimization) {
            mipsbBlock.addMIPSInstruction(new IInstruction("mul  ", rs, rd, imm));
            return;
        }
        ArrayList<String[]> muloffs = MulOptimizer.getInstance().mulOptimize(imm.getImmediate());
        MIPSRegister tmpPReg = RegisterInfo.getPhysicalRegisterById(1);
        if (muloffs.size() == 0)
            mipsbBlock.addMIPSInstruction(new IInstruction("mul  ", rs, rd, imm));
        else if (muloffs.size() == 1) {
            if (muloffs.get(0)[1].equals("0"))
                mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", rd, rs));
            else
                mipsbBlock.addMIPSInstruction(new IInstruction("sll  ", rs, rd, new MIPSImmediate(Integer.parseInt(muloffs.get(0)[1]))));
            if (muloffs.get(0)[0].equals("-")) {
                mipsbBlock.addMIPSInstruction(new RInstruction("subu ", RegisterInfo.getPhysicalRegisterById(0), rd, rd));
            }
        } else {
            if (muloffs.get(0)[1].equals("0"))
                mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", tmpPReg, rs));
            else
                mipsbBlock.addMIPSInstruction(new IInstruction("sll  ", rs, tmpPReg, new MIPSImmediate(Integer.parseInt(muloffs.get(0)[1]))));
            if (muloffs.get(0)[0].equals("-")) {
                mipsbBlock.addMIPSInstruction(new RInstruction("subu ", RegisterInfo.getPhysicalRegisterById(0), tmpPReg, tmpPReg));
            }
            for (int i = 1; i < muloffs.size() - 1; i++) {
                MIPSRegister tmpVReg = null;
                if (!muloffs.get(i)[1].equals("0")) {
                    tmpVReg = new VirtualRegister();
                    mipsbBlock.addMIPSInstruction(new IInstruction("sll  ", rs, tmpVReg, new MIPSImmediate(Integer.parseInt(muloffs.get(i)[1]))));
                }
                if (tmpVReg == null) {
                    if (muloffs.get(i)[0].equals("-")) {
                        mipsbBlock.addMIPSInstruction(new RInstruction("subu ", tmpPReg, rs, tmpPReg));
                    } else {
                        mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmpPReg, rs, tmpPReg));
                    }
                } else {
                    if (muloffs.get(i)[0].equals("-")) {
                        mipsbBlock.addMIPSInstruction(new RInstruction("subu ", tmpPReg, tmpVReg, tmpPReg));
                    } else {
                        mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmpPReg, tmpVReg, tmpPReg));
                    }
                }
            }
            String[] last = muloffs.get(muloffs.size() - 1);
            if (last[1].equals("0")) {
                if (last[0].equals("-")) {
                    mipsbBlock.addMIPSInstruction(new RInstruction("subu ", tmpPReg, rs, rd));
                } else {
                    mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmpPReg, rs, rd));
                }
            } else {
                MIPSRegister tmpVReg = new VirtualRegister();
                mipsbBlock.addMIPSInstruction(new IInstruction("sll  ", rs, tmpVReg, new MIPSImmediate(Integer.parseInt(last[1]))));
                if (last[0].equals("-")) {
                    mipsbBlock.addMIPSInstruction(new RInstruction("subu ", tmpPReg, tmpVReg, rd));
                } else {
                    mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmpPReg, tmpVReg, rd));
                }
            }
        }
    }

    public static long[] chooseMultiplier(int d, int prec) {
        long[] ret = new long[2];
        long nc = (1L << prec) - ((1L << prec) % d) - 1;
        long p = 32;
        while ((1L << p) <= nc * (d - (1L << p) % d)) {
            p++;
        }
        long m = (((1L << p) + (long) d - (1L << p) % d) / (long) d);
        long n = ((m << 32) >>> 32);
        ret[0] = n;
        ret[1] = p - 32;
        return ret;
    }

    // n * m >> shift
    public static void divOptimization(MIPSBBlock mipsbBlock, MIPSRegister rd, MIPSRegister rs, MIPSImmediate imm) {
        if (!Config.divOptimization) {
            mipsbBlock.addMIPSInstruction(new IInstruction("div  ", rs, rd, imm));
            return;
        }
        int d = imm.getImmediate();
        if (d == 1) {
            mipsbBlock.addMIPSInstruction(new EInsrtruction("move ", rd, rs));
        } else if (d == -1) {
            mipsbBlock.addMIPSInstruction(new RInstruction("subu ", RegisterInfo.getPhysicalRegisterById(0), rs, rd));
        } else {
            d = d > 0 ? d : -d;
            if ((d & (d - 1)) == 0) {   // d = 2 ^ l
                int l = Integer.numberOfTrailingZeros(d);
                MIPSRegister tmp = new VirtualRegister(), tmp1 = new VirtualRegister();
                mipsbBlock.addMIPSInstruction(new IInstruction("sra  ", rs, tmp, new MIPSImmediate(31)));
                mipsbBlock.addMIPSInstruction(new IInstruction("srl  ", tmp, tmp, new MIPSImmediate(32 - l)));
                mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmp, rs, tmp1));
                mipsbBlock.addMIPSInstruction(new IInstruction("sra  ", tmp1, rd, new MIPSImmediate(l)));
            } else {    // n / d = n * m >> shift
                long[] mandshift = chooseMultiplier(d, 31);
                long m = mandshift[0];
                int shift = (int) mandshift[1];
                MIPSRegister tmp = new VirtualRegister(), tmp2 = new VirtualRegister();
                if (m < (1L << 31)) {
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", tmp, new MIPSImmediate((int) m)));
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("mult ", tmp, rs));
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("mfhi ", tmp2));
                } else {
//                    Uint32 t = muluh(n, m - (Uint64(1) << 31);
//                    return (((n - t) >> 1) + t) >> (l - 1); 无符号整数
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", tmp, new MIPSImmediate((int) (m - (1L << 32)))));
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("mult ", tmp, rs));
                    mipsbBlock.addMIPSInstruction(new EInsrtruction("mfhi ", tmp2));
                    mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmp2, rs, tmp2));
                }
                mipsbBlock.addMIPSInstruction(new IInstruction("sra  ", tmp2, tmp2, new MIPSImmediate(shift)));
                mipsbBlock.addMIPSInstruction(new IInstruction("srl  ", rs, tmp, new MIPSImmediate(31)));
                mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmp2, tmp, rd));
//                mipsbBlock.addMIPSInstruction(new EInsrtruction("li   ", tmp, new MIPSImmediate((int) m)));
//                mipsbBlock.addMIPSInstruction(new EInsrtruction("mult ", tmp, rs));
//                mipsbBlock.addMIPSInstruction(new EInsrtruction("mfhi ", tmp));
//                mipsbBlock.addMIPSInstruction(new IInstruction("sra  ", tmp, tmp, new MIPSImmediate(shift)));
//                mipsbBlock.addMIPSInstruction(new IInstruction("srl  ", rs, tmp2, new MIPSImmediate(31)));
//                mipsbBlock.addMIPSInstruction(new RInstruction("addu ", tmp2, tmp, rd));
            }
        }
        if (imm.getImmediate() < 0) {
            mipsbBlock.addMIPSInstruction(new RInstruction("subu ", RegisterInfo.getPhysicalRegisterById(0), rd, rd));
        }
    }

    // x rem y = x - x / y * y
    public static void remOptimization(MIPSBBlock mipsbBlock, MIPSRegister rd, MIPSRegister rs, MIPSImmediate imm) {
        if (!Config.modOptimization) {
            mipsbBlock.addMIPSInstruction(new IInstruction("rem  ", rs, rd, imm));
            return;
        }
        divOptimization(mipsbBlock, rd, rs, imm);
        mulOptimization(mipsbBlock, rd, rd, imm);
        mipsbBlock.addMIPSInstruction(new RInstruction("subu ", rs, rd, rd));
    }
}
