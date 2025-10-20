package Servlets.Chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Date;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private static final int MAX_MESSAGES = 20;
    private static final Deque<String> messages = new ArrayDeque<>();
    private static long versionCounter = 0;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String body = sb.toString().trim();
        JsonObject json;
        try {
            json = gson.fromJson(body, JsonObject.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid JSON");
            return;
        }

        if (!json.has("username") || !json.has("message")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing username or message");
            return;
        }

        String username = json.get("username").getAsString();
        String message = json.get("message").getAsString();

        if (username.isEmpty() || message.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Empty username or message");
            return;
        }

        String time = TIME_FORMAT.format(new Date());
        String formattedMessage = String.format("| %s | %s: %s", time, username, message);

        if (messages.size() == MAX_MESSAGES) {
            messages.pollFirst(); // remove oldest
        }
        messages.addLast(formattedMessage);
        versionCounter++;

        resp.getWriter().println("OK");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        out.println("VERSION:" + versionCounter);

        StringBuilder chatBuilder = new StringBuilder();
        for (String msg : messages) {
            chatBuilder.append(msg).append("\n");
        }

        out.print(chatBuilder.toString());
    }
}
