package Servlets.ExecutionServlets;

import DTOConverter.SInstructionDTOConverter;
import Exceptions.UserNotFoundException;
import Storage.ProgramsStorage;
import Storage.UserInstance;
import com.google.gson.Gson;
import dto.SProgramDTO;
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
import java.util.Map;

@WebServlet("/execution/get-program")
public class GetProgramServlet extends HttpServlet {
    private final Gson gson = new Gson();

    String username;
    Integer degree;
    UserInstance userInstance;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try{
            validateRequest(request);
        }
        catch (UserNotFoundException e){
            sendPlain(response, HttpServletResponse.SC_GONE, e.getMessage());
        }
        catch(Exception e){
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        SProgramDTO executionDTO = getSProgramDTO(userInstance);

        userInstance.setDegreeSelected(degree);

        String dto_json = gson.toJson(executionDTO);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);

    }

    void validateRequest(HttpServletRequest request) throws Exception {

        username = request.getParameter("user");
        try {
            degree = Integer.parseInt(request.getParameter("degree"));
        }
        catch (NumberFormatException e) {
            throw new Exception("Degree must be an integer");
        }

        ServletContext context = getServletContext();
        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
         userInstance = userInstanceMap.get(username);

        if (username == null || degree == null || username.isEmpty()) {
            throw new Exception("Invalid request parameters");
        }
        if (userInstance == null) {
            throw new UserNotFoundException("User instance not found");
        }

    }

    private SProgramDTO getSProgramDTO(UserInstance userInstance) throws IOException {
        SProgramDTO dto = new SProgramDTO();
        ServletContext context = getServletContext();
        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");

        SProgramView usersProgram = programsStorage.getProgramView(userInstance.getProgramSelected(), userInstance.getProgramType());

        int max_degree = usersProgram.getInstructionsView().getMaxDegree();
        degree = Math.max(Math.min(degree, max_degree), 0);

        usersProgram = SProgramExpander.expand(usersProgram, degree);

        dto.sInstructionsDTOs = new ArrayList<>();

        SInstructionsView instructionsView = usersProgram.getInstructionsView();
        for(SInstruction instr : instructionsView.getAllInstructions()){
            dto.sInstructionsDTOs.add(SInstructionDTOConverter.convertToDTO(instr));
        }
        dto.current_degree = degree;
        dto.maxDegree = max_degree;
        dto.variablesUsed = usersProgram.getInstructionsView().getVariablesUsed();
        dto.labelsUsed = usersProgram.getInstructionsView().getLabelsUsed();
        dto.inputVariables = usersProgram.getInstructionsView().getInputVariablesUsed();
        dto.programName = usersProgram.getName();

        return dto;
    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
