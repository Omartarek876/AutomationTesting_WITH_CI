package utils;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class BrowserUtils {

    private static WebDriver driver(){
        return DriverFactory.getDriver();
    }

    @Step("Navigate to Url : [{url}]")
    public static void navigateToUrl(String url) {
        driver().navigate().to(url);
        dismissGoogleAdOverlayIfPresent();
    }

    /** Closes a Google vignette ad when the site injects one into the page or an iframe. */
    public static void dismissGoogleAdOverlayIfPresent() {
        WebDriver webDriver = driver();
        webDriver.switchTo().defaultContent();
        if (clickGoogleAdCloseButton(webDriver)) {
            return;
        }

        for (WebElement frame : webDriver.findElements(By.tagName("iframe"))) {
            try {
                webDriver.switchTo().defaultContent();
                webDriver.switchTo().frame(frame);
                if (clickGoogleAdCloseButton(webDriver)) {
                    webDriver.switchTo().defaultContent();
                    return;
                }
            } catch (WebDriverException ignored) {
                // Ads may remove their iframe while the page is changing.
            }
        }
        webDriver.switchTo().defaultContent();
    }

    private static boolean clickGoogleAdCloseButton(WebDriver webDriver) {
        List<By> closeButtonLocators = List.of(
                By.cssSelector("#dismiss-button, button[aria-label*='Close'], [role='button'][aria-label*='Close']"),
                By.xpath("//*[normalize-space(.)='Close' or @aria-label='Close' or @aria-label='Close ad' or @id='dismiss-button']")
        );
        for (By locator : closeButtonLocators) {
            for (WebElement closeButton : webDriver.findElements(locator)) {
                try {
                    if (closeButton.isDisplayed() && closeButton.isEnabled()) {
                        closeButton.click();
                        return true;
                    }
                } catch (WebDriverException ignored) {
                    // Try another close control if this one became stale or intercepted.
                }
            }
        }
        return false;
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
