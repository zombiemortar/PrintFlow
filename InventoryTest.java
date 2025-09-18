import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the Inventory class.
 * Tests all static methods, stock management, and edge cases.
 */
public class InventoryTest {
    
    private Material testMaterial1;
    private Material testMaterial2;
    private Material testMaterial3;
    
    @BeforeEach
    void setUp() {
        // Create test materials
        testMaterial1 = new Material("PLA", 0.02, 200, "White");
        testMaterial2 = new Material("ABS", 0.025, 230, "Black");
        testMaterial3 = new Material("PETG", 0.03, 240, "Clear");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testMaterial1 = null;
        testMaterial2 = null;
        testMaterial3 = null;
    }
    
    // setStock Tests
    @Test
    void testSetStockValidInput() {
        Inventory.setStock(testMaterial1, 500);
        assertEquals(500, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testSetStockZero() {
        Inventory.setStock(testMaterial1, 0);
        assertEquals(0, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testSetStockLargeAmount() {
        Inventory.setStock(testMaterial1, 10000);
        assertEquals(10000, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testSetStockWithNullMaterial() {
        Inventory.setStock(null, 500);
        // Should not throw exception and should not affect the system
        assertEquals(1000, Inventory.getStock(testMaterial1)); // Default value
    }
    
    @Test
    void testSetStockWithNegativeAmount() {
        Inventory.setStock(testMaterial1, -100);
        // Should not set negative stock
        assertEquals(1000, Inventory.getStock(testMaterial1)); // Default value
    }
    
    @Test
    void testSetStockOverwritesExisting() {
        Inventory.setStock(testMaterial1, 500);
        assertEquals(500, Inventory.getStock(testMaterial1));
        
        Inventory.setStock(testMaterial1, 750);
        assertEquals(750, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testSetStockMultipleMaterials() {
        Inventory.setStock(testMaterial1, 500);
        Inventory.setStock(testMaterial2, 750);
        Inventory.setStock(testMaterial3, 1000);
        
        assertEquals(500, Inventory.getStock(testMaterial1));
        assertEquals(750, Inventory.getStock(testMaterial2));
        assertEquals(1000, Inventory.getStock(testMaterial3));
    }
    
    // getStock Tests
    @Test
    void testGetStockExistingMaterial() {
        Inventory.setStock(testMaterial1, 500);
        assertEquals(500, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testGetStockNonExistentMaterial() {
        assertEquals(1000, Inventory.getStock(testMaterial1)); // Default value
    }
    
    @Test
    void testGetStockWithNullMaterial() {
        assertEquals(0, Inventory.getStock(null));
    }
    
    @Test
    void testGetStockAfterSetStock() {
        Inventory.setStock(testMaterial1, 250);
        assertEquals(250, Inventory.getStock(testMaterial1));
        
        Inventory.setStock(testMaterial1, 750);
        assertEquals(750, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testGetStockDefaultValue() {
        // For materials that haven't been set, should return default of 1000
        assertEquals(1000, Inventory.getStock(testMaterial1));
        assertEquals(1000, Inventory.getStock(testMaterial2));
        assertEquals(1000, Inventory.getStock(testMaterial3));
    }
    
    // hasSufficient Tests
    @Test
    void testHasSufficientWithEnoughStock() {
        Inventory.setStock(testMaterial1, 500);
        assertTrue(Inventory.hasSufficient(testMaterial1, 250));
    }
    
    @Test
    void testHasSufficientWithExactStock() {
        Inventory.setStock(testMaterial1, 500);
        assertTrue(Inventory.hasSufficient(testMaterial1, 500));
    }
    
    @Test
    void testHasSufficientWithInsufficientStock() {
        Inventory.setStock(testMaterial1, 250);
        assertFalse(Inventory.hasSufficient(testMaterial1, 500));
    }
    
    @Test
    void testHasSufficientWithZeroRequired() {
        Inventory.setStock(testMaterial1, 500);
        assertTrue(Inventory.hasSufficient(testMaterial1, 0));
    }
    
    @Test
    void testHasSufficientWithZeroStock() {
        Inventory.setStock(testMaterial1, 0);
        assertFalse(Inventory.hasSufficient(testMaterial1, 100));
    }
    
    @Test
    void testHasSufficientWithNullMaterial() {
        assertFalse(Inventory.hasSufficient(null, 100));
    }
    
    @Test
    void testHasSufficientWithNegativeRequired() {
        Inventory.setStock(testMaterial1, 500);
        assertTrue(Inventory.hasSufficient(testMaterial1, -100)); // Negative requirement should pass
    }
    
    @Test
    void testHasSufficientWithDefaultStock() {
        // Material not explicitly set should have default 1000 grams
        assertTrue(Inventory.hasSufficient(testMaterial1, 500));
        assertTrue(Inventory.hasSufficient(testMaterial1, 1000));
        assertFalse(Inventory.hasSufficient(testMaterial1, 1500));
    }
    
    // consume Tests
    @Test
    void testConsumeValidAmount() {
        Inventory.setStock(testMaterial1, 500);
        boolean result = Inventory.consume(testMaterial1, 250);
        assertTrue(result);
        assertEquals(250, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testConsumeExactAmount() {
        Inventory.setStock(testMaterial1, 500);
        boolean result = Inventory.consume(testMaterial1, 500);
        assertTrue(result);
        assertEquals(0, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testConsumeInsufficientStock() {
        Inventory.setStock(testMaterial1, 250);
        boolean result = Inventory.consume(testMaterial1, 500);
        assertFalse(result);
        assertEquals(250, Inventory.getStock(testMaterial1)); // Stock should remain unchanged
    }
    
    @Test
    void testConsumeZeroAmount() {
        Inventory.setStock(testMaterial1, 500);
        boolean result = Inventory.consume(testMaterial1, 0);
        assertFalse(result); // Should return false for zero consumption
        assertEquals(500, Inventory.getStock(testMaterial1)); // Stock should remain unchanged
    }
    
    @Test
    void testConsumeNegativeAmount() {
        Inventory.setStock(testMaterial1, 500);
        boolean result = Inventory.consume(testMaterial1, -100);
        assertFalse(result); // Should return false for negative consumption
        assertEquals(500, Inventory.getStock(testMaterial1)); // Stock should remain unchanged
    }
    
    @Test
    void testConsumeWithNullMaterial() {
        boolean result = Inventory.consume(null, 100);
        assertFalse(result);
    }
    
    @Test
    void testConsumeMultipleTimes() {
        Inventory.setStock(testMaterial1, 1000);
        
        boolean result1 = Inventory.consume(testMaterial1, 200);
        assertTrue(result1);
        assertEquals(800, Inventory.getStock(testMaterial1));
        
        boolean result2 = Inventory.consume(testMaterial1, 300);
        assertTrue(result2);
        assertEquals(500, Inventory.getStock(testMaterial1));
        
        boolean result3 = Inventory.consume(testMaterial1, 500);
        assertTrue(result3);
        assertEquals(0, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testConsumeWithDefaultStock() {
        // Material not explicitly set should have default 1000 grams
        boolean result = Inventory.consume(testMaterial1, 500);
        assertTrue(result);
        assertEquals(500, Inventory.getStock(testMaterial1));
    }
    
    // Integration Tests
    @Test
    void testCompleteInventoryWorkflow() {
        // Set initial stock
        Inventory.setStock(testMaterial1, 1000);
        Inventory.setStock(testMaterial2, 750);
        
        // Check stock levels
        assertEquals(1000, Inventory.getStock(testMaterial1));
        assertEquals(750, Inventory.getStock(testMaterial2));
        
        // Check sufficiency
        assertTrue(Inventory.hasSufficient(testMaterial1, 500));
        assertTrue(Inventory.hasSufficient(testMaterial2, 300));
        assertFalse(Inventory.hasSufficient(testMaterial2, 800));
        
        // Consume some stock
        boolean consumed1 = Inventory.consume(testMaterial1, 200);
        boolean consumed2 = Inventory.consume(testMaterial2, 150);
        
        assertTrue(consumed1);
        assertTrue(consumed2);
        
        // Check updated stock levels
        assertEquals(800, Inventory.getStock(testMaterial1));
        assertEquals(600, Inventory.getStock(testMaterial2));
        
        // Check sufficiency again
        assertTrue(Inventory.hasSufficient(testMaterial1, 500));
        assertFalse(Inventory.hasSufficient(testMaterial2, 700));
    }
    
    @Test
    void testInventoryWithMultipleMaterials() {
        // Set different stock levels for different materials
        Inventory.setStock(testMaterial1, 500);
        Inventory.setStock(testMaterial2, 750);
        Inventory.setStock(testMaterial3, 1000);
        
        // Verify each material has correct stock
        assertEquals(500, Inventory.getStock(testMaterial1));
        assertEquals(750, Inventory.getStock(testMaterial2));
        assertEquals(1000, Inventory.getStock(testMaterial3));
        
        // Consume from different materials
        Inventory.consume(testMaterial1, 100);
        Inventory.consume(testMaterial2, 200);
        Inventory.consume(testMaterial3, 300);
        
        // Verify updated stock levels
        assertEquals(400, Inventory.getStock(testMaterial1));
        assertEquals(550, Inventory.getStock(testMaterial2));
        assertEquals(700, Inventory.getStock(testMaterial3));
    }
    
    // Edge Cases
    @Test
    void testInventoryWithVeryLargeStock() {
        Inventory.setStock(testMaterial1, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, Inventory.getStock(testMaterial1));
        
        assertTrue(Inventory.hasSufficient(testMaterial1, 1000000));
        assertTrue(Inventory.hasSufficient(testMaterial1, Integer.MAX_VALUE));
    }
    
    @Test
    void testInventoryWithVeryLargeConsumption() {
        Inventory.setStock(testMaterial1, 1000000);
        boolean result = Inventory.consume(testMaterial1, 999999);
        assertTrue(result);
        assertEquals(1, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testInventoryWithZeroStockAfterConsumption() {
        Inventory.setStock(testMaterial1, 500);
        boolean result = Inventory.consume(testMaterial1, 500);
        assertTrue(result);
        assertEquals(0, Inventory.getStock(testMaterial1));
        
        // Should not be able to consume more
        boolean result2 = Inventory.consume(testMaterial1, 1);
        assertFalse(result2);
        assertEquals(0, Inventory.getStock(testMaterial1));
    }
    
    @Test
    void testInventoryWithMaterialNameChanges() {
        // Set stock for material
        Inventory.setStock(testMaterial1, 500);
        assertEquals(500, Inventory.getStock(testMaterial1));
        
        // Change material name
        testMaterial1.setName("NewPLA");
        
        // Should still be able to access by new name
        assertEquals(500, Inventory.getStock(testMaterial1));
        
        // Old name should return default
        Material oldMaterial = new Material("PLA", 0.02, 200, "White");
        assertEquals(1000, Inventory.getStock(oldMaterial));
    }
    
    @Test
    void testInventoryWithSpecialCharactersInMaterialName() {
        Material specialMaterial = new Material("PLA+", 0.02, 200, "White");
        Inventory.setStock(specialMaterial, 500);
        assertEquals(500, Inventory.getStock(specialMaterial));
        
        boolean result = Inventory.consume(specialMaterial, 200);
        assertTrue(result);
        assertEquals(300, Inventory.getStock(specialMaterial));
    }
    
    @Test
    void testInventoryWithUnicodeCharactersInMaterialName() {
        Material unicodeMaterial = new Material("PLA™", 0.02, 200, "White");
        Inventory.setStock(unicodeMaterial, 500);
        assertEquals(500, Inventory.getStock(unicodeMaterial));
        
        boolean result = Inventory.consume(unicodeMaterial, 200);
        assertTrue(result);
        assertEquals(300, Inventory.getStock(unicodeMaterial));
    }
    
    @Test
    void testInventoryWithWhitespaceInMaterialName() {
        Material whitespaceMaterial = new Material("  PLA  ", 0.02, 200, "White");
        Inventory.setStock(whitespaceMaterial, 500);
        assertEquals(500, Inventory.getStock(whitespaceMaterial));
        
        boolean result = Inventory.consume(whitespaceMaterial, 200);
        assertTrue(result);
        assertEquals(300, Inventory.getStock(whitespaceMaterial));
    }
    
    @Test
    void testInventoryWithEmptyMaterialName() {
        Material emptyNameMaterial = new Material("", 0.02, 200, "White");
        Inventory.setStock(emptyNameMaterial, 500);
        assertEquals(500, Inventory.getStock(emptyNameMaterial));
        
        boolean result = Inventory.consume(emptyNameMaterial, 200);
        assertTrue(result);
        assertEquals(300, Inventory.getStock(emptyNameMaterial));
    }
    
    @Test
    void testInventoryWithNullMaterialName() {
        Material nullNameMaterial = new Material(null, 0.02, 200, "White");
        Inventory.setStock(nullNameMaterial, 500);
        assertEquals(500, Inventory.getStock(nullNameMaterial));
        
        boolean result = Inventory.consume(nullNameMaterial, 200);
        assertTrue(result);
        assertEquals(300, Inventory.getStock(nullNameMaterial));
    }
}
