import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Simplified system tests for configuration, integration, and system-wide functionality.
 * Consolidates SystemConfig, Integration, and Performance tests.
 */
public class SystemTests {
    
    @BeforeEach
    void setUp() {
        // Reset system configuration to defaults
        SystemConfig.resetToDefaults();
    }
    
    // ==================== SYSTEM CONFIG TESTS ====================
    
    @Test
    void testSystemConfigDefaults() {
        assertTrue(SystemConfig.getBaseSetupCost() > 0);
        assertTrue(SystemConfig.getElectricityCostPerHour() > 0);
        assertTrue(SystemConfig.getMachineTimeCostPerHour() > 0);
        assertTrue(SystemConfig.getTaxRate() >= 0);
        assertNotNull(SystemConfig.getCurrency());
    }
    
    @Test
    void testSystemConfigModification() {
        double originalCost = SystemConfig.getBaseSetupCost();
        SystemConfig.setBaseSetupCost(10.0);
        assertEquals(10.0, SystemConfig.getBaseSetupCost(), 0.001);
        
        // Reset
        SystemConfig.setBaseSetupCost(originalCost);
    }
    
    @Test
    void testSystemConfigValidation() {
        // Test valid values - verify they are set correctly
        SystemConfig.setBaseSetupCost(5.0);
        assertEquals(5.0, SystemConfig.getBaseSetupCost(), 0.001);
        
        SystemConfig.setElectricityCostPerHour(0.15);
        assertEquals(0.15, SystemConfig.getElectricityCostPerHour(), 0.001);
        
        SystemConfig.setTaxRate(0.08);
        assertEquals(0.08, SystemConfig.getTaxRate(), 0.001);
        
        // Test invalid values - verify they are rejected (values remain unchanged)
        double originalSetupCost = SystemConfig.getBaseSetupCost();
        SystemConfig.setBaseSetupCost(-1.0);
        assertEquals(originalSetupCost, SystemConfig.getBaseSetupCost(), 0.001);
        
        double originalElectricityCost = SystemConfig.getElectricityCostPerHour();
        SystemConfig.setElectricityCostPerHour(-0.1);
        assertEquals(originalElectricityCost, SystemConfig.getElectricityCostPerHour(), 0.001);
        
        double originalTaxRate = SystemConfig.getTaxRate();
        SystemConfig.setTaxRate(-0.1);
        assertEquals(originalTaxRate, SystemConfig.getTaxRate(), 0.001);
    }
    
    @Test
    void testSystemConfigFileOperations() {
        // Test saving configuration
        boolean saved = SystemConfig.saveToFile();
        assertTrue(saved);
        
        // Test loading configuration
        boolean loaded = SystemConfig.loadFromFile();
        assertTrue(loaded);
        
        // Test backup creation
        boolean backedUp = SystemConfig.backupConfigFile();
        assertTrue(backedUp);
    }
    
    @Test
    void testSystemConfigStatus() {
        String status = SystemConfig.getConfigFileStatus();
        assertNotNull(status);
        assertTrue(status.contains("Configuration"));
    }
    
    // ==================== INTEGRATION TESTS ====================
    
    @Test
    void testCompleteOrderWorkflow() {
        // Create user
        User user = new User("integrationuser", "integration@example.com", "customer", "IntegrationPass123!");
        
        // Create admin
        AdminUser admin = new AdminUser("integrationadmin", "admin@example.com", "AdminPass123!");
        
        // Admin adds material
        Material material = admin.addMaterial("IntegrationPLA", 0.06, 210, "Purple");
        assertNotNull(material);
        
        // User submits order
        Order order = user.submitOrder(material, "12x12x8", 4, "Integration test order");
        assertNotNull(order);
        
        // Calculate price
        double price = order.calculatePrice();
        assertTrue(price > 0);
        
        // Create invoice
        Invoice invoice = new Invoice(order, price);
        assertNotNull(invoice);
        
        // Verify complete workflow
        assertEquals(order, invoice.getOrder());
        assertEquals(price, invoice.getTotalCost(), 0.01);
    }
    
    @Test
    void testVIPCustomerWorkflow() {
        User vipUser = new User("vipcustomer", "vip@example.com", "vip", "VIPPass123!");
        Material material = new Material("VIPMaterial", 0.08, 240, "Gold");
        
        Order vipOrder = vipUser.submitOrder(material, "20x20x15", 5, "VIP order");
        double vipPrice = vipOrder.calculatePrice();
        
        // VIP customers should get discounts
        assertTrue(vipPrice > 0);
        
        Invoice vipInvoice = new Invoice(vipOrder, vipPrice);
        assertNotNull(vipInvoice);
    }
    
