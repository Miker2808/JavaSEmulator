package engine;

import engine.execution.ExecutionContext;
import engine.execution.ExecutionResult;

import java.util.*;

public class SInterpreter
{
    private SProgram program;
    private ExecutionContext context;

    public SInterpreter(SProgram program, ArrayList<Integer> inputVariables){
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

}
