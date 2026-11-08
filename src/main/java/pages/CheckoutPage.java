package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ElementActions;

public class CheckoutPage {
    private final By signupLoginLink = By.cssSelector("#checkoutModal a[href='/login']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");
    private final By orderCommentField = By.cssSelector("#ordermsg textarea");
    private final By placeOrderLink = By.cssSelector("a[href='/payment']");
    private final By cardholderNameField = By.name("name_on_card");
    private final By cardNumberField = By.name("card_number");
    private final By cardCvcField = By.name("cvc");
    private final By cardExpiryMonthField = By.name("expiry_month");
    private final By cardExpiryYearField = By.name("expiry_year");
    private final By paymentSubmitButton = By.id("submit");
    private final By paymentSuccessMessage = By.cssSelector("#form .col-sm-9 p");
    private final By invoiceDownloadLink = By.cssSelector("a[href*='download_invoice']");
    private final By deliveryAddress = By.id("address_delivery");
    private final By billingAddress = By.id("address_invoice");
    private final By guestCheckoutPrompt = By.cssSelector("#checkoutModal .modal-body p");

    @Step("Open signup and login from checkout")
    public SignupLoginPage openSignupLoginFromCheckout() {
        ElementActions.clickElement(signupLoginLink);
        return new SignupLoginPage();
    }

    @Step("Verify checkout prompts guest to log in or sign up")
    public CheckoutPage assertGuestCheckoutPrompt(String expectedMessage) {
        String actualMessage = ElementActions.getText(guestCheckoutPrompt);
        Assert.assertTrue(actualMessage.contains(expectedMessage),
                "Expected checkout prompt to contain '" + expectedMessage + "' but was '" + actualMessage + "'.");
        Assert.assertTrue(ElementActions.getElement(signupLoginLink).isDisplayed(),
                "Checkout should provide a login or signup link for a guest.");
        return this;
    }

    @Step("Enter order comment")
    public CheckoutPage enterOrderComment(String comment) {
        ElementActions.typeText(orderCommentField, comment);
        return this;
    }

    @Step("Proceed to payment")
    public CheckoutPage proceedToPayment() {
        ElementActions.clickElement(placeOrderLink);
        return this;
    }

    @Step("Submit payment for {cardholderName}")
    public CheckoutPage submitPayment(String cardholderName, String cardNumber, String cvc,
                                     String expiryMonth, String expiryYear) {
        ElementActions.typeText(cardholderNameField, cardholderName);
        ElementActions.typeText(cardNumberField, cardNumber);
        ElementActions.typeText(cardCvcField, cvc);
        ElementActions.typeText(cardExpiryMonthField, expiryMonth);
        ElementActions.typeText(cardExpiryYearField, expiryYear);
        ElementActions.clickElement(paymentSubmitButton);
        return this;
    }

    public String getPaymentSuccessMessage() {
        return ElementActions.getText(paymentSuccessMessage);
    }

    @Step("Download invoice")
    public CheckoutPage downloadInvoice() {
        ElementActions.clickElement(invoiceDownloadLink);
        return this;
    }

    public String getDeliveryAddress() {
        return ElementActions.getText(deliveryAddress);
    }

    public String getBillingAddress() {
        return ElementActions.getText(billingAddress);
    }

    @Step("Continue to home page")
    public HomePage continueToHomePage() {
        ElementActions.clickElement(continueButton);
        return new HomePage();
    }
}
