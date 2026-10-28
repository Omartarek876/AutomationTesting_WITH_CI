package com.automationexercise.framework.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static WebDriver driver;

    public static void initializeDriver() {
        initializeDriver(System.getProperty("browser"), Boolean.parseBoolean(System.getProperty("headless")), Boolean.parseBoolean(System.getProperty("windowMaximize")));
    }

    public static void initializeDriver(String browser, boolean headless, boolean maximizeWindow) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2); // block images
        prefs.put("profile.managed_default_content_settings.ads", 2);    // block ads
        if (browser == null || browser.isBlank()) {
            throw new IllegalArgumentException("Browser must be configured with the 'browser' system property");
        }
        WebDriver driver;
        switch (browser.toLowerCase().trim()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("prefs", prefs);
                if (headless) chromeOptions.addArguments("--headless=new");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
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
        DriverFactory.driver = driver;
        if (maximizeWindow && !headless) {
            driver.manage().window().maximize();
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void quitDriver() {
        try {
            if (driver != null) driver.quit();
        } finally {
            driver = null;
        }
    }
}
