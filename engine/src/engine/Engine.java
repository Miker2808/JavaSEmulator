
package engine;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
public class Engine {
    private SInterpreter mainInterpreter;
    private SProgram loadedProgram;


    public static boolean validatePath(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.canRead();
    }

    public boolean loadFromXML(String path) {
        if (!validatePath(path)) {
            System.err.println("File does not exist or cannot be read: " + path);
            return false;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(SProgram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File xmlFile = new File(path);

            // This will throw JAXBException if XML is invalid or doesn't match class
            loadedProgram = (SProgram) unmarshaller.unmarshal(xmlFile);
            return true;

        } catch (JAXBException e) {
            System.err.println("Failed to parse XML: " + e.getMessage());
            // TODO: check what went wrong
        }
        return false;
    }


    public Engine(){
        Interpreter mainInterpreter = null;
        Program loadedProgram = null;
    }

    public SProgram getLoadedProgram() {
        return loadedProgram;
    }




}
