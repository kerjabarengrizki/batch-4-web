package saucedemo;

import core.BaseTests;
import core.DriverManager;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.asserts.SoftAssert;
import core.TestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LoginTests extends BaseTests {

    private static final Logger logger = LogManager.getLogger(LoginTests.class);

    @Test(priority = 1, groups = {"smoke"}, description = "Test successful login", retryAnalyzer = core.RetryAnalyzer.class)
    public void testLogin() {
        logger.info("Memulai test login dengan credential standard user");
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        logger.info("User login menggunakan credential standard user");
        loginPage.login(config.getProperty("standardUser"), config.getProperty("password"));

        logger.info("Verify user sukses login dan melihat halaman Products");
        Assert.assertTrue(loginPage.isUserLoggedInSuccessfully(),
                "User should be able to see the Products page after logging in with valid credentials");

        logger.info("Verify user sukses redirected ke halaman inventory");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("inventory"),
                "User should be redirected to the inventory page after successful login");

        logger.info("Verify tidak ada error message yang ditampilkan setelah login sukses");
        Assert.assertFalse(loginPage.isErrorMessageDisplayed(),
                "User should not see any error message after successful login");
        logger.info("testLogin sudah dijalankan dengan sukses");
    }

    @DataProvider(name = "loginCredentials", parallel = true)
    public Object[][] loginCredentials() {
        return TestUtils.getTestData("src/test/resources/data/login-data-test.xlsx", "login-tests");
    }

    @Test(priority = 2, dataProvider = "loginCredentials", description = "Data-driven login test")
    public void testDataDriven(String username, String password, String expectedResult) {
        logger.info("Starting data-driven test with username: '{}', expectedResult: '{}'", username, expectedResult);
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.login(username, password);

        SoftAssert softAssert = new SoftAssert();

        if (expectedResult.equalsIgnoreCase("success")) {
            logger.info("Expecting successful login for user: '{}'", username);
            softAssert.assertTrue(loginPage.isUserLoggedInSuccessfully(),
                    "User with username '" + username + "' should be able to login successfully");

            softAssert.assertTrue(loginPage.getCurrentUrl().contains("inventory"),
                    "User should be redirected to the inventory page after successful login");
        } else {
            logger.info("Expecting failed login for user: '{}'", username);
            softAssert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "User with username '" + username + "' should see an error message");

            softAssert.assertFalse(loginPage.isUserLoggedInSuccessfully(),
                    "User should not be able to access the Products page with invalid credentials");
        }

        softAssert.assertAll();
        logger.info("Data-driven test completed for user: '{}'", username);
    }

}
