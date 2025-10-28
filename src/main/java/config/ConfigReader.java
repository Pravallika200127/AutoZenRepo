package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration reader utility for loading properties from config.properties file
 */
public class ConfigReader {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    
    static {
        loadProperties();
    }
    
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
    
    /**
     * Reload properties from file
     */
    public static void reload() {
        loadProperties();
    }
    
    /**
     * Print all loaded properties (for debugging)
     */
    public static void printAll() {
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
}