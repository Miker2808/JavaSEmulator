
package engine;

import java.util.HashMap;

public class Instruction
{
    public enum InstructionType {
        INCREASE, // V <- V+1
        DECREASE, // V <- V-1
        JUMP_NOT_ZERO, // IF V != 0 GOTO L
        NEUTRAL, // V <- V
        ZERO_VARIABLE, // V <- 0
        GOTO_LABEL, // GOTO L (no condition)
        ASSIGNMENT, // V <- V'
        CONSTANT_ASSIGNMENT, // V <- 5
        JUMP_ZERO, // IF V = 0 GOTO L
        JUMP_EQUAL_CONSTANT,
        JUMP_EQUAL_VARIABLE,
        QUOTE,
        JUMP_EQUAL_FUNCTION,
    }

    private final InstructionType type;
    private final Boolean isSyntactic;
    private final int cycles;
    private final HashMap<String, String> arguments;

    public Instruction(InstructionType type, HashMap<String, String> arguments){
        this.type = type;
        isSyntactic = !isBasicInstruction(type);
        cycles = countCycles();
        this.arguments = new HashMap<>(arguments);
    }

    private boolean isBasicInstruction(InstructionType type) {
        return type == InstructionType.INCREASE ||
                type == InstructionType.DECREASE ||
                type == InstructionType.JUMP_NOT_ZERO ||
                type == InstructionType.NEUTRAL;
    }

    private int countCycles(){
        return switch (type) {
            case INCREASE, DECREASE -> 1;
            case JUMP_NOT_ZERO -> 2;
            case NEUTRAL -> 0;
            case ZERO_VARIABLE -> 1;
            case GOTO_LABEL -> 1;
            case ASSIGNMENT -> 4;
            case CONSTANT_ASSIGNMENT -> 2;
            case JUMP_ZERO -> 2;
            case JUMP_EQUAL_CONSTANT -> 2;
            case JUMP_EQUAL_VARIABLE -> 2;
            case QUOTE -> 5;
            case JUMP_EQUAL_FUNCTION -> 6;
        };
    }

    private String getOperationString(String variable) {
        return switch (type) {
            case INCREASE -> String.format("%s <- %s + 1", variable, variable);
            case DECREASE -> String.format("%s <- %s - 1", variable, variable);
            case JUMP_NOT_ZERO -> String.format("IF %s != 0 GOTO %s", variable, arguments.get("gotoLabel"));
            case NEUTRAL -> String.format("%s <- %s", variable, variable);
            case ZERO_VARIABLE -> String.format("%s <- 0", variable);
            case GOTO_LABEL -> String.format("GOTO %s", arguments.get("gotoLabel"));
            case ASSIGNMENT -> String.format("%s <- %s", variable, arguments.get("assignedVariable"));
            case CONSTANT_ASSIGNMENT -> String.format("%s <- %s", variable, arguments.get("constantValue"));
            case JUMP_ZERO -> String.format("IF %s = 0 GOTO %s", variable, arguments.get("JZLabel"));
            case JUMP_EQUAL_CONSTANT ->
                    String.format("IF %s = %s GOTO %s", variable, arguments.get("constantValue"), arguments.get("JEConstantLabel"));
            case JUMP_EQUAL_VARIABLE ->
                    String.format("IF %s = %s GOTO %s", variable, arguments.get("variableName"), arguments.get("JEVariableLabel"));
            default -> "";
        };
    }

    public String toString() {
        String label = arguments.getOrDefault("label", "");
        String phase = isSyntactic ? "S" : "B";
        String operation = getOperationString(arguments.get("variable"));

        return String.format("(%s) [ %-3s ] %s (%d)", phase, label, operation, cycles);
    }

    // for now case-sensitive
    public static boolean isValidVariable(String input) {
        // Regex explanation:
        // ^(y|[xz][1-9][0-9]*)$
        // y                 → exactly "y"
        // |                 → OR
        // [xz][1-9][0-9]*   → x or z followed by number starting with 1-9, then digits
        return input.matches("^(y|[xz][1-9][0-9]*)$");
    }

    // for now case-sensitive
    public static boolean isValidLabel(String input){
        return input.matches("^(EXIT|L[1-9][0-9]*)$");
    }

    public InstructionType getInstructionType() {
        return type;
    }

    public String getVariable() {
        return arguments.get("variable");
    }

    public String getLabel(){
        return arguments.get("label");
    }

    public int getCycles(){
        return cycles;
    }

    public HashMap<String, String> getArguments() {
        return new HashMap<>(arguments);
    }

}
