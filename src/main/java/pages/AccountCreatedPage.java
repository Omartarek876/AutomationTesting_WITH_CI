package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ElementActions;

public class AccountCreatedPage {
    private final By confirmationHeading = By.xpath("//h2[@data-qa='account-created']");
    private final By continueButton = By.xpath("//a[@data-qa='continue-button']");

    @Step("Verify account created confirmation")
    public AccountCreatedPage assertAccountCreated(String expectedMessage) {
        Assert.assertEquals(ElementActions.getText(confirmationHeading), expectedMessage);
        return this;
    }

    @Step("Continue to home page after account creation")
    public HomePage continueToHomePage() {
        ElementActions.clickElement(continueButton);
        return new HomePage();
    }
}
