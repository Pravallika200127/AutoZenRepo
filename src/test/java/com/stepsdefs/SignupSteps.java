package com.stepsdefs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import drivers.DriverFactory;
import utils.SeleniumActions;
import io.cucumber.java.en.*;

public class SignupSteps {

    private WebDriver driver = DriverFactory.getDriver();
    private SeleniumActions actions;
    
    public SignupSteps() {
        // Initialize generic actions wrapper
        this.actions = new SeleniumActions(driver);
    }
    
    @Given("User opens the login page")
    public void openLoginPage() {
        // Generic navigation - automatically handles timeouts and errors
        actions.navigateTo("https://idp.bits-pilani.ac.in/idp/Authn/UserPassword");
        
        // Verify page loaded by checking for email field
        actions.waitForVisibility(By.id("login"));
        
        System.out.println("✅ Login page opened successfully");
    }
    
    @When("User enters valid credentials")
    public void enterCredentials() {
        // Generic type actions - automatically handles NoSuchElement, StaleElement, etc.
        actions.type(By.id("username"), "2021hx70001@wilp.bits-pilani.ac.in");
        actions.type(By.xpath("//input[@id='password']"), "Pravallika@2001");
        
        // Generic click action - automatically handles all click failures
        actions.click(By.id("submitbtn"));
        
        // Small wait for login processing
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✅ Credentials entered and login completed");
    }
    
    @Then("User should be logged in successfully")
    public void verifyLogin() {
        // Generic URL verification - automatically captures mismatches
        actions.verifyUrlContains("dashboard"); // Adjust to your expected URL
        
        // Generic element verification - automatically handles element not found
        if (actions.elementExists(By.id("logout"), 5)) {
            System.out.println("✅ Logout button found - User is logged in");
        } else {
            // This will create "Expected Output Mismatch" defect
            actions.verifyTextEquals(
                By.id("logout"), 
                "Logout" // Will fail if element doesn't exist
            );
        }
        
        System.out.println("✅ Login verification successful");
    }
    
  
    
}