package engine.instruction;

import engine.SVariable.SVariable;
import engine.execution.ExecutionContext;
import engine.validator.InstructionValidator;

public class IncreaseInstruction extends SInstruction {
    public IncreaseInstruction(SInstruction base) {
        super(base);
        // any additional initialization
        this.setCycles(1);
        this.setType("basic");
    }

    public IncreaseInstruction( String variable, String label){
        super();
        this.setInstructionName(InstructionName.INCREASE);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setType("basic");
    }

    public IncreaseInstruction(IncreaseInstruction other){
        super(other);
        this.setCycles(other.getCycles());
        this.setType(other.getType());
        this.setDegree(other.getDegree());
    }

    @Override
    public IncreaseInstruction copy() {
        return new IncreaseInstruction(this);
    }

    @Override
    public String getInstructionString() {
        return String.format("%s <- %s + 1", getSVariable(), getSVariable());
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public void execute(ExecutionContext context){
        //String var = this.getSVariable();
        //int value = context.getVariables().computeIfAbsent(var, k -> 0) + 1;
        //context.getVariables().put(var, value);

        SVariable var = this.getSVariableS();
        int value = context.getVariableValue(var) + 1;
        context.setVariableValue(var, value);

        context.increaseCycles(getCycles());
        context.increasePC(1);

    }


}
