package engine.instruction;

public class JumpZeroInstruction extends SInstruction {
    public JumpZeroInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = 0 GOTO %s", variable, getArgument("JZLabel"));
    }
}
