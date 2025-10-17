package dto;

import enums.RunState;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ExecutionDTO {
    public LinkedHashMap<String, Integer> runVariables;
    public Integer runPCHighlight;
    public Long cycles;
    public String programName;
    public Long credits;
    public RunState state;
    public Boolean computing;
    public Long steps;

}
