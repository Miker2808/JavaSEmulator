package ui.storage;

public class AppContext {
    private String username;
    private String programName;
    private int degreeSelected;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getDegreeSelected() { return degreeSelected; }
    public void setDegreeSelected(int degreeSelected) { this.degreeSelected = degreeSelected; }
}