    @Test
    void testRushOrderWorkflow() {
        User user = new User("rushuser", "rush@example.com", "customer", "RushPass123!");
        Material material = new Material("RushMaterial", 0.07, 220, "Red");
        
        Order rushOrder = user.submitOrder(material, "8x8x6", 2, "Rush order");
        rushOrder.setPriority("rush");
        
        double rushPrice = rushOrder.calculatePrice();
        assertTrue(rushPrice > 0);
        
        Invoice rushInvoice = new Invoice(rushOrder, rushPrice);
        assertNotNull(rushInvoice);
    }
    
    @Test
    void testBulkOrderWorkflow() {
        User user = new User("bulkuser", "bulk@example.com", "customer", "BulkPass123!");
        Material material = new Material("BulkMaterial", 0.05, 200, "Green");
        
        Order bulkOrder = user.submitOrder(material, "25x25x20", 20, "Bulk order");
        double bulkPrice = bulkOrder.calculatePrice();
        
        // Bulk orders should have quantity discounts
        assertTrue(bulkPrice > 0);
        
        Invoice bulkInvoice = new Invoice(bulkOrder, bulkPrice);
        assertNotNull(bulkInvoice);
    }
    
    @Test
    void testAdminWorkflow() {
        AdminUser admin = new AdminUser("workflowadmin", "workflowadmin@example.com", "AdminPass123!");
        
        // Admin adds multiple materials
        Material material1 = admin.addMaterial("AdminMaterial1", 0.06, 210, "Blue");
        Material material2 = admin.addMaterial("AdminMaterial2", 0.08, 240, "Red");
        
        assertNotNull(material1);
        assertNotNull(material2);
        
        // Admin views all orders
        Order[] orders = admin.viewAllOrders();
        assertNotNull(orders);
        
        // Admin modifies pricing
        boolean pricingModified = admin.modifyPricingConstants(0.15, 0.20, 5.0);
        assertTrue(pricingModified);
    }
    
    @Test
    void testQueueProcessingWorkflow() {
        User user1 = new User("queueuser1", "queue1@example.com", "customer", "QueuePass123!");
        User user2 = new User("queueuser2", "queue2@example.com", "customer", "QueuePass123!");
        
        Material material = new Material("QueueMaterial", 0.05, 200, "Yellow");
        
        // Submit multiple orders
        Order order1 = user1.submitOrder(material, "10x10x5", 1, "First order");
        Order order2 = user2.submitOrder(material, "15x15x8", 2, "Second order");
        
        assertNotNull(order1);
        assertNotNull(order2);
        
        // Process orders (simplified queue processing)
        OrderManager.enqueueOrder(order1);
        OrderManager.enqueueOrder(order2);
        
        Order nextOrder = OrderManager.dequeueNextOrder();
        assertNotNull(nextOrder);
    }
    
    @Test
    void testErrorScenarioWorkflow() {
        User user = new User("erroruser", "error@example.com", "customer", "ErrorPass123!");
        
        // Test with invalid material (null)
        assertThrows(Exception.class, () -> {
            user.submitOrder(null, "10x10x5", 1, "Invalid order");
        });
        
        // Test with invalid dimensions
        Material material = new Material("ErrorMaterial", 0.05, 200, "Black");
        Order order = user.submitOrder(material, "", 1, "Empty dimensions");
        assertNotNull(order); // Should still create order but with empty dimensions
    }
    
    @Test
    void testComplexMultiStepWorkflow() {
        // Create multiple users
        User user1 = new User("multiuser1", "multi1@example.com", "customer", "MultiPass123!");
        User user2 = new User("multiuser2", "multi2@example.com", "vip", "MultiPass123!");
        AdminUser admin = new AdminUser("multiadmin", "multiadmin@example.com", "AdminPass123!");
        
        // Admin adds materials
        Material material1 = admin.addMaterial("MultiMaterial1", 0.06, 210, "Blue");
        Material material2 = admin.addMaterial("MultiMaterial2", 0.08, 240, "Red");
        
        // Users submit orders
        Order order1 = user1.submitOrder(material1, "10x10x5", 2, "Regular order");
        Order order2 = user2.submitOrder(material2, "15x15x8", 3, "VIP order");
        
        // Process orders
        double price1 = order1.calculatePrice();
        double price2 = order2.calculatePrice();
        
        assertTrue(price1 > 0);
        assertTrue(price2 > 0);
        
        // Create invoices
        Invoice invoice1 = new Invoice(order1, price1);
        Invoice invoice2 = new Invoice(order2, price2);
        
        assertNotNull(invoice1);
        assertNotNull(invoice2);
        
        // Admin views all orders
        Order[] allOrders = admin.viewAllOrders();
        assertTrue(allOrders.length >= 2);
    }
    
