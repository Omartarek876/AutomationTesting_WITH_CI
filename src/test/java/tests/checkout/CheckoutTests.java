package tests.checkout;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.AccountCreatedPage;
import pages.AccountDeletedPage;
import pages.CartPage;
import pages.CheckoutPage;
import pages.HomePage;
import pages.RegisterUserPage;
import pages.SignupLoginPage;
import pages.StorefrontPage;
import support.TestDataFactory;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Listeners(TestNgListener.class)
@Feature("Checkout")
public class CheckoutTests {
    private JsonFileManager checkoutData;
    private JsonFileManager checkoutMessages;
    private JsonFileManager cartData;
    private JsonFileManager registrationData;
    private JsonFileManager registrationMessages;
    private JsonFileManager storefrontMessages;
    private HomePage homePage;
    private boolean accountCreated;
    private String checkoutUserName;
    private Path invoiceDownloadDirectory;

    @Test(description = "Guest checkout prompts user to log in or sign up")
    @Description("Add an item as a guest, proceed to checkout, and verify the login or signup prompt appears.")
    @Severity(SeverityLevel.NORMAL)
    public void checkoutAsGuest() {
        addProductToCart().proceedToCheckout()
                .assertGuestCheckoutPrompt(checkoutMessages.getTestData("$.guestCheckoutPrompt"));
    }

    @Test(description = "Logged-in checkout displays delivery and billing addresses")
    @Description("Create a user, proceed to checkout, and verify both displayed addresses match the account details.")
    @Severity(SeverityLevel.NORMAL)
    public void checkoutAsLoggedInUser() {
        registerCheckoutUser();
        CheckoutPage checkoutPage = addProductToCart().proceedToCheckout();

        assertAddressContainsExpectedDetails(checkoutPage.getDeliveryAddress());
        assertAddressContainsExpectedDetails(checkoutPage.getBillingAddress());
    }

    @Test(description = "Place an order using valid payment details")
    @Description("Create a user, place an order using the configured payment data, and verify order confirmation.")
    @Severity(SeverityLevel.CRITICAL)
    public void placeOrderWithValidPayment() {
        CheckoutPage confirmationPage = placeConfiguredOrder();
        Assert.assertTrue(confirmationPage.getPaymentSuccessMessage()
                        .contains(checkoutMessages.getTestData("$.paymentSuccess")),
                "The order confirmation message was not displayed.");
    }

    @Test(description = "Download an invoice after a successful order")
    @Description("Place a successful order, download its invoice, and verify a file appears in the configured download folder.")
    @Severity(SeverityLevel.NORMAL)
    public void downloadInvoice() throws IOException, InterruptedException {
        CheckoutPage confirmationPage = placeConfiguredOrder();
        Assert.assertTrue(confirmationPage.getPaymentSuccessMessage()
                        .contains(checkoutMessages.getTestData("$.paymentSuccess")),
                "Order confirmation must appear before downloading the invoice.");

        confirmationPage.downloadInvoice();
        Assert.assertTrue(waitForDownloadedInvoice(), "No invoice file was downloaded.");
    }

    private CheckoutPage placeConfiguredOrder() {
        registerCheckoutUser();
        CheckoutPage checkoutPage = addProductToCart().proceedToCheckout();
        checkoutPage.enterOrderComment(checkoutData.getTestData("$.comment")).proceedToPayment();
        return checkoutPage.submitPayment(
                checkoutData.getTestData("$.payment.nameOnCard"),
                checkoutData.getTestData("$.payment.cardNumber"),
                checkoutData.getTestData("$.payment.cvc"),
                checkoutData.getTestData("$.payment.expiryMonth"),
                checkoutData.getTestData("$.payment.expiryYear")
        );
    }

    private CartPage addProductToCart() {
        var productsPage = new StorefrontPage().openProductsPage();
        productsPage.addProductsToCart(cartData.getTestData("$.singleProductId"));
        return productsPage.openCartPage();
    }

