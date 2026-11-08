package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.BrowserUtils;
import utils.ElementActions;

public class HomePage {
    private final By firstHeading = By.xpath("//h1//span");
    private final By secondHeading = By.xpath("//div[@class='col-sm-6']//h2");
    private final By signupLoginButton = By.xpath("//i[@class='fa fa-lock']");
    private final By deleteAccountButton = By.xpath("//i[@class='fa fa-trash-o']");
    private final By loggedInUsername = By.xpath("//a[.//i[contains(@class,'fa-user')] ]/b");

    @Step("Navigate to {baseUrl}")
    public HomePage navigateTo(String baseUrl) {
        BrowserUtils.navigateToUrl(baseUrl);
        return this;
    }

    @Step("Open signup and login page")
    public SignupLoginPage openSignupLoginPage() {
        ElementActions.clickElement(signupLoginButton);
        return new SignupLoginPage();
    }

    @Step("Click delete account")
    public AccountDeletedPage clickDeleteAccountButton() {
        ElementActions.clickElement(deleteAccountButton);
        return new AccountDeletedPage();
    }

    @Step("Verify home page headings")
    public HomePage assertHomePageHeadings(String expectedFirstHeading, String expectedSecondHeading) {
        Assert.assertEquals(ElementActions.getText(firstHeading), expectedFirstHeading);
        Assert.assertEquals(ElementActions.getText(secondHeading), expectedSecondHeading);
        return this;
    }

    @Step("Verify logged-in username is {expectedUsername}")
    public HomePage assertLoggedInAs(String expectedUsername) {
        Assert.assertEquals(ElementActions.getText(loggedInUsername), expectedUsername);
        return this;
    }
}
