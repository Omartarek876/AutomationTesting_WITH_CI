package com.automationexercise.framework.browser;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public final class AlertActions {
    private AlertActions() { }
    private static Alert alert() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) throw new IllegalStateException("WebDriver has not been initialized");
        return driver.switchTo().alert();
    }
    public static void acceptAlert() { alert().accept(); }
    public static void dismissAlert() { alert().dismiss(); }
    public static String getAlertText() { return alert().getText(); }
    public static void sendTextToAlert(String text) { alert().sendKeys(text); alert().accept(); }
    public static void sendAuthToAlert(String username, String password) { alert().sendKeys(username + Keys.TAB + password); }
}
