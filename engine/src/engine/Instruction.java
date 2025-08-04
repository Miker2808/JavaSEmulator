
package engine;

public class Instruction
{
    public enum InstructionType {
        INCREMENT, // V <- V+1
        DECREMENT, // V <- V-1
        IF_GOTO, // IF V != 0 GOTO L
        NO_OP, // V <- V
    }

    private final InstructionType type;
    private final String variable;      // The variable being operated on
    private final int label;   // For GOTO instructions (0 if no label)
    private final Boolean isSyntactic;
    private final int cycles;
    private final int targetLabel;
    public static final int EXIT_LABEL = -1;
    public static final int MAX_LABEL = 99;


    public Instruction(InstructionType type, String variable, int label, int targetLabel){
        this.type = type;
        this.variable = variable;
        this.label = label;
        this.targetLabel = targetLabel;
        isSyntactic = !isBasicInstruction(type);
        cycles = countCycles();
    }

    private boolean isBasicInstruction(InstructionType type) {
        return type == InstructionType.INCREMENT ||
                type == InstructionType.DECREMENT ||
                type == InstructionType.IF_GOTO ||
                type == InstructionType.NO_OP;
    }

    private int countCycles(){
        return switch (type) {
            case INCREMENT, DECREMENT -> 1;
            case IF_GOTO -> 2;
            case NO_OP -> 0;
            // some cases in the future require running specialized functions
        };
    }

    public String toString(){

        // syntactic phase
        String output = "(";
        output += isSyntactic ? "S)" : "B)";

        String displayLabel = (label > 0) ? ("L" + label) : "   ";

        output += String.format(" [ %-3s ] ", displayLabel);

        switch (type){
            case INCREMENT:{
                output += String.format("%s <- %s + 1", variable, variable);
                break;
            }
            case DECREMENT:{
                output += String.format("%s <- %s - 1", variable, variable);
                break;
            }
            case IF_GOTO:{
                String displayTargetLabel =  (targetLabel > 0) ? ("L" + targetLabel) : "   ";
                displayTargetLabel = (targetLabel == EXIT_LABEL) ? "EXIT" : displayTargetLabel;
                output += String.format("IF %s != 0 GOTO %s", variable, displayTargetLabel);
                break;
            }
            case NO_OP:{
                output += String.format("%s <- %s", variable, variable);
            }
        }

        output += " (" + cycles + ")";

        return output;
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

    public static boolean isValidLabel(int label){
        return label > 0 && label <= MAX_LABEL;
    }


}
