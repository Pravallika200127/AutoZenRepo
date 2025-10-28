package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import config.ConfigReader;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static String browserName;

    public static void initDriver() {
        if (driver.get() == null) {
            driver.set(createDriver());
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initDriver();
        }
        return driver.get();
    }

    private static WebDriver createDriver() {
        browserName = ConfigReader.get("browser", "chrome").toLowerCase();
        WebDriver webDriver;

        System.out.println("🌐 Initializing " + browserName + " browser...");

        try {
            switch (browserName) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized", "--disable-notifications", "--disable-popup-blocking");
                    webDriver = new ChromeDriver(chromeOptions);
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--width=1920", "--height=1080");
                    firefoxOptions.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
                    webDriver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--start-maximized");
                    webDriver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException("❌ Browser not supported: " + browserName);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to initialize WebDriver for: " + browserName);
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver for: " + browserName, e);
        }

        System.out.println("✅ Browser initialized successfully: " + browserName);
        return webDriver;
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                System.out.println("🔒 Quitting browser...");
                driver.get().quit();
                driver.remove();
                System.out.println("✅ Browser closed successfully");
            } catch (Exception e) {
                System.err.println("⚠️ Error closing browser: " + e.getMessage());
            }
        }
    }

    /** ✅ Added method */
    public static String getBrowserName() {
        return browserName != null ? browserName : "unknown";
    }
}
