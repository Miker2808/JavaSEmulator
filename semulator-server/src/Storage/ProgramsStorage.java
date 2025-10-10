package Storage;

import DTOConverter.SProgramDTOConverter;
import dto.SProgramViewStatsDTO;
import engine.SProgram;
import engine.SProgramView;
import engine.functions.SFunction;
import engine.functions.SFunctions;

import java.util.*;

public class ProgramsStorage {

    private Map<String, SProgram> programs;
    private Map<String, SFunction> functions;

    public ProgramsStorage() {
        programs = new HashMap<String, SProgram>();
        functions = new HashMap<String, SFunction>();
    }

    protected Map<String, SProgram> getProgramsMap(){
        return this.programs;
    }
    protected Map<String, SFunction> getFunctionsMap(){
        return this.functions;
    }

    public HashSet<String> getAvailableProgramNames(){
        return new HashSet<>(this.programs.keySet());
    }

    public HashSet<String> getAvailableFunctionNames(){
        return new HashSet<>(this.functions.keySet());
    }

    public SProgramView getProgramView(String programName){
        if(this.functions.containsKey(programName)){
            return this.functions.get(programName);
        }
        else if(this.programs.containsKey(programName)){
            return this.programs.get(programName);
        }
        return null;
    }

    protected void addProgram(SProgram program) throws Exception{
        if(!this.programs.containsKey(program.getName())) {
            this.programs.put(program.getName(), program);
        }
        else{
            throw new Exception(String.format("Program name %s is already in use", program.getName()));
        }
    }
    protected void addFunction(SFunction function, String parentName) throws Exception{
        if(!containsFunction(function.getName())) {
            function.setParentProgram(parentName);
            this.functions.put(function.getName(), function);
        }
        else{
            throw new Exception(String.format("Function name %s is already in use", function.getName()));
        }
    }

    protected void addFunctions(SFunctions functions, String parentName) throws Exception{
        for (SFunction func : functions.getSFunction()){
            if(containsFunction(func.getName())){
                throw new Exception(String.format("Function name %s is already in use", func.getName()));
            }
        }

        for (SFunction func : functions.getSFunction()) {
            this.addFunction(func, parentName);
        }
    }

    // The method to use if you want to add SProgram with SFunctions
    // On any error, nothing is updated to the storage.
    public void addAll(SProgram program) throws Exception{
        if(containsProgram(program.getName())) {
            throw new Exception(String.format("Program name %s is already in use", program.getName()));
        }
        addFunctions(program.getSFunctions(), program.getName());
        addProgram(program);
    }

    public boolean containsProgram(String programName){
        return this.programs.containsKey(programName);
    }

    public boolean containsFunction(String functionName){
        return this.functions.containsKey(functionName);
    }

    // get all programs and functions as programview, returns in order of functions first, then programs
    public ArrayList<SProgramView> getFullProgramsView(){
        ArrayList<SProgramView> output = new ArrayList<>();
        for(Map.Entry<String, SFunction> entry : this.functions.entrySet()){
            output.add(entry.getValue());
        }
        for(Map.Entry<String, SProgram> entry : this.programs.entrySet()){
            output.add(entry.getValue());
        }
        return output;
    }

    public ArrayList<SProgramViewStatsDTO> getFullProgramsStats(){
        ArrayList<SProgramViewStatsDTO> output = new ArrayList<>();
        for(SProgramView program : this.programs.values()){
            output.add(SProgramDTOConverter.toStatsDTO(program));
        }
        return output;
    }

    public ArrayList<SProgramViewStatsDTO> getFullFunctionsStats(){
        ArrayList<SProgramViewStatsDTO> output = new ArrayList<>();
        for(SProgramView function : this.functions.values()){
            output.add(SProgramDTOConverter.toStatsDTO(function));
        }
        return output;
    }

    public void validateProgramNotUsed(SProgram program) throws Exception{
        for (SFunction func : program.getSFunctions().getSFunction()){
            if(containsFunction(func.getName())){
                throw new Exception(String.format("Function name %s is already in use", func.getName()));
            }
        }
        if(containsProgram(program.getName())){
            throw new Exception(String.format("Program name %s is already in use", program.getName()));
        }

    }


}
