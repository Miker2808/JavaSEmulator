
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
    private final ExecutionHistoryManager historyManager;
    private ExecutionHistory currentExecutionHistory = null;
    private SInterpreter interpreter;
    private boolean running = false;
    public Engine(ExecutionHistoryManager history_manager) {
        this.historyManager = new ExecutionHistoryManager();
    }

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

    /*
    private ExecutionContext runStaticProgram(SProgramView selectedProgram, LinkedHashMap<String, Integer> input){
        // default main program
        ExecutionContext context = SInterpreter.staticRun(selectedProgram.getInstructionsView(), input);
        historyManager.addExecutionHistory(selectedProgram.getName(), new ExecutionHistory(input, context, selectedDegree));

        return context;
    }

    public ExecutionContext runProgram(LinkedHashMap<String, Integer> input, int degree, boolean debug, Set<Integer> breakpoints){

        this.interpreter = new SInterpreter(selectedProgram.getInstructionsView(), input);
        currentExecutionHistory = new ExecutionHistory(input, degree);
        return resumeLoadedRun(breakpoints);
    }

    public ExecutionContext stepLoadedRun(){
        ExecutionContext context = this.interpreter.step(true);
        currentExecutionHistory.setContext(context);
        if(context.getExit()){
            running = false;
            historyManager.addExecutionHistory(selectedProgram.getName(), currentExecutionHistory);
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
    */


}
