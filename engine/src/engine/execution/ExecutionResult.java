package engine.execution;

import java.util.*;

public class ExecutionResult {
    private LinkedHashMap<String, Integer> variables;
    private int cycles;


    public ExecutionResult(ExecutionContext context){
        variables = buildOrderedMap(context.getVariables());
        cycles = context.getCycles();
    }

    public LinkedHashMap<String, Integer> getVariables(){
        return variables;
    }

    public int getCycles(){
        return cycles;
    }

    // converts hashmap of variables into ordered hash map in order of exercise requirements
    private static LinkedHashMap<String, Integer> buildOrderedMap(Map<String, Integer> source) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        // Always put "y" (default to 0 if missing)
        result.put("y", source.getOrDefault("y", 0));

        // Add x1, x2, x3... in numeric order if present
        int xIndex = 1;
        while (source.containsKey("x" + xIndex)) {
            result.put("x" + xIndex, source.get("x" + xIndex));
            xIndex++;
        }

        // Add z1, z2, z3... in numeric order if present
        int zIndex = 1;
        while (source.containsKey("z" + zIndex)) {
            result.put("z" + zIndex, source.get("z" + zIndex));
            zIndex++;
        }

        return result;
    }
}
