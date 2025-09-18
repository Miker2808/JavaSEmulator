package engine.instruction;

import engine.SProgramView;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.Arrays;

public class JumpEqualFunctionInstruction extends SInstruction {
    private final String argumentLabelName = "JEFunctionLabel";
    private final String argFunctionName = "functionName";
    private final String argFunctionArgumentsName = "functionArguments";
    private String functionName;
    private String functionArguments;
    private String JEFunctionLabel;

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
        return String.format("IF %s = %S GOTO %s", getSVariable(), "PLACEHOLDER", getArgumentLabel());
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

    public void setArgumentLabel(String label) {
        this.JEFunctionLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JEFunctionLabel;
    }



}
