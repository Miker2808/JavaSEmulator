package engine.instruction;

import engine.validator.InstructionValidator;

public class JumpEqualFunctionInstruction extends SInstruction {
    public JumpEqualFunctionInstruction(SInstruction base) {
        super(base);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
