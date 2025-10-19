package engine.interpreter;

import engine.SInstructionsView;
import engine.execution.ExecutionContext;
import engine.execution.ExecutionContextHistory;
import enums.RunState;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SInterpreter
{
    private SInstructionsView sInstructions;
    private ExecutionContext context;
    private HashMap<String, Integer> inputVariables;
    private ExecutionContextHistory contextHistory; // used for backstepping, not for user
    private final int backstep_capacity = 50;
    private long steps = 0;
    private boolean new_run = true;
    private RunState state = RunState.RUNNING;
    private AtomicLong creditsAvail;
    private AtomicLong creditsConsumed;
    private ArrayList<Long> stepsByGen;

    public SInterpreter(SInstructionsView sInstructions, HashMap<String, Integer> inputVariables, AtomicLong creditsAvail, AtomicLong creditsConsumed){
        this.sInstructions = sInstructions;
        this.inputVariables = new HashMap<>(inputVariables);
        this.contextHistory = new ExecutionContextHistory(backstep_capacity);
        this.context = new ExecutionContext(sInstructions, inputVariables);
        this.creditsAvail = creditsAvail;
        this.creditsConsumed = creditsConsumed;
        stepsByGen = new ArrayList<>(Arrays.asList(0L, 0L, 0L, 0L));
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
        int prev_cycles = context.getCycles();
        if(keephistory) {
            contextHistory.push(context);
        }
        if(!context.getExit() && context.getPC() <= num_lines) {
            int gen = sInstructions.getInstruction(context.getPC()).getGeneration();
            stepsByGen.set(gen - 1, stepsByGen.get(gen-1) + 1);
            sInstructions.getInstruction(context.getPC()).execute(context);
        }

        if(!(context.getPC() <= num_lines) || (steps >= Long.MAX_VALUE - 1)){
            context.setExit(true);
            this.state = RunState.COMPLETE;
        }

        long credits_used = context.getCycles() - prev_cycles;
        if(credits_used > this.creditsAvail.get()) {
            context.setExit(true);
            this.state = RunState.ABORTED;
        }



        long credits_consume = Math.min(credits_used, this.creditsAvail.get());
        this.creditsAvail.addAndGet(-1 * credits_consume);
        this.creditsConsumed.addAndGet(credits_consume);

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

    protected ExecutionContext reRunToSteps(long steps){
        long curr_steps = 0;
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
        long curr_steps = 0;
        while(!context.getExit()){

            if(breakpoints != null){
                if(breakpoints.contains(context.getPC())){
                    if(new_run || curr_steps > 0){
                        break;
                    }
                }
            }
            this.step(false);
            curr_steps++;
        }
        new_run = false;
        contextHistory.clear();

        return context;

    }

    public LinkedHashMap<String, Long> getGenUsage(){
        LinkedHashMap<String, Long> usage = new LinkedHashMap<>();
        usage.put("I", stepsByGen.get(0));
        usage.put("II", stepsByGen.get(1));
        usage.put("III", stepsByGen.get(2));
        usage.put("IV", stepsByGen.get(3));
        return usage;
    }

    public LinkedHashMap<String, Integer> getOrderedVariables(){
        return context.getOrderedVariables();
    }

    public boolean getExit(){
        return context.getExit();
    }

    public void Stop(){
        this.state = RunState.COMPLETE;
        context.setExit(true);
    }

    public long getCycles(){
        return context.getCycles();
    }
    public long getSteps(){
        return steps;
    }

    public int getPC(){
        return context.getPC();
    }

    public Boolean isRunning(){
        return !context.getExit();
    }

    public RunState getState(){
        return state;
    }
}
