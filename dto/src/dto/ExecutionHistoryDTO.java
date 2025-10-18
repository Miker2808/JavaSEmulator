package dto;

import java.util.LinkedHashMap;

public class ExecutionHistoryDTO {

    public int num;
    public String type;
    public String name;
    public String gen;
    public int degree;
    public int y;
    public int cycles;
    public LinkedHashMap<String, Integer> inputVariables;
    public LinkedHashMap<String, Integer> resultVariables;

}
