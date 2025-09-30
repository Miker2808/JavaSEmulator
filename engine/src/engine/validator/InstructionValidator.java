package engine.validator;

import engine.instruction.*;

import java.util.List;

public class InstructionValidator {
    protected List<String> validFunctions;
    protected FunctionArgumentsValidator functionArgumentsValidator;

    public InstructionValidator(List<String> validFunctions){
        this.validFunctions = validFunctions;
        functionArgumentsValidator = new FunctionArgumentsValidator(validFunctions);
    }


    public void validate(SInstruction instr) throws InvalidInstructionException {
        InstructionName name = instr.getInstructionName();
        String label = instr.getSLabel();

        isValidName(name);

        validateType(instr);

        validateSVariable(instr);

        if(!label.isEmpty() && !isValidLabelFormat(label)) {
            throw new  InvalidInstructionException("invalid label format: " + label);
        }
    }

    public void validate(JumpNotZeroInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String JNZLabel = instruction.getArgumentLabel();
        String argName = instruction.getArgumentLabelName();
        validateArgumentNotEmpty(argName, JNZLabel);
        validateLabelArgument(argName, JNZLabel);
    }

    public void validate(GotoLabelInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String gotoLabel = instruction.getArgumentLabel();
        String argName = instruction.getArgumentLabelName();
        validateLabelArgument(argName, gotoLabel);
    }

    /** validate AssignmentInstruction */
    public void validate(AssignmentInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String assignedVariable = instruction.getArgumentVariable();
        String varName = instruction.getArgumentVariableName();
        validateVariableArgument(varName, assignedVariable);
    }


    public void validate(ConstantAssignmentInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String constantValue = instruction.getArgumentConst();
        String constName = instruction.getArgumentConstName();

        validateConstantValueArgument(constName, constantValue);
    }

    public void validate(JumpZeroInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String JZLabel = instruction.getArgumentLabel();
        String argName = instruction.getArgumentLabelName();

        validateLabelArgument(argName, JZLabel);
    }

    public void validate(JumpEqualConstantInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String JEConstantLabel = instruction.getArgumentLabel();
        String argLabelName = instruction.getArgumentLabelName();
        validateArgumentNotEmpty(argLabelName, JEConstantLabel);

        String constantValue = instruction.getArgumentConst();
        String argConstName = instruction.getArgumentConstName();

        validateConstantValueArgument(argConstName, constantValue);
    }

    public void validate(JumpEqualVariableInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);

        String JEVariableLabel = instruction.getArgumentLabel();
        String argLabelName = instruction.getArgumentLabelName();
        validateLabelArgument(argLabelName, JEVariableLabel);

        String variableName =  instruction.getArgumentVariable();
        String argVarName = instruction.getArgumentVariableName();
        validateVariableArgument(argVarName, variableName);
    }


    public void validate(QuoteInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);
        String functionName = instruction.getFunctionName();
        String functionArgs = instruction.getFunctionArguments();

        if(!functionArgumentsValidator.isFunctionAvailable(functionName)){
            throw new InvalidInstructionException(String.format("Function %s does not exist", functionName));
        }
        try {
            functionArgumentsValidator.validateArguments(functionArgs);
        }
        catch(Exception e){
            throw new InvalidInstructionException(e.getMessage());
        }
    }

    public void validate(JumpEqualFunctionInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction);
        String functionName = instruction.getFunctionName();
        String functionArgs = instruction.getFunctionArguments();

        if(!functionArgumentsValidator.isFunctionAvailable(functionName)){
            throw new InvalidInstructionException(String.format("Function %s does not exist", functionName));
        }
        try {
            functionArgumentsValidator.validateArguments(functionArgs);
        }
        catch(Exception e){
            throw new InvalidInstructionException(e.getMessage());
        }

    }

    public void validate(UnsupportedInstruction instruction) throws InvalidInstructionException{
        validate((SInstruction) instruction); // or just throw immedietly
    }

    public static void validateType(SInstruction instr) throws InvalidInstructionException{
        InstructionName name = instr.getInstructionName();
        String type = instr.getType();

        boolean validTypeSyntax = type.equals("basic") || type.equals("synthetic");
        if(!validTypeSyntax){
            throw new InvalidInstructionException(String.format("invalid type %s (supported: basic/synthetic)", type));
        }
        boolean isBasic = isBasicInstruction(name);
        boolean valid = (isBasic && type.equals("basic")) || (!isBasic && type.equals("synthetic"));
        if(!valid){
            throw new InvalidInstructionException(String.format("instruction name %s is not of type %s", name, type));
        }
    }

    public static boolean isBasicInstruction(InstructionName name) {
        return name.equals(InstructionName.INCREASE) ||
                name.equals(InstructionName.DECREASE) ||
                name.equals(InstructionName.JUMP_NOT_ZERO) ||
                name.equals(InstructionName.NEUTRAL);
    }

    public static void isValidName(InstructionName name) throws InvalidInstructionException{
        if (name == InstructionName.UNSUPPORTED) {
            throw new InvalidInstructionException("invalid or unsupported instruction name");
        }
    }

    public static void validateSVariable(SInstruction instr) throws  InvalidInstructionException{
        InstructionName name = instr.getInstructionName();
        String svariable = instr.getSVariable();

        if(svariable.isEmpty()) {
            if (!name.equals(InstructionName.GOTO_LABEL)) {
                throw new InvalidInstructionException("S-Variable is required for this instruction");
            }
        }
        else{
            if (!isValidVariableFormat(svariable)) {
                throw new InvalidInstructionException("S-Variable format is invalid");
            }
        }
    }

    // case-sensitive
    public static boolean isValidVariableFormat(String variable){
        // Regex explanation:
        // ^(y|[xz][1-9][0-9]*)$
        // y                 → exactly "y"
        // |                 → OR
        // [xz][1-9][0-9]*   → x or z followed by number starting with 1-9, then digits

        return variable.matches("^(y|[xz][1-9][0-9]*)$");
    }

    // for now case-sensitive
    public static boolean isValidLabelFormat(String input){
        return input.matches("^(EXIT|L[1-9][0-9]*)$");
    }

    public static void validateArgumentNotEmpty(String argName, String argValue) throws InvalidInstructionException{
        if(argValue.isEmpty()){
            throw new InvalidInstructionException(
                    String.format("required %s argument is empty or missing", argName));
        }
    }

    public static void validateConstantValueArgument(String argName, String argValue) throws InvalidInstructionException{
        validateArgumentNotEmpty(argName, argValue);
        try {
            int value = Integer.parseInt(argValue);
            if(value < 0){
                throw new InvalidInstructionException(
                        String.format("argument %s value cannot be negative: %d", argName ,value));
            }
        }
        catch(NumberFormatException e){
            throw new InvalidInstructionException(
                    String.format("argument %s is not a positive integer: %s", argName ,argValue));
        }
    }

    public static void validateVariableArgument(String argName, String argValue) throws InvalidInstructionException{
        validateArgumentNotEmpty(argName, argValue);

        if(!isValidVariableFormat(argValue)){
            throw new InvalidInstructionException("invalid variable format: " + argValue);
        }

    }

    public static void validateLabelArgument(String argName, String argValue) throws InvalidInstructionException{
        validateArgumentNotEmpty(argName, argValue);
        if(!isValidLabelFormat(argValue)){
            throw new InvalidInstructionException("invalid label format: " + argValue);
        }
    }

}
