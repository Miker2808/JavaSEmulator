package ui;

import javafx.stage.Stage;
import ui.storage.AppContext;

public interface StatefulController {
    void setAppContext(AppContext context);
    void setStage(Stage stage);
}
