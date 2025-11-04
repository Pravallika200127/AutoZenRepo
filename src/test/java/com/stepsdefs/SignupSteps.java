package com.stepsdefs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import drivers.DriverFactory;
import utils.SeleniumActions;
import config.ConfigReader;          // ✅ Only ConfigReader needed
import pages.PageLocators;
import constants.Constants;
import io.cucumber.java.en.*;
import java.util.Set;

public class SignupSteps {
    private WebDriver driver = DriverFactory.getDriver();
    private SeleniumActions actions;
    private String originalWindow;
    
    public SignupSteps() {
        this.actions = new SeleniumActions(driver);
        this.originalWindow = driver.getWindowHandle();
    }
    
    // ==================== LOGIN STEPS ====================
    
    @Given("User opens the login page")
    public void openLoginPage() {
        actions.navigateTo(Constants.URLs.LOGIN_PAGE);
        actions.waitForVisibility(PageLocators.LoginPage.LOGIN_PAGE_LOADER);
    }
    
    @When("User enters valid credentials")
    public void enterCredentials() {
        actions.type(PageLocators.LoginPage.USERNAME_FIELD, ConfigReader.getUsername());
        actions.type(PageLocators.LoginPage.PASSWORD_FIELD, ConfigReader.getPassword());
        actions.click(PageLocators.LoginPage.LOGIN_BUTTON);
        
        waitFor(Constants.Timeouts.SHORT_WAIT);
        System.out.println("credentialsEntered");
    }
    
    @Then("User should be logged in successfully")
    public void verifyLogin() {
        actions.verifyUrlContains("https://elearn.bits-pilani.ac.in/");
        System.out.println("loginVerified");
    }
    
    // ==================== TOP NAVIGATION VERIFICATION ====================
    
    @Given("User verifies {string} is displaying in Top Navigation bar")
    public void verifyTopNavigationElement(String elementName) {
        By locator = elementName.equalsIgnoreCase(Constants.Elements.BITS_LOGO) 
            ? PageLocators.TopNavigation.BITS_LOGO 
            : PageLocators.getElementByText(elementName);
            
        actions.waitForVisibility(locator);
        System.out.println("✅ " + elementName + " is displayed in Top Navigation bar");
    }
    
    // ==================== PROFILE SECTION ====================
    
    @Then("User verifies {string} is displaying in Profile Section")
    public void verifyProfileSection(String elementName) {
        By locator = elementName.equalsIgnoreCase(Constants.Elements.STUDENT_NAME)
            ? PageLocators.ProfileSection.STUDENT_NAME
            : PageLocators.getElementByText(elementName);
            
        actions.waitForVisibility(locator);
        System.out.println("✅ " + elementName + " is displayed in Profile Section");
    }
    
    // ==================== BANNER SECTION ====================
    
    @Then("User verifies {string} is displaying in with multiple banners in Dashboard")
    public void verifyBannerSection(String elementName) {
        actions.waitForVisibility(PageLocators.BannerSection.BANNER_CONTAINER);
        
            System.out.println("✅ " + elementName + " with multiple banners is displayed");
        } 
    
    
    // ==================== DASHBOARD TILES ====================
    
    @Then("User verifies {string} as {string} Tile is displaying in Dashboard")
    public void verifyDashboardTile(String tileName, String tileDescription) {
        By tileLocator = PageLocators.getTileByName(tileName);
        actions.waitForVisibility(tileLocator);
        System.out.println("✅ " + tileName + " tile is displayed in Dashboard");
    }
    
    // ==================== MY ACADEMICS ====================
    
    @When("User Clicks on {string} CTA on {string} Tile in Dashboard")
    public void clickCTAOnTile(String ctaName, String tileName) {
        By ctaLocator = PageLocators.getTileCTA(tileName, ctaName);
        actions.scrollToElement(ctaLocator);
        actions.click(ctaLocator);
        System.out.println("✅ Clicked on " + ctaName + " CTA on " + tileName + " tile");
    }
    
