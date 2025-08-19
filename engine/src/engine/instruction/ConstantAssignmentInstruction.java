package engine.instruction;

import engine.validator.InstructionValidator;

public class ConstantAssignmentInstruction extends SInstruction {
    private String constantValue;
    private final String argName = "constantValue";

    public ConstantAssignmentInstruction(SInstruction base) {
        super(base);
        this.setCycles(4);
        this.setDegree(2);
        this.setArgumentConst(getArgument(argName));
    }

    public ConstantAssignmentInstruction(InstructionName name, String variable, String label, int constantValue) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(4);
        this.setDegree(2);
        setArgumentConst(String.format("%d",constantValue));
    }

    public void setArgumentConst(String value) {
        this.constantValue = value.trim();
    }

    @Override
    public String getArgumentConst() {
        return constantValue;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, getArgumentConst());
    }

    public String getArgumentConstName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

}
