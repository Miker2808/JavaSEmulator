import Storage.ProgramsStorage;
import Storage.UserInstance;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashMap;
import java.util.Map;

@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // init

        ProgramsStorage programsStorage = new ProgramsStorage();
        ServletContext context = sce.getServletContext();
        context.setAttribute("programsStorage", programsStorage);

        Map<String, UserInstance> userInstanceMap = new HashMap<String, UserInstance>();
        context.setAttribute("userInstanceMap", userInstanceMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
