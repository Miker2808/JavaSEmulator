package Servlets.ExecutionServlets;

import DTOConverter.SInstructionDTOConverter;
import Storage.ProgramsStorage;
import Storage.UserInstance;
import com.google.gson.Gson;
import dto.SInstructionDTO;
import engine.SProgramView;
import engine.expander.SProgramExpander;
import engine.instruction.SInstruction;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/execution/get-expansion-history")
public class expansionHistoryServlet extends HttpServlet {
    private final Gson gson = new Gson();

    String username;
    Integer degree;
    Integer line;
    UserInstance userInstance;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            validateRequest(request);
        }
        catch(Exception e){
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        List<SInstructionDTO> dto = getExpansionHistoryDTO(userInstance, degree, line);

        String dto_json = gson.toJson(dto);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);
    }

    void validateRequest(HttpServletRequest request) throws Exception {

        username = request.getParameter("user");
        try {
            degree = Integer.parseInt(request.getParameter("degree"));
            line = Integer.parseInt(request.getParameter("line"));
        }
        catch (NumberFormatException e) {
            throw new Exception("'degree' and 'line' parameters must be integers");
        }

        ServletContext context = getServletContext();
        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        userInstance = userInstanceMap.get(username);

        if (userInstance == null) {
            throw new Exception("User instance not found");
        }
    }

    List<SInstructionDTO> getExpansionHistoryDTO(UserInstance userInstance, Integer degree, Integer line) {
        ServletContext context = getServletContext();
        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");

        SProgramView usersProgram = programsStorage.getProgramView(userInstance.getProgramSelected(), userInstance.getProgramType());
        int max_degree = usersProgram.getInstructionsView().getMaxDegree();
        usersProgram = SProgramExpander.expand(usersProgram, degree);

        List<SInstructionDTO> chain = new ArrayList<>();

        if(degree > max_degree || degree < 0){
            return chain;
        }

        if(line < 1 || usersProgram.getInstructionsView().size() < line){
            return chain;
        }

        SInstruction current = usersProgram.getInstructionsView().getInstruction(line);
        current = current.getParent();
        while(current != null){

            chain.add(SInstructionDTOConverter.convertToDTO(current));
            current = current.getParent();
        }

        return chain;

    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
