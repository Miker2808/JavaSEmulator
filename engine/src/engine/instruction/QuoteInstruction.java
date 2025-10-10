package engine.instruction;

import engine.SProgramView;
import engine.SVariable.SVariable;
import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.interpreter.SInterpreter;
import engine.validator.FunctionArgumentsValidator;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteInstruction extends SInstruction {

    protected static ArrayList<SProgramView> programViews;

    protected final String argFunctionName = "functionName";
    protected final String argFunctionArgumentsName = "functionArguments";
    protected String functionName;
    protected String functionArguments;

    public static void setProgramViews(ArrayList<SProgramView> programViews) {
        QuoteInstruction.programViews = programViews;
    }

    protected static ArrayList<SProgramView> getProgramViews() {
        return QuoteInstruction.programViews;
    }

    public QuoteInstruction(SInstruction base) {
        super(base);
        setType("synthetic");
        setInstructionName(InstructionName.QUOTE);

        // cant assign degree and cycles

        setFunctionName(getArgument(argFunctionName));
        setFunctionArguments(getArgument(argFunctionArgumentsName));

        this.setCredits(1000);
        this.setGeneration(4);
    }

    public QuoteInstruction(String sVariable, String sLabel, String functionName, String functionArgumentsStr){
        super();
        setInstructionName(InstructionName.QUOTE);
        this.setSVariable(sVariable);
        this.setSLabel(sLabel);
        this.setCycles(getCycles());
        this.setType("synthetic");
        this.setFunctionName(functionName);
        this.setFunctionArguments(functionArgumentsStr);
        this.setDegree(getDegree());

        this.setCredits(1000);
        this.setGeneration(4);
    }

    public QuoteInstruction(QuoteInstruction other) {
        super(other);
        setInstructionName(getInstructionName());
        setCycles(other.getCycles());
        setType(other.getType());
        setDegree(other.getDegree());
        setFunctionName(other.getFunctionName());
        setFunctionArguments(other.getFunctionArguments());

        this.setCredits(other.getCredits());
        this.setGeneration(other.getGeneration());
    }

    @Override
    public QuoteInstruction copy() {
        return new QuoteInstruction(this);
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

    @Override
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
    public String getInstructionString() {
        if(getFunctionArguments().isBlank()){
            return String.format("%s <- (%s)", getSVariable(), getUserFunctionName(getFunctionName()));
        }
        return String.format("%s <- (%s,%s)", getSVariable(), getUserFunctionName(getFunctionName()),
                getUserFunctionArgs(getFunctionArguments()));
    }

    protected String getUserFunctionName(String functionName){
        SProgramView view = getProgramView(functionName);
        if(view.getProgramType() == SProgramView.ProgramType.FUNCTION){
            return view.getUserString();
        }
        return getProgramView(functionName).getName();
    }

    protected String getUserFunctionArgs(String functionArgs){
        HashMap<String, String> functionNameMap = new HashMap<>();
        for(SProgramView programView : QuoteInstruction.programViews){
            if(programView.getProgramType() == SProgramView.ProgramType.FUNCTION) {
                functionNameMap.put(programView.getName(), programView.getUserString());
            }else {
                functionNameMap.put(programView.getName(), programView.getName());
            }
        }

        return replaceKeys(functionArgs, functionNameMap);
    }

    @Override
    public int getDegree(){
        return getFunctionMaxDegree(getFunctionName(), getFunctionArguments());
    }

    @Override
    public int getCycles(){
        return 5;
    }

    @Override
    public void execute(ExecutionContext context){
        ExecutionContext result = runFunction(getFunctionName(), getFunctionArguments(), context);

        //int value = result.getVariables().computeIfAbsent("y", k -> 0);
        //context.getVariables().put(getSVariable(), value);
        int value = result.getVariableValue(new SVariable("y"));
        context.setVariableValue(getSVariableS(), value);

        context.increaseCycles(result.getCycles() + getCycles());
        context.increasePC(1);
    }

    // make a HashMap<String, String> which maps all old variables and labels to new ones.
    // 1. Get all arguments for the instruction and convert into assignment if variable, or quote
    // if a composite function call. assign into new x_i for now.
    // 2. add all function instructions into expanded, after assignment.
    // 3. iterate all instructions and replace all variables and labels with new free variable
    // and label. use hashmap to decided if to request new variable or label, or use already assigned one.
    @Override
    public List<SInstruction> expand(ExpansionContext context, int line) {
        HashMap<String, String> reuse_map = new HashMap<>();
        List<SInstruction> expanded = new ArrayList<>();

        reuse_map.put("y", getSVariable());
        reuse_map.put("", "");

        expanded.add(new NeutralInstruction("y", getSLabel()));

        // 1.

        List<String> arguments = getArgumentsList();
        for(int i = 0; i < arguments.size(); i++){
            String arg = arguments.get(i).trim();

            String label = (i == 0) ? getSLabel() : "";
            if(FunctionArgumentsValidator.isValidVariable(arg)){
                String new_var = context.freshVar();
                reuse_map.put("x" + (i+1), new_var);
                expanded.add(new AssignmentInstruction(new_var, label, arg));
            }
            else if(!arg.isEmpty()){
                String func = FunctionArgumentsValidator.getFunctionName(arg);
                String sub_args = "";
                if(!FunctionArgumentsValidator.functionNoArgs(arg)){
                    sub_args = FunctionArgumentsValidator.getArguments(arg);
                }
                String new_var = context.freshVar();
                reuse_map.put("x" + (i+1), new_var);
                expanded.add(new QuoteInstruction(new_var, label, func, sub_args));
            }
        }

        // 2. + 3.
        expanded.addAll(getCopyFunctionRenamed(getFunctionName(), reuse_map, context));

        // add last neutral instruction to jump into if exit.
        if(reuse_map.containsKey("EXIT")) {
            expanded.add(new NeutralInstruction("y", reuse_map.get("EXIT")));
        }


        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }


    protected HashMap<String, String> getReuseMap(HashMap<String, String> map, String functionName, ExpansionContext context){
        if(map == null) {
            map = new HashMap<>();
            map.put("", "");
        }
        SProgramView functionView = getProgramView(functionName);

        for(SInstruction instr : functionView.getInstructionsView().getAllInstructions()){
            if(!map.containsKey(instr.getSVariable())){
                map.put(instr.getSVariable(), context.freshVar());
            }
            if(!map.containsKey(instr.getSLabel())){
                map.put(instr.getSLabel(), context.freshLabel());
            }
            if(!map.containsKey(instr.getArgumentVariable())){
                map.put(instr.getArgumentVariable(), context.freshVar());
            }
            if(!map.containsKey(instr.getArgumentLabel())){
                map.put(instr.getArgumentLabel(), context.freshLabel());
            }

            if(instr.getInstructionName() == InstructionName.QUOTE || instr.getInstructionName() == InstructionName.JUMP_EQUAL_FUNCTION) {
                List<String> arg_variables;
                if (instr.getInstructionName() == InstructionName.QUOTE) {
                    QuoteInstruction quote = (QuoteInstruction) instr;
                    arg_variables = extractVariablesFromString(quote.getFunctionArguments());
                } else {
                    JumpEqualFunctionInstruction jump_func = (JumpEqualFunctionInstruction) instr;
                    arg_variables = extractVariablesFromString(jump_func.getFunctionArguments());
                }

                for(String var :  arg_variables){
                    if(!map.containsKey(var)){
                        map.put(var, context.freshVar());
                    }
                }
            }
        }

        return map;
    }

    protected ArrayList<SInstruction> getCopyFunctionRenamed(String functionName, HashMap<String, String> map, ExpansionContext context){

        HashMap<String, String> reuse_map = getReuseMap(map, functionName, context);

        SProgramView functionView = getProgramView(functionName);
        ArrayList<SInstruction> copyFunctionRenamed = new ArrayList<>();

        for(SInstruction instr : functionView.getInstructionsView().getAllInstructions()){
            SInstruction instr_copy = instr.copy();
            instr_copy.setSVariable(reuse_map.get(instr.getSVariable()));
            instr_copy.setSLabel(reuse_map.get(instr.getSLabel()));
            instr_copy.setArgumentVariable(reuse_map.get(instr.getArgumentVariable()));
            instr_copy.setArgumentLabel(reuse_map.get(instr.getArgumentLabel()));
            instr_copy.setFunctionArguments(replaceKeys(instr_copy.getFunctionArguments(), reuse_map));
            copyFunctionRenamed.add(instr_copy);
        }

        return copyFunctionRenamed;
    }

    protected SProgramView getProgramView(String name){
        SProgramView program = null; // will always find, as validator verifies it before.
        for(SProgramView programView : QuoteInstruction.programViews){
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



    protected ExecutionContext runFunction(String functionName, String functionArguments, ExecutionContext context){

        List<String> arguments = FunctionArgumentsValidator.splitTopLevel(functionArguments);
        HashMap<String, Integer> input = new HashMap<>();
        for(int i=0; i<arguments.size(); i++){
            String arg = arguments.get(i).trim();

            if(FunctionArgumentsValidator.isValidVariable(arg)){
                //Integer value = context.getVariables().computeIfAbsent(arg, k -> 0);
                int value = context.getVariableValue(new SVariable(arg));
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
                //Integer value = result.getVariables().computeIfAbsent("y", k -> 0);
                int value = result.getVariableValue(new SVariable("y"));
                input.put("x" + (i+1), value);
            }
        }

        SProgramView program = getProgramView(functionName);

        return SInterpreter.staticRun(program.getInstructionsView(), input);

    }

    // returns only x{i>0} variables
    public ArrayList<String> getInputVariablesFromArguments() {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("x[1-9][0-9]*");
        java.util.regex.Matcher matcher = pattern.matcher(getFunctionArguments());
        ArrayList<String> matches = new ArrayList<>();

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    // returns y or z{i>0} or x{i>0} variables
    public static List<String> extractVariablesFromString(String input) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("(y|[xz][1-9][0-9]*)"); // no ^ and $
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }



    protected static String replaceKeys(String input, Map<String, String> replacements) {
        if (input == null || input.isEmpty() || replacements == null || replacements.isEmpty()) {
            return input;
        }

        String result = input;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
