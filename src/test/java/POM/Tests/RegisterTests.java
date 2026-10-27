package POM.Tests;

import io.qameta.allure.*;
import org.testng.annotations.*;
import utils.*;
import POM.pages.*;

import java.time.Instant;
import java.util.Map;

@Listeners(utils.TestNgListener.class)
@Feature("Register Feature")
@Story("Register and manage a test account")
public class RegisterTests {
    HomePage homePage;
    RegisterUserPage registerUserPage;
    JsonFileManager registrationData;
    JsonFileManager uiMessages;
    Map<String, Object> newUserData;
    Map<String, Object> existingUserData;

    @Test(description = "Register a new user and verify the account lifecycle")
    @Description("Creates a unique account, verifies the signed-in state, and removes the test account.")
    @Severity(SeverityLevel.CRITICAL)
    public void registerUserAndVerifyAccountLifecycle() {
        String uniqueSuffix = Long.toString(Instant.now().toEpochMilli());
        String userName = value("userName") + uniqueSuffix;
        String email = value("email").replace("@", "+" + uniqueSuffix + "@");
        registerUserPage.signupUser(userName, email)
                         .assertOnSignupFormTitle2(uiMessages.getTestData("$.registration.accountInformationTitle"))
                         .enterAccountInformation(
                value("title"),
                value("password"),
                value("day"),
                value("month"),
                value("year"),
                parseCheckboxValue(value("newsletter")),
                parseCheckboxValue(value("specialOffers"))
        );

        registerUserPage.enterAddressInformation(
                value("firstName"),
                value("lastName"),
                value("company"),
                value("address1"),
                value("address2"),
                value("country"),
                value("state"),
                value("city"),
                value("zipcode"),
                value("mobileNumber")
        );
        homePage
                .assertOnAccountCreatedTitle(uiMessages.getTestData("$.home.accountCreated"))
                .clickOnContinueButton()
                .assertOnExpectedLoggedInUser(userName)
                .clickOnDeleteAccountButton()
                .assertOnAccountDeletedTitle(uiMessages.getTestData("$.home.accountDeleted"))
                .clickOnContinueButton();
    }

    private boolean parseCheckboxValue(String value) {
        return switch (value.trim().toLowerCase()) {
            case "true", "check", "checked", "yes" -> true;
            case "false", "uncheck", "unchecked", "no" -> false;
            default -> throw new IllegalArgumentException("Unsupported checkbox value: " + value);
        };
    }

    @Test(description = "Reject an email address that already has an account")
    @Description("The signup form should explain that an existing email address cannot be registered again.")
    @Severity(SeverityLevel.NORMAL)
    public void rejectRegistrationWithExistingEmail() {
        registerUserPage.signupUser(
                        requiredValue(existingUserData, "userName"),
                        requiredValue(existingUserData, "email"))
                .assertOnExistingAccountMessage(uiMessages.getTestData("$.registration.existingEmail"));
    }

    @BeforeClass
    public void loadTestData() {
        registrationData = new JsonFileManager("RegistrationData.json");
        uiMessages = new JsonFileManager("uiMessages.json");
        newUserData = registrationData.getObject("$.userData");
        existingUserData = registrationData.getObject("$.existingUserData");
    }

    @BeforeMethod
    public void beforeMethod() {
        DriverFactory.InitializeDriver();
        homePage = new HomePage();
        registerUserPage = new RegisterUserPage();
        homePage.navigate(System.getProperty("baseUrl"));
        homePage.assertOnPageTitles(
                uiMessages.getTestData("$.home.title"),
                uiMessages.getTestData("$.home.subtitle"));
        homePage.clickOnSignupButton();
        registerUserPage.assertOnSignupFormTitle1(uiMessages.getTestData("$.registration.signupTitle"));
    }

    @AfterMethod
    public void afterMethod() {
        DriverFactory.quitDriver();
    }

    private String value(String key) {
        return requiredValue(newUserData, key);
    }

    private String requiredValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException("Missing required registration data: " + key);
        }
        return String.valueOf(value);
    }
}
