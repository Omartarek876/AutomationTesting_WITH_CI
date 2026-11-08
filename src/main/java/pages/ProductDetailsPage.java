package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ElementActions;
import utils.WaitUtils;

public class ProductDetailsPage {
    private final By productName = By.cssSelector(".product-information h2");
    private final By quantityField = By.id("quantity");
    private final By addToCartButton = By.cssSelector("button.cart");
    private final By continueShoppingButton = By.cssSelector("#cartModal button.close-modal");
    private final By reviewNameField = By.id("name");
    private final By reviewEmailField = By.id("email");
    private final By reviewTextField = By.id("review");
    private final By reviewSubmitButton = By.id("button-review");
    private final By reviewSuccessMessage = By.xpath("//*[contains(.,'Thank you for your review.')]");
    private final By cartLink = By.cssSelector("a[href='/view_cart']");

    public String getProductName() {
        return normalizeWhitespace(ElementActions.getText(productName));
    }

    private String normalizeWhitespace(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    public String getProductInformation(String informationLabel) {
        if ("price".equalsIgnoreCase(informationLabel)) {
            By price = By.cssSelector(".product-information span span");
            return ElementActions.getText(price);
        }

        By information = By.xpath(
                "//div[@class='product-information']/p[contains(normalize-space(.),'" + informationLabel + "')]"
        );
        return ElementActions.getText(information);
    }

    @Step("Set product quantity to {quantity}")
    public ProductDetailsPage setQuantity(String quantity) {
        WebElement quantityInput = WaitUtils.waitForVisibility(quantityField);
        quantityInput.clear();
        quantityInput.sendKeys(quantity);
        return this;
    }

    @Step("Add product details to cart")
    public ProductDetailsPage addToCart() {
        ElementActions.clickElement(addToCartButton);
        closeCartModal();
        return this;
    }

    @Step("Open the product review section")
    public ProductDetailsPage openReviewSection() {
        WaitUtils.waitForVisibility(reviewSubmitButton);
        return this;
    }

    @Step("Submit review by {name}")
    public ProductDetailsPage submitReview(String name, String email, String review) {
        ElementActions.typeText(reviewNameField, name);
        ElementActions.typeText(reviewEmailField, email);
        ElementActions.typeText(reviewTextField, review);
        ElementActions.clickElement(reviewSubmitButton);
        return this;
    }

    @Step("Verify review submission message")
    public ProductDetailsPage assertReviewSubmitted(String expectedMessage) {
        Assert.assertTrue(ElementActions.getText(reviewSuccessMessage).contains(expectedMessage));
        return this;
    }

    @Step("Open cart from product details")
    public CartPage openCartPage() {
        ElementActions.clickElement(cartLink);
        return new CartPage();
    }

    private void closeCartModal() {
        try {
            ElementActions.clickElement(continueShoppingButton);
        } catch (RuntimeException ignored) {
            // The modal is not displayed when the add-to-cart action does not open it.
        }
    }
}
