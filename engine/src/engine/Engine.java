
package engine;

import engine.execution.ExecutionContext;
import engine.execution.ExecutionResult;
import engine.expander.SProgramExpander;
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
        SProgram loadedProgramtemp;

        // verify path to a file
        XMLValidator.validateXMLFile(path);

        // try to unmarshal to SProgram object
        try {
            JAXBContext context = JAXBContext.newInstance(SProgram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File xmlFile = new File(path);

            // This will throw JAXBException if XML is invalid or doesn't match class
            loadedProgramtemp = (SProgram) unmarshaller.unmarshal(xmlFile);

        } catch (JAXBException e) {
            throw new Exception("Failed to load XML: XML file may be invalid schema-wise");
        }

        loadedProgramtemp.validateProgram();

        // happens only if validateProgram was successful (did not raise an exception)
        loadedProgram = loadedProgramtemp;
        executionHistory.clear();
    }

    // returns loaded program,
    // if no program is loaded returns null
    protected SProgram getLoadedProgram(){
        return loadedProgram;
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
        // TODO: Loop functions

        // Default to main program
        return SProgramExpander.expand(loadedProgram, degree);
    }

    // get in 0 degree same shit as above
    public SProgramView getSelectedProgram(String program_name){

        return loadedProgram;
    }


    // TODO: Make work both for Function and SProgram
    public ExecutionResult runProgram(String program_name, HashMap<String, Integer> input, int degree){
        // TODO: loop Functions

        // default main program
        return new SInterpreter(SProgramExpander.expand(loadedProgram, degree).getSInstructions(), input).run();
    }

    public List<ExecutionHistory> getExecutionHistory(){
        return Collections.unmodifiableList(executionHistory);
    }

    private static File getFile(String path) throws IOException {
        File file = new File(path);

        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Path must be an absolute (global) path: " + path);
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IOException("Path is not a file: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IOException("File cannot be read: " + file.getAbsolutePath());
        }
        return file;
    }

    public void startDebugRun(String program_name, HashMap<String, Integer> input, int degree){
        this.interpreter = new SInterpreter(loadedProgram.sInstructions, input);
        running = true;
    }

    public ExecutionContext stepLoadedRun(){
        if(!running){
            return null;
        }

        return this.interpreter.step();
    }







}
