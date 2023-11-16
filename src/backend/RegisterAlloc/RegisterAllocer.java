package backend.RegisterAlloc;

import backend.LivenessAnalyze.LivenessAnalyzer;
import backend.LivenessAnalyze.LivenessInfo;
import backend.MIPSInfo.RegisterInfo;
import backend.MIPSIntstruction.EInsrtruction;
import backend.MIPSIntstruction.MIPSInstruction;
import backend.MIPSIntstruction.RInstruction;
import backend.MIPSModule.MIPSBBlock;
import backend.MIPSModule.MIPSFunction;
import backend.MIPSModule.MIPSModule;
import backend.MIPSOperand.MIPSRegister;
import backend.MIPSOperand.PhysicalRegister;
import backend.MIPSOperand.VirtualRegister;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 这部分照着虎书实现的，慢慢再理解。。。。。
 */
public class RegisterAllocer {
    MIPSModule mipsModule;
    static int K = RegisterInfo.getCanAllocRegister().size();
    HashSet<MIPSRegister> precolored;
    HashSet<MIPSRegister> initial;
    HashSet<MIPSRegister> simplifyWorklist;
    HashSet<MIPSRegister> freezeWorklist;
    HashSet<MIPSRegister> spillWorklist;
    HashSet<MIPSRegister> spilledNodes;
    HashSet<MIPSRegister> coalescedNodes;
    HashSet<MIPSRegister> coloredNodes;
    Stack<MIPSRegister> selectStack;
    HashSet<MIPSInstruction> coalescedMoves;
    HashSet<MIPSInstruction> constrainedMoves;
    HashSet<MIPSInstruction> frozenMoves;
    HashSet<MIPSInstruction> worklistMoves;
    HashSet<MIPSInstruction> activeMoves;
    HashSet<Edge> adjSet;
    HashMap<MIPSRegister, HashSet<MIPSRegister>> adjList;
    HashMap<MIPSRegister, Integer> degree;
    HashMap<MIPSRegister, HashSet<MIPSInstruction>> moveList;
    HashMap<MIPSRegister, MIPSRegister> alias;
    HashMap<MIPSRegister, Integer> color;
    HashMap<MIPSBBlock, LivenessInfo> livenessMap;

