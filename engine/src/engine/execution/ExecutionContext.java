package engine.execution;

import engine.SInstructions;
import engine.SProgram;
import engine.instruction.SInstruction;

import java.util.ArrayList;
import java.util.HashMap;

public class ExecutionContext {

    private final HashMap<String, Integer> variables = new HashMap<>();
    private final HashMap<String, Integer> labelMap;
    private int pc;
    private int cycles;
    private boolean exit;

    public ExecutionContext(SInstructions sInstructions, HashMap<String, Integer> InputVariables){
        variables.putAll(InputVariables);
        labelMap = mapLabels(sInstructions);
        exit = false;
        pc = 1;
        cycles = 0;
    }

    // maps labels to line number
    // simply assigns label to map on first encounter each line
    private HashMap<String, Integer> mapLabels(SInstructions sInstructions){
        HashMap<String, Integer> map = new HashMap<>();
        int size = sInstructions.size();
        for (int line=1; line <= size; line++){
            SInstruction instr = sInstructions.getInstruction(line);
            String label = instr.getSLabel();
            if(label != null){
                if(!map.containsKey(label)){
                    map.put(label, line);
                }
            }
        }
        return map;
    }

    public boolean getExit(){
        return exit;
    }

    public void setExit(boolean exit){
        this.exit = exit;
    }

    public int getPC(){
        return pc;
    }
    public void setPC(int pc){
        this.pc = pc;
    }

    public int getCycles(){
        return cycles;
    }

    public void setCycles(int cycles){
        this.cycles = cycles;
    }

    public HashMap<String, Integer> getVariables(){
        return variables;
    }

    public void increaseCycles(int cycles){
        this.cycles += cycles;
    }
    public void increasePC(int steps){
        this.pc += steps;
    }

    public int getLabelLine(String label){
        return labelMap.get(label);
    }
}
