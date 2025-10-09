package Storage;

import java.util.HashSet;
import java.util.Set;

public class UserInstance {
    private String programSelected = "";
    private Integer numFilesUploaded = 0;
    private Integer numFunctionsUploaded = 0;
    private Integer creditsAvailable = 0;
    private Integer creditsUsed = 0;
    private Integer totalRuns = 0;
    private Integer degree_selected = 0;
    private Boolean running = false;

    public UserInstance(){

    }

    public Integer getDegreeSelected() {
        return degree_selected;
    }

    public void setDegreeSelected(Integer degree_selected) {
        this.degree_selected = degree_selected;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public Integer getNumFilesUploaded() {
        return numFilesUploaded;
    }

    public void setNumFilesUploaded(Integer numFilesUploaded) {
        this.numFilesUploaded = numFilesUploaded;
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
    public Integer getCreditsUsed() {
        return creditsUsed;
    }
    public void setCreditsUsed(Integer creditsUsed) {
        this.creditsUsed = creditsUsed;
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

}
