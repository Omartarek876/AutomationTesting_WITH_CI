package utils;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;

public class ElementActions {

    private static WebDriver driver(){
        return DriverFactory.getDriver();
    }

    /////////////////////////////////////////////Bacis Methods//////////////////////////////////////////////////
    public static WebElement getElement(By locator) {
        return WaitUtils.waitForVisibility(locator);
    }

    public static List<WebElement> getElements(By locator) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static String getText(By locator) {
        return getElement(locator).getText();
    }

    /////////////////////////////////////////////Mouse Methods//////////////////////////////////////////////////
    @Step("Click on element located in : [{locator}]")
    public static void clickElement(By locator) {
        getElement(locator).click();
    }

    public static void doubleClickOnElement(By locator) {
        Actions actions = new Actions(driver());
        actions.doubleClick(getElement(locator)).perform();
    }

    public static void rightClickOnElement(By locator) {
        Actions actions = new Actions(driver());
        actions.contextClick(getElement(locator)).perform();
    }

    public static void clickAndHold(By locator) {
        Actions actions = new Actions(driver());
        actions.clickAndHold(getElement(locator)).perform();
    }

    public static void dragAndDrop(By sourceLocator, By targetLocator) {
        Actions actions = new Actions(driver());
        actions.dragAndDrop(getElement(sourceLocator), getElement(targetLocator)).perform();
    }

    public static void scrollToElement(By locator) {
        Actions actions = new Actions(driver());
        actions.scrollToElement(getElement(locator)).perform();
    }

    public static void hoverOverElement(By locator) {
        Actions actions = new Actions(driver());
        actions.moveToElement(getElement(locator)).perform();
    }

    public static void hoverAndClick(By locator) {
        Actions actions = new Actions(driver());
        actions.moveToElement(getElement(locator)).click().perform();
    }

    public static void releaseMouse() {
        Actions actions = new Actions(driver());
        actions.release().perform();
    }

    public static void selectDropdownByVisibleText(By locator, String visibleText) {
        new Select(WaitUtils.waitForVisibility(locator)).selectByVisibleText(visibleText);
    }

    public static void selectDropdownByValue(By locator, String value) {
        new Select(WaitUtils.waitForVisibility(locator)).selectByValue(value);
    }

    public static void selectDropdownByIndex(By locator, int index) {
        new Select(WaitUtils.waitForVisibility(locator)).selectByIndex(index);
    }

    public static void checkCheckbox(By locator) {
        WebElement checkbox = getElement(locator);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public static void uncheckCheckbox(By locator) {
        WebElement checkbox = getElement(locator);
        if (checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public static void selectRadioButton(By locator) {
        WebElement radioButton = getElement(locator);
        if (!radioButton.isSelected()) {
            radioButton.click();
        }
    }

    /////////////////////////////////////////////Keyboard Methods//////////////////////////////////////////////////
    @Step("Enter text [{text}] in element located in [{locator}]")
    public static void typeText(By locator, String text) {
        getElement(locator).sendKeys(text);
    }

    public static void enterKey(By locator, Keys key) {
        getElement(locator).sendKeys(key);
    }

    public static void enterKeyCombination(By locator, Keys key1, Keys key2) {
        getElement(locator).sendKeys(Keys.chord(key1, key2));
    }

    public static void clearAndTypeText(By locator, String text) {
        getElement(locator).clear();
        getElement(locator).sendKeys(text);
    }

    public static void copyAndPasteText(By sourceLocator, By targetLocator) {
        getElement(sourceLocator).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        getElement(sourceLocator).sendKeys(Keys.chord(Keys.CONTROL, "c"));
        getElement(targetLocator).sendKeys(Keys.chord(Keys.CONTROL, "v"));
    }


    /////////////////////////////////////////////Generic Methods//////////////////////////////////////////////////
    public static void uploadFile(By Locator, String path) {
        ElementActions.getElement(Locator).sendKeys(path);
    }

    public static void basicAuthUsingRobot(String name, String pass) throws AWTException {
        Robot robot = new Robot();
        robot.delay(500); // Allow the browser authentication prompt to appear.

        // Type username
        for (char c : name.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }

        // Press TAB to move to the password field
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);

        // Type password
        for (char c : pass.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }

        // Press ENTER to submit the credentials
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

}
