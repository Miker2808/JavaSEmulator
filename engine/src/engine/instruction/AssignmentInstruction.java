package engine.instruction;

import engine.validator.InstructionValidator;

public class AssignmentInstruction extends SInstruction {
    private String assignedVariable;
    private final String argName = "assignedVariable";

    public AssignmentInstruction(SInstruction base) {
        super(base);
        this.setArgumentVariable(getArgument(argName));
        this.setCycles(4);
        this.setDegree(2);
    }

    public AssignmentInstruction(InstructionName name, String variable, String label, String assignedVariable) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setArgumentVariable(assignedVariable);
        this.setCycles(4);
        this.setDegree(2);
    }

    public void setArgumentVariable(String variable) {
        this.assignedVariable = variable.trim();
    }

    @Override
    public String getArgumentVariable() {
        return assignedVariable;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, getArgumentVariable());
    }

    public String getArgumentVariableName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
