package utils;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void InitializeDriver() {
        PropertiesReader.loadProperties();
        InitializeDriver(System.getProperty("browser"), Boolean.parseBoolean(System.getProperty("headless")), Boolean.parseBoolean(System.getProperty("windowMaximize")));
    }

    public static void InitializeDriver(String browser, boolean headless, boolean maximizeWindow) {
        if (browser == null || browser.isBlank()) {
            throw new IllegalArgumentException("Browser must be configured (chrome, firefox, or edge)");
        }
        quitDriver();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2); // block images
        prefs.put("profile.managed_default_content_settings.ads", 2);    // block ads
        WebDriver driver;
        switch (browser.toLowerCase(Locale.ROOT).trim()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("prefs", prefs);
                if (headless) chromeOptions.addArguments("--headless=new");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
//                firefoxOptions.setExperimentalOption("prefs", prefs);
                if (headless) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setExperimentalOption("prefs", prefs);
                if (headless) edgeOptions.addArguments("--headless=new");
                driver = new EdgeDriver(edgeOptions);
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
        DRIVER.set(driver);
        if (maximizeWindow) {
            driver.manage().window().maximize();
        }
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    @Step("Quit the browser")
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
