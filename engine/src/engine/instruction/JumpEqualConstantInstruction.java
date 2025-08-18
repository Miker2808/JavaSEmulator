package engine.instruction;

public class JumpEqualConstantInstruction extends SInstruction {
    public JumpEqualConstantInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable, getArgument("constantValue"), getArgument("JEConstantLabel"));
    }
}
