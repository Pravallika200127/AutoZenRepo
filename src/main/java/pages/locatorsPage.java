package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import constants.Constants;


	public class locatorsPage {
	    private WebDriver driver;

	    public locatorsPage(WebDriver driver) {
	        this.driver = driver;
	    }

    // Method to open signup page
    public void openSignupPage() {
        driver.get(Constants.SIGNUP_PAGE_URL);
    }

    public void enterFirstName(String firstName) {
        driver.findElement(By.name(constants.Constants.FIRST_NAME_FIELD)).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(By.name(constants.Constants.LAST_NAME_FIELD)).sendKeys(lastName);
    }

    public void enterEmail(String email) {
        driver.findElement(By.name(constants.Constants.EMAIL_FIELD)).sendKeys(email);
    }

    public void reEnterEmail(String email) {
        driver.findElement(By.name(constants.Constants.REENTER_EMAIL_FIELD)).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(By.name(constants.Constants.PASSWORD_FIELD)).sendKeys(password);
    }

    public void selectBirthDate(String day, String month, String year) {
        new Select(driver.findElement(By.id(constants.Constants.DAY_DROPDOWN))).selectByVisibleText(day);
        new Select(driver.findElement(By.id(constants.Constants.MONTH_DROPDOWN))).selectByVisibleText(month);
        new Select(driver.findElement(By.id(constants.Constants.YEAR_DROPDOWN))).selectByVisibleText(year);
    }

    public void selectGenderMale() {
        driver.findElement(By.xpath(constants.Constants.GENDER_MALE_RADIO)).click();
    }

    public void clickSignUp() {
        driver.findElement(By.name(constants.Constants.SIGNUP_BUTTON)).click();
    }

    // Placeholder for verification
    public boolean isVerificationPageDisplayed() {
        // Implement verification logic here
        // e.g., check for specific element
        return true; // Dummy return
    }
}