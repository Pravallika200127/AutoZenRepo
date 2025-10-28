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
        
        // ✅ Check if headless mode is enabled (from system property or config)
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", 
                            ConfigReader.get("headless", "false")));
        
        WebDriver webDriver;
        System.out.println("🌐 Initializing " + browserName + " browser..." + 
                          (isHeadless ? " (Headless Mode)" : ""));
        
        try {
            switch (browserName) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    
                    // ✅ Add headless mode configuration for CI/CD
                    if (isHeadless) {
                        chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--no-sandbox");
                        chromeOptions.addArguments("--disable-dev-shm-usage");
                        chromeOptions.addArguments("--disable-gpu");
                        chromeOptions.addArguments("--window-size=1920,1080");
                        chromeOptions.addArguments("--disable-extensions");
                        chromeOptions.addArguments("--disable-software-rasterizer");
                    } else {
                        chromeOptions.addArguments("--start-maximized");
                    }
                    
                    // Common options for both modes
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    
                    webDriver = new ChromeDriver(chromeOptions);
                    break;
                    
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    
                    // ✅ Add headless mode for Firefox
                    if (isHeadless) {
                        firefoxOptions.addArguments("--headless");
                        firefoxOptions.addArguments("--width=1920");
                        firefoxOptions.addArguments("--height=1080");
                    } else {
                        firefoxOptions.addArguments("--width=1920");
                        firefoxOptions.addArguments("--height=1080");
                        // ⚠️ Only set binary path if not in CI environment
                        String firefoxPath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                        if (new java.io.File(firefoxPath).exists()) {
                            firefoxOptions.setBinary(firefoxPath);
                        }
                    }
                    
                    webDriver = new FirefoxDriver(firefoxOptions);
                    break;
                    
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    
                    // ✅ Add headless mode for Edge
                    if (isHeadless) {
                        edgeOptions.addArguments("--headless=new");
                        edgeOptions.addArguments("--no-sandbox");
                        edgeOptions.addArguments("--disable-dev-shm-usage");
                        edgeOptions.addArguments("--window-size=1920,1080");
                    } else {
                        edgeOptions.addArguments("--start-maximized");
                    }
                    
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
        
        System.out.println("✅ Browser initialized successfully: " + browserName + 
                          (isHeadless ? " (Headless)" : ""));
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
    
    public static String getBrowserName() {
        return browserName != null ? browserName : "unknown";
    }
}