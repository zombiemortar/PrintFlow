import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the User class.
 * Tests all methods, constructors, and edge cases.
 */
public class UserTest {
    
    private User testUser;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        SystemConfig.resetToDefaults();
        
        // Create test user
        testUser = new User("testuser", "test@example.com", "customer");
        
        // Create test material
        testMaterial = new Material("PLA", 0.02, 200, "White");
        
        // Set up inventory
        Inventory.setStock(testMaterial, 1000);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testUser = null;
        testMaterial = null;
    }
    
    // Constructor Tests
    @Test
    void testParameterizedConstructor() {
        User user = new User("john", "john@test.com", "customer");
        assertEquals("john", user.getUsername());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("customer", user.getRole());
    }
    
    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("customer", user.getRole());
    }
    
    @Test
    void testConstructorWithNullValues() {
        User user = new User(null, null, null);
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getRole());
    }
    
    // Getter and Setter Tests
    @Test
    void testUsernameGetterAndSetter() {
        testUser.setUsername("newusername");
        assertEquals("newusername", testUser.getUsername());
    }
    
    @Test
    void testEmailGetterAndSetter() {
        testUser.setEmail("newemail@test.com");
        assertEquals("newemail@test.com", testUser.getEmail());
    }
    
    @Test
    void testRoleGetterAndSetter() {
        testUser.setRole("vip");
        assertEquals("vip", testUser.getRole());
    }
    
    @Test
    void testSetUsernameWithNull() {
        testUser.setUsername(null);
        assertNull(testUser.getUsername());
    }
    
    @Test
    void testSetEmailWithNull() {
        testUser.setEmail(null);
        assertNull(testUser.getEmail());
    }
    
    @Test
    void testSetRoleWithNull() {
        testUser.setRole(null);
        assertNull(testUser.getRole());
    }
    
    // submitOrder Tests
    @Test
    void testSubmitOrderValidInput() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        assertNotNull(order);
        assertEquals(testUser, order.getUser());
        assertEquals(testMaterial, order.getMaterial());
        assertEquals("10cm x 5cm x 3cm", order.getDimensions());
        assertEquals(2, order.getQuantity());
        assertEquals("High quality", order.getSpecialInstructions());
        assertEquals("pending", order.getStatus());
    }
    
    @Test
    void testSubmitOrderWithNullMaterial() {
        Order order = testUser.submitOrder(null, "10cm x 5cm x 3cm", 2, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderWithNullDimensions() {
        Order order = testUser.submitOrder(testMaterial, null, 2, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderWithEmptyDimensions() {
        Order order = testUser.submitOrder(testMaterial, "   ", 2, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderWithZeroQuantity() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 0, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderWithNegativeQuantity() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", -1, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderExceedsMaxQuantity() {
        SystemConfig.setMaxOrderQuantity(5);
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 10, "High quality");
        assertNull(order);
    }
    
    @Test
    void testSubmitOrderInsufficientMaterial() {
        Inventory.setStock(testMaterial, 5); // Only 5 grams available
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        assertNull(order); // Needs 20 grams (2 * 10g per item)
    }
    
    @Test
    void testSubmitOrderWithRushInstructions() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "RUSH - need ASAP");
        assertNotNull(order);
        assertEquals("rush", order.getPriority());
    }
    
    @Test
    void testSubmitOrderWithNullInstructions() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, null);
        assertNotNull(order);
        assertEquals("normal", order.getPriority());
    }
    
    @Test
    void testSubmitOrderConsumesInventory() {
        int initialStock = Inventory.getStock(testMaterial);
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        assertNotNull(order);
        int finalStock = Inventory.getStock(testMaterial);
        assertEquals(initialStock - 20, finalStock); // 2 items * 10g per item
    }
    
    // viewInvoice Tests
    @Test
    void testViewInvoiceReturnsNull() {
        // Currently returns null as placeholder
        Invoice invoice = testUser.viewInvoice(123);
        assertNull(invoice);
    }
    
    // toString Tests
    @Test
    void testToString() {
        String result = testUser.toString();
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("customer"));
    }
    
    @Test
    void testToStringWithNullValues() {
        User user = new User(null, null, null);
        String result = user.toString();
        assertTrue(result.contains("null"));
    }
    
    // Edge Cases
    @Test
    void testSubmitOrderWithVeryLargeQuantity() {
        SystemConfig.setMaxOrderQuantity(1000);
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 1000, "High quality");
        assertNull(order); // Should fail due to insufficient material
    }
    
    @Test
    void testSubmitOrderWithSpecialCharactersInDimensions() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm with special chars!@#", 2, "High quality");
        assertNotNull(order);
        assertEquals("10cm x 5cm x 3cm with special chars!@#", order.getDimensions());
    }
    
    @Test
    void testSubmitOrderWithEmptyStringInstructions() {
        Order order = testUser.submitOrder(testMaterial, "10cm x 5cm x 3cm", 2, "");
        assertNotNull(order);
        assertEquals("", order.getSpecialInstructions());
    }
}
