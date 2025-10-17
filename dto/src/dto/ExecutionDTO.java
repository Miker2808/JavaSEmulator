package dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ExecutionDTO {
    public LinkedHashMap<String, Integer> runVariables;
    public Integer runPCHighlight;
    public Long cycles;
    public String programName;
    public Long credits;
    public Boolean running;
    public Boolean computing;
    public Long steps;
}
