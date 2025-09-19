package engine.instruction;

import engine.SProgramView;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.Arrays;

public class JumpEqualFunctionInstruction extends QuoteInstruction {
    protected final String argumentLabelName = "JEFunctionLabel";
    protected String JEFunctionLabel;

    public JumpEqualFunctionInstruction(SInstruction base) {
        super(base);
        setType("synthetic");

        // cant assign degree and cycles

        setArgumentLabel(getArgument(argumentLabelName));
        setFunctionArguments(getArgument(argFunctionArgumentsName));
        setFunctionName(getArgument(argFunctionName));
    }

    JumpEqualFunctionInstruction(JumpEqualFunctionInstruction other) {
        super(other);
        setType(other.getType());
        setCycles(other.getCycles());
        setDegree(other.getDegree());

        setFunctionName(other.getFunctionName());
        setFunctionArguments(other.getFunctionArguments());
        setArgumentLabel(other.getArgumentLabel());

    }

    @Override
    public JumpEqualFunctionInstruction copy() {
        return new JumpEqualFunctionInstruction(this);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public String getInstructionString() {
        return String.format("IF %s = %s(%s) GOTO %s", getSVariable(), getFunctionName(), getFunctionArguments(), getArgumentLabel());
    }

    public void setArgumentLabel(String label) {
        this.JEFunctionLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JEFunctionLabel;
    }



}
