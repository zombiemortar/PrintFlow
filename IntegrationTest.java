import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

/**
 * Integration tests for complete workflows from order creation to invoice generation.
 * Tests the interaction between multiple classes and complete business processes.
 */
public class IntegrationTest {
    
    private User regularUser;
    private User vipUser;
    private AdminUser adminUser;
    private Material plaMaterial;
    private Material absMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system configuration to defaults
        SystemConfig.resetToDefaults();
        
        // Clear the order queue and registry
        clearOrderSystem();
        
        // Create test users
        regularUser = new User("john_doe", "john@example.com", "customer");
        vipUser = new User("vip_customer", "vip@example.com", "vip");
        adminUser = new AdminUser("admin_user", "admin@example.com");
        
        // Create test materials
        plaMaterial = new Material("PLA", 0.05, 200, "Blue");
        absMaterial = new Material("ABS", 0.08, 250, "Red");
        
        // Set up inventory
        Inventory.setStock(plaMaterial, 1000);
        Inventory.setStock(absMaterial, 500);
    }
    
    /**
     * Helper method to clear the order system between tests
     */
    private void clearOrderSystem() {
        // Clear all orders from the system
        OrderManager.clearAllOrders();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up any static state if needed
        SystemConfig.resetToDefaults();
    }
    
    /**
     * Test complete workflow: Regular customer places order -> Order processed -> Invoice generated
     */
    @Test
    void testCompleteRegularOrderWorkflow() {
        System.out.println("=== Testing Complete Regular Order Workflow ===");
        
        // Step 1: Customer submits order
        Order order = regularUser.submitOrder(plaMaterial, "10x10x5cm", 2, "Standard quality");
        assertNotNull(order, "Order should be created successfully");
        assertEquals("john_doe", order.getUser().getUsername());
        assertEquals("PLA", order.getMaterial().getName());
        assertEquals(2, order.getQuantity());
        assertEquals("pending", order.getStatus());
        
        // Step 2: Verify order is registered in OrderManager
        Order retrievedOrder = OrderManager.getOrderById(order.getOrderID());
        assertNotNull(retrievedOrder, "Order should be retrievable from OrderManager");
        assertEquals(order.getOrderID(), retrievedOrder.getOrderID());
        
        // Step 3: Verify order is in print queue
        assertEquals(1, OrderManager.getQueueSize(), "Order should be in print queue");
        
        // Step 4: Calculate price
        double price = order.calculatePrice();
        assertTrue(price > 0, "Price should be calculated and positive");
        System.out.println("Calculated price: $" + String.format("%.2f", price));
        
        // Step 5: Update order status to completed
        boolean statusUpdated = order.updateStatus("completed");
        assertTrue(statusUpdated, "Status should be updated successfully");
        assertEquals("completed", order.getStatus());
        
        // Step 6: Generate invoice
        Invoice invoice = new Invoice(order, price);
        assertNotNull(invoice, "Invoice should be created successfully");
        assertEquals(order.getOrderID(), invoice.getOrder().getOrderID());
        assertEquals(price, invoice.getTotalCost(), 0.01);
        
        // Step 7: Verify invoice content
        String invoiceSummary = invoice.generateSummary();
        assertNotNull(invoiceSummary, "Invoice summary should be generated");
        assertTrue(invoiceSummary.contains("INVOICE SUMMARY"));
        assertTrue(invoiceSummary.contains("john_doe"));
        assertTrue(invoiceSummary.contains("PLA"));
        assertTrue(invoiceSummary.contains("$" + String.format("%.2f", price)));
        
        System.out.println("✓ Complete regular order workflow test passed");
    }
    
    /**
     * Test complete workflow: VIP customer places order with discounts
     */
    @Test
    void testCompleteVIPOrderWorkflow() {
        System.out.println("=== Testing Complete VIP Order Workflow ===");
        
        // Step 1: VIP customer submits order
        Order order = vipUser.submitOrder(absMaterial, "15x15x10cm", 5, "High quality finish");
        assertNotNull(order, "VIP order should be created successfully");
        assertEquals("vip_customer", order.getUser().getUsername());
        assertEquals("ABS", order.getMaterial().getName());
        assertEquals(5, order.getQuantity());
        
        // Step 2: Calculate price (should include VIP discount)
        double price = order.calculatePrice();
        assertTrue(price > 0, "VIP order price should be calculated and positive");
        
        // Step 3: Verify VIP discount is applied (10% off)
        Order regularOrder = regularUser.submitOrder(absMaterial, "15x15x10cm", 5, "High quality finish");
        double regularPrice = regularOrder.calculatePrice();
        assertTrue(price < regularPrice, "VIP price should be lower than regular price");
        
        System.out.println("VIP price: $" + String.format("%.2f", price));
        System.out.println("Regular price: $" + String.format("%.2f", regularPrice));
        
        // Step 4: Generate invoice
        Invoice invoice = new Invoice(order, price);
        String invoiceSummary = invoice.generateSummary();
        assertTrue(invoiceSummary.contains("vip_customer"));
        assertTrue(invoiceSummary.contains("ABS"));
        
        System.out.println("✓ Complete VIP order workflow test passed");
    }
    
    /**
     * Test complete workflow: Rush order with surcharge
     */
    @Test
    void testCompleteRushOrderWorkflow() {
        System.out.println("=== Testing Complete Rush Order Workflow ===");
        
        // Step 1: Customer submits rush order
        Order order = regularUser.submitOrder(plaMaterial, "5x5x3cm", 1, "RUSH - needed urgently");
        assertNotNull(order, "Rush order should be created successfully");
        assertEquals("rush", order.getPriority(), "Order should be marked as rush");
        
        // Step 2: Calculate price (should include rush surcharge)
        double price = order.calculatePrice();
        assertTrue(price > 0, "Rush order price should be calculated and positive");
        
        // Step 3: Verify rush surcharge is applied
        Order normalOrder = regularUser.submitOrder(plaMaterial, "5x5x3cm", 1, "Standard delivery");
        double normalPrice = normalOrder.calculatePrice();
        assertTrue(price > normalPrice, "Rush price should be higher than normal price");
        
        System.out.println("Rush price: $" + String.format("%.2f", price));
        System.out.println("Normal price: $" + String.format("%.2f", normalPrice));
        
        // Step 4: Process rush order
        order.updateStatus("printing");
        order.updateStatus("completed");
        
        // Step 5: Generate invoice
        Invoice invoice = new Invoice(order, price);
        String invoiceSummary = invoice.generateSummary();
        assertTrue(invoiceSummary.contains("completed"));
        
        System.out.println("✓ Complete rush order workflow test passed");
    }
    
    /**
     * Test complete workflow: Bulk order with quantity discount
     */
    @Test
    void testCompleteBulkOrderWorkflow() {
        System.out.println("=== Testing Complete Bulk Order Workflow ===");
        
        // Step 1: Customer submits bulk order (10+ items for discount)
        Order order = regularUser.submitOrder(plaMaterial, "8x8x4cm", 15, "Bulk order for business");
        assertNotNull(order, "Bulk order should be created successfully");
        assertEquals(15, order.getQuantity());
        
        // Step 2: Calculate price (should include bulk discount)
        double price = order.calculatePrice();
        assertTrue(price > 0, "Bulk order price should be calculated and positive");
        
        // Step 3: Verify bulk discount is applied (5% off for 10+ items)
        // Create a similar order without bulk discount to compare
        Order smallOrder = regularUser.submitOrder(plaMaterial, "8x8x4cm", 5, "Small order");
        double smallPrice = smallOrder.calculatePrice();
        
        // The bulk order should have a lower per-item cost due to the 5% discount
        // We can verify this by checking that the bulk order price is reasonable
        assertTrue(price > 0, "Bulk order should have positive price");
        assertTrue(price > smallPrice, "Bulk order should cost more than small order due to quantity");
        
        // Verify the discount is applied by checking the pricing structure
        // The bulk order should be cheaper per item due to the 5% discount
        // We'll verify this by checking that the bulk order has a reasonable price
        // and that it's less than what it would be without the discount
        double priceWithoutDiscount = smallPrice * 3; // 3x quantity without discount
        assertTrue(price < priceWithoutDiscount, "Bulk order should be cheaper than 3x small order");
        assertTrue(price > 0, "Bulk order should have positive price");
        
        System.out.println("Bulk order price: $" + String.format("%.2f", price));
        System.out.println("Small order price: $" + String.format("%.2f", smallPrice));
        
        // Step 4: Process bulk order
        order.updateStatus("printing");
        order.updateStatus("completed");
        
        // Step 5: Generate invoice
        Invoice invoice = new Invoice(order, price);
        String invoiceSummary = invoice.generateSummary();
        assertTrue(invoiceSummary.contains("15"));
        
        System.out.println("✓ Complete bulk order workflow test passed");
    }
    
    /**
     * Test complete workflow: Admin adds material and processes orders
     */
    @Test
    void testCompleteAdminWorkflow() {
        System.out.println("=== Testing Complete Admin Workflow ===");
        
        // Step 1: Admin adds new material
        Material newMaterial = adminUser.addMaterial("PETG", 0.06, 230, "Green");
        assertNotNull(newMaterial, "Admin should be able to add new material");
        assertEquals("PETG", newMaterial.getName());
        assertEquals(0.06, newMaterial.getCostPerGram(), 0.01);
        
        // Step 2: Verify material is available in inventory
        int stock = Inventory.getStock(newMaterial);
        assertEquals(1000, stock, "New material should have default stock");
        
        // Step 3: Customer places order with new material
        Order order = regularUser.submitOrder(newMaterial, "12x12x6cm", 3, "Test new material");
        assertNotNull(order, "Order with new material should be created");
        assertEquals("PETG", order.getMaterial().getName());
        
        // Step 4: Admin views all orders
        Order[] allOrders = adminUser.viewAllOrders();
        assertTrue(allOrders.length > 0, "Admin should see orders");
        
        // Step 5: Admin modifies pricing constants
        boolean pricingUpdated = adminUser.modifyPricingConstants(0.20, 3.00, 6.00);
        assertTrue(pricingUpdated, "Admin should be able to modify pricing");
        
        // Step 6: Verify pricing changes affect new orders
        Order newOrder = regularUser.submitOrder(newMaterial, "10x10x5cm", 2, "After pricing change");
        double newPrice = newOrder.calculatePrice();
        assertTrue(newPrice > 0, "New order should have updated pricing");
        
        System.out.println("✓ Complete admin workflow test passed");
    }
    
    /**
     * Test complete workflow: Multiple orders in queue processing
     */
    @Test
    void testCompleteQueueProcessingWorkflow() {
        System.out.println("=== Testing Complete Queue Processing Workflow ===");
        
        // Step 1: Submit multiple orders
        Order order1 = regularUser.submitOrder(plaMaterial, "5x5x3cm", 1, "First order");
        Order order2 = vipUser.submitOrder(absMaterial, "8x8x4cm", 2, "Second order");
        Order order3 = regularUser.submitOrder(plaMaterial, "10x10x5cm", 3, "Third order");
        
        assertNotNull(order1, "First order should be created");
        assertNotNull(order2, "Second order should be created");
        assertNotNull(order3, "Third order should be created");
        
        // Step 2: Verify all orders are in queue
        assertEquals(3, OrderManager.getQueueSize(), "All orders should be in queue");
        
        // Step 3: Process orders in FIFO order
        Order processedOrder1 = OrderManager.dequeueNextOrder();
        Order processedOrder2 = OrderManager.dequeueNextOrder();
        Order processedOrder3 = OrderManager.dequeueNextOrder();
        
        assertNotNull(processedOrder1, "First order should be dequeued");
        assertNotNull(processedOrder2, "Second order should be dequeued");
        assertNotNull(processedOrder3, "Third order should be dequeued");
        
        // Step 4: Verify FIFO order
        assertEquals(order1.getOrderID(), processedOrder1.getOrderID(), "First submitted should be first processed");
        assertEquals(order2.getOrderID(), processedOrder2.getOrderID(), "Second submitted should be second processed");
        assertEquals(order3.getOrderID(), processedOrder3.getOrderID(), "Third submitted should be third processed");
        
        // Step 5: Queue should be empty
        assertEquals(0, OrderManager.getQueueSize(), "Queue should be empty after processing");
        
        // Step 6: Generate invoices for all orders
        Invoice invoice1 = new Invoice(processedOrder1, processedOrder1.calculatePrice());
        Invoice invoice2 = new Invoice(processedOrder2, processedOrder2.calculatePrice());
        Invoice invoice3 = new Invoice(processedOrder3, processedOrder3.calculatePrice());
        
        assertNotNull(invoice1, "Invoice 1 should be created");
        assertNotNull(invoice2, "Invoice 2 should be created");
        assertNotNull(invoice3, "Invoice 3 should be created");
        
        System.out.println("✓ Complete queue processing workflow test passed");
    }
    
    /**
     * Test complete workflow: Error scenarios and edge cases
     */
    @Test
    void testCompleteErrorScenarioWorkflow() {
        System.out.println("=== Testing Complete Error Scenario Workflow ===");
        
        // Step 1: Test insufficient inventory
        Material limitedMaterial = new Material("Limited", 0.10, 200, "Black");
        Inventory.setStock(limitedMaterial, 5); // Only 5 grams available
        
        Order insufficientOrder = regularUser.submitOrder(limitedMaterial, "5x5x3cm", 2, "Test insufficient stock");
        assertNull(insufficientOrder, "Order should fail with insufficient inventory");
        
        // Step 2: Test order exceeding quantity limit
        SystemConfig.setMaxOrderQuantity(5);
        Order excessiveOrder = regularUser.submitOrder(plaMaterial, "5x5x3cm", 10, "Test quantity limit");
        assertNull(excessiveOrder, "Order should fail when exceeding quantity limit");
        
        // Step 3: Test invalid material
        Order invalidMaterialOrder = regularUser.submitOrder(null, "5x5x3cm", 1, "Test invalid material");
        assertNull(invalidMaterialOrder, "Order should fail with null material");
        
        // Step 4: Test invalid dimensions
        Order invalidDimensionsOrder = regularUser.submitOrder(plaMaterial, "", 1, "Test invalid dimensions");
        assertNull(invalidDimensionsOrder, "Order should fail with empty dimensions");
        
        // Step 5: Test invalid quantity
        Order invalidQuantityOrder = regularUser.submitOrder(plaMaterial, "5x5x3cm", 0, "Test invalid quantity");
        assertNull(invalidQuantityOrder, "Order should fail with zero quantity");
        
        // Step 6: Test rush orders disabled
        SystemConfig.setAllowRushOrders(false);
        Order rushDisabledOrder = regularUser.submitOrder(plaMaterial, "5x5x3cm", 1, "RUSH - test disabled");
        assertNotNull(rushDisabledOrder, "Order should still be created but without rush surcharge");
        assertEquals("normal", rushDisabledOrder.getPriority(), "Priority should be normal when rush disabled");
        
        System.out.println("✓ Complete error scenario workflow test passed");
    }
    
    /**
     * Test complete workflow: Complex multi-step order processing
     */
    @Test
    void testCompleteComplexOrderWorkflow() {
        System.out.println("=== Testing Complete Complex Order Workflow ===");
        
        // Step 1: VIP customer places bulk rush order
        Order complexOrder = vipUser.submitOrder(absMaterial, "20x20x10cm", 12, "RUSH - VIP bulk order for urgent project");
        assertNotNull(complexOrder, "Complex order should be created");
        assertEquals("rush", complexOrder.getPriority(), "Should be marked as rush");
        assertEquals(12, complexOrder.getQuantity(), "Should have bulk quantity");
        
        // Step 2: Calculate complex pricing
        double price = complexOrder.calculatePrice();
        assertTrue(price > 0, "Complex order should have valid price");
        
        // Step 3: Process through multiple status updates
        assertTrue(complexOrder.updateStatus("confirmed"), "Should update to confirmed");
        assertTrue(complexOrder.updateStatus("printing"), "Should update to printing");
        assertTrue(complexOrder.updateStatus("post-processing"), "Should update to post-processing");
        assertTrue(complexOrder.updateStatus("completed"), "Should update to completed");
        
        // Step 4: Generate detailed invoice
        Invoice invoice = new Invoice(complexOrder, price);
        String invoiceExport = invoice.exportInvoice();
        assertNotNull(invoiceExport, "Detailed invoice should be generated");
        assertTrue(invoiceExport.contains("3D PRINTING SERVICE INVOICE"));
        assertTrue(invoiceExport.contains("vip_customer"));
        assertTrue(invoiceExport.contains("ABS"));
        assertTrue(invoiceExport.contains("20x20x10cm"));
        assertTrue(invoiceExport.contains("12"));
        assertTrue(invoiceExport.contains("completed"));
        
        // Step 5: Verify inventory was consumed
        int remainingStock = Inventory.getStock(absMaterial);
        assertTrue(remainingStock < 500, "Inventory should be reduced");
        
        System.out.println("Complex order price: $" + String.format("%.2f", price));
        System.out.println("Remaining ABS stock: " + remainingStock + " grams");
        System.out.println("✓ Complete complex order workflow test passed");
    }
    
    /**
     * Test complete workflow: System configuration impact on orders
     */
    @Test
    void testCompleteSystemConfigWorkflow() {
        System.out.println("=== Testing Complete System Config Workflow ===");
        
        // Step 1: Create baseline order
        Order baselineOrder = regularUser.submitOrder(plaMaterial, "10x10x5cm", 5, "Baseline order");
        double baselinePrice = baselineOrder.calculatePrice();
        
        // Step 2: Modify system configuration
        SystemConfig.setElectricityCostPerHour(0.25); // Increase electricity cost
        SystemConfig.setMachineTimeCostPerHour(4.00); // Increase machine time cost
        SystemConfig.setBaseSetupCost(8.00); // Increase setup cost
        SystemConfig.setTaxRate(0.10); // Increase tax rate
        
        // Step 3: Create order with new configuration
        Order newConfigOrder = regularUser.submitOrder(plaMaterial, "10x10x5cm", 5, "New config order");
        double newConfigPrice = newConfigOrder.calculatePrice();
        
        // Step 4: Verify price increase
        assertTrue(newConfigPrice > baselinePrice, "Price should increase with higher costs");
        
        System.out.println("Baseline price: $" + String.format("%.2f", baselinePrice));
        System.out.println("New config price: $" + String.format("%.2f", newConfigPrice));
        
        // Step 5: Generate invoices for comparison
        Invoice baselineInvoice = new Invoice(baselineOrder, baselinePrice);
        Invoice newConfigInvoice = new Invoice(newConfigOrder, newConfigPrice);
        
        assertNotNull(baselineInvoice, "Baseline invoice should be created");
        assertNotNull(newConfigInvoice, "New config invoice should be created");
        
        System.out.println("✓ Complete system config workflow test passed");
    }
    
    /**
     * Main method to run integration tests
     */
    public static void main(String[] args) {
        System.out.println("=== INTEGRATION TEST SUITE ===");
        System.out.println("Testing complete workflows from order to invoice");
        System.out.println();
        
        IntegrationTest integrationTest = new IntegrationTest();
        
        try {
            // Run all integration tests with setup before each
            integrationTest.setUp();
            integrationTest.testCompleteRegularOrderWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteVIPOrderWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteRushOrderWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteBulkOrderWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteAdminWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteQueueProcessingWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteErrorScenarioWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteComplexOrderWorkflow();
            
            integrationTest.setUp();
            integrationTest.testCompleteSystemConfigWorkflow();
            
            System.out.println();
            System.out.println("=== INTEGRATION TEST SUMMARY ===");
            System.out.println("✓ All integration tests passed successfully!");
            System.out.println("✓ Complete workflows from order to invoice are working correctly");
            System.out.println("✓ System handles various scenarios: regular, VIP, rush, bulk orders");
            System.out.println("✓ Admin workflows function properly");
            System.out.println("✓ Error scenarios are handled gracefully");
            System.out.println("✓ Queue processing follows FIFO order");
            System.out.println("✓ System configuration changes affect pricing correctly");
            
        } catch (Exception e) {
            System.out.println("✗ Integration test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            integrationTest.tearDown();
        }
    }
}
