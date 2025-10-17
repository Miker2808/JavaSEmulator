package engine;

public interface SProgramView {
    ProgramType getProgramType();

    String getName();
    SInstructionsView getInstructionsView();
    String getUserString();
    String getUploader();
    void setUploader(String uploader);
    int getNumRuns();
    int getAverage_credits_cost();
    void setAverage_credits_cost(int value);
    String getParentProgram();
    void setNumRuns(int value);
    void addNumRuns(int value);
    enum ProgramType {
        PROGRAM,
        FUNCTION
    }
}
