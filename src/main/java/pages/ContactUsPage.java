package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.AlertActions;
import utils.ElementActions;

public class ContactUsPage {
    private final By contactFormHeading = By.cssSelector(".contact-form h2");
    private final By nameField = By.name("name");
    private final By emailField = By.name("email");
    private final By subjectField = By.name("subject");
    private final By messageField = By.name("message");
    private final By fileInput = By.cssSelector("input[type='file']");
    private final By submitButton = By.cssSelector("#contact-us-form input[type='submit']");
    private final By submissionSuccessMessage = By.cssSelector("#contact-page .status.alert-success");
    private final By homeButton = By.cssSelector("a.btn-success");

    @Step("Verify contact form heading contains {expectedHeading}")
    public ContactUsPage assertContactFormHeading(String expectedHeading) {
        Assert.assertTrue(ElementActions.getText(contactFormHeading).contains(expectedHeading));
        return this;
    }

    @Step("Submit contact form for {name}")
    public ContactUsPage submitContactForm(String name, String email, String subject,
                                           String message, String filePath) {
        ElementActions.typeText(nameField, name);
        ElementActions.typeText(emailField, email);
        ElementActions.typeText(subjectField, subject);
        ElementActions.typeText(messageField, message);

        if (filePath != null && !filePath.isBlank()) {
            ElementActions.uploadFile(fileInput, filePath);
        }

        ElementActions.clickElement(submitButton);
        AlertActions.acceptAlert();
        return this;
    }

    public String getSubmissionSuccessMessage() {
        return ElementActions.getText(submissionSuccessMessage);
    }

    @Step("Return to home page")
    public HomePage returnToHomePage() {
        ElementActions.clickElement(homeButton);
        return new HomePage();
    }
}
