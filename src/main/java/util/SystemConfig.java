package util;

import java.util.*;
import java.util.regex.Pattern;
import service.OrderManager;

/**
 * Manages system configuration including pricing constants, tax rates, and other settings.
 * Provides centralized access to system parameters that can be modified by administrators.
 * Includes file operations for configuration persistence.
 */
public class SystemConfig {
    // Pricing constants
    private static double electricityCostPerHour = 0.15; // $0.15 per kWh
    private static double machineTimeCostPerHour = 2.50; // $2.50 per hour
    private static double baseSetupCost = 5.00; // $5.00 base setup cost
    
    // Tax rates
    private static double taxRate = 0.08; // 8% tax rate
    private static String currency = "USD"; // Default currency
    
    // System settings
    private static int maxOrderQuantity = 100; // Maximum items per order
    private static double maxOrderValue = 1000.00; // Maximum order value
    private static boolean allowRushOrders = true; // Whether rush orders are allowed
    private static double rushOrderSurcharge = 0.25; // 25% surcharge for rush orders
    
    // File operation constants
    private static final String CONFIG_FILENAME = "system_config.txt";
    private static final String CONFIG_COMMENT_PREFIX = "#";
    private static final String CONFIG_KEY_VALUE_SEPARATOR = "=";
    private static final Pattern VALID_KEY_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods and variables.
     */
    private SystemConfig() {
        // Utility class - no instances needed
    }
    
    // Pricing constant getters and setters
    public static double getElectricityCostPerHour() {
        return electricityCostPerHour;
    }
    
    public static void setElectricityCostPerHour(double cost) {
        if (cost >= 0) {
            electricityCostPerHour = cost;
        }
    }
    
    public static double getMachineTimeCostPerHour() {
        return machineTimeCostPerHour;
    }
    
    public static void setMachineTimeCostPerHour(double cost) {
        if (cost >= 0) {
            machineTimeCostPerHour = cost;
        }
    }
    
    public static double getBaseSetupCost() {
        return baseSetupCost;
    }
    
    public static void setBaseSetupCost(double cost) {
        if (cost >= 0) {
            baseSetupCost = cost;
        }
    }
    
    // Tax rate getters and setters
    public static double getTaxRate() {
        return taxRate;
    }
    
    public static void setTaxRate(double rate) {
        if (rate >= 0 && rate <= 1) {
            taxRate = rate;
        }
    }
    
    public static String getCurrency() {
        return currency;
    }
    
    public static void setCurrency(String newCurrency) {
        if (newCurrency != null && !newCurrency.trim().isEmpty()) {
            currency = newCurrency.trim().toUpperCase();
        }
    }
    
    // System setting getters and setters
    public static int getMaxOrderQuantity() {
        return maxOrderQuantity;
    }
    
    public static void setMaxOrderQuantity(int maxQuantity) {
        if (maxQuantity > 0) {
            maxOrderQuantity = maxQuantity;
        }
    }
    
    public static double getMaxOrderValue() {
        return maxOrderValue;
    }
    
    public static void setMaxOrderValue(double maxValue) {
        if (maxValue > 0) {
            maxOrderValue = maxValue;
        }
    }
    
    public static boolean isAllowRushOrders() {
        return allowRushOrders;
    }
    
    public static void setAllowRushOrders(boolean allow) {
        allowRushOrders = allow;
    }
    
    public static double getRushOrderSurcharge() {
        return rushOrderSurcharge;
    }
    
    public static void setRushOrderSurcharge(double surcharge) {
        if (surcharge >= 0) {
            rushOrderSurcharge = surcharge;
        }
    }
    
    /**
     * Gets a summary of all current system configuration.
     * @return A formatted string containing all configuration values
     */
    public static String getConfigurationSummary() {
        return String.format("""
                SYSTEM CONFIGURATION SUMMARY
                =============================
                
                PRICING CONSTANTS:
                - Electricity Cost: $%.2f per hour
                - Machine Time Cost: $%.2f per hour
                - Base Setup Cost: $%.2f
                
                TAX & CURRENCY:
                - Tax Rate: %.1f%%
                - Currency: %s
                
                ORDER LIMITS:
                - Max Order Quantity: %d items
                - Max Order Value: $%.2f
                
                RUSH ORDER SETTINGS:
                - Rush Orders Allowed: %s
                - Rush Order Surcharge: %.1f%%
                """,
                electricityCostPerHour,
                machineTimeCostPerHour,
                baseSetupCost,
                taxRate * 100,
                currency,
                maxOrderQuantity,
                maxOrderValue,
                allowRushOrders ? "Yes" : "No",
                rushOrderSurcharge * 100
        );
    }
    
    /**
     * Resets all configuration values to their defaults.
     */
    public static void resetToDefaults() {
        electricityCostPerHour = 0.15;
        machineTimeCostPerHour = 2.50;
        baseSetupCost = 5.00;
        taxRate = 0.08;
        currency = "USD";
        maxOrderQuantity = 100;
        maxOrderValue = 1000.00;
        allowRushOrders = true;
        rushOrderSurcharge = 0.25;
        // Also reset runtime state in managers to ensure clean test isolation
        try {
            OrderManager.clearAllOrders();
        } catch (Throwable ignored) {
        }
    }
    
