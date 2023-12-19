package at.rtr.rmbt.filter;

import java.io.File;
import java.io.IOException;

public class DeadlockFilter implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Generate thread dump
        generateThreadDump();
        // You can log or handle the exception as needed
        System.err.println("Uncaught exception in thread " + t.getName() + ": " + e.getMessage());
    }

    private void generateThreadDump() {
        try {
            // Perform the thread dump generation here (e.g., using jstack)
            // You can use ProcessBuilder or Runtime.exec() to run jstack
            // Write the thread dump to a file or log it
            // Example:
            ProcessBuilder processBuilder = new ProcessBuilder("jstack", String.valueOf(ProcessHandle.current().pid()));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new File("thread_dump.txt")));
            processBuilder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
