package engine.instruction;

public class ZeroVariableInstruction extends  SInstruction {

    public ZeroVariableInstruction(SInstruction base) {
        super(base);
        // any additional initialization
        this.setCycles(1);
        this.setDegree(1);
    }

    public ZeroVariableInstruction(InstructionName name, String variable, String label){
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setDegree(1);
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- 0", variable);
    }

}
