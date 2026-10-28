package com.automationexercise.tests;

import com.automationexercise.data.JsonFileManager;
import com.automationexercise.framework.browser.DriverFactory;
import com.automationexercise.pages.HomePage;
import com.automationexercise.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

@Feature("Login")
@Story("Sign in with valid and invalid credentials")
public class LoginTests {
    private final JsonFileManager loginData = new JsonFileManager("loginTestData.json");
    private final JsonFileManager messages = new JsonFileManager("uiMessages.json");

    @BeforeMethod
    public void openLoginForm() {
        DriverFactory.initializeDriver();
        new HomePage()
                .navigate(System.getProperty("baseUrl"))
                .assertOnPageTitles(messages.getTestData("$.home.title"), messages.getTestData("$.home.subtitle"))
                .clickOnLoginButton();
        new LoginPage().assertOnLoginFormTitle(messages.getTestData("$.login.formTitle"));
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Sign in with valid credentials")
    @Description("A registered user can sign in and see their username on the home page.")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithValidCredentials() {
        Map<String, Object> scenario = loginData.getObject("$.loginScenarios.validCredentials");
        new LoginPage().userLogin((String) scenario.get("email"), (String) scenario.get("password"));
        new HomePage().assertOnExpectedLoggedInUser((String) scenario.get("expectedUserName"));
    }

    @Test(description = "Show an error for invalid credentials")
    @Description("The login form explains when an email address or password is incorrect.")
    @Severity(SeverityLevel.NORMAL)
    public void loginWithInvalidCredentials() {
        Map<String, Object> scenario = loginData.getObject("$.loginScenarios.invalidCredentials");
        new LoginPage()
                .userLogin((String) scenario.get("email"), (String) scenario.get("password"))
                .assertOnInvalidLoginErrorMessage(messages.getTestData("$.login.invalidCredentials"));
    }
}
