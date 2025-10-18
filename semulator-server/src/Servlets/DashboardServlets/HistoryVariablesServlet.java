package Servlets.DashboardServlets;


import Storage.UserInstance;
import com.google.gson.Gson;
import engine.history.ExecutionHistory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/dashboard/history-status")
public class HistoryVariablesServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private String historyUsername = null;
    private int historyIndex;
    private String username = null;
    private UserInstance userInstance = null;
    private UserInstance historyUserInstance = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        username = request.getParameter("user");
        historyUsername = request.getParameter("history");

        try {
            historyIndex = Integer.parseInt(request.getParameter("index").strip());
        }
        catch (NumberFormatException e) {
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid index format");
            return;
        }

        if (username == null || username.isEmpty()) {
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "missing 'user' parameter");
            return;
        }

        ServletContext context = getServletContext();
        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        userInstance = userInstanceMap.get(username);

        if(userInstance == null){
            sendPlain(response, HttpServletResponse.SC_GONE, "User instance not found");
            return;
        }
        UserInstance historyUserInstance = userInstanceMap.get(historyUsername);
        if(historyUserInstance == null){
            historyUserInstance = userInstance;
        }

        ArrayList<ExecutionHistory> history = historyUserInstance.getHistoryManager().getExecutionHistory();

        if(historyIndex > history.size() + 1 || historyIndex < 1){
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid index");
            return;
        }

        LinkedHashMap<String, Integer> variables = history.get(historyIndex - 1).getVariables();

        String dto_json = gson.toJson(variables);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }

}
