package back;

import middle.Module;
import middle.base.BasicBlock;
import middle.base.Op;
import middle.base.Value;
import middle.base.instructions.*;
import middle.base.values.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenCode {
    Module module;
    public List<MipsInstruction> mipsCode = new ArrayList<>();
    int max_func_param;
    MemMan memMan = new MemMan();
    Function thisFunction;


    public GenCode(Module module) {
        this.module = module;
        findMaxParam();
    }

    void findMaxParam() {
        int maxNum = 0;
        for (Function function :
                module.functions)
            if (function.funcParams.size() > maxNum)
                maxNum = function.funcParams.size();
        max_func_param = maxNum > 4 ? maxNum - 4 : 0;
    }

    void print() {
        for (MipsInstruction mipsInstruction :
                mipsCode) {
            mipsInstruction.print();
        }
    }

    MipsInstruction addMips(String label) {
        MipsInstruction mipsInstruction = new MipsInstruction(label);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }

    MipsInstruction addMips(MipsInstruction mipsInstruction) {
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }


    MipsInstruction addMips(String opStr, String reg1) {
        MipsInstruction mipsInstruction = new MipsInstruction(opStr, reg1);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }

    private MipsInstruction addMips(String opStr, String reg1, String reg2) {
        MipsInstruction mipsInstruction = new MipsInstruction(opStr, reg1, reg2);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }


    MipsInstruction addMips(String opStr, String reg1, String reg2, String reg3) {
        MipsInstruction mipsInstruction = new MipsInstruction(opStr, reg1, reg2, reg3);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }

    MipsInstruction addMips(String opStr, String reg1, String reg2, int offset) {
        MipsInstruction mipsInstruction = new MipsInstruction(opStr, reg1, reg2, offset);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }

    MipsInstruction addSWStack(String reg1, int offset) {
        MipsInstruction mipsInstruction = new MipsInstruction("sw", reg1, "$sp", offset);
        this.mipsCode.add(mipsInstruction);
        return mipsInstruction;
    }

    public void genMips() {
        genModule();
        print();
    }

    private void genModule() {
        addMips(".data");
        genGlobal();
        addMips(".text");

        genFunctions();
        genDeclare();

    }

    private void genFunctions() {
        Collections.reverse(module.functions);
        for (Function function : module.functions) {
            thisFunction = function;
            genFunc(function);
        }

    }

    private void genFunc(Function function) {
        addMips(function.name + ":");
        int maxStack = calFuncMem(function);

        //多出来的函数参数保存到栈
        for (int i = 0; i < max_func_param; i++) {
            memMan.getVirtualInStack("funcParam" + (4 - i));
        }

        //把8~18寄存器保存到栈
        for (int i = 8; i < 18; i++) {
            memMan.getVirtualInStack(MemMan.registerList[i]);
        }


        {//栈指针保存到栈
            addMips("addu", "$fp", "$sp", "$zero");
            addMips("addiu", "$sp", "$sp", "-" + maxStack);
            StackEle ele = memMan.getVirtualInStack("$sp");
            addSWStack("$fp", ele.getPos());
            ele = memMan.getVirtualInStack("$ra");
            addSWStack("$ra", ele.getPos());
        }
        genFuncDefineParams(function.funcParams);
        boolean start = true;
        for (BasicBlock basicBlock : function.basicBlocks) {
            genBlock(basicBlock, start);
            start = false;
        }
        memMan.clear();


    }

    private void genFuncDefineParams(List<FuncParam> funcParams) {

        // 4号寄存器是a0
        for (int i = 0; i < funcParams.size() && i < 4; i++)
            memMan.temRegUseMap[i + 4] = funcParams.get(i).getName();
        for (int i = 4; i < funcParams.size(); i++)
            memMan.getVirtualInStack(funcParams.get(i).getName());

    }

    private void genBlock(BasicBlock basicBlock, boolean start) {
        if (!start) addMips(genLabel(basicBlock) + ":");
        for (Instruction instruction : basicBlock.instructions) {
            genInstruction(instruction);
        }
    }

    private void genInstruction(Instruction instruction) {
        //注释
        MipsInstruction comment = new MipsInstruction();
        comment.setComment(instruction.toString());
        addMips(comment);

        if (instruction instanceof Alloca alloca) genAlloc(alloca);
        else if (instruction instanceof Load load) genLoad(load);
        else if (instruction instanceof Store store) genStore(store);
        else if (instruction instanceof Add add) genAdd(add);
        else if (instruction instanceof Sub sub) genSub(sub);
        else if (instruction instanceof Mul mul) genMul(mul);
        else if (instruction instanceof Sdiv sdiv) genSdiv(sdiv);
        else if (instruction instanceof Srem srem) genSrem(srem);
        else if (instruction instanceof Ret ret) genRet(ret);
        else if (instruction instanceof Call call) genCall(call);
        else if (instruction instanceof Icmp icmp) genIcmp(icmp);
        else if (instruction instanceof Br br) genBr(br);
        else if (instruction instanceof Getelementptr getelementptr) genGetElementPtr(getelementptr);
        else if (instruction instanceof Zext zext) genZext(zext);

    }

    private void genSrem(Srem srem) {

        String Operator = "rem";

        // 做二元算式
        Register tempReg1 = lookup(srem.value1);
        Register tempReg2 = lookup(srem.value2);
        Register resReg = memMan.getTempReg(srem.result.getName());


        addMips("div", tempReg1.toString(), tempReg2.toString());
        addMips("mfhi", resReg.toString());

        // 把东西存到栈里
        StackEle stack = memMan.getVirtualInStack(srem.result.getName());
        addSWStack(resReg.toString(), stack.getPos());
        // 释放寄存器
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
        memMan.freeTempReg(resReg);
    }

    private void genMul(Mul mul) {
        String Operator = "mul";

        // 做二元算式
        Register tempReg1 = lookup(mul.value1);
        Register tempReg2 = lookup(mul.value2);
        Register resReg = memMan.getTempReg(mul.result.getName());

        addMips(Operator, resReg.toString(), tempReg1.toString(), tempReg2.toString());

        // 把东西存到栈里
        StackEle stack = memMan.getVirtualInStack(mul.result.getName());
        addSWStack(resReg.toString(), stack.getPos());
        // 释放寄存器
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
        memMan.freeTempReg(resReg);
    }
//    private void constDiv(Value result,Value ,Const){
//
//    }
    private void genSdiv(Sdiv sdiv) {

        String Operator = "div";

        // 做二元算式
        Register tempReg1 = lookup(sdiv.value1);
        Register tempReg2 = lookup(sdiv.value2);
        Register resReg = memMan.getTempReg(sdiv.result.getName());

        addMips(Operator, tempReg1.toString(), tempReg2.toString());
        addMips("mflo", resReg.toString());

        // 把东西存到栈里
        StackEle stack = memMan.getVirtualInStack(sdiv.result.getName());
        addSWStack(resReg.toString(), stack.getPos());
        // 释放寄存器
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
        memMan.freeTempReg(resReg);
    }

    private void genSub(Sub sub) {

        String Operator = "sub";

        // 做二元算式
        Register tempReg1 = lookup(sub.value1);
        Register tempReg2 = lookup(sub.value2);
        Register resReg = memMan.getTempReg(sub.result.getName());

        addMips(Operator, resReg.toString(), tempReg1.toString(), tempReg2.toString());

        // 把东西存到栈里
        StackEle stack = memMan.getVirtualInStack(sub.result.getName());
        addSWStack(resReg.toString(), stack.getPos());
        // 释放寄存器
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
        memMan.freeTempReg(resReg);
    }

    private void genZext(Zext zext) {
        Register tempReg = memMan.lookUpTemp(zext.value.getName());
        memMan.temRegUseMap[tempReg.getNum()] = zext.result.getName();
    }

    private void genGetElementPtr(Getelementptr getelementptr) {
        if (getelementptr.array.isPtr) {
            Register arrOffset = lookup(getelementptr.index);
            Register array = lookup(getelementptr.array);

            addMips("mul", arrOffset.toString(), arrOffset.toString(), "4");
            addMips("addu", array.toString(), array.toString(), arrOffset.toString());

            StackEle curOffsetStack = memMan.getVirtualInStack(getelementptr.result.getName());
            addSWStack(array.toString(), curOffsetStack.getPos());

            memMan.freeTempReg(array);
            memMan.freeTempReg(arrOffset);

        } else {
            Register arrOffset = lookup(getelementptr.index);
            addMips("mul", arrOffset.toString(), arrOffset.toString(), "4");

            StackEle array = memMan.lookUpStack(getelementptr.array.getName());
            Register tempReg = memMan.getTempReg(getelementptr.result.getName());
            StackEle curOffsetStack = memMan.getVirtualInStack(getelementptr.result.getName());
            if (array == null) {
                if (memMan.isGlobal(getelementptr.array.getName())) {

                    addMips("la", tempReg.toString(), getelementptr.array.name);
                    addMips("addu", tempReg.toString(), tempReg.toString(), arrOffset.toString());
                }
            }else {
                addMips("addu", tempReg.toString(), "$sp", array.toString());
                addMips("addu", tempReg.toString(), tempReg.toString(), arrOffset.toString());

            }

            addSWStack(tempReg.toString(), curOffsetStack.getPos());
            memMan.freeTempReg(arrOffset);
            memMan.freeTempReg(tempReg);
        }
    }

    private void genBr(Br br) {
        if (br.outBlock == null) {
            Register tempReg = memMan.lookUpTemp(br.value1.getName());
            String label_true = genLabel(br.trueBlock);
            String label_false = genLabel(br.falseBlock);
            mipsCode.add(new MipsInstruction("bne", tempReg.toString(), "$zero", label_true));
            mipsCode.add(new MipsInstruction("j", label_false));
            memMan.freeTempReg(tempReg);
        } else {
            String label_true = genLabel(br.outBlock);
            mipsCode.add(new MipsInstruction("j", label_true));
        }
    }

    // mips里的比较分支是一条语句, 我们需要把ir的两条语句合并成一条
    // ir里的这个比较和分支是连在一起的, 所以我们可以设个全局变量, 一起处理。
    private void genIcmp(Icmp icmp) {
        Register tempReg1 = lookup(icmp.value1);
        Register tempReg2 = lookup(icmp.value2);
        Register tempRegRes = memMan.getTempReg(icmp.result.getName());
        String Operator = "";
        if (icmp.cond.op == Op.eq) Operator = "seq";
        else if (icmp.cond.op == Op.ne) Operator = "sne";
        else if (icmp.cond.op == Op.sge) Operator = "sge";
        else if (icmp.cond.op == Op.sgt) Operator = "sgt";
        else if (icmp.cond.op == Op.sle) Operator = "sle";
        else if (icmp.cond.op == Op.slt) Operator = "slt";
        mipsCode.add(new MipsInstruction(Operator, tempRegRes.toString(), tempReg1.toString(), tempReg2.toString()));
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
    }

    private void genCall(Call call) {
        // 首先把函数的参数加载到对应的寄存器中
        int size = call.params.size();
        loadParam(call.params);
        // 释放有关寄存器
        for (int i = 0; i < size; i++) {
            Value param = call.params.get(i);
            Register realRegister = memMan.lookUpTemp(param.getName());
            if (realRegister != null) memMan.freeTempReg(realRegister);
        }
        // 保存现场
        for (int i = 8; i < 18; i++) {
            if (!memMan.temRegUseMap[i].equals(memMan._none)) {
                StackEle stack = memMan.lookUpStack(memMan.tempRegPool.get(i).toString());
                Register tempReg = memMan.tempRegPool.get(i);
                addSWStack(tempReg.toString(), stack.getPos());
                memMan.record(tempReg, stack, memMan.temRegUseMap[i]);
                memMan.freeTempReg(tempReg);
            }
        }
        // 跳转到函数名
        String funcName = call.function.name;
        addMips("jal", funcName);
        // 恢复现场, 这里不需要再次申请寄存器
        for (Record record : memMan.recordList) {

            addMips("lw", record.getTempReg().toString(), "$sp", record.getStack().getPos());
            memMan.temRegUseMap[(record.getTempReg()).getNum()] = record.getName();
        }
        memMan.recordClear();
        // 如果有返回值，把$v0的赋值到另外一个寄存器里
        if (call.result != null) {
            Register tempReg = this.memMan.getTempReg(call.result.getName());
            addMips("addu", tempReg.toString(), "$zero", "$v0");
            //把东西存到栈里
            StackEle stack = memMan.getVirtualInStack(call.result.getName());
            addMips("sw", tempReg.toString(), "$sp", stack.getPos());
            //释放寄存器
            memMan.freeTempReg(tempReg);
        }
    }

    private void loadParam(List<Value> params) {
        for (int i = 0; i < params.size(); i++) {
            Value param = params.get(i);
            if (i < 4) {
                if (param instanceof Const c) {
                    mipsCode.add(new MipsInstruction("li", "$a" + i, Integer.toString(c.value)));
                } else {
                    // 如果是栈上的值，就先把栈上的值加载到寄存器里
                    StackEle stack = memMan.lookUpStack(param.getName());
                    String global = memMan.lookupGlobal(param.getName());
                    if (stack != null) {
                        // 如果参数的类型是i32但是虚拟寄存器是数组
                        if (memMan.isArrayVirtualReg(param.getName())) {
                            if (param.isPtr) {    //那就先把数组的地址load出来,然后再把值load到ax寄存器里

                                addMips("lw", "$a" + i, "$sp", stack.getPos());

                            } else {
                                Register arrayAddr = memMan.getTempReg(param.getName());
                                addMips("lw", arrayAddr.toString(), "$sp", stack.getPos());
                                addMips("lw", "$a" + i, arrayAddr.toString(), 0);
                                memMan.freeTempReg(arrayAddr);
                            }
                        } else addMips("lw", "$a" + i, "$sp", stack.getPos());
                    } else if (global != null) {
                        addMips("lw", "$a" + i, global);
                    } else throw new RuntimeException();
                }

            } else {
                if (param instanceof Const c) {
                    Register tempReg = memMan.getTempReg(memMan.getTempNum());
                    addMips("li", tempReg.toString(), Integer.toString(c.value));
                    StackEle stack = memMan.lookUpStack("param" + i);
                    addSWStack(tempReg.toString(), stack.getPos());
                    memMan.freeTempReg(tempReg);
                } else {
                    // 如果是栈上的值，就先把栈上的值加载到寄存器里
                    StackEle stack = memMan.lookUpStack(param.getName());
                    String global = memMan.lookupGlobal(param.getName());
                    if (stack != null) {
                        // 如果参数的类型是i32但是虚拟寄存器是数组
                        if (memMan.isArrayVirtualReg(param.getName()) && param.isPtr) {
                            //那就先把数组的地址load出来
                            Register arrayAddr = memMan.getTempReg(param.getName());
                            addMips("lw", arrayAddr.toString(), "$sp", stack.getPos());
                            addMips("lw", "$a" + i, arrayAddr.toString(), 0);
                            StackEle stackParam = memMan.lookUpStack("param" + i);
                            addSWStack(arrayAddr.toString(), stackParam.getPos());
                            memMan.freeTempReg(arrayAddr);
                        } else {
                            Register tempReg = memMan.getTempReg(memMan.getTempNum());

                            addMips("lw", tempReg.toString(), "$sp", stack.getPos());
                            StackEle stackParam = memMan.lookUpStack("param" + i);
                            addSWStack(tempReg.toString(), stackParam.getPos());
                            memMan.freeTempReg(tempReg);
                        }
                    } else if (global != null) {
                        Register tempReg = memMan.getTempReg(memMan.getTempNum());
                        addMips("lw", tempReg.toString(), global);
                        StackEle stackParam = memMan.lookUpStack("param" + i);
                        addSWStack(tempReg.toString(), stackParam.getPos());
                        memMan.freeTempReg(tempReg);
                    } else throw new RuntimeException();
                }
            }

        }

    }

    private void genRet(Ret ret) {
        // 清空栈，然后还原栈指针
        if (thisFunction.name.equals("main")) {
            addMips("addiu", "$v0", "$zero", "10");
            addMips("syscall", "");
        } else {

            // 把返回值赋值到v0中
            Value tempRet = ret.value;
            if (tempRet != null) {
                if (tempRet instanceof Const con) addMips("li", "$v0", con.name);
                else {
                    Register reg = lookup(ret.value);
                    addMips("addu", "$v0", "$zero", reg.toString());
                    memMan.freeTempReg(reg);
                }
            }


            // 把ra和fp还原
            StackEle stack = memMan.lookUpStack("$ra");
            addMips("lw", "$ra", "$sp", stack.getPos());
            // 把$sp还原
            stack = memMan.lookUpStack("$sp");
            addMips("lw", "$sp", "$sp", stack.getPos());


            // 返回栈指针
            addMips("jr", "$ra");

        }
    }

    private void genAdd(Add add) {
        String Operator = "addu";

        // 做二元算式
        Register tempReg1 = lookup(add.value1);
        Register tempReg2 = lookup(add.value2);
        Register resReg = memMan.getTempReg(add.result.getName());

        addMips(Operator, resReg.toString(), tempReg1.toString(), tempReg2.toString());

        // 把东西存到栈里
        StackEle stack = memMan.getVirtualInStack(add.result.getName());
        addSWStack(resReg.toString(), stack.getPos());
        // 释放寄存器
        memMan.freeTempReg(tempReg1);
        memMan.freeTempReg(tempReg2);
        memMan.freeTempReg(resReg);
    }

    private Register lookup(Value value) {
        Register tempReg;
        if (value instanceof Const c) {
            int constNum = c.value;
            tempReg = memMan.getTempReg(memMan.getTempNum());
            addMips("addiu", tempReg.toString(), "$zero", Integer.toString(constNum));
        } else {
            tempReg = memMan.lookUpTemp(value.getName());
            if (tempReg == null) {
                StackEle stackReg = memMan.lookUpStack(value.getName());
                tempReg = memMan.getTempReg(value.getName());
                addMips("lw", tempReg.toString(), "$sp", stackReg.getPos());
            }
        }
        return tempReg;
    }

    private void genStore(Store store) {
        Register tempReg = lookup(store.value2);
        StackEle stackReg = memMan.lookUpStack(store.value1.getName());
        if (stackReg != null) {
            // 如果是数组就需要先load出地址
            if (memMan.isArrayVirtualReg(store.value1.getName())) {
                // 申请临时寄存器把数组地址load出来
                Register arrayAddr = memMan.getTempReg(memMan.getTempNum());
                addMips("lw", arrayAddr.toString(), "$sp", stackReg.getPos());
                addMips("sw", tempReg.toString(), arrayAddr.toString(), 0);
                // 释放零时寄存器
                memMan.freeTempReg(arrayAddr);
            } else addMips("sw", tempReg.toString(), "$sp", stackReg.getPos());
        } else if (memMan.isGlobal(store.value1.getName())) {
            mipsCode.add(new MipsInstruction("sw", tempReg.toString(), store.value1.name));
        }
        memMan.freeTempReg(tempReg);
    }

    private void genLoad(Load load) {

        if (memMan.isParam(load.result.getName())) {
            // 如果是参数，就直接做个对应，不做翻译工作，但是需要注意的是数组对应的是地址的地址
            StackEle ele = memMan.lookUpStack(load.pointer.getName());
            memMan.virtual2StackEle.put(load.result.getName(), ele);
            // 如果是数组也把他加进数组的hashset里
            if (memMan.isArrayVirtualReg(load.pointer.getName())) {
                memMan.addToArrayVirtualReg(load.result.getName());
            }
            // 如果是全局变量则添加字典
            if (memMan.isGlobal(load.pointer.getName()))
                memMan.addToGlobal(load.result.getName(), load.pointer.getName());
        } else {

            // 对于不同的类别需要不同的寄存器
            Register resultReg = memMan.getTempReg(load.result.getName());
            StackEle stackReg = memMan.lookUpStack(load.pointer.getName());
            // 这是一般的情况, 从栈上加载数据到寄存器里
            if (stackReg != null)
                // 如果是数组就需要先load出地址
                if (memMan.isArrayVirtualReg(load.pointer.getName())) {
                    // 申请临时寄存器把数组地址load出来
                    Register arrayAddr = memMan.getTempReg(memMan.getTempNum());
                    addMips("lw", arrayAddr.toString(), "$sp", stackReg.getPos());
                    addMips("lw", resultReg.toString(), arrayAddr.toString(), 0);

                    // 释放零时寄存器
                    memMan.freeTempReg(arrayAddr);
                } else
                    addMips("lw", resultReg.toString(), "$sp", stackReg.getPos());

            else if (memMan.isGlobal(load.pointer.getName())) {
                // 如果还是找不到, 寻找全局变量
                addMips("lw", resultReg.toString(), load.pointer.name);
            }
//            memMan.freeTempReg(resultReg);
        }

    }


    private void genAlloc(Alloca alloca) {
        String vName = alloca.result.getName();
        if (!alloca.result.isPtr && alloca.result.dim > 0) {
            int size = alloca.result.size;
            memMan.getVirtualInStack(vName, size);
        } else memMan.getVirtualInStack(vName);
    }

    private int calFuncMem(Function function) {
        int memSize = (2 + 10 + max_func_param) * 4;
        for (BasicBlock basicBlock : function.basicBlocks) {
            for (Instruction instruction : basicBlock.instructions) {
                if (instruction instanceof Alloca alloc) {
                    if (!alloc.result.isPtr && alloc.result.dim > 0)
                        memSize += alloc.result.size * 4;
                    else memSize += 4;
                }
                if (instruction instanceof Add ||
                        instruction instanceof Sub ||
                        instruction instanceof Sdiv ||
                        instruction instanceof Mul ||
                        instruction instanceof Srem
                ) memSize += 4;


                if (instruction instanceof Getelementptr getElementPtr) {
                    // 每一个数组加载出来的地址需要存到相应的地方
                    memSize += 4;
                    // 如果是数组加载出来的结果就需要添加到数组的集合中
                    memMan.addToArrayVirtualReg(getElementPtr.result.getName());
                }
                if (instruction instanceof Call callInstruction) {
                    // 如果是callInstruction有返回值也需要加内存
                    if (callInstruction.result != null) memSize += 4;
                    // 加载参数 ,对于函数加载参数之前的load语句不释放寄存器的情况需要解决
                    int size = callInstruction.params.size();
                    // 如果是参数，加载出来的结果就需要放到参数的集合中
                    List<Value> params = callInstruction.params;
                    for (int i = 0; i < size; i++) {
                        memMan.addToParam(params.get(i).getName());
                    }
                }

            }
        }
        return memSize;
    }

    private void genDeclare() {
        addMips("putch:");
        addMips("addiu", "$v0", "$zero", "11");
        addMips("syscall", "");
        addMips("jr", "$ra");

        addMips("putint:");
        addMips("addiu", "$v0", "$zero", "1");
        addMips("syscall", "");
        addMips("jr", "$ra");

        addMips("getint:");
        addMips("addiu", "$v0", "$zero", "5");
        addMips("syscall", "");
        addMips("jr", "$ra");

    }

    private void genGlobal() {
        for (Var var : module.globalVar) {
            if (var.initValues.isEmpty()) {
                if (var.dim == 0)
                    addMips(var.name + ":", ".word 0");
                else addMips(var.name + ":", ".space " + var.size * 4);
            } else {
                if (var.dim == 0)
                    addMips(var.name + ":", ".word " + var.initValues.get(0));
                else {
                    String data = "";
                    boolean flag = true;
                    for (int i = 0; i <var.resetInit.size() ; i++) {
                        if (flag) flag = false;
                        else data += ",";
                        data += var.resetInit.get(i);
                    }
                    addMips(var.name + ":", ".word " + data);
                }
            }
            memMan.globalSet.add(var.getName());
        }

    }

    String genLabel(BasicBlock basicBlock) {
        return thisFunction.name + "_label_" + basicBlock.name;
    }


}
