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

public class TestNgListener implements ISuiteListener, ITestListener, IInvokedMethodListener, IAlterSuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("[START EXECUTION]");
        PropertiesReader.loadProperties();
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("[END EXECUTION]");
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
        if (method.isTestMethod() && ITestResult.FAILURE == testResult.getStatus()) {
            WebDriver driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot screenshotDriver) {
                try {
                    byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);
                    Allure.attachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
                } catch (RuntimeException e) {
                    System.err.println("Could not capture failure screenshot: " + e.getMessage());
                }
            }
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

                if (classes.size() > 1) {
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
