package dto;

import java.util.List;
import java.util.Map;

public class SProgramDTO {

    public List<SInstructionDTO> sInstructionsDTOs;
    public List<String> inputVariables;
    public List<String> variablesUsed;
    public List<String> labelsUsed;
    public Map<Integer, Integer> architectureSummary;
    public Integer current_degree;
    public Integer maxDegree;
    public String programName;

}
