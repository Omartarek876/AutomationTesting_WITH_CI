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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestNgListener implements ISuiteListener, ITestListener, IInvokedMethodListener, IAlterSuiteListener {
    private static final AtomicInteger TEST_COUNTER = new AtomicInteger();
    private static final String TEST_NUMBER_ATTRIBUTE = TestNgListener.class.getName() + ".testNumber";
    private static final String TEST_FINISHED_ATTRIBUTE = TestNgListener.class.getName() + ".testFinished";

    @Override
    public void onStart(ISuite suite) {
        System.out.println("[START EXECUTION]");
        PropertiesReader.loadProperties();
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("END EXECUTION");
        AllureReportGenerator.generate();
    }

    @Override
    public void onTestStart(ITestResult result) {
        int testNumber = TEST_COUNTER.incrementAndGet();
        result.setAttribute(TEST_NUMBER_ATTRIBUTE, testNumber);
        System.out.println("[INFO] Starting test #" + testNumber + ": " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logTestFinished(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTestFinished(result, "FAIL");
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
        logTestFinished(result, "SKIP");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logTestFinished(result, "WARN");
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        logTestFinished(result, "TIMEOUT");
        onTestFailure(result);
    }

    private void logTestFinished(ITestResult result, String outcome) {
        synchronized (result) {
            if (result.getAttribute(TEST_FINISHED_ATTRIBUTE) != null) {
                return;
            }
            result.setAttribute(TEST_FINISHED_ATTRIBUTE, Boolean.TRUE);
        }
        Object assignedNumber = result.getAttribute(TEST_NUMBER_ATTRIBUTE);
        int testNumber = assignedNumber instanceof Integer
                ? (Integer) assignedNumber
                : TEST_COUNTER.incrementAndGet();
        if (assignedNumber == null) {
            result.setAttribute(TEST_NUMBER_ATTRIBUTE, testNumber);
        }
        System.out.println("[" + outcome + "] Finished test #" + testNumber + ": " + result.getName());
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
