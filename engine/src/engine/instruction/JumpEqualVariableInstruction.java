package engine.instruction;

public class JumpEqualVariableInstruction extends SInstruction {
    private String variableName;
    private String JEVariableLabel;

    public JumpEqualVariableInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentVariable(getArgument("variableName"));
        this.setArgumentLabel(getArgument("JEVariableLabel"));

    }

    public JumpEqualVariableInstruction(InstructionName name, String variable,
                                        String label, String variableName, String JEVariableLabel) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentVariable(variableName.trim());
        this.setArgumentLabel(JEVariableLabel.trim());
    }

    public void setArgumentVariable(String variable) {
        this.variableName = variable.trim();
    }

    @Override
    public String getArgumentVariable() {
        return variableName;
    }

    public void setArgumentLabel(String label) {
        this.JEVariableLabel = label.trim();
    }

    @Override
    public String getArgumentLabel() {
        return JEVariableLabel;
    }


    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable, getArgumentVariable(), getArgumentLabel());
    }

}
