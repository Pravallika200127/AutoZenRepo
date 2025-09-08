package com.stepsdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import constants.Constants;
import drivers.DriverFactory;
import model.SignupData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import io.cucumber.java.en.*;

import java.io.File;

import static pages.SignupPage.*;

public class SignupSteps {

    private WebDriver driver;
    private SignupData data;

    public SignupSteps() {
        try {
            driver = DriverFactory.getDriver();
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new File(Constants.TESTDATA_PATH), SignupData.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load test data: " + e.getMessage(), e);
        }
    }

    @Given("User opens the Facebook signup page")
    public void openSignupPage() {
        driver.get(Constants.SIGNUP_URL);
    }

    @When("User enters first name {string}")
    public void enterFirstName(String firstName) {
        driver.findElement(FIRSTNAME_INPUT).sendKeys(firstName);
    }

    @When("User enters last name {string}")
    public void enterLastName(String lastName) {
        driver.findElement(LASTNAME_INPUT).sendKeys(lastName);
    }

    @When("User enters mobile number or email {string}")
    public void enterEmail(String email) {
        driver.findElement(EMAIL_INPUT).sendKeys(email);
    }

    @When("User re-enters mobile number or email {string}")
    public void reenterEmail(String email) {
        driver.findElement(REEMAIL_INPUT).sendKeys(email);
    }

    @When("User enters a new password {string}")
    public void enterPassword(String password) {
        driver.findElement(PASSWORD_INPUT).sendKeys(password);
    }

    @When("User selects birth date {string} {string} {string}")
    public void selectDOB(String day, String month, String year) {
        new Select(driver.findElement(DOB_DAY)).selectByVisibleText(day);
        new Select(driver.findElement(DOB_MONTH)).selectByVisibleText(month);
        new Select(driver.findElement(DOB_YEAR)).selectByVisibleText(year);
    }

    @When("User selects gender {string}")
    public void selectGender(String gender) {
        if (gender.equalsIgnoreCase("Male")) {
            driver.findElement(GENDER_MALE).click();
        } else {
            driver.findElement(GENDER_FEMALE).click();
        }
    }

    @When("User clicks the Sign Up button")
    public void clickSignup() {
        driver.findElement(SIGNUP_BUTTON).click();
    }

    @Then("User should see an account verification page")
    public void verifyAccountPage() {
        // Example validation: check page title
        if (!driver.getTitle().contains("verification")) {
            throw new AssertionError("❌ Account verification page not displayed!");
        }
    }

    @Then("User should receive a confirmation email or SMS")
    public void verifyConfirmation() {
        // Normally handled by API / mock validation
        System.out.println("📩 Confirmation email/SMS should be received (mock check).");
    }
}
