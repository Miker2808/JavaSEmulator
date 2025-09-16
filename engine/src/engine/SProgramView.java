package engine;

import engine.instruction.SInstruction;
import java.util.List;

public interface SProgramView {
    ProgramType getBlockType();

    String getName();
    SInstructionsView getInstructionsView();
    String getUserString();

    enum ProgramType {
        PROGRAM,
        FUNCTION
    }
}
