package engine.instruction;

import engine.SInstructions;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.interpreter.SInterpreter;
import engine.validator.FunctionArgumentsValidator;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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

    public List<String> getArgumentsList(){
        return FunctionArgumentsValidator.splitTopLevel(getFunctionArguments());
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

        List<String> arguments = FunctionArgumentsValidator.splitTopLevel(functionArguments);
        for (String arg : arguments) {
            // if of type "(Func1)"
            if (FunctionArgumentsValidator.functionNoArgs(arg)) {
                // Get cycles of composition calls.
                String func = arg.substring(1, arg.length()-1);
                maxDegree = Math.max(getFunctionMaxDegree(func, ""), maxDegree);
            }
            // if of type "(Func1,x1,x2)"
            else if (FunctionArgumentsValidator.enclosedInParenthesis(arg)) {
                String func = FunctionArgumentsValidator.getFunctionName(arg);
                String sub_args = FunctionArgumentsValidator.getArguments(arg);
                maxDegree = Math.max(getFunctionMaxDegree(func, sub_args), maxDegree);
            }
        }

        SProgramView program = getProgramView(functionName);

        for(int line=1; line<=program.getInstructionsView().size(); line++){
            maxDegree = Math.max(program.getInstructionsView().getInstruction(line).getDegree(), maxDegree);
        }

        return maxDegree + 1;
    }

    public int getDegree(){
        return getFunctionMaxDegree(getFunctionName(), getFunctionArguments());
    }

    protected int getFunctionCycles(String functionName, String functionArguments){
        int cycles = 0;

        List<String> arguments = FunctionArgumentsValidator.splitTopLevel(functionArguments);
        for (String arg : arguments) {
            // if of type "(Func1)"
            if (FunctionArgumentsValidator.functionNoArgs(arg)) {
                // Get cycles of composition calls.
                String func = arg.substring(1, arg.length()-1);
                cycles += getFunctionCycles(func, "");
            }
            // if of type "(Func1,x1,x2)"
            else if (FunctionArgumentsValidator.enclosedInParenthesis(arg)) {
                String func = FunctionArgumentsValidator.getFunctionName(arg);
                String sub_args = FunctionArgumentsValidator.getArguments(arg);
                cycles += getFunctionCycles(func, sub_args);
            }
        }

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

        List<String> arguments = FunctionArgumentsValidator.splitTopLevel(functionArguments);
        HashMap<String, Integer> input = new HashMap<>();
        for(int i=0; i<arguments.size(); i++){
            String arg = arguments.get(i).trim();

            if(isVariable(arg)){
                Integer value = context.getVariables().get(arg);
                input.put("x" + (i+1), value);
            }
            else if(!arg.isEmpty()){
                String func;
                String sub_args = "";

                if(FunctionArgumentsValidator.functionNoArgs(arg)){
                    func = arg.substring(1, arg.length() - 1);
                }
                else{
                    func = FunctionArgumentsValidator.getFunctionName(arg);
                    sub_args = FunctionArgumentsValidator.getArguments(arg);
                }

                ExecutionContext result = runFunction(func, sub_args, context);
                Integer value = result.getVariables().get("y");
                input.put("x" + (i+1), value);
            }
        }

        SProgramView program = getProgramView(functionName);

        return SInterpreter.staticRun(program.getInstructionsView(), input);

    }

    @Override
    public void execute(ExecutionContext context){
        ExecutionContext result = runFunction(getFunctionName(), getFunctionArguments(), context);

        String var = this.getSVariable();
        int value = result.getVariables().get("y");
        context.getVariables().put(var, value);
        context.increaseCycles(result.getCycles() + 5);
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
