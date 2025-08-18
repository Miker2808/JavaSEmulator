package engine.instruction;

public class ZeroVariableInstruction extends  SInstruction {
    public ZeroVariableInstruction(SInstruction base) {
        super(base);

    }
    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- 0", variable);
    }
}
