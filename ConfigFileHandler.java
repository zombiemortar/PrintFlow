import java.util.*;
import java.util.regex.Pattern;

/**
 * Handles file operations for SystemConfig settings.
 * Provides methods to save and load system configuration to/from files.
 * Supports key-value format with comments and validation.
 */
public class ConfigFileHandler {
    
    private static final String CONFIG_FILENAME = "system_config.txt";
    private static final String CONFIG_COMMENT_PREFIX = "#";
    private static final String CONFIG_KEY_VALUE_SEPARATOR = "=";
    private static final Pattern VALID_KEY_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    
    /**
     * Saves current SystemConfig settings to a file.
     * @return true if save was successful
     */
    public static boolean saveConfiguration() {
        StringBuilder data = new StringBuilder();
        data.append("# System Configuration File\n");
        data.append("# Format: key=value (one per line)\n");
        data.append("# Lines starting with # are comments\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        // Pricing constants section
        data.append("# PRICING CONSTANTS\n");
        data.append("electricity_cost_per_hour=").append(SystemConfig.getElectricityCostPerHour()).append("\n");
        data.append("machine_time_cost_per_hour=").append(SystemConfig.getMachineTimeCostPerHour()).append("\n");
        data.append("base_setup_cost=").append(SystemConfig.getBaseSetupCost()).append("\n\n");
        
        // Tax and currency section
        data.append("# TAX & CURRENCY\n");
        data.append("tax_rate=").append(SystemConfig.getTaxRate()).append("\n");
        data.append("currency=").append(SystemConfig.getCurrency()).append("\n\n");
        
        // Order limits section
        data.append("# ORDER LIMITS\n");
        data.append("max_order_quantity=").append(SystemConfig.getMaxOrderQuantity()).append("\n");
        data.append("max_order_value=").append(SystemConfig.getMaxOrderValue()).append("\n\n");
        
        // Rush order settings section
        data.append("# RUSH ORDER SETTINGS\n");
        data.append("allow_rush_orders=").append(SystemConfig.isAllowRushOrders()).append("\n");
        data.append("rush_order_surcharge=").append(SystemConfig.getRushOrderSurcharge()).append("\n");
        
        return DataFileManager.writeToFile(CONFIG_FILENAME, data.toString());
    }
    
    /**
     * Loads SystemConfig settings from file.
     * @return true if load was successful
     */
    public static boolean loadConfiguration() {
        String configData = DataFileManager.readFromFile(CONFIG_FILENAME);
        if (configData == null) {
            System.out.println("Configuration file not found. Using default settings.");
            return false;
        }
        
        try {
            Map<String, String> configMap = parseConfigurationFile(configData);
            return applyConfiguration(configMap);
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Parses configuration file content into key-value pairs.
     * @param configData The raw configuration file content
     * @return Map of configuration keys to values
     */
    private static Map<String, String> parseConfigurationFile(String configData) {
        Map<String, String> configMap = new HashMap<>();
        String[] lines = configData.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith(CONFIG_COMMENT_PREFIX)) {
                continue;
            }
            
            // Parse key=value pairs
            if (line.contains(CONFIG_KEY_VALUE_SEPARATOR)) {
                String[] parts = line.split(CONFIG_KEY_VALUE_SEPARATOR, 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    if (isValidConfigKey(key)) {
                        configMap.put(key, value);
                    } else {
                        System.err.println("Invalid configuration key: " + key);
                    }
                }
            }
        }
        
        return configMap;
    }
    
    /**
     * Applies parsed configuration values to SystemConfig.
     * @param configMap Map of configuration keys to values
     * @return true if all values were applied successfully
     */
    private static boolean applyConfiguration(Map<String, String> configMap) {
        boolean allSuccess = true;
        
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            try {
                if (!applyConfigValue(key, value)) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                System.err.println("Error applying configuration " + key + "=" + value + ": " + e.getMessage());
                allSuccess = false;
            }
        }
        
        return allSuccess;
    }
    
