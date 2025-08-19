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

    public JumpNotZeroInstruction(InstructionName name, String variable, String label){
        super();
        this.setInstructionName(name);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setArgumentLabel(JNZLabel);
    }

    public void setArgumentLabel(String label){
        this.JNZLabel = label.trim();
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

    /*
    @Override
    protected void validateExtra() throws InvalidInstructionException {
        if(this.getArgumentLabel().isEmpty()){
            throw new InvalidInstructionException(
                    String.format("required %s argument is empty or missing", argName));
        }
    }
    */

}// end of class
