package POM.Tests;

import POM.pages.HomePage;
import POM.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.DriverFactory;
import utils.JsonFileManager;

import java.util.Map;

@Listeners(utils.TestNgListener.class)
@Feature("Login")
@Story("Sign in with valid and invalid credentials")
public class LoginTests {
    private JsonFileManager loginData;
    private JsonFileManager uiMessages;
    private Map<String, Object> loginScenarios;

    @BeforeClass
    public void loadTestData() {
        loginData = new JsonFileManager("loginTestData.json");
        uiMessages = new JsonFileManager("uiMessages.json");
        loginScenarios = loginData.getObject("$.loginScenarios");
    }

    @DataProvider(name = "loginScenarios")
    public Object[][] loginScenarios() {
        return loginScenarios.keySet().stream()
                .map(scenarioName -> new Object[]{scenarioName})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "loginScenarios", description = "Verify each JSON-defined login outcome")
    @Description("Submits every login scenario from loginTestData.json and checks the expected page result.")
    @Severity(SeverityLevel.CRITICAL)
    public void loginMatchesExpectedOutcome(String scenarioName) {
        Map<String, Object> scenario = scenarioFor(scenarioName);

        new LoginPage().userLogin(
                requiredValue(scenario, "email", scenarioName),
                requiredValue(scenario, "password", scenarioName));

        switch (requiredValue(scenario, "expectedOutcome", scenarioName)) {
            case "success" -> new HomePage().assertOnExpectedLoggedInUser(
                    requiredValue(scenario, "expectedUserName", scenarioName));
            case "error" -> new LoginPage().assertOnInvalidLoginErrorMessage(
                    uiMessages.getTestData("$.login.invalidCredentials"));
            default -> throw new IllegalArgumentException(
                    "Unsupported expected login outcome in scenario: " + scenarioName);
        }
    }

    @BeforeMethod
    public void openLoginForm() {
        DriverFactory.InitializeDriver();
        new HomePage()
                .navigate(System.getProperty("baseUrl"))
                .assertOnPageTitles(
                        uiMessages.getTestData("$.home.title"),
                        uiMessages.getTestData("$.home.subtitle"))
                .clickOnLoginButton();
        new LoginPage().assertOnLoginFormTitle(uiMessages.getTestData("$.login.formTitle"));
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> scenarioFor(String scenarioName) {
        Object value = loginScenarios.get(scenarioName);
        if (value instanceof Map<?, ?>) {
            return (Map<String, Object>) value;
        }
        throw new IllegalArgumentException("No login scenario is configured for: " + scenarioName);
    }

    private String requiredValue(Map<String, Object> scenario, String key, String scenarioName) {
        Object rawValue = scenario.get(key);
        if (rawValue == null || String.valueOf(rawValue).isBlank()) {
            throw new IllegalArgumentException(
                    "Missing '" + key + "' in login scenario: " + scenarioName);
        }
        return String.valueOf(rawValue);
    }
}
