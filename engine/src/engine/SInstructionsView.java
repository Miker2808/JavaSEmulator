package engine;

import engine.instruction.InvalidInstructionException;
import engine.instruction.SInstruction;

import java.util.List;

public interface SInstructionsView {
    int getMaxDegree();

    // validates that all used labels jump to a line
    void validateLabelsUsed() throws InvalidInstructionException;

    // value of highest L{n} variable used
    int getMaxUsedLabel();

    // value of highest z{n} variable used
    int getMaxUsedZVariable();

    // returns list of labels used in order
    List<String> getLabelsUsed();

    // returns list of variables used in order
    List<String> getVariablesUsed();

    // returns number of SInstruction's in object
    int size();

    SInstruction getInstruction(int line_num);

    // returns input variables used in order
    List<String> getInputVariablesUsed();

    List<SInstruction> getAllInstructions();

}
