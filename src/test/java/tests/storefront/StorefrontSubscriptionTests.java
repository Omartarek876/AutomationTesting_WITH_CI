package tests.storefront;

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
import pages.HomePage;
import pages.StorefrontPage;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

@Listeners(TestNgListener.class)
@Feature("Storefront")
public class StorefrontSubscriptionTests {
    private JsonFileManager storefrontMessages;
    private JsonFileManager cartData;

    @Test(description = "Subscribe to the storefront with a valid email")
    @Description("Subscribe using a valid email address and verify the success message in the footer.")
    @Severity(SeverityLevel.NORMAL)
    public void subscribeWithValidEmail() {
        StorefrontPage storefrontPage = new StorefrontPage().scrollToFooter();
        Assert.assertEquals(storefrontPage.getSubscriptionHeading(),
                storefrontMessages.getTestData("$.subscriptionHeading"));
        storefrontPage.subscribeWithEmail(cartData.getTestData("$.subscriptionEmail"));
        Assert.assertTrue(storefrontPage.getSubscriptionSuccessMessage()
                        .contains(storefrontMessages.getTestData("$.subscriptionSuccess")),
                "Subscription success message was not displayed.");
    }

    @BeforeClass
    public void loadTestData() {
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
        cartData = new JsonFileManager("src/test/resources/testdata/cart/cartData.json");
    }

    @BeforeMethod
    public void openHomePage() {
        DriverFactory.InitializeDriver();
        new HomePage().navigateTo(System.getProperty("baseUrl"))
                .assertHomePageHeadings(
                        storefrontMessages.getTestData("$.homeTitle"),
                        storefrontMessages.getTestData("$.homeSubtitle")
                );
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }
}