    /**
     * Applies a single configuration value to SystemConfig.
     * @param key The configuration key
     * @param value The configuration value
     * @return true if the value was applied successfully
     */
    private static boolean applyConfigValue(String key, String value) {
        switch (key) {
            case "electricity_cost_per_hour":
                try {
                    double cost = Double.parseDouble(value);
                    SystemConfig.setElectricityCostPerHour(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid electricity cost value: " + value);
                    return false;
                }
                
            case "machine_time_cost_per_hour":
                try {
                    double cost = Double.parseDouble(value);
                    SystemConfig.setMachineTimeCostPerHour(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid machine time cost value: " + value);
                    return false;
                }
                
            case "base_setup_cost":
                try {
                    double cost = Double.parseDouble(value);
                    SystemConfig.setBaseSetupCost(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid base setup cost value: " + value);
                    return false;
                }
                
            case "tax_rate":
                try {
                    double rate = Double.parseDouble(value);
                    SystemConfig.setTaxRate(rate);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid tax rate value: " + value);
                    return false;
                }
                
            case "currency":
                SystemConfig.setCurrency(value);
                return true;
                
            case "max_order_quantity":
                try {
                    int quantity = Integer.parseInt(value);
                    SystemConfig.setMaxOrderQuantity(quantity);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max order quantity value: " + value);
                    return false;
                }
                
            case "max_order_value":
                try {
                    double maxValue = Double.parseDouble(value);
                    SystemConfig.setMaxOrderValue(maxValue);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max order value: " + value);
                    return false;
                }
                
            case "allow_rush_orders":
                try {
                    boolean allow = Boolean.parseBoolean(value);
                    SystemConfig.setAllowRushOrders(allow);
                    return true;
                } catch (Exception e) {
                    System.err.println("Invalid allow rush orders value: " + value);
                    return false;
                }
                
            case "rush_order_surcharge":
                try {
                    double surcharge = Double.parseDouble(value);
                    SystemConfig.setRushOrderSurcharge(surcharge);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid rush order surcharge value: " + value);
                    return false;
                }
                
            default:
                System.err.println("Unknown configuration key: " + key);
                return false;
        }
    }
    
    /**
     * Validates that a configuration key is properly formatted.
     * @param key The key to validate
     * @return true if the key is valid
     */
    private static boolean isValidConfigKey(String key) {
        return VALID_KEY_PATTERN.matcher(key).matches();
    }
    
    /**
     * Tests if a configuration value can be parsed without actually applying it.
     * @param key The configuration key
     * @param value The configuration value
     * @return true if the value can be parsed
     */
    private static boolean canParseConfigValue(String key, String value) {
        try {
            switch (key) {
                case "electricity_cost_per_hour":
                case "machine_time_cost_per_hour":
                case "base_setup_cost":
                case "tax_rate":
                case "max_order_value":
                case "rush_order_surcharge":
                    Double.parseDouble(value);
                    return true;
                    
                case "max_order_quantity":
                    Integer.parseInt(value);
                    return true;
                    
                case "currency":
                    return value != null && !value.trim().isEmpty();
                    
                case "allow_rush_orders":
                    Boolean.parseBoolean(value);
                    return true;
                    
                default:
                    return true; // Unknown keys are considered valid
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Creates a default configuration file with current SystemConfig values.
     * @return true if the default file was created successfully
     */
    public static boolean createDefaultConfiguration() {
        return saveConfiguration();
    }
    
    /**
     * Validates the current configuration file format.
     * @return true if the configuration file is valid
     */
    public static boolean validateConfigurationFile() {
        String configData = DataFileManager.readFromFile(CONFIG_FILENAME);
        if (configData == null) {
            return false;
        }
        
        try {
            Map<String, String> configMap = parseConfigurationFile(configData);
            
            // Check for required configuration keys
            String[] requiredKeys = {
                "electricity_cost_per_hour",
                "machine_time_cost_per_hour", 
                "base_setup_cost",
                "tax_rate",
                "currency",
                "max_order_quantity",
                "max_order_value",
                "allow_rush_orders",
                "rush_order_surcharge"
            };
            
            for (String requiredKey : requiredKeys) {
                if (!configMap.containsKey(requiredKey)) {
                    System.err.println("Missing required configuration key: " + requiredKey);
                    return false;
                }
            }
            
            // Validate that all values can be parsed correctly
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Test if the value can be parsed by trying to apply it
                if (!canParseConfigValue(key, value)) {
                    System.err.println("Invalid value for configuration key " + key + ": " + value);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Configuration file validation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the configuration file path.
     * @return The full path to the configuration file
     */
    public static String getConfigFilePath() {
        return DataFileManager.getFilePath(CONFIG_FILENAME);
    }
    
    /**
     * Checks if the configuration file exists.
     * @return true if the configuration file exists
     */
    public static boolean configFileExists() {
        return DataFileManager.fileExists(CONFIG_FILENAME);
    }
    
    /**
     * Creates a backup of the current configuration file.
     * @return true if backup was successful
     */
    public static boolean backupConfiguration() {
        return DataFileManager.createBackup(CONFIG_FILENAME);
    }
    
    /**
     * Restores configuration from a backup file.
     * @param backupFilename The backup filename to restore from
     * @return true if restore was successful
     */
    public static boolean restoreConfiguration(String backupFilename) {
        return DataFileManager.restoreFromBackup(backupFilename, CONFIG_FILENAME);
    }
    
    /**
     * Restores configuration from the most recent backup.
     * @return true if restore was successful
     */
    public static boolean restoreConfigurationFromLatestBackup() {
        return DataFileManager.restoreFromLatestBackup(CONFIG_FILENAME);
    }
    
    /**
     * Lists all available configuration backups.
     * @return Array of backup filenames for configuration
     */
    public static String[] listConfigurationBackups() {
        return DataFileManager.listBackupsForFile(CONFIG_FILENAME);
    }
    
    /**
     * Gets a summary of the current configuration file status.
     * @return A formatted string describing the configuration file status
     */
    public static String getConfigurationFileStatus() {
        StringBuilder status = new StringBuilder();
        status.append("CONFIGURATION FILE STATUS\n");
        status.append("========================\n");
        
        boolean exists = configFileExists();
        status.append("File exists: ").append(exists ? "Yes" : "No").append("\n");
        
        if (exists) {
            status.append("File path: ").append(getConfigFilePath()).append("\n");
            boolean isValid = validateConfigurationFile();
            status.append("File valid: ").append(isValid ? "Yes" : "No").append("\n");
        }
        
        return status.toString();
    }
}
