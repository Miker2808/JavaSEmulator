package engine.instruction;

import engine.validator.InstructionValidator;

public class JumpEqualVariableInstruction extends SInstruction {
    private String variableName;
    private final String argVarName = "variableName";
    private String JEVariableLabel;
    private final String argLabelName = "JEVariableLabel";

    public JumpEqualVariableInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentVariable(getArgument(argVarName));
        this.setArgumentLabel(getArgument(argLabelName));

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

    public String getArgumentVariableName(){
        return argVarName;
    }

    public String getArgumentLabelName(){
        return argLabelName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

}
