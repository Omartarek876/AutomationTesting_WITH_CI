package utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class AlertActions {

    private static WebDriver driver(){
        return DriverFactory.getDriver();
    }

    public static void acceptAlert() {
        Alert alert = driver().switchTo().alert();
        alert.accept();
    }

    public static void dismissAlert() {
        Alert alert = driver().switchTo().alert();
        alert.dismiss();
    }

    public static String getAlertText() {
        Alert alert = driver().switchTo().alert();
        return alert.getText();
    }

    public static void sendTextToAlert(String text) {
        Alert alert = driver().switchTo().alert();
        alert.sendKeys(text);
        alert.accept();
    }

    public static void sendAuthToAlert(String name, String password) {
        Alert alert = driver().switchTo().alert();
        alert.sendKeys(name + Keys.TAB + password);
    }
}
