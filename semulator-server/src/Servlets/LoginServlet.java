package Servlets;

import Storage.UserInstance;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("user");
        response.setContentType("text/plain;charset=UTF-8");

        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing 'user' parameter");
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid username format.\nOnly letters (a-z, A-Z), numbers (0-9), and underscores (_) are allowed.");
            return;
        }

        boolean success = false;

        ServletContext context = getServletContext();
        Map<String, UserInstance> userMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        if(!userMap.containsKey(username)){
            userMap.put(username, new UserInstance());
            success = true;
        }

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Successfully added user " + username);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("Username is taken");
        }
    }
}
