package tests.auth;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.*;
import utils.DriverFactory;
import utils.JsonFileManager;
import pages.HomePage;
import pages.SignupLoginPage;
import pages.StorefrontPage;


@Listeners(utils.TestNgListener.class)
@Feature("Login Feature")
public class LoginTests {
    private JsonFileManager loginData;
    private JsonFileManager loginMessages;
    private JsonFileManager storefrontMessages;

    @Test(description = "Login with valid email and password")
    @Description("Sign in with valid credentials and verify the logged-in username.")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithValidCredentials_displaysLoggedInUser() {
        new SignupLoginPage()
                .loginWithCredentials(
                        loginData.getTestData("$.loginScenarios.validCredentials.email"),
                        loginData.getTestData("$.loginScenarios.validCredentials.password")
                );
        new HomePage().assertLoggedInAs(
                loginData.getTestData("$.loginScenarios.validCredentials.expectedUserName")
        );
    }

    @Test(description = "Login with incorrect email and password")
    @Description("Submit invalid credentials and verify the login error message.")
    public void loginWithInvalidCredentials_displaysError() {
        new SignupLoginPage()
                .loginWithCredentials(
                        loginData.getTestData("$.loginScenarios.invalidCredentials.email"),
                        loginData.getTestData("$.loginScenarios.invalidCredentials.password")
                )
                .assertInvalidLoginMessage(loginMessages.getTestData("$.invalidCredentials"));
    }

    @Test(description = "Log out after a successful login")
    @Description("Sign in with valid credentials, log out, and verify the login form is displayed again.")
    @Severity(SeverityLevel.CRITICAL)
    public void logoutAfterLogin_returnsToLoginPage() {
        new SignupLoginPage()
                .loginWithCredentials(
                        loginData.getTestData("$.loginScenarios.validCredentials.email"),
                        loginData.getTestData("$.loginScenarios.validCredentials.password")
                );
        new HomePage().assertLoggedInAs(
                loginData.getTestData("$.loginScenarios.validCredentials.expectedUserName")
        );

        new StorefrontPage()
                .logOut()
                .assertLoginFormHeading(loginMessages.getTestData("$.loginLogoutHeading"));
    }

    @BeforeClass
    public void setup() {
        loginData = new JsonFileManager("src/test/resources/testdata/auth/login/loginData.json");
        loginMessages = new JsonFileManager("src/test/resources/testdata/auth/login/loginMessages.json");
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
    }


    @BeforeMethod
    public void beforeMethod() {
        DriverFactory.InitializeDriver();
        new HomePage()
                .navigateTo(System.getProperty("baseUrl"))
                .assertHomePageHeadings(
                        storefrontMessages.getTestData("$.homeTitle"),
                        storefrontMessages.getTestData("$.homeSubtitle")
                )
                .openSignupLoginPage()
                .assertLoginFormHeading(loginMessages.getTestData("$.formTitle"));
    }

    @AfterMethod
    public void afterMethod() {
        DriverFactory.quitDriver();
    }
}
