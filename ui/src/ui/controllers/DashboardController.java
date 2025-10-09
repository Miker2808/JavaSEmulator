package ui.controllers;

import dto.DashboardDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import okhttp3.Response;
import ui.StatefulController;
import ui.elements.InfoMessage;
import ui.elements.ProgressBarDialog;
import ui.netcode.NetCode;
import ui.storage.AppContext;

import java.io.File;
import java.util.function.UnaryOperator;

public class DashboardController implements StatefulController {
    private AppContext appContext;
    private Stage stage;

    @FXML private Label usernameLabel;
    @FXML private Button loadFileButton;
    @FXML private TextField loadedFilePathTextField;
    @FXML private TextField creditsTextField;
    @FXML private Label availableCreditsLabel;

    @Override
    public void setAppContext(AppContext context) {
        this.appContext = context;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize(){
        initCreditsTextField();
        startAutoRefresh();
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
                1, // default value
                filter
        );

        creditsTextField.setTextFormatter(formatter);
    }


    private void startAutoRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> refreshDashboardAsync())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void refreshDashboardAsync() {
        Task<DashboardDTO> task = new Task<>() {
            @Override
            protected DashboardDTO call() throws Exception {
                if (appContext == null) return null;

                return NetCode.getDashboardDTO(appContext.getUsername());
            }
        };

        task.setOnSucceeded(e -> {
            DashboardDTO dto = task.getValue();
            if (dto == null) return; // null is safe, skip update

            refreshDashboard(dto);
        });

        task.setOnFailed(e -> { /* intentionally empty */ });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void refreshDashboard(DashboardDTO dto) {
        // update all UI elements
        usernameLabel.setText(appContext.getUsername());
        availableCreditsLabel.setText(String.format("Available Credits: %d", dto.credits));
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
        int credits = Integer.parseInt(creditsTextField.getText());
        try {
            Response response = NetCode.chargeCredits(appContext.getUsername(), credits);
            if(response.isSuccessful()) {

            }
        }
        catch(Exception e){
            InfoMessage.showInfoMessage("Failed to charge credits", "Network error");
        }
    }
}
