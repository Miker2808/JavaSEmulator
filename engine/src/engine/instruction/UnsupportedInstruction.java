package engine.instruction;

import engine.validator.InstructionValidator;

public class UnsupportedInstruction extends SInstruction {
    public UnsupportedInstruction(SInstruction base) {
        super(base);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
