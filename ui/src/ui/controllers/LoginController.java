package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import ui.App;
import ui.NetworkException;
import ui.StatefulController;
import ui.elements.InfoMessage;
import ui.netcode.NetCode;
import ui.storage.AppContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements StatefulController{

    private AppContext context;
    private Stage stage;

    @FXML private TextField usernameField;

    @Override
    public void setAppContext(AppContext context) {
        this.context = context;
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onLoginClicked() throws Exception {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            InfoMessage.showInfoMessage("Missing credentials", "Username is required");
            return;
        }

        try{
            NetCode.login(username); // throws exception if not 200 OK

            // Save user info to context
            context.setUsername(username);
            context.reset();
            // Transition to setup screen
            App.loadScreen("/fxml/dashboard.fxml");

        }
        catch (NetworkException ne){
            InfoMessage.showInfoMessage("Login failed", ne.getMessage());
        }
        catch (Exception e){
            InfoMessage.showInfoMessage("Login failed", "Network error");
        }




    }
}
