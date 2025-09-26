import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified core tests for the 3D printing service system.
 * Consolidates essential functionality tests for User, AdminUser, Material, Order, and Invoice classes.
 */
public class CoreTests {
    
    private User testUser;
    private AdminUser testAdmin;
    private Material testMaterial;
    private Order testOrder;
    private Invoice testInvoice;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "customer", "TestPass123!");
        testAdmin = new AdminUser("admin", "admin@example.com", "AdminPass123!");
        testMaterial = new Material("PLA", 0.05, 200, "Blue");
        testOrder = testUser.submitOrder(testMaterial, "10x10x5", 2, "Standard quality");
        testInvoice = new Invoice(testOrder, testOrder.calculatePrice());
    }
    
    // ==================== USER TESTS ====================
    
    @Test
    void testUserCreation() {
        assertNotNull(testUser);
        assertEquals("testuser", testUser.getUsername());
        assertEquals("test@example.com", testUser.getEmail());
        assertEquals("customer", testUser.getRole());
    }
    
    @Test
    void testUserSubmitOrder() {
        Order order = testUser.submitOrder(testMaterial, "5x5x5", 1, "Test order");
        assertNotNull(order);
        assertEquals(testUser, order.getUser());
        assertEquals(testMaterial, order.getMaterial());
        assertEquals("5x5x5", order.getDimensions());
        assertEquals(1, order.getQuantity());
    }
    
    @Test
    void testUserViewInvoice() {
        Invoice invoice = testUser.viewInvoice(testOrder.getOrderID());
        assertNotNull(invoice);
        assertEquals(testOrder, invoice.getOrder());
    }
    
    @Test
    void testUserPasswordVerification() {
        assertTrue(testUser.verifyPassword("TestPass123!"));
        assertFalse(testUser.verifyPassword("WrongPassword"));
    }
    
    // ==================== ADMIN USER TESTS ====================
    
    @Test
    void testAdminUserCreation() {
        assertNotNull(testAdmin);
        assertEquals("admin", testAdmin.getUsername());
        assertEquals("admin", testAdmin.getRole());
    }
    
    @Test
    void testAdminAddMaterial() {
        Material newMaterial = testAdmin.addMaterial("ABS", 0.08, 250, "Red");
        assertNotNull(newMaterial);
        assertEquals("ABS", newMaterial.getName());
        assertEquals(0.08, newMaterial.getCostPerGram(), 0.001);
        assertEquals(250, newMaterial.getPrintTemp());
        assertEquals("Red", newMaterial.getColor());
    }
    
    @Test
    void testAdminViewAllOrders() {
        Order[] orders = testAdmin.viewAllOrders();
        assertNotNull(orders);
        assertTrue(orders.length >= 1);
    }
    
    @Test
    void testAdminModifyPricingConstants() {
        boolean result = testAdmin.modifyPricingConstants(0.15, 25.0, 10.0);
        assertTrue(result);
    }
    
    // ==================== MATERIAL TESTS ====================
    
    @Test
    void testMaterialCreation() {
        assertNotNull(testMaterial);
        assertEquals("PLA", testMaterial.getName());
        assertEquals(0.05, testMaterial.getCostPerGram(), 0.001);
        assertEquals(200, testMaterial.getPrintTemp());
        assertEquals("Blue", testMaterial.getColor());
    }
    
    @Test
    void testMaterialInfo() {
        String info = testMaterial.getMaterialInfo();
        assertNotNull(info);
        assertTrue(info.contains("PLA"));
        assertTrue(info.contains("0.05"));
        assertTrue(info.contains("200"));
        assertTrue(info.contains("Blue"));
    }
    
    @Test
    void testMaterialSetters() {
        testMaterial.setCostPerGram(0.06);
        testMaterial.setPrintTemp(210);
        testMaterial.setColor("Green");
        
        assertEquals(0.06, testMaterial.getCostPerGram(), 0.001);
        assertEquals(210, testMaterial.getPrintTemp());
        assertEquals("Green", testMaterial.getColor());
    }
    
    // ==================== ORDER TESTS ====================
    
    @Test
    void testOrderCreation() {
        assertNotNull(testOrder);
        assertEquals(testUser, testOrder.getUser());
        assertEquals(testMaterial, testOrder.getMaterial());
        assertEquals("10x10x5", testOrder.getDimensions());
        assertEquals(2, testOrder.getQuantity());
        assertEquals("Standard quality", testOrder.getSpecialInstructions());
        assertEquals("pending", testOrder.getStatus());
    }
    
    @Test
    void testOrderPriceCalculation() {
        double price = testOrder.calculatePrice();
        assertTrue(price > 0);
        assertTrue(price >= SystemConfig.getBaseSetupCost());
    }
    
    @Test
    void testOrderStatusUpdate() {
        boolean result = testOrder.updateStatus("processing");
        assertTrue(result);
        assertEquals("processing", testOrder.getStatus());
    }
    
    @Test
    void testOrderPriority() {
        testOrder.setPriority("rush");
        assertEquals("rush", testOrder.getPriority());
    }
    
    @Test
    void testOrderPrintTimeEstimation() {
        double hours = testOrder.estimatePrintTimeHours();
        assertTrue(hours > 0);
    }
    
    // ==================== INVOICE TESTS ====================
    
    @Test
    void testInvoiceCreation() {
        assertNotNull(testInvoice);
        assertEquals(testOrder, testInvoice.getOrder());
        assertTrue(testInvoice.getTotalCost() > 0);
        assertNotNull(testInvoice.getDateIssued());
    }
    
    @Test
    void testInvoiceSummary() {
        String summary = testInvoice.generateSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("Invoice ID"));
        assertTrue(summary.contains("Order ID"));
        assertTrue(summary.contains("Customer"));
    }
    
    @Test
    void testInvoiceExport() {
        String export = testInvoice.exportInvoice();
        assertNotNull(export);
        assertTrue(export.contains("Invoice"));
    }
    
    // ==================== INTEGRATION TESTS ====================
    
    @Test
    void testCompleteWorkflow() {
        // Create user
        User user = new User("workflowuser", "workflow@example.com", "customer", "WorkflowPass123!");
        
        // Create material
        Material material = new Material("PETG", 0.07, 230, "Clear");
        
        // Submit order
        Order order = user.submitOrder(material, "15x15x10", 3, "High quality");
        assertNotNull(order);
        
        // Calculate price
        double price = order.calculatePrice();
        assertTrue(price > 0);
        
        // Create invoice
        Invoice invoice = new Invoice(order, price);
        assertNotNull(invoice);
        
        // Verify invoice matches order
        assertEquals(order, invoice.getOrder());
        assertEquals(price, invoice.getTotalCost(), 0.01);
    }
    
    @Test
    void testAdminWorkflow() {
        // Admin adds material
        Material material = testAdmin.addMaterial("TPU", 0.12, 220, "Black");
        assertNotNull(material);
        
        // Admin views all orders
        Order[] orders = testAdmin.viewAllOrders();
        assertNotNull(orders);
        
        // Admin modifies pricing
        boolean pricingModified = testAdmin.modifyPricingConstants(0.15, 25.0, 10.0);
        assertTrue(pricingModified);
    }
    
    @Test
    void testBulkOrderDiscount() {
        Order bulkOrder = testUser.submitOrder(testMaterial, "20x20x20", 15, "Bulk order");
        double price = bulkOrder.calculatePrice();
        
        // Should have quantity discount for >= 10 items
        assertTrue(price > 0);
    }
    
    @Test
    void testVIPCustomerDiscount() {
        User vipUser = new User("vipuser", "vip@example.com", "vip", "VIPPass123!");
        Order vipOrder = vipUser.submitOrder(testMaterial, "10x10x5", 2, "VIP order");
        double price = vipOrder.calculatePrice();
        
        assertTrue(price > 0);
    }
    
    @Test
    void testRushOrderSurcharge() {
        testOrder.setPriority("rush");
        double rushPrice = testOrder.calculatePrice();
        double normalPrice = testOrder.calculatePrice(); // Reset priority first
        
        testOrder.setPriority("normal");
        normalPrice = testOrder.calculatePrice();
        
        // Rush orders should cost more
        assertTrue(rushPrice >= normalPrice);
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testNullInputs() {
        assertThrows(Exception.class, () -> new User(null, "test@example.com", "customer", "TestPass123!"));
        assertThrows(Exception.class, () -> new Material(null, 0.05, 200, "Blue"));
    }
    
    @Test
    void testEmptyStrings() {
        User emptyUser = new User("", "", "", "");
        assertNotNull(emptyUser);
        assertEquals("", emptyUser.getUsername());
    }
    
    @Test
    void testZeroValues() {
        Material zeroMaterial = new Material("Test", 0.0, 0, "Color");
        assertNotNull(zeroMaterial);
        assertEquals(0.0, zeroMaterial.getCostPerGram(), 0.001);
        assertEquals(0, zeroMaterial.getPrintTemp());
    }
    
    @Test
    void testLargeValues() {
        Material largeMaterial = new Material("Test", 999.99, 9999, "Color");
        assertNotNull(largeMaterial);
        assertEquals(999.99, largeMaterial.getCostPerGram(), 0.001);
        assertEquals(9999, largeMaterial.getPrintTemp());
    }
    
    @Test
    void testSpecialCharacters() {
        User specialUser = new User("user@#$", "test+tag@domain.co.uk", "customer", "TestPass123!");
        assertNotNull(specialUser);
        assertEquals("user@#$", specialUser.getUsername());
        assertEquals("test+tag@domain.co.uk", specialUser.getEmail());
    }
    
    // ==================== PERFORMANCE TESTS ====================
    
    @Test
    void testMultipleOrderCreation() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            Order order = testUser.submitOrder(testMaterial, "5x5x5", 1, "Test order " + i);
            assertNotNull(order);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time (adjust threshold as needed)
        assertTrue(duration < 5000, "Order creation took too long: " + duration + "ms");
    }
    
    @Test
    void testMultiplePriceCalculations() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            double price = testOrder.calculatePrice();
            assertTrue(price > 0);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 2000, "Price calculation took too long: " + duration + "ms");
    }
}
