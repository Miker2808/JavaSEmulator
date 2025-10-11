package Servlets;


import DTOConverter.SInstructionDTOConverter;
import Storage.ProgramsStorage;
import Storage.UserInstance;
import com.google.gson.Gson;
import dto.ExecutionDTO;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.expander.SProgramExpander;
import engine.instruction.SInstruction;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
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
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "User not found");
            return;
        }

        ExecutionDTO executionDTO = getExecutionDTO(request, userInstance);

        String dto_json = gson.toJson(executionDTO);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);

    }

    private ExecutionDTO getExecutionDTO(HttpServletRequest request, UserInstance userInstance) throws IOException {
        ExecutionDTO dto = new ExecutionDTO();
        ServletContext context = getServletContext();
        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");

        SProgramView usersProgram = programsStorage.getProgramView(userInstance.getProgramSelected(),  userInstance.getProgramType());
        int max_degree = usersProgram.getInstructionsView().getMaxDegree();

        if(userInstance.getDegreeSelected() > 0){
            usersProgram = SProgramExpander.expand(usersProgram, userInstance.getDegreeSelected());
        }

        dto.sInstructionsDTOs = new ArrayList<>();

        SInstructionsView instructionsView = usersProgram.getInstructionsView();
        for(SInstruction instr : instructionsView.getAllInstructions()){
            dto.sInstructionsDTOs.add(SInstructionDTOConverter.convertToDTO(instr));
        }

        dto.cycles = 0; // TODO: set cycles from interpreter in userInstance
        dto.running = userInstance.isRunning();
        dto.runPCHighlight = null;
        dto.maxDegree = max_degree;
        dto.degree = userInstance.getDegreeSelected();
        dto.programName = userInstance.getProgramSelected();
        dto.credits = userInstance.getCreditsAvailable();
        dto.searchHighlight = new ArrayList<>();
        dto.expansionHistoryDTO = new ArrayList<>();
        dto.inputVariables = new ArrayList<>();
        dto.inputValues = new ArrayList<>();
        dto.runVariables = new LinkedHashMap<>();

        return dto;
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
