package DTOConverter;

import dto.ExecutionHistoryDTO;
import engine.history.ExecutionHistory;

import java.util.ArrayList;

public class HistoryDTOConverter {

    public static ArrayList<ExecutionHistoryDTO> convertToDTO(ArrayList<ExecutionHistory> executionHistoryList) {
        ArrayList<ExecutionHistoryDTO> executionHistoryDTOList = new ArrayList<>();
        for (ExecutionHistory executionHistory : executionHistoryList) {
            ExecutionHistoryDTO executionHistoryDTO = convertToDTO(executionHistory);
            executionHistoryDTOList.add(executionHistoryDTO);

        }
        return executionHistoryDTOList;
    }

    public static ExecutionHistoryDTO convertToDTO(ExecutionHistory executionHistory){
        ExecutionHistoryDTO dto = new ExecutionHistoryDTO();
        dto.inputVariables = executionHistory.getInputVariables();
        dto.degree = executionHistory.getDegree();
        dto.cycles = executionHistory.getCycles();
        dto.y = executionHistory.getY();
        dto.num = executionHistory.getNum();
        dto.gen = executionHistory.getGeneration();
        dto.name = executionHistory.getName();
        dto.userstring = executionHistory.getUserString();
        dto.type = executionHistory.getType();

        return dto;
    }
}
