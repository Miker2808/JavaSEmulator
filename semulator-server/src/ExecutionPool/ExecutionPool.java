package ExecutionPool;

import Storage.UserInstance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPool {

    // Thread pool shared by all requests
    private static final ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
    );

    // Submit a new execution job
    public static void submitTask(UserInstance userInstance, Runnable taskLogic) {
        pool.submit(() -> {
            userInstance.setComputing(true);
            try {
                taskLogic.run();
                userInstance.setExceptionString("");
            } catch (Exception e) {
                userInstance.setExceptionString(e.getMessage());
            } finally {
                userInstance.setComputing(false);
            }

        });
    }

    public static void shutdown() {
        pool.shutdown();
    }
}
