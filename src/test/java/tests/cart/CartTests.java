package tests.cart;

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
import pages.CartPage;
import pages.HomePage;
import pages.ProductDetailsPage;
import pages.ProductsPage;
import pages.StorefrontPage;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

import java.util.Arrays;

@Listeners(TestNgListener.class)
@Feature("Cart")
public class CartTests {
    private JsonFileManager cartData;
    private JsonFileManager cartMessages;
    private JsonFileManager storefrontMessages;

    @Test(description = "Add one product to the cart")
    @Description("Add a product from the product list and verify it is displayed in the cart.")
    @Severity(SeverityLevel.NORMAL)
    public void addSingleProductToCart_displaysCorrectProduct() {
        String productId = cartData.getTestData("$.singleProductId");
        ProductsPage productsPage = new StorefrontPage().openProductsPage();
        String productName = productsPage.openProductDetails(productId).getProductName();
        productsPage = new StorefrontPage().openProductsPage();
        productsPage.addProductsToCart(productId).openCartPage();

        new CartPage().assertContainsProducts(Arrays.asList(productName));
        Assert.assertEquals(new CartPage().getProductDetail(productId, "name"), productName);
    }

    @Test(description = "Set product quantity and verify the cart total")
    @Description("Set a product quantity on its details page and verify the quantity and calculated total in the cart.")
    @Severity(SeverityLevel.NORMAL)
    public void setProductQuantity_cartShowsExpectedQuantityAndTotal() {
        String productId = cartData.getTestData("$.singleProductId");
        int quantity = Integer.parseInt(cartData.getTestData("$.quantity"));
        ProductDetailsPage productDetails = new StorefrontPage().openProductsPage().openProductDetails(productId);
        productDetails.setQuantity(Integer.toString(quantity)).addToCart();
        CartPage cartPage = productDetails.openCartPage();

        Assert.assertEquals(cartPage.getProductDetail(productId, "quantity"), Integer.toString(quantity));
        String price = cartPage.getProductDetail(productId, "price");
        String total = cartPage.getProductDetail(productId, "total");
        Assert.assertEquals(amount(total), amount(price) * quantity,
                "Cart total should equal the displayed unit price multiplied by the requested quantity.");
    }

    @Test(description = "Open the cart without products")
    @Description("Open the cart in a fresh browser session and verify the empty-cart message.")
    @Severity(SeverityLevel.NORMAL)
    public void openCartWithoutProducts_displaysEmptyCartMessage() {
        CartPage cartPage = new StorefrontPage().openCartPage();
        Assert.assertTrue(cartPage.getEmptyCartMessage().contains(cartMessages.getTestData("$.emptyCart")));
    }

    private int amount(String value) {
        return Integer.parseInt(value.replaceAll("[^0-9]", ""));
    }

    @BeforeClass
    public void loadTestData() {
        cartData = new JsonFileManager("src/test/resources/testdata/cart/cartData.json");
        cartMessages = new JsonFileManager("src/test/resources/testdata/cart/cartMessages.json");
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
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
