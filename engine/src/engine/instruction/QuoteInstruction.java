package engine.instruction;

import engine.SProgramView;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.Arrays;

public class QuoteInstruction extends SInstruction {

    private final String argFunctionName = "functionName";
    private final String argFunctionArgumentsName = "functionArguments";
    private String functionName;
    private String functionArguments;

    public QuoteInstruction(SInstruction base) {
        super(base);
        setType("synthetic");

        // cant assign degree and cycles

        setFunctionName(getArgument(argFunctionName));
        setFunctionArguments(getArgument(argFunctionArgumentsName));
    }

    public QuoteInstruction(QuoteInstruction other) {
        super(other);
        setCycles(other.getCycles());
        setType(other.getType());
        setDegree(other.getDegree());
        setFunctionName(other.getFunctionName());
        setFunctionArguments(other.getFunctionArguments());
    }

    private int calcDegree(){
        int degree = 0;

        // TODO: implement

        return degree;
    }

    private int calcCycles(){
        int cycles = 0;

        // TODO: implement

        return cycles + 5;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName.trim();
    }

    public String getFunctionArguments(){
        return functionArguments;
    }

    public void setFunctionArguments(String functionArguments) {
        this.functionArguments = functionArguments.trim();
    }

    public ArrayList<String> getArgumentsList(){
        ArrayList<String> arguments = new ArrayList<>();
        if(!functionArguments.isBlank()){
            arguments = new ArrayList<>(Arrays.asList(functionArguments.split(",")));
        }
        return arguments;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public QuoteInstruction copy() {
        return new QuoteInstruction(this);
    }

    @Override
    public String getInstructionString() {
        return String.format("%s <- %s(%s)", getSVariable(), functionName, functionArguments);
    }

}
