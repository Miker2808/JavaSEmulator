package engine.execution;

import engine.SInstructions;
import engine.SInstructionsView;
import engine.SProgram;
import engine.instruction.SInstruction;

import java.util.*;

public class ExecutionContext {
    private final HashMap<String, Integer> variables = new HashMap<>();
    private final HashMap<String, Integer> labelMap;
    private int pc;
    private int cycles;
    private boolean exit;

    public ExecutionContext(SInstructionsView sInstructions, HashMap<String, Integer> InputVariables){
        variables.putAll(InputVariables);
        List<String> used_variables = sInstructions.getVariablesUsed();
        for(String variable : used_variables){
            variables.putIfAbsent(variable, 0);
        }
        labelMap = mapLabels(sInstructions);
        exit = false;
        pc = 1;
        cycles = 0;
    }

    // maps labels to line number
    // simply assigns label to map on first encounter each line
    private HashMap<String, Integer> mapLabels(SInstructionsView sInstructions){
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

    // converts hashmap of variables into ordered hash map in order of exercise requirements
    private static LinkedHashMap<String, Integer> buildOrderedMap(Map<String, Integer> source) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        // Always put "y" (default to 0 if missing)
        result.putIfAbsent("y", source.getOrDefault("y", 0));

        // Add x1, x2, x3... in numeric order if present
        int xIndex = 1;
        while (source.containsKey("x" + xIndex)) {
            result.putIfAbsent("x" + xIndex, source.get("x" + xIndex));
            xIndex++;
        }

        // Add z1, z2, z3... in numeric order if present
        int zIndex = 1;
        while (source.containsKey("z" + zIndex)) {
            result.putIfAbsent("z" + zIndex, source.get("z" + zIndex));
            zIndex++;
        }

        return result;
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

    public LinkedHashMap<String, Integer> getOrderedVariables(){
        return buildOrderedMap(variables);
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
