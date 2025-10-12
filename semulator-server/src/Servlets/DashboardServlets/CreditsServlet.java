package Servlets.DashboardServlets;

import Storage.UserInstance;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/credits")
public class CreditsServlet extends HttpServlet {


    // Requires user parameter
    // if charge parameter exists, adds credits available
    // wrong use of charge parameter equals to not using it
    // anyway returns available credits.
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
        Map<String, UserInstance> userMap = (Map<String, UserInstance>) context.getAttribute("userInstanceMap");
        if(userMap.containsKey(username)){

            String charge = request.getParameter("charge");
            if(charge != null && !charge.isEmpty()){
                try{
                    int value = Integer.parseInt(charge);

                    if(value > 0) {
                        userMap.get(username).addCreditsAvailable(value);
                    }
                }
                catch(Exception ignored){}
            }

            int credits_available = userMap.get(username).getCreditsAvailable();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(credits_available + "");
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Username doesn't exist");
        }
    }

}
