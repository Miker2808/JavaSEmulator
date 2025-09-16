package engine.interpreter;

import engine.SInstructions;
import engine.execution.ExecutionContext;
import engine.execution.ExecutionResult;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SInterpreter
{
    private SInstructions sInstructions;
    private ExecutionContext context;

    public SInterpreter(SInstructions sInstructions, HashMap<String, Integer> inputVariables){
        this.sInstructions = sInstructions;
        this.context = new ExecutionContext(sInstructions, inputVariables);
    }

    // emulates a run on a clean environment
    public ExecutionResult run(){

        int num_lines = sInstructions.size();
        while(!context.getExit() && context.getPC() <= num_lines){
            step();
        }
        return new ExecutionResult(context);
    }

    // Runs a single step in execution
    public ExecutionContext step(){
        if(!context.getExit()) {
            sInstructions.getInstruction(context.getPC()).execute(context);
        }
        return context;
    }

    public int getPC(){
        return context.getPC();
    }

    public boolean getExit(){
        return context.getExit();
    }

    public int getCycles(){
        return context.getCycles();
    }

    // converts hashmap of variables into ordered hash map in order of exercise requirements
    public LinkedHashMap<String, Integer> getOrderedVariables() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

        // Always put "y" (default to 0 if missing)
        result.put("y", context.getVariables().getOrDefault("y", 0));

        // Add x1, x2, x3... in numeric order if present
        int xIndex = 1;
        while (context.getVariables().containsKey("x" + xIndex)) {
            result.put("x" + xIndex, context.getVariables().get("x" + xIndex));
            xIndex++;
        }

        // Add z1, z2, z3... in numeric order if present
        int zIndex = 1;
        while (context.getVariables().containsKey("z" + zIndex)) {
            result.put("z" + zIndex, context.getVariables().get("z" + zIndex));
            zIndex++;
        }

        return result;
    }

}
