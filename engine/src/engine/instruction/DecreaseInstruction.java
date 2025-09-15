package engine.instruction;

import engine.execution.ExecutionContext;
import engine.validator.InstructionValidator;

public class DecreaseInstruction extends SInstruction {
    public DecreaseInstruction(SInstruction base) {
        super(base);
        this.setCycles(1);
        this.setType("basic");
    }

    public DecreaseInstruction( String variable, String label){
        super();
        this.setInstructionName(InstructionName.DECREASE);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setType("basic");

    }

    public DecreaseInstruction(DecreaseInstruction other){
        super(other);
        this.setCycles(other.getCycles());
        this.setType(other.getType());
        this.setDegree(other.getDegree());
    }

    @Override
    public DecreaseInstruction copy() {
        return new DecreaseInstruction(this);
    }

    @Override
    public String getInstructionString() {
        return String.format("%s <- %s - 1", getSVariable(), getSVariable());
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        int value = context.getVariables().computeIfAbsent(var, k -> 0) - 1;

        if(value < 0){
            value = 0;
        }

        context.getVariables().put(var, value);
        context.increaseCycles(getCycles());
        context.increasePC(1);
    }

}
