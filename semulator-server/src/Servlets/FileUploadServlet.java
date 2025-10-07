package Servlets;

import Storage.ProgramsStorage;
import Storage.UserInstance;
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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import engine.Engine;
import engine.SProgram;

@WebServlet("/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();

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
            ServletContext context = getServletContext();
            ProgramsStorage programsStorage = (ProgramsStorage) context.getAttribute("programsStorage");

            SProgram program = Engine.loadFromXML(xmlStream);

            programsStorage.addAll(program);

            HashSet<String> availableFunctions = programsStorage.getAvailableFunctionNames();
            availableFunctions.addAll(programsStorage.getAvailableProgramNames());

            program.validateProgram(availableFunctions);

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