

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
import java.util.*;
import java.util.stream.Collectors;

import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import ui.ProgressBarDialog;
import ui.VariableRow;


public class MainController {

    private final Engine engine = new Engine();
    // bunch of variables to make my lazy ass more comfortable
    private int degree_selected = 0;
    private Boolean debug_run = false;
    private Boolean run_ended = false;
    private final Set<Integer> searchHighlightedLines = new HashSet<>();
    private Integer lineHighlighted = null; // only one line at a time
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
    private Button debugButton;
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
        initializeInstructionTable();
        initializeHighlightChoiceBox();
        initializedExpansionsTable();
        initializeInputTable();
        initializeProgramVariablesTable();
        initializeProgramSelectionChoiceBox();

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
                new ProgressBarDialog(.3f).start();
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
        programSelectionChoiceBox.getItems().setAll(engine.getLoadedProgramNames());
        programSelectionChoiceBox.getSelectionModel().selectFirst();
        reloadSelectedProgram();
    }

    private void reloadSelectedProgram(){
        degree_selected = 0;
        run_ended = false;
        debug_run = false;

        String selected_program = programSelectionChoiceBox.getSelectionModel().getSelectedItem();
        selectedProgramView = engine.getSelectedProgram(selected_program);
        updateInstructionsUI(selectedProgramView);
        resetInputTable();
        updateInputControllers();
        resetHighlightChoiceBox(selectedProgramView);
        updateUIOnExpansion();
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

        instructionsTable.setRowFactory(tv -> new TableRow<SInstruction>() {
            @Override
            protected void updateItem(SInstruction item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                List<String> styles = new ArrayList<>();

                // Search highlight
                if (searchHighlightedLines.contains(item.getLine())) {
                    styles.add("-fx-background-color: yellow;");
                }

                // Line highlight
                if (lineHighlighted != null && item.getLine() == lineHighlighted) {
                    styles.add("-fx-background-color: lightgreen;"); // stronger color
                }

                setStyle(String.join("", styles));
            }
        });

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

    private void initializeHighlightChoiceBox() {
        highlightChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updateSearchHighlights(newV);
            instructionsTable.refresh();
        });
    }

    private void initializeProgramSelectionChoiceBox(){
        programSelectionChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            onProgramSelection();
        });
    }

    private void onProgramSelection(){
        reloadSelectedProgram();
    }

    // Called when search filter changes
    private void updateSearchHighlights(String choice) {
        searchHighlightedLines.clear();
        if (choice != null && !choice.trim().isEmpty()) {
            String query = choice.toUpperCase().trim();
            for (SInstruction instr : instructionsTable.getItems()) {
                boolean match =
                        instr.getSLabel().toUpperCase().contains(query) ||
                                instr.getInstructionString().toUpperCase().contains(query);
                if (match) {
                    searchHighlightedLines.add(instr.getLine());
                }
            }
        }
    }

    // highlight a specific line by its "lineColumn" value
    public void highLightInstructionTableLine(int lineNumber) {
        lineHighlighted = lineNumber;
        instructionsTable.refresh();
    }

    // clear the line highlight (restores search highlights)
    public void clearInstructionTableHighlight() {
        lineHighlighted = null;
        instructionsTable.refresh();
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

    // clear highlight
    public void clearVariableHighlight() {
        inputTable.refresh();
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
        chooseDegreeTextField.setText("" + degree_selected);
        resetHighlightChoiceBox(programView);
    }

    @FXML
    void onCollapseButtonClicked(MouseEvent event) {
        degree_selected -= 1;
        updateUIOnExpansion();
    }

    @FXML
    void onExpandButtonClicked(MouseEvent event) {
        degree_selected += 1;
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

    void updateInputControllers(){
        boolean not_loaded = !engine.isProgramLoaded();
        newRunButton.setDisable(not_loaded);
        runButton.setDisable(debug_run || not_loaded || run_ended);
        stopButton.setDisable(!debug_run || not_loaded || run_ended);
        stepOverButton.setDisable(!debug_run || not_loaded || run_ended);
        resumeButton.setDisable(!debug_run || not_loaded || run_ended);
        expandButton.setDisable(not_loaded || debug_run);
        collapseButton.setDisable(not_loaded || debug_run);
        chooseDegreeTextField.setDisable(not_loaded || debug_run);
        debugButton.setDisable(not_loaded || debug_run || run_ended);
        inputTable.setDisable(debug_run);
        programSelectionChoiceBox.setDisable(debug_run || not_loaded);
    }

    @FXML
    void onNewRunClicked(MouseEvent event) {
        resetInputTable();
        cyclesMeterLabel.setText("Cycles: 0");
        programVariablesTable.getItems().clear();
        debug_run = false;
        run_ended = false;
        clearInstructionTableHighlight();
        updateInputControllers();
    }

    @FXML
    void onNormalRunClicked(MouseEvent event) {
        HashMap<String, Integer> input_variables = getInputVariablesFromUI();

        // run full program
        ExecutionContext result = engine.runProgram(programSelectionChoiceBox.getValue(), input_variables, degree_selected);
        // populate table with result variables (later it'll be the same with execution context

        updateProgramVariablesTable(result.getOrderedVariables(), false);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        run_ended = true;
        updateInputControllers();
    }

    @FXML
    void onDebugButtonClicked(MouseEvent event) {

        LinkedHashMap<String, Integer> variables = engine.startDebugRun(programSelectionChoiceBox.getValue(), getInputVariablesFromUI(), degree_selected);
        debug_run = true;

        updateProgramVariablesTable(variables, false);

        highLightInstructionTableLine(1);
        updateInputControllers();

    }

    @FXML
    void onResumeClicked(MouseEvent event) {

        // execute single step
        ExecutionContext result = engine.resumeLoadedRun();
        // populate table with result variables (later it'll be the same with execution context
        updateProgramVariablesTable(result.getOrderedVariables(), true);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        highLightInstructionTableLine(result.getPC());

        debug_run = false;
        run_ended = true;
        updateInputControllers();
    }

    @FXML
    void onStepOverClicked(MouseEvent event) {

        // execute single step
        ExecutionContext result = engine.stepLoadedRun();
        // populate table with result variables (later it'll be the same with execution context

        updateProgramVariablesTable(result.getOrderedVariables(), true);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        highLightInstructionTableLine(result.getPC());
        if(result.getExit()){
            debug_run = false;
            run_ended = true;
            updateInputControllers();
        }

    }

    @FXML
    void onStopClicked(MouseEvent event) {
        debug_run = false;
        run_ended = true;
        clearInstructionTableHighlight();
        updateInputControllers();

    }

    HashMap<String, Integer> getInputVariablesFromUI(){
        HashMap<String, Integer> input_variables = new HashMap<>();

        for (VariableRow row : inputTable.getItems()) {
            String key = row.getVariable();
            int value = row.getValue(); // primitive int
            if (key != null && !key.isEmpty()) { // skip empty keys if needed
                input_variables.put(key, value);
            }
        }
        return input_variables;
    }

    public void updateProgramVariablesTable(LinkedHashMap<String, Integer> result, boolean highlight) {
        Map<String, Integer> oldValues = programVariablesTable.getItems().stream()
                .collect(Collectors.toMap(VariableRow::getVariable, VariableRow::getValue));

        programVariablesTable.getItems().setAll(
                result.entrySet().stream()
                        .map(e -> new VariableRow(e.getKey(), e.getValue()))
                        .toList()
        );

        programVariablesTable.refresh();

        programVariablesTable.setRowFactory(tv -> {
            TableRow<VariableRow> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                row.setStyle("");
                if(highlight) {
                    if (newItem != null && newItem.getVariable() != null &&
                            !Objects.equals(oldValues.get(newItem.getVariable()), newItem.getValue())) {
                        row.setStyle("-fx-background-color: orange;");
                    }
                }
            });
            return row;
        });
    }





}