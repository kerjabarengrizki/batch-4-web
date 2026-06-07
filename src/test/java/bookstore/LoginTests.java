package bookstore;

import core.BaseTests;
import core.DriverManager;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginTests extends BaseTests {

    private static final Logger logger = LogManager.getLogger(LoginTests.class);

    @Test(priority = 1, groups = {"smoke"}, description = "Test successful login")
    public void testLoginSuccess() {

        logger.info("Memulai test login dengan credential standard user");
        LoginPage loginPage= new LoginPage(DriverManager.getDriver());

        logger.info("User login menggunakan credential success user");
        loginPage.login(config.getProperty("emailUser"), config.getProperty("passwordUser"));

        logger.info("Verify user sukses login dan melihat halaman Welcome Message");
        Assert.assertTrue(loginPage.verifyLoginSuccess(),
                "User should be able to see the Welcome Message page after logging in with valid credentials");
        logger.info("testLoginSuccess sudah dijalankan dengan sukses");

    }

    @Test(priority = 2, groups = {"smoke"}, description = "Test failed login scenario")
    public void testLoginFailed() {

        logger.info("Memulai test login dengan credential failed user");
        LoginPage loginPage= new LoginPage(DriverManager.getDriver());

        logger.info("User login menggunakan credential failed user");
        loginPage.login(config.getProperty("emailUser"), config.getProperty("inccorectPassword"));

        logger.info("Verify user gagal login dan melihat error message");
        Assert.assertTrue(loginPage.verifyLoginFailed(),
                "User should be able to see the error message after logging in with invalid credentials");
        logger.info("testLoginFailed sudah dijalankan dengan sukses");
    }
}
