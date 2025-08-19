package engine.instruction;

import engine.validator.InstructionValidator;

public class NeutralInstruction extends SInstruction {

    public NeutralInstruction(SInstruction base) {
        super(base);
    }

    public NeutralInstruction(InstructionName name, String variable, String label){
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, variable);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
