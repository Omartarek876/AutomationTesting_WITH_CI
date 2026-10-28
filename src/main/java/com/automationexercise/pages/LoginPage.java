package com.automationexercise.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import com.automationexercise.framework.elements.ElementActions;

public class LoginPage {
    private static final By LOGIN_FORM_TITLE = By.xpath("//div[@class='login-form']//h2");
    private static final By EMAIL_FIELD = By.xpath("//form//input[@data-qa='login-email']");
    private static final By PASSWORD_FIELD = By.xpath("//form//input[@data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.xpath("//form//button[@data-qa='login-button']");
    private static final By INVALID_LOGIN_ERROR = By.xpath("//p[@style='color: red;']");

    @Step("Submit login form")
    public LoginPage userLogin(String email, String password) {
        ElementActions.typeText(EMAIL_FIELD, email);
        ElementActions.typeText(PASSWORD_FIELD, password);
        ElementActions.clickElement(LOGIN_BUTTON);
        return this;
    }

    @Step("Verify the login form heading")
    public LoginPage assertOnLoginFormTitle(String expectedLoginFormTitle) {
        Assert.assertEquals(
                ElementActions.getText(LOGIN_FORM_TITLE), expectedLoginFormTitle,
                "Login form heading did not match the expected message");
        return this;
    }

    @Step("Verify the invalid-credentials message")
    public LoginPage assertOnInvalidLoginErrorMessage(String invalidLoginMessage) {
        Assert.assertEquals(
                ElementActions.getText(INVALID_LOGIN_ERROR), invalidLoginMessage,
                "The login form did not show the expected invalid-credentials message");
        return this;
    }
}
