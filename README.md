# Selenium UI automation

Maven project for browser-based registration and login checks against [AutomationExercise](https://automationexercise.com/). Tests use Java 21, Selenium WebDriver, TestNG, JSON test data, the Page Object Model, and Allure reporting.

## Run locally

Prerequisites: JDK 21, Maven, and Chrome, Firefox, or Edge. Selenium Manager resolves the appropriate browser driver.

```powershell
mvn clean test
```

The default configuration runs Chrome headlessly. Override settings with Java system properties, for example:

```powershell
mvn test -Dbrowser=firefox -Dheadless=false -DwaitTimeOutSeconds=45
```

Supported browser values are `chrome`, `firefox`, and `edge`. The base URL and defaults are in `src/main/resources/environment.properties` and `src/main/resources/execution.properties`; command-line system properties take precedence.

## Reports

TestNG/Surefire output is written under `target/surefire-reports`. Allure results are written to `target/allure-results`; generate the HTML report with:

```powershell
mvn allure:report
```

The GitHub Actions workflow runs the suite on every push and pull request, generates the Allure report when results exist, and uploads the reports as a workflow artifact.

## Test data

Test inputs are stored under `src/test/resources` and loaded from the classpath with `JsonFileManager`:

- `loginTestData.json` defines named login scenarios. TestNG runs each scenario through the same data-driven test.
- `RegistrationData.json` contains new-account and existing-account data.
- `uiMessages.json` contains the expected page headings, confirmations, and validation messages used by both test classes.

`JsonFileManager` loads resources from the classpath, reads scalar values as strings, and imports complete JSON objects as maps. This keeps test methods focused on behavior instead of repeating a JSON path for every field. Keep test inputs and expected UI text in these files rather than embedding them in test methods.

The successful-registration test adds a unique suffix to its username and email so repeated runs do not collide. The existing-account test requires the configured address to already exist on the target site.

Page-object methods add readable Allure steps for navigation, form submission, account creation, and visible-result checks. Low-level Selenium actions stay out of the step list so the report focuses on what each test is doing.
