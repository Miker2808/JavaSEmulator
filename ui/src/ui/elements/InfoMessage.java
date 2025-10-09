package ui.elements;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class InfoMessage {
    // opens an "Alert" window with information.
    public static void showInfoMessage(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // removes the ugly header
        alert.showAndWait();
    }
}
