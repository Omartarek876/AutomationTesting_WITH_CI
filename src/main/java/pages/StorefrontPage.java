package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverFactory;
import utils.BrowserUtils;
import utils.ElementActions;
import utils.WaitUtils;

import java.time.Duration;

/** Page object for shared storefront navigation, footer, categories, and brands. */
public class StorefrontPage {
    private final By footer = By.tagName("footer");
    private final By header = By.id("header");
    private final By categoryHeading = By.cssSelector("h2.title.text-center");
    private final By subscriptionHeading = By.cssSelector(".single-widget h2");
    private final By subscriptionEmailField = By.id("susbscribe_email");
    private final By subscribeButton = By.id("subscribe");
    private final By subscriptionSuccessMessage = By.cssSelector(".alert-success");
    private final By scrollToTopButton = By.id("scrollUp");

    @Step("Open products page")
    public ProductsPage openProductsPage() {
        BrowserUtils.dismissGoogleAdOverlayIfPresent();
        clickThroughAdOverlay(By.cssSelector("a[href='/products']"));
        return new ProductsPage().waitUntilLoaded();
    }

    @Step("Open cart page")
    public CartPage openCartPage() {
        ElementActions.clickElement(By.cssSelector("a[href='/view_cart']"));
        return new CartPage();
    }

    @Step("Open signup and login page")
    public SignupLoginPage openSignupLoginPage() {
        ElementActions.clickElement(By.cssSelector("a[href='/login']"));
        return new SignupLoginPage();
    }

    @Step("Open contact us page")
    public ContactUsPage openContactUsPage() {
        ElementActions.clickElement(By.cssSelector("a[href='/contact_us']"));
        return new ContactUsPage();
    }

    @Step("Open test cases page")
    public StorefrontPage openTestCasesPage() {
        ElementActions.clickElement(By.cssSelector("a[href='/test_cases']"));
        return this;
    }

    @Step("Log out")
    public SignupLoginPage logOut() {
        ElementActions.clickElement(By.cssSelector("a[href='/logout']"));
        return new SignupLoginPage();
    }

    @Step("Scroll to footer")
    public StorefrontPage scrollToFooter() {
        ElementActions.scrollToElement(footer);
        return this;
    }

    @Step("Scroll to page header")
    public StorefrontPage scrollToHeader() {
        ElementActions.scrollToElement(header);
        return this;
    }

    @Step("Click scroll-to-top arrow")
    public StorefrontPage clickScrollToTopButton() {
        ElementActions.clickElement(scrollToTopButton);
        return this;
    }

    public String getSubscriptionHeading() {
        return ElementActions.getText(subscriptionHeading);
    }

    @Step("Subscribe with email {email}")
    public StorefrontPage subscribeWithEmail(String email) {
        ElementActions.typeText(subscriptionEmailField, email);
        ElementActions.clickElement(subscribeButton);
        return this;
    }

    public String getSubscriptionSuccessMessage() {
        return ElementActions.getText(subscriptionSuccessMessage);
    }

    public String getCategoryLinkText(String categoryName) {
        By categoryLink = By.xpath(
                "//div[@class='left-sidebar']//a[contains(normalize-space(.), '" + categoryName + "')]"
        );
        return ElementActions.getText(categoryLink);
    }

    @Step("Open {subcategoryName} under {categoryName}")
    public ProductsPage openSubcategoryPage(String categoryName, String subcategoryName) {
        int categoryIndex = switch (categoryName.toLowerCase()) {
            case "women" -> 1;
            case "men" -> 2;
            case "kids" -> 3;
            default -> throw new IllegalArgumentException("Unknown product category: " + categoryName);
        };

        By categoryToggle = By.cssSelector(
                "#accordian > div:nth-child(" + categoryIndex + ") h4 > a"
        );
        By subcategoryLink = By.xpath(
                "//div[@id='" + categoryName + "']//a[contains(.,'" + subcategoryName + "')]"
        );
        String previousHeading = ElementActions.getText(categoryHeading);
        clickThroughAdOverlay(categoryToggle);
        clickThroughAdOverlay(subcategoryLink);
        waitForHeadingChange(previousHeading);
        return new ProductsPage();
    }

    public String getCategoryPageHeading() {
        return ElementActions.getText(categoryHeading);
    }

    @Step("Open brand {brandName}")
    public ProductsPage openBrandPage(String brandName) {
        By brandLink = By.xpath(
                "//div[@class='brands-name']//a[contains(normalize-space(.), '" + brandName + "')]"
        );
        String previousHeading = ElementActions.getText(categoryHeading);
        clickThroughAdOverlay(brandLink);
        waitForHeadingChange(previousHeading);
        return new ProductsPage();
    }

    public String getBrandPageHeading() {
        return ElementActions.getText(categoryHeading);
    }

    public String getCurrentUrl() {
        return BrowserUtils.GetCurrentUrl();
    }

    private void clickThroughAdOverlay(By locator) {
        WebElement element = WaitUtils.waitForVisibility(locator);
        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();",
                element
        );
    }

    private void waitForHeadingChange(String previousHeading) {
        new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10)).until(driver -> {
            String currentHeading = driver.findElement(categoryHeading).getText().trim();
            return !currentHeading.equals(previousHeading.trim());
        });
    }
}
