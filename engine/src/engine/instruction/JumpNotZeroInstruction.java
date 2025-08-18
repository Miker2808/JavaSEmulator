package engine.instruction;

public class JumpNotZeroInstruction extends SInstruction{


    public JumpNotZeroInstruction(SInstruction base) {
        super(base);
    }

    @Override
    protected String getOperationString(String variable) {
       return String.format("IF %s != 0 GOTO %s", variable, getArgument("JNZLabel"));
    }


}// end of class
