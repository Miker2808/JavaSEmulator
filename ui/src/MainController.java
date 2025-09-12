

import engine.SInstructions;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.Engine;
import javafx.stage.FileChooser;
import ui.ProgressBarDialog;


public class MainController {

    private final Engine engine = new Engine();

    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Label degreeLabel;
    @FXML
    private TextField instructionSearchField;
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

    @FXML
    private TableView<SInstruction> historyChainTable;
    @FXML
    private TableColumn<SInstruction, Number> historyChainLine;
    @FXML
    private TableColumn<SInstruction, String> historyChainType;
    @FXML
    private TableColumn<SInstruction, Number> historyChainCycles;
    @FXML
    private TableColumn<SInstruction, String> historyChainLabel;
    @FXML
    private TableColumn<SInstruction, String> historyChainInstruction;


    // bunch of variables to make my lazy ass more comfortable
    private int expansion_selected = 0;
    private Boolean run_debug = false;


    // opens an "Alert" window with information.
    private void showInfoMessage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // removes the ugly header
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        System.out.println("Initializing Main Controller");

        initializeInstructionTable();
        initializedInstructionSearch();
        //initializedExpansionsTable();

        collapseButton.setDisable(true);
        expandButton.setDisable(true);
    }

    private void initializeInstructionTable(){
        //lineColumn "#" — dynamic row numbering
        lineColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getLine())
        );
        // typeColumn — string from getType()
        typeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTypeShort())
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

    /*
    private void initializedExpansionsTable(){
        //  lineColumn "#" — dynamic row numbering
        expansionLine.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getLine())
        );
        // typeColumn — string from getType()
        expansionType.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getType())
        );
        // cyclesColumn — integer from getCycles()
        expansionCycles.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getCycles())
        );

        // labelColumn — string from getLabel()
        expansionLabel.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getSLabel())
        );

        // instructionColumn — string from getInstructionString()
        expansionInstruction.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getInstructionString())
        );

        // prepare table list
        expansionTable.setItems(FXCollections.observableArrayList());

        instructionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                expansionTable.getItems().clear();
                return;
            }

            List<SInstruction> chain = new ArrayList<>();
            SInstruction current = newSel.getParent();  // start from parent, not self

            // walk up to root
            while (current != null) {
                chain.add(current);
                current = current.getParent();
            }

            expansionTable.getItems().setAll(chain);
        });
    }
    */

    private void initializedInstructionSearch(){
        instructionSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            instructionsTable.refresh();
        });

        // Highlight rows based on search
        instructionsTable.setRowFactory(tv -> new TableRow<SInstruction>() {
            @Override
            protected void updateItem(SInstruction item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    String search = instructionSearchField.getText();
                    if (search != null && !search.isEmpty()) {
                        String lowerSearch = search.toLowerCase();
                        // if instead of InstructionString I use only variables,
                        // search can be more strict
                        boolean match =
                                item.getSLabel().toLowerCase().contains(lowerSearch) ||
                                        item.getInstructionString().toLowerCase().contains(lowerSearch);

                        if (match) {
                            setStyle("-fx-background-color: yellow;");
                        } else {
                            setStyle("");
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }



    @FXML
    void onClickedLoadProgramButton(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(loadProgramButton.getScene().getWindow());

        if (selectedFile != null) {
            new ProgressBarDialog(1.0f).start();

            String path = selectedFile.getAbsolutePath();
            try {
                engine.loadFromXML(path);
                loadedFilePathTextField.setStyle("-fx-control-inner-background: lightgreen;");
                loadedFilePathTextField.setText(path);
                expansion_selected = 0;
                updateUIOnProgram(engine.getLoadedProgram());
            }
            catch(Exception e){
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