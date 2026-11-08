package tests.auth;

import io.qameta.allure.Feature;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.AccountCreatedPage;
import pages.AccountDeletedPage;
import pages.HomePage;
import pages.RegisterUserPage;
import pages.SignupLoginPage;
import support.TestDataFactory;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

@Listeners(TestNgListener.class)
@Feature("Registration")
public class RegistrationTests {
    private HomePage homePage;
    private SignupLoginPage signupLoginPage;
    private RegisterUserPage registerUserPage;
    private AccountCreatedPage accountCreatedPage;
    private JsonFileManager registrationData;
    private JsonFileManager registrationMessages;
    private JsonFileManager storefrontMessages;
    private boolean accountCreated;
    private String registeredUserName;

    @Test(description = "Register a new user and verify the account can be deleted")
    @Description("Create a new account, verify the confirmation and logged-in user, then remove the account.")
    @Severity(SeverityLevel.CRITICAL)
    public void registerNewUserSuccessfully() {
        String timestamp = TestDataFactory.timestampSuffix();
        registeredUserName = TestDataFactory.timestampedValue(
                registrationData.getTestData("$.userData.userName"),
                timestamp
        );
        String registeredEmail = TestDataFactory.timestampedEmail(
                registrationData.getTestData("$.userData.email"),
                timestamp
        );

        registerUserPage = signupLoginPage.startRegistration(
                registeredUserName,
                registeredEmail
        );
        registerUserPage.assertAccountInformationHeading(
                registrationMessages.getTestData("$.accountInformationTitle")
        );

        registerUserPage.fillAccountInformation(
                registrationData.getTestData("$.userData.title"),
                registrationData.getTestData("$.userData.password"),
                registrationData.getTestData("$.userData.day"),
                registrationData.getTestData("$.userData.month"),
                registrationData.getTestData("$.userData.year"),
                Boolean.parseBoolean(registrationData.getTestData("$.userData.newsletter")),
                Boolean.parseBoolean(registrationData.getTestData("$.userData.specialOffers"))
        );

        accountCreatedPage = registerUserPage.fillAddressInformation(
                registrationData.getTestData("$.userData.firstName"),
                registrationData.getTestData("$.userData.lastName"),
                registrationData.getTestData("$.userData.company"),
                registrationData.getTestData("$.userData.address1"),
                registrationData.getTestData("$.userData.address2"),
                registrationData.getTestData("$.userData.country"),
                registrationData.getTestData("$.userData.state"),
                registrationData.getTestData("$.userData.city"),
                registrationData.getTestData("$.userData.zipcode"),
                registrationData.getTestData("$.userData.mobileNumber")
        );
        accountCreated = true;

        accountCreatedPage.assertAccountCreated(registrationMessages.getTestData("$.accountCreated"));
        homePage = accountCreatedPage.continueToHomePage();
        homePage.assertLoggedInAs(registeredUserName);
    }

    @Test(description = "Attempt to register with an email that is already registered")
    @Description("Submit a previously registered email and verify the duplicate-email message.")
    @Severity(SeverityLevel.NORMAL)
    public void registerWithExistingEmail_displaysDuplicateEmailError() {
        signupLoginPage.startRegistration(
                registrationData.getTestData("$.existingUserData.userName"),
                registrationData.getTestData("$.existingUserData.email")
        );
        signupLoginPage.assertExistingEmailErrorMessage(registrationMessages.getTestData("$.existingEmail"));
    }

    @Test(description = "Attempt to register with required signup fields missing")
    @Description("Submit the signup form empty and verify browser validation blocks submission.")
    @Severity(SeverityLevel.NORMAL)
    public void signupWithMissingRequiredFields_showsValidation() {
        signupLoginPage.submitSignupWithMissingRequiredFields();
    }

    @BeforeClass
    public void loadTestData() {
        registrationData = new JsonFileManager(
                "src/test/resources/testdata/auth/registration/registrationData.json"
        );
        registrationMessages = new JsonFileManager(
                "src/test/resources/testdata/auth/registration/registrationMessages.json"
        );
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
    }

    @BeforeMethod
    public void openSignupPage() {
        accountCreated = false;
        DriverFactory.InitializeDriver();
        homePage = new HomePage();
        homePage.navigateTo(System.getProperty("baseUrl"));
        homePage.assertHomePageHeadings(
                storefrontMessages.getTestData("$.homeTitle"),
                storefrontMessages.getTestData("$.homeSubtitle")
        );
        signupLoginPage = homePage.openSignupLoginPage();
        signupLoginPage.assertNewUserSignupHeading(registrationMessages.getTestData("$.signupTitle"));
    }

    @AfterMethod
    public void deleteCreatedAccountAndCloseBrowser() {
        try {
            if (accountCreated) {
                AccountDeletedPage deletedPage = homePage.clickDeleteAccountButton();
                deletedPage.assertAccountDeleted(registrationMessages.getTestData("$.accountDeleted"));
                deletedPage.continueToHomePage();
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
