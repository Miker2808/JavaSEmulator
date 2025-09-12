package ui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressBarDialog {
    private final float seconds;

    public ProgressBarDialog(float seconds) {
        this.seconds = seconds;
    }

    public void start() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Loading File...");

        ProgressBar progressBar = new ProgressBar(0);
        VBox box = new VBox(10, new Label("Loading file..."), progressBar);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        Scene scene = new Scene(box, 300, 80);
        dialog.setScene(scene);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int steps = 100;
                for (int i = 1; i <= steps; i++) {
                    Thread.sleep((long) (seconds * 1000 / steps));
                    updateProgress(i, steps);
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(ev -> dialog.close());
        task.setOnFailed(ev -> dialog.close());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        dialog.showAndWait();
    }
}
