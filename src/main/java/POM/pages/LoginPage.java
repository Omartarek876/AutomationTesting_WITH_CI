package POM.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utils.ElementActions;

public class LoginPage {
    private By loginFormTitle = By.xpath("//div[@class='login-form']//h2");
    private By emailField = By.xpath("//form//input[@data-qa='login-email']");
    private By passwordField = By.xpath("//form//input[@data-qa='login-password']");
    private By loginButtom = By.xpath("//form//button[@data-qa='login-button']");
    private By invalidLoginErrorMessage = By.xpath("//p[@style='color: red;']");

    @Step("Submit login form")
    public LoginPage userLogin(String email, String password) {
        ElementActions.typeText(emailField, email);
        ElementActions.typeText(passwordField, password);
        ElementActions.clickElement(loginButtom);
        return this;
    }

    @Step("Verify the login form heading")
    public LoginPage assertOnLoginFormTitle(String expectedLoginFormTitle) {
        Assert.assertEquals(
                ElementActions.getText(loginFormTitle), expectedLoginFormTitle,
                "Login form heading did not match the expected message");
        return this;
    }

    @Step("Verify the invalid-credentials message")
    public LoginPage assertOnInvalidLoginErrorMessage(String invalidLoginMessage) {
        Assert.assertEquals(
                ElementActions.getText(invalidLoginErrorMessage), invalidLoginMessage,
                "The login form did not show the expected invalid-credentials message");
        return this;
    }
}
