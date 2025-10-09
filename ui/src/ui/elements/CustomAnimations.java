package ui.elements;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CustomAnimations {

    public static void animateButton(Button button, CheckMenuItem checkMenuItem) {
        Timeline timeline = new Timeline();

        for (int i = 0; i <= 100; i++) {
            double hue = i * 3.6; // 0 to 360 degrees
            Color color = Color.hsb(hue, 0.8, 0.9);
            String colorHex = String.format("#%02X%02X%02X",
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255));

            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 50),
                    e -> button.setStyle("-fx-background-color: " + colorHex + ";"));
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setCycleCount(Timeline.INDEFINITE);

        checkMenuItem.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                timeline.play();
            } else {
                timeline.stop();
                button.setStyle("-fx-border-radius: 10px;"); // Reset with radius
            }
        });

        // Start immediately if already checked
        if (checkMenuItem.isSelected()) {
            timeline.play();
        }
    }
}
