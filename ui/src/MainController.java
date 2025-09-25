import engine.Engine;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.execution.ExecutionContext;
import engine.history.ExecutionHistory;
import engine.instruction.SInstruction;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import ui.ProgressBarDialog;
import ui.VariableRow;
import ui.VariableTablePopup;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class MainController {

    private final Engine engine = new Engine();
    // bunch of variables to make my lazy ass more comfortable
    private int degree_selected = 0;
    private Boolean running = false;
    private final Set<Integer> searchHighlightedLines = new HashSet<>();
    private final Set<Integer> breakPoints = new HashSet<>();
    private Integer lineHighlighted = null; // only one line at a time
    SProgramView selectedProgramView = null;
    private Stage stage;

    @FXML private ToggleGroup themeRadioMenu;

    @FXML
    private CheckMenuItem animationsMenuCheck;

    @FXML
    private ChoiceBox<String> programSelectionChoiceBox;
    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Label maxDegreeLabel;
    @FXML
    private ComboBox<String> highlightSelectionBox;
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
    private TableColumn<SInstruction, String> breakPointColumn;
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
    private Label instructionsCountLabel;

    // Expansion table
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

    // History Table
    @FXML
    private TableView<ExecutionHistory> historyTable;
    @FXML
    private TableColumn<ExecutionHistory, Number> numHistoryColumn;
    @FXML
    private TableColumn<ExecutionHistory, Number> degreeHistoryColumn;
    @FXML
    private TableColumn<ExecutionHistory, Number> yHistoryColumn;
    @FXML
    private TableColumn<ExecutionHistory, Number> cyclesHistoryColumn;


    // Debugger / Execution Section
    // Buttons
    @FXML
    private Button newRunButton;
    @FXML
    private Label cyclesMeterLabel;
    @FXML
    private Button executeButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Button stepOverButton;
    @FXML
    private Button stopButton;
    @FXML
    private RadioButton normalRadioButton;
    @FXML
    private RadioButton debugRadioButton;

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

    // History buttons
    @FXML
    private Button showInfoButton;
    @FXML
    private Button reRunButton;


    // opens an "Alert" window with information.
    private void showInfoMessage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // removes the ugly header
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        initializeTheme();
        initializeInstructionTable();
        initializeHighlightSelectionBox();
        initializedExpansionsTable();
        initializeInputTable();
        initializeProgramVariablesTable();
        initializeProgramSelectionChoiceBox();
        initializeHistoryTable();
        applySlideAnimation(resumeButton, Duration.millis(500));
        applySlideAnimation(stepOverButton, Duration.millis(500));
        applySlideAnimation(stopButton, Duration.millis(500));
        collapseButton.setDisable(true);
        expandButton.setDisable(true);

        initializeChooseDegreeTextField();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initializeTheme() {
        themeRadioMenu.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) return;

            RadioMenuItem selected = (RadioMenuItem) newToggle;
            String theme = selected.getText();

            switch (theme) {
                case "Default" -> applyTheme(null);
                case "Dark" -> applyTheme("dark.css");
                case "Blue" -> applyTheme("blue.css");
                case "Modern" -> applyTheme("modern.css");
            }
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
        running = false;

        String selected_program = programSelectionChoiceBox.getSelectionModel().getSelectedItem();
        selectedProgramView = engine.getSelectedProgram(selected_program);
        updateInstructionsUI(selectedProgramView);
        resetInputTable();
        programVariablesTable.getItems().clear();
        updateInputControllers();
        resetHighlightSelectionBox(selectedProgramView);
        updateUIOnExpansion();
        updateHistoryTableUI(selectedProgramView);


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

        initBreakpointColumn();

    }

    private void initializeHistoryTable(){
        numHistoryColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getNum())
        );
        degreeHistoryColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getDegree())
        );
        yHistoryColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getY())
        );
        // labelColumn — string from getLabel()
        cyclesHistoryColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getCycles())
        );


        // prepare table list
        historyTable.setItems(FXCollections.observableArrayList());

        historyTable.setRowFactory(tv -> new TableRow<ExecutionHistory>() {
            @Override
            protected void updateItem(ExecutionHistory item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }
            }
        });

        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            showInfoButton.setDisable(newSel == null);
            reRunButton.setDisable((newSel == null) || running);
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

    private void initializeHighlightSelectionBox() {
        highlightSelectionBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return; // guard against null
            // Skip headers
            if (newV.equals("Variables:") || newV.equals("Labels:")) return;

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

    private void resetHighlightSelectionBox(SProgramView programView) {
        List<String> variables = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Separate items into Variables and Labels
        for (String item : programView.getInstructionsView().getVariablesUsed()) {
            if (item.equals("EXIT") || item.startsWith("L")) {
                labels.add(item);
            } else {
                variables.add(item);
            }
        }
        for (String item : programView.getInstructionsView().getLabelsUsed()) {
            labels.add(item);
        }

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
            showInfoMessage("Invalid input variable value","Please enter a non-negative integer.");
        }
        event.getTableView().refresh();
    }

    // updates instructions UI with highlight selection
    void updateInstructionsUI(SProgramView programView){

        instructionsTable.getItems().clear();
        instructionsTable.refresh();
        for(int i=1; i <= programView.getInstructionsView().size(); i++){
            SInstruction instr = programView.getInstructionsView().getInstruction(i);
            instructionsTable.getItems().add(instr);
        }
        chooseDegreeTextField.setText("" + degree_selected);

        resetHighlightSelectionBox(programView);
        updateInstructionsTableSummary(programView);

    }

    void updateHistoryTableUI(SProgramView programView){
        historyTable.getItems().clear();
        ArrayList<ExecutionHistory> history = engine.getHistory(programView.getName());
        for (ExecutionHistory executionHistory : history) {
            historyTable.getItems().add(executionHistory);
        }
    }

    void updateInstructionsTableSummary(SProgramView programView){
        int count = programView.getInstructionsView().size();
        int synth_count = countSynthetic(programView.getInstructionsView());
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
        breakPoints.clear();
        SProgramView expanded = engine.getExpandedProgram(programSelectionChoiceBox.getValue(), degree_selected);
        updateInstructionsUI(expanded);
    }


    void resetInputTable(){
        List<String> input_variables = selectedProgramView.getInstructionsView().getInputVariablesUsed();
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
        boolean not_loaded = !engine.isProgramLoaded();
        boolean debug = debugRadioButton.isSelected();
        newRunButton.setDisable(not_loaded || running);
        normalRadioButton.setDisable(not_loaded || running);
        debugRadioButton.setDisable(not_loaded || running);
        stopButton.setDisable(not_loaded || !(debug && running));
        stepOverButton.setDisable(not_loaded || !(debug && running));
        resumeButton.setDisable(not_loaded || !(debug && running));
        expandButton.setDisable(not_loaded || running);
        collapseButton.setDisable(not_loaded || running);
        chooseDegreeTextField.setDisable(not_loaded || running);
        executeButton.setDisable(not_loaded || running);
        inputTable.setDisable(not_loaded || running);
        programSelectionChoiceBox.setDisable(not_loaded || running);
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
    }

    @FXML
    void onResumeClicked(MouseEvent event) {
        // execute single step
        ExecutionContext result = engine.resumeLoadedRun(breakPoints);

        running = !result.getExit();

        // populate table with result variables (later it'll be the same with execution context
        updateProgramVariablesTable(result.getOrderedVariables(), true);

        cyclesMeterLabel.setText("Cycles: " + result.getCycles());
        highLightInstructionTableLine(result.getPC());
        updateInputControllers();
        updateHistoryTableUI(selectedProgramView);
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
            running = false;
            updateInputControllers();
            updateHistoryTableUI(selectedProgramView);
        }

    }

    @FXML
    void onStopClicked(MouseEvent event) {
        running = false;
        engine.stopLoadedRun();
        clearInstructionTableHighlight();
        updateInputControllers();
        updateHistoryTableUI(selectedProgramView);
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


    public int countSynthetic(SInstructionsView instructions){
        int count = 0;
        for(SInstruction instr : instructions.getAllInstructions()){
            if(Objects.equals(instr.getType(), "synthetic")){
                count++;
            }
        }
        return count;

    }

    @FXML
    public void onReRunClicked(MouseEvent event) {
        ExecutionHistory selectedHistory = historyTable.getSelectionModel().getSelectedItem();
        if(selectedHistory != null){
            degree_selected = selectedHistory.getDegree();
            updateUIOnExpansion();
            setInputTableValues(selectedHistory.getInputVariables());
        }
    }

    @FXML
    public void onShowInfoClicked(MouseEvent event) {
        ExecutionHistory selectedHistory = historyTable.getSelectionModel().getSelectedItem();
        if(selectedHistory != null){
            new VariableTablePopup(selectedHistory.getVariables());
        }
    }

    private void applySlideAnimation(Button button, Duration duration) {
        // initial state if button is disabled
        if (animationsMenuCheck.isSelected()) {
                button.setScaleY(0);
        } else {
            button.setScaleX(1);
            button.setScaleY(1);
        }

        button.disabledProperty().addListener((obs, wasDisabled, isDisabled) -> {
            if(animationsMenuCheck.isSelected()) {
                ScaleTransition st = new ScaleTransition(duration, button);
                if (isDisabled) {
                    st.setToY(0);
                } else {
                    st.setToY(1);
                }
                st.play();
            }
        });

        animationsMenuCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if(!isSelected) {
                ScaleTransition st = new ScaleTransition(duration, button);
                st.setToY(1);
                st.setToX(1);
                st.play();
            }
            else {
                if(button.isDisabled()) {
                    ScaleTransition st = new ScaleTransition(duration, button);
                    st.setToY(0);
                    st.play();
                }
            }
        });
    }

    private void initBreakpointColumn() {
        breakPointColumn.setCellFactory(col -> {
            TableCell<SInstruction, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() >= getTableView().getItems().size()) {
                        setText(null);
                    } else {
                        SInstruction row = getTableView().getItems().get(getIndex());
                        // check breakPoints set to persist mark
                        setText(breakPoints.contains(row.getLine()) ? "⬤" : "");
                    }
                    setStyle("-fx-alignment: CENTER;");
                }
            };

            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    SInstruction row = instructionsTable.getItems().get(cell.getIndex());
                    boolean marked = breakPoints.contains(row.getLine());

                    // toggle mark in breakPoints
                    if (marked) breakPoints.remove(row.getLine());
                    else breakPoints.add(row.getLine());

                    // update cell text immediately
                    cell.setText(!marked ? "⬤" : "");

                    onBreakpointClicked(row, !marked);
                }
            });

            return cell;
        });
    }

    // example callback
    private void onBreakpointClicked(SInstruction instruction, boolean marked) {
        if(marked){
            breakPoints.add(instruction.getLine());
        }
        else{
            breakPoints.remove(instruction.getLine());
        }

    }






}