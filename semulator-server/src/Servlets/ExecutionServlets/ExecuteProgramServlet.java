package Servlets.ExecutionServlets;

import Storage.UserInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.ExecutionRequestDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@WebServlet("/execution/execute")
public class ExecuteProgramServlet extends HttpServlet {

    private final Gson gson = new Gson();
    ExecutionRequestDTO executionRequestDTO;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("user");
        response.setContentType("text/plain;charset=UTF-8");

        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing 'user' parameter");
            return;
        }

        ServletContext context = getServletContext();
        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        UserInstance userInstance = userInstanceMap.get(username);

        if(userInstance == null){
            sendPlain(response, HttpServletResponse.SC_GONE, "User instance not found");
            return;
        }

        handleExecution(userInstance, request, response);
    }

    protected void handleExecution(UserInstance userInstance, HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();

        ExecutionRequestDTO dto = gson.fromJson(reader, ExecutionRequestDTO.class);
        System.out.println("command: " + dto.command);
        System.out.println("debug: " + dto.debug);
        System.out.println("generation: " + dto.generation);
        System.out.println("input variables:" + dto.inputVariables);
        System.out.println("breakpoints: " + dto.breakpoints);

        switch(dto.command){
            default -> sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown command");
        }

        sendPlain(response, HttpServletResponse.SC_OK, dto.command);
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }

}
