package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DriverFactory {
    private static WebDriver driver;
    private static Properties properties;

    private static final String CONFIG_FILE_PATH = "/config.properties"; // if in src/main/resources or src/test/resources

    private static void loadProperties() {
        properties = new Properties();
        String resourcePath = "/config.properties"; // root of classpath
        System.out.println("Resource URL: " + DriverFactory.class.getResource(resourcePath));
        try (InputStream input = DriverFactory.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new RuntimeException("Unable to find resource: " + resourcePath);
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration properties");
        }
    }

    public static void initDriver() {
        if (driver == null) {
            loadProperties();
            String browserName = properties.getProperty("browser", "chrome").toLowerCase();

            switch (browserName) {
                case "firefox":
                    // Ensure geckodriver is in PATH
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    // Ensure msedgedriver is in PATH
                    driver = new EdgeDriver();
                    break;
                case "safari":
                    // SafariDriver comes pre-installed on macOS, ensure Safari's "Allow Remote Automation" is enabled in Safari's Develop menu
                    driver = new SafariDriver();
                    break;
                case "chrome":
                default:
                    // Ensure chromedriver is in PATH
                    driver = new ChromeDriver();
                    break;
            }
            driver.manage().window().maximize();
        }
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            initDriver();
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}