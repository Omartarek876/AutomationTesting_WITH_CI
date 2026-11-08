package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ElementActions;

/** Page object for login controls on the combined signup and login screen. */
public class LoginPage {
    private final By loginFormHeading = By.xpath("//div[@class='login-form']//h2");
    private final By emailField = By.xpath("//form//input[@data-qa='login-email']");
    private final By passwordField = By.xpath("//form//input[@data-qa='login-password']");
    private final By loginButton = By.xpath("//form//button[@data-qa='login-button']");
    private final By invalidLoginMessage = By.xpath("//p[@style='color: red;']");

    @Step("Log in with email {email}")
    public LoginPage loginWithCredentials(String email, String password) {
        ElementActions.typeText(emailField, email);
        ElementActions.typeText(passwordField, password);
        ElementActions.clickElement(loginButton);
        return this;
    }

    @Step("Verify login form heading")
    public LoginPage assertLoginFormHeading(String expectedHeading) {
        Assert.assertEquals(ElementActions.getText(loginFormHeading), expectedHeading);
        return this;
    }

    @Step("Verify invalid login message")
    public LoginPage assertInvalidLoginMessage(String expectedMessage) {
        Assert.assertEquals(ElementActions.getText(invalidLoginMessage), expectedMessage);
        return this;
    }
}
