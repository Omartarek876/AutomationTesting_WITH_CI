package POM.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.BrowserUtils;
import utils.ElementActions;

public class HomePage {
    private By pageFirstTitle = By.xpath("//h1//span");
    private By pageSecondTitle = By.xpath("//div[@class='col-sm-6']//h2");
    private By signupButton = By.xpath("//i[@class='fa fa-lock']");
    private By loginButton = By.xpath("//i[@class='fa fa-lock']");
    private By deleteAccountButton = By.xpath("//i[@class='fa fa-trash-o']");
    ////i[@class = 'fa fa-user']/parent::a/b
    private By loggedInUser = By.xpath("//a[.//i[contains(@class,'fa-user')] ]/b");
    private By accountCreatedTitle = By.xpath("//h2[@data-qa='account-created']");
    private By accountDeletedTitle = By.xpath("//h2[@data-qa='account-deleted']");
    private By continueButton = By.xpath("//a[@data-qa='continue-button']");

    @Step("Open the application home page")
    public HomePage navigate(String baseUrl) {
        BrowserUtils.navigateToUrl(baseUrl);
        return this;
    }

    @Step("Open the signup form")
    public HomePage clickOnSignupButton() {
        ElementActions.clickElement(signupButton);
        return this;
    }

    @Step("Open the login form")
    public HomePage clickOnLoginButton() {
        ElementActions.clickElement(loginButton);
        return this;
    }

    @Step("Continue from the confirmation page")
    public HomePage clickOnContinueButton() {
        ElementActions.clickElement(continueButton);
        return this;
    }

    @Step("Delete the test account")
    public HomePage clickOnDeleteAccountButton() {
        ElementActions.clickElement(deleteAccountButton);
        return this;
    }

    @Step("Verify the home page headings")
    public HomePage assertOnPageTitles(String title1, String title2) {
        Assert.assertEquals(
                ElementActions.getText(pageFirstTitle), title1,
                "Home page title did not match the expected text");
        Assert.assertEquals(
                ElementActions.getText(pageSecondTitle), title2,
                "Home page subtitle did not match the expected text");
        return this;
    }

    @Step("Verify the account-created confirmation")
    public HomePage assertOnAccountCreatedTitle(String createdTitle) {
        Assert.assertEquals(
                ElementActions.getText(accountCreatedTitle), createdTitle,
                "The account-created confirmation was not displayed as expected");
        return this;
    }

    @Step("Verify the account-deleted confirmation")
    public HomePage assertOnAccountDeletedTitle(String deletedTitle) {
        Assert.assertEquals(
                ElementActions.getText(accountDeletedTitle), deletedTitle,
                "The account-deleted confirmation was not displayed as expected");
        return this;
    }

    @Step("Verify the signed-in username is displayed")
    public HomePage assertOnExpectedLoggedInUser(String expectedLoggedInUser) {
        Assert.assertEquals(
                ElementActions.getText(loggedInUser), expectedLoggedInUser,
                "The signed-in username did not match the expected account");
        return this;
    }

}
