package engine.instruction;

import engine.validator.InstructionValidator;

public class JumpEqualConstantInstruction extends SInstruction {
    private String constantValue;
    private final String argConstName = "constantValue";
    private String JEConstantLabel;
    private final String argLabelName = "JEConstantLabel";

    public JumpEqualConstantInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(2);
        this.setArgumentConst(getArgument(argConstName));
        this.setArgumentLabel(getArgument(argLabelName));
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

    public String getArgumentConstName(){
        return argConstName;
    }

    public String getArgumentLabelName(){
        return argLabelName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
