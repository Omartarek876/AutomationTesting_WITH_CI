package com.automationexercise.support;

import com.automationexercise.framework.browser.DriverFactory;
import com.automationexercise.framework.config.PropertiesReader;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class TestNgListener implements ISuiteListener, IInvokedMethodListener {

    @Override
    public void onStart(ISuite suite) {
        PropertiesReader.loadProperties();
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

}
