package engine.instruction;

public class GotoLabelInstruction extends SInstruction {
    private String gotoLabel;

    public GotoLabelInstruction(SInstruction base) {
        super(base);

        this.setCycles(1);
        this.setDegree(1);
        this.setArgumentLabel(getArgument("gotoLabel"));
    }

    public GotoLabelInstruction(InstructionName name, String variable, String label, String gotoLabel) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setArgumentLabel(gotoLabel);
        this.setCycles(1);
        this.setDegree(1);
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("GOTO %s", getArgumentLabel());
    }

    public void setArgumentLabel(String label) {
        this.gotoLabel = label.trim();
    }

    @Override
    public String getArgumentLabel(){
        return gotoLabel;
    }
}
