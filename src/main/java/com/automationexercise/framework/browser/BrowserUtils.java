package com.automationexercise.framework.browser;

import org.openqa.selenium.WebDriver;
import java.util.ArrayList;
import java.util.List;

public final class BrowserUtils {
    private BrowserUtils() { }
    private static WebDriver driver() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) throw new IllegalStateException("WebDriver has not been initialized");
        return driver;
    }
    public static void navigateToUrl(String url) { driver().navigate().to(url); }
    public static String getCurrentUrl() { return driver().getCurrentUrl(); }
    public static void navigateBack() { driver().navigate().back(); }
    public static void navigateForward() { driver().navigate().forward(); }
    public static void refreshPage() { driver().navigate().refresh(); }
    public static void switchToTab(int index) {
        List<String> tabs = new ArrayList<>(driver().getWindowHandles());
        if (index < 0 || index >= tabs.size()) throw new IllegalArgumentException("Invalid tab index: " + index);
        driver().switchTo().window(tabs.get(index));
    }
    public static void closeCurrentTabAndSwitchToFirst() {
        WebDriver driver = driver();
        driver.close();
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabs.isEmpty()) throw new IllegalStateException("No browser tabs remain");
        driver.switchTo().window(tabs.get(0));
    }
}
