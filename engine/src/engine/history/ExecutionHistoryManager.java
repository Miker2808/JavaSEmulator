package engine.history;

import java.util.ArrayList;

public class ExecutionHistoryManager {
    private final ArrayList<ExecutionHistory> executionHistoryList = new ArrayList<>();

    public void clearHistory(){
        executionHistoryList.clear();
    }

    public ArrayList<ExecutionHistory> getExecutionHistory(){
        return executionHistoryList;
    }

    public void addExecutionHistory(ExecutionHistory executionHistory){

        executionHistory.setNum(executionHistoryList.size() + 1);
        executionHistoryList.add(executionHistory);
    }


}
