package engine.instruction;

public class IncreaseInstruction extends SInstruction {
    public IncreaseInstruction(SInstruction base) {
        super(base);
        // any additiona initialization
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s - 1", variable, variable);
    }

}
