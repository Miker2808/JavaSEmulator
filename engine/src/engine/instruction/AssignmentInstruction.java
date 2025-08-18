package engine.instruction;

public class AssignmentInstruction extends SInstruction {
    public AssignmentInstruction(SInstruction base) {
        super(base);
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, getArgument("assignedVariable"));
    }
}
