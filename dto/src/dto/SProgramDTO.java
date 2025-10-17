package dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SProgramDTO {

    public List<SInstructionDTO> sInstructionsDTOs;
    public List<String> inputVariables;
    public List<String> variablesUsed;
    public List<String> labelsUsed;
    public LinkedHashMap<String, Integer> architectureSummary;
    public Integer current_degree;
    public Integer maxDegree;
    public String programName;

}
