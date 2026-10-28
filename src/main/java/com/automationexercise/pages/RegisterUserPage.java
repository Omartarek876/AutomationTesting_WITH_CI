package com.automationexercise.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import com.automationexercise.framework.elements.ElementActions;

public class RegisterUserPage {

    private static final By SIGNUP_FORM_TITLE = By.xpath("//div[@class='signup-form']//h2");
    private static final By ACCOUNT_INFORMATION_TITLE = By.xpath("//div[@class='login-form']/h2[@class='title text-center']/b");
    private static final By USERNAME_FIELD = By.xpath("//input[@data-qa='signup-name']");
    private static final By EMAIL_FIELD = By.xpath("//input[@data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.xpath("//button[@data-qa='signup-button']");
    private By mrTitleRadio = By.id("id_gender1");
    private By mrsTitleRadio = By.id("id_gender2");
    private By passwordInput = By.id("password");
    private By daysSelect = By.id("days");
    private By monthsSelect = By.id("months");
    private By yearsSelect = By.id("years");
    private By newsletterCheckbox = By.id("newsletter");
    private By specialOffersCheckbox = By.id("optin");
    private By firstNameInput = By.id("first_name");
    private By lastNameInput = By.id("last_name");
    private By companyInput = By.id("company");
    private By address1Input = By.id("address1");
    private By address2Input = By.id("address2");
    private By countrySelect = By.id("country");
    private By stateInput = By.id("state");
    private By cityInput = By.id("city");
    private By zipcodeInput = By.id("zipcode");
    private By mobileNumberInput = By.id("mobile_number");
    private By createAccountBtn = By.xpath("//button[@data-qa='create-account']");
    private By existingAccountErrorMessage = By.xpath("//p[@style='color: red;']");


    @Step("Submit the initial signup details")
    public RegisterUserPage signupUser(String userName, String email) {
        ElementActions.typeText(USERNAME_FIELD, userName);
        ElementActions.typeText(EMAIL_FIELD, email);
        ElementActions.clickElement(SIGNUP_BUTTON);
        return this;
    }

    @Step("Complete the account information")
    public RegisterUserPage enterAccountInformation(String title, String password, String day,
                                        String month, String year, boolean newsletter, boolean specialOffers) {
        selectUserTitle(title);
        ElementActions.typeText(passwordInput, password);
        selectDateOfBirth(day, month, year);
        checkNewsletter(newsletter);
        checkSpecialOffers(specialOffers);
        return this;
    }

    @Step("Complete the address details and create the account")
    public RegisterUserPage enterAddressInformation(String firstName, String lastName, String company, String address1, String address2,
                                        String country, String state, String city, String zipcode, String mobileNumber) {
        ElementActions.typeText(firstNameInput, firstName);
        ElementActions.typeText(lastNameInput, lastName);
        ElementActions.typeText(companyInput, company);
        ElementActions.typeText(address1Input, address1);
        ElementActions.typeText(address2Input, address2);
        ElementActions.selectDropdownByVisibleText(countrySelect, country);
        ElementActions.typeText(stateInput, state);
        ElementActions.typeText(cityInput, city);
        ElementActions.typeText(zipcodeInput, zipcode);
        ElementActions.typeText(mobileNumberInput, mobileNumber);
        ElementActions.clickElement(createAccountBtn);
        return this;
    }

    private RegisterUserPage selectUserTitle(String title) {
        if (title.toLowerCase().trim().equals("mr")) {
            ElementActions.selectRadioButton(mrTitleRadio);
        } else if (title.toLowerCase().trim().equals("mrs")) {
            ElementActions.selectRadioButton(mrsTitleRadio);
        } else {
            throw new IllegalArgumentException("Unsupported account title: " + title);
        }
        return this;
    }

    private RegisterUserPage checkNewsletter(Boolean newsletter) {
        if (newsletter) {
            ElementActions.checkCheckbox(newsletterCheckbox);
        } else {
            ElementActions.uncheckCheckbox(newsletterCheckbox);
        }
        return this;
    }

    private RegisterUserPage checkSpecialOffers(Boolean specialOffers) {
        if (specialOffers) {
            ElementActions.checkCheckbox(specialOffersCheckbox);
        } else {
            ElementActions.uncheckCheckbox(specialOffersCheckbox);
        }
        return this;
    }

    private RegisterUserPage selectDateOfBirth(String day, String month, String year) {
        ElementActions.selectDropdownByVisibleText(daysSelect, day);
        ElementActions.selectDropdownByVisibleText(monthsSelect, month);
        ElementActions.selectDropdownByVisibleText(yearsSelect, year);
        return this;
    }

    @Step("Verify the signup form heading")
    public RegisterUserPage assertOnSignupFormTitle1(String signupTitle1) {
        Assert.assertEquals(
                ElementActions.getText(SIGNUP_FORM_TITLE), signupTitle1,
                "Signup form heading did not match the expected text");
        return this;
    }

    @Step("Verify the account-information heading")
    public RegisterUserPage assertOnSignupFormTitle2(String signupTitle2) {
        Assert.assertEquals(
                ElementActions.getText(ACCOUNT_INFORMATION_TITLE), signupTitle2,
                "Account-information heading did not match the expected text");
        return this;
    }

    @Step("Verify that the email address is already registered")
    public RegisterUserPage assertOnExistingAccountMessage(String invalidLoginMessage) {
        Assert.assertEquals(
                ElementActions.getText(existingAccountErrorMessage), invalidLoginMessage,
                "The signup form did not show the expected existing-email message");
        return this;
    }
}
