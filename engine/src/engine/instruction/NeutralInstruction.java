package engine.instruction;

public class NeutralInstruction extends SInstruction {
    public NeutralInstruction(SInstruction base) {
        super(base);

    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, variable);
    }
}
