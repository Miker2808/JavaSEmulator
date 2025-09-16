import engine.Engine;
import engine.ExecutionHistory;
import engine.SProgram;
import engine.execution.ExecutionResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class UserInterface extends Application{
    Engine engine;
    Scanner scanner; // Not Used

    @Override
    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent root = loader.load();

        // Get the controller (defined in FXML)
        MainController controller = loader.getController();

        // Setup the stage
        primaryStage.setTitle("S-Emulator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
