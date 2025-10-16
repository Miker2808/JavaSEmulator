package Servlets.ExecutionServlets;

import ExecutionPool.ExecutionPool;
import Storage.ProgramsStorage;
import Storage.UserInstance;
import com.google.gson.Gson;
import dto.ExecutionRequestDTO;
import engine.SProgramView;
import engine.execution.ExecutionContext;
import engine.expander.SProgramExpander;
import engine.history.ExecutionHistory;
import engine.interpreter.SInterpreter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebServlet("/execution/execute")
public class ExecuteProgramServlet extends HttpServlet {

    private final Gson gson = new Gson();
    ExecutionRequestDTO executionRequestDTO;
    UserInstance userInstance;
    HttpServletResponse response;
    SProgramView usersProgram;
    boolean credits_exhausted = false;


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
        userInstance = userInstanceMap.get(username);

        if(userInstance == null){
            sendPlain(response, HttpServletResponse.SC_GONE, "User instance not found");
            return;
        }

        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");
        // get the program, as it was uploaded
        usersProgram = programsStorage.getProgramView(userInstance.getProgramSelected(),  userInstance.getProgramType());
        // assign expanded version based on degree
        usersProgram = SProgramExpander.expand(usersProgram, userInstance.getDegreeSelected());
        handleExecution(request, response);
    }

    protected void handleExecution(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();

        executionRequestDTO = gson.fromJson(reader, ExecutionRequestDTO.class);

        if (userInstance.isComputing()) {
            sendPlain(response, 429, "User instance is busy computing");
            return;
        }

        // Sends OK unless altered by the switch
        sendPlain(response, HttpServletResponse.SC_OK, String.format("Command '%s' called", executionRequestDTO.command));

        switch(executionRequestDTO.command){
            case "new_run" -> newRunCommand(response);
            case "execute" -> executeCommand(response);
            case "resume" -> resumeCommand(response);
            case "stepover" -> stepoverCommand(response);
            case "backstep" -> backstepCommand(response);
            case "stop" -> stopCommand(response);
        }
    }

    private void newRunCommand(HttpServletResponse response) throws IOException {
        userInstance.setInterpreter(null);
    }

    private void executeCommand(HttpServletResponse response) throws IOException {

        if(userInstance.getInterpreter() != null && userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is already running");
            return;
        }

        userInstance.setInterpreter(new SInterpreter(usersProgram.getInstructionsView(), executionRequestDTO.inputVariables));
        userInstance.setCurrentExecutionHistory(new ExecutionHistory(usersProgram, executionRequestDTO.inputVariables, userInstance.getDegreeSelected()));
        usersProgram.setNumRuns(usersProgram.getNumRuns() + 1);

        ExecutionPool.submitTask(userInstance, () -> {
            Set<Integer> breakpoints = new HashSet<>();
            if(executionRequestDTO.debug){
                breakpoints = executionRequestDTO.breakpoints;
            }

            ExecutionContext exec_context = userInstance.getInterpreter().runToBreakPoint(breakpoints);
            userInstance.getCurrentExecutionHistory().setContext(exec_context);

            if (exec_context.getExit()) {
                userInstance.getHistoryManager().addExecutionHistory(
                        usersProgram.getName(),
                        userInstance.getCurrentExecutionHistory());
            }
        });
    }

    private void resumeCommand(HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionPool.submitTask(userInstance, () -> {

            ExecutionContext exec_context = userInstance.getInterpreter().runToBreakPoint(executionRequestDTO.breakpoints);
            userInstance.getCurrentExecutionHistory().setContext(exec_context);

            if (exec_context.getExit()) {
                userInstance.getHistoryManager().addExecutionHistory(
                        usersProgram.getName(),
                        userInstance.getCurrentExecutionHistory());
            }
        });
    }

    private void stepoverCommand(HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionContext context = userInstance.getInterpreter().step(true);
        userInstance.getCurrentExecutionHistory().setContext(context);
        if(context.getExit()){
            userInstance.getHistoryManager().addExecutionHistory(usersProgram.getName(), userInstance.getCurrentExecutionHistory());
        }
    }

    private void backstepCommand(HttpServletResponse response) throws IOException {

        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionContext context = userInstance.getInterpreter().backstep();
        userInstance.getCurrentExecutionHistory().setContext(context);
    }

    private void stopCommand(HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }
        userInstance.getInterpreter().Stop();
        userInstance.getHistoryManager().addExecutionHistory(usersProgram.getName(), userInstance.getCurrentExecutionHistory());
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }

}
