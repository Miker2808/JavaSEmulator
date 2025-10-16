package Servlets.DashboardServlets;

import Storage.ProgramsStorage;
import Storage.UserInstance;
import engine.Engine;
import engine.SProgram;
import engine.instruction.QuoteInstruction;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();

        String username = request.getParameter("user");

        ServletContext context = getServletContext();

        if (username == null || username.isEmpty()) {
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Missing 'user' parameter");
            return;
        }

        Map<String, UserInstance> userInstanceMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        UserInstance userInstance = userInstanceMap.get(username);
        if(userInstance == null){
            sendPlain(response, HttpServletResponse.SC_GONE, "User not found");
            return;
        }

        if (parts.size() != 1) {
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Exactly one file must be uploaded");
            return;
        }

        Part filePart = parts.iterator().next();
        if (!filePart.getName().equals("file")) {
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, "Form field must be named 'file'");
            return;
        }

        try (InputStream xmlStream = filePart.getInputStream()) {

            ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");

            SProgram program = Engine.loadFromXML(xmlStream); // unmarshal with jaxb

            programsStorage.validateProgramNotUsed(program); // Throws exception if function or program name is used

            // get shared programs and function names
            HashSet<String> availableFunctions = programsStorage.getAvailableFunctionNames();
            availableFunctions.addAll(programsStorage.getAvailableProgramNames());

            // validate if program is of valid syntax, and functions are available.
            program.validateProgram(availableFunctions);

            // From here: program is valid, and can be stored
            // update user instance on how many programs and functions he uploaded
            userInstance.setNumProgramsUploaded(userInstance.getNumProgramsUploaded() + 1);
            userInstance.setNumFunctionsUploaded(userInstance.getNumFunctionsUploaded() + program.getSFunctions().getSFunction().size());

            program.setUploader(username);
            programsStorage.addAll(program);

            // share updated available programs with QuoteInstruction
            QuoteInstruction.setProgramViews(programsStorage.getFullProgramsView());

            sendPlain(response, HttpServletResponse.SC_OK, "Successfully uploaded file: " + filePart.getSubmittedFileName());

        }
        catch (Exception e) {
            getServletContext().log("Error uploading program", e); // logs full stack trace
            sendPlain(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }


    private void sendPlain(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}