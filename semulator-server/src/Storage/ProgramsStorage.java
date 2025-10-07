package Storage;

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

    public Map<String, SProgram> getProgramsMap(){
        return this.programs;
    }
    public Map<String, SFunction> getFunctionsMap(){
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

    public void addProgram(SProgram program) throws Exception{
        if(!this.programs.containsKey(program.getName())) {
            this.programs.put(program.getName(), program);
        }
        else{
            throw new Exception(String.format("Program name %s is already in use", program.getName()));
        }
    }
    public void addFunction(SFunction function) throws Exception{
        if(!containsFunction(function.getName())) {
            this.functions.put(function.getName(), function);
        }
        else{
            throw new Exception(String.format("Function name %s is already in use", function.getName()));
        }
    }

    public void addFunctions(SFunctions functions) throws Exception{
        for (SFunction func : functions.getSFunction()){
            if(containsFunction(func.getName())){
                throw new Exception(String.format("Function name %s is already in use", func.getName()));
            }
        }

        for (SFunction func : functions.getSFunction()) {
            this.addFunction(func);
        }
    }

    // The method to use if you want to add SProgram with SFunctions
    // On any error, nothing is updated to the storage.
    public void addAll(SProgram program) throws Exception{
        if(containsProgram(program.getName())) {
            throw new Exception(String.format("Program name %s is already in use", program.getName()));
        }
        addFunctions(program.getSFunctions());
        addProgram(program);
    }

    public boolean containsProgram(String programName){
        return this.programs.containsKey(programName);
    }

    public boolean containsFunction(String functionName){
        return this.functions.containsKey(functionName);
    }


}
