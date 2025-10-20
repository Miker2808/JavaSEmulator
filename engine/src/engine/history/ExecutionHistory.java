package engine.history;

import engine.SProgramView;
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
    private String programName;
    private String type;
    private String generation;
    private String userString;

    public ExecutionHistory(SProgramView program, LinkedHashMap<String, Integer> inputVariables, int degree)
    {
        this.inputVariables = new LinkedHashMap<>(inputVariables);
        this.degree = degree;
        this.programName = program.getName();
        this.type = String.valueOf(program.getProgramType()).toLowerCase();
        this.generation = setGeneration(program.getInstructionsView().getRequiredGen());
        this.userString = program.getUserString();

    }
    public ExecutionHistory(SProgramView program, LinkedHashMap<String, Integer> inputVariables, ExecutionContext executionContext, int degree)
    {
        this.inputVariables = new LinkedHashMap<>(inputVariables);
        this.variables = new LinkedHashMap<>(executionContext.getOrderedVariables());
        this.cycles = executionContext.getCycles();
        this.degree = degree;
        this.y = variables.get("y");
        this.programName = program.getName();
        this.type = String.valueOf(program.getProgramType()).toLowerCase();
        this.generation = setGeneration(program.getInstructionsView().getRequiredGen());
        this.userString = program.getUserString();
    }

    protected String setGeneration(int gen){
        return switch (gen) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            default -> "";
        };
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

    public String getName(){
        return programName;
    }

    public String getType(){
        return type;
    }

    public String getGeneration(){
        return generation;
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

    public String getUserString(){
        return userString;
    }


}
