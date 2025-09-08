package com.stepsdefs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.locatorsPage;
import io.cucumber.java.en.*;
import io.cucumber.java.After;

public class SignupSteps {

    private WebDriver driver;
    private locatorsPage locatorsPage; // fixed typo in variable name

    @Given("User opens the Facebook signup page")
    public void user_opens_the_facebook_signup_page() {
        // Initialize WebDriver (example with ChromeDriver)
        driver = new ChromeDriver();
        locatorsPage = new locatorsPage(driver);
        locatorsPage.openSignupPage();
        throw new io.cucumber.java.PendingException();
    }

    @When("User enters first name {string}")
    public void user_enters_first_name(String string) {
        locatorsPage.enterFirstName(string);
        throw new io.cucumber.java.PendingException();
    }

    public void user_enters_last_name(String lastName) {
        locatorsPage.enterLastName(lastName);
        throw new io.cucumber.java.PendingException();
    }

    @And("User enters mobile number or email {string}")
    public void user_enters_mobile_or_email(String email) {
        locatorsPage.enterEmail(email);
        throw new io.cucumber.java.PendingException();
    }

    @And("User re-enters mobile number or email {string}")
    public void user_reenters_mobile_or_email(String email) {
        locatorsPage.reEnterEmail(email);
        throw new io.cucumber.java.PendingException();
    }

    @And("User enters a new password {string}")
    public void user_enters_a_new_password(String password) {
        locatorsPage.enterPassword(password);
        throw new io.cucumber.java.PendingException();
    }

    @And("User selects birth date {string} {string} {string}")
    public void user_selects_birth_date(String day, String month, String year) {
        locatorsPage.selectBirthDate(day, month, year);
        throw new io.cucumber.java.PendingException();
    }

    @And("User selects gender {string}")
    public void user_selects_gender(String gender) {
        if (gender.equalsIgnoreCase("Male")) {
            locatorsPage.selectGenderMale();
            throw new io.cucumber.java.PendingException();
        }
    }

    @And("User clicks the Sign Up button")
    public void user_clicks_the_sign_up_button() {
        locatorsPage.clickSignUp();
        throw new io.cucumber.java.PendingException();
    }

    @Then("User should see an account verification page")
    public void user_should_see_an_account_verification_page() {
        boolean isVerificationPage = locatorsPage.isVerificationPageDisplayed();
        // Add assertions here, e.g.,
        // Assert.assertTrue(isVerificationPage);
        throw new io.cucumber.java.PendingException();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}