package engine.interpreter;

import engine.SInstructionsView;
import engine.execution.ExecutionContext;
import engine.execution.ExecutionContextHistory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class SInterpreter
{
    private SInstructionsView sInstructions;
    private ExecutionContext context;
    private HashMap<String, Integer> inputVariables;
    private ExecutionContextHistory contextHistory;
    private final int backstep_capacity = 20;
    private int steps = 0;

    public SInterpreter(SInstructionsView sInstructions, HashMap<String, Integer> inputVariables){
        this.sInstructions = sInstructions;
        this.inputVariables = new HashMap<>(inputVariables);
        this.contextHistory = new ExecutionContextHistory(backstep_capacity);
        this.context = new ExecutionContext(sInstructions, inputVariables);

    }

    public static ExecutionContext staticRun(SInstructionsView instructions, HashMap<String, Integer> inputVariables){
        ExecutionContext context = new ExecutionContext(instructions, inputVariables);
        int num_lines = instructions.size();
        while(!context.getExit() && context.getPC() <= num_lines){
            instructions.getInstruction(context.getPC()).execute(context);
        }
        context.setExit(true);
        return context;
    }

    // Runs a single step in execution
    public ExecutionContext step(boolean keephistory){
        int num_lines = sInstructions.size();
        if(keephistory) {
            contextHistory.push(context);
        }
        if(!context.getExit() && context.getPC() <= num_lines) {
            sInstructions.getInstruction(context.getPC()).execute(context);
        }
        if(!(context.getPC() <= num_lines)){
            context.setExit(true);
        }
        steps++;
        return context;
    }

    public ExecutionContext backstep(){

        if(steps == 0){
            return context;
        }
        if(contextHistory.isEmpty()) {
            context = reRunToSteps(steps);
        }
        steps--;
        context = contextHistory.popBack();
        return context;
    }

    protected ExecutionContext reRunToSteps(int steps){
        int curr_steps = 0;
        ExecutionContext context = new ExecutionContext(sInstructions, inputVariables);
        while(curr_steps < steps){
            if(steps - curr_steps < backstep_capacity) {
                contextHistory.push(context);
            }

            sInstructions.getInstruction(context.getPC()).execute(context);
            curr_steps++;
        }

        return context;
    }

    public ExecutionContext runToBreakPoint(Set<Integer> breakpoints){
        int curr_steps = 0;
        while(!context.getExit()){
            if(breakpoints != null){
                if(breakpoints.contains(context.getPC())){
                    if(curr_steps > 0 || steps == 0) {
                        break;
                    }
                }
            }
            this.step(false);
            curr_steps++;
        }

        contextHistory.clear();

        return context;

    }

    public ExecutionContext getExecutionContext(){
        return context;
    }
    public void setExecutionContext(ExecutionContext context){
        this.context = context;
    }

    public LinkedHashMap<String, Integer> getOrderedVariables(){
        return context.getOrderedVariables();
    }

    public boolean getExit(){
        return context.getExit();
    }

    public int getCycles(){
        return context.getCycles();
    }
    public int getSteps(){
        return steps;
    }

}
