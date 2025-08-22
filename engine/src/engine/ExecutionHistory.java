package engine;

import java.util.ArrayList;

public class ExecutionHistory
{
    private final int degree;
    private final ArrayList<Integer> variables;
    private final int y;
    private final int cycles;

    public ExecutionHistory(int degree, ArrayList<Integer> variables, int y, int cycles){

        this.degree = degree;
        this.variables = variables;
        this.y = y;
        this.cycles = cycles;
    }

    public int getDegree(){
        return degree;
    }

    public ArrayList<Integer> getVariables(){
        return variables;
    }

    public int getY(){
        return y;
    }
    public int getCycles(){
        return cycles;
    }

}
