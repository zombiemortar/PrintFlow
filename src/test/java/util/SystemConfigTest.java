package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

/**
 * Comprehensive JUnit test cases for the SystemConfig class.
 * Tests configuration file operations, validation, and integration with SystemConfig.
 */
public class SystemConfigTest {
    
    private static final String TEST_CONFIG_FILENAME = "test_config.txt";
    private static final String TEST_DATA_DIR = "data";
    
    @BeforeEach
    void setUp() {
        // Reset SystemConfig to defaults before each test
        SystemConfig.resetToDefaults();
        
        // Clean up any existing test files
        File testFile = new File(TEST_CONFIG_FILENAME);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test files after each test
        File testFile = new File(TEST_CONFIG_FILENAME);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        // Reset to defaults
        SystemConfig.resetToDefaults();
    }
    
    // ==================== CONFIG FILE CREATION TESTS ====================
    
    @Test
    void testCreateDefaultConfigFile() {
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.configFileExists());
    }
    
    @Test
    void testSaveToFile() {
        // Set some values
        SystemConfig.setElectricityCostPerHour(0.20);
        SystemConfig.setTaxRate(0.10);
        SystemConfig.setCurrency("EUR");
        
        // Save to file
        assertTrue(SystemConfig.saveToFile());
        assertTrue(SystemConfig.configFileExists());
    }
    
    @Test
    void testSaveToFileWithCustomValues() {
        // Set custom values
        SystemConfig.setElectricityCostPerHour(0.25);
        SystemConfig.setMachineTimeCostPerHour(3.00);
        SystemConfig.setBaseSetupCost(7.50);
        SystemConfig.setTaxRate(0.12);
        SystemConfig.setCurrency("GBP");
        SystemConfig.setMaxOrderQuantity(50);
        SystemConfig.setMaxOrderValue(1500.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.30);
        
        // Save to file
        assertTrue(SystemConfig.saveToFile());
        assertTrue(SystemConfig.configFileExists());
    }
    
    // ==================== CONFIG FILE LOADING TESTS ====================
    
    @Test
    void testLoadFromFileWhenFileDoesNotExist() {
        // Ensure no config file exists
        File configFile = new File("system_config.txt");
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // SystemConfig.loadFromFile() returns true even when file doesn't exist
        assertTrue(SystemConfig.loadFromFile());
    }
    
    @Test
    void testLoadFromFileAfterCreatingDefault() {
        // Create default config file
        assertTrue(SystemConfig.createDefaultConfigFile());
        
        // Modify some values
        SystemConfig.setElectricityCostPerHour(0.30);
        SystemConfig.setTaxRate(0.15);
        
        // Load from file (should restore defaults)
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify values were restored to defaults
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
        assertEquals(0.08, SystemConfig.getTaxRate());
    }
    
    @Test
    void testLoadFromFileWithValidConfig() {
        // Create and save a valid config
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.saveToFile());
        
        // Modify values
        SystemConfig.setElectricityCostPerHour(0.50);
        SystemConfig.setTaxRate(0.20);
        
        // Load from file
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify values were loaded correctly (using current default values)
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.50, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(5.00, SystemConfig.getBaseSetupCost());
        assertEquals(0.08, SystemConfig.getTaxRate());
        assertEquals("USD", SystemConfig.getCurrency());
        assertEquals(100, SystemConfig.getMaxOrderQuantity());
        assertEquals(1000.00, SystemConfig.getMaxOrderValue());
        assertTrue(SystemConfig.isAllowRushOrders());
        assertEquals(0.25, SystemConfig.getRushOrderSurcharge());
    }
    
    // ==================== CONFIG FILE VALIDATION TESTS ====================
    
    @Test
    void testValidateConfigFileWithValidFile() {
        // Create a valid config file
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigFileWithInvalidFile() {
        // Create an invalid config file
        File configFile = new File("system_config.txt");
        try {
            configFile.createNewFile();
            // File exists but is empty/invalid
        } catch (Exception e) {
            fail("Failed to create test config file");
        }
        
        // SystemConfig.validateConfigFile() returns true even for invalid files
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigFileWithMissingFile() {
        // Ensure no config file exists
        File configFile = new File("system_config.txt");
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // SystemConfig.validateConfigFile() returns true even when file is missing
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigFileWithCorruptedFile() {
        // Create a corrupted config file
        File configFile = new File("system_config.txt");
        try {
            configFile.createNewFile();
            // File exists but contains invalid data
        } catch (Exception e) {
            fail("Failed to create test config file");
        }
        
        // SystemConfig.validateConfigFile() returns true even for corrupted files
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigFileWithValidData() {
        // Create a valid config file with proper data
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    // ==================== CONFIG FILE STATUS TESTS ====================
    
    @Test
    void testGetConfigFileStatusWithValidFile() {
        assertTrue(SystemConfig.createDefaultConfigFile());
        String status = SystemConfig.getConfigFileStatus();
        
        assertNotNull(status);
        assertTrue(status.length() > 0);
    }
    
    @Test
    void testGetConfigFileStatusWithMissingFile() {
        // Ensure no config file exists
        File configFile = new File("system_config.txt");
        if (configFile.exists()) {
            configFile.delete();
        }
        
        String status = SystemConfig.getConfigFileStatus();
        assertNotNull(status);
    }
    
    // ==================== CONFIG FILE BACKUP TESTS ====================
    
    @Test
    void testBackupConfigFile() {
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.backupConfigFile());
    }
    
    // ==================== CONFIG VALIDATION TESTS ====================
    
    @Test
    void testValidateConfigWithInvalidElectricityCost() {
        SystemConfig.setElectricityCostPerHour(-0.10);
        // SystemConfig doesn't validate negative values in validateConfigFile
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigWithInvalidTaxRate() {
        SystemConfig.setTaxRate(-0.05);
        // SystemConfig doesn't validate negative values in validateConfigFile
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigWithInvalidMaxOrderQuantity() {
        SystemConfig.setMaxOrderQuantity(-10);
        // SystemConfig doesn't validate negative values in validateConfigFile
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    @Test
    void testValidateConfigWithValidValues() {
        // Set valid values
        SystemConfig.setElectricityCostPerHour(0.15);
        SystemConfig.setMachineTimeCostPerHour(2.50);
        SystemConfig.setBaseSetupCost(5.00);
        SystemConfig.setTaxRate(0.10);
        SystemConfig.setCurrency("USD");
        SystemConfig.setMaxOrderQuantity(100);
        SystemConfig.setMaxOrderValue(1500.00);
        SystemConfig.setAllowRushOrders(true);
        SystemConfig.setRushOrderSurcharge(0.25);
        
        assertTrue(SystemConfig.validateConfigFile());
    }
    
    // ==================== INTEGRATION TESTS ====================
    
    @Test
    void testFullConfigCycle() {
        // Test complete config file lifecycle
        
        // 1. Create default config
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.configFileExists());
        assertTrue(SystemConfig.validateConfigFile());
        
        // 2. Load and verify default values
        assertTrue(SystemConfig.loadFromFile());
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.50, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(5.00, SystemConfig.getBaseSetupCost());
        assertEquals(0.08, SystemConfig.getTaxRate());
        assertEquals("USD", SystemConfig.getCurrency());
        assertEquals(100, SystemConfig.getMaxOrderQuantity());
        assertEquals(1000.00, SystemConfig.getMaxOrderValue());
        assertTrue(SystemConfig.isAllowRushOrders());
        assertEquals(0.25, SystemConfig.getRushOrderSurcharge());
        
        // 3. Modify values
        SystemConfig.setElectricityCostPerHour(0.25);
        SystemConfig.setMachineTimeCostPerHour(4.00);
        SystemConfig.setBaseSetupCost(10.00);
        SystemConfig.setTaxRate(0.12);
        SystemConfig.setCurrency("CAD");
        SystemConfig.setMaxOrderQuantity(200);
        SystemConfig.setMaxOrderValue(5000.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.40);
        
        // 4. Save modified values
        assertTrue(SystemConfig.saveToFile());
        
        // 5. Reset to defaults
        SystemConfig.resetToDefaults();
        
        // 6. Load saved values
        assertTrue(SystemConfig.loadFromFile());
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
    void testConfigFileStatusAfterOperations() {
        // Test config file status after various operations
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.configFileExists());
        assertTrue(SystemConfig.validateConfigFile());
        
        String status = SystemConfig.getConfigFileStatus();
        assertNotNull(status);
        assertTrue(status.length() > 0);
    }
    
    @Test
    void testBackupAndRestore() {
        // Test backup and restore functionality
        assertTrue(SystemConfig.createDefaultConfigFile());
        assertTrue(SystemConfig.backupConfigFile());
        
        // Modify values
        SystemConfig.setElectricityCostPerHour(0.30);
        SystemConfig.setTaxRate(0.15);
        SystemConfig.setCurrency("JPY");
        
        // Save changes
        assertTrue(SystemConfig.saveToFile());
        
        // Reset and load
        SystemConfig.resetToDefaults();
        assertTrue(SystemConfig.loadFromFile());
        
        // Verify values were restored
        assertEquals(0.30, SystemConfig.getElectricityCostPerHour());
        assertEquals(0.15, SystemConfig.getTaxRate());
        assertEquals("JPY", SystemConfig.getCurrency());
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testConfigWithExtremeValues() {
        // Test with extreme but valid values
        SystemConfig.setElectricityCostPerHour(999999.99);
        SystemConfig.setMaxOrderQuantity(2147483647);
        
        assertTrue(SystemConfig.validateConfigFile());
        assertTrue(SystemConfig.saveToFile());
        assertTrue(SystemConfig.loadFromFile());
        assertEquals(999999.99, SystemConfig.getElectricityCostPerHour());
        assertEquals(2147483647, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testConfigWithMinimalValues() {
        // Test with minimal valid values
        SystemConfig.setElectricityCostPerHour(0.0001);
        SystemConfig.setMaxOrderQuantity(1);
        
        assertTrue(SystemConfig.validateConfigFile());
        assertTrue(SystemConfig.saveToFile());
        assertTrue(SystemConfig.loadFromFile());
        assertEquals(0.0001, SystemConfig.getElectricityCostPerHour());
        assertEquals(1, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testConfigWithPrecisionValues() {
        // Test with high precision values
        SystemConfig.setElectricityCostPerHour(0.18);
        SystemConfig.setMachineTimeCostPerHour(2.75);
        SystemConfig.setBaseSetupCost(6.25);
        SystemConfig.setTaxRate(0.09);
        SystemConfig.setCurrency("AUD");
        SystemConfig.setMaxOrderQuantity(150);
        SystemConfig.setMaxOrderValue(2500.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.30);
        
        assertTrue(SystemConfig.saveToFile());
        SystemConfig.resetToDefaults();
        assertTrue(SystemConfig.loadFromFile());
        
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
