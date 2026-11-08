package utils;

import io.qameta.allure.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.ByteArrayInputStream;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TestNgListener implements ISuiteListener, ITestListener, IInvokedMethodListener, IAlterSuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("[START EXECUTION]");
        PropertiesReader.loadProperties();
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("END EXECUTION");
        generateAllureReport();
    }

    private void generateAllureReport() {
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

    private void openReport(File reportFile) {
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

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[INFO] Starting test: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[PASS] Test passed: " + result.getName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod() || ITestResult.FAILURE != testResult.getStatus()) {
            return;
        }
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.attachment(
                    "Failure Screenshot",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    AttachmentOptions.empty()
            );
        } catch (RuntimeException e) {
            System.err.println("Could not capture failure screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[SKIP] Test skipped: " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("[WARN] Test failed but within success percentage: " + result.getName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println("[TIMEOUT] Test failed due to timeout: " + result.getName());
        onTestFailure(result);
    }

    @Override
    public void alter(List<XmlSuite> suites) {
        String projectName = System.getProperty("allure.suite.name", "DefaultProjectName");

        for (XmlSuite suite : suites) {
            suite.setName(projectName);

            List<XmlTest> originalTests = new ArrayList<>(suite.getTests());
            suite.getTests().clear();

            for (XmlTest test : originalTests) {
                List<XmlClass> classes = test.getClasses();

                if (classes.isEmpty()) {
                    suite.getTests().add(test);
                } else if (classes.size() > 1) {
                    // Create separate XmlTest for each class
                    for (XmlClass clazz : classes) {
                        XmlTest newTest = new XmlTest(suite);
                        newTest.setName(getFeatureName(clazz));
                        List<XmlClass> singleClassList = new ArrayList<>();
                        singleClassList.add(clazz);
                        newTest.setXmlClasses(singleClassList);
                    }
                } else {
                    // Single class, just rename test based on feature
                    XmlClass clazz = classes.get(0);
                    test.setName(getFeatureName(clazz));
                    suite.getTests().add(test);
                }
            }
        }
    }

    private String getFeatureName(XmlClass xmlClass) {
        try {
            Class<?> clazz = Class.forName(xmlClass.getName()); // load the actual class
            Feature feature = clazz.getAnnotation(Feature.class);
            if (feature != null) return feature.value();
            return clazz.getSimpleName(); // fallback to real class simple name
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return xmlClass.getName(); // fallback to full name if class not found
        }
    }
}
