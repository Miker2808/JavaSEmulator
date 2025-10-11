package Storage;

import engine.SProgramView;
import engine.history.ExecutionHistory;
import engine.history.ExecutionHistoryManager;
import engine.interpreter.SInterpreter;

public class UserInstance {
    private String programSelected = "";
    private String programType = "";
    private Integer degree_selected = 0;
    private Integer numProgramsUploaded = 0;
    private Integer numFunctionsUploaded = 0;
    private Integer creditsAvailable = 0;
    private Integer creditsUsed = 0;
    private Integer totalRuns = 0;

    private Boolean running = false;
    private SInterpreter interpreter = null;

    public UserInstance(){

    }

    public Integer getDegreeSelected() {
        return degree_selected;
    }

    public void setDegreeSelected(Integer degree_selected) {
        this.degree_selected = degree_selected;
    }

    public Boolean isRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Integer getNumProgramsUploaded() {
        return numProgramsUploaded;
    }

    public void setNumProgramsUploaded(Integer numProgramsUploaded) {
        this.numProgramsUploaded = numProgramsUploaded;
    }
    public Integer getNumFunctionsUploaded() {
        return numFunctionsUploaded;
    }
    public void setNumFunctionsUploaded(Integer numFunctionsUploaded) {
        this.numFunctionsUploaded = numFunctionsUploaded;
    }
    public Integer getCreditsAvailable() {
        return creditsAvailable;
    }
    public void setCreditsAvailable(Integer creditsAvailable) {
        this.creditsAvailable = creditsAvailable;
    }

    public void addCreditsAvailable(Integer creditsAvailable) {
        this.creditsAvailable += creditsAvailable;
    }

    public Integer getCreditsUsed() {
        return creditsUsed;
    }
    public void setCreditsUsed(Integer creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public void addCreditsUsed(Integer creditsUsed) {
        this.creditsUsed += creditsUsed;
    }

    public Integer getTotalRuns() {
        return totalRuns;
    }
    public void setTotalRuns(Integer totalRuns) {
        this.totalRuns = totalRuns;
    }

    public String getProgramSelected(){
        return programSelected;
    }

    public void setProgramSelected(String programSelected) {
        this.programSelected = programSelected;
    }

    public void setInterpreter(SInterpreter interpreter) {
        this.interpreter = interpreter;
    }
    public SInterpreter getInterpreter(){
        return interpreter;
    }

    public String getProgramType() {
        return programType;
    }
    public void setProgramType(String programType) {
        this.programType = programType;
    }

}
