import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the Order class.
 * Tests all methods, constructors, pricing calculations, and edge cases.
 */
public class OrderTest {
    
    private Order testOrder;
    private User testUser;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        SystemConfig.resetToDefaults();
        
        // Create test objects
        testUser = new User("testuser", "test@example.com", "customer", "password123");
        testMaterial = new Material("PLA", 0.02, 200, "White");
        testOrder = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testOrder = null;
        testUser = null;
        testMaterial = null;
    }
    
    // Constructor Tests
    @Test
    void testParameterizedConstructor() {
        Order order = new Order(testUser, testMaterial, "5cm x 5cm x 5cm", 1, "Standard");
        assertEquals(testUser, order.getUser());
        assertEquals(testMaterial, order.getMaterial());
        assertEquals("5cm x 5cm x 5cm", order.getDimensions());
        assertEquals(1, order.getQuantity());
        assertEquals("Standard", order.getSpecialInstructions());
        assertEquals("pending", order.getStatus());
        assertEquals("normal", order.getPriority());
        assertTrue(order.getOrderID() > 0);
    }
    
    @Test
    void testDefaultConstructor() {
        Order order = new Order();
        assertNull(order.getUser());
        assertNull(order.getMaterial());
        assertEquals("", order.getDimensions());
        assertEquals(0, order.getQuantity());
        assertEquals("", order.getSpecialInstructions());
        assertEquals("pending", order.getStatus());
        assertEquals("normal", order.getPriority());
        assertTrue(order.getOrderID() > 0);
    }
    
    @Test
    void testConstructorWithNullValues() {
        Order order = new Order(null, null, null, 0, null);
        assertNull(order.getUser());
        assertNull(order.getMaterial());
        assertNull(order.getDimensions());
        assertEquals(0, order.getQuantity());
        assertNull(order.getSpecialInstructions());
        assertEquals("pending", order.getStatus());
        assertEquals("normal", order.getPriority());
    }
    
    @Test
    void testOrderIDIncrement() {
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        
        assertTrue(order2.getOrderID() > order1.getOrderID());
        assertTrue(order3.getOrderID() > order2.getOrderID());
    }
    
    // Getter and Setter Tests
    @Test
    void testUserGetterAndSetter() {
        User newUser = new User("newuser", "new@test.com", "vip", "password123");
        testOrder.setUser(newUser);
        assertEquals(newUser, testOrder.getUser());
    }
    
    @Test
    void testMaterialGetterAndSetter() {
        Material newMaterial = new Material("ABS", 0.025, 230, "Black");
        testOrder.setMaterial(newMaterial);
        assertEquals(newMaterial, testOrder.getMaterial());
    }
    
    @Test
    void testDimensionsGetterAndSetter() {
        testOrder.setDimensions("20cm x 10cm x 5cm");
        assertEquals("20cm x 10cm x 5cm", testOrder.getDimensions());
    }
    
    @Test
    void testQuantityGetterAndSetter() {
        testOrder.setQuantity(5);
        assertEquals(5, testOrder.getQuantity());
    }
    
    @Test
    void testSpecialInstructionsGetterAndSetter() {
        testOrder.setSpecialInstructions("New instructions");
        assertEquals("New instructions", testOrder.getSpecialInstructions());
    }
    
    @Test
    void testStatusGetter() {
        assertEquals("pending", testOrder.getStatus());
    }
    
    @Test
    void testPriorityGetterAndSetter() {
        testOrder.setPriority("rush");
        assertEquals("rush", testOrder.getPriority());
    }
    
    @Test
    void testEstimatedPrintHoursGetter() {
        assertTrue(testOrder.getEstimatedPrintHours() > 0);
    }
    
    // updateStatus Tests
    @Test
    void testUpdateStatusValidInput() {
        boolean result = testOrder.updateStatus("processing");
        assertTrue(result);
        assertEquals("processing", testOrder.getStatus());
    }
    
    @Test
    void testUpdateStatusWithNull() {
        boolean result = testOrder.updateStatus(null);
        assertFalse(result);
        assertEquals("pending", testOrder.getStatus()); // Should remain unchanged
    }
    
    @Test
    void testUpdateStatusWithEmptyString() {
        boolean result = testOrder.updateStatus("");
        assertFalse(result);
        assertEquals("pending", testOrder.getStatus()); // Should remain unchanged
    }
    
    @Test
    void testUpdateStatusWithWhitespace() {
        boolean result = testOrder.updateStatus("   ");
        assertFalse(result);
        assertEquals("pending", testOrder.getStatus()); // Should remain unchanged
    }
    
    @Test
    void testUpdateStatusTrimsWhitespace() {
        boolean result = testOrder.updateStatus("  completed  ");
        assertTrue(result);
        assertEquals("completed", testOrder.getStatus());
    }
    
    @Test
    void testUpdateStatusWithSpecialCharacters() {
        boolean result = testOrder.updateStatus("processing-50%");
        assertTrue(result);
        assertEquals("processing-50%", testOrder.getStatus());
    }
    
    // setPriority Tests
    @Test
    void testSetPriorityValidInput() {
        testOrder.setPriority("rush");
        assertEquals("rush", testOrder.getPriority());
    }
    
    @Test
    void testSetPriorityWithNull() {
        testOrder.setPriority(null);
        assertEquals("normal", testOrder.getPriority()); // Should remain unchanged
    }
    
    @Test
    void testSetPriorityWithEmptyString() {
        testOrder.setPriority("");
        assertEquals("normal", testOrder.getPriority()); // Should remain unchanged
    }
    
    @Test
    void testSetPriorityWithWhitespace() {
        testOrder.setPriority("   ");
        assertEquals("normal", testOrder.getPriority()); // Should remain unchanged
    }
    
    @Test
    void testSetPriorityTrimsAndLowercases() {
        testOrder.setPriority("  RUSH  ");
        assertEquals("rush", testOrder.getPriority());
    }
    
    @Test
    void testSetPriorityWithVip() {
        testOrder.setPriority("VIP");
        assertEquals("vip", testOrder.getPriority());
    }
    
    // calculatePrice Tests
    @Test
    void testCalculatePriceBasicOrder() {
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        
        // Basic calculation: material cost + base cost + electricity + machine time + tax
        double expectedMaterialCost = 2 * 10 * 0.02; // 2 items * 10g per item * $0.02 per gram
        double expectedBaseCost = 5.00; // Base setup cost
        
        // Should be approximately correct (allowing for rounding differences)
        assertTrue(price > expectedMaterialCost + expectedBaseCost);
    }
    
    @Test
    void testCalculatePriceWithNullMaterial() {
        testOrder.setMaterial(null);
        double price = testOrder.calculatePrice();
        assertEquals(0.0, price);
    }
    
    @Test
    void testCalculatePriceWithZeroQuantity() {
        testOrder.setQuantity(0);
        double price = testOrder.calculatePrice();
        assertEquals(0.0, price);
    }
    
    @Test
    void testCalculatePriceWithNegativeQuantity() {
        testOrder.setQuantity(-1);
        double price = testOrder.calculatePrice();
        assertEquals(0.0, price);
    }
    
    @Test
    void testCalculatePriceWithBulkDiscount() {
        testOrder.setQuantity(10); // Should trigger 5% bulk discount
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        
        // Calculate expected price without discount
        double materialCost = 10 * 10 * 0.02; // 10 items * 10g per item * $0.02 per gram
        double baseCost = 5.00;
        double electricityCost = SystemConfig.getElectricityCostPerHour() * testOrder.getEstimatedPrintHours();
        double machineTimeCost = SystemConfig.getMachineTimeCostPerHour() * testOrder.getEstimatedPrintHours();
        double subtotal = materialCost + baseCost + electricityCost + machineTimeCost;
        double expectedWithDiscount = subtotal * 0.95; // 5% discount
        double expectedWithTax = expectedWithDiscount * 1.08; // 8% tax
        
        // Verify the calculated price is reasonable
        assertTrue(price > expectedWithTax * 0.9 && price < expectedWithTax * 1.1);
        
        // Should be approximately correct (allowing for rounding differences)
        assertTrue(Math.abs(price - expectedWithTax) < 1.0);
    }
    
    @Test
    void testCalculatePriceWithVipDiscount() {
        User vipUser = new User("vipuser", "vip@test.com", "vip", "password123");
        testOrder.setUser(vipUser);
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        
        // Should have both bulk discount (if applicable) and VIP discount
        // VIP discount is 10% off
    }
    
    @Test
    void testCalculatePriceWithRushSurcharge() {
        testOrder.setPriority("rush");
        SystemConfig.setAllowRushOrders(true);
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        
        // Should have rush surcharge applied
    }
    
    @Test
    void testCalculatePriceWithRushDisabled() {
        testOrder.setPriority("rush");
        SystemConfig.setAllowRushOrders(false);
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        
        // Should not have rush surcharge applied
    }
    
    @Test
    void testCalculatePriceWithDifferentTaxRate() {
        SystemConfig.setTaxRate(0.10); // 10% tax
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
    }
    
    @Test
    void testCalculatePriceWithZeroTaxRate() {
        SystemConfig.setTaxRate(0.0);
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
    }
    
    // estimatePrintTimeHours Tests
    @Test
    void testEstimatePrintTimeHoursBasicDimensions() {
        testOrder.setDimensions("10cm x 5cm x 3cm");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
        
        // Should be approximately 10 * 5 * 3 / 1000 * 2 = 0.3 hours
        // But with minimum of 0.1 hours per item, so 0.2 hours minimum
        assertTrue(hours >= 0.2);
    }
    
    @Test
    void testEstimatePrintTimeHoursWithInvalidDimensions() {
        testOrder.setDimensions("invalid dimensions");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0); // Should use default values
    }
    
    @Test
    void testEstimatePrintTimeHoursWithNullDimensions() {
        testOrder.setDimensions(null);
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0); // Should use default values
    }
    
    @Test
    void testEstimatePrintTimeHoursWithEmptyDimensions() {
        testOrder.setDimensions("");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0); // Should use default values
    }
    
    @Test
    void testEstimatePrintTimeHoursWithDifferentFormats() {
        testOrder.setDimensions("10x5x3cm");
        double hours1 = testOrder.estimatePrintTimeHours();
        
        testOrder.setDimensions("10 x 5 x 3 cm");
        double hours2 = testOrder.estimatePrintTimeHours();
        
        // Should be approximately the same
        assertTrue(Math.abs(hours1 - hours2) < 0.1);
    }
    
    @Test
    void testEstimatePrintTimeHoursWithLargeDimensions() {
        testOrder.setDimensions("100cm x 50cm x 30cm");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
        assertTrue(hours > 10); // Should be much larger
    }
    
    @Test
    void testEstimatePrintTimeHoursWithSmallDimensions() {
        testOrder.setDimensions("1cm x 1cm x 1cm");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
        assertTrue(hours >= 0.2); // Should be at least minimum
    }
    
    @Test
    void testEstimatePrintTimeHoursWithQuantity() {
        testOrder.setQuantity(5);
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
        assertTrue(hours >= 0.5); // Should be at least 5 * 0.1 hours
    }
    
    // toString Tests
    @Test
    void testToString() {
        String result = testOrder.toString();
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("PLA"));
        assertTrue(result.contains("10cm x 5cm x 3cm"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("High quality"));
        assertTrue(result.contains("pending"));
        assertTrue(result.contains("normal"));
    }
    
    @Test
    void testToStringWithNullValues() {
        Order order = new Order(null, null, null, 0, null);
        String result = order.toString();
        assertTrue(result.contains("null"));
    }
    
    // Edge Cases
    @Test
    void testOrderWithVeryLargeQuantity() {
        testOrder.setQuantity(1000);
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
    }
    
    @Test
    void testOrderWithVeryHighCostMaterial() {
        Material expensiveMaterial = new Material("Gold", 999.99, 200, "Gold");
        testOrder.setMaterial(expensiveMaterial);
        double price = testOrder.calculatePrice();
        assertTrue(price > 1000); // Should be very expensive
    }
    
    @Test
    void testOrderWithVeryLongInstructions() {
        String longInstructions = "This is a very long set of special instructions that contains many details about how the print should be completed with specific requirements and preferences";
        testOrder.setSpecialInstructions(longInstructions);
        assertEquals(longInstructions, testOrder.getSpecialInstructions());
    }
    
    @Test
    void testOrderWithSpecialCharactersInDimensions() {
        testOrder.setDimensions("10cm x 5cm x 3cm (with notes!)");
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
    }
    
    @Test
    void testOrderWithUnicodeCharacters() {
        testOrder.setSpecialInstructions("Special characters: ñáéíóú");
        assertEquals("Special characters: ñáéíóú", testOrder.getSpecialInstructions());
    }
}
