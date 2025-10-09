package engine;

import engine.functions.SFunction;
import engine.functions.SFunctions;
import engine.instruction.InvalidInstructionException;
import engine.validator.InstructionValidator;
import engine.validator.InvalidFunctionException;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    @XmlTransient
    protected String uploader = "";

    public void validateProgram(HashSet<String> availableFunctions) throws Exception {
        if(getName() == null || getName().isEmpty()){
            throw new InvalidInstructionException("S-Program name is required");
        }
        validateInstructions(getSInstructions(), availableFunctions);
        SFunctions functions = getSFunctions();
        for (SFunction function : functions.getSFunction()) {
            if (function.getName() == null || function.getName().isEmpty()) {
                throw new InvalidFunctionException("S-Function name is required");
            }
            validateInstructions(function.getSInstructions(), availableFunctions);
        }

    }

    protected void validateInstructions(SInstructions instructions, HashSet<String> availableFunctions) throws InvalidInstructionException {
        // validate instructions in general
        InstructionValidator validator = new InstructionValidator(availableFunctions);
        for(int line = 1; line <= instructions.size(); line++){
            try {

                instructions.getInstruction(line).validate(validator);

            } catch (InvalidInstructionException e) {
                throw new InvalidInstructionException(String.format("Instruction #%d, %s\n", line, e.getMessage()));
            }
        }

        // find that all labels are used
        instructions.validateLabelsUsed();
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
        if(sFunctions == null){
            sFunctions = new SFunctions();
        }
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

    public String getUploader(){
        return uploader;
    }

    public void setUploader(String value){
        this.uploader = value;
        SFunctions functions = getSFunctions();
        for (SFunction function : functions.getSFunction()) {
            function.setUploader(value);
        }
    }

}
