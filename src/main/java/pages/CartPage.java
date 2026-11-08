package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.DriverFactory;
import utils.ElementActions;

import java.util.ArrayList;
import java.util.List;

public class CartPage {
    private final By productRows = By.cssSelector("#cart_info_table tbody tr[id^='product-']");
    private final By footer = By.tagName("footer");
    private final By subscriptionHeading = By.cssSelector(".single-widget h2");
    private final By subscriptionEmailField = By.id("susbscribe_email");
    private final By subscribeButton = By.id("subscribe");
    private final By subscriptionSuccessMessage = By.cssSelector(".alert-success");
    private final By cartHeading = By.cssSelector("#cart_items li.active");
    private final By productNames = By.cssSelector("td.cart_description h4 a");
    private final By checkoutButton = By.cssSelector("a.btn.check_out");
    private final By emptyCartMessage = By.id("empty_cart");

    public boolean hasProductsInCart() {
        return !DriverFactory.getDriver().findElements(productRows).isEmpty();
    }

    @Step("Scroll to cart page footer")
    public CartPage scrollToFooter() {
        ElementActions.scrollToElement(footer);
        return this;
    }

    public String getSubscriptionHeading() {
        return ElementActions.getText(subscriptionHeading);
    }

    @Step("Subscribe with email {email}")
    public CartPage subscribeWithEmail(String email) {
        ElementActions.typeText(subscriptionEmailField, email);
        ElementActions.clickElement(subscribeButton);
        return this;
    }

    public String getSubscriptionSuccessMessage() {
        return ElementActions.getText(subscriptionSuccessMessage);
    }

    public String getCartHeading() {
        return ElementActions.getText(cartHeading);
    }

    public String getProductDetail(String productId, String detailName) {
        String detailSelector = switch (detailName.toLowerCase()) {
            case "name" -> "td.cart_description h4 a";
            case "price" -> "td.cart_price p";
            case "quantity" -> "td.cart_quantity button";
            case "total" -> "td.cart_total p";
            default -> throw new IllegalArgumentException("Unknown cart product detail: " + detailName);
        };
        By productDetail = By.cssSelector("#product-" + productId + " " + detailSelector);
        String value = ElementActions.getText(productDetail);
        return "name".equalsIgnoreCase(detailName) ? normalizeWhitespace(value) : value;
    }

    @Step("Verify cart contains expected products")
    public CartPage assertContainsProducts(List<String> expectedProductNames) {
        List<String> actualProductNames = new ArrayList<>();
        for (WebElement productName : ElementActions.getElements(productNames)) {
            actualProductNames.add(normalizeWhitespace(productName.getText()));
        }

        for (String expectedName : expectedProductNames) {
            if (!actualProductNames.contains(normalizeWhitespace(expectedName))) {
                throw new AssertionError("Cart did not contain product: " + expectedName);
            }
        }
        return this;
    }

    @Step("Remove product {productId} from cart")
    public CartPage removeProductFromCart(String productId) {
        By removeButton = By.cssSelector("#product-" + productId + " .cart_delete a");
        ElementActions.clickElement(removeButton);
        return this;
    }

    public String getEmptyCartMessage() {
        return ElementActions.getText(emptyCartMessage);
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        ElementActions.clickElement(checkoutButton);
        return new CheckoutPage();
    }

    private String normalizeWhitespace(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }
}
