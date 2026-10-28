package com.automationexercise.framework.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ElementActions {
    private ElementActions() { }

    public static WebElement getElement(By locator) {
        return WaitUtils.waitForVisibility(locator);
    }

    public static String getText(By locator) {
        return getElement(locator).getText();
    }

    public static void clickElement(By locator) {
        getElement(locator).click();
    }

    public static void selectDropdownByVisibleText(By locator, String visibleText) {
        new Select(WaitUtils.waitForVisibility(locator)).selectByVisibleText(visibleText);
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

    public static void typeText(By locator, String text) {
        getElement(locator).sendKeys(text);
    }
}
