package utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import config.ConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";
    private static final String EXTENT_PROPERTIES = "src/test/resources/extent.properties";

    /** Initialize Extent Reports */
    public static void initReports() {
        if (extent == null) {
            System.out.println("🔧 Initializing Extent Reports...");

            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) screenshotDir.mkdirs();

            Properties props = loadExtentProperties();

            String reportPath = props.getProperty("extent.reporter.spark.out", "test-output/ExtentReport.html");
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            String configPath = props.getProperty("extent.reporter.spark.config");
            if (configPath != null && !configPath.isEmpty() && new File(configPath).exists()) {
                try {
                    sparkReporter.loadXMLConfig(configPath);
                    System.out.println("✅ Loaded extent-config.xml from: " + configPath);
                } catch (Exception e) {
                    System.err.println("⚠️ Could not load XML config: " + e.getMessage());
                    configureReporterFromProperties(sparkReporter, props);
                }
            } else {
                configureReporterFromProperties(sparkReporter, props);
            }

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            setSystemInfoFromProperties(props);
            System.out.println("✅ Extent Reports initialized successfully");
        }
    }

    private static Properties loadExtentProperties() {
        Properties props = new Properties();
        File file = new File(EXTENT_PROPERTIES);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                System.out.println("✅ Loaded extent.properties");
            } catch (IOException e) {
                System.err.println("⚠️ Could not load extent.properties: " + e.getMessage());
            }
        }
        return props;
    }

    private static void configureReporterFromProperties(ExtentSparkReporter reporter, Properties props) {
        String theme = props.getProperty("theme", "dark").toUpperCase();
        reporter.config().setTheme("DARK".equals(theme) ? Theme.DARK : Theme.STANDARD);
        reporter.config().setReportName(props.getProperty("reportName", "Automation Execution Report"));
        reporter.config().setDocumentTitle(props.getProperty("documentTitle", "Test Execution Summary"));
        reporter.config().setEncoding(props.getProperty("encoding", "UTF-8"));
        reporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
    }

    private static void setSystemInfoFromProperties(Properties props) {
        props.forEach((key, value) -> {
            String keyStr = key.toString();
            if (keyStr.startsWith("systeminfo.")) {
                extent.setSystemInfo(keyStr.substring("systeminfo.".length()), value.toString());
            }
        });
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        try {
            String browser = ConfigReader.get("browser");
            if (browser != null) extent.setSystemInfo("Browser", browser);
        } catch (Exception ignored) {}
    }

    public static void createTest(String testName) {
        initReports();
        test.set(extent.createTest(testName));
    }

    public static void createTest(String testName, String description) {
        initReports();
        test.set(extent.createTest(testName, description));
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void logInfo(String msg) { if (getTest() != null) getTest().info(msg); }
    public static void logPass(String msg) { if (getTest() != null) getTest().pass(msg); }
    public static void logFail(String msg) { if (getTest() != null) getTest().fail(msg); }
    public static void logWarning(String msg) { if (getTest() != null) getTest().warning(msg); }

    /** ✅ FIXED: Save screenshot and embed with Base64 fallback */
    public static void captureAndAttachScreenshot(WebDriver driver, String title) {
        if (getTest() != null && driver != null) {
            try {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String timestamp = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
                String filename = title.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
                String filepath = SCREENSHOT_DIR + filename;

                // Save screenshot to disk
                Files.write(Paths.get(filepath), screenshotBytes);
                System.out.println("📸 Screenshot saved: " + filepath);

                // ✅ PRIMARY FIX: Use Base64 encoding for embedded screenshots
                // This ensures screenshots always work regardless of file paths
                String base64 = Base64.getEncoder().encodeToString(screenshotBytes);
                getTest().info(title,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());

            } catch (Exception e) {
                System.err.println("⚠️ Failed to attach screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void attachFailureScreenshot(WebDriver driver, String msg) {
        logFail(msg);
        captureAndAttachScreenshot(driver, "Failure Screenshot");
    }
    
    public static void attachBase64Screenshot(String base64, String title) {
        try {
            getTest().addScreenCaptureFromBase64String(base64, title);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to attach Base64 screenshot: " + e.getMessage());
        }
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            System.out.println("✅ Extent Report flushed successfully");
        }
    }

    public static void assignCategory(String... cat) { if (getTest() != null) getTest().assignCategory(cat); }
    public static void assignAuthor(String... authors) { if (getTest() != null) getTest().assignAuthor(authors); }
    public static void assignDevice(String... dev) { if (getTest() != null) getTest().assignDevice(dev); }

    public static void removeTest() { test.remove(); }
}