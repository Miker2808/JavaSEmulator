package ui.controllers;

import dto.ExecutionDTO;
import dto.SInstructionDTO;
import dto.SProgramDTO;
import dto.SProgramViewDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import ui.App;
import ui.StatefulController;
import ui.elements.InfoMessage;
import ui.netcode.NetCode;
import ui.storage.AppContext;
import ui.storage.VariableRow;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class MainController implements StatefulController {
    private AppContext appContext;
    private Stage stage;

    private Integer max_degree = 0;
    private Integer degree_selected = 0;
    private Boolean running = false;
    private final Set<Integer> searchHighlightedLines = new HashSet<>();
    private final Set<Integer> breakPoints = new HashSet<>();
    private Integer lineHighlighted = null; // only one line at a time

    @FXML private Button collapseButton;
    @FXML private Button expandButton;
    @FXML private Label maxDegreeLabel;
    @FXML private ComboBox<String> highlightSelectionBox;
    @FXML private TextField chooseDegreeTextField;
    @FXML private Label availCreditsLabel;
    @FXML private Label userNameLabel;

// instructions table
    @FXML private TableView<SInstructionDTO> instructionsTable;
    @FXML private TableColumn<SInstructionDTO, String> breakPointColumn;
    @FXML private TableColumn<SInstructionDTO, Number> lineColumn;
    @FXML private TableColumn<SInstructionDTO, String> typeColumn;
    @FXML private TableColumn<SInstructionDTO, String> cyclesColumn;
    @FXML private TableColumn<SInstructionDTO, String> labelColumn;
    @FXML private TableColumn<SInstructionDTO, String> instructionColumn;
    @FXML private TableColumn<SInstructionDTO, String> genColumn;

    // Expansion table
    @FXML private TableView<SInstructionDTO> historyChainTable;
    @FXML private TableColumn<SInstructionDTO, Number> historyChainLine;
    @FXML private TableColumn<SInstructionDTO, String> historyChainType;
    @FXML private TableColumn<SInstructionDTO, String> historyChainCycles;
    @FXML private TableColumn<SInstructionDTO, String> historyChainLabel;
    @FXML private TableColumn<SInstructionDTO, String> historyChainInstruction;
    @FXML private TableColumn<SInstructionDTO, String> historyChangeGen;

    @FXML private Label instructionsCountLabel;

    // Debugger / Execution Section
    // Buttons
    @FXML private Button newRunButton;
    @FXML private Label cyclesMeterLabel;
    @FXML private Button executeButton;
    @FXML private Button resumeButton;
    @FXML private Button stepOverButton;
    @FXML private Button backstepButton;
    @FXML private Button stopButton;
    @FXML private RadioButton normalRadioButton;
    @FXML private RadioButton debugRadioButton;

    // Input Table
    @FXML private TableView<VariableRow> inputTable;
    @FXML private TableColumn<VariableRow, Integer> inputTableValueColumn;
    @FXML private TableColumn<VariableRow, String> inputTableVariableColumn;

    // Run Variables Table
    @FXML private TableView<VariableRow> programVariablesTable;
    @FXML private TableColumn<VariableRow, Integer> programVariablesTableValueColumn;
    @FXML private TableColumn<VariableRow, String> programVariablesTableVariableColumn;

    @FXML private Label programNameLabel;

    @Override
    public void setAppContext(AppContext context) {
        this.appContext = context;
        initializeUI(); // appContext needs to be defined before initialize is called.
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public MainController() {}

    private <T> void updateTableKeepSelection(TableView<T> table, List<T> newItems) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();

        table.getItems().setAll(newItems);

        if (selectedIndex >= 0 && selectedIndex < table.getItems().size()) {
            table.getSelectionModel().select(selectedIndex);
        }
    }

// ** Initializers **
    @FXML
    public void initializeUI() {
        initializeInstructionTable();
        initializeHighlightSelectionBox();
        initializedExpansionsTable();
        initializeInputTable();
        initializeProgramVariablesTable();
        initializeChooseDegreeTextField();
        updateUIOnExpansion();
        startAutoRefresh();

        collapseButton.setDisable(true);
        expandButton.setDisable(max_degree == 0);
    }

    private void startAutoRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> refreshExecutionAsync())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void refreshExecutionAsync() {
        Task<ExecutionDTO> task = new Task<>() {
            @Override
            protected ExecutionDTO call() throws Exception {
                if (appContext == null) return null;

                return NetCode.getExecutionDTO(appContext.getUsername());
            }
        };

        task.setOnSucceeded(e -> {
            ExecutionDTO dto = task.getValue();
            if (dto == null) return; // null is safe, skip update

            refreshExecutionUI(dto);
        });

        task.setOnFailed(e -> { /* intentionally empty */ });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // update all UI elements
    private void refreshExecutionUI(ExecutionDTO dto) {
        userNameLabel.setText(appContext.getUsername());
        availCreditsLabel.setText(String.format("Available Credits: %d", dto.credits));

    }

    private void initializeInstructionTable(){
        //lineColumn "#" — dynamic row numbering
        lineColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().line)
        );
        // typeColumn — string from getType()
        typeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().typeShort)
        );
        // cyclesColumn — integer from getCycles()
        cyclesColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().cyclesStr)
        );

        // labelColumn — string from getLabel()
        labelColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().sLabel)
        );

        // instructionColumn — string from getInstructionString()
        instructionColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().instructionString)
        );
        genColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().generation)
        );

        // prepare table list
        instructionsTable.setItems(FXCollections.observableArrayList());

        instructionsTable.setRowFactory(tv -> new TableRow<SInstructionDTO>() {
            @Override
            protected void updateItem(SInstructionDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                List<String> styles = new ArrayList<>();

                // Search highlight
                if (searchHighlightedLines.contains(item.line)) {
                    styles.add("-fx-background-color: yellow;");
                }

                // Line highlight
                if (lineHighlighted != null && item.line == lineHighlighted) {
                    styles.add("-fx-background-color: lightgreen;"); // stronger color
                }

                setStyle(String.join("", styles));
            }
        });

        initBreakpointColumn();

    }

    private void initializedExpansionsTable(){
        //  lineColumn "#" — dynamic row numbering
        historyChainLine.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().line)
        );
        // typeColumn — string from getType()
        historyChainType.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().typeShort)
        );
        // cyclesColumn — integer from getCycles()
        historyChainCycles.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().cyclesStr)
        );

        // labelColumn — string from getLabel()
        historyChainLabel.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().sLabel)
        );

        // instructionColumn — string from getInstructionString()
        historyChainInstruction.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().instructionString)
        );

        // prepare table list
        historyChainTable.setItems(FXCollections.observableArrayList());

        // add listener to request expansion list when instructionsTable line is selected
        instructionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                historyChainTable.getItems().clear();
                return;
            }
            // TODO: request chain from server
            /*
            List<SInstructionDTO> chain = new ArrayList<>();
            SInstructionDTO current = newSel.getParent();  // start from parent, not self

            // walk up to root
            while (current != null) {
                chain.add(current);
                current = current.getParent();
            }

            historyChainTable.getItems().setAll(chain);

             */
        });
    }

    private void initializeHighlightSelectionBox() {
        highlightSelectionBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return; // guard against null
            // Skip headers
            if (newV.equals("Variables:") || newV.equals("Labels:")) return;

            updateSearchHighlights(newV);
            instructionsTable.refresh();
        });
    }


    void initializeProgramVariablesTable(){
        programVariablesTableVariableColumn.setCellValueFactory(new PropertyValueFactory<>("variable"));
        programVariablesTableValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    // initialize textfield for degree, limiting to positive integers only
    void initializeChooseDegreeTextField(){
        chooseDegreeTextField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().matches("\\d*")) {
                return c; // allow digits only
            }
            return null; // block everything else
        }));
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

    private void initBreakpointColumn() {
        breakPointColumn.setCellFactory(col -> {
            TableCell<SInstructionDTO, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() >= getTableView().getItems().size()) {
                        setText(null);
                    } else {
                        SInstructionDTO row = getTableView().getItems().get(getIndex());
                        // check breakPoints set to persist mark
                        setText(breakPoints.contains(row.line) ? "⬤" : "");
                    }
                    setStyle("-fx-alignment: CENTER;");
                }
            };

            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    SInstructionDTO row = instructionsTable.getItems().get(cell.getIndex());
                    boolean marked = breakPoints.contains(row.line);

                    // toggle mark in breakPoints
                    if (marked) breakPoints.remove(row.line);
                    else breakPoints.add(row.line);

                    // update cell text immediately
                    cell.setText(!marked ? "⬤" : "");

                    onBreakpointClicked(row, !marked);
                }
            });

            return cell;
        });
    }

    private void applyTheme(String cssFile) {
        Scene scene = stage.getScene();

        scene.getStylesheets().clear();
        if (cssFile != null) {
            URL cssUrl = getClass().getResource(cssFile);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
        }
    }

    // Called when search filter changes
    private void updateSearchHighlights(String choice) {
        searchHighlightedLines.clear();
        if (choice != null && !choice.trim().isEmpty()) {
            String query = choice.toUpperCase().trim();
            for (SInstructionDTO instr : instructionsTable.getItems()) {
                String queryUpper = query.toUpperCase();
                String labelUpper = instr.sLabel.toUpperCase();
                String instrStrUpper = instr.instructionString.toUpperCase();

                boolean match = labelUpper.matches("\\b" + Pattern.quote(queryUpper) + "\\b") ||
                        instrStrUpper.matches(".*\\b" + Pattern.quote(queryUpper) + "\\b.*");

                if (match) {
                    searchHighlightedLines.add(instr.line);
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

    // updates search highlight selection box with available variables and labels
    private void resetHighlightSelectionBox(List<String> variables, List<String> labels) {
        // Combine into ObservableList with headers
        ObservableList<String> comboItems = FXCollections.observableArrayList();
        comboItems.add("Clear");
        comboItems.add("Variables:");
        comboItems.addAll(variables);
        comboItems.add("Labels:");
        comboItems.addAll(labels);

        // Set items in ComboBox
        highlightSelectionBox.setItems(comboItems);

        // Show only 10 items at a time in the popup
        highlightSelectionBox.setVisibleRowCount(10);

        // Select the first item by default (skip header)
        highlightSelectionBox.getSelectionModel().selectFirst();

        // Custom cell factory to style headers and prevent selection
        highlightSelectionBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item);
                    if (item.endsWith(":")) {
                        setStyle("-fx-font-weight: bold;"); // bold headers
                        setDisable(true); // prevent header selection
                    } else {
                        setStyle(""); // normal items
                        setDisable(false);
                    }
                }
            }
        });
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
            InfoMessage.showInfoMessage("Invalid input variable value","Please enter a non-negative integer.");
        }
        event.getTableView().refresh();
    }

    // updates instructions UI with highlight selection
    void updateInstructionsUI(SProgramViewDTO programView){
        /*
        instructionsTable.getItems().clear();
        instructionsTable.refresh();
        for(int i=1; i <= programView.sInstructionsDTOs.size(); i++){
            SInstructionDTO instr = programView.sInstructionsDTOs.get(i);
            instructionsTable.getItems().add(instr);
        }

        chooseDegreeTextField.setText("" + degree_selected);

        resetHighlightSelectionBox(programView);
        updateInstructionsTableSummary(programView);

         */

    }

    void updateHistoryTableUI(SProgramViewDTO programView){
        /*
        historyTable.getItems().clear();
        ArrayList<ExecutionHistory> history = engine.getHistory(programView.getName());
        for (ExecutionHistory executionHistory : history) {
            historyTable.getItems().add(executionHistory);
        }

         */
    }

    void updateInstructionsTableSummary(SProgramDTO programDTO){
        int count = programDTO.sInstructionsDTOs.size();
        int synth_count = countSynthetic(programDTO);
        int basic = count - synth_count;
        instructionsCountLabel.setText("Instructions: " + count + " (Basic: " + basic + " / Synthetic: " + synth_count + " )");
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
        int max_degree = Integer.parseInt(maxDegreeLabel.getText());
        int text_deg = Integer.parseInt(text.trim());
        this.degree_selected = Math.min(text_deg, max_degree);

        updateUIOnExpansion();

    }

    void updateUIOnExpansion(){

        breakPoints.clear();
        lineHighlighted = null;
        SProgramDTO programDTO;
        try {
            programDTO = NetCode.getSProgramDTO(appContext.getUsername(), degree_selected);
        }
        catch (Exception e) {
            InfoMessage.showInfoMessage("Error", e.getMessage());
            return;
        }

        programNameLabel.setText(programDTO.programName);
        degree_selected = programDTO.current_degree;
        max_degree = programDTO.maxDegree;
        maxDegreeLabel.setText(String.format("%d", max_degree));

        updateInputTable(programDTO.inputVariables);
        resetHighlightSelectionBox(programDTO.variablesUsed, programDTO.labelsUsed);
        updateTableKeepSelection(instructionsTable, programDTO.sInstructionsDTOs);
        chooseDegreeTextField.setText("" + degree_selected);

        updateInputControllers();

    }


    void updateInputTable(List<String> input_variables){
        inputTable.getItems().setAll(
                input_variables.stream().map(v -> new VariableRow(v, 0)).toList()
        );


    }

    void setInputTableValues(LinkedHashMap<String, Integer> variables) {
        inputTable.getItems().setAll(
                variables.entrySet().stream()
                        .map(e -> new VariableRow(e.getKey(), e.getValue()))
                        .toList()
        );
    }

    void updateInputControllers(){
        int max_degree = this.max_degree;
        boolean debug = false;

        newRunButton.setDisable(running);
        normalRadioButton.setDisable(running);
        debugRadioButton.setDisable(running);
        stopButton.setDisable(!(debug && running));
        backstepButton.setDisable(!(debug && running));
        stepOverButton.setDisable(!(debug && running));
        resumeButton.setDisable(!(debug && running));
        expandButton.setDisable(running || (degree_selected == max_degree));
        collapseButton.setDisable(running || (degree_selected == 0));
        chooseDegreeTextField.setDisable(running);
        executeButton.setDisable(running);
        inputTable.setDisable(running);

    }

    @FXML
    void onNewRunClicked(MouseEvent event) {
        cyclesMeterLabel.setText("Cycles: 0");
        programVariablesTable.getItems().clear();
        running = false;
        clearInstructionTableHighlight();
        updateInputControllers();
    }

    @FXML
    void onExecuteButtonClicked(MouseEvent event) {
        /*
        ExecutionContext result = engine.runProgram(programSelectionChoiceBox.getValue(),
                getInputVariablesFromUI(),
                degree_selected,
                debugRadioButton.isSelected(),
                breakPoints);
        running = !result.getExit();

        if(debugRadioButton.isSelected()) {
            highLightInstructionTableLine(result.getPC());
        }

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        updateProgramVariablesTable(result.getOrderedVariables(), false);
        updateInputControllers();

        if(result.getExit()){
            updateHistoryTableUI(selectedProgramView);

        }

         */
    }

    @FXML
    void onResumeClicked(MouseEvent event) {
        /*
        // execute single step
        ExecutionContext result = engine.resumeLoadedRun(breakPoints);

        running = !result.getExit();

        // populate table with result variables (later it'll be the same with execution context
        updateProgramVariablesTable(result.getOrderedVariables(), true);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        highLightInstructionTableLine(result.getPC());
        updateInputControllers();
        updateHistoryTableUI(selectedProgramView);

         */
    }

    @FXML
    void onStepOverClicked(MouseEvent event) {

        /*
        // execute single step
        ExecutionContext result = engine.stepLoadedRun();
        // populate table with result variables (later it'll be the same with execution context

        updateProgramVariablesTable(result.getOrderedVariables(), true);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        highLightInstructionTableLine(result.getPC());
        if(result.getExit()){
            running = false;
            updateInputControllers();
            updateHistoryTableUI(selectedProgramView);
        }

         */

    }


    @FXML
    void onBackStepClicked(MouseEvent event) {
        // TODO: Implement and verify
        /*
        ExecutionContext backstep = engine.backstepLoadedRun();
        updateProgramVariablesTable(backstep.getOrderedVariables(), true);
        cyclesMeterLabel.setText("Cycles: " + backstep.getCycles());
        highLightInstructionTableLine(backstep.getPC());

         */
    }

    @FXML
    void onStopClicked(MouseEvent event) {
        // TODO:: Implement and verify
        /*
        running = false;
        engine.stopLoadedRun();
        clearInstructionTableHighlight();
        updateInputControllers();
        updateHistoryTableUI(selectedProgramView);

         */
    }

    LinkedHashMap<String, Integer> getInputVariablesFromUI(){
        LinkedHashMap<String, Integer> input_variables = new LinkedHashMap<>();

        for (VariableRow row : inputTable.getItems()) {
            String key = row.getVariable();
            int value = row.getValue(); // primitive int
            if (key != null && !key.isEmpty()) { // skip empty keys if needed
                input_variables.put(key, value);
            }
        }
        return input_variables;
    }

    // Updates variables table from hashmap,
    // if highlight is True, highlights all rows with changed values
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
                        row.setStyle("-fx-background-color: orange");
                    }
                }
            });
            return row;
        });
    }


    public int countSynthetic(SProgramDTO programDTO){
        int count = 0;
        for(SInstructionDTO instr : programDTO.sInstructionsDTOs){
            if(Objects.equals(instr.typeShort, "S")){
                count++;
            }
        }
        return count;
    }


    private void onBreakpointClicked(SInstructionDTO instruction, boolean marked) {
        if(marked){
            breakPoints.add(instruction.line);
        }
        else{
            breakPoints.remove(instruction.line);
        }

    }

    @FXML
    private void onBackToDashboardClicked(MouseEvent event) {
        try {
            App.loadScreen("/fxml/dashboard.fxml");
        } catch (Exception e) {
            InfoMessage.showInfoMessage("Failed to load dashboard", e.getMessage());
        }
    }

}