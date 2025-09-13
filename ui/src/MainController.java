

import engine.SInstructions;
import engine.SProgram;
import engine.execution.ExecutionResult;
import engine.instruction.SInstruction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import engine.Engine;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import ui.ProgressBarDialog;
import ui.VariableRow;


public class MainController {

    private final Engine engine = new Engine();
    // bunch of variables to make my lazy ass more comfortable
    private int expansion_selected = 0;
    private Boolean run_debug = false;


    @FXML
    private ChoiceBox<String> programSelectionChoiceBox;
    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Label degreeLabel;
    @FXML
    private TextField instructionSearchField;
    @FXML
    private Button loadProgramButton;
    @FXML
    private TextField loadedFilePathTextField;

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


    // Debugger / Execution Section
    // Buttons
    @FXML
    private Label cyclesMeterLabel;
    @FXML
    private ToggleButton debugModeToggle;
    @FXML
    private Button resumeButton;
    @FXML
    private Button runButton;
    @FXML
    private Button stepOverButton;
    @FXML
    private Button stopButton;

    // tables
    @FXML
    private TableView<VariableRow> inputTable;
    @FXML
    private TableColumn<VariableRow, Integer> inputTableValueColumn;
    @FXML
    private TableColumn<VariableRow, String> inputTableVariableColumn;

    // program variable state table
    @FXML
    private TableView<VariableRow> programVariablesTable;
    @FXML
    private TableColumn<VariableRow, Integer> programVariablesTableValueColumn;
    @FXML
    private TableColumn<VariableRow, String> programVariablesTableVariableColumn;


    // History
    @FXML
    private TableView<?> historyTable;





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
        initializedExpansionsTable();
        initializeInputTable();
        initializeProgramVariablesTable();

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

    private void initializedExpansionsTable(){
        //  lineColumn "#" — dynamic row numbering
        historyChainLine.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getLine())
        );
        // typeColumn — string from getType()
        historyChainType.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getType())
        );
        // cyclesColumn — integer from getCycles()
        historyChainCycles.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getCycles())
        );

        // labelColumn — string from getLabel()
        historyChainLabel.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getSLabel())
        );

        // instructionColumn — string from getInstructionString()
        historyChainInstruction.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getInstructionString())
        );

        // prepare table list
        historyChainTable.setItems(FXCollections.observableArrayList());

        instructionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                historyChainTable.getItems().clear();
                return;
            }

            List<SInstruction> chain = new ArrayList<>();
            SInstruction current = newSel.getParent();  // start from parent, not self

            // walk up to root
            while (current != null) {
                chain.add(current);
                current = current.getParent();
            }

            historyChainTable.getItems().setAll(chain);
        });
    }

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

            String path = selectedFile.getAbsolutePath();
            if(path.endsWith(".xml")) {
                new ProgressBarDialog(1.0f).start();
            }
            try {
                engine.loadFromXML(path);
                loadedFilePathTextField.setStyle("-fx-control-inner-background: lightgreen;");
                loadedFilePathTextField.setText(path);
                expansion_selected = 0;
                updateUIOnProgram(engine.getLoadedProgram());
                resetInputTable();
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

        // for now, TODO: make it show all functions (The engine needs to supply names)
        programSelectionChoiceBox.getItems().setAll(engine.getLoadedProgram().getName());
        programSelectionChoiceBox.setValue(engine.getLoadedProgram().getName());
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


    void resetInputTable(){
        List<String> input_variables = engine.getLoadedProgram().getInputVariablesUsed();
        inputTable.getItems().setAll(
                input_variables.stream().map(v -> new VariableRow(v, 0)).toList()
        );
    }

    @FXML
    void onNewRunClicked(MouseEvent event) {
        resetInputTable();
        cyclesMeterLabel.setText("Cycles: 0");
        programVariablesTable.getItems().clear();
    }

    void initializeInputTable(){
        inputTable.setEditable(true);

        inputTableVariableColumn.setCellValueFactory(new PropertyValueFactory<>("variable"));

        inputTableValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        inputTableValueColumn.setCellFactory(col -> new TextFieldTableCell<>(
                new IntegerStringConverter()) {
            @Override
            public void startEdit() {
                VariableRow row = getTableRow().getItem();
                if (row != null && row.getVariable() != null && !row.getVariable().isEmpty()) {
                    super.startEdit(); // only editable if left column is not empty
                }
            }
        });
    }

    void initializeProgramVariablesTable(){
        programVariablesTableVariableColumn.setCellValueFactory(new PropertyValueFactory<>("variable"));
        programVariablesTableValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    @FXML
    void onDebugModeClicked(MouseEvent event) {
        stepOverButton.setDisable(!debugModeToggle.isSelected());
        runButton.setDisable(debugModeToggle.isSelected());
        String on_off = debugModeToggle.isSelected() ? "ON" : "OFF";
        debugModeToggle.setText("Debug Mode: " + on_off);
    }

    @FXML
    private void onEditCommitInputColumn(TableColumn.CellEditEvent<VariableRow, Integer> event){
        Integer newValue = event.getNewValue();

        if (newValue == null) {
            event.getRowValue().setValue(0);
        } else if (newValue >= 0) {
            event.getRowValue().setValue(newValue);
        } else {
            event.getTableView().refresh();
            showInfoMessage("Invalid input variable value","Please enter a non-negative integer.");
        }
        event.getTableView().refresh();
    }

    @FXML
    void onResumeClicked(MouseEvent event) {

    }

    @FXML
    void onRunClicked(MouseEvent event) {
        HashMap<String, Integer> input_variables = new HashMap<>();

        for (VariableRow row : inputTable.getItems()) {
            String key = row.getVariable();
            int value = row.getValue(); // primitive int
            if (key != null && !key.isEmpty()) { // skip empty keys if needed
                input_variables.put(key, value);
            }
        }

        // run full program
        ExecutionResult result = engine.runProgram(engine.getLoadedProgram(), input_variables, expansion_selected);
        // populate table with result variables (later it'll be the same with execution context
        programVariablesTable.getItems().setAll(
            result.getVariables().entrySet().stream()
                    .map(e -> new VariableRow(e.getKey(), e.getValue()))
                    .toList()
        );

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());

    }

    @FXML
    void onStepOverClicked(MouseEvent event) {

    }

    @FXML
    void onStopClicked(MouseEvent event) {

    }

}