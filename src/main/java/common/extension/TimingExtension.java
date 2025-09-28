package common.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Path RESULTS_FILE = Path.of("test-results.md");
    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();

    private static class WorkerResult {
        List<String> tests = new ArrayList<>();
        long totalDuration = 0;
    }

    private static final Map<String, WorkerResult> results = new ConcurrentHashMap<>();
    private static volatile boolean shutdownHookRegistered = false;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getDisplayName();
        startTimes.put(testName, System.currentTimeMillis());

        registerShutdownHook();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getDisplayName();
        Long start = startTimes.get(testName);
        if (start != null) {
            long duration = System.currentTimeMillis() - start;
            String worker = Thread.currentThread().getName();

            results.compute(worker, (w, res) -> {
                if (res == null) res = new WorkerResult();
                res.tests.add(testName + " (" + duration + " ms)");
                res.totalDuration += duration;
                return res;
            });
        }
    }

    private void registerShutdownHook() {
        if (!shutdownHookRegistered) {
            synchronized (TimingExtension.class) {
                if (!shutdownHookRegistered) {
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE.toFile()))) {
                            writer.println("| Worker | Tests | Total Duration (ms) |");
                            writer.println("|--------|-------|----------------------|");

                            results.forEach((worker, res) -> {
                                String tests = String.join("<br>", res.tests);
                                writer.printf("| %s | %s | %d |%n", worker, tests, res.totalDuration);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));
                    shutdownHookRegistered = true;
                }
            }
        }
    }
}
