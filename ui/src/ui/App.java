package ui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import ui.controllers.MainController;
import ui.storage.AppContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application{
    private static Stage primaryStage;
    private static final AppContext context = new AppContext();


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        loadScreen("/fxml/login.fxml");

    }

    public static void loadScreen(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Parent root = null;
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
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        launch(args);
    }


}