    /**
     * Loads configuration from external file.
     * @return true if configuration was loaded successfully
     */
    public static boolean loadFromFile() {
        String configData = FileManager.readFromFile(CONFIG_FILENAME);
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
     * Saves current configuration to external file.
     * @return true if configuration was saved successfully
     */
    public static boolean saveToFile() {
        StringBuilder data = new StringBuilder();
        data.append("# System Configuration File\n");
        data.append("# Format: key=value (one per line)\n");
        data.append("# Lines starting with # are comments\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        // Pricing constants section
        data.append("# PRICING CONSTANTS\n");
        data.append("electricity_cost_per_hour=").append(electricityCostPerHour).append("\n");
        data.append("machine_time_cost_per_hour=").append(machineTimeCostPerHour).append("\n");
        data.append("base_setup_cost=").append(baseSetupCost).append("\n\n");
        
        // Tax and currency section
        data.append("# TAX & CURRENCY\n");
        data.append("tax_rate=").append(taxRate).append("\n");
        data.append("currency=").append(currency).append("\n\n");
        
        // Order limits section
        data.append("# ORDER LIMITS\n");
        data.append("max_order_quantity=").append(maxOrderQuantity).append("\n");
        data.append("max_order_value=").append(maxOrderValue).append("\n\n");
        
        // Rush order settings section
        data.append("# RUSH ORDER SETTINGS\n");
        data.append("allow_rush_orders=").append(allowRushOrders).append("\n");
        data.append("rush_order_surcharge=").append(rushOrderSurcharge).append("\n");
        
        return FileManager.writeToFile(CONFIG_FILENAME, data.toString());
    }
    
    /**
     * Creates a default configuration file with current settings.
     * @return true if the default file was created successfully
     */
    public static boolean createDefaultConfigFile() {
        return saveToFile();
    }
    
    /**
     * Validates the current configuration file.
     * @return true if the configuration file is valid
     */
    public static boolean validateConfigFile() {
        String configData = FileManager.readFromFile(CONFIG_FILENAME);
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
     * Gets the status of the configuration file system.
     * @return A formatted string describing the configuration file status
     */
    public static String getConfigFileStatus() {
        StringBuilder status = new StringBuilder();
        status.append("CONFIGURATION FILE STATUS\n");
        status.append("========================\n");
        
        boolean exists = configFileExists();
        status.append("File exists: ").append(exists ? "Yes" : "No").append("\n");
        
        if (exists) {
            status.append("File path: ").append(FileManager.getFilePath(CONFIG_FILENAME)).append("\n");
            boolean isValid = validateConfigFile();
            status.append("File valid: ").append(isValid ? "Yes" : "No").append("\n");
        }
        
        return status.toString();
    }
    
    /**
     * Checks if the configuration file exists.
     * @return true if the configuration file exists
     */
    public static boolean configFileExists() {
        return FileManager.fileExists(CONFIG_FILENAME);
    }
    
    /**
     * Creates a backup of the current configuration file.
     * @return true if backup was successful
     */
    public static boolean backupConfigFile() {
        return FileManager.createBackup(CONFIG_FILENAME);
    }
    
    /**
     * Restores configuration from a backup file.
     * @param backupFilename The backup filename to restore from
     * @return true if restore was successful
     */
    public static boolean restoreConfigFile(String backupFilename) {
        boolean restored = FileManager.restoreFromBackup(backupFilename, CONFIG_FILENAME);
        if (restored) {
            // Reload configuration after restore
            return loadFromFile();
        }
        return false;
    }
    
    /**
     * Restores configuration from the most recent backup.
     * @return true if restore was successful
     */
    public static boolean restoreConfigFileFromLatestBackup() {
        boolean restored = FileManager.restoreFromLatestBackup(CONFIG_FILENAME);
        if (restored) {
            // Reload configuration after restore
            return loadFromFile();
        }
        return false;
    }
    
    /**
     * Lists all available configuration backups.
     * @return Array of backup filenames for configuration
     */
    public static String[] listConfigBackups() {
        return FileManager.listBackupsForFile(CONFIG_FILENAME);
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
                    setElectricityCostPerHour(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid electricity cost value: " + value);
                    return false;
                }
                
            case "machine_time_cost_per_hour":
                try {
                    double cost = Double.parseDouble(value);
                    setMachineTimeCostPerHour(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid machine time cost value: " + value);
                    return false;
                }
                
            case "base_setup_cost":
                try {
                    double cost = Double.parseDouble(value);
                    setBaseSetupCost(cost);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid base setup cost value: " + value);
                    return false;
                }
                
            case "tax_rate":
                try {
                    double rate = Double.parseDouble(value);
                    setTaxRate(rate);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid tax rate value: " + value);
                    return false;
                }
                
            case "currency":
                setCurrency(value);
                return true;
                
            case "max_order_quantity":
                try {
                    int quantity = Integer.parseInt(value);
                    setMaxOrderQuantity(quantity);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max order quantity value: " + value);
                    return false;
                }
                
            case "max_order_value":
                try {
                    double maxValue = Double.parseDouble(value);
                    setMaxOrderValue(maxValue);
                    return true;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid max order value: " + value);
                    return false;
                }
                
            case "allow_rush_orders":
                try {
                    boolean allow = Boolean.parseBoolean(value);
                    setAllowRushOrders(allow);
                    return true;
                } catch (Exception e) {
                    System.err.println("Invalid allow rush orders value: " + value);
                    return false;
                }
                
            case "rush_order_surcharge":
                try {
                    double surcharge = Double.parseDouble(value);
                    setRushOrderSurcharge(surcharge);
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
}

