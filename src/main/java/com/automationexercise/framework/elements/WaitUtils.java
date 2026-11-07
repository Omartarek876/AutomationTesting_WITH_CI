package com.automationexercise.framework.elements;

import com.automationexercise.framework.browser.DriverFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {


    private static WebDriver driver() {
        WebDriver currentDriver = DriverFactory.getDriver();
        if (currentDriver == null) throw new IllegalStateException("WebDriver has not been initialized for this test");
        return currentDriver;
    }

    private static int timeoutSeconds() {
        return Integer.parseInt(System.getProperty("waitTimeOutSeconds", "30"));
    }

    public static WebElement waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(By locator) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(By locator) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForTextToBePresent(By locator, String text) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForInvisibility(By locator) {
        return new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

}
