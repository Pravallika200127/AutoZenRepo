package hooks;

import io.cucumber.java.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import drivers.DriverFactory;
import config.ConfigReader;
import utils.Client;
import utils.ExtentReportManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Hooks {

    private static WebDriver driver;
    private static Client testRailClient;
    private static int runId = 0;
    private static boolean testRailEnabled = true;
    private static boolean captureAllSteps = true;
    private static ThreadLocal<String> failureMessage = new ThreadLocal<>();
    private static ThreadLocal<Throwable> failureCause = new ThreadLocal<>();

    @BeforeAll
    public static void setupTestRail() {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🔧 INITIALIZING TESTRAIL INTEGRATION");
            System.out.println("=".repeat(80));

            testRailEnabled = !"false".equalsIgnoreCase(ConfigReader.get("testrail.enabled"));
            captureAllSteps = Boolean.parseBoolean(ConfigReader.get("screenshot.captureAllSteps", "true"));

            if (!testRailEnabled) {
                System.out.println("⚠️ TestRail integration disabled");
                return;
            }

            testRailClient = new Client();
            String runName = "Automated Test Run - " +
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            runId = testRailClient.createTestRun(runName);
            System.out.println("✅ TestRail run created: R" + runId);
        } catch (Exception e) {
            System.err.println("⚠️ TestRail setup failed: " + e.getMessage());
            testRailEnabled = false;
        }
    }

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("\n🚀 Starting Scenario: " + scenario.getName());
        failureMessage.remove(); failureCause.remove();
        try {
            ExtentReportManager.createTest(scenario.getName());
            ExtentReportManager.logInfo("📋 Scenario: " + scenario.getName());
            if (!scenario.getSourceTagNames().isEmpty()) {
                String tags = String.join(", ", scenario.getSourceTagNames());
                ExtentReportManager.logInfo("🏷️ Tags: " + tags);
                scenario.getSourceTagNames().forEach(tag ->
                        ExtentReportManager.assignCategory(tag.replace("@", "")));
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed Extent init: " + e.getMessage());
        }

        driver = DriverFactory.getDriver();
        ExtentReportManager.logPass("✅ Browser initialized");
    }

    @AfterStep
    public void captureStepScreenshot(Scenario scenario) {
        try {
            if (driver != null) {
                boolean shouldCapture = captureAllSteps || scenario.isFailed();
                String title = scenario.isFailed() ? "❌ Failed Step Screenshot" : "✅ Step Screenshot";

                if (shouldCapture) {
                    ExtentReportManager.captureAndAttachScreenshot(driver, title);
                    System.out.println("📸 Screenshot captured: " + title);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Screenshot error: " + e.getMessage());
        }
    }

    @After(order = 1)
    public void updateTestRailResult(Scenario scenario) {
        boolean passed = !scenario.isFailed();
        try {
            if (passed)
                ExtentReportManager.logPass("✅ Scenario passed");
            else {
                ExtentReportManager.logFail("❌ Scenario failed");
                ExtentReportManager.captureAndAttachScreenshot(driver, "🔍 Failure State Screenshot");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Extent update error: " + e.getMessage());
        }

        if (!testRailEnabled || testRailClient == null) return;

        try {
            Integer caseId = extractCaseIdFromScenario(scenario);
            if (caseId == null) return;

            String comment = buildTestResultComment(scenario, passed);
            File screenshotFile = null;

            if (!passed && driver != null) {
                byte[] scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                screenshotFile = saveScreenshotForExtentReport(scr, scenario.getName(), "failure");
            }

            Integer resultId = testRailClient.updateTestResult(caseId, passed, comment);
            if (resultId != null) {
                if (screenshotFile != null) testRailClient.uploadExtentReportToResult(resultId, screenshotFile);
                if (!passed) createDetailedDefect(scenario, caseId, resultId);
            }
        } catch (Exception e) {
            System.err.println("❌ TestRail update failed: " + e.getMessage());
        }
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        try {
            if (driver != null) driver.quit();
        } catch (Exception ignored) {}
        ExtentReportManager.removeTest();
    }

    @AfterAll
    public static void teardownTestRail() {
        try {
            ExtentReportManager.flushReports();
            if (!testRailEnabled || testRailClient == null) return;
            File report = new File("test-output/ExtentReport.html");
            if (report.exists() && testRailClient.getLastResultId() > 0)
                testRailClient.uploadExtentReportToResult(testRailClient.getLastResultId(), report);
            testRailClient.closeTestRun();
        } catch (Exception e) {
            System.err.println("⚠️ Final teardown failed: " + e.getMessage());
        }
    }

    // ========== Utility methods retained exactly ==========

    private Integer extractCaseIdFromScenario(Scenario scenario) {
        for (String tag : scenario.getSourceTagNames()) {
            if (tag.startsWith("@CaseID_")) {
                try {
                    return Integer.parseInt(tag.replace("@CaseID_", ""));
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    private String buildTestResultComment(Scenario scenario, boolean passed) {
        return "**Automated Test Execution**\n\n" +
                "**Scenario:** " + scenario.getName() + "\n" +
                "**Status:** " + (passed ? "✅ PASSED" : "❌ FAILED") + "\n" +
                "**Executed:** " + java.time.LocalDateTime.now() + "\n";
    }

    private File saveScreenshotForExtentReport(byte[] screenshot, String scenarioName, String type) {
        try {
            File dir = new File("test-output/screenshots");
            if (!dir.exists()) dir.mkdirs();
            String fileName = type + "_" + scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            File file = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(screenshot);
            }
            return file;
        } catch (Exception e) {
            System.err.println("⚠️ Save screenshot failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create detailed defect in TestRail based on failure type
     */
    private void createDetailedDefect(Scenario scenario, int caseId, int resultId) {
        try {
            System.out.println("🐛 Analyzing failure and creating defect...");
            
            // Analyze failure type from captured information
            String failureAnalysis = analyzeFailureType(scenario);
            String defectTitle = generateDefectTitle(scenario, failureAnalysis);
            String defectDescription = generateDefectDescription(scenario, failureAnalysis, caseId);
            
            System.out.println("   Detected Failure Type: " + failureAnalysis);
            
            // Create defect
            boolean defectCreated = testRailClient.createDefect(defectTitle, defectDescription);
            
            if (defectCreated) {
                System.out.println("✅ Defect created in TestRail");
                System.out.println("   Type: " + failureAnalysis);
                System.out.println("   Title: " + defectTitle);
            } else {
                System.out.println("⚠️  Failed to create defect");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️  Error creating defect: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Analyze failure type from captured failure information
     */
    private String analyzeFailureType(Scenario scenario) {
        String scenarioName = scenario.getName().toLowerCase();
        String failureMsg = failureMessage.get();
        Throwable cause = failureCause.get();
        
        // Priority 1: Check captured failure message from SeleniumActions
        if (failureMsg != null) {
            String lowerMsg = failureMsg.toLowerCase();
            
            // Check for specific failure patterns
            if (lowerMsg.contains("xpath issue") || 
                lowerMsg.contains("element not found") ||
                lowerMsg.contains("nosuchelementexception") || 
                lowerMsg.contains("element not interactable")) {
                return "XPATH_ISSUE";
            }
            
            if (lowerMsg.contains("expected output mismatch") ||
                lowerMsg.contains("assertionerror") ||
                lowerMsg.contains("expected") && lowerMsg.contains("but got")) {
                return "EXPECTED_OUTPUT_MISMATCH";
            }
            
            if (lowerMsg.contains("timeout") || 
                lowerMsg.contains("timeoutexception")) {
                return "TIMEOUT_ISSUE";
            }
            
            if (lowerMsg.contains("stale element") || 
                lowerMsg.contains("staleelementreferenceexception")) {
                return "STALE_ELEMENT";
            }
        }
        
        // Priority 2: Check throwable cause
        if (cause != null) {
            String causeType = cause.getClass().getSimpleName().toLowerCase();
            
            if (causeType.contains("nosuchelement") || causeType.contains("elementnotinteractable")) {
                return "XPATH_ISSUE";
            }
            if (causeType.contains("assertion")) {
                return "EXPECTED_OUTPUT_MISMATCH";
            }
            if (causeType.contains("timeout")) {
                return "TIMEOUT_ISSUE";
            }
            if (causeType.contains("stale")) {
                return "STALE_ELEMENT";
            }
        }
        
        // Priority 3: Check scenario name
        if (scenarioName.contains("xpath") || scenarioName.contains("locator")) {
            return "XPATH_ISSUE";
        }
        if (scenarioName.contains("expected") || scenarioName.contains("output")) {
            return "EXPECTED_OUTPUT_MISMATCH";
        }
        if (scenarioName.contains("timeout")) {
            return "TIMEOUT_ISSUE";
        }
        
        return "GENERAL_FAILURE";
    }
    
    /**
     * Generate defect title based on failure type
     */
    private String generateDefectTitle(Scenario scenario, String failureType) {
        String scenarioName = scenario.getName();
        
        switch (failureType) {
            case "XPATH_ISSUE":
                return "[XPATH ISSUE] " + scenarioName;
            case "EXPECTED_OUTPUT_MISMATCH":
                return "[EXPECTED OUTPUT MISMATCH] " + scenarioName;
            case "TIMEOUT_ISSUE":
                return "[TIMEOUT] " + scenarioName;
            case "STALE_ELEMENT":
                return "[STALE ELEMENT] " + scenarioName;
            default:
                return "[TEST FAILURE] " + scenarioName;
        }
    }
    
    /**
     * Generate detailed defect description
     */
    private String generateDefectDescription(Scenario scenario, String failureType, int caseId) {
        StringBuilder desc = new StringBuilder();
        
        desc.append("# 🐛 Test Failure Report\n\n");
        
        // Test Information
        desc.append("## Test Information\n");
        desc.append("- **Test Case ID:** C").append(caseId).append("\n");
        desc.append("- **Scenario:** ").append(scenario.getName()).append("\n");
        desc.append("- **Feature File:** ").append(scenario.getUri()).append("\n");
        desc.append("- **Tags:** ").append(scenario.getSourceTagNames()).append("\n");
        desc.append("- **Failed at:** ").append(
            java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
        ).append("\n");
        desc.append("- **Browser:** ").append(ConfigReader.get("browser")).append("\n\n");
        
        // Captured Error Details
        String capturedMessage = failureMessage.get();
        Throwable capturedCause = failureCause.get();
        
        if (capturedMessage != null || capturedCause != null) {
            desc.append("## ❌ Error Details\n\n");
            
            if (capturedMessage != null) {
                desc.append("**Error Message:**\n");
                desc.append("```\n").append(capturedMessage).append("\n```\n\n");
            }
            
            if (capturedCause != null) {
                desc.append("**Exception Type:** ").append(capturedCause.getClass().getSimpleName()).append("\n\n");
                
                // Include stack trace snippet (first 10 lines)
                desc.append("**Stack Trace:**\n");
                desc.append("```\n");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                capturedCause.printStackTrace(pw);
                String stackTrace = sw.toString();
                String[] lines = stackTrace.split("\n");
                int linesToShow = Math.min(10, lines.length);
                for (int i = 0; i < linesToShow; i++) {
                    desc.append(lines[i]).append("\n");
                }
                if (lines.length > 10) {
                    desc.append("... (").append(lines.length - 10).append(" more lines)\n");
                }
                desc.append("```\n\n");
            }
        }
        
        // Failure Analysis based on type
        desc.append("## 🔍 Failure Analysis\n\n");
        
        switch (failureType) {
            case "XPATH_ISSUE":
                desc.append("**Issue Type:** ❌ **XPATH/Locator Issue**\n\n");
                desc.append("**Description:** Test failed due to element locator not working correctly.\n\n");
                desc.append("**Possible Root Causes:**\n");
                desc.append("- Element XPath/ID has changed in the application\n");
                desc.append("- Element is not present on the page at the time of interaction\n");
                desc.append("- Element is hidden, disabled, or not visible\n");
                desc.append("- Page structure/DOM has been modified by developers\n");
                desc.append("- Dynamic content loaded after element search\n\n");
                desc.append("**Recommended Actions:**\n");
                desc.append("1. ✅ Inspect the page and verify current element locators\n");
                desc.append("2. ✅ Update XPath/CSS selectors in step definitions\n");
                desc.append("3. ✅ Add explicit waits if element loads dynamically\n");
                desc.append("4. ✅ Check if element ID/class names changed\n");
                desc.append("5. ✅ Verify element visibility conditions\n\n");
                desc.append("**Priority:** 🔴 HIGH - Blocks test execution\n\n");
                break;
                
            case "EXPECTED_OUTPUT_MISMATCH":
                desc.append("**Issue Type:** ❌ **Expected Output Mismatch**\n\n");
                desc.append("**Description:** Actual application output does not match expected test output.\n\n");
                desc.append("**Possible Root Causes:**\n");
                desc.append("- Application behavior/logic has changed\n");
                desc.append("- Expected values in test assertions are outdated\n");
                desc.append("- Data inconsistency in test environment\n");
                desc.append("- Business requirements changed but tests not updated\n");
                desc.append("- Environmental differences (dev vs staging vs prod)\n\n");
                desc.append("**Recommended Actions:**\n");
                desc.append("1. ✅ Compare actual vs expected output in screenshot\n");
                desc.append("2. ✅ Verify if application changes are intentional\n");
                desc.append("3. ✅ Check with developers if behavior changed\n");
                desc.append("4. ✅ Update test assertions if expected behavior changed\n");
                desc.append("5. ✅ Validate test data setup and environment configuration\n\n");
                desc.append("**Priority:** 🟡 MEDIUM - Requires investigation\n\n");
                break;
                
            case "TIMEOUT_ISSUE":
                desc.append("**Issue Type:** ⏱️ **Timeout Issue**\n\n");
                desc.append("**Description:** Element or condition not met within specified timeout period.\n\n");
                desc.append("**Possible Root Causes:**\n");
                desc.append("- Page/element taking longer than expected to load\n");
                desc.append("- Network latency or connectivity issues\n");
                desc.append("- Server response time is slow\n");
                desc.append("- Wait time configured is insufficient\n");
                desc.append("- Application performance degradation\n\n");
                desc.append("**Recommended Actions:**\n");
                desc.append("1. ✅ Increase explicit wait times in code\n");
                desc.append("2. ✅ Check application performance metrics\n");
                desc.append("3. ✅ Verify network connectivity and speed\n");
                desc.append("4. ✅ Add proper wait conditions (visibility, clickability)\n");
                desc.append("5. ✅ Investigate server response times\n\n");
                desc.append("**Priority:** 🟡 MEDIUM - May indicate performance issue\n\n");
                break;
                
            case "STALE_ELEMENT":
                desc.append("**Issue Type:** ⚠️ **Stale Element Reference**\n\n");
                desc.append("**Description:** Element reference became stale/invalid.\n\n");
                desc.append("**Possible Root Causes:**\n");
                desc.append("- Page refreshed or navigated after element was located\n");
                desc.append("- Element was removed from DOM and re-added\n");
                desc.append("- AJAX/JavaScript updated the page dynamically\n");
                desc.append("- Single Page Application (SPA) re-rendered component\n\n");
                desc.append("**Recommended Actions:**\n");
                desc.append("1. ✅ Re-locate element before each interaction\n");
                desc.append("2. ✅ Add waits after page updates/AJAX calls\n");
                desc.append("3. ✅ Use fresh element references, don't reuse old ones\n");
                desc.append("4. ✅ Implement retry logic for stale elements\n\n");
                desc.append("**Priority:** 🟢 LOW - Code improvement needed\n\n");
                break;
                
            default:
                desc.append("**Issue Type:** ❌ **General Test Failure**\n\n");
                desc.append("**Description:** Test failed due to unspecified reason.\n\n");
                desc.append("**Recommended Actions:**\n");
                desc.append("1. ✅ Review attached screenshot for visual clues\n");
                desc.append("2. ✅ Check console logs for error messages\n");
                desc.append("3. ✅ Review test execution video if available\n");
                desc.append("4. ✅ Re-run test to verify if failure is consistent\n\n");
                desc.append("**Priority:** 🟡 MEDIUM - Needs investigation\n\n");
        }
        
        // Environment Details
        desc.append("## 🖥️ Environment Details\n");
        desc.append("- **Browser:** ").append(ConfigReader.get("browser")).append("\n");
        desc.append("- **Test URL:** ").append(ConfigReader.get("testrail.url")).append("\n");
        desc.append("- **Run ID:** R").append(testRailClient.getRunId()).append("\n\n");
        
        // Steps to Reproduce
        desc.append("## 📋 Steps to Reproduce\n");
        desc.append("1. Navigate to TestRail Run: R").append(testRailClient.getRunId()).append("\n");
        desc.append("2. View test case C").append(caseId).append(" result\n");
        desc.append("3. Check attached screenshot for failure details\n");
        desc.append("4. Run test locally: `mvn clean test -Dcucumber.filter.tags=\"@CaseID_").append(caseId).append("\"`\n\n");
        
        // Attachments
        desc.append("## 📎 Attachments\n");
        desc.append("- Screenshot captured at failure point\n");
        desc.append("- See TestRail result for full execution report\n\n");
        
        // Additional Notes
        desc.append("## 📝 Additional Notes\n");
        desc.append("This defect was automatically created by the test automation framework.\n");
        desc.append("Please review the screenshot and logs before taking action.\n");
        
        return desc.toString();
    }
   
    public static WebDriver getDriver() { return driver; }
    public static void setFailureInfo(String msg, Throwable cause) {
        failureMessage.set(msg);
        failureCause.set(cause);
    }
}
