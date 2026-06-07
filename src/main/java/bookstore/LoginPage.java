package bookstore;

import core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "email")
    private WebElement inputEmail;

    @FindBy(id = "password")
    private WebElement inputPassword;

    @FindBy(xpath = "//*[@id='submit']")
    private WebElement signInButtonXpath;

    @FindBy(id = "submit")
    private WebElement signInButtonId;

    @FindBy(css = "#submit")
    private WebElement signInButtonCss;

    @FindBy(xpath = "//*[@id='flash']")
    private WebElement popUpError;

    @FindBy(xpath = "//*[@id='welcome-message']")
    private WebElement welcomeMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        scrollToElement(inputEmail);
        waitForElementToBeVisible(inputEmail); // Tunggu hingga elemen inputEmail terlihat
        inputEmail.sendKeys(username); // Masukkan username ke dalam inputEmail
        inputPassword.sendKeys(password); //Masukan password ke dalam inputPassword
        signInButtonId.click();
    }

    public boolean verifyLoginSuccess() {
        scrollToElement(welcomeMessage);
        waitForElementToBeVisible(welcomeMessage);
        return welcomeMessage.isDisplayed();
    }

    public boolean verifyLoginFailed() {
        waitForElementToBeVisible(popUpError);
        return popUpError.isDisplayed();
    }
}
