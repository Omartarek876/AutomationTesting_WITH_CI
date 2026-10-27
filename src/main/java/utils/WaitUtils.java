package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class WaitUtils {


    private static WebDriver driver(){
        WebDriver currentDriver = DriverFactory.getDriver();
        if (currentDriver == null) throw new IllegalStateException("WebDriver has not been initialized for this test thread");
        return currentDriver;
    }

    private static int timeoutSeconds() {
        return Integer.parseInt(System.getProperty("waitTimeOutSeconds", "30"));
    }

    public static void setImplicitWait(int timeoutSeconds) {
        driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutSeconds));
    }

    public static WebElement waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickability(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForTextToBePresent(By locator, String text) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForInvisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(timeoutSeconds()));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static WebElement fluentWait(By locator, int timeoutSeconds, int pollingSeconds) {
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
