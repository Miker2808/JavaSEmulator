
package engine;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.InvalidPathException;

public class Engine {
    private SInterpreter mainInterpreter;
    private SProgram loadedProgram;


    public static boolean validatePath(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.canRead();
    }

    public void loadFromXML(String path) throws Exception {
        if (!validatePath(path)) {
            throw new Exception("Couldn't load file, path is invalid");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(SProgram.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File xmlFile = new File(path);

            // This will throw JAXBException if XML is invalid or doesn't match class
            loadedProgram = (SProgram) unmarshaller.unmarshal(xmlFile);
            loadedProgram.validateProgram();

        } catch (JAXBException e) {
            System.err.println("Failed to parse XML: " + e.getMessage());
            // TODO: check what went wrong
        }
    }

    public SProgram getLoadedProgram() {
        return loadedProgram;
    }




}
