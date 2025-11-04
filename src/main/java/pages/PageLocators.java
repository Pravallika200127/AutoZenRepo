package pages;

import org.openqa.selenium.By;

/**
 * Centralized repository for all page element locators
 * Organized by page sections for easy maintenance
 */
public class PageLocators {
    
    // ==================== LOGIN PAGE ====================
    public static class LoginPage {
        public static final By USERNAME_FIELD = By.id("username");
        public static final By PASSWORD_FIELD = By.xpath("//input[@id='password']");
        public static final By LOGIN_BUTTON = By.id("submitbtn");
        public static final By LOGIN_PAGE_LOADER = By.xpath("//div[@class='row']");
    }
    
    // ==================== TOP NAVIGATION ====================
    public static class TopNavigation {
        public static final By BITS_LOGO = By.xpath("//nav[contains(@class,'d-lg-block d-none navbar')]//a[@class='navbar-brand']//img");
    }
    
    // ==================== PROFILE SECTION ====================
    public static class ProfileSection {
        public static final By STUDENT_NAME = By.xpath("//span[@class='mx-1 text-black']");
    }
    
    // ==================== DASHBOARD - BANNER SECTION ====================
    public static class BannerSection {
        public static final By BANNER_CONTAINER = By.xpath("//main[@class='flex-shrink-0']");
        }
    
    // ==================== DASHBOARD - TILES ====================
    public static class DashboardTiles {
        // My Academics Tile
        public static final By MY_ACADEMICS_TILE = By.xpath("//li[contains(@class, 'active') and contains(., 'My Academics')]  | //div[@data-tile='my-academics']");
        public static final By MY_ACADEMICS_DETAILS_CTA = By.xpath("//li[contains(., 'My Academics')]//a[contains(., 'Details')] | //li[contains(., 'My Academics')]//a[contains(., 'Details')]");
        
        // Examinations Tile
        public static final By EXAMINATIONS_TILE = By.xpath("//li[contains(@class, 'active') and contains(., 'Examinations')] | //div[@data-tile='examinations']");
    public static final By EXAMINATIONS_CTA = By.xpath("//li[contains(., 'Examinations')]//a | //li[contains(., 'Examinations')]//a[contains(@class, 'Details')]");
        
        // Student Services Tile
        public static final By STUDENT_SERVICES_TILE = By.xpath("//li[contains(@class, 'active') and contains(., 'Student Support')] | //div[@data-tile='Student Support']");
        public static final By STUDENT_SERVICES_CTA = By.xpath("//li[contains(., 'Student Support')]//a | //li[contains(., 'Student Support')]//a[contains(@class, 'Details')]");
        
        // WILP Policies Tile
        public static final By WILP_POLICIES_TILE = By.xpath("//li[contains(@class, 'active') and contains(., 'WILP Policies')] | //div[@data-tile='WILP Policies']");
        public static final By WILP_POLICIES_CTA = By.xpath("//li[contains(., 'WILP Policies')]//a | //li[contains(., 'WILP Policies')]//a[contains(@class, 'Details')]");
    }
    
    // ==================== MY COURSES SECTION ====================
    public static class MyCoursesSection {
        public static final By MY_COURSES_HEADING = By.id("myCourses");
        public static final By COURSE_CARDS = By.xpath("//div[contains(@class, 'course-card')] | //div[contains(@class, 'course-item')]");
        public static final By COURSE_TILE = By.xpath("//div[@class='col-lg-3 col-sm-12 col-md-6 mt-4 course-card']//div[@class='card-body text-start text-wrap']");
        public static final By VIEW_COURSES_CTA = By.xpath("//div[contains(@class,'card-footer text-center py-3 bg-body-tertiary text-primary')]");
        public static final By AVAILABLE_COURSE = By.xpath("//h5[normalize-space()='Dissertation (S2-24_SEHEXZG628T)']");
    }
    
    // ==================== COURSE DETAILS PAGE ====================
    public static class CourseDetailsPage {
        public static final By COURSE_TITLE = By.xpath("//h1[contains(@class, 'course-title')] | //div[contains(@class, 'course-header')]//h1");
        public static final By COURSE_CODE = By.xpath("//span[contains(@class, 'course-code')] | //div[contains(@class, 'course-meta')]//span");
        public static final By COURSE_DESCRIPTION = By.xpath("//div[contains(@class, 'course-description')]");
        public static final By COURSE_INSTRUCTOR = By.xpath("//div[contains(@class, 'instructor-name')] | //span[contains(@class, 'faculty')]");
    }
    
    // ==================== E-LIBRARY SECTION ====================
    public static class ELibrarySection {
        public static final By ELIBRARY_LINK = By.xpath("//a[contains(., 'eLibrary')] | //a[contains(., 'Library')] | //div[contains(@class, 'library')]//a");
        public static final By ELIBRARY_ICON = By.xpath("//i[contains(@class, 'library-icon')] | //img[@alt='Library']");
        public static final By ELIBRARY_TILE = By.xpath("//div[contains(@class, 'tile') and contains(., 'Library')]");
    }
    
