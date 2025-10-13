package engine.execution;

import engine.SInstructionsView;
import engine.SVariable.SVariable;
import engine.SVariable.SVariablesMap;
import engine.instruction.SInstruction;

import java.util.*;


public class ExecutionContext {
    private final HashMap<String, Integer> variables;
    private final SVariablesMap variablesSmartMap;
    private final HashMap<String, Integer> labelMap;
    private int pc;
    private int cycles;
    private boolean exit;
    private boolean complete;

    public ExecutionContext(SInstructionsView sInstructions, HashMap<String, Integer> InputVariables){

        variables =  new HashMap<>();
        variablesSmartMap = new SVariablesMap();

        variables.putAll(InputVariables);
        variablesSmartMap.putAll(InputVariables);

        List<String> used_variables = sInstructions.getVariablesUsed();
        for(String variable : used_variables){
            variables.putIfAbsent(variable, 0);

            variablesSmartMap.get(new SVariable(variable));
        }
        labelMap = mapLabels(sInstructions);
        exit = false;
        complete = false;
        pc = 1;
        cycles = 0;
    }

    public ExecutionContext(ExecutionContext other){
        variables = new HashMap<>(other.variables);
        variablesSmartMap = new SVariablesMap(other.variablesSmartMap);
        labelMap = new HashMap<>(other.labelMap);
        cycles = other.cycles;
        exit = other.exit;
        pc = other.pc;
        complete = other.complete;
    }

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
    public static LinkedHashMap<String, Integer> buildOrderedMap(Map<String, Integer> source) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        // Always put "y" (default to 0 if missing)
        result.put("y", source.getOrDefault("y", 0));

        // Handle all x{i} keys
        source.keySet().stream()
                .filter(k -> k.startsWith("x"))
                .sorted(Comparator.comparingInt(k -> Integer.parseInt(k.substring(1))))
                .forEach(k -> result.put(k, source.get(k)));

        // Handle all z{j} keys
        source.keySet().stream()
                .filter(k -> k.startsWith("z"))
                .sorted(Comparator.comparingInt(k -> Integer.parseInt(k.substring(1))))
                .forEach(k -> result.put(k, source.get(k)));

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
        //return buildOrderedMap(variables);
        return variablesSmartMap.getOrderedMap();
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

    public int getVariableValue(SVariable variable){
        return variablesSmartMap.get(variable);
    }

    public void setVariableValue(SVariable variable, int value){
        variablesSmartMap.put(variable, value);
    }
}
