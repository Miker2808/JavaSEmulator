package ui;

import engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class UserInterface extends Application{
    Engine engine;
    Scanner scanner; // Not Used

    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent root = loader.load();

        // Setup the stage
        primaryStage.setTitle("S-Emulator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Get the controller (defined in FXML)
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }


}
