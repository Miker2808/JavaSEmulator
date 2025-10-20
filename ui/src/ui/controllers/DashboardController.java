package ui.controllers;

import dto.DashboardDTO;
import dto.ExecutionHistoryDTO;
import dto.SProgramViewStatsDTO;
import dto.UserStatDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import ui.App;
import ui.NetworkException;
import ui.StatefulController;
import ui.elements.ChatWindow;
import ui.elements.InfoMessage;
import ui.elements.VariableTablePopup;
import ui.netcode.NetCode;
import ui.storage.AppContext;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController implements StatefulController {
    private AppContext appContext;
    private Stage stage;
    private Timeline refreshPullTimeline;
    private String selectedUser;

    // <-- FXML -->
    @FXML private Label usernameLabel;
    @FXML private Button loadFileButton;
    @FXML private TextField loadedFilePathTextField;
    @FXML private TextField creditsTextField;
    @FXML private Label availableCreditsLabel;

    @FXML private TableColumn<SProgramViewStatsDTO, Number> programAvgCreditsColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, Number> programMaxDegreeColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, String> programNameColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, Number> programNumInstColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, Number> programRunsCountColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, String> programUploaderColumn;
    @FXML private TableView<SProgramViewStatsDTO> programsTable;

    @FXML private TableColumn<SProgramViewStatsDTO, Number> functionMaxDegree;
    @FXML private TableColumn<SProgramViewStatsDTO, String> functionNameColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, Number> functionNumInstColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, String> functionParentColumn;
    @FXML private TableColumn<SProgramViewStatsDTO, String> functionUploaderColumn;
    @FXML private TableView<SProgramViewStatsDTO> functionsTable;

    @FXML private TableColumn<UserStatDTO, Number> userStatsAvailCreditsColumn;
    @FXML private TableColumn<UserStatDTO, Number> userStatsCreditsSpentColumn;
    @FXML private TableColumn<UserStatDTO, Number> userStatsFunctionsUploadedColumn;
    @FXML private TableColumn<UserStatDTO, Number> userStatsProgramsUploadedColumn;
    @FXML private TableColumn<UserStatDTO, Number> userStatsRunsCountColumn;
    @FXML private TableColumn<UserStatDTO, String> userStatsUsernameColumn;
    @FXML private TableView<UserStatDTO> usersTable;

    @FXML private Button deselectButton;

    @FXML private TableColumn<ExecutionHistoryDTO, Number> historyCyclesCol;
    @FXML private TableColumn<ExecutionHistoryDTO, Number> historyDegreeCol;
    @FXML private TableColumn<ExecutionHistoryDTO, String> historyGenCol;
    @FXML private TableColumn<ExecutionHistoryDTO, String> historyNameCol;
    @FXML private TableColumn<ExecutionHistoryDTO, Number> historyNumCol;
    @FXML private TableColumn<ExecutionHistoryDTO, String> historyTypeCol;
    @FXML private TableColumn<ExecutionHistoryDTO, Number> historyYCol;
    @FXML private TableView<ExecutionHistoryDTO> historyTable;

    @FXML private Button reRunButton;
    @FXML private Button showStatusButton;

    @Override
    public void setAppContext(AppContext context) {
        this.appContext = context;
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        initializeUI();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initializeUI(){
        initCreditsTextField();
        initializeProgramStatsTable();
        initializeFunctionStatsTable();
        initializeUserStatsTable();
        initializeHistoryTable();
        startAutoRefresh();
    }

    private void initializeFunctionStatsTable(){
        functionMaxDegree.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().maxDegree)
        );
        functionNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().name)
        );
        functionNumInstColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num_instructions)
        );
        functionUploaderColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().uploader)
        );
        functionParentColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().parentProgram)
        );
        functionsTable.setItems(FXCollections.observableArrayList());
    }

    private void initializeProgramStatsTable(){
        programAvgCreditsColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().average_credits_cost)
        );
        programMaxDegreeColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().maxDegree)
        );
        programNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().name)
        );
        programNumInstColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num_instructions)
        );
        programRunsCountColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().numRuns)
        );
        programUploaderColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().uploader)
        );

        programsTable.setItems(FXCollections.observableArrayList());
    }

    private void initializeUserStatsTable(){
        userStatsAvailCreditsColumn.setCellValueFactory(cell ->
                new SimpleLongProperty(cell.getValue().avail_credits)
        );
        userStatsCreditsSpentColumn.setCellValueFactory(cell ->
                new SimpleLongProperty(cell.getValue().credits_spent)
        );
        userStatsProgramsUploadedColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num_uploaded_programs)
        );
        userStatsFunctionsUploadedColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num_uploaded_functions)
        );
        userStatsRunsCountColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num_runs)
        );
        userStatsUsernameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().username)
        );
        functionsTable.setItems(FXCollections.observableArrayList());

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if(newSelection != null){
                        selectedUser = newSelection.username;
                    }
                }
        );
    }

    private void initializeHistoryTable(){
        historyNumCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().num)
        );
        historyTypeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().type)
        );
        historyNameCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().userstring)
        );
        historyGenCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().gen)
        );
        historyDegreeCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().degree)
        );

        historyYCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().y)
        );
        historyCyclesCol.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().cycles)
        );

        // prepare table list
        historyTable.setItems(FXCollections.observableArrayList());

        historyTable.setRowFactory(tv -> new TableRow<ExecutionHistoryDTO>() {
            @Override
            protected void updateItem(ExecutionHistoryDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                }
            }
        });

        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            showStatusButton.setDisable(newSel == null);
            reRunButton.setDisable((newSel == null));
        });

    }

    private void initCreditsTextField(){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-9][0-9]*") || newText.isEmpty()) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> formatter = new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                filter
        );

        creditsTextField.setTextFormatter(formatter);
    }


    private void startAutoRefresh() {
        refreshPullTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> refreshDashboardAsync())
        );
        refreshPullTimeline.setCycleCount(Animation.INDEFINITE);
        refreshPullTimeline.play();
    }

    private void refreshDashboardAsync() {
        Task<DashboardDTO> task = new Task<>() {
            @Override
            protected DashboardDTO call() throws Exception {
                if (appContext == null) return null;

                return NetCode.getDashboardDTO(appContext.getUsername(), selectedUser);
            }
        };

        task.setOnSucceeded(e -> {
            DashboardDTO dto = task.getValue();
            if (dto == null) return; // null is safe, skip update

            refreshDashboard(dto);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (Objects.requireNonNull(ex) instanceof NetworkException ne) {
                if (ne.getHttpCode() == 410) {
                    refreshPullTimeline.stop();
                    InfoMessage.showInfoMessage("Server reset", ne.getMessage());
                    App.loadScreen("/fxml/login.fxml");
                }
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void refreshDashboard(DashboardDTO dto) {
        // update all UI elements
        usernameLabel.setText(appContext.getUsername());
        availableCreditsLabel.setText(String.format("Available Credits: %d", dto.credits));

        updateTableKeepSelection(programsTable, dto.programStats);
        updateTableKeepSelection(functionsTable, dto.functionStats);
        updateTableKeepSelection(usersTable, dto.userStats);
        updateTableKeepSelection(historyTable, dto.executionHistory);

    }

    // updates table with list of objects but keeps selection at same index it was
    private <T> void updateTableKeepSelection(TableView<T> table, List<T> newItems) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();

        table.getItems().setAll(newItems);

        if (selectedIndex >= 0 && selectedIndex < table.getItems().size()) {
            table.getSelectionModel().select(selectedIndex);
        }
    }


    @FXML
    void onClickedLoadProgramButton(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());

        if (selectedFile != null) {

            try {
                Response response = NetCode.uploadFile(appContext.getUsername(), selectedFile);

                if(response.isSuccessful()) {
                    loadedFilePathTextField.setText(selectedFile.getAbsolutePath());
                    InfoMessage.showInfoMessage("Success", response.body().string());
                }
                else{
                    InfoMessage.showInfoMessage("Failed to load XML file", response.body().string());
                }

            }
            catch(Exception e){
                // add alert window
                InfoMessage.showInfoMessage("Failed to load XML file", "Network error");
            }
        }
    }

    @FXML
    void onChargeCreditsClicked(MouseEvent event) {

        try {
            int credits = Integer.parseInt(creditsTextField.getText());
            NetCode.chargeCredits(appContext.getUsername(), credits);
        }
        catch(NumberFormatException num_e){
            return;
        }
        catch(Exception e){
            InfoMessage.showInfoMessage("Failed to charge credits", "Network error");
        }
    }

    @FXML
    void onExecuteProgramClicked(MouseEvent event) {

        SProgramViewStatsDTO selected = programsTable.getSelectionModel().getSelectedItem();
        if(selected == null) return;
        String program_name = programNameColumn.getCellObservableValue(selected).getValue();

        try {
            Response response = NetCode.selectProgram(appContext.getUsername(), program_name, "PROGRAM");
            if(response.isSuccessful()) {
                App.loadScreen("/fxml/main.fxml");
            }
            else{
                InfoMessage.showInfoMessage("Failed to select program", response.body().string());
            }
            response.close();
        }
        catch(Exception e){
            InfoMessage.showInfoMessage("Failed to reach the server", "Network error");
        }
    }

    @FXML
    void onExecuteFunctionClicked(MouseEvent event) {
        SProgramViewStatsDTO selected = functionsTable.getSelectionModel().getSelectedItem();
        if(selected == null) return;
        String program_name = functionNameColumn.getCellObservableValue(selected).getValue();

        try {
            Response response = NetCode.selectProgram(appContext.getUsername(), program_name, "FUNCTION");
            if(response.isSuccessful()) {
                App.loadScreen("/fxml/main.fxml");
            }
            else{
                InfoMessage.showInfoMessage("Failed to select function", response.body().string());
            }
            response.close();
        }
        catch(Exception e){
            InfoMessage.showInfoMessage("Failed to reach the server", "Network error");
        }
    }

    @FXML
    void onReRunButtonClicked(MouseEvent event) {
        ExecutionHistoryDTO selectedHistory = historyTable.getSelectionModel().getSelectedItem();
        if(selectedHistory != null){

            try {
                Response response = NetCode.selectProgram(appContext.getUsername(), selectedHistory.name, selectedHistory.type.toUpperCase());
                if(response.isSuccessful()) {
                    appContext.setDegree(selectedHistory.degree);
                    appContext.setInputVariables(selectedHistory.inputVariables);
                    App.loadScreen("/fxml/main.fxml");
                }
                else{
                    InfoMessage.showInfoMessage("Failed to select program", response.body().string());
                }
                response.close();

            }catch(Exception e){
                InfoMessage.showInfoMessage("Failed to reach the server", "Network error");
            }
        }
    }

    @FXML
    void onShowStatusButton(MouseEvent event) {
        ExecutionHistoryDTO selectedHistory = historyTable.getSelectionModel().getSelectedItem();
        if(selectedHistory != null){
            try {
                LinkedHashMap<String, Integer> variables = NetCode.getHistoryStatusVariables(appContext.getUsername(),
                        selectedUser,
                        selectedHistory.num);
                // ^ throws exception if an issue makes it impossible to set a popup
                String title = String.format("Variables Info #%d", selectedHistory.num);
                new VariableTablePopup(title,variables);

            } catch (Exception e) {
                InfoMessage.showInfoMessage("Failure", e.getMessage());
            }
        }
    }

    @FXML
    void onDeselectButtonClicked(MouseEvent event) {
        usersTable.getSelectionModel().clearSelection();
        selectedUser = null;
    }

    @FXML
    void onChatButtonClicked(MouseEvent event) {
        new ChatWindow(appContext.getUsername());
    }
}
