package engine.instruction;

import engine.validator.InstructionValidator;

public class QuoteInstruction extends SInstruction {
    public QuoteInstruction(SInstruction base) {
        super(base);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

}