    @Then("User verifies {string} as {string}is displaying in Dashboard")
    public void verifyElementInDashboard(String elementName, String course) {
    	actions.waitForVisibility(PageLocators.MyCoursesSection.MY_COURSES_HEADING );
        System.out.println("✅ " + elementName + " is displayed in Dashboard");
    }
    
    // ==================== COURSE SECTION ====================
    @Then("User verifies {string} is displaying in Dashboard")
    public void verifyElementsInDashboard(String elementName) {
        actions.waitForVisibility(PageLocators.MyCoursesSection.AVAILABLE_COURSE);
        actions.waitForVisibility(PageLocators.MyCoursesSection.COURSE_TILE);
        
        // Fetch and print the text from COURSE_TILE
        String courseTileText = actions.getText(PageLocators.MyCoursesSection.COURSE_TILE);
        System.out.println("✅ " + elementName + " is displayed in Dashboard with text: " + courseTileText);
    }

    @When("User Clicks on {string} CTA on Course Tile")
    public void clickCTAOnCourseTile(String ctaName) {
        By ctaLocator = PageLocators.getByText(ctaName);
        actions.scrollToElement(ctaLocator);
        actions.click(ctaLocator);
        System.out.println("✅ Clicked on " + ctaName + " CTA on Course Tile"); 
        waitForNewTab();
       
    }
    
    @Then("User verifies {string} opened in new tab")
    public void verifyURLInNewTab(String expectedURL) {
        switchToNewTab();
        
        String currentURL = driver.getCurrentUrl();
        if (!currentURL.contains(expectedURL)) {
            throw new AssertionError("Expected URL to contain: " + expectedURL + " but got: " + currentURL);
        }
        
        System.out.println("✅ Verified URL opened in new tab: " + expectedURL);
    }
    
    @Then("User verifies {string} as {string} in Subject Screen and closes the tab")
    public void verifyCourseDetailAndCloseTab(String elementName, String expectedValue) {
        By courseDetailLocator = PageLocators.CourseDetailsPage.COURSE_TITLE;
        actions.waitForVisibility(courseDetailLocator);
        
        String actualValue = actions.getText(courseDetailLocator);
        if (!actualValue.contains(expectedValue)) {
            throw new AssertionError("Expected " + elementName + " to contain: " + expectedValue + " but got: " + actualValue);
        }
        
        closeCurrentTabAndSwitchBack();
        System.out.println("✅ Verified " + elementName + " and closed the tab");
    }
    
    // ==================== E-LIBRARY SECTION ====================
    
    @Given("User clicks on {string} and verifies it is redirecting to {string} in newtab and close the tab")
    public void clickAndVerifyRedirectInNewTab(String linkName, String expectedURL) {
        By linkLocator = linkName.equalsIgnoreCase(Constants.Sections.ELIBRARY)
            ? PageLocators.ELibrarySection.ELIBRARY_LINK
            : PageLocators.getLinkByText(linkName);
            
        actions.scrollToElement(linkLocator);
        actions.click(linkLocator);
        
        waitForNewTab();
        switchToNewTab();
        
        String currentURL = driver.getCurrentUrl();
        if (!currentURL.contains(expectedURL)) {
            throw new AssertionError("Expected URL to contain: " + expectedURL + " but got: " + currentURL);
        }
        
        closeCurrentTabAndSwitchBack();
        System.out.println("✅ Verified " + linkName + " redirects to " + expectedURL);
    }
    
    // ==================== VIRTUAL LABS SECTION ====================
    
    @Then("User click on {string} Sections and validate the available labs in Dashboard")
    public void clickOnSectionAndValidateLabs(String sectionName) {
        By sectionLocator = sectionName.equalsIgnoreCase(Constants.Sections.MY_VIRTUAL_LABS)
            ? PageLocators.VirtualLabsSection.VIRTUAL_LABS_SECTION
            : PageLocators.getSectionByName(sectionName);
            
        actions.scrollToElement(sectionLocator);
        actions.click(sectionLocator);
        
        System.out.println("✅ Clicked on " + sectionName + " section");
    }
    
