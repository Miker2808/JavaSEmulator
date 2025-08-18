package engine.instruction;

public class JumpEqualConstantInstruction extends SInstruction {
    private String constantValue;
    private String JEConstantLabel;

    public JumpEqualConstantInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(2);
        this.setArgumentConst(getArgument("constantValue"));
        this.setArgumentLabel(getArgument("JEConstantLabel"));
    }

    public JumpEqualConstantInstruction(InstructionName name, String variable,
                                        String label, String JEConstantLabel, String constantValue) {
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(2);
        this.setDegree(2);
        this.setArgumentConst(constantValue.trim());
        this.setArgumentLabel(JEConstantLabel.trim());
    }

    public void setArgumentConst(String value) {
        this.constantValue = value.trim();
    }

    @Override
    public String getArgumentConst() {
        return constantValue;
    }

    public void setArgumentLabel(String label) {
        this.JEConstantLabel = label.trim();
    }

    @Override
    public String getArgumentLabel() {
        return JEConstantLabel;
    }


    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable,
                getArgumentConst(), getArgumentLabel());
    }
}
