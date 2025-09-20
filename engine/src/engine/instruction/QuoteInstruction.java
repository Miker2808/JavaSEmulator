package engine.instruction;

import engine.SInstructions;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.interpreter.SInterpreter;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QuoteInstruction extends SInstruction {

    protected final String argFunctionName = "functionName";
    protected final String argFunctionArgumentsName = "functionArguments";
    protected String functionName;
    protected String functionArguments;

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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName.trim();
    }

    protected String getFunctionUserName(){
        return getProgramView(getFunctionName()).getUserString();
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
        return String.format("%s <- (%s,%s)", getSVariable(), getFunctionName(), getFunctionArguments());
    }

    @Override
    public List<SInstruction> expand(ExpansionContext context, int line) {
        List<SInstruction> expanded = new ArrayList<>();

        // TODO: Implement

        return expanded;
    }

    protected static boolean isVariable(String str){
        return str.matches("^(y|([xz][1-9][0-9]*))$");
    }

    protected static boolean isEnclosedInParenthesis (String str){
        return str.matches("^\\(.*?\\)$");
    }

    protected static String removeParentheses(String s) {
        if (isEnclosedInParenthesis(s)) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }


    protected SProgramView getProgramView(String name){
        SProgramView program = null; // will always find, as validator verifies it before.
        for(SProgramView programView : SInstruction.programViews){
            if(programView.getName().equals(name)){
                program = programView;
            }
        }
        return program;
    }


    protected int getFunctionMaxDegree(String functionName, String functionArguments){
        int maxDegree = 0;

        ArrayList<String> arguments = getArgumentsList();
        for(int i=0; i<arguments.size(); i++){

            if(isEnclosedInParenthesis(arguments.get(i))){
                String inner = arguments.get(i).substring(1, arguments.get(i).length() - 1);
                int firstComma = inner.indexOf(',');
                String innerName = inner.substring(0, firstComma);
                String innerArgs = inner.substring(firstComma + 1);

                // Get cycles of composition calls.
                maxDegree = Math.max(getFunctionMaxDegree(innerName, innerArgs), maxDegree);
            }
        }

        SProgramView program = getProgramView(functionName);

        for(int line=1; line<=program.getInstructionsView().size(); line++){
            maxDegree = Math.max(program.getInstructionsView().getInstruction(line).getCycles(), maxDegree);
        }

        return maxDegree + 1;
    }

    protected int getFunctionCycles(String functionName, String functionArguments){
        int cycles = 0;

        /*
        ArrayList<String> arguments = getArgumentsList();
        for(int i=0; i<arguments.size(); i++){

            if(isEnclosedInParenthesis(arguments.get(i))){
                String inner = arguments.get(i).substring(1, arguments.get(i).length() - 1);
                int firstComma = inner.indexOf(',');
                String innerName = inner.substring(0, firstComma);
                String innerArgs = inner.substring(firstComma + 1);

                // Get cycles of composition calls.
                cycles += getFunctionCycles(innerName, innerArgs);
            }
        }
         */

        SProgramView program = getProgramView(functionName);

        for(int line=1; line<=program.getInstructionsView().size(); line++){
            cycles += program.getInstructionsView().getInstruction(line).getCycles();
        }

        return cycles;
    }

    public int getCycles(){
        return getFunctionCycles(getFunctionName(), getFunctionArguments()) + 5;
    }

    protected ExecutionContext runFunction(String functionName, String functionArguments, ExecutionContext context){

        ArrayList<String> arguments = getArgumentsList();
        HashMap<String, Integer> input = new HashMap<>();
        for(int i=0; i<arguments.size(); i++){
            if(isVariable(arguments.get(i))){
                String variable = arguments.get(i);
                Integer value = context.getVariables().get(variable);
                input.putIfAbsent("x" + i, value);
            }
            else{
                String inner = arguments.get(i).substring(1, arguments.get(i).length() - 1);
                int firstComma = inner.indexOf(',');
                String innerName = inner.substring(0, firstComma);
                String innerArgs = inner.substring(firstComma + 1);
                ExecutionContext result = runFunction(innerName, innerArgs, context);
                Integer value = result.getVariables().get("y");
                input.putIfAbsent("x" + i, value);
            }
        }

        SProgramView program = getProgramView(functionName);

        return SInterpreter.staticRun(program.getInstructionsView(), input);

    }

    @Override
    public void execute(ExecutionContext context){
        ExecutionContext result = runFunction(functionName, functionArguments, context);

        String var = this.getSVariable();
        int value =  context.getVariables().get("y");
        context.getVariables().put(var, value);
        context.increaseCycles(getCycles());
        context.increasePC(1);

    }


    public ArrayList<String> getInputVariablesFromArguments() {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("x[1-9][0-9]*");
        java.util.regex.Matcher matcher = pattern.matcher(getFunctionArguments());
        ArrayList<String> matches = new ArrayList<>();

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }



}
