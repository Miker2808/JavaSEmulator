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

        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");
        // get the program, as it was uploaded
        SProgramView originalProgram = programsStorage.getProgramView(userInstance.getProgramSelected(),  userInstance.getProgramType());
        // assign expanded version based on degree
        SProgramView usersProgram = SProgramExpander.expand(originalProgram, userInstance.getDegreeSelected());
        handleExecution(usersProgram, originalProgram, userInstance, request, response);
    }

    protected void handleExecution(SProgramView usersProgram,
                                   SProgramView originalProgram,
                                   UserInstance userInstance,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException

    {
        BufferedReader reader = request.getReader();

        ExecutionRequestDTO executionRequestDTO = gson.fromJson(reader, ExecutionRequestDTO.class);

        if (userInstance.isComputing()) {
            sendPlain(response, 429, "User instance is busy computing");
            return;
        }

        // Sends OK unless altered by the switch
        sendPlain(response, HttpServletResponse.SC_OK, String.format("Command '%s' called", executionRequestDTO.command));

        switch(executionRequestDTO.command){
            case "new_run" -> newRunCommand(userInstance);
            case "execute" -> executeCommand(usersProgram, originalProgram, userInstance, executionRequestDTO, response);
            case "resume" -> resumeCommand(userInstance, executionRequestDTO, response);
            case "stepover" -> stepoverCommand(userInstance, response);
            case "backstep" -> backstepCommand(userInstance, response);
            case "stop" -> stopCommand(userInstance, response);
        }
    }

    private void newRunCommand(UserInstance userInstance) {
        userInstance.setInterpreter(null);
    }

    private boolean validateExecute(
            UserInstance userInstance,
            ExecutionRequestDTO requestDTO,
            SProgramView usersProgram,
            SProgramView originalProgram,
            HttpServletResponse response
    ) throws IOException
    {
        int requiredGen = usersProgram.getInstructionsView().getRequiredGen();
        String genStr = genToStr(requiredGen);
        int runCost = runCostsMap(requestDTO.generation) + originalProgram.getAverage_credits_cost();

        if(userInstance.getInterpreter() != null && userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is already running");
            return true;
        }

        if(requestDTO.generation < requiredGen){
            String message = String.format("Program requires generation of at least %s to execute", genStr);
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, message);
            return true;
        }

        if(userInstance.getCreditsAvailable() < runCost){
            String message = String.format("At least %d credits required to execute at gen %s",
                    runCost, genStr);
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, message);
            return true;
        }

        return false;
    }

    private void updateAverageCredits(SProgramView originalProgram, int credits_cost){
        int num_runs =  originalProgram.getNumRuns();
        int avg_credits = originalProgram.getAverage_credits_cost();
        originalProgram.setAverage_credits_cost(avg_credits + (credits_cost -  avg_credits)/num_runs);
    }

    private void executeCommand(SProgramView usersProgram,
                                SProgramView originalProgram,
                                UserInstance userInstance,
                                ExecutionRequestDTO requestDTO,
                                HttpServletResponse response) throws IOException {

        if(validateExecute(userInstance, requestDTO, usersProgram, originalProgram, response)) return;

        userInstance.setInterpreter(new SInterpreter(usersProgram.getInstructionsView(),
                                                requestDTO.inputVariables,
                                                userInstance.getCreditsAvailRef(),
                                                userInstance.getCreditsUsedRef()
                                                    ));

        userInstance.setCurrentExecutionHistory(new ExecutionHistory(usersProgram,
                requestDTO.inputVariables,
                userInstance.getDegreeSelected()));

        originalProgram.addNumRuns(1);
        userInstance.setTotalRuns(userInstance.getTotalRuns() + 1);
        userInstance.addCreditsAvailable(-1 * runCostsMap(requestDTO.generation));
        userInstance.addCreditsUsed(runCostsMap(requestDTO.generation));

        ExecutionPool.submitTask(userInstance, () -> {
            Set<Integer> breakpoints = new HashSet<>();
            if(requestDTO.debug){
                breakpoints = requestDTO.breakpoints;
            }

            ExecutionContext exec_context = userInstance.getInterpreter().runToBreakPoint(breakpoints);
            userInstance.getCurrentExecutionHistory().setContext(exec_context);

            if (exec_context.getExit()) {
                updateAverageCredits(originalProgram ,exec_context.getCycles());
                userInstance.getHistoryManager().addExecutionHistory(userInstance.getCurrentExecutionHistory());
            }
        });
    }

    private void resumeCommand(UserInstance userInstance, ExecutionRequestDTO requestDTO, HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionPool.submitTask(userInstance, () -> {

            ExecutionContext exec_context = userInstance.getInterpreter().runToBreakPoint(requestDTO.breakpoints);
            userInstance.getCurrentExecutionHistory().setContext(exec_context);

            if (exec_context.getExit()) {
                userInstance.getHistoryManager().addExecutionHistory(userInstance.getCurrentExecutionHistory());
            }
        });
    }

    private void stepoverCommand(UserInstance userInstance, HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionContext context = userInstance.getInterpreter().step(true);
        userInstance.getCurrentExecutionHistory().setContext(context);
        if(context.getExit()){
            userInstance.getHistoryManager().addExecutionHistory(userInstance.getCurrentExecutionHistory());
        }
    }

    private void backstepCommand(UserInstance userInstance, HttpServletResponse response) throws IOException {

        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }

        ExecutionContext context = userInstance.getInterpreter().backstep();
        userInstance.getCurrentExecutionHistory().setContext(context);
    }

    private void stopCommand(UserInstance userInstance, HttpServletResponse response) throws IOException {
        if(userInstance.getInterpreter() == null || !userInstance.getInterpreter().isRunning()){
            sendPlain(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "User instance is not running");
            return;
        }
        userInstance.getInterpreter().Stop();
        userInstance.getHistoryManager().addExecutionHistory(userInstance.getCurrentExecutionHistory());
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.reset();
        response.setStatus(statusCode);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }

    protected static int runCostsMap(int generation){
        return switch(generation){
            case 1 -> 5;
            case 2 -> 100;
            case 3 -> 500;
            default -> 1000;
        };
    }

    protected static String genToStr(int gen){
        return switch(gen){
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> "IV";
        };
    }

}
