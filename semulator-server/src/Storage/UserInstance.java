package Storage;

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

    private Boolean computing = false; // is user instance physically computing something (thread pool currently working on user request)
    private Boolean stop = false;
    private Boolean debug = false;
    private String exceptionString = ""; // if exception was raised from thread pool, message will appear here

    private SInterpreter interpreter = null;
    private ExecutionHistoryManager historyManager = new ExecutionHistoryManager();
    private ExecutionHistory currentExecutionHistory = null;

    public UserInstance(){

    }

    public Integer getDegreeSelected() {
        return degree_selected;
    }

    public void setDegreeSelected(Integer degree_selected) {
        this.degree_selected = degree_selected;
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

    public void setExceptionString(String message){
        this.exceptionString = message;
    }
    public String getExceptionString(){
        return exceptionString;
    }

    public ExecutionHistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(ExecutionHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }
    public Boolean getStop() {
        return stop;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
    public Boolean getDebug() {
        return debug;
    }

    public void setCurrentExecutionHistory(ExecutionHistory currentExecutionHistory) {
        this.currentExecutionHistory = currentExecutionHistory;
    }
    public ExecutionHistory getCurrentExecutionHistory() {
        return currentExecutionHistory;
    }

    public void setComputing(Boolean computing) {
        this.computing = computing;
    }
    public Boolean isComputing() {
        return computing;
    }
}