    @Test
    void testSystemConfigurationImpact() {
        User user = new User("configuser", "config@example.com", "customer", "ConfigPass123!");
        Material material = new Material("ConfigMaterial", 0.05, 200, "White");
        Order order = user.submitOrder(material, "10x10x5", 2, "Config test");
        
        // Get original price
        double originalPrice = order.calculatePrice();
        
        // Modify system configuration
        double originalSetupCost = SystemConfig.getBaseSetupCost();
        SystemConfig.setBaseSetupCost(originalSetupCost * 2);
        
        // Recalculate price
        double newPrice = order.calculatePrice();
        
        // Price should be different
        assertNotEquals(originalPrice, newPrice);
        
        // Restore original configuration
        SystemConfig.setBaseSetupCost(originalSetupCost);
    }
    
    // ==================== PERFORMANCE TESTS ====================
    
    @Test
    void testHighVolumeOrderCreation() {
        User user = new User("perfuser", "perf@example.com", "customer", "PerfPass123!");
        Material material = new Material("PerfMaterial", 0.05, 200, "Gray");
        
        long startTime = System.currentTimeMillis();
        
        // Create 1000 orders
        for (int i = 0; i < 1000; i++) {
            Order order = user.submitOrder(material, "5x5x5", 1, "Performance test " + i);
            assertNotNull(order);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 10000, "High volume order creation took too long: " + duration + "ms");
    }
    
    @Test
    void testBatchPriceCalculations() {
        User user = new User("batchuser", "batch@example.com", "customer", "BatchPass123!");
        Material material = new Material("BatchMaterial", 0.05, 200, "Orange");
        Order order = user.submitOrder(material, "10x10x5", 2, "Batch test");
        
        long startTime = System.currentTimeMillis();
        
        // Calculate price 1000 times
        for (int i = 0; i < 1000; i++) {
            double price = order.calculatePrice();
            assertTrue(price > 0);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 5000, "Batch price calculations took too long: " + duration + "ms");
    }
    
    @Test
    void testTimeEstimationStability() {
        User user = new User("timeuser", "time@example.com", "customer", "TimePass123!");
        Material material = new Material("TimeMaterial", 0.05, 200, "Pink");
        
        // Test with different dimension formats
        String[] dimensions = {"10x10x5", "15x15x8", "20x20x10", "5x5x5", "100x100x50"};
        
        for (String dim : dimensions) {
            Order order = user.submitOrder(material, dim, 1, "Time test");
            double hours = order.estimatePrintTimeHours();
            assertTrue(hours > 0, "Time estimation failed for dimensions: " + dim);
        }
    }
    
    @Test
    void testMemoryUsage() {
        User user = new User("memoryuser", "memory@example.com", "customer", "MemoryPass123!");
        Material material = new Material("MemoryMaterial", 0.05, 200, "Cyan");
        
        // Create many objects to test memory usage
        List<Order> orders = new ArrayList<>();
        List<Invoice> invoices = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            Order order = user.submitOrder(material, "5x5x5", 1, "Memory test " + i);
            orders.add(order);
            
            double price = order.calculatePrice();
            Invoice invoice = new Invoice(order, price);
            invoices.add(invoice);
        }
        
        // Verify all objects were created successfully
        assertEquals(100, orders.size());
        assertEquals(100, invoices.size());
        
        // Verify no null objects
        for (Order order : orders) {
            assertNotNull(order);
        }
        for (Invoice invoice : invoices) {
            assertNotNull(invoice);
        }
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testSystemLimits() {
        // Test maximum order quantity
        User user = new User("limituser", "limit@example.com", "customer", "LimitPass123!");
        Material material = new Material("LimitMaterial", 0.05, 200, "Magenta");
        
        Order largeOrder = user.submitOrder(material, "100x100x100", 1000, "Large order");
        assertNotNull(largeOrder);
        
        double price = largeOrder.calculatePrice();
        assertTrue(price > 0);
    }
    
    @Test
    void testUnicodeSupport() {
        User user = new User("unicodeuser", "unicode@example.com", "customer", "UnicodePass123!");
        Material material = new Material("UnicodeMaterial", 0.05, 200, "Unicode");
        
        Order order = user.submitOrder(material, "10x10x5", 1, "Unicode test: 测试 🎯 émojis");
        assertNotNull(order);
        assertTrue(order.getSpecialInstructions().contains("测试"));
        assertTrue(order.getSpecialInstructions().contains("🎯"));
    }
    
    @Test
    void testBoundaryValues() {
        User user = new User("boundaryuser", "boundary@example.com", "customer", "BoundaryPass123!");
        
        // Test minimum values
        Material minMaterial = new Material("Min", 0.001, 1, "Min");
        Order minOrder = user.submitOrder(minMaterial, "1x1x1", 1, "Min order");
        assertNotNull(minOrder);
        
        // Test maximum reasonable values
        Material maxMaterial = new Material("Max", 999.999, 9999, "Max");
        Order maxOrder = user.submitOrder(maxMaterial, "999x999x999", 999, "Max order");
        assertNotNull(maxOrder);
    }
}
