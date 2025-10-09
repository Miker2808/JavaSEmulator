package ui.elements;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class VariableTablePopup {

    public VariableTablePopup(LinkedHashMap<String, Integer> variables) {
        // Convert map to observable list of entries
        ObservableList<Map.Entry<String, Integer>> data =
                FXCollections.observableArrayList(variables.entrySet());

        // Table setup
        TableView<Map.Entry<String, Integer>> table = new TableView<>(data);

        TableColumn<Map.Entry<String, Integer>, String> varCol = new TableColumn<>("Variable");
        varCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKey()));

        TableColumn<Map.Entry<String, Integer>, Number> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getValue()));

        table.getColumns().addAll(varCol, valCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setEditable(false);

        // Popup window
        Stage popup = new Stage();
        popup.setTitle("Variables Info");
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setScene(new Scene(new VBox(table), 200, 300));
        popup.showAndWait();
    }
}
