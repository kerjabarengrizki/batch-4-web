package bookstore;

import core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(xpath = "//*[@id='core']/div/div/div[2]/div[2]/div[1]/div")
    private WebElement titleHomePage;

    @FindBy(css = ".filter_sort-select--label")
    private WebElement filterSortPrice;

    @FindBy(xpath = "//a[normalize-space()='Sort By ASC']")
    private WebElement sortByASC;

    @FindBy(xpath = "//a[normalize-space()='Sort By DESC']")
    private WebElement sortByDESC;

    @FindBy(xpath = "//input[@id='search-input']")
    private WebElement searchInput;

    @FindBy(xpath = "//button[@id='search-btn']")
    private WebElement searchButton;

    @FindBy(xpath = "//a[@id='navbarDropdown']")
    private WebElement dropdownUser;

    @FindBy(xpath = "//a[@id='profile']")
    private WebElement profileCta;

    @FindBy(xpath = "//a[@id='logout']")
    private WebElement logoutCta;

    @FindBy(xpath = "//a[contains(text(),'E-commerce Bookstore')]")
    private WebElement homepageEntryPoint;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void verifySortIsDisplayed() {
        waitForElementToBeVisible(filterSortPrice);
        filterSortPrice.click();
        waitForElementToBeVisible(sortByASC);
        waitForElementToBeVisible(sortByDESC);
    }

    public void verifyDropdownUserIsDisplayed() {
        waitForElementToBeVisible(dropdownUser);
        dropdownUser.click();
        waitForElementToBeVisible(profileCta);
        waitForElementToBeVisible(logoutCta);
    }

    public void verifySearchFunction() {
        waitForElementToBeVisible(searchInput);
        searchInput.sendKeys("The DevOps Handbook");
        searchButton.click();
    }

    public void verifyLandingOnHomepage() {
        scrollToElement(titleHomePage);
        waitForElementToBeVisible(titleHomePage);
    }

    public void navigateToHomepage() {
        waitForElementToBeVisible(homepageEntryPoint);
        homepageEntryPoint.click();
    }
}

