package com.stepsdefs;

import drivers.DriverFactory;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GenericSteps {

    WebDriver driver = DriverFactory.getDriver();

    @Given("User opens the Facebook signup page")
    public void openFacebookSignupPage() {
        driver.get("https://www.facebook.com/r.php");
    }

    @When("User enters first name {string}")
    public void enterFirstName(String firstName) {
        driver.findElement(By.name("firstname")).sendKeys(firstName);
    }

    @When("User enters last name {string}")
    public void enterLastName(String lastName) {
        driver.findElement(By.name("lastname")).sendKeys(lastName);
    }

    @When("User enters mobile number or email {string}")
    public void enterEmail(String email) {
        driver.findElement(By.name("reg_email__")).sendKeys(email);
    }

    @When("User re-enters mobile number or email {string}")
    public void reEnterEmail(String email) {
        driver.findElement(By.name("reg_email_confirmation__")).sendKeys(email);
    }

    @When("User enters a new password {string}")
    public void enterPassword(String password) {
        driver.findElement(By.name("reg_passwd__")).sendKeys(password);
    }

    @When("User selects birth date {string} {string} {string}")
    public void selectBirthDate(String day, String month, String year) {
        driver.findElement(By.name("birthday_day")).sendKeys(day);
        driver.findElement(By.name("birthday_month")).sendKeys(month);
        driver.findElement(By.name("birthday_year")).sendKeys(year);
    }

    @When("User selects gender {string}")
    public void selectGender(String gender) {
        if (gender.equalsIgnoreCase("Male")) {
            driver.findElement(By.xpath("//label[text()='Male']")).click();
        } else if (gender.equalsIgnoreCase("Female")) {
            driver.findElement(By.xpath("//label[text()='Female']")).click();
        } else {
            driver.findElement(By.xpath("//label[text()='Custom']")).click();
        }
    }

    @When("User clicks the Sign Up button")
    public void clickSignUp() {
        driver.findElement(By.name("websubmit")).click();
    }

    @Then("User should see an account verification page")
    public void verifyAccountVerificationPage() {
        // Basic check: Facebook redirects to confirmation
        boolean isDisplayed = driver.getPageSource().contains("Enter the code");
        if (!isDisplayed) {
            throw new AssertionError("Verification page not displayed!");
        }
    }

    @Then("User should receive a confirmation email or SMS")
    public void verifyConfirmationMessage() {
        // This is only a placeholder; in real tests, you’d check mailbox or SMS API
        System.out.println("Confirmation email or SMS should be sent.");
    }
}
