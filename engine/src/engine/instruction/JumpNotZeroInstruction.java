package engine.instruction;

import engine.validator.InstructionValidator;

public class JumpNotZeroInstruction extends SInstruction{
    private String JNZLabel;
    private final String argName = "JNZLabel";

    public JumpNotZeroInstruction(SInstruction base) {
        super(base);
        this.setCycles(1);
        this.setArgumentLabel(getArgument(argName));
    }

    public JumpNotZeroInstruction(String variable, String label, String argLabel){
        super();
        this.setInstructionName(InstructionName.JUMP_NOT_ZERO);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setArgumentLabel(argLabel);
    }

    public void setArgumentLabel(String label){
        this.JNZLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel(){
        return JNZLabel;
    }

    @Override
    protected String getOperationString(String variable) {
       return String.format("IF %s != 0 GOTO %s", variable, getArgumentLabel());
    }

    public String getArgumentLabelName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

}// end of class
