import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Comprehensive JUnit test cases for the ConfigFileHandler class.
 * Tests configuration file operations, validation, and integration with SystemConfig.
 */
public class ConfigFileHandlerTest {
    
    private static final String TEST_CONFIG_FILENAME = "test_config.txt";
    private static final String TEST_DATA_DIR = "data";
    
    @BeforeEach
    void setUp() {
        // Reset SystemConfig to defaults before each test
        SystemConfig.resetToDefaults();
        
        // Ensure test data directory exists
        DataFileManager.ensureDataDirectory();
    }
    
    @AfterEach
    void tearDown() {
        // Reset SystemConfig to defaults after each test
        SystemConfig.resetToDefaults();
        
        // Clean up test files
        File testFile = new File(TEST_DATA_DIR + File.separator + TEST_CONFIG_FILENAME);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    // Configuration File Creation Tests
    @Test
    void testCreateDefaultConfiguration() {
        assertTrue(ConfigFileHandler.createDefaultConfiguration());
        assertTrue(ConfigFileHandler.configFileExists());
    }
    
    @Test
    void testSaveConfiguration() {
        // Modify some settings
        SystemConfig.setElectricityCostPerHour(0.20);
        SystemConfig.setTaxRate(0.10);
        SystemConfig.setCurrency("EUR");
        
        // Save configuration
        assertTrue(ConfigFileHandler.saveConfiguration());
        assertTrue(ConfigFileHandler.configFileExists());
    }
    
    @Test
    void testSaveConfigurationWithAllSettings() {
        // Modify all settings
        SystemConfig.setElectricityCostPerHour(0.25);
        SystemConfig.setMachineTimeCostPerHour(3.00);
        SystemConfig.setBaseSetupCost(7.50);
        SystemConfig.setTaxRate(0.12);
        SystemConfig.setCurrency("GBP");
        SystemConfig.setMaxOrderQuantity(50);
        SystemConfig.setMaxOrderValue(1500.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.30);
        
        // Save configuration
        assertTrue(ConfigFileHandler.saveConfiguration());
        assertTrue(ConfigFileHandler.configFileExists());
    }
    
    // Configuration File Loading Tests
    @Test
    void testLoadConfigurationFromNonExistentFile() {
        // Delete config file if it exists
        File configFile = new File(ConfigFileHandler.getConfigFilePath());
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // Try to load from non-existent file
        assertFalse(ConfigFileHandler.loadConfiguration());
    }
    
    @Test
    void testLoadConfigurationFromValidFile() {
        // Create a valid configuration file
        assertTrue(ConfigFileHandler.createDefaultConfiguration());
        
        // Modify settings in memory
        SystemConfig.setElectricityCostPerHour(0.30);
        SystemConfig.setTaxRate(0.15);
        
        // Load configuration (should reset to file values)
        assertTrue(ConfigFileHandler.loadConfiguration());
        
        // Verify settings were loaded from file
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
        assertEquals(0.08, SystemConfig.getTaxRate());
    }
    
    @Test
    void testLoadConfigurationWithCustomValues() {
        // Create custom configuration content
        String customConfig = """
            # Custom Configuration
            electricity_cost_per_hour=0.20
            machine_time_cost_per_hour=3.50
            base_setup_cost=8.00
            tax_rate=0.10
            currency=EUR
            max_order_quantity=75
            max_order_value=2000.00
            allow_rush_orders=false
            rush_order_surcharge=0.35
            """;
        
        // Write custom config to file
        assertTrue(DataFileManager.writeToFile("system_config.txt", customConfig));
        
        // Load configuration
        assertTrue(ConfigFileHandler.loadConfiguration());
        
        // Verify all values were loaded correctly
        assertEquals(0.20, SystemConfig.getElectricityCostPerHour());
        assertEquals(3.50, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(8.00, SystemConfig.getBaseSetupCost());
        assertEquals(0.10, SystemConfig.getTaxRate());
        assertEquals("EUR", SystemConfig.getCurrency());
        assertEquals(75, SystemConfig.getMaxOrderQuantity());
        assertEquals(2000.00, SystemConfig.getMaxOrderValue());
        assertFalse(SystemConfig.isAllowRushOrders());
        assertEquals(0.35, SystemConfig.getRushOrderSurcharge());
    }
    
    // Configuration File Validation Tests
    @Test
    void testValidateConfigurationFileWithValidContent() {
        // Create valid configuration file
        assertTrue(ConfigFileHandler.createDefaultConfiguration());
        assertTrue(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testValidateConfigurationFileWithMissingKeys() {
        // Create configuration with missing keys
        String incompleteConfig = """
            electricity_cost_per_hour=0.15
            machine_time_cost_per_hour=2.50
            # Missing other required keys
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", incompleteConfig));
        assertFalse(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testValidateConfigurationFileWithInvalidValues() {
        // Create configuration with invalid values
        String invalidConfig = """
            electricity_cost_per_hour=invalid
            machine_time_cost_per_hour=2.50
            base_setup_cost=5.00
            tax_rate=0.08
            currency=USD
            max_order_quantity=100
            max_order_value=1000.00
            allow_rush_orders=true
            rush_order_surcharge=0.25
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", invalidConfig));
        assertFalse(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testValidateConfigurationFileWithComments() {
        // Create configuration with comments
        String configWithComments = """
            # System Configuration File
            # Pricing constants
            electricity_cost_per_hour=0.15
            machine_time_cost_per_hour=2.50
            base_setup_cost=5.00
            
            # Tax settings
            tax_rate=0.08
            currency=USD
            
            # Order limits
            max_order_quantity=100
            max_order_value=1000.00
            
            # Rush order settings
            allow_rush_orders=true
            rush_order_surcharge=0.25
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", configWithComments));
        assertTrue(ConfigFileHandler.validateConfigurationFile());
    }
    
    // Configuration File Status Tests
    @Test
    void testGetConfigurationFileStatusWithExistingFile() {
        assertTrue(ConfigFileHandler.createDefaultConfiguration());
        String status = ConfigFileHandler.getConfigurationFileStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("CONFIGURATION FILE STATUS"));
        assertTrue(status.contains("File exists: Yes"));
        assertTrue(status.contains("File valid: Yes"));
    }
    
    @Test
    void testGetConfigurationFileStatusWithNonExistentFile() {
        // Delete config file if it exists
        File configFile = new File(ConfigFileHandler.getConfigFilePath());
        if (configFile.exists()) {
            configFile.delete();
        }
        
        String status = ConfigFileHandler.getConfigurationFileStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("CONFIGURATION FILE STATUS"));
        assertTrue(status.contains("File exists: No"));
    }
    
    // Configuration File Backup Tests
    @Test
    void testBackupConfiguration() {
        assertTrue(ConfigFileHandler.createDefaultConfiguration());
        assertTrue(ConfigFileHandler.backupConfiguration());
        
        // Check that backup directory exists and has files
        File backupDir = new File("backups");
        assertTrue(backupDir.exists());
        assertTrue(backupDir.isDirectory());
    }
    
    // Edge Cases and Error Handling Tests
    @Test
    void testConfigurationWithEmptyFile() {
        assertTrue(DataFileManager.writeToFile("system_config.txt", ""));
        assertFalse(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testConfigurationWithOnlyComments() {
        String onlyComments = """
            # This is a comment
            # Another comment
            # No actual configuration
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", onlyComments));
        assertFalse(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testConfigurationWithInvalidKeyFormat() {
        String invalidKeys = """
            invalid-key=0.15
            valid_key=2.50
            invalid.key=5.00
            tax_rate=0.08
            currency=USD
            max_order_quantity=100
            max_order_value=1000.00
            allow_rush_orders=true
            rush_order_surcharge=0.25
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", invalidKeys));
        assertFalse(ConfigFileHandler.validateConfigurationFile());
    }
    
    @Test
    void testConfigurationWithDuplicateKeys() {
        String duplicateKeys = """
            electricity_cost_per_hour=0.15
            electricity_cost_per_hour=0.20
            machine_time_cost_per_hour=2.50
            base_setup_cost=5.00
            tax_rate=0.08
            currency=USD
            max_order_quantity=100
            max_order_value=1000.00
            allow_rush_orders=true
            rush_order_surcharge=0.25
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", duplicateKeys));
        assertTrue(ConfigFileHandler.validateConfigurationFile());
        
        // Load and verify last value is used
        assertTrue(ConfigFileHandler.loadConfiguration());
        assertEquals(0.20, SystemConfig.getElectricityCostPerHour());
    }
    
    @Test
    void testConfigurationWithWhitespaceHandling() {
        String configWithWhitespace = """
            electricity_cost_per_hour = 0.15
            machine_time_cost_per_hour= 2.50
            base_setup_cost =5.00
            tax_rate = 0.08
            currency = USD
            max_order_quantity = 100
            max_order_value = 1000.00
            allow_rush_orders = true
            rush_order_surcharge = 0.25
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", configWithWhitespace));
        assertTrue(ConfigFileHandler.validateConfigurationFile());
        
        assertTrue(ConfigFileHandler.loadConfiguration());
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.50, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(5.00, SystemConfig.getBaseSetupCost());
    }
    
    // Integration Tests with SystemConfig
    @Test
    void testSystemConfigLoadFromFile() {
        // Create custom configuration
        String customConfig = """
            electricity_cost_per_hour=0.25
            machine_time_cost_per_hour=4.00
            base_setup_cost=10.00
            tax_rate=0.12
            currency=CAD
            max_order_quantity=200
            max_order_value=5000.00
            allow_rush_orders=false
            rush_order_surcharge=0.40
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", customConfig));
        
        // Test SystemConfig.loadFromFile()
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify all values were loaded
        assertEquals(0.25, SystemConfig.getElectricityCostPerHour());
        assertEquals(4.00, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(10.00, SystemConfig.getBaseSetupCost());
        assertEquals(0.12, SystemConfig.getTaxRate());
        assertEquals("CAD", SystemConfig.getCurrency());
        assertEquals(200, SystemConfig.getMaxOrderQuantity());
        assertEquals(5000.00, SystemConfig.getMaxOrderValue());
        assertFalse(SystemConfig.isAllowRushOrders());
        assertEquals(0.40, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSystemConfigSaveToFile() {
        // Modify settings
        SystemConfig.setElectricityCostPerHour(0.30);
        SystemConfig.setTaxRate(0.15);
        SystemConfig.setCurrency("JPY");
        
        // Save to file
        assertTrue(SystemConfig.saveToFile());
        
        // Reset to defaults
        SystemConfig.resetToDefaults();
        
        // Load from file
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify settings were restored
        assertEquals(0.30, SystemConfig.getElectricityCostPerHour());
        assertEquals(0.15, SystemConfig.getTaxRate());
        assertEquals("JPY", SystemConfig.getCurrency());
    }
    
    @Test
    void testSystemConfigCreateDefaultConfigFile() {
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.configFileExists());
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testSystemConfigGetConfigFileStatus() {
        String status = SystemConfig.getConfigFileStatus();
        assertNotNull(status);
        assertTrue(status.contains("CONFIGURATION FILE STATUS"));
    }
    
    @Test
    void testSystemConfigBackupConfigFile() {
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.backupConfigFile());
    }
    
    // Performance and Stress Tests
    @Test
    void testConfigurationWithLargeValues() {
        String largeValuesConfig = """
            electricity_cost_per_hour=999999.99
            machine_time_cost_per_hour=999999.99
            base_setup_cost=999999.99
            tax_rate=0.99
            currency=USD
            max_order_quantity=2147483647
            max_order_value=999999999.99
            allow_rush_orders=true
            rush_order_surcharge=0.99
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", largeValuesConfig));
        assertTrue(ConfigFileHandler.validateConfigurationFile());
        
        assertTrue(ConfigFileHandler.loadConfiguration());
        assertEquals(999999.99, SystemConfig.getElectricityCostPerHour());
        assertEquals(2147483647, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testConfigurationWithVerySmallValues() {
        String smallValuesConfig = """
            electricity_cost_per_hour=0.0001
            machine_time_cost_per_hour=0.0001
            base_setup_cost=0.0001
            tax_rate=0.0001
            currency=USD
            max_order_quantity=1
            max_order_value=0.01
            allow_rush_orders=false
            rush_order_surcharge=0.0001
            """;
        
        assertTrue(DataFileManager.writeToFile("system_config.txt", smallValuesConfig));
        assertTrue(ConfigFileHandler.validateConfigurationFile());
        
        assertTrue(ConfigFileHandler.loadConfiguration());
        assertEquals(0.0001, SystemConfig.getElectricityCostPerHour());
        assertEquals(1, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testConfigurationRoundTrip() {
        // Set custom values
        SystemConfig.setElectricityCostPerHour(0.18);
        SystemConfig.setMachineTimeCostPerHour(2.75);
        SystemConfig.setBaseSetupCost(6.25);
        SystemConfig.setTaxRate(0.09);
        SystemConfig.setCurrency("AUD");
        SystemConfig.setMaxOrderQuantity(150);
        SystemConfig.setMaxOrderValue(2500.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.30);
        
        // Save configuration
        assertTrue(SystemConfig.saveToFile());
        
        // Reset to defaults
        SystemConfig.resetToDefaults();
        
        // Load configuration
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify all values were restored
        assertEquals(0.18, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.75, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(6.25, SystemConfig.getBaseSetupCost());
        assertEquals(0.09, SystemConfig.getTaxRate());
        assertEquals("AUD", SystemConfig.getCurrency());
        assertEquals(150, SystemConfig.getMaxOrderQuantity());
        assertEquals(2500.00, SystemConfig.getMaxOrderValue());
        assertFalse(SystemConfig.isAllowRushOrders());
        assertEquals(0.30, SystemConfig.getRushOrderSurcharge());
    }
}
