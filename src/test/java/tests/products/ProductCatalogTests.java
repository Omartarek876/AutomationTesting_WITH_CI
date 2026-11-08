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
import pages.StorefrontPage;
import utils.DriverFactory;
import utils.JsonFileManager;
import utils.TestNgListener;

import java.util.Locale;

@Listeners(TestNgListener.class)
@Feature("Product Catalog")
public class ProductCatalogTests {
    private JsonFileManager productData;
    private JsonFileManager productMessages;
    private JsonFileManager storefrontMessages;

    @Test(description = "Search products and verify matching results")
    @Description("Search the catalog and confirm the heading and returned product names match the query.")
    @Severity(SeverityLevel.CRITICAL)
    public void searchProductsWithMatchingQuery_displaysMatchingResults() {
        String query = productData.getTestData("$.searchQuery");

        new StorefrontPage()
                .openProductsPage()
                .searchProducts(query)
                .assertSearchHeading(productMessages.getTestData("$.searchedProductsHeading"))
                .assertSearchResultsContainQuery(query);
    }

    @Test(description = "Filter products by category")
    @Description("Open a configured category and subcategory and verify the resulting category heading.")
    @Severity(SeverityLevel.NORMAL)
    public void filterProductsByCategory_displaysCategoryHeading() {
        String category = productData.getTestData("$.categoryPaths[0].category");
        String subcategory = productData.getTestData("$.categoryPaths[0].subcategory");
        String expectedHeading = productData.getTestData("$.categoryPaths[0].heading");

        new StorefrontPage().openProductsPage();
        new StorefrontPage().openSubcategoryPage(category, subcategory);

        String actualHeading = new StorefrontPage().getCategoryPageHeading();
        Assert.assertEquals(
                normalizeHeading(actualHeading),
                normalizeHeading(expectedHeading)
        );
    }

    @Test(description = "Filter products by brand")
    @Description("Open a configured brand and verify that its name appears in the product heading.")
    @Severity(SeverityLevel.NORMAL)
    public void filterProductsByBrand_displaysBrandHeading() {
        String brand = productData.getTestData("$.brands[0]");
        new StorefrontPage().openProductsPage();
        new StorefrontPage().openBrandPage(brand);
        String actualHeading = new StorefrontPage().getBrandPageHeading();

        Assert.assertTrue(
                actualHeading.toUpperCase().contains(brand.toUpperCase()),
                "Brand heading did not include '" + brand + "': " + actualHeading
        );
    }

    @BeforeClass
    public void loadTestData() {
        productData = new JsonFileManager("src/test/resources/testdata/products/productData.json");
        productMessages = new JsonFileManager("src/test/resources/testdata/products/productsMessages.json");
        storefrontMessages = new JsonFileManager("src/test/resources/testdata/storefront/storefrontMessages.json");
    }

    @BeforeMethod
    public void openHomePage() {
        DriverFactory.InitializeDriver();
        new HomePage()
                .navigateTo(System.getProperty("baseUrl"))
                .assertHomePageHeadings(
                        storefrontMessages.getTestData("$.homeTitle"),
                        storefrontMessages.getTestData("$.homeSubtitle")
                );
    }

    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    private String normalizeHeading(String heading) {
        return heading.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
