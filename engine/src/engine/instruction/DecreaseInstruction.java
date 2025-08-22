package engine.instruction;

import engine.execution.ExecutionContext;
import engine.validator.InstructionValidator;

public class DecreaseInstruction extends SInstruction {
    public DecreaseInstruction(SInstruction base) {
        super(base);
        this.setCycles(1);
    }

    public DecreaseInstruction( String variable, String label){
        super();
        this.setInstructionName(InstructionName.DECREASE);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s - 1", variable, variable);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        int value = context.getVariables().computeIfAbsent(var, k -> 0) - 1;
        context.getVariables().put(var, value);
        context.increaseCycles(getCycles());
        context.increasePC(1);
    }

}
