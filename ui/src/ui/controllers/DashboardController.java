package ui.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Response;
import ui.StatefulController;
import ui.elements.InfoMessage;
import ui.elements.ProgressBarDialog;
import ui.netcode.NetCode;
import ui.storage.AppContext;

import java.io.File;

public class DashboardController implements StatefulController {
    private AppContext appContext;
    private Stage stage;

    @FXML private Label usernameLabel;
    @FXML private Button loadFileButton;
    @FXML private TextField loadedFilePathTextField;

    @Override
    public void setAppContext(AppContext context) {
        this.appContext = context;
        refreshUI(); // update username immediately
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void refreshUI() {
        if (appContext != null) {
            usernameLabel.setText(appContext.getUsername());
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
}
