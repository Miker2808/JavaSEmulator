package ui.elements;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InfoMessage {
    private static Alert currentAlert;

    private static Stage stage;

    public static void setStage(Stage stage) {
        InfoMessage.stage = stage;
    }

    public static void showInfoMessage(String title, String message) {
        Platform.runLater(() -> {
            try {
                // If there's an existing alert still visible, update it
                if (currentAlert != null && currentAlert.isShowing()) {
                    currentAlert.setTitle(title);
                    currentAlert.setContentText(message);
                    currentAlert.getDialogPane().requestLayout();
                    return;
                }

                // Otherwise, create a new alert
                currentAlert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                currentAlert.setTitle(title);
                currentAlert.setHeaderText(null);

                if(stage != null) {
                    currentAlert.initOwner(stage);
                    currentAlert.initModality(Modality.WINDOW_MODAL); // optional, makes it modal over the owner
                }

                // When user closes it, clear the reference
                currentAlert.setOnHidden(event -> currentAlert = null);

                currentAlert.show();
            } catch (Exception ignored) {
                // Silently fail if UI not initialized or app shutting down
            }
        });
    }
}
