package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ElementActions;

public class RegisterUserPage {
    private final By accountInformationHeading = By.xpath("//div[@class='login-form']/h2[@class='title text-center']/b");
    private final By mrRadioButton = By.id("id_gender1");
    private final By mrsRadioButton = By.id("id_gender2");
    private final By passwordField = By.id("password");
    private final By daysDropdown = By.id("days");
    private final By monthsDropdown = By.id("months");
    private final By yearsDropdown = By.id("years");
    private final By newsletterCheckbox = By.id("newsletter");
    private final By specialOffersCheckbox = By.id("optin");
    private final By firstNameField = By.id("first_name");
    private final By lastNameField = By.id("last_name");
    private final By companyField = By.id("company");
    private final By addressLine1Field = By.id("address1");
    private final By addressLine2Field = By.id("address2");
    private final By countryDropdown = By.id("country");
    private final By stateField = By.id("state");
    private final By cityField = By.id("city");
    private final By zipCodeField = By.id("zipcode");
    private final By mobileNumberField = By.id("mobile_number");
    private final By createAccountButton = By.xpath("//button[@data-qa='create-account']");

    @Step("Verify account information heading")
    public RegisterUserPage assertAccountInformationHeading(String expectedHeading) {
        Assert.assertEquals(ElementActions.getText(accountInformationHeading), expectedHeading);
        return this;
    }

    @Step("Fill account information")
    public RegisterUserPage fillAccountInformation(String title, String password, String day,
                                                   String month, String year,
                                                   boolean subscribeToNewsletter,
                                                   boolean receiveSpecialOffers) {
        selectTitle(title);
        ElementActions.typeText(passwordField, password);
        selectDateOfBirth(day, month, year);
        setCheckbox(newsletterCheckbox, subscribeToNewsletter);
        setCheckbox(specialOffersCheckbox, receiveSpecialOffers);
        return this;
    }

    @Step("Fill address information")
    public AccountCreatedPage fillAddressInformation(String firstName, String lastName, String company,
                                                     String addressLine1, String addressLine2,
                                                     String country, String state, String city,
                                                     String zipCode, String mobileNumber) {
        ElementActions.typeText(firstNameField, firstName);
        ElementActions.typeText(lastNameField, lastName);
        ElementActions.typeText(companyField, company);
        ElementActions.typeText(addressLine1Field, addressLine1);
        ElementActions.typeText(addressLine2Field, addressLine2);
        ElementActions.selectDropdownByVisibleText(countryDropdown, country);
        ElementActions.typeText(stateField, state);
        ElementActions.typeText(cityField, city);
        ElementActions.typeText(zipCodeField, zipCode);
        ElementActions.typeText(mobileNumberField, mobileNumber);
        ElementActions.clickElement(createAccountButton);
        return new AccountCreatedPage();
    }

    private void selectTitle(String title) {
        String normalizedTitle = title.trim().toLowerCase();
        if (normalizedTitle.equals("mr")) {
            ElementActions.selectRadioButton(mrRadioButton);
            return;
        }

        if (normalizedTitle.equals("mrs")) {
            ElementActions.selectRadioButton(mrsRadioButton);
            return;
        }

        throw new IllegalArgumentException("Unsupported title: " + title);
    }

    private void setCheckbox(By checkbox, boolean shouldBeSelected) {
        if (shouldBeSelected) {
            ElementActions.checkCheckbox(checkbox);
        } else {
            ElementActions.uncheckCheckbox(checkbox);
        }
    }

    private void selectDateOfBirth(String day, String month, String year) {
        ElementActions.selectDropdownByVisibleText(daysDropdown, day);
        ElementActions.selectDropdownByVisibleText(monthsDropdown, month);
        ElementActions.selectDropdownByVisibleText(yearsDropdown, year);
    }
}
