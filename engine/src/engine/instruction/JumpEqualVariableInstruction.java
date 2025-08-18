package engine.instruction;

public class JumpEqualVariableInstruction extends SInstruction {
    public JumpEqualVariableInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable, getArgument("variableName"), getArgument("JEVariableLabel"));
    }
}
