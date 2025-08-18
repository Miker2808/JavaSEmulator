package engine.instruction;

public class JumpZeroInstruction extends SInstruction {
    private String JZLabel;

    public JumpZeroInstruction(SInstruction base) {
        super(base);
        this.setArgumentLabel(getArgument("JZLabel"));
        this.setCycles(2);
        this.setDegree(2);
    }

    public JumpZeroInstruction(InstructionName name, String variable, String label, String JZLabel) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setArgumentLabel(JZLabel);
        this.setCycles(1);
        this.setDegree(2);
    }

    public void setArgumentLabel(String label) {
        this.JZLabel = label.trim();
    }

    @Override
    public String getArgumentLabel() {
        return JZLabel;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = 0 GOTO %s", variable, getArgumentLabel());
    }

}
