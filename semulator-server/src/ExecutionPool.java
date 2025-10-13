import Storage.UserInstance;

import java.util.concurrent.*;

public class ExecutionPool {

    // Thread pool shared by all requests
    private static final ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
    );

    // Submit a new emulator execution job
    public static void submitTask(UserInstance userInstance, Runnable taskLogic) {
        pool.submit(() -> {
            try {
                userInstance.setRunning(true);
                taskLogic.run(); // Perform long computation
                userInstance.setExceptionString("");
            } catch (Exception e) {
                userInstance.setExceptionString(e.getMessage());
            }
            userInstance.setRunning(false);

        });
    }
}
