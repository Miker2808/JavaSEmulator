
package engine;

import engine.execution.ExecutionResult;
import engine.expander.SProgramExpander;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class Engine {
    private SInterpreter mainInterpreter;
    private SProgram loadedProgram = null;
    private ArrayList<ExecutionHistory> executionHistory = new ArrayList<>();

    public void loadFromXML(String path) throws Exception {
        SProgram loadedProgramtemp = new SProgram();

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
        loadedProgram = loadedProgramtemp;

    }

    // returns loaded program,
    // if no program is loaded returns null
    public SProgram getLoadedProgram(){
        return loadedProgram;
    }

    public boolean isProgramLoaded(){
        return loadedProgram != null;
    }

    public ExecutionResult emulateProgram(SProgram program, ArrayList<Integer> input){
        return new SInterpreter(program, input).run();
    }

    public SProgram expandProgram(SProgram program, int degree){

        return SProgramExpander.expand(program, degree);
    }

    public ArrayList<ExecutionHistory> getExecutionHistory(){
        return executionHistory;
    }




}
