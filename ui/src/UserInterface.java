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


    // prints execution history from engine
    public void printHistory(){
        List<ExecutionHistory> history = engine.getExecutionHistory();

        if(history.isEmpty()){
            System.out.println("Execution history is empty");
            return;
        }

        System.out.print("Execution history:");

        for(int i=0; i<history.size(); i++){
            ExecutionHistory eh = history.get(i);

            System.out.printf("\nExecution %d\n", i+1);
            System.out.printf("Degree: %d\n", eh.getDegree());
            System.out.println("Input variables:");
            for(int j=0; j<eh.getVariables().size(); j++){
                System.out.printf("x%d = %s\n", j+1, eh.getVariables().get(j));
            }
            System.out.printf("y = %d\n", eh.getY());
            System.out.printf("Cycles: %d\n", eh.getCycles());
        }

    }

    // asks user for path to load instance
    // if failed, goes back to menu.
    public void loadInstance(){
        System.out.print("Path to load file (.semulator): ");
        String path = scanner.nextLine();

        try {
            this.engine = Engine.loadInstance(path);
            System.out.println("File loaded successfully.");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // asks user for path to save instance
    // if failed prompts to try again
    public void saveInstance(){
        while(true) {
            System.out.print("Path to save file (.semulator): ");
            String path = scanner.nextLine();
            try {
                engine.saveInstance(path);
                System.out.println("File saved successfully.");
                break;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

    }

    public void exitApplication(){
        System.out.println("\nGood Bye");
        System.exit(0);
    }


}
