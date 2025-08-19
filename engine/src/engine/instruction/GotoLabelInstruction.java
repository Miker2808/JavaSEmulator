package engine.instruction;

import engine.validator.InstructionValidator;

public class GotoLabelInstruction extends SInstruction {
    private String gotoLabel;
    private final String argName = "gotoLabel";

    public GotoLabelInstruction(SInstruction base) {
        super(base);

        this.setCycles(1);
        this.setDegree(1);
        this.setArgumentLabel(getArgument(argName));
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

    public String getArgumentLabelName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }
}
