package Servlets.DashboardServlets;

import DTOConverter.UserStatDTOConverter;
import Storage.ProgramsStorage;
import Storage.UserInstance;
import dto.DashboardDTO;
import dto.UserStatDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
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

        DashboardDTO dto = getDashboardDTO(request, userInstance);

        String dto_json = gson.toJson(dto);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(dto_json);
    }

    private DashboardDTO getDashboardDTO(HttpServletRequest request, UserInstance userInstance) throws IOException {

        // TODO: get all dashboard stuff, for now only username and credits
        DashboardDTO dto = new DashboardDTO();
        dto.credits = userInstance.getCreditsAvailable();
        dto.userStats = getUserStatsDTO();

        ServletContext context = getServletContext();
        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");
        dto.programStats = programsStorage.getFullProgramsStats();
        dto.functionStats = programsStorage.getFullFunctionsStats();

        return dto;

    }

    private ArrayList<UserStatDTO> getUserStatsDTO(){
        ServletContext context = getServletContext();
        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");

        ArrayList<UserStatDTO> userStats = new ArrayList<>();
        for(String username : userInstanceMap.keySet()){
            UserInstance instance = userInstanceMap.get(username);
            userStats.add(UserStatDTOConverter.toDTO(username, instance));
        }

        return userStats;
    }


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
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "User not found");
            return;
        }

        BufferedReader reader = request.getReader();
        Map<String, String> map = gson.fromJson(reader, Map.class);
        String program_name = map.get("program");
        String type = map.get("type");

        ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");
        if(Objects.equals(type, "PROGRAM") && programsStorage.containsProgram(program_name)){
            userInstance.setProgramSelected(program_name);
            userInstance.setProgramType(type);
        }
        else if(Objects.equals(type, "FUNCTION") && programsStorage.containsFunction(program_name)){
            userInstance.setProgramSelected(program_name);
            userInstance.setProgramType(type);
        }
        else{
            throw new IOException("Program was not found");
        }
        sendPlain(response, HttpServletResponse.SC_OK, "Success");

    }

    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
