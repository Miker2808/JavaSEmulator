
package engine;

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
    public SProgram getLoadedProgram(){
        return loadedProgram;
    }


    public boolean isProgramLoaded(){
        return loadedProgram != null;
    }

    public ExecutionResult runProgram(SProgram program, HashMap<String, Integer> input, int degree){
        SProgram expanded = expandProgram(program, degree);

        ExecutionResult result = new SInterpreter(expanded.getSInstructions(), input).run();
        // TODO: Modifiy execution history (it will need changes anyway)
        //executionHistory.add(new ExecutionHistory(degree,
        //        input,
        //        result.getVariables().get("y"),
        //        result.getCycles()));

        return result;
    }

    public SProgram expandProgram(SProgram program, int degree){
        return SProgramExpander.expand(program, degree);
    }

    public List<ExecutionHistory> getExecutionHistory(){
        return Collections.unmodifiableList(executionHistory);
    }

    // saves instance from engine to specificed path, raises execpetion if
    // the path is invalid format.
    // accepts only global (absolute) path and a file ends with .semulator
    public void saveInstance(String path) throws Exception {
        if (!path.endsWith(".semulator")) {
            throw new IllegalArgumentException("Path must end with .semulator");
        }

        File file = new File(path);

        // Reject relative paths
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Path must be an absolute (global) path: " + path);
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + parent.getAbsolutePath());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
        catch(Exception e) {
            throw new Exception("Failed to save instance: " + e.getMessage());
        }
    }

    // loads Engine instance from file, accepts only files ending with .semulator
    // raises exception on invalid path format.
    // returns instance of Engine
    public static Engine loadInstance(String path) throws Exception {
        if (!path.endsWith(".semulator")) {
            throw new IllegalArgumentException("Path must end with .semulator");
        }

        File file = getFile(path);

        Engine engine;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            engine =  (Engine) ois.readObject();
        }
        catch (Exception e) {
            throw new Exception("Failed to load instance");
        }

        return engine;
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
        // TODO: give different SInstructions depending on name, for now default on main program

        //this.interpreter = new SInterpreter()
    }

    public void startNewInterpreter(SInstructions instructions, HashMap<String, Integer> input){
        this.interpreter = new SInterpreter(instructions, input);

    }

    public SInterpreter getInterpreter(){
        return interpreter;
    }




}
