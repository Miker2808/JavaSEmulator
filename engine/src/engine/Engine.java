
package engine;

import engine.execution.ExecutionContext;
import engine.expander.SProgramExpander;
import engine.functions.SFunction;
import engine.functions.SFunctions;
import engine.history.ExecutionHistory;
import engine.history.ExecutionHistoryManager;
import engine.interpreter.SInterpreter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;


public class Engine implements Serializable{
    private SProgram loadedProgram = null;
    private ExecutionHistoryManager historyManager = new ExecutionHistoryManager();
    private ExecutionHistory currentExecutionHistory = null;
    private SInterpreter interpreter;
    private boolean running = false;
    private String current_running = "";

    // loads XML file for SProgram. raises exception on invalid
    // overrides current loaded program on successful load
    public static SProgram loadFromXML(InputStream xmlStream) throws Exception {
        SProgram program;

        // try to unmarshal to SProgram object
        try {
            JAXBContext context = JAXBContext.newInstance(SProgram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // This will throw JAXBException if XML is invalid or doesn't match class
            program = (SProgram) unmarshaller.unmarshal(xmlStream);

        } catch (JAXBException e) {
            throw new Exception("XML file may be invalid schema-wise");
        }

        return program;
    }

    // TODO: implement servlet version
    public SProgramView getExpandedProgram(String program_name, int degree){

        SFunctions functions = loadedProgram.getSFunctions();
        for( SFunction func : functions.getSFunction()){
            if(func.getName().equals(program_name)){
                return SProgramExpander.expand(func, degree);
            }
        }

        // Default to main program
        return SProgramExpander.expand(loadedProgram, degree);
    }

    // TODO: implement servlet version
    public SProgramView getSelectedProgram(String program_name){

        SFunctions functions = loadedProgram.getSFunctions();
        for( SFunction func : functions.getSFunction()){
            if(func.getName().equals(program_name)){
                return func;
            }
        }

        return loadedProgram;
    }

    // get names of possible programs
    public ArrayList<String> getLoadedProgramNames(){
        System.out.println("getLoadedProgramNames needs reimplementation!");
        return null;
    }

    private ExecutionContext runStaticProgram(String program_name, LinkedHashMap<String, Integer> input, int degree){
        // default main program
        SProgramView expanded = getExpandedProgram(program_name, degree);
        ExecutionContext context = SInterpreter.staticRun(expanded.getInstructionsView(), input);
        historyManager.addExecutionHistory(program_name, new ExecutionHistory(input, context, degree));

        return context;
    }

    public ExecutionContext runProgram(String program_name, LinkedHashMap<String, Integer> input, int degree, boolean debug, Set<Integer> breakpoints){
        current_running = program_name;
        if(!debug){
            running = false;
            return runStaticProgram(program_name, input, degree);
        }
        SProgramView expanded = getExpandedProgram(program_name, degree);
        this.interpreter = new SInterpreter(expanded.getInstructionsView(), input);
        currentExecutionHistory = new ExecutionHistory(input, degree);
        return resumeLoadedRun(breakpoints);
    }

    public ExecutionContext stepLoadedRun(){
        ExecutionContext context = this.interpreter.step(true);
        currentExecutionHistory.setContext(context);
        if(context.getExit()){
            running = false;
            historyManager.addExecutionHistory(current_running, currentExecutionHistory);
        }
        return context;
    }

    public ExecutionContext backstepLoadedRun(){
        ExecutionContext context = this.interpreter.backstep();
        currentExecutionHistory.setContext(context);
        return context;
    }

    public void stopLoadedRun(){
        if(running){
            running = false;
            historyManager.addExecutionHistory(current_running, currentExecutionHistory);
        }
    }

    public ExecutionContext resumeLoadedRun(Set<Integer> breakpoints){

        ExecutionContext context = this.interpreter.runToBreakPoint(breakpoints);
        currentExecutionHistory.setContext(context);
        running = true;
        if(context.getExit()){
            historyManager.addExecutionHistory(current_running, currentExecutionHistory);
            running = false;
        }

        return context;
    }


    public ArrayList<ExecutionHistory> getHistory(String program_name){

        return historyManager.getExecutionHistory(program_name);
    }








}
