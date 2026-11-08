package utils;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class BrowserUtils {

    private static WebDriver driver(){
        return DriverFactory.getDriver();
    }

    @Step("Navigate to Url : [{url}]")
    public static void navigateToUrl(String url) {
        driver().navigate().to(url);
    }

    public static String GetCurrentUrl() {
        return driver().getCurrentUrl();
    }

    public static void navigateBack() {
        driver().navigate().back();
    }

    public static void navigateForward() {
        driver().navigate().forward();
    }

    public static void refreshPage() {
        driver().navigate().refresh();
    }

    public static void switchToTab(int index) {
        List<String> tabs = new ArrayList<>(driver().getWindowHandles());
        if (index >= 0 && index < tabs.size()) {
            driver().switchTo().window(tabs.get(index));
        } else {
            throw new IllegalArgumentException("Invalid tab index: " + index);
        }
    }

    public static void closeCurrentTabAndSwitchToFirst() {
        driver().close();
        List<String> tabs = new ArrayList<>(driver().getWindowHandles());
        driver().switchTo().window(tabs.get(0));
    }
}
