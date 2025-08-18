package engine.instruction;

public class GotoLabelInstruction extends SInstruction {
    public GotoLabelInstruction(SInstruction base) {
        super(base);

    }
    @Override
    protected String getOperationString(String variable) {
        return String.format("GOTO %s", getArgument("gotoLabel"));
    }
}
