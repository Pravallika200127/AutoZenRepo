package constants;

public class Constants {


	/**
	 * Central repository for all test constants
	 * URLs, timeouts, test data, messages, etc.
	 */
	    // ==================== URLS ====================
	    public static class URLs {
	        public static final String LOGIN_PAGE = "https://idp.bits-pilani.ac.in/idp/Authn/UserPassword";
	        public static final String ELIBRARY_URL = "https://your-elibrary-url.com"; // Update with actual URL
	        public static final String PROJECT_PORTAL_URL = "https://your-project-portal-url.com"; // Update with actual URL
	        public static final String COURSE_DETAILS_BASE_URL = "https://your-course-url.com/course/"; // Update with actual URL
	    }
	    
	    // ==================== CREDENTIALS ====================
	    public static class Credentials {
	        public static final String USERNAME = "2021hx70001@wilp.bits-pilani.ac.in";
	        public static final String PASSWORD = "Pravallika@2001";
	    }
	    
	    // ==================== TIMEOUTS (in seconds) ====================
	    public static class Timeouts {
	        public static final int IMPLICIT_WAIT = 10;
	        public static final int EXPLICIT_WAIT = 15;
	        public static final int PAGE_LOAD_TIMEOUT = 30;
	        public static final int SCRIPT_TIMEOUT = 30;
	        public static final int SHORT_WAIT = 5;
	        public static final int MEDIUM_WAIT = 10;
	        public static final int LONG_WAIT = 20;
	        public static final int TAB_SWITCH_WAIT = 2;
	    }
	    
	    // ==================== DASHBOARD TILES ====================
	    public static class DashboardTiles {
	        public static final String MY_ACADEMICS = "My Academics";
	        public static final String EXAMINATIONS = "Examinations";
	        public static final String STUDENT_SERVICES = "Student Services";
	        public static final String WILP_POLICIES = "Wilp Policies";
	    }
	    
	    // ==================== CTA BUTTON NAMES ====================
	    public static class CTAButtons {
	        public static final String DETAILS = "Details";
	        public static final String VIEW_COURSES = "View Courses";
	        public static final String GO_TO_PORTAL = "Go to Viva/Project portal";
	        public static final String SUBMIT = "Submit";
	        public static final String CANCEL = "Cancel";
	        public static final String SAVE = "Save";
	    }
	    
	    // ==================== SECTION NAMES ====================
	    public static class Sections {
	        public static final String MY_COURSES = "My Courses";
	        public static final String MY_VIRTUAL_LABS = "My Virtual Labs";
	        public static final String VIVA_PROJECT = "Viva/Project";
	        public static final String ELIBRARY = "eLibrary";
	        public static final String BANNER_SECTION = "Banner section";
	    }
	    
	    // ==================== ELEMENT NAMES ====================
	    public static class Elements {
	        public static final String BITS_LOGO = "Bits Logo and Name";
	        public static final String STUDENT_NAME = "Student name";
	        public static final String COURSE_TITLE = "Course title";
	        public static final String AVAILABLE_COURSE = "Available course";
	        public static final String DISSERTATION_RATING = "Dissertions Rating";
	        public static final String LOGOUT = "Logout";
	        public static final String BANNER_CONTAINER = "Banner section";
	    }
	    
	    // ==================== SUCCESS MESSAGES ====================
	    public static class SuccessMessages {
	        public static final String LOGIN_SUCCESS = "✅ Login successful";
	        public static final String LOGOUT_SUCCESS = "✅ Logout successful";
	        public static final String PAGE_LOADED = "✅ Page loaded successfully";
	        public static final String ELEMENT_VERIFIED = "✅ Element verified successfully";
	        public static final String NAVIGATION_SUCCESS = "✅ Navigation successful";
	        public static final String TAB_CLOSED = "✅ Tab closed successfully";
	    }
	    
	    // ==================== ERROR MESSAGES ====================
	    public static class ErrorMessages {
	        public static final String LOGIN_FAILED = "❌ Login failed";
	        public static final String ELEMENT_NOT_FOUND = "❌ Element not found";
	        public static final String PAGE_LOAD_FAILED = "❌ Page failed to load";
	        public static final String URL_MISMATCH = "❌ URL does not match expected";
	        public static final String TEXT_MISMATCH = "❌ Text does not match expected";
	        public static final String ELEMENT_NOT_VISIBLE = "❌ Element is not visible";
	    }
	    
	    // ==================== BROWSER SETTINGS ====================
	    public static class Browser {
	        public static final String CHROME = "chrome";
	        public static final String FIREFOX = "firefox";
	        public static final String EDGE = "edge";
	        public static final String SAFARI = "safari";
	    }
	    
	    // ==================== TEST DATA KEYS ====================
	    public static class TestDataKeys {
	        public static final String MY_ACADEMICS_DESCRIPTION = "myAcademics";
	        public static final String EXAMINATIONS_DESCRIPTION = "examinations";
	        public static final String STUDENT_SERVICES_DESCRIPTION = "studentServices";
	        public static final String WILP_POLICIES_DESCRIPTION = "wilpPolicies";
	        public static final String MY_COURSES_DESCRIPTION = "Mycourses";
	        public static final String COURSE_DETAILS_URL = "CoursedetailsURL";
	        public static final String COURSE_TITLE = "courseTitle";
	        public static final String ELIBRARY_URL = "elibraryURL";
	        public static final String DISSERTATION_STATUS = "Dissertation Evaluation Progress and Status";
	    }
	    
	    // ==================== SCREENSHOT SETTINGS ====================
	    public static class Screenshots {
	        public static final String SCREENSHOT_FORMAT = ".png";
	        public static final String SCREENSHOT_FOLDER = "test-output/screenshots/";
	        public static final String FAILURE_PREFIX = "failure_";
	        public static final String SUCCESS_PREFIX = "success_";
	    }
	    
	    // ==================== REPORT SETTINGS ====================
	    public static class Reports {
	        public static final String EXTENT_REPORT_PATH = "test-output/ExtentReport.html";
	        public static final String CUCUMBER_REPORT_PATH = "reports/cucumber-html-report.html";
	        public static final String EXTENT_REPORT_NAME = "BITS Pilani Automation Test Report";
	        public static final String REPORT_TITLE = "Test Execution Report";
	    }
	}
