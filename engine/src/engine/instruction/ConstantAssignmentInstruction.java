package engine.instruction;

public class ConstantAssignmentInstruction extends SInstruction {
    public ConstantAssignmentInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {

        return String.format("%s <- %s", variable, getArgument("constantValue"));

    }

}
