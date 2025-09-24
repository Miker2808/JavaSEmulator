package engine;

import java.util.ArrayList;
import java.util.HashMap;

public class ExecutionHistoryManager {
    private final HashMap<String, ArrayList<ExecutionHistory>> executionHistoryMap = new HashMap<>();


    public void clearHistory(){
        executionHistoryMap.clear();
    }

    public ArrayList<ExecutionHistory> getExecutionHistory(String programName){
        if(!executionHistoryMap.containsKey(programName)){
            executionHistoryMap.put(programName, new ArrayList<>());
        }

        return executionHistoryMap.get(programName);
    }

    public void addExecutionHistory(String programName, ExecutionHistory executionHistory){

        if(!executionHistoryMap.containsKey(programName)){
            executionHistoryMap.put(programName, new ArrayList<>());

        }

        executionHistoryMap.get(programName).add(executionHistory);
        ArrayList<ExecutionHistory> executionHistoryList = executionHistoryMap.get(programName);
        executionHistoryList.getLast().setNum(executionHistoryList.size());
    }


}
