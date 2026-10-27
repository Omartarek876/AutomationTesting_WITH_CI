package utils;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class Validations {

    /* -------------------- TEXT VALIDATIONS -------------------- */

    @Step("Assert element text equals [{expectedText}]")
    public static void assertTextEquals(By locator, String expectedText) {
        String actualText = ElementActions.getText(locator);
        Assert.assertEquals(actualText, expectedText, "Text mismatch for element: " + locator
        );
    }

    @Step("Assert element text contains [{expectedText}]")
    public static void assertTextContains(By locator, String expectedText) {
        String actualText = ElementActions.getText(locator);
        Assert.assertTrue(actualText.contains(expectedText),"Expected text to contain [" + expectedText + "] but found [" + actualText + "]");
    }

    /* -------------------- VISIBILITY VALIDATIONS -------------------- */

    @Step("Assert element is visible")
    public static void assertElementIsVisible(By locator) {
        Assert.assertTrue(ElementActions.getElement(locator).isDisplayed(),"Expected element to be visible but it was not: " + locator);
    }

    @Step("Assert element is NOT visible")
    public static void assertElementIsNotVisible(By locator) {
        Assert.assertTrue(WaitUtils.waitForInvisibility(locator), "Expected element to be hidden or absent: " + locator);
    }

    /* -------------------- ENABLE / DISABLE VALIDATIONS -------------------- */

    @Step("Assert button located in [{locator}] is disabled")
    public static void assertButtonIsDisabled(By locator) {
        Assert.assertFalse(ElementActions.getElement(locator).isEnabled(), "Expected element located in ["+locator+"] to be visible but it was not");
    }

    @Step("Assert button is enabled")
    public static void assertButtonIsEnabled(By locator) {
        Assert.assertTrue(ElementActions.getElement(locator).isEnabled(), "Expected element located in ["+locator+"] to be visible but it was not");
    }

    /* -------------------- REQUIRED FIELD VALIDATIONS -------------------- */

    @Step("Assert required field error message equals [{expectedMessage}]")
    public static void assertRequiredFieldError(By locator, String expectedMessage) {
        String actualMessage = ElementActions.getText(locator);
        Assert.assertEquals(actualMessage, expectedMessage, "Required field error message mismatch");
    }

    /* -------------------- ATTRIBUTE VALIDATIONS -------------------- */

//    @Step("Assert element attribute [{attribute}] equals [{expectedValue}]")
//    public static void assertAttributeEquals(By locator, String attribute, String expectedValue) {
//        String actualValue = ElementActions.getAttribute(locator, attribute);
//        Assert.assertEquals(
//                actualValue,
//                expectedValue,
//                "Attribute [" + attribute + "] mismatch"
//        );
//    }
}
