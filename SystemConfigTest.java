import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the SystemConfig class.
 * Tests all static methods, configuration management, and edge cases.
 */
public class SystemConfigTest {
    
    @BeforeEach
    void setUp() {
        // Reset to defaults before each test
        SystemConfig.resetToDefaults();
    }
    
    @AfterEach
    void tearDown() {
        // Reset to defaults after each test
        SystemConfig.resetToDefaults();
    }
    
    // Electricity Cost Tests
    @Test
    void testGetElectricityCostPerHourDefault() {
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour());
    }
    
    @Test
    void testSetElectricityCostPerHourValidValue() {
        SystemConfig.setElectricityCostPerHour(0.20);
        assertEquals(0.20, SystemConfig.getElectricityCostPerHour());
    }
    
    @Test
    void testSetElectricityCostPerHourZero() {
        SystemConfig.setElectricityCostPerHour(0.0);
        assertEquals(0.0, SystemConfig.getElectricityCostPerHour());
    }
    
    @Test
    void testSetElectricityCostPerHourNegative() {
        SystemConfig.setElectricityCostPerHour(-0.10);
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour()); // Should remain unchanged
    }
    
    @Test
    void testSetElectricityCostPerHourLargeValue() {
        SystemConfig.setElectricityCostPerHour(100.0);
        assertEquals(100.0, SystemConfig.getElectricityCostPerHour());
    }
    
    @Test
    void testSetElectricityCostPerHourDecimalPrecision() {
        SystemConfig.setElectricityCostPerHour(0.123456789);
        assertEquals(0.123456789, SystemConfig.getElectricityCostPerHour());
    }
    
    // Machine Time Cost Tests
    @Test
    void testGetMachineTimeCostPerHourDefault() {
        assertEquals(2.50, SystemConfig.getMachineTimeCostPerHour());
    }
    
    @Test
    void testSetMachineTimeCostPerHourValidValue() {
        SystemConfig.setMachineTimeCostPerHour(3.00);
        assertEquals(3.00, SystemConfig.getMachineTimeCostPerHour());
    }
    
    @Test
    void testSetMachineTimeCostPerHourZero() {
        SystemConfig.setMachineTimeCostPerHour(0.0);
        assertEquals(0.0, SystemConfig.getMachineTimeCostPerHour());
    }
    
    @Test
    void testSetMachineTimeCostPerHourNegative() {
        SystemConfig.setMachineTimeCostPerHour(-1.0);
        assertEquals(2.50, SystemConfig.getMachineTimeCostPerHour()); // Should remain unchanged
    }
    
    @Test
    void testSetMachineTimeCostPerHourLargeValue() {
        SystemConfig.setMachineTimeCostPerHour(1000.0);
        assertEquals(1000.0, SystemConfig.getMachineTimeCostPerHour());
    }
    
    // Base Setup Cost Tests
    @Test
    void testGetBaseSetupCostDefault() {
        assertEquals(5.00, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testSetBaseSetupCostValidValue() {
        SystemConfig.setBaseSetupCost(7.50);
        assertEquals(7.50, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testSetBaseSetupCostZero() {
        SystemConfig.setBaseSetupCost(0.0);
        assertEquals(0.0, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testSetBaseSetupCostNegative() {
        SystemConfig.setBaseSetupCost(-2.0);
        assertEquals(5.00, SystemConfig.getBaseSetupCost()); // Should remain unchanged
    }
    
    @Test
    void testSetBaseSetupCostLargeValue() {
        SystemConfig.setBaseSetupCost(500.0);
        assertEquals(500.0, SystemConfig.getBaseSetupCost());
    }
    
    // Tax Rate Tests
    @Test
    void testGetTaxRateDefault() {
        assertEquals(0.08, SystemConfig.getTaxRate());
    }
    
    @Test
    void testSetTaxRateValidValue() {
        SystemConfig.setTaxRate(0.10);
        assertEquals(0.10, SystemConfig.getTaxRate());
    }
    
    @Test
    void testSetTaxRateZero() {
        SystemConfig.setTaxRate(0.0);
        assertEquals(0.0, SystemConfig.getTaxRate());
    }
    
    @Test
    void testSetTaxRateMaximum() {
        SystemConfig.setTaxRate(1.0);
        assertEquals(1.0, SystemConfig.getTaxRate());
    }
    
    @Test
    void testSetTaxRateNegative() {
        SystemConfig.setTaxRate(-0.05);
        assertEquals(0.08, SystemConfig.getTaxRate()); // Should remain unchanged
    }
    
    @Test
    void testSetTaxRateAboveMaximum() {
        SystemConfig.setTaxRate(1.5);
        assertEquals(0.08, SystemConfig.getTaxRate()); // Should remain unchanged
    }
    
    @Test
    void testSetTaxRateDecimalPrecision() {
        SystemConfig.setTaxRate(0.0825);
        assertEquals(0.0825, SystemConfig.getTaxRate());
    }
    
    // Currency Tests
    @Test
    void testGetCurrencyDefault() {
        assertEquals("USD", SystemConfig.getCurrency());
    }
    
    @Test
    void testSetCurrencyValidValue() {
        SystemConfig.setCurrency("EUR");
        assertEquals("EUR", SystemConfig.getCurrency());
    }
    
    @Test
    void testSetCurrencyLowerCase() {
        SystemConfig.setCurrency("eur");
        assertEquals("EUR", SystemConfig.getCurrency()); // Should be converted to uppercase
    }
    
    @Test
    void testSetCurrencyWithWhitespace() {
        SystemConfig.setCurrency("  GBP  ");
        assertEquals("GBP", SystemConfig.getCurrency()); // Should be trimmed and uppercased
    }
    
    @Test
    void testSetCurrencyWithNull() {
        SystemConfig.setCurrency(null);
        assertEquals("USD", SystemConfig.getCurrency()); // Should remain unchanged
    }
    
    @Test
    void testSetCurrencyWithEmptyString() {
        SystemConfig.setCurrency("");
        assertEquals("USD", SystemConfig.getCurrency()); // Should remain unchanged
    }
    
    @Test
    void testSetCurrencyWithWhitespaceOnly() {
        SystemConfig.setCurrency("   ");
        assertEquals("USD", SystemConfig.getCurrency()); // Should remain unchanged
    }
    
    @Test
    void testSetCurrencyWithSpecialCharacters() {
        SystemConfig.setCurrency("USD$");
        assertEquals("USD$", SystemConfig.getCurrency());
    }
    
    // Max Order Quantity Tests
    @Test
    void testGetMaxOrderQuantityDefault() {
        assertEquals(100, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testSetMaxOrderQuantityValidValue() {
        SystemConfig.setMaxOrderQuantity(50);
        assertEquals(50, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testSetMaxOrderQuantityZero() {
        SystemConfig.setMaxOrderQuantity(0);
        assertEquals(100, SystemConfig.getMaxOrderQuantity()); // Should remain unchanged
    }
    
    @Test
    void testSetMaxOrderQuantityNegative() {
        SystemConfig.setMaxOrderQuantity(-10);
        assertEquals(100, SystemConfig.getMaxOrderQuantity()); // Should remain unchanged
    }
    
    @Test
    void testSetMaxOrderQuantityLargeValue() {
        SystemConfig.setMaxOrderQuantity(10000);
        assertEquals(10000, SystemConfig.getMaxOrderQuantity());
    }
    
    // Max Order Value Tests
    @Test
    void testGetMaxOrderValueDefault() {
        assertEquals(1000.00, SystemConfig.getMaxOrderValue());
    }
    
    @Test
    void testSetMaxOrderValueValidValue() {
        SystemConfig.setMaxOrderValue(1500.00);
        assertEquals(1500.00, SystemConfig.getMaxOrderValue());
    }
    
    @Test
    void testSetMaxOrderValueZero() {
        SystemConfig.setMaxOrderValue(0.0);
        assertEquals(1000.00, SystemConfig.getMaxOrderValue()); // Should remain unchanged
    }
    
    @Test
    void testSetMaxOrderValueNegative() {
        SystemConfig.setMaxOrderValue(-100.0);
        assertEquals(1000.00, SystemConfig.getMaxOrderValue()); // Should remain unchanged
    }
    
    @Test
    void testSetMaxOrderValueLargeValue() {
        SystemConfig.setMaxOrderValue(100000.00);
        assertEquals(100000.00, SystemConfig.getMaxOrderValue());
    }
    
    // Rush Order Settings Tests
    @Test
    void testIsAllowRushOrdersDefault() {
        assertTrue(SystemConfig.isAllowRushOrders());
    }
    
    @Test
    void testSetAllowRushOrdersTrue() {
        SystemConfig.setAllowRushOrders(true);
        assertTrue(SystemConfig.isAllowRushOrders());
    }
    
    @Test
    void testSetAllowRushOrdersFalse() {
        SystemConfig.setAllowRushOrders(false);
        assertFalse(SystemConfig.isAllowRushOrders());
    }
    
    @Test
    void testGetRushOrderSurchargeDefault() {
        assertEquals(0.25, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSetRushOrderSurchargeValidValue() {
        SystemConfig.setRushOrderSurcharge(0.30);
        assertEquals(0.30, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSetRushOrderSurchargeZero() {
        SystemConfig.setRushOrderSurcharge(0.0);
        assertEquals(0.0, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSetRushOrderSurchargeNegative() {
        SystemConfig.setRushOrderSurcharge(-0.10);
        assertEquals(0.25, SystemConfig.getRushOrderSurcharge()); // Should remain unchanged
    }
    
    @Test
    void testSetRushOrderSurchargeLargeValue() {
        SystemConfig.setRushOrderSurcharge(1.0);
        assertEquals(1.0, SystemConfig.getRushOrderSurcharge());
    }
    
    // Configuration Summary Tests
    @Test
    void testGetConfigurationSummary() {
        String summary = SystemConfig.getConfigurationSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("SYSTEM CONFIGURATION SUMMARY"));
        assertTrue(summary.contains("PRICING CONSTANTS:"));
        assertTrue(summary.contains("Electricity Cost: $0.15 per hour"));
        assertTrue(summary.contains("Machine Time Cost: $2.50 per hour"));
        assertTrue(summary.contains("Base Setup Cost: $5.00"));
        assertTrue(summary.contains("TAX & CURRENCY:"));
        assertTrue(summary.contains("Tax Rate: 8.0%"));
        assertTrue(summary.contains("Currency: USD"));
        assertTrue(summary.contains("ORDER LIMITS:"));
        assertTrue(summary.contains("Max Order Quantity: 100 items"));
        assertTrue(summary.contains("Max Order Value: $1000.00"));
        assertTrue(summary.contains("RUSH ORDER SETTINGS:"));
        assertTrue(summary.contains("Rush Orders Allowed: Yes"));
        assertTrue(summary.contains("Rush Order Surcharge: 25.0%"));
    }
    
    @Test
    void testGetConfigurationSummaryAfterChanges() {
        SystemConfig.setElectricityCostPerHour(0.20);
        SystemConfig.setMachineTimeCostPerHour(3.00);
        SystemConfig.setBaseSetupCost(7.50);
        SystemConfig.setTaxRate(0.10);
        SystemConfig.setCurrency("EUR");
        SystemConfig.setMaxOrderQuantity(50);
        SystemConfig.setMaxOrderValue(1500.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.30);
        
        String summary = SystemConfig.getConfigurationSummary();
        assertTrue(summary.contains("Electricity Cost: $0.20 per hour"));
        assertTrue(summary.contains("Machine Time Cost: $3.00 per hour"));
        assertTrue(summary.contains("Base Setup Cost: $7.50"));
        assertTrue(summary.contains("Tax Rate: 10.0%"));
        assertTrue(summary.contains("Currency: EUR"));
        assertTrue(summary.contains("Max Order Quantity: 50 items"));
        assertTrue(summary.contains("Max Order Value: $1500.00"));
        assertTrue(summary.contains("Rush Orders Allowed: No"));
        assertTrue(summary.contains("Rush Order Surcharge: 30.0%"));
    }
    
    // Reset to Defaults Tests
    @Test
    void testResetToDefaults() {
        // Change all values
        SystemConfig.setElectricityCostPerHour(0.30);
        SystemConfig.setMachineTimeCostPerHour(5.00);
        SystemConfig.setBaseSetupCost(10.00);
        SystemConfig.setTaxRate(0.15);
        SystemConfig.setCurrency("EUR");
        SystemConfig.setMaxOrderQuantity(200);
        SystemConfig.setMaxOrderValue(2000.00);
        SystemConfig.setAllowRushOrders(false);
        SystemConfig.setRushOrderSurcharge(0.50);
        
        // Reset to defaults
        SystemConfig.resetToDefaults();
        
        // Verify all values are reset
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
    
    // Edge Cases
    @Test
    void testSystemConfigWithVeryLargeValues() {
        SystemConfig.setElectricityCostPerHour(Double.MAX_VALUE);
        SystemConfig.setMachineTimeCostPerHour(Double.MAX_VALUE);
        SystemConfig.setBaseSetupCost(Double.MAX_VALUE);
        SystemConfig.setMaxOrderValue(Double.MAX_VALUE);
        SystemConfig.setRushOrderSurcharge(Double.MAX_VALUE);
        
        assertEquals(Double.MAX_VALUE, SystemConfig.getElectricityCostPerHour());
        assertEquals(Double.MAX_VALUE, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(Double.MAX_VALUE, SystemConfig.getBaseSetupCost());
        assertEquals(Double.MAX_VALUE, SystemConfig.getMaxOrderValue());
        assertEquals(Double.MAX_VALUE, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSystemConfigWithVerySmallValues() {
        SystemConfig.setElectricityCostPerHour(Double.MIN_VALUE);
        SystemConfig.setMachineTimeCostPerHour(Double.MIN_VALUE);
        SystemConfig.setBaseSetupCost(Double.MIN_VALUE);
        SystemConfig.setMaxOrderValue(Double.MIN_VALUE);
        SystemConfig.setRushOrderSurcharge(Double.MIN_VALUE);
        
        assertEquals(Double.MIN_VALUE, SystemConfig.getElectricityCostPerHour());
        assertEquals(Double.MIN_VALUE, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(Double.MIN_VALUE, SystemConfig.getBaseSetupCost());
        assertEquals(Double.MIN_VALUE, SystemConfig.getMaxOrderValue());
        assertEquals(Double.MIN_VALUE, SystemConfig.getRushOrderSurcharge());
    }
    
    @Test
    void testSystemConfigWithIntegerMaxValue() {
        SystemConfig.setMaxOrderQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, SystemConfig.getMaxOrderQuantity());
    }
    
    @Test
    void testSystemConfigWithIntegerMinValue() {
        SystemConfig.setMaxOrderQuantity(Integer.MIN_VALUE);
        assertEquals(100, SystemConfig.getMaxOrderQuantity()); // Should remain unchanged
    }
    
    @Test
    void testSystemConfigWithSpecialCurrencyCodes() {
        SystemConfig.setCurrency("BTC");
        assertEquals("BTC", SystemConfig.getCurrency());
        
        SystemConfig.setCurrency("ETH");
        assertEquals("ETH", SystemConfig.getCurrency());
        
        SystemConfig.setCurrency("JPY");
        assertEquals("JPY", SystemConfig.getCurrency());
    }
    
    @Test
    void testSystemConfigWithUnicodeCurrency() {
        SystemConfig.setCurrency("€");
        assertEquals("€", SystemConfig.getCurrency());
        
        SystemConfig.setCurrency("¥");
        assertEquals("¥", SystemConfig.getCurrency());
        
        SystemConfig.setCurrency("£");
        assertEquals("£", SystemConfig.getCurrency());
    }
    
    @Test
    void testSystemConfigWithVeryLongCurrencyString() {
        String longCurrency = "VERYLONGCURRENCYCODE";
        SystemConfig.setCurrency(longCurrency);
        assertEquals(longCurrency, SystemConfig.getCurrency());
    }
    
    @Test
    void testSystemConfigWithDecimalPrecision() {
        SystemConfig.setElectricityCostPerHour(0.1234567890123456789);
        SystemConfig.setMachineTimeCostPerHour(2.9876543210987654321);
        SystemConfig.setBaseSetupCost(5.5555555555555555555);
        SystemConfig.setTaxRate(0.0825);
        SystemConfig.setMaxOrderValue(1234.567890123456789);
        SystemConfig.setRushOrderSurcharge(0.123456789);
        
        assertEquals(0.1234567890123456789, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.9876543210987654321, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(5.5555555555555555555, SystemConfig.getBaseSetupCost());
        assertEquals(0.0825, SystemConfig.getTaxRate());
        assertEquals(1234.567890123456789, SystemConfig.getMaxOrderValue());
        assertEquals(0.123456789, SystemConfig.getRushOrderSurcharge());
    }
}
