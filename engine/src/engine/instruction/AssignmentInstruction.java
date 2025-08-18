package engine.instruction;

public class AssignmentInstruction extends SInstruction {
    private String assignedVariable;

    public AssignmentInstruction(SInstruction base) {
        super(base);
        this.setArgumentVariable(getArgument("assignedVariable"));
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
}
