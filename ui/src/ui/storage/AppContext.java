package ui.storage;

import java.util.LinkedHashMap;

public class AppContext {
    private String username;
    private LinkedHashMap<String, Integer> inputVariables;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LinkedHashMap<String, Integer> getInputVariables(){ return inputVariables; }
    public void setInputVariables(LinkedHashMap<String, Integer> inputVariables){ this.inputVariables = inputVariables; }
}
