package engine;

public interface SProgramView {
    ProgramType getProgramType();

    String getName();
    SInstructionsView getInstructionsView();
    String getUserString();

    enum ProgramType {
        PROGRAM,
        FUNCTION
    }
}
