
package engine;

import engine.execution.ExecutionContext;
import engine.expander.SProgramExpander;
import engine.functions.SFunction;
import engine.functions.SFunctions;
import engine.history.ExecutionHistory;
import engine.history.ExecutionHistoryManager;
import engine.instruction.QuoteInstruction;
import engine.interpreter.SInterpreter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.*;


public class Engine implements Serializable{
    private SProgram loadedProgram = null;
    private ExecutionHistoryManager historyManager = new ExecutionHistoryManager();
    private ExecutionHistory currentExecutionHistory = null;
    private SInterpreter interpreter;
    private boolean running = false;
    private String current_running = "";

    // loads XML file for SProgram. raises exception on invalid
    // overrides current loaded program on successful load
    public void loadFromXML(String path) throws Exception {
        SProgram loadedProgramTemp;

        // verify path to a file
        XMLValidator.validateXMLFile(path);

        // try to unmarshal to SProgram object
        try {
            JAXBContext context = JAXBContext.newInstance(SProgram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File xmlFile = new File(path);

            // This will throw JAXBException if XML is invalid or doesn't match class
            loadedProgramTemp = (SProgram) unmarshaller.unmarshal(xmlFile);

        } catch (JAXBException e) {
            throw new Exception("Failed to load XML: XML file may be invalid schema-wise");
        }

        loadedProgramTemp.validateProgram();
        QuoteInstruction.setProgramViews(getProgramViews(loadedProgramTemp));

        // happens only if validateProgram was successful (did not raise an exception)
        loadedProgram = loadedProgramTemp;
        historyManager.clearHistory();
    }

    // populates Quote instruction with references to functions and main program
    private ArrayList<SProgramView> getProgramViews(SProgram program) {
        ArrayList<SProgramView> programViews = new ArrayList<>();
        try {
            programViews.add(program);
            programViews.addAll(program.getSFunctions().getSFunction());
        }catch (Exception ignored){}

        return programViews;
    }

    public boolean isProgramLoaded(){
        return loadedProgram != null;
    }

    /**
     Checks program_name and returns suitable function or main program if name fits
     Defaults to main program on no find
     Returns version expanded to degree
     **/
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

    // get in 0 degree same shit as above, default loadedProgram
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
        return loadedProgram.getProgramNames();
    }

    public ExecutionContext runProgram(String program_name, LinkedHashMap<String, Integer> input, int degree){
        // default main program
        SProgramView expanded = getExpandedProgram(program_name, degree);
        ExecutionContext context = SInterpreter.staticRun(expanded.getInstructionsView(), input);
        historyManager.addExecutionHistory(program_name, new ExecutionHistory(input, context, degree));

        return context;
    }

    public LinkedHashMap<String, Integer> startDebugRun(String program_name, LinkedHashMap<String, Integer> input, int degree){
        SProgramView expanded = getExpandedProgram(program_name, degree);
        this.interpreter = new SInterpreter(expanded.getInstructionsView(), input);
        currentExecutionHistory = new ExecutionHistory(input, degree);
        running = true;
        current_running = program_name;
        return this.interpreter.getOrderedVariables();
    }

    public ExecutionContext stepLoadedRun(){
        ExecutionContext context = this.interpreter.step();

        if(context.getExit()){
            running = false;
            current_running = "";
            currentExecutionHistory.setContext(context);
            historyManager.addExecutionHistory(current_running, currentExecutionHistory);
        }
        return context;
    }

    public void stopLoadedRun(){
        if(running){
            running = false;
            current_running = "";
            currentExecutionHistory.setContext(this.interpreter.getExecutionContext());
            historyManager.addExecutionHistory(current_running, currentExecutionHistory);
        }
    }

    public ExecutionContext resumeLoadedRun(){

        ExecutionContext context = this.interpreter.step();
        while(!context.getExit()){
            context = this.interpreter.step();
        }

        currentExecutionHistory.setContext(context);
        historyManager.addExecutionHistory(current_running, currentExecutionHistory);

        running = false;
        current_running = "";
        return context;
    }


    public ArrayList<ExecutionHistory> getHistory(String program_name){

        return historyManager.getExecutionHistory(program_name);
    }








}
