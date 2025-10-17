package Storage;

import engine.history.ExecutionHistory;
import engine.history.ExecutionHistoryManager;
import engine.interpreter.SInterpreter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserInstance {
    private String programSelected = "";
    private String programType = "";
    private final AtomicInteger degree_selected = new AtomicInteger(0);
    private Integer numProgramsUploaded = 0;
    private Integer numFunctionsUploaded = 0;
    private final AtomicLong creditsAvailable = new AtomicLong(0);
    private final AtomicLong creditsUsed = new AtomicLong(0);
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
        return degree_selected.get();
    }

    public void setDegreeSelected(Integer degree_selected) {
        this.degree_selected.set(degree_selected);
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

    public Long getCreditsAvailable() {
        return creditsAvailable.get();
    }

    public void setCreditsAvailable(Long creditsAvailable) {
        this.creditsAvailable.set(creditsAvailable);
    }

    public void addCreditsAvailable(int value) {
        this.creditsAvailable.addAndGet(value);
    }

    public Long getCreditsUsed() {
        return creditsUsed.get();
    }

    public void setCreditsUsed(Long creditsUsed) {
        this.creditsUsed.set(creditsUsed);
    }

    public void addCreditsUsed(int value) {
        this.creditsUsed.addAndGet(value);
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

    public AtomicLong getCreditsAvailRef(){
        return this.creditsAvailable;
    }
    public AtomicLong getCreditsUsedRef(){
        return this.creditsUsed;
    }
}
