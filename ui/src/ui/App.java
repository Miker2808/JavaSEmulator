package ui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.elements.InfoMessage;
import ui.storage.AppContext;

public class App extends Application{
    private static Stage primaryStage;
    private static final AppContext context = new AppContext();


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        InfoMessage.setStage(primaryStage);
        loadScreen("/fxml/login.fxml");

    }

    public static void loadScreen(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Parent root;
        try {
            root = loader.load();
        }
        catch (Exception e) {
            System.out.println("loadScreen exception");
            return;
        }
        Object controller = loader.getController();
        if (controller instanceof StatefulController) {
            StatefulController c = (StatefulController) controller;
            c.setAppContext(context);
            c.setStage(primaryStage);
        }

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } else {
            primaryStage.getScene().setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