    public void initDS(MIPSFunction mipsFunction) {
        adjList = new HashMap<>();
        adjSet = new HashSet<>();
        alias = new HashMap<>();
        moveList = new HashMap<>();
        simplifyWorklist = new HashSet<>();
        freezeWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        selectStack = new Stack<>();
        worklistMoves = new HashSet<>();
        activeMoves = new HashSet<>();
        coalescedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        degree = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            degree.put(RegisterInfo.getPhysicalRegisterById(i), Integer.MAX_VALUE);
            color.put(RegisterInfo.getPhysicalRegisterById(i), i);
        }
    }

    private void init(MIPSFunction mipsFunction) {
        precolored = new HashSet<>(RegisterInfo.getCanAllocRegister());
        initial = mipsFunction.getRegs();
        coloredNodes = new HashSet<>();
        color = new HashMap<>();
    }

    public RegisterAllocer(MIPSModule mipsModule) {
        this.mipsModule = mipsModule;
    }

    public void alloc() {
        for (MIPSFunction mipsFunction : mipsModule.getFunctions()) {
            init(mipsFunction);
            allocInFunc(mipsFunction);
            switchV2R(mipsFunction);
            storeReg(mipsFunction); // 用于记录现场，便于以后回复
            mipsFunction.refreshArgOff();
        }
    }

    private void storeReg(MIPSFunction mipsFunction) {
        if (mipsFunction.getName().equals("@main")) return;
        for (MIPSBBlock block : mipsFunction.getBlocks()) {
            for (MIPSInstruction instr : block.getInstructions()) {
                for (MIPSRegister reg : instr.getDef()) {
                    int rindex = ((PhysicalRegister) reg).getRegisterNum();
                    if (RegisterInfo.needSave(rindex)) {
                        mipsFunction.addToSavedRegisters(rindex);
                    }
                }
            }
        }
    }

    private void switchV2R(MIPSFunction func) {
        for (MIPSBBlock block : func.getBlocks()) {
            for (MIPSInstruction instr : block.getInstructions()) {
                ArrayList<MIPSRegister> defs = new ArrayList<>(instr.getDef());
                ArrayList<MIPSRegister> uses = new ArrayList<>(instr.getUse());

                for (MIPSRegister def : defs) {
                    if (color.containsKey(def) && !precolored.contains(def)) {
                        instr.replaceDef(def, RegisterInfo.getPhysicalRegisterById(color.get(def)));
                    }
                }
                for (MIPSRegister use : uses) {
                    if (color.containsKey(use) && !precolored.contains(use)) {
                        instr.replaceUse(use, RegisterInfo.getPhysicalRegisterById(color.get(use)));
                    }
                }
            }

        }
    }

    private void allocInFunc(MIPSFunction mipsFunction) {
        initDS(mipsFunction);
        livenessMap = LivenessAnalyzer.getInstance().analyze(mipsFunction);
        build(mipsFunction);
        makeWorklist();
        do {
            if (!simplifyWorklist.isEmpty()) Simplify();
            else if (!worklistMoves.isEmpty()) Coalesce();
            else if (!freezeWorklist.isEmpty()) Freeze();
            else if (!spillWorklist.isEmpty()) SelectSpill();
        } while (!simplifyWorklist.isEmpty() || !worklistMoves.isEmpty() || !freezeWorklist.isEmpty() || !spillWorklist.isEmpty());
        AssignColors();
        if (!spilledNodes.isEmpty()) {
            RewriteProgram(mipsFunction);
//            System.out.println(mipsFunction.toString());
            allocInFunc(mipsFunction);
        }
    }


    private void build(MIPSFunction mipsFunction) {
        for (MIPSBBlock b : mipsFunction.getBlocks()) {
            HashSet<MIPSRegister> live = new HashSet<>(livenessMap.get(b).getOut());
            for (int i = b.getInstructions().size() - 1; i >= 0; i--) {
                MIPSInstruction I = b.getInstructions().get(i);
                if (I instanceof EInsrtruction && I.getName().equals("move ")) {
                    MIPSRegister rs = ((EInsrtruction) I).getRs();
                    MIPSRegister rd = ((EInsrtruction) I).getRd();
                    live.remove(rs);
                    moveList.putIfAbsent(rs, new HashSet<>());
                    moveList.putIfAbsent(rd, new HashSet<>());
                    moveList.get(rs).add(I);
                    moveList.get(rd).add(I);
                    worklistMoves.add(I);
                }
                for (MIPSRegister d : I.getDef()) {
                    for (MIPSRegister l : live) {
                        AddEdge(l, d);
                    }
                }
                HashSet<MIPSRegister> def = I.getDef();
                HashSet<MIPSRegister> use = I.getUse();
                def.forEach(live::remove);
                live.addAll(use);
            }
        }
    }

    private void AddEdge(MIPSRegister u, MIPSRegister v) {
        Edge e = new Edge(u, v);
        if (!adjSet.contains(e) && !u.equals(v)) {
            adjSet.add(e);
            adjSet.add(e.reverse());
            if (!precolored.contains(u)) {
                adjList.putIfAbsent(u, new HashSet<>());
                adjList.get(u).add(v);
                if (degree.containsKey(u)) {
                    degree.compute(u, (key, value) -> value + 1);
                } else {
                    degree.put(u, 1);
                }
            }
            if (!precolored.contains(v)) {
                adjList.putIfAbsent(v, new HashSet<>());
                adjList.get(v).add(u);
                if (degree.containsKey(v)) {
                    degree.compute(v, (key, value) -> value + 1);
                } else {
                    degree.put(v, 1);
                }
            }
        }
    }

    private void makeWorklist() {
        HashSet<MIPSRegister> temp = new HashSet<>(initial);
        for (MIPSRegister n : temp) {
            initial.remove(n);
            if (degree.containsKey(n) && degree.get(n) >= K) {
                spillWorklist.add(n);
            } else if (MoveRelated(n)) {
                freezeWorklist.add(n);
            } else {
                simplifyWorklist.add(n);
            }
        }
    }

    private Set<MIPSRegister> Adjacent(MIPSRegister n) {
        if (adjList.containsKey(n))
            return adjList.get(n).stream()
                    .filter(v -> !(selectStack.contains(v) || coalescedNodes.contains(v)))
                    .collect(Collectors.toSet());
        else {
            return new HashSet<>();
        }
    }

    private Set<MIPSInstruction> NodeMoves(MIPSRegister n) {
        return moveList.getOrDefault(n, new HashSet<>()).stream().filter(v -> activeMoves.contains(v) || worklistMoves.contains(v)).collect(Collectors.toSet());
    }

    private boolean MoveRelated(MIPSRegister n) {
        return !NodeMoves(n).isEmpty();
    }

    private void Simplify() {
        MIPSRegister n = simplifyWorklist.stream().findFirst().get();
        simplifyWorklist.remove(n);
        selectStack.push(n);
        for (MIPSRegister m : Adjacent(n)) {
            DecrementDegree(m);
        }
    }

    private void DecrementDegree(MIPSRegister m) {
        int d = degree.getOrDefault(m, 0);
        degree.compute(m, (k, v) -> v - 1);
        if (d == K) {
            Set<MIPSRegister> a = Adjacent(m);
            a.add(m);
            EnableMoves(a);
            spillWorklist.remove(m);
            if (MoveRelated(m)) {
                freezeWorklist.add(m);
            } else simplifyWorklist.add(m);
        }
    }

    private void EnableMoves(Set<MIPSRegister> nodes) {
        for (MIPSRegister n : nodes) {
            for (MIPSInstruction m : NodeMoves(n)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    worklistMoves.add(m);
                }
            }
        }
    }

    private void Coalesce() {
        MIPSInstruction m = worklistMoves.stream().findFirst().get();
        MIPSRegister x = ((EInsrtruction) m).getRd();
        MIPSRegister y = ((EInsrtruction) m).getRs();
        x = GetAlias(x);
        y = GetAlias(y);
        MIPSRegister u, v;
        if (precolored.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }
        worklistMoves.remove(m);
        if (u.equals(v)) {
            coalescedMoves.add(m);
            AddWorkList(u);
        } else if (precolored.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            AddWorkList(u);
            AddWorkList(v);
        } else if ((precolored.contains(u) && Adjacent(v).stream().allMatch(t -> OK(t, v))) || (!(precolored.contains(u) && Conservative(Adjacent(u), Adjacent(v))))) {
            coalescedMoves.add(m);
            Combine(u, v);
            AddWorkList(u);
        } else activeMoves.add(m);
    }


    private MIPSRegister GetAlias(MIPSRegister n) {
        if (coalescedNodes.contains(n)) {
            return GetAlias(alias.get(n));
        } else
            return n;
    }

    private void AddWorkList(MIPSRegister u) {
        if (!precolored.contains(u) && !MoveRelated(u) && (!degree.containsKey(u) || degree.get(u) < K)) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    private boolean OK(MIPSRegister t, MIPSRegister v) {
        return degree.getOrDefault(t, 0) < K || precolored.contains(t) || adjSet.contains(new Edge(t, v));
    }

    private boolean Conservative(Set<MIPSRegister> nodes1, Set<MIPSRegister> nodes2) {
        Set<MIPSRegister> nodes = new HashSet<>(nodes1);
        nodes.addAll(nodes2);
        int k = 0;
        for (MIPSRegister n : nodes) {
            if (degree.get(n) >= K)
                k++;
        }
        return k < K;
    }

    private void Combine(MIPSRegister u, MIPSRegister v) {
        if (freezeWorklist.contains(v)) {
            freezeWorklist.remove(v);
        } else
            spillWorklist.remove(v);
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        EnableMoves(new HashSet<>() {{
            add(v);
        }});
        for (MIPSRegister t : Adjacent(v)) {
            AddEdge(t, u);
            DecrementDegree(t);
        }
        if (degree.getOrDefault(u, 0) >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }

    private void Freeze() {
        MIPSRegister u = freezeWorklist.iterator().next();
        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        FreezeMoves(u);
    }

    private void FreezeMoves(MIPSRegister u) {
        for (MIPSInstruction m : NodeMoves(u)) {
            MIPSRegister x = ((EInsrtruction) m).getRd();
            MIPSRegister y = ((EInsrtruction) m).getRs();
            MIPSRegister v;
            MIPSRegister ay = GetAlias(y), au = GetAlias(u);
            if (ay.equals(au)) {
                v = GetAlias(x);
            } else v = ay;
            activeMoves.remove(m);
            frozenMoves.add(m);
            if (NodeMoves(v).isEmpty() && degree.getOrDefault(v, 0) < K) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }

    private void SelectSpill() {
        MIPSRegister m = spillWorklist.stream().findAny().get(); // 先随便找吧
        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        FreezeMoves(m);
    }

    private void AssignColors() {
        while (!selectStack.isEmpty()) {
            MIPSRegister n = selectStack.pop();
            HashSet<Integer> okColors = new HashSet<>(RegisterInfo.getCanIndexAllocRegister());
            for (MIPSRegister w : adjList.getOrDefault(n, new HashSet<>())) {
                MIPSRegister aw = GetAlias(w);
                if (coloredNodes.contains(aw) || precolored.contains(aw)) {
                    okColors.remove(color.get(aw));
                }
            }
            if (okColors.isEmpty()) spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                int c = okColors.stream().findFirst().get();
                color.put(n, c);
            }
        }
        for (MIPSRegister n : coalescedNodes) {
            MIPSRegister an = GetAlias(n);
            color.put(n, color.get(an));
        }
    }


    private void RewriteProgram(MIPSFunction mipsFunction) {
        HashSet<MIPSRegister> newTemp = new HashSet<>();
        for (MIPSRegister n : spilledNodes) {
//            int cntInstr = 0;
            for (MIPSBBlock block : mipsFunction.getBlocks()) {
                LinkedList<MIPSInstruction> temp = new LinkedList<>(block.getInstructions());
                for (MIPSInstruction instr : temp) {
                    VirtualRegister vReg = null;
                    MIPSInstruction firstUse = null;
                    MIPSInstruction lastDef = null;
                    HashSet<MIPSRegister> defs = new HashSet<>(instr.getDef());
                    HashSet<MIPSRegister> uses = new HashSet<>(instr.getUse());
                    for (MIPSRegister use : uses) {
                        if (use.equals(n)) {
                            if (firstUse == null && lastDef == null)
                                firstUse = instr;
                            if (vReg == null) {
                                vReg = new VirtualRegister();
                                newTemp.add(vReg);
                            }
                            instr.replaceUse(use, vReg);
                        }
                    }
                    for (MIPSRegister def : defs) {
                        if (def.equals(n)) {
                            lastDef = instr;
                            if (vReg == null) {
                                vReg = new VirtualRegister();
                                newTemp.add(vReg);
                            }
                            instr.replaceDef(def, vReg);
                        }
                    }
                    if (lastDef != null) {
                        RInstruction store = new RInstruction("sw   ", RegisterInfo.getPhysicalRegisterById(29), vReg, mipsFunction.getAllocSize());
                        block.addMIPSInstructionAfter(lastDef, store);
                    }
                    if (firstUse != null) {
                        RInstruction load = new RInstruction("lw   ", RegisterInfo.getPhysicalRegisterById(29), vReg, mipsFunction.getAllocSize());
                        block.addMIPSInstructionBefore(firstUse, load);
                    }
                }
            }
            mipsFunction.addAllocSize(4);   // 开始以为在前面先加栈空间，后面发现会导致，寄存器溢出的时候可能会有一个保存的栈和新分配的位置重合,相当于往后位移了4
        }
        spilledNodes.clear();
        initial.clear();
        coalescedNodes.addAll(newTemp);
        coloredNodes.addAll(coalescedNodes);
        initial.addAll(coloredNodes);
        coalescedNodes.clear();
        coloredNodes.clear();
    }

}
