package engine.instruction;

import engine.execution.ExecutionContext;
import engine.validator.InstructionValidator;

public class JumpNotZeroInstruction extends SInstruction{
    private String JNZLabel;
    private final String argName = "JNZLabel";

    public JumpNotZeroInstruction(SInstruction base) {
        super(base);
        this.setCycles(1);
        this.setArgumentLabel(getArgument(argName));
        this.setType("basic");
    }

    public JumpNotZeroInstruction(String variable, String label, String argLabel){
        super();
        this.setInstructionName(InstructionName.JUMP_NOT_ZERO);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setArgumentLabel(argLabel);
        this.setType("basic");
    }

    public JumpNotZeroInstruction(JumpNotZeroInstruction other){
        super(other);
        this.setCycles(other.getCycles());
        this.setArgumentLabel(other.getArgumentLabel());
        this.setType(other.getType());
    }

    @Override
    public JumpNotZeroInstruction copy() {
        return new JumpNotZeroInstruction(this);
    }

    public void setArgumentLabel(String label){
        this.JNZLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel(){
        return JNZLabel;
    }

    @Override
    public String getInstructionString() {
       return String.format("IF %s != 0 GOTO %s", getSVariable(), getArgumentLabel());
    }

    public String getArgumentLabelName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        String argLabel = this.getArgumentLabel();
        int value = context.getVariables().computeIfAbsent(var, k -> 0);

        if(value != 0){
            if(argLabel.equals("EXIT")){
                context.setExit(true);
            }
            else{
                context.setPC(context.getLabelLine(argLabel));
            }
        }
        else{
            context.increasePC(1);
        }

        context.increaseCycles(getCycles());

    }

}// end of class
