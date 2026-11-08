package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.DriverFactory;
import utils.ElementActions;
import utils.WaitUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductsPage {
    private final By searchField = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By searchHeading = By.cssSelector("h2.title.text-center");
    private final By continueShoppingButton = By.cssSelector("#cartModal button.close-modal");
    private final By recommendedItems = By.id("recommended-item-carousel");
    private final By cartLink = By.cssSelector("a[href='/view_cart']");

    private WebDriver driver() {
        return DriverFactory.getDriver();
    }

    @Step("Search products for {query}")
    public ProductsPage searchProducts(String query) {
        ElementActions.typeText(searchField, query);
        ElementActions.clickElement(searchButton);
        return this;
    }

    @Step("Open product details for product {productId}")
    public ProductDetailsPage openProductDetails(String productId) {
        By productLink = By.cssSelector("a[href='/product_details/" + productId + "']");
        ElementActions.scrollToElement(productLink);
        clickThroughAdOverlay(productLink);
        return new ProductDetailsPage();
    }

    @Step("Verify product search heading")
    public ProductsPage assertSearchHeading(String expectedHeading) {
        org.testng.Assert.assertEquals(ElementActions.getText(searchHeading), expectedHeading);
        return this;
    }

    @Step("Verify search results are relevant to {query}")
    public ProductsPage assertSearchResultsContainQuery(String query) {
        List<WebElement> resultNames = driver().findElements(
                By.cssSelector(".features_items .productinfo.text-center p")
        );
        org.testng.Assert.assertFalse(resultNames.isEmpty(), "Product search should return at least one result.");

        for (WebElement resultName : resultNames) {
            org.testng.Assert.assertTrue(
                    resultName.getText().toLowerCase().contains(query.toLowerCase()),
                    "Search result did not contain query '" + query + "': " + resultName.getText()
            );
        }
        return this;
    }

    @Step("Add products to cart")
    public ProductsPage addProductsToCart(String... productIds) {
        for (String productId : productIds) {
            By addButton = By.cssSelector("a.add-to-cart[data-product-id='" + productId + "']");
            ElementActions.scrollToElement(addButton);
            clickThroughAdOverlay(addButton);
            closeCartModal();
        }
        return this;
    }

    @Step("Add all search results to cart")
    public List<String> addAllSearchResultsToCart() {
        By productCards = By.cssSelector(".product-image-wrapper");
        int resultCount = ElementActions.getElements(productCards).size();
        List<String> productNames = new ArrayList<>();

        for (int index = 1; index <= resultCount; index++) {
            By resultName = By.xpath("(//div[@class='productinfo text-center']/p)[" + index + "]");
            By addButton = By.xpath(
                    "(//div[@class='productinfo text-center'])[" + index + "]//a[contains(@class,'add-to-cart')]"
            );
            String name = ElementActions.getText(resultName);
            ElementActions.clickElement(addButton);
            closeCartModal();
            productNames.add(name);
        }

        return productNames;
    }

    @Step("Scroll to recommended items")
    public ProductsPage scrollToRecommendedItems() {
        ElementActions.scrollToElement(recommendedItems);
        return this;
    }

    @Step("Get recommended items heading")
    public String getRecommendedItemsHeading() {
        By heading = By.xpath(
                "//h2[translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='RECOMMENDED ITEMS']"
        );
        return ElementActions.getText(heading);
    }

    @Step("Add recommended products to cart")
    public List<String> addRecommendedProductsToCart(String... productIds) {
        List<String> productNames = new ArrayList<>();

        for (String productId : productIds) {
            By productName = By.xpath(
                    "//*[@id='recommended-item-carousel']//*[@data-product-id='" + productId + "']" +
                            "/ancestor::div[contains(@class,'productinfo')]//p"
            );
            By addButton = By.cssSelector(
                    "#recommended-item-carousel a[data-product-id='" + productId + "']"
            );
            String name = ElementActions.getText(productName);
            ElementActions.hoverOverElement(addButton);
            clickThroughAdOverlay(addButton);
            closeCartModal();
            productNames.add(name);
        }

        return productNames;
    }

    @Step("Open cart from products page")
    public CartPage openCartPage() {
        clickThroughAdOverlay(cartLink);
        return new CartPage();
    }

    private void closeCartModal() {
        try {
            ElementActions.clickElement(continueShoppingButton);
        } catch (RuntimeException ignored) {
            // The modal is not displayed when the add-to-cart action does not open it.
        }
    }

    private void clickThroughAdOverlay(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        JavascriptExecutor javascript = (JavascriptExecutor) driver();
        javascript.executeScript("arguments[0].click();", element);
    }
}
