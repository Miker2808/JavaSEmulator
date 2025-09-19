
package engine;

import engine.execution.ExecutionContext;
import engine.expander.SProgramExpander;
import engine.functions.SFunction;
import engine.functions.SFunctions;
import engine.instruction.SInstruction;
import engine.interpreter.SInterpreter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.*;


public class Engine implements Serializable{
    private SProgram loadedProgram = null;
    private ArrayList<ExecutionHistory> executionHistory = new ArrayList<>();
    private SInterpreter interpreter;
    private boolean running = false;

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
        updateProgramViews(loadedProgramTemp);
        // happens only if validateProgram was successful (did not raise an exception)
        loadedProgram = loadedProgramTemp;
        executionHistory.clear();
    }

    // populates Quote instruction with references to functions and main program
    private void updateProgramViews(SProgram program) {
        ArrayList<SProgramView> programViews = new ArrayList<>();
        programViews.add(program);
        programViews.addAll(program.getSFunctions().getSFunction());
        SInstruction.setProgramViews(programViews);
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
    public ArrayList<String> getProgramNames(){
        ArrayList<String> programNames = new ArrayList<>();
        programNames.add(loadedProgram.getName());
        SFunctions functions = loadedProgram.getSFunctions();
        for(SFunction func : functions.getSFunction()){
            programNames.add(func.getName());
        }
        return programNames;
    }

    public ExecutionContext runProgram(String program_name, HashMap<String, Integer> input, int degree){
        // default main program
        SProgramView expanded = getExpandedProgram(program_name, degree);
        return SInterpreter.staticRun(expanded.getInstructionsView(), input);
    }

    public List<ExecutionHistory> getExecutionHistory(){
        return Collections.unmodifiableList(executionHistory);
    }


    public void startDebugRun(String program_name, HashMap<String, Integer> input, int degree){
        SProgramView expanded = getExpandedProgram(program_name, degree);
        this.interpreter = new SInterpreter(expanded.getInstructionsView(), input);
        running = true;
    }

    public ExecutionContext stepLoadedRun(){
        ExecutionContext context = this.interpreter.step();
        if(context.getExit()){
            running = false;
            // TODO: add history
        }
        return context;
    }

    public ExecutionContext resumeLoadedRun(){

        ExecutionContext context = this.interpreter.step();
        while(!context.getExit()){
            context = this.interpreter.step();
        }
        running = false;
        // TODO: add history
        return context;
    }







}
