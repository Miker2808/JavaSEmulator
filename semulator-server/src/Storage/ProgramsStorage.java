package Storage;

import engine.SProgram;
import engine.functions.SFunction;
import engine.functions.SFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramsStorage {

    private Map<String, SProgram> programs;
    private Map<String, SFunction> functions;

    public ProgramsStorage() {}

    public Map<String, SProgram> getProgramsMap(){
        return this.programs;
    }
    public Map<String, SFunction> getFunctionsMap(){
        return this.functions;
    }

    public List<String> getAvailableProgramNames(){
        List<String> list = new ArrayList<String>();
        list.addAll(this.programs.keySet());
        return list;
    }

    public List<String> getAvailableFunctionNames(){
        List<String> list = new ArrayList<>();
        list.addAll(this.functions.keySet());
        return list;
    }

    public SProgram getProgram(String name){
        return this.programs.get(name);
    }

    public SFunction getFunction(String name){
        return this.functions.get(name);
    }

    public void addProgram(SProgram program){
        this.programs.put(program.getName(), program);
    }
    public void addFunction(SFunction function){
        this.functions.put(function.getName(), function);
    }

    public void addFunctions(SFunctions functions){
        for (SFunction func : functions.getSFunction()) {
            this.addFunction(func);
        }
    }
}
