package config;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Configuration reader utility for loading properties from config.properties file
 * and test data from JSON files
 */
public class ConfigReader {
    
    private static Properties properties;
    private static JSONObject testData;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/testdata.json";
    
    static {
        loadProperties();
        loadTestData();
    }
    
    // ==================== PROPERTIES FILE METHODS ====================
    
    /**
     * Load properties from config file
     */
    private static void loadProperties() {
        properties = new Properties();
        
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
            System.out.println("✅ Configuration loaded successfully from: " + CONFIG_FILE_PATH);
        } catch (IOException e) {
            System.err.println("❌ Failed to load config.properties from: " + CONFIG_FILE_PATH);
            System.err.println("Error: " + e.getMessage());
            
            // Try loading from classpath as fallback
            try (InputStream input = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config.properties")) {
                
                if (input != null) {
                    properties.load(input);
                    System.out.println("✅ Configuration loaded from classpath");
                } else {
                    throw new RuntimeException("config.properties not found in classpath");
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not load configuration file", ex);
            }
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value or null if not found
     */
    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("⚠️  Property not found: " + key);
        }
        return value;
    }
    
    /**
     * Get property value with default fallback
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get property as integer
     * @param key Property key
     * @return Integer value
     */
    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Property '" + key + "' is not a valid integer: " + value);
        }
    }
    
    /**
     * Get property as integer with default
     * @param key Property key
     * @param defaultValue Default value
     * @return Integer value or default
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get property as boolean
     * @param key Property key
     * @return Boolean value
     */
    public static boolean getBoolean(String key) {
        String value = get(key);
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get property as boolean with default
     * @param key Property key
     * @param defaultValue Default value
     * @return Boolean value or default
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Check if property exists
     * @param key Property key
     * @return true if property exists
     */
    public static boolean has(String key) {
        return properties.containsKey(key);
    }
    
    // ==================== JSON TEST DATA METHODS ====================
    
    /**
     * Load test data from JSON file
     */
    private static void loadTestData() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(TEST_DATA_PATH)));
            testData = new JSONObject(content);
            System.out.println("✅ Test data loaded successfully from: " + TEST_DATA_PATH);
        } catch (IOException e) {
            System.err.println("⚠️  Failed to load test data from: " + TEST_DATA_PATH);
            System.err.println("⚠️  Test data methods will not be available. Error: " + e.getMessage());
            testData = new JSONObject(); // Initialize empty JSON to avoid null pointer
        }
    }
    
    /**
     * Get JSON object by path (e.g., "loginCredentials")
     */
    public static JSONObject getJsonObject(String path) {
        if (testData == null || testData.isEmpty()) {
            throw new RuntimeException("Test data not loaded. Check JSON file path.");
        }
        return testData.getJSONObject(path);
    }
    
    /**
     * Get JSON array by path
     */
    public static JSONArray getJsonArray(String path) {
        if (testData == null || testData.isEmpty()) {
            throw new RuntimeException("Test data not loaded. Check JSON file path.");
        }
        return testData.getJSONArray(path);
    }
    
    /**
     * Get string value from JSON by nested path (e.g., "loginCredentials.username")
     */
    public static String getJsonString(String path) {
        if (testData == null || testData.isEmpty()) {
            throw new RuntimeException("Test data not loaded. Check JSON file path.");
        }
        
        String[] keys = path.split("\\.");
        Object current = testData;
        
        for (String key : keys) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(key);
            } else {
                throw new RuntimeException("Invalid JSON path: " + path);
            }
        }
        
        return current.toString();
    }
    
    // ==================== CONVENIENCE METHODS FOR TEST DATA ====================
    
    /**
     * Get login username from JSON
     */
    public static String getUsername() {
        return getJsonString("loginCredentials.username");
    }
    
    /**
     * Get login password from JSON
     */
    public static String getPassword() {
        return getJsonString("loginCredentials.password");
    }
    
    /**
     * Get URL from JSON by key
     */
    public static String getTestUrl(String urlKey) {
        return getJsonString("urls." + urlKey);
    }
    
    /**
     * Get dashboard tile data from JSON
     */
    public static JSONObject getDashboardTile(String tileName) {
        return getJsonObject("dashboardTiles").getJSONObject(tileName);
    }
    
    /**
     * Get course section data from JSON
     */
    public static JSONObject getCourseData(String key) {
        return getJsonObject("coursesSection").getJSONObject(key);
    }
    
    /**
     * Get virtual labs data from JSON
     */
    public static JSONObject getVirtualLabsData() {
        return getJsonObject("virtualLabs");
    }
    
    /**
     * Get viva/project data from JSON
     */
    public static JSONObject getVivaProjectData() {
        return getJsonObject("vivaProject");
    }
    
    /**
     * Get expected text from JSON
     */
    public static String getExpectedText(String key) {
        return getJsonString("expectedTexts." + key);
    }
    
    /**
     * Get validation message from JSON
     */
    public static String getValidationMessage(String key) {
        return getJsonString("validationMessages." + key);
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Reload both properties and test data
     */
    public static void reload() {
        loadProperties();
        loadTestData();
    }
    
    /**
     * Reload only properties file
     */
    public static void reloadProperties() {
        loadProperties();
    }
    
    /**
     * Reload only test data JSON
     */
    public static void reloadTestData() {
        loadTestData();
    }
    
    /**
     * Print all loaded properties (for debugging)
     */
    public static void printAllProperties() {
        System.out.println("\n📋 All Configuration Properties:");
        System.out.println("=".repeat(60));
        properties.forEach((key, value) -> {
            // Mask sensitive values
            String displayValue = key.toString().toLowerCase().contains("password") || 
                                 key.toString().toLowerCase().contains("apikey") ||
                                 key.toString().toLowerCase().contains("token")
                    ? "********" 
                    : value.toString();
            System.out.println(key + " = " + displayValue);
        });
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Print all test data from JSON (for debugging)
     */
    public static void printAllTestData() {
        System.out.println("\n📋 All Test Data:");
        System.out.println("=".repeat(60));
        if (testData != null && !testData.isEmpty()) {
            System.out.println(testData.toString(2)); // Pretty print with indentation
        } else {
            System.out.println("No test data loaded");
        }
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Print all configuration (both properties and test data)
     */
    public static void printAll() {
        printAllProperties();
        printAllTestData();
    }
}