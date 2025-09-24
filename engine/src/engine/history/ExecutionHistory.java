package engine.history;

import engine.execution.ExecutionContext;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ExecutionHistory implements Serializable {
    private final int degree;
    private final LinkedHashMap<String, Integer> inputVariables;
    private LinkedHashMap<String, Integer> variables;
    private int y;
    private int cycles;
    private int num;

    public ExecutionHistory(LinkedHashMap<String, Integer> inputVariables, int degree)
    {
        this.inputVariables = new LinkedHashMap<>(inputVariables);
        this.degree = degree;
    }
    public ExecutionHistory(LinkedHashMap<String, Integer> inputVariables, ExecutionContext executionContext, int degree)
    {
        this.inputVariables = new LinkedHashMap<>(inputVariables);
        this.variables = new LinkedHashMap<>(executionContext.getOrderedVariables());
        this.cycles = executionContext.getCycles();
        this.degree = degree;
        this.y = variables.get("y");
    }

    public int getNum(){
        return num;
    }

    public void setNum(int num){
        this.num = num;
    }

    public int getDegree(){
        return degree;
    }
    public int getY(){
        return y;
    }
    public int getCycles(){
        return cycles;
    }


    public LinkedHashMap<String, Integer> getInputVariables(){
        return inputVariables;
    }

    public LinkedHashMap<String, Integer> getVariables(){
        return variables;
    }


    public void setCycles(int cycles){
        this.cycles = cycles;
    }

    public void setContext(ExecutionContext context){
        this.variables = new LinkedHashMap<>(context.getOrderedVariables());
        this.y = this.variables.get("y");
        this.cycles = context.getCycles();;

    }


}
