import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // init
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
