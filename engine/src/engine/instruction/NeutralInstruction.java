package engine.instruction;

import engine.execution.ExecutionContext;
import engine.validator.InstructionValidator;

public class NeutralInstruction extends SInstruction {

    public NeutralInstruction(SInstruction base) {
        super(base);
        this.setType("basic");
    }

    public NeutralInstruction(String variable, String label){
        super();
        this.setInstructionName(InstructionName.NEUTRAL);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setType("basic");
    }

    public NeutralInstruction(NeutralInstruction other) {
        super(other);
        this.setCycles(other.getCycles());
        this.setType(other.getType());
        this.setDegree(other.getDegree());
    }

    @Override
    public NeutralInstruction copy() {
        return new NeutralInstruction(this);
    }

    @Override
    public String getInstructionString() {
        return String.format("%s <- %s", getSVariable(), getSVariable());
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public void execute(ExecutionContext context){
        context.setPC(context.getPC() + 1);
    }
}
