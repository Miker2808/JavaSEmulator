package dto;

import java.util.LinkedHashMap;
import java.util.Set;

public class ExecutionRequestDTO {
    public String command;
    public Boolean debug;
    public Integer generation;
    public Set<Integer> breakpoints;
    public LinkedHashMap<String, Integer> inputVariables;

}
