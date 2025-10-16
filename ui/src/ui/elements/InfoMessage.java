package ui.elements;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class InfoMessage {
    private static Alert currentAlert;

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

                // When user closes it, clear the reference
                currentAlert.setOnHidden(event -> currentAlert = null);

                currentAlert.show();
            } catch (Exception ignored) {
                // Silently fail if UI not initialized or app shutting down
            }
        });
    }
}
