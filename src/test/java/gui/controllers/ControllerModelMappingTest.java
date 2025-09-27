package gui.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import model.Material;
import model.Order;
import model.User;
import util.Inventory;

/**
 * Unit tests for controller model mapping logic.
 * Tests model interactions without UI dependencies.
 */
public class ControllerModelMappingTest {

    private Material testMaterial;
    private User testUser;

    @BeforeEach
    void setUp() {
        testMaterial = new Material("TestBrand", "PLA", 0.02, 210, "Black");
        testUser = new User("testuser", "test@example.com", "customer", "password");
    }

    @Test
    @DisplayName("Should create material with valid parameters")
    void testCreateMaterial() {
        Material material = new Material("Overture", "PLA", 0.025, 215, "White");
        
        assertEquals("Overture", material.getBrand());
        assertEquals("PLA", material.getType());
        assertEquals(0.025, material.getCostPerGram(), 0.001);
        assertEquals(215, material.getPrintTemp());
        assertEquals("White", material.getColor());
    }

    @Test
    @DisplayName("Should handle material display name formatting")
    void testMaterialDisplayName() {
        Material material = new Material("Brand", "Type", 0.02, 210, "Color");
        String displayName = material.getDisplayName();
        
        assertNotNull(displayName, "Display name should not be null");
        assertTrue(displayName.contains("Brand"), "Display name should contain brand");
        assertTrue(displayName.contains("Type"), "Display name should contain type");
        assertTrue(displayName.contains("Color"), "Display name should contain color");
    }

    @Test
    @DisplayName("Should handle material info formatting")
    void testMaterialInfo() {
        Material material = new Material("Overture", "PLA", 0.025, 215, "White");
        String info = material.getMaterialInfo();
        
        assertNotNull(info, "Material info should not be null");
        assertTrue(info.contains("Overture"), "Info should contain brand");
        assertTrue(info.contains("PLA"), "Info should contain type");
        assertTrue(info.contains("White"), "Info should contain color");
    }

    @Test
    @DisplayName("Should handle stock operations correctly")
    void testStockOperations() {
        // Test setting and getting stock
        Inventory.setStock(testMaterial, 1000);
        int stock = Inventory.getStock(testMaterial);
        assertEquals(1000, stock, "Stock should be set correctly");
        
        // Test updating stock
        Inventory.setStock(testMaterial, 500);
        int updatedStock = Inventory.getStock(testMaterial);
        assertEquals(500, updatedStock, "Stock should be updated correctly");
    }

    @Test
    @DisplayName("Should create order with valid parameters")
    void testCreateOrder() {
        Order order = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        assertNotNull(order);
        assertEquals(testUser, order.getUser());
        assertEquals(testMaterial, order.getMaterial());
        assertEquals("10x5x3", order.getDimensions());
        assertEquals(5, order.getQuantity());
        assertEquals("Test instructions", order.getSpecialInstructions());
        assertEquals(25.5, order.getMaterialGrams(), 0.001);
        assertEquals("pending", order.getStatus());
        assertEquals("normal", order.getPriority());
    }

    @Test
    @DisplayName("Should handle order status changes")
    void testOrderStatusChanges() {
        Order order = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        // Test initial status
        assertEquals("pending", order.getStatus());
        
        // Test status changes
        boolean result1 = order.updateStatus("processing");
        assertTrue(result1, "Status update should succeed");
        assertEquals("processing", order.getStatus());
        
        boolean result2 = order.updateStatus("completed");
        assertTrue(result2, "Status update should succeed");
        assertEquals("completed", order.getStatus());
    }

    @Test
    @DisplayName("Should handle order priority changes")
    void testOrderPriorityChanges() {
        Order order = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        // Test initial priority
        assertEquals("normal", order.getPriority());
        
        // Test priority changes
        order.setPriority("rush");
        assertEquals("rush", order.getPriority());
        
        order.setPriority("vip");
        assertEquals("vip", order.getPriority());
    }

    @Test
    @DisplayName("Should calculate order price correctly")
    void testOrderPriceCalculation() {
        Order order = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        double price = order.calculatePrice();
        assertTrue(price > 0, "Order price should be positive");
        
        // Price should include material cost, setup cost, and tax
        // Material cost: 25.5 grams * 0.02 per gram = 0.51
        // Plus setup cost and tax
        assertTrue(price >= 0.51, "Price should be at least the material cost");
    }

    @Test
    @DisplayName("Should handle order ID generation")
    void testOrderIDGeneration() {
        Order order1 = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        Order order2 = new Order(testUser, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        assertNotNull(order1.getOrderID());
        assertNotNull(order2.getOrderID());
        assertNotEquals(order1.getOrderID(), order2.getOrderID(), "Order IDs should be unique");
    }

    @Test
    @DisplayName("Should handle order with different quantities")
    void testOrderDifferentQuantities() {
        Order order1 = new Order(testUser, testMaterial, "10x5x3", 1, "Test instructions", 25.5);
        Order order2 = new Order(testUser, testMaterial, "10x5x3", 10, "Test instructions", 25.5);
        
        assertEquals(1, order1.getQuantity());
        assertEquals(10, order2.getQuantity());
        
        // Price should scale with quantity
        double price1 = order1.calculatePrice();
        double price2 = order2.calculatePrice();
        
        assertTrue(price2 > price1, "Price should increase with quantity");
    }

    @Test
    @DisplayName("Should handle order with different materials")
    void testOrderDifferentMaterials() {
        Material expensiveMaterial = new Material("Premium", "PLA", 0.05, 210, "Black");
        Material cheapMaterial = new Material("Budget", "PLA", 0.01, 210, "Black");
        
        Order expensiveOrder = new Order(testUser, expensiveMaterial, "10x5x3", 5, "Test instructions", 25.5);
        Order cheapOrder = new Order(testUser, cheapMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        double expensivePrice = expensiveOrder.calculatePrice();
        double cheapPrice = cheapOrder.calculatePrice();
        
        assertTrue(expensivePrice > cheapPrice, "Expensive material should result in higher price");
    }

    @Test
    @DisplayName("Should handle order with different users")
    void testOrderDifferentUsers() {
        User user1 = new User("user1", "user1@example.com", "customer", "password");
        User user2 = new User("user2", "user2@example.com", "vip", "password");
        
        Order order1 = new Order(user1, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        Order order2 = new Order(user2, testMaterial, "10x5x3", 5, "Test instructions", 25.5);
        
        assertEquals(user1, order1.getUser());
        assertEquals(user2, order2.getUser());
    }

    @Test
    @DisplayName("Should handle material cost calculations")
    void testMaterialCostCalculations() {
        Material material = new Material("TestBrand", "PLA", 0.03, 210, "Black");
        
        // Test cost per gram
        assertEquals(0.03, material.getCostPerGram(), 0.001);
        
        // Test cost calculation for specific weight
        double costFor100Grams = material.getCostPerGram() * 100;
        assertEquals(3.0, costFor100Grams, 0.001);
    }

    @Test
    @DisplayName("Should handle material temperature settings")
    void testMaterialTemperature() {
        Material material = new Material("TestBrand", "PLA", 0.02, 220, "Black");
        assertEquals(220, material.getPrintTemp());
        
        // Test different temperature ranges
        Material lowTempMaterial = new Material("TestBrand", "PLA", 0.02, 180, "Black");
        assertEquals(180, lowTempMaterial.getPrintTemp());
        
        Material highTempMaterial = new Material("TestBrand", "ABS", 0.02, 250, "Black");
        assertEquals(250, highTempMaterial.getPrintTemp());
    }
}
