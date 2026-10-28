package com.automationexercise.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import com.automationexercise.framework.browser.DriverFactory;
import com.automationexercise.framework.elements.ElementActions;

public class HomePage {
    private static final By PAGE_FIRST_TITLE = By.xpath("//h1//span");
    private static final By PAGE_SECOND_TITLE = By.xpath("//div[@class='col-sm-6']//h2");
    private static final By SIGNUP_BUTTON = By.xpath("//i[@class='fa fa-lock']");
    private static final By LOGIN_BUTTON = By.xpath("//i[@class='fa fa-lock']");
    private static final By DELETE_ACCOUNT_BUTTON = By.xpath("//i[@class='fa fa-trash-o']");
    private static final By LOGGED_IN_USER = By.xpath("//a[.//i[contains(@class,'fa-user')] ]/b");
    private static final By ACCOUNT_CREATED_TITLE = By.xpath("//h2[@data-qa='account-created']");
    private static final By ACCOUNT_DELETED_TITLE = By.xpath("//h2[@data-qa='account-deleted']");
    private static final By CONTINUE_BUTTON = By.xpath("//a[@data-qa='continue-button']");

    @Step("Open the application home page")
    public HomePage navigate(String baseUrl) {
        DriverFactory.getDriver().navigate().to(baseUrl);
        return this;
    }

    @Step("Open the signup form")
    public HomePage clickOnSignupButton() {
        ElementActions.clickElement(SIGNUP_BUTTON);
        return this;
    }

    @Step("Open the login form")
    public HomePage clickOnLoginButton() {
        ElementActions.clickElement(LOGIN_BUTTON);
        return this;
    }

    @Step("Continue from the confirmation page")
    public HomePage clickOnContinueButton() {
        ElementActions.clickElement(CONTINUE_BUTTON);
        return this;
    }

    @Step("Delete the test account")
    public HomePage clickOnDeleteAccountButton() {
        ElementActions.clickElement(DELETE_ACCOUNT_BUTTON);
        return this;
    }

    @Step("Verify the home page headings")
    public HomePage assertOnPageTitles(String title1, String title2) {
        Assert.assertEquals(
                ElementActions.getText(PAGE_FIRST_TITLE), title1,
                "Home page title did not match the expected text");
        Assert.assertEquals(
                ElementActions.getText(PAGE_SECOND_TITLE), title2,
                "Home page subtitle did not match the expected text");
        return this;
    }

    @Step("Verify the account-created confirmation")
    public HomePage assertOnAccountCreatedTitle(String createdTitle) {
        Assert.assertEquals(
                ElementActions.getText(ACCOUNT_CREATED_TITLE), createdTitle,
                "The account-created confirmation was not displayed as expected");
        return this;
    }

    @Step("Verify the account-deleted confirmation")
    public HomePage assertOnAccountDeletedTitle(String deletedTitle) {
        Assert.assertEquals(
                ElementActions.getText(ACCOUNT_DELETED_TITLE), deletedTitle,
                "The account-deleted confirmation was not displayed as expected");
        return this;
    }

    @Step("Verify the signed-in username is displayed")
    public HomePage assertOnExpectedLoggedInUser(String expectedLoggedInUser) {
        Assert.assertEquals(
                ElementActions.getText(LOGGED_IN_USER), expectedLoggedInUser,
                "The signed-in username did not match the expected account");
        return this;
    }

}
