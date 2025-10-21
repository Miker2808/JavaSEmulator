
package engine;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.io.Serializable;


public class Engine implements Serializable{

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

}
