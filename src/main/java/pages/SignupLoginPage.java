package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.DriverFactory;
import utils.ElementActions;

/** Page object for signup controls on the combined signup and login screen. */
public class SignupLoginPage extends LoginPage {
    private final By newUserSignupHeading = By.xpath("//div[@class='signup-form']//h2");
    private final By signupNameField = By.xpath("//input[@data-qa='signup-name']");
    private final By signupEmailField = By.xpath("//input[@data-qa='signup-email']");
    private final By signupButton = By.xpath("//button[@data-qa='signup-button']");
    private final By signupForm = By.xpath("//input[@data-qa='signup-name']/ancestor::form");
    private final By existingEmailErrorMessage = By.xpath("//p[@style='color: red;']");

    @Step("Verify new user signup heading")
    public SignupLoginPage assertNewUserSignupHeading(String expectedHeading) {
        Assert.assertEquals(ElementActions.getText(newUserSignupHeading), expectedHeading);
        return this;
    }

    @Step("Start signup for {userName}")
    public RegisterUserPage startRegistration(String userName, String email) {
        ElementActions.typeText(signupNameField, userName);
        ElementActions.typeText(signupEmailField, email);
        ElementActions.clickElement(signupButton);
        return new RegisterUserPage();
    }

    @Step("Submit signup form with required fields missing")
    public SignupLoginPage submitSignupWithMissingRequiredFields() {
        ElementActions.clickElement(signupButton);
        WebElement form = ElementActions.getElement(signupForm);
        boolean formIsValid = (Boolean) ((JavascriptExecutor) DriverFactory.getDriver())
                .executeScript("return arguments[0].checkValidity();", form);
        Assert.assertFalse(formIsValid, "Signup form should reject submission when required fields are empty.");
        Assert.assertTrue(
                ElementActions.getElement(newUserSignupHeading).isDisplayed(),
                "The signup form should remain visible after browser validation blocks submission."
        );
        return this;
    }

    @Step("Verify existing email error message")
    public SignupLoginPage assertExistingEmailErrorMessage(String expectedMessage) {
        Assert.assertEquals(ElementActions.getText(existingEmailErrorMessage), expectedMessage);
        return this;
    }
}
