package Servlets.ExecutionServlets;


import Storage.UserInstance;
import com.google.gson.Gson;
import dto.ExecutionDTO;
import enums.RunState;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/execution")
public class ExecutionServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        ExecutionDTO executionDTO = getExecutionDTO(userInstance);

        String dto_json = gson.toJson(executionDTO);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);

    }

    private ExecutionDTO getExecutionDTO(UserInstance userInstance) throws IOException {
        ExecutionDTO dto = new ExecutionDTO();

        dto.programName = userInstance.getProgramSelected();
        dto.credits = userInstance.getCreditsAvailable();
        dto.computing = userInstance.isComputing();
        dto.state = RunState.IDLE;
        if(userInstance.getInterpreter() != null) {
            dto.state = userInstance.getInterpreter().getState();
            dto.cycles = userInstance.getInterpreter().getCycles();
            dto.steps = userInstance.getInterpreter().getSteps();
            dto.runPCHighlight = userInstance.getInterpreter().getPC();
            dto.runVariables = userInstance.getInterpreter().getOrderedVariables();
            dto.genUsage = userInstance.getInterpreter().getGenUsage();
        }
        return dto;
    }



    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
