package ui.storage;

import java.util.LinkedHashMap;

public class AppContext {
    private String username;
    private LinkedHashMap<String, Integer> inputVariables;
    private Integer degree = 0;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LinkedHashMap<String, Integer> getInputVariables(){ return inputVariables; }
    public void setInputVariables(LinkedHashMap<String, Integer> inputVariables){ this.inputVariables = inputVariables; }
    public Integer getDegree() { return degree; }
    public void setDegree(Integer degree) { this.degree = degree; }
    public void reset(){
        inputVariables = null;
        degree = 0;
    }
}
