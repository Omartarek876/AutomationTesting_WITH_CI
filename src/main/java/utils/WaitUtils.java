package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class WaitUtils {


    private static WebDriver driver(){
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call DriverFactory.InitializeDriver() first.");
        }
        return driver;
    }

    private static Duration defaultTimeout() {
        PropertiesReader.loadProperties();
        String configuredTimeout = System.getProperty("waitTimeOutSeconds", "30");
        try {
            long seconds = Long.parseLong(configuredTimeout);
            if (seconds <= 0) throw new NumberFormatException("timeout must be positive");
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("waitTimeOutSeconds must be a positive integer, got: " + configuredTimeout, e);
        }
    }

    public static void setImplicitWait(int timeoutSeconds) {
        driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutSeconds));
    }

    public static WebElement waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), defaultTimeout());
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), defaultTimeout());
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), defaultTimeout());
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForTextToBePresent(By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver(), defaultTimeout());
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForInvisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), defaultTimeout());
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static WebElement fluentWait(By locator, int timeoutSeconds, int pollingSeconds) {
        if (timeoutSeconds <= 0 || pollingSeconds <= 0) {
            throw new IllegalArgumentException("Timeout and polling interval must be positive");
        }
        FluentWait<WebDriver> wait = new FluentWait<>(driver())
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofSeconds(pollingSeconds))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });
    }

}
