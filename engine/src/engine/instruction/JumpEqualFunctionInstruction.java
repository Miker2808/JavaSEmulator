package engine.instruction;

import engine.validator.InstructionValidator;

public class JumpEqualFunctionInstruction extends SInstruction {
    public JumpEqualFunctionInstruction(SInstruction base) {
        super(base);
        this.setType("synthetic");
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public JumpEqualFunctionInstruction copy() {
        return new JumpEqualFunctionInstruction(this);
    }
}