    private void registerCheckoutUser() {
        String timestamp = TestDataFactory.timestampSuffix();
        checkoutUserName = TestDataFactory.timestampedValue(
                registrationData.getTestData("$.userData.userName"), timestamp);
        String email = TestDataFactory.timestampedEmail(
                registrationData.getTestData("$.userData.email"), timestamp);

        SignupLoginPage signupLoginPage = homePage.openSignupLoginPage();
        RegisterUserPage registerUserPage = signupLoginPage.startRegistration(checkoutUserName, email);
        registerUserPage.assertAccountInformationHeading(
                registrationMessages.getTestData("$.accountInformationTitle"));
        registerUserPage.fillAccountInformation(
                registrationData.getTestData("$.userData.title"),
                registrationData.getTestData("$.userData.password"),
                registrationData.getTestData("$.userData.day"),
                registrationData.getTestData("$.userData.month"),
                registrationData.getTestData("$.userData.year"),
                Boolean.parseBoolean(registrationData.getTestData("$.userData.newsletter")),
                Boolean.parseBoolean(registrationData.getTestData("$.userData.specialOffers"))
        );
        AccountCreatedPage createdPage = registerUserPage.fillAddressInformation(
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
        createdPage.assertAccountCreated(registrationMessages.getTestData("$.accountCreated"));
        homePage = createdPage.continueToHomePage();
        homePage.assertLoggedInAs(checkoutUserName);
        accountCreated = true;
    }

    private void assertAddressContainsExpectedDetails(String address) {
        String normalizedAddress = normalize(address);
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.firstName"))));
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.lastName"))));
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.address1"))));
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.city"))));
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.state"))));
        Assert.assertTrue(normalizedAddress.contains(normalize(registrationData.getTestData("$.userData.zipcode"))));
    }

    private String normalize(String value) {
        return value.toLowerCase().replaceAll("\\s+", " ").trim();
    }

    private boolean waitForDownloadedInvoice() throws IOException, InterruptedException {
        long deadline = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < deadline) {
            try (Stream<Path> files = Files.list(invoiceDownloadDirectory)) {
                if (files.anyMatch(path -> Files.isRegularFile(path)
                        && !path.getFileName().toString().endsWith(".crdownload"))) {
                    return true;
                }
            }
            Thread.sleep(250);
        }
        return false;
    }

    @BeforeClass
    public void loadTestData() {
        checkoutData = new JsonFileManager("src/test/resources/testdata/checkout/checkoutData.json");
        checkoutMessages = new JsonFileManager("src/test/resources/testdata/checkout/checkoutMessages.json");
        cartData = new JsonFileManager("src/test/resources/testdata/cart/cartData.json");
        registrationData = new JsonFileManager(
                "src/test/resources/testdata/auth/registration/registrationData.json");
        registrationMessages = new JsonFileManager(
                "src/test/resources/testdata/auth/registration/registrationMessages.json");
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
    }

    @BeforeMethod
    public void openHomePage() throws IOException {
        accountCreated = false;
        invoiceDownloadDirectory = Path.of("target", "invoice-downloads").toAbsolutePath();
        Files.createDirectories(invoiceDownloadDirectory);
        try (Stream<Path> files = Files.list(invoiceDownloadDirectory)) {
            files.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new RuntimeException("Could not clear invoice download directory", exception);
                }
            });
        }
        System.setProperty("download.dir", invoiceDownloadDirectory.toString());

        DriverFactory.InitializeDriver();
        homePage = new HomePage().navigateTo(System.getProperty("baseUrl"));
        homePage.assertHomePageHeadings(
                storefrontMessages.getTestData("$.homeTitle"),
                storefrontMessages.getTestData("$.homeSubtitle")
        );
    }

    @AfterMethod
    public void cleanUpAccountAndBrowser() {
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
