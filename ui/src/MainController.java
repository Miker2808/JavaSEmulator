import engine.Engine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.io.File;

public class MainController {

    Engine engine = new Engine();

    @FXML
    private Button loadProgramButton;

    @FXML
    private TextField loadedFilePathTextField;

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
            }
            catch(Exception e){
                //loadedFilePathTextField.setText(e.getMessage());
                // add alert window
                showInfoMessage("Failed to load XML file", e.getMessage());
            }
        }
    }

    private void showInfoMessage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // removes the ugly header
        alert.showAndWait();
    }


}