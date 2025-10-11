package dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ExecutionDTO {
    public ArrayList<SInstructionDTO> sInstructionsDTOs;
    public ArrayList<SInstructionDTO> expansionHistoryDTO;
    public LinkedHashMap<String, Integer> runVariables;
    public ArrayList<String> inputVariables;
    public ArrayList<Integer> inputValues;
    public ArrayList<Integer> searchHighlight;
    public Integer runPCHighlight;
    public Integer cycles;
    public Integer degree;
    public Integer maxDegree;
    public String programName;
    public Integer credits;
    public Boolean running;

}
