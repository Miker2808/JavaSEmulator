package engine.instruction;

public class DecreaseInstruction extends SInstruction {
    public DecreaseInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s - 1", variable, variable);
    }
}
