package com.automationexercise.tests;

import com.automationexercise.data.JsonFileManager;
import com.automationexercise.framework.browser.DriverFactory;
import com.automationexercise.pages.HomePage;
import com.automationexercise.pages.RegisterUserPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Map;

@Feature("Registration")
@Story("Create and validate a test account")
public class RegisterTests {
    private final JsonFileManager registrationData = new JsonFileManager("RegistrationData.json");
    private final JsonFileManager messages = new JsonFileManager("uiMessages.json");

    @BeforeMethod
    public void openSignupForm() {
        DriverFactory.initializeDriver();
        new HomePage()
                .navigate(System.getProperty("baseUrl"))
                .assertOnPageTitles(messages.getTestData("$.home.title"), messages.getTestData("$.home.subtitle"))
                .clickOnSignupButton();
        new RegisterUserPage().assertOnSignupFormTitle1(messages.getTestData("$.registration.signupTitle"));
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Register a new user and verify the account lifecycle")
    @Description("Creates a unique account, verifies the signed-in state, and removes the test account.")
    @Severity(SeverityLevel.CRITICAL)
    public void registerUserAndVerifyAccountLifecycle() {
        Map<String, Object> user = registrationData.getObject("$.userData");
        String suffix = Long.toString(Instant.now().toEpochMilli());
        String userName = (String) user.get("userName") + suffix;
        String email = ((String) user.get("email")).replace("@", "+" + suffix + "@");

        new RegisterUserPage()
                .signupUser(userName, email)
                .assertOnSignupFormTitle2(messages.getTestData("$.registration.accountInformationTitle"))
                .enterAccountInformation(
                        (String) user.get("title"),
                        (String) user.get("password"),
                        (String) user.get("day"),
                        (String) user.get("month"),
                        (String) user.get("year"),
                        Boolean.parseBoolean(String.valueOf(user.get("newsletter"))),
                        Boolean.parseBoolean(String.valueOf(user.get("specialOffers"))));

        new RegisterUserPage().enterAddressInformation(
                (String) user.get("firstName"),
                (String) user.get("lastName"),
                (String) user.get("company"),
                (String) user.get("address1"),
                (String) user.get("address2"),
                (String) user.get("country"),
                (String) user.get("state"),
                (String) user.get("city"),
                (String) user.get("zipcode"),
                (String) user.get("mobileNumber"));

        new HomePage()
                .assertOnAccountCreatedTitle(messages.getTestData("$.home.accountCreated"))
                .clickOnContinueButton()
                .assertOnExpectedLoggedInUser(userName)
                .clickOnDeleteAccountButton()
                .assertOnAccountDeletedTitle(messages.getTestData("$.home.accountDeleted"))
                .clickOnContinueButton();
    }

    @Test(description = "Reject an email address that already has an account")
    @Description("The signup form explains that an existing email address cannot be registered again.")
    @Severity(SeverityLevel.NORMAL)
    public void rejectRegistrationWithExistingEmail() {
        Map<String, Object> user = registrationData.getObject("$.existingUserData");
        new RegisterUserPage()
                .signupUser((String) user.get("userName"), (String) user.get("email"))
                .assertOnExistingAccountMessage(messages.getTestData("$.registration.existingEmail"));
    }
}
