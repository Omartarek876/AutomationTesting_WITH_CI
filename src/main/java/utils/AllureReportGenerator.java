package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Generates and opens the Allure HTML report after a TestNG suite finishes. */
public final class AllureReportGenerator {

    private AllureReportGenerator() {
    }

    public static void generate() {
        boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
        List<String> command = windows
                ? List.of("cmd.exe", "/c", "mvn", "allure:report")
                : List.of("mvn", "allure:report");

        try {
            Process process = new ProcessBuilder(command)
                    .directory(new File(System.getProperty("user.dir")))
                    .inheritIO()
                    .start();
            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                System.err.println("Allure report generation timed out after 5 minutes.");
                return;
            }
            if (process.exitValue() == 0) {
                File reportFile = new File(System.getProperty("user.dir"),
                        "target" + File.separator + "allure-report" + File.separator + "index.html");
                System.out.println("Allure report generated at: " + reportFile.getAbsolutePath());
                openReport(reportFile);
            } else {
                System.err.println("Allure report generation failed with exit code " + process.exitValue() + ".");
            }
        } catch (IOException e) {
            System.err.println("Could not start Allure report generation. Ensure Maven is available on PATH: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Allure report generation was interrupted.");
        }
    }

    private static void openReport(File reportFile) {
        if (!reportFile.isFile()) {
            System.err.println("Allure report index.html was not found; skipping browser launch.");
            return;
        }
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                System.out.println("Desktop browsing is unavailable; open the report manually: " + reportFile.getAbsolutePath());
                return;
            }
            URI reportUri = reportFile.toURI();
            Desktop.getDesktop().browse(reportUri);
            System.out.println("Opened the Allure report in the default browser.");
        } catch (IOException | UnsupportedOperationException e) {
            System.err.println("Could not open the Allure report automatically: " + e.getMessage());
        }
    }
}
