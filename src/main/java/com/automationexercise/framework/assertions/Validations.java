package com.automationexercise.framework.assertions;

import com.automationexercise.framework.elements.ElementActions;
import com.automationexercise.framework.elements.WaitUtils;
import org.openqa.selenium.By;
import org.testng.Assert;

public final class Validations {
    private Validations() { }
    public static void assertTextEquals(By locator, String expected) {
        Assert.assertEquals(ElementActions.getText(locator), expected, "Text mismatch for element: " + locator);
    }
    public static void assertTextContains(By locator, String expected) {
        String actual = ElementActions.getText(locator);
        Assert.assertTrue(actual.contains(expected), "Expected text to contain [" + expected + "] but found [" + actual + "]");
    }
    public static void assertElementIsVisible(By locator) {
        Assert.assertTrue(ElementActions.getElement(locator).isDisplayed(), "Expected visible element: " + locator);
    }
    public static void assertElementIsNotVisible(By locator) {
        Assert.assertTrue(WaitUtils.waitForInvisibility(locator), "Expected hidden or absent element: " + locator);
    }
    public static void assertButtonIsEnabled(By locator) {
        Assert.assertTrue(ElementActions.getElement(locator).isEnabled(), "Expected enabled element: " + locator);
    }
    public static void assertButtonIsDisabled(By locator) {
        Assert.assertFalse(ElementActions.getElement(locator).isEnabled(), "Expected disabled element: " + locator);
    }
    public static void assertRequiredFieldError(By locator, String expected) { assertTextEquals(locator, expected); }
}
