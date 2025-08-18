package engine.instruction;

public class JumpNotZeroInstruction extends SInstruction{
    private String JNZLabel;

    public JumpNotZeroInstruction(SInstruction base) {
        super(base);
        this.setCycles(1);
        this.setArgumentLabel(getArgument("JNZLabel"));
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



}// end of class
