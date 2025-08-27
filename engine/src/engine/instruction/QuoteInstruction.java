package engine.instruction;

import engine.validator.InstructionValidator;

public class QuoteInstruction extends SInstruction {
    public QuoteInstruction(SInstruction base) {
        super(base);
        this.setType("synthetic");
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

}
