import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the AdminUser class.
 * Tests all methods, constructors, inheritance, and edge cases.
 */
public class AdminUserTest {
    
    private AdminUser testAdmin;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        SystemConfig.resetToDefaults();
        
        // Create test admin user
        testAdmin = new AdminUser("adminuser", "admin@example.com");
        
        // Create test material
        testMaterial = new Material("PLA", 0.02, 200, "White");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testAdmin = null;
        testMaterial = null;
    }
    
    // Constructor Tests
    @Test
    void testParameterizedConstructor() {
        AdminUser admin = new AdminUser("admin", "admin@test.com");
        assertEquals("admin", admin.getUsername());
        assertEquals("admin@test.com", admin.getEmail());
        assertEquals("admin", admin.getRole());
    }
    
    @Test
    void testDefaultConstructor() {
        AdminUser admin = new AdminUser();
        assertEquals("", admin.getUsername());
        assertEquals("", admin.getEmail());
        assertEquals("admin", admin.getRole());
    }
    
    @Test
    void testConstructorWithNullValues() {
        AdminUser admin = new AdminUser(null, null);
        assertNull(admin.getUsername());
        assertNull(admin.getEmail());
        assertEquals("admin", admin.getRole()); // Role should always be admin
    }
    
    // Inheritance Tests
    @Test
    void testInheritanceFromUser() {
        assertTrue(testAdmin instanceof User);
    }
    
    @Test
    void testInheritedMethods() {
        testAdmin.setUsername("newadmin");
        testAdmin.setEmail("newadmin@test.com");
        assertEquals("newadmin", testAdmin.getUsername());
        assertEquals("newadmin@test.com", testAdmin.getEmail());
    }
    
    // Role Management Tests
    @Test
    void testRoleAlwaysReturnsAdmin() {
        assertEquals("admin", testAdmin.getRole());
        
        // Try to change role - should be ignored
        testAdmin.setRole("customer");
        assertEquals("admin", testAdmin.getRole());
        
        testAdmin.setRole("vip");
        assertEquals("admin", testAdmin.getRole());
        
        testAdmin.setRole(null);
        assertEquals("admin", testAdmin.getRole());
    }
    
    // addMaterial Tests
    @Test
    void testAddMaterialValidInput() {
        Material material = testAdmin.addMaterial("ABS", 0.025, 230, "Black");
        assertNotNull(material);
        assertEquals("ABS", material.getName());
        assertEquals(0.025, material.getCostPerGram());
        assertEquals(230, material.getPrintTemp());
        assertEquals("Black", material.getColor());
        
        // Check that stock was set
        assertEquals(1000, Inventory.getStock(material));
    }
    
    @Test
    void testAddMaterialWithNullName() {
        Material material = testAdmin.addMaterial(null, 0.025, 230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithEmptyName() {
        Material material = testAdmin.addMaterial("", 0.025, 230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithWhitespaceName() {
        Material material = testAdmin.addMaterial("   ", 0.025, 230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithZeroCost() {
        Material material = testAdmin.addMaterial("ABS", 0.0, 230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithNegativeCost() {
        Material material = testAdmin.addMaterial("ABS", -0.025, 230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithZeroTemperature() {
        Material material = testAdmin.addMaterial("ABS", 0.025, 0, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithNegativeTemperature() {
        Material material = testAdmin.addMaterial("ABS", 0.025, -230, "Black");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithNullColor() {
        Material material = testAdmin.addMaterial("ABS", 0.025, 230, null);
        assertNull(material);
    }
    
    @Test
    void testAddMaterialWithEmptyColor() {
        Material material = testAdmin.addMaterial("ABS", 0.025, 230, "");
        assertNull(material);
    }
    
    @Test
    void testAddMaterialTrimsWhitespace() {
        Material material = testAdmin.addMaterial("  ABS  ", 0.025, 230, "  Black  ");
        assertNotNull(material);
        assertEquals("ABS", material.getName());
        assertEquals("Black", material.getColor());
    }
    
    @Test
    void testAddMaterialSetsDefaultStock() {
        Material material = testAdmin.addMaterial("ABS", 0.025, 230, "Black");
        assertNotNull(material);
        assertEquals(1000, Inventory.getStock(material));
    }
    
    // viewAllOrders Tests
    @Test
    void testViewAllOrdersEmpty() {
        Order[] orders = testAdmin.viewAllOrders();
        assertNotNull(orders);
        assertEquals(0, orders.length);
    }
    
    @Test
    void testViewAllOrdersWithOrders() {
        // Create some orders
        User customer = new User("customer", "customer@test.com", "customer");
        Inventory.setStock(testMaterial, 1000);
        
        customer.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        customer.submitOrder(testMaterial, "5cm x 5cm x 5cm", 1, "Standard");
        
        Order[] orders = testAdmin.viewAllOrders();
        assertNotNull(orders);
        assertEquals(2, orders.length);
    }
    
    // modifyPricingConstants Tests
    @Test
    void testModifyPricingConstantsValidInput() {
        boolean result = testAdmin.modifyPricingConstants(0.20, 3.00, 7.50);
        assertTrue(result);
        
        assertEquals(0.20, SystemConfig.getElectricityCostPerHour());
        assertEquals(3.00, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(7.50, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testModifyPricingConstantsWithZeroValues() {
        boolean result = testAdmin.modifyPricingConstants(0.0, 0.0, 0.0);
        assertTrue(result);
        
        assertEquals(0.0, SystemConfig.getElectricityCostPerHour());
        assertEquals(0.0, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(0.0, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testModifyPricingConstantsWithNegativeValues() {
        // SystemConfig should handle negative values by not updating
        double originalElectricity = SystemConfig.getElectricityCostPerHour();
        double originalMachineTime = SystemConfig.getMachineTimeCostPerHour();
        double originalBaseSetup = SystemConfig.getBaseSetupCost();
        
        boolean result = testAdmin.modifyPricingConstants(-0.20, -3.00, -7.50);
        assertTrue(result); // Method returns true, but values shouldn't change
        
        assertEquals(originalElectricity, SystemConfig.getElectricityCostPerHour());
        assertEquals(originalMachineTime, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(originalBaseSetup, SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testModifyPricingConstantsWithVeryLargeValues() {
        boolean result = testAdmin.modifyPricingConstants(1000.0, 2000.0, 3000.0);
        assertTrue(result);
        
        assertEquals(1000.0, SystemConfig.getElectricityCostPerHour());
        assertEquals(2000.0, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(3000.0, SystemConfig.getBaseSetupCost());
    }
    
    // toString Tests
    @Test
    void testToString() {
        String result = testAdmin.toString();
        assertTrue(result.contains("adminuser"));
        assertTrue(result.contains("admin@example.com"));
        assertTrue(result.contains("admin"));
    }
    
    @Test
    void testToStringWithNullValues() {
        AdminUser admin = new AdminUser(null, null);
        String result = admin.toString();
        assertTrue(result.contains("null"));
        assertTrue(result.contains("admin"));
    }
    
    // Edge Cases
    @Test
    void testAddMaterialWithSpecialCharacters() {
        Material material = testAdmin.addMaterial("PLA+", 0.025, 230, "Black/White");
        assertNotNull(material);
        assertEquals("PLA+", material.getName());
        assertEquals("Black/White", material.getColor());
    }
    
    @Test
    void testAddMaterialWithVeryLongName() {
        String longName = "Very Long Material Name That Exceeds Normal Length";
        Material material = testAdmin.addMaterial(longName, 0.025, 230, "Black");
        assertNotNull(material);
        assertEquals(longName, material.getName());
    }
    
    @Test
    void testAddMaterialWithVeryHighCost() {
        Material material = testAdmin.addMaterial("Expensive", 999.99, 230, "Gold");
        assertNotNull(material);
        assertEquals(999.99, material.getCostPerGram());
    }
    
    @Test
    void testAddMaterialWithVeryHighTemperature() {
        Material material = testAdmin.addMaterial("HighTemp", 0.025, 1000, "Black");
        assertNotNull(material);
        assertEquals(1000, material.getPrintTemp());
    }
    
    @Test
    void testModifyPricingConstantsWithDecimalPrecision() {
        boolean result = testAdmin.modifyPricingConstants(0.123456789, 2.987654321, 5.555555555);
        assertTrue(result);
        
        assertEquals(0.123456789, SystemConfig.getElectricityCostPerHour());
        assertEquals(2.987654321, SystemConfig.getMachineTimeCostPerHour());
        assertEquals(5.555555555, SystemConfig.getBaseSetupCost());
    }
}
