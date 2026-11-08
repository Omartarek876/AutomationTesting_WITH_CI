package tests.contact;

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
import pages.ContactUsPage;
import pages.HomePage;
import pages.StorefrontPage;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

@Listeners(TestNgListener.class)
@Feature("Contact Us")
public class ContactUsTests {
    private JsonFileManager contactData;
    private JsonFileManager contactMessages;
    private JsonFileManager storefrontMessages;

    @Test(description = "Submit the contact form with valid details")
    @Description("Complete the contact form with valid data and verify the confirmation message.")
    @Severity(SeverityLevel.NORMAL)
    public void submitContactFormWithValidDetails() {
        ContactUsPage contactPage = new StorefrontPage().openContactUsPage();
        contactPage.assertContactFormHeading(contactMessages.getTestData("$.contactHeader"));
        contactPage.submitContactForm(
                contactData.getTestData("$.name"),
                contactData.getTestData("$.email"),
                contactData.getTestData("$.subject"),
                contactData.getTestData("$.message"),
                ""
        );
        Assert.assertTrue(contactPage.getSubmissionSuccessMessage()
                        .contains(contactMessages.getTestData("$.contactSuccess")),
                "Contact form confirmation was not displayed.");
    }

    @BeforeClass
    public void loadTestData() {
        contactData = new JsonFileManager("src/test/resources/testdata/contact/contactData.json");
        contactMessages = new JsonFileManager("src/test/resources/testdata/contact/contactMessages.json");
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
