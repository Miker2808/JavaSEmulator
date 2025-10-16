package engine;

public interface SProgramView {
    ProgramType getProgramType();

    String getName();
    SInstructionsView getInstructionsView();
    String getUserString();
    String getUploader();
    int getNumRuns();
    int getAverage_credits_cost();
    String getParentProgram();
    void setNumRuns(int value);
    enum ProgramType {
        PROGRAM,
        FUNCTION
    }
}
