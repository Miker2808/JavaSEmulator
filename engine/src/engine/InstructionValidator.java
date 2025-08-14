package engine;

import java.util.Objects;
import java.util.Set;

public class InstructionValidator {

    private static final Set<String> VALID_NAMES = Set.of(
            "INCREASE", "DECREASE", "JUMP_NOT_ZERO", "NEUTRAL", "ZERO_VARIABLE",
            "GOTO_LABEL", "ASSIGNMENT", "CONSTANT_ASSIGNMENT", "JUMP_ZERO",
            "JUMP_EQUAL_CONSTANT", "JUMP_EQUAL_VARIABLE", "QUOTE", "JUMP_EQUAL_FUNCTION"
    );

    public static void validateInstruction(SInstruction instr) throws InvalidInstructionException{
        String name = instr.getName();
        String label = instr.getSLabel();

        isValidName(name);

        validateType(instr);

        validateSVariable(instr);

        if(!label.isEmpty() && !isValidLabelFormat(label)) {
            throw new  InvalidInstructionException("invalid label format: " + label);
        }

        switch (name) {
            case "INCREASE" -> validateIncrease(instr);
            case "DECREASE" -> validateDecrease(instr);
            case "JUMP_NOT_ZERO" -> validateJumpNotZero(instr);
            case "NEUTRAL" -> validateNeutral(instr);
            case "ZERO_VARIABLE" -> validateZeroVariable(instr);
            case "GOTO_LABEL" -> validateGotoLabel(instr);
            case "ASSIGNMENT" -> validateAssignment(instr);
            case "CONSTANT_ASSIGNMENT" -> validateConstantAssignment(instr);
            case "JUMP_ZERO" -> validateJumpZero(instr);
            case "JUMP_EQUAL_CONSTANT" -> validateJumpEqualConstant(instr);
            case "JUMP_EQUAL_VARIABLE" -> validateJumpEqualVariable(instr);
            case "QUOTE" -> validateQuote(instr);
            case "JUMP_EQUAL_FUNCTION" -> validateJumpEqualFunction(instr);
            default -> throw new InvalidInstructionException("invalid or unsupported instruction name: " + name);
        }
    }


    public static void validateIncrease(SInstruction instruction) throws InvalidInstructionException{

    }

    public static void validateDecrease(SInstruction instruction) throws InvalidInstructionException{

    }

    public static void validateJumpNotZero(SInstruction instruction) throws InvalidInstructionException{
        String JNZLabel = instruction.getArgument("JNZLabel");
        validateArgumentNotEmpty("JNZLabel", JNZLabel);
        validateLabelArgument("JNZLabel", JNZLabel);
    }

    public static void validateNeutral(SInstruction instruction) throws InvalidInstructionException{

    }

    public static void validateZeroVariable(SInstruction instruction) throws InvalidInstructionException{

    }

    public static void validateGotoLabel(SInstruction instruction) throws InvalidInstructionException{
        String gotoLabel = instruction.getArgument("gotoLabel");
        validateLabelArgument("gotoLabel", gotoLabel);
    }

    public static void validateAssignment(SInstruction instruction) throws InvalidInstructionException{
        String assignedVariable = instruction.getArgument("assignedVariable");
        validateVariableArgument("assignedVariable", assignedVariable);
    }

    public static void validateConstantAssignment(SInstruction instruction) throws InvalidInstructionException{
        String constantValue = instruction.getArgument("constantValue");
        validateConstantValueArgument("constantValue", constantValue);
    }

    public static void validateJumpZero(SInstruction instruction) throws InvalidInstructionException{
        String JZLabel = instruction.getArgument("JZLabel");
        validateLabelArgument("JZLabel", JZLabel);
    }

    public static void validateJumpEqualConstant(SInstruction instruction) throws InvalidInstructionException{
        String JEConstantLabel = instruction.getArgument("JEConstantLabel");
        validateLabelArgument("JEConstantLabel", JEConstantLabel);

        String constantValue = instruction.getArgument("constantValue");
        validateConstantValueArgument("constantValue", constantValue);
    }

    public static void validateJumpEqualVariable(SInstruction instruction) throws InvalidInstructionException{
        String JEVariableLabel = instruction.getArgument("JEVariableLabel");
        validateLabelArgument("JEVariableLabel", JEVariableLabel);
        String variableName =  instruction.getArgument("variableName");
        validateVariableArgument("variableName", variableName);
    }

    // TODO: ADD SUPPORT
    public static void validateQuote(SInstruction instruction) throws InvalidInstructionException{
        throw new InvalidInstructionException(String.format("instruction name %s is not supported", instruction.getName()));
    }

    // TODO: ADD SUPPORT
    public static void validateJumpEqualFunction(SInstruction instruction) throws InvalidInstructionException{
        throw new InvalidInstructionException(String.format("instruction name %s is not supported", instruction.getName()));
    }

    public static void validateType(SInstruction instr) throws InvalidInstructionException{
        String name = instr.getName();
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

    public static boolean isBasicInstruction(String name) {
        return Objects.equals(name, "INCREASE") ||
                Objects.equals(name, "DECREASE") ||
                Objects.equals(name, "JUMP_NOT_ZERO") ||
                Objects.equals(name, "NEUTRAL");
    }

    public static void isValidName(String name) throws InvalidInstructionException{
        if (!VALID_NAMES.contains(name)) {
            throw new InvalidInstructionException("invalid or unsupported instruction name: " + name);
        }
    }


    public static void validateSVariable(SInstruction instr) throws  InvalidInstructionException{
        String name = instr.getName();
        String svariable = instr.getSVariable();

        if(svariable.isEmpty()) {
            if (!name.equals("GOTO_LABEL")) {
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
