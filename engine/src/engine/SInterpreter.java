package engine;

import engine.execution.ExecutionContext;
import engine.execution.ExecutionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SInterpreter
{
    private SProgram program;
    private ExecutionContext context;

    public SInterpreter(SProgram program, HashMap<String, Integer> inputVariables){
        this.program = (program == null) ? new SProgram() : program;
        this.context = new ExecutionContext(program, inputVariables);
    }

    // emulates a run on a clean environment
    public ExecutionResult run(){

        int num_lines = program.Size();
        while(!context.getExit() && context.getPC() <= num_lines){
            step();
        }
        return new ExecutionResult(context);
    }

    // Runs a single step in execution
    public void step(){
        program.getInstruction(context.getPC()).execute(context);
    }

    public int getPC(){
        return context.getPC();
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
