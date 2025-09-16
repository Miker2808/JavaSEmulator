

import engine.*;
import engine.execution.ExecutionContext;
import engine.instruction.SInstruction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import ui.ProgressBarDialog;
import ui.VariableRow;


public class MainController {

    private final Engine engine = new Engine();
    // bunch of variables to make my lazy ass more comfortable
    private int degree_selected = 0;
    private Boolean run_debug = false;
    private Boolean new_run = true;

    SProgramView selectedProgramView = null;


    @FXML
    private ChoiceBox<String> programSelectionChoiceBox;
    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Label maxDegreeLabel;
    @FXML
    private ChoiceBox<String> highlightChoiceBox;
    @FXML
    private Button loadProgramButton;
    @FXML
    private TextField loadedFilePathTextField;
    @FXML
    private TextField chooseDegreeTextField;

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
    private Button newRunButton;
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
        initializeHighlightChoiceBox();
        initializedExpansionsTable();
        initializeInputTable();
        initializeProgramVariablesTable();

        collapseButton.setDisable(true);
        expandButton.setDisable(true);

        initializeChooseDegreeTextField();
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
                initOnLoad(path);
            }
            catch(Exception e){
                // add alert window
                showInfoMessage("Failed to load XML file", e.getMessage());
            }
        }
    }

    private void initOnLoad(String path){
        loadedFilePathTextField.setStyle("-fx-control-inner-background: lightgreen;");
        loadedFilePathTextField.setText(path);
        degree_selected = 0;
        selectedProgramView = engine.getSelectedProgram("");
        updateInstructionsUI(selectedProgramView);
        updateUIOnExpansion();
        resetInputTable();
        updateExecutionButtons();
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
                new SimpleStringProperty(cell.getValue().getTypeShort())
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


    private void initializeHighlightChoiceBox(){
        highlightChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
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
                    String choice = highlightChoiceBox.getValue();
                    if (choice != null && !choice.isEmpty()) {
                        choice = choice.toUpperCase().trim();

                        // if instead of InstructionString I use only variables,
                        // search can be more strict
                        boolean match =
                                item.getSLabel().toUpperCase().contains(choice) ||
                                        item.getInstructionString().toUpperCase().contains(choice);
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

    void initializeChooseDegreeTextField(){
        chooseDegreeTextField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().matches("\\d*")) {
                return c; // allow digits only
            }
            return null; // block everything else
        }));
    }

    private void resetHighlightChoiceBox(SProgramView programView){
        List<String> used_variables = programView.getInstructionsView().getVariablesUsed();
        List<String> used_labels = programView.getInstructionsView().getLabelsUsed();
        highlightChoiceBox.getItems().setAll(used_variables);
        highlightChoiceBox.getItems().addFirst("Highlight Selection");
        highlightChoiceBox.getItems().addAll(used_labels);
        highlightChoiceBox.getSelectionModel().selectFirst();
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

    // updates instructions UI with highlight selection
    void updateInstructionsUI(SProgramView programView){

        instructionsTable.getItems().clear();
        for(int i=1; i <= programView.getInstructionsView().size(); i++){
            SInstruction instr = programView.getInstructionsView().getInstruction(i);
            instructionsTable.getItems().add(instr);
        }

        // for now, TODO: make it show all functions (The engine needs to supply names)
        programSelectionChoiceBox.getItems().setAll(selectedProgramView.getName());
        programSelectionChoiceBox.setValue(selectedProgramView.getName());
        chooseDegreeTextField.setText("" + degree_selected);
        resetHighlightChoiceBox(programView);
    }

    @FXML
    void onCollapseButtonClicked(MouseEvent event) {
        degree_selected = degree_selected - 1;
        updateUIOnExpansion();
    }

    @FXML
    void onExpandButtonClicked(MouseEvent event) {
        degree_selected = degree_selected + 1;
        updateUIOnExpansion();
    }

    @FXML
    void onDegreeTextFieldAction() {
        String text = chooseDegreeTextField.getText();
        int max_degree = selectedProgramView.getInstructionsView().getMaxDegree();
        int text_deg = Integer.parseInt(text.trim());
        this.degree_selected = Math.min(text_deg, max_degree);

        updateUIOnExpansion();

    }

    void updateUIOnExpansion(){
        int max_degree = selectedProgramView.getInstructionsView().getMaxDegree();;
        maxDegreeLabel.setText(String.format("%d", max_degree));
        collapseButton.setDisable(degree_selected == 0);
        expandButton.setDisable(degree_selected == max_degree);
        updateInstructionsUI(engine.getExpandedProgram(programSelectionChoiceBox.getValue(), degree_selected));
    }


    void resetInputTable(){
        List<String> input_variables = selectedProgramView.getInstructionsView().getInputVariablesUsed();
        inputTable.getItems().setAll(
                input_variables.stream().map(v -> new VariableRow(v, 0)).toList()
        );
    }

    void updateExecutionButtons(){
        boolean not_loaded = !engine.isProgramLoaded();
        boolean debug = debugModeToggle.isSelected();
        debugModeToggle.setDisable(not_loaded);
        newRunButton.setDisable(!new_run && not_loaded);
        runButton.setDisable(debug || not_loaded);
        stopButton.setDisable(new_run || not_loaded);
        stepOverButton.setDisable(!debug || not_loaded);
        resumeButton.setDisable(!debug || new_run);
        stopButton.setDisable( new_run);
        chooseDegreeTextField.setDisable(not_loaded);
    }

    @FXML
    void onNewRunClicked(MouseEvent event) {
        resetInputTable();
        cyclesMeterLabel.setText("Cycles: 0");
        programVariablesTable.getItems().clear();
        new_run = true;
        updateExecutionButtons();
    }

    @FXML
    void onDebugModeClicked(MouseEvent event) {
        updateExecutionButtons();
        String on_off = debugModeToggle.isSelected() ? "ON" : "OFF";
        debugModeToggle.setText("Debug Mode: " + on_off);
    }

    @FXML
    void onResumeClicked(MouseEvent event) {

    }

    @FXML
    void onStepOverClicked(MouseEvent event) {

    }

    @FXML
    void onStopClicked(MouseEvent event) {

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
        ExecutionContext result = engine.runProgram(programSelectionChoiceBox.getValue(), input_variables, degree_selected);
        // populate table with result variables (later it'll be the same with execution context
        programVariablesTable.getItems().setAll(
            result.getOrderedVariables().entrySet().stream()
                    .map(e -> new VariableRow(e.getKey(), e.getValue()))
                    .toList()
        );

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        runButton.setDisable(true);
        new_run = false;
    }



}