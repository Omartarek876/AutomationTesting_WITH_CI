# AutomationExercise UI Automation Framework

Java and Selenium UI automation for [AutomationExercise](https://automationexercise.com/), built to demonstrate maintainable test design, reusable Page Objects, browser configuration, data-driven scenarios, reporting, and continuous integration.

The framework uses **Java 21**, **Maven**, **Selenium WebDriver**, **TestNG**, and **Allure**. Tests exercise the public practice site, so results can depend on the availability and current data of that external site.

## What the project demonstrates

- Page Object Model with focused page classes and fluent transitions between pages.
- Reusable browser, element, wait, alert, configuration, validation, and JSON data utilities.
- TestNG lifecycle hooks, scenario descriptions, feature labels, severity labels, and failure screenshots.
- JSON-based test data and expected messages, organized by feature.
- GitHub Actions execution on pushes, pull requests, and manual dispatch.

## Current test coverage

| Area | Automated scenarios |
| --- | --- |
| Login | Valid credentials, invalid credentials, and logout |
| Registration | Successful account creation and cleanup, duplicate email, and required-field validation |
| Product catalog | Search results, category filtering, and brand filtering |
| Product details | Product name and price, and review submission |
| Cart | Single and multiple products, quantity and total calculation, and empty-cart state |

The page objects also provide reusable actions for checkout, contact form, subscriptions, product recommendations, and other storefront flows. These are framework capabilities; they are not all covered by automated tests yet.

## Technology

| Component | Version / role |
| --- | --- |
| Java | 21 |
| Maven | Build and dependency management |
| Selenium WebDriver | 4.52.0; browser automation |
| TestNG | 7.11.0; test execution and lifecycle |
| Allure TestNG | 3.0.0; test results and reporting |
| JsonPath | 2.10.0; reading JSON test data |
| JSON Simple | 1.1.1; JSON parsing |
| AspectJ Weaver | 1.9.25; Allure instrumentation |
| GitHub Actions | CI workflow in `.github/workflows/CI.yml` |

## Project structure

```text
.
├── .github/
│   └── workflows/
│       └── CI.yml                         # GitHub Actions test and report workflow
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── pages/                     # Page Object Model
│   │   │   │   ├── HomePage.java
│   │   │   │   ├── SignupLoginPage.java
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── RegisterUserPage.java
│   │   │   │   ├── AccountCreatedPage.java
│   │   │   │   ├── AccountDeletedPage.java
│   │   │   │   ├── StorefrontPage.java
│   │   │   │   ├── ProductsPage.java
│   │   │   │   ├── ProductDetailsPage.java
│   │   │   │   ├── CartPage.java
│   │   │   │   ├── CheckoutPage.java
│   │   │   │   └── ContactUsPage.java
│   │   │   └── utils/                      # Driver, browser, actions, waits, config, JSON, listener
│   │   └── resources/
│   │       ├── environment.properties      # Base URL and browser defaults
│   │       └── execution.properties       # Execution defaults
│   └── test/
│       ├── java/
│       │   ├── tests/
│       │   │   ├── auth/                   # Login and registration tests
│       │   │   ├── cart/                   # Cart scenarios
│       │   │   └── products/               # Catalog and product detail scenarios
│       │   └── support/
│       │       └── TestDataFactory.java    # Unique test input helpers
│       └── resources/testdata/             # Feature-specific JSON data/messages
├── pom.xml                                 # Dependencies and Maven plugins
└── README.md
```

### Framework responsibilities

- **Pages** encapsulate locators, page actions, assertions, and page-to-page transitions. Action methods return the current or next page object to support readable chaining.
- **`DriverFactory`** configures and manages the browser driver. **`BrowserUtils`** provides browser navigation, and **`ElementActions`** and **`WaitUtils`** provide common interactions and waits.
- **`PropertiesReader`** loads bundled defaults without overriding command-line Java system properties. **`JsonFileManager`** reads JSON values using JsonPath.
- **`TestDataFactory`** creates timestamped names and email addresses for scenarios that need unique values.
- **`AlertActions`** handles browser alerts, **`Validations`** provides reusable checks, and **`TestNgListener`** loads configuration, logs test outcomes, captures failure screenshots for Allure, and generates the report after a suite.
- **Test data** is separated from test logic under `src/test/resources/testdata/`, with folders for authentication, cart, checkout, contact, products, reviews, and storefront scenarios. Most feature folders include data and expected-message JSON files; contact resources also include an upload image.

## Requirements

- JDK 21
- Maven 3.8 or newer
- Chrome, Firefox, or Microsoft Edge
- Network access to AutomationExercise

Selenium Manager resolves the browser driver when the tests start. No driver executable is stored in the repository.

## Run locally

Run the TestNG suite from the repository root:

```bash
mvn clean test
```

The defaults are Chrome, headless mode enabled, window maximization disabled, and a 30-second wait timeout. Override them with Maven system properties:

```bash
mvn clean test -Dbrowser=firefox -Dheadless=false -DwindowMaximize=true -DwaitTimeOutSeconds=45
```

Supported `browser` values are `chrome`, `firefox`, and `edge`. The `baseUrl` can also be overridden:

```bash
mvn clean test -DbaseUrl=https://automationexercise.com/
```

Defaults are loaded from `src/main/resources/environment.properties` and `src/main/resources/execution.properties`. Values supplied with `-D` take precedence.

## Test data

Test data and expected messages are stored by feature. For example:

```text
src/test/resources/testdata/
├── auth/
│   ├── login/
│   └── registration/
├── cart/
├── checkout/
├── contact/
├── products/
├── review/
└── storefront/
```

Update the relevant JSON files when adding scenarios or changing test inputs and expected UI messages. Keep account credentials and email addresses suitable for the target practice site; successful registration requires an email address that is not already registered.

Registration and review scenarios generate unique values from their configured JSON data to reduce collisions across repeated runs.

## Reports

- Surefire and TestNG output: `target/surefire-reports/`
- Allure raw results: `target/allure-results/`
- Allure HTML report: `target/allure-report/`

The TestNG listener generates an Allure report when a suite finishes. You can also request one manually after results have been created:

```bash
mvn allure:report
```

## Continuous integration

The workflow at `.github/workflows/CI.yml` runs on pushes, pull requests, and manual dispatch. It uses Ubuntu and Java 21, runs `mvn clean test`, generates an Allure report when results exist, and uploads Surefire and Allure output as the `ui-test-results` artifact for 14 days.