    // ==================== VIRTUAL LABS SECTION ====================
    public static class VirtualLabsSection {
        public static final By VIRTUAL_LABS_SECTION = By.xpath("//div[contains(., 'Virtual Labs')] | //section[@id='virtual-labs']");
        public static final By VIRTUAL_LABS_LINK = By.xpath("//a[contains(., 'Virtual Labs')] | //a[contains(., 'My Virtual Labs')]");
        public static final By LAB_CARDS = By.xpath("//div[contains(@class, 'lab-card')] | //div[contains(@class, 'virtual-lab-item')]");
        public static final By AVAILABLE_LABS = By.xpath("//div[contains(@class, 'lab-available')] | //div[contains(@class, 'lab') and contains(@class, 'active')]");
        public static final By LAB_TITLE = By.xpath("//h3[contains(@class, 'lab-title')] | //div[contains(@class, 'lab-name')]");
    }
    
    // ==================== VIVA/PROJECT SECTION ====================
    public static class VivaProjectSection {
        public static final By VIVA_PROJECT_SECTION = By.xpath("//div[contains(., 'Viva/Project')] | //section[@id='viva-project']");
        public static final By VIVA_PROJECT_LINK = By.xpath("//a[contains(., 'Viva/Project')] | //a[contains(., 'Project Portal')]");
        public static final By GO_TO_PORTAL_CTA = By.xpath("//button[contains(., 'Go to Viva/Project portal')] | //a[contains(., 'Go to Viva/Project portal')]");
        public static final By PROJECT_TILE = By.xpath("//div[contains(@class, 'project-tile')]");
    }
    
    // ==================== PROJECT PORTAL PAGE ====================
    public static class ProjectPortalPage {
        public static final By DISSERTATION_RATING = By.xpath("//div[contains(., 'Dissertation')] | //h2[contains(., 'Dissertation Evaluation')]");
        public static final By EVALUATION_PROGRESS = By.xpath("//div[contains(@class, 'evaluation-progress')] | //div[contains(., 'Progress')]");
        public static final By EVALUATION_STATUS = By.xpath("//span[contains(@class, 'status')] | //div[contains(@class, 'evaluation-status')]");
        public static final By PROJECT_DETAILS = By.xpath("//div[contains(@class, 'project-details')]");
    }
    
    // ==================== COMMON ELEMENTS ====================
    public static class Common {
        public static final By LOADING_SPINNER = By.xpath("//div[contains(@class, 'spinner')] | //div[contains(@class, 'loading')]");
        public static final By ERROR_MESSAGE = By.xpath("//div[contains(@class, 'error')] | //span[contains(@class, 'error-message')]");
        public static final By SUCCESS_MESSAGE = By.xpath("//div[contains(@class, 'success')] | //span[contains(@class, 'success-message')]");
        public static final By CLOSE_BUTTON = By.xpath("//button[contains(@class, 'close')] | //button[contains(., '×')]");
    }
    
    // ==================== DYNAMIC LOCATOR BUILDERS ====================
    
    /**
     * Get locator for any tile by name
     */
    public static By getTileByName(String tileName) {
        return By.xpath("//li[contains(@class, 'active') and contains(., '" + tileName + "')]");
    }
    
    /**
     * Get locator for CTA button on a specific tile
     */
    public static By getTileCTA(String tileName, String ctaText) {
        return By.xpath("//li[contains(., '" + tileName + "')]//a[contains(., '" + ctaText + "')] | " +
                       "//li[contains(., '" + tileName + "')]//a[contains(., '" + ctaText + "')]");
    }
    
    /**
     * Get locator for any link by text
     */
    public static By getLinkByText(String linkText) {
        return By.xpath("//a[contains(., '" + linkText + "')]");
    }
    
    /**
     * Get locator for any button by text
     */
    public static By getButtonByText(String buttonText) {
        return By.xpath("//button[contains(., '" + buttonText + "')] | //input[@type='button' and contains(@value, '" + buttonText + "')]");
    }
    
    public static By getByText(String text) {
        return By.xpath("(//a[@class='link-underline link-underline-opacity-0'])[2]");
    }
    /**
     * Get locator for any element containing specific text
     */
    public static By getElementByText(String text) {
        return By.xpath("//*[contains(text(), '" + text + "')]");
    }
    
    /**
     * Get section locator by name
     */
    public static By getSectionByName(String sectionName) {
        return By.xpath("//div[contains(., '" + sectionName + "')] | " +
                       "//section[contains(., '" + sectionName + "')] | " +
                       "//h2[contains(., '" + sectionName + "')]");
    }
}