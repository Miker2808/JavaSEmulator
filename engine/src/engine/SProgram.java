package engine;

import engine.functions.SFunctions;
import engine.instruction.InvalidInstructionException;
import engine.instruction.SInstruction;
import engine.validator.InstructionValidator;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sInstructions",
    "sFunctions"
})
@XmlRootElement(name = "S-Program")
public class SProgram implements Serializable, SProgramView {

    @XmlElement(name = "S-Instructions", required = true)
    protected SInstructions sInstructions = new SInstructions();

    @XmlElement(name = "S-Functions")
    protected SFunctions sFunctions;

    @XmlAttribute(name = "name", required = true)
    protected String name;

    public int Size(){
        return sInstructions.size(); // number of instructions in the program
    }

    public void validateProgram() throws InvalidInstructionException {
        if(this.name == null){
            throw new InvalidInstructionException("S-Program name is required");
        }
        // validate program in general
        InstructionValidator validator = new InstructionValidator();
        for(int line = 1; line <= Size(); line++){
            try {
                // validator.validate(this.getInstruction(line)); // old
                getSInstructions().getInstruction(line).validate(validator); // new

            } catch (InvalidInstructionException e) {
                throw new InvalidInstructionException(String.format("Instruction #%d, %s\n", line, e.getMessage()));
            }
        }

        // find that all labels are used
        sInstructions.validateLabelsUsed();
    }

    public SInstructions getSInstructions() {
        sInstructions.updateInstructionsLines();
        return sInstructions;
    }

    @Override
    public SInstructionsView getInstructionsView(){
        sInstructions.updateInstructionsLines();
        return sInstructions;
    }

    public void setSInstructions(SInstructions value) {
        this.sInstructions = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public SFunctions getSFunctions() {
        return sFunctions;
    }

    @Override
    public ProgramType getProgramType() {
        return ProgramType.PROGRAM;
    }

    @Override
    public String getUserString() {
        return "";
    }
}
