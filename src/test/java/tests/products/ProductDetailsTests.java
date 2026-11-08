package tests.products;

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
import pages.ProductDetailsPage;
import pages.StorefrontPage;
import support.TestDataFactory;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

@Listeners(TestNgListener.class)
@Feature("Product Details")
public class ProductDetailsTests {
    private JsonFileManager productData;
    private JsonFileManager productMessages;
    private JsonFileManager reviewData;
    private JsonFileManager reviewMessages;
    private JsonFileManager storefrontMessages;
    private ProductDetailsPage productDetailsPage;

    @Test(description = "Open a product and verify its details")
    @Description("Open the configured product and verify its displayed name and price.")
    @Severity(SeverityLevel.NORMAL)
    public void openProductDetails() {
        Assert.assertEquals(
                productDetailsPage.getProductName(),
                productMessages.getTestData("$.productBlueTop")
        );
        Assert.assertEquals(
                productDetailsPage.getProductInformation("price"),
                productMessages.getTestData("$.productPriceOne")
        );
    }

    @Test(description = "Submit a review for a product")
    @Description("Submit a product review with unique run data and verify the confirmation message.")
    @Severity(SeverityLevel.NORMAL)
    public void submitProductReview() {
        String timestamp = TestDataFactory.timestampSuffix();
        String name = TestDataFactory.timestampedValue(reviewData.getTestData("$.name"), timestamp);
        String email = TestDataFactory.timestampedEmail(reviewData.getTestData("$.email"), timestamp);
        String review = TestDataFactory.timestampedValue(reviewData.getTestData("$.review"), timestamp);

        productDetailsPage
                .openReviewSection()
                .submitReview(name, email, review)
                .assertReviewSubmitted(reviewMessages.getTestData("$.reviewSuccess"));
    }

    @BeforeClass
    public void loadTestData() {
        productData = new JsonFileManager("src/test/resources/testdata/products/productData.json");
        productMessages = new JsonFileManager("src/test/resources/testdata/products/productsMessages.json");
        reviewData = new JsonFileManager("src/test/resources/testdata/review/reviewData.json");
        reviewMessages = new JsonFileManager("src/test/resources/testdata/review/reviewMessages.json");
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
    }

    @BeforeMethod
    public void openConfiguredProductDetails() {
        DriverFactory.InitializeDriver();
        new HomePage()
                .navigateTo(System.getProperty("baseUrl"))
                .assertHomePageHeadings(
                        storefrontMessages.getTestData("$.homeTitle"),
                        storefrontMessages.getTestData("$.homeSubtitle")
                );
        productDetailsPage = new StorefrontPage()
                .openProductsPage()
                .openProductDetails(productData.getTestData("$.singleProductId"));
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }
}