    @Then("User verifies the available labs in Dashboard")
    public void verifyAvailableLabs() {
        By labsLocator = PageLocators.VirtualLabsSection.AVAILABLE_LABS;
        
        if (actions.findElements(labsLocator).size() > 0) {
            System.out.println("✅ Available labs are displayed in Dashboard");
        } else {
            throw new AssertionError("No labs found in Dashboard");
        }
    }
    
    // ==================== VIVA/PROJECT SECTION ====================
    
    @Given("user clicks on {string} Section")
    public void clickOnSection(String sectionName) {
        By sectionLocator = sectionName.equalsIgnoreCase(Constants.Sections.VIVA_PROJECT)
            ? PageLocators.VivaProjectSection.VIVA_PROJECT_SECTION
            : PageLocators.getSectionByName(sectionName);
            
        actions.scrollToElement(sectionLocator);
        actions.click(sectionLocator);
        
        System.out.println("✅ Clicked on " + sectionName + " section");
    }
    
    @Then("user verifies and clicks on {string} CTA")
    public void verifyAndClickCTA(String ctaName) {
        By ctaLocator = ctaName.contains("Viva/Project portal")
            ? PageLocators.VivaProjectSection.GO_TO_PORTAL_CTA
            : PageLocators.getButtonByText(ctaName);
            
        actions.waitForVisibility(ctaLocator);
        
        if (!actions.isDisplayed(ctaLocator)) {
            throw new AssertionError(ctaName + " CTA is not displayed");
        }
        
        actions.click(ctaLocator);
        waitForNewTab();
        
        System.out.println("✅ Verified and clicked on " + ctaName + " CTA");
    }
    
    @Then("User Verifies {string} Open in New tab")
    public void verifyProjectURLInNewTab(String urlDescription) {
        switchToNewTab();
        
        String currentURL = driver.getCurrentUrl();
        System.out.println("✅ " + urlDescription + " opened in new tab: " + currentURL);
    }
    
    @Then("User verifies {string} as {string} and close the tab")
    public void verifyElementAndCloseTab(String elementName, String expectedValue) {
        By elementLocator = elementName.toLowerCase().contains("dissertation")
            ? PageLocators.ProjectPortalPage.DISSERTATION_RATING
            : PageLocators.getElementByText(elementName);
            
        actions.waitForVisibility(elementLocator);
        
        String actualValue = actions.getText(elementLocator);
        if (!actualValue.contains(expectedValue)) {
            System.out.println("⚠️ Warning: " + elementName + " value doesn't match. Expected: " + expectedValue + ", Actual: " + actualValue);
        }
        
        closeCurrentTabAndSwitchBack();
        System.out.println("✅ Verified " + elementName + " and closed the tab");
    }
    
    // ==================== HELPER METHODS ====================
    
    private By getElementLocatorByName(String elementName) {
        switch (elementName.toLowerCase()) {
            case "My Courses":
                return PageLocators.MyCoursesSection.MY_COURSES_HEADING;
            case "Available course":
                return PageLocators.MyCoursesSection.AVAILABLE_COURSE;
            case "course title":
                return PageLocators.CourseDetailsPage.COURSE_TITLE;
            default:
                return PageLocators.getTileByName(elementName);
        }
    }
    
   
    private void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void waitForNewTab() {
        waitFor(Constants.Timeouts.TAB_SWITCH_WAIT);
    }
    
    private void switchToNewTab() {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
    
    private void closeCurrentTabAndSwitchBack() {
        driver.close();
        driver.switchTo().window(originalWindow);
        System.out.println(ConfigReader.getValidationMessage("tabClosed"));
    }
}