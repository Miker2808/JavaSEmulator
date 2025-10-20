package ui.elements;

import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.OkHttpClient;
import ui.netcode.NetCode;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ChatWindow {

    private final String username;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;

    private long lastVersion = -1;
    private static final String CHAT_URL = "http://localhost:8080/semulator-server/chat";

    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .build();

    public ChatWindow(String username) {
        this.username = username;
        initUI();
        startPolling();
    }

    private void initUI() {
        Stage stage = new Stage();
        stage.setTitle("Chat - " + username);
        stage.setWidth(500);
        stage.setHeight(400);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton = new Button("Send");
        sendButton.setMinWidth(70);
        sendButton.setPrefWidth(80);
        sendButton.setOnAction(e -> onSend());
        sendButton.setDefaultButton(true);

        HBox inputArea = new HBox(5, inputField, sendButton);
        inputArea.setPadding(new Insets(5));

        BorderPane root = new BorderPane();
        root.setCenter(chatArea);
        root.setBottom(inputArea);

        BorderPane.setMargin(chatArea, new Insets(5));

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void onSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        try {
            NetCode.sendMessageToServer(username, message);
            inputField.clear();
        }
        catch (IOException e) {
            InfoMessage.showInfoMessage("Failure", e.getMessage());
        }
    }

    private void startPolling() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> updateChatUI()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    // UI updater: called every 500ms
    private void updateChatUI() {
        String response = NetCode.getChatFromServer();
        if (response == null || response.isEmpty()) return;

        Scanner scanner = new Scanner(response);
        if (!scanner.hasNextLine()) return;

        String versionLine = scanner.nextLine();
        if (!versionLine.startsWith("VERSION:")) return;

        long version = Long.parseLong(versionLine.substring(8).trim());
        if (version == lastVersion) return;

        lastVersion = version;

        StringBuilder chatBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            chatBuilder.append(scanner.nextLine()).append("\n");
        }

        String chatText = chatBuilder.toString();
        Platform.runLater(() -> chatArea.setText(chatText));
    }
}
