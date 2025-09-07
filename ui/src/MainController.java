

import engine.SProgram;
import engine.instruction.SInstruction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;

import java.io.File;

import engine.Engine;
import javafx.stage.FileChooser;


public class MainController {

    private final Engine engine = new Engine();

    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Label degreeLabel;
    @FXML
    private TableView<?> expansionTable;
    @FXML
    private TableView<?> historyTable;
    @FXML
    private Button loadProgramButton;
    @FXML
    private TextField loadedFilePathTextField;
    @FXML
    private TableView<?> programVariablesTable;

// instructions table
    @FXML
    private TableView<SInstruction> instructionsTable;
    @FXML
    private TableColumn<SInstruction, Number> lineColumn;
    @FXML
    private TableColumn<SInstruction, String> typeColumn;
    @FXML
    private TableColumn<SInstruction, Number> cyclesColumn;
    @FXML
    private TableColumn<SInstruction, String> labelColumn;
    @FXML
    private TableColumn<SInstruction, String> instructionColumn;

// bunch of variables to make my lazy ass more comfortable
    private int expansion_selected = 0;
    private Boolean run_debug = false;

    private void initializeInstructionTable(){
        // 1️⃣ lineColumn "#" — dynamic row numbering
        lineColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getLine())
        );
        // typeColumn — string from getType()
        typeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getType())
        );
        // cyclesColumn — integer from getCycles()
        cyclesColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getCycles())
        );

        // labelColumn — string from getLabel()
        labelColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getSLabel())
        );

        // instructionColumn — string from getInstructionString()
        instructionColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getInstructionString())
        );

        // prepare table list
        instructionsTable.setItems(FXCollections.observableArrayList());

    }

    @FXML
    public void initialize() {
        System.out.println("Initializing Main Controller");

        initializeInstructionTable();

        collapseButton.setDisable(true);
        expandButton.setDisable(true);
    }

    // opens an "Alert" window with information.
    private void showInfoMessage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // removes the ugly header
        alert.showAndWait();
    }

    @FXML
    void onClickedLoadProgramButton(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(loadProgramButton.getScene().getWindow());

        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            try {
                engine.loadFromXML(path);
                loadedFilePathTextField.setStyle("-fx-control-inner-background: lightgreen;");
                loadedFilePathTextField.setText(path);
                expansion_selected = 0;
                updateUIOnProgram(engine.getLoadedProgram());


            }
            catch(Exception e){
                //loadedFilePathTextField.setText(e.getMessage());
                // add alert window
                showInfoMessage("Failed to load XML file", e.getMessage());
            }
        }
    }

    void updateUIOnProgram(SProgram program){
        int max_degree = engine.getLoadedProgram().getMaxDegree();
        degreeLabel.setText(String.format("%d / %d Degree",expansion_selected, max_degree));
        collapseButton.setDisable(expansion_selected == 0);
        expandButton.setDisable(expansion_selected == max_degree);
        instructionsTable.getItems().clear();
        for(int i=1; i <= program.Size(); i++){
            SInstruction instr = program.getInstruction(i);
            instructionsTable.getItems().add(instr);
        }
    }

    @FXML
    void onCollapseButtonClicked(MouseEvent event) {
        expansion_selected = expansion_selected - 1;
        SProgram expanded = engine.expandProgram(engine.getLoadedProgram(), expansion_selected);
        updateUIOnProgram(expanded);
    }

    @FXML
    void onExpandButtonClicked(MouseEvent event) {
        expansion_selected = expansion_selected + 1;
        SProgram expanded = engine.expandProgram(engine.getLoadedProgram(), expansion_selected);
        updateUIOnProgram(expanded);
    }


}