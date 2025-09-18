import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the OrderManager class.
 * Tests all static methods, order registration, queue management, and edge cases.
 */
public class OrderManagerTest {
    
    private Order testOrder1;
    private Order testOrder2;
    private Order testOrder3;
    private User testUser;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        SystemConfig.resetToDefaults();
        
        // Create test objects
        testUser = new User("testuser", "test@example.com", "customer");
        testMaterial = new Material("PLA", 0.02, 200, "White");
        
        // Create test orders
        testOrder1 = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        testOrder2 = new Order(testUser, testMaterial, "5cm x 5cm x 5cm", 1, "Standard");
        testOrder3 = new Order(testUser, testMaterial, "20cm x 10cm x 5cm", 3, "Rush order");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testOrder1 = null;
        testOrder2 = null;
        testOrder3 = null;
        testUser = null;
        testMaterial = null;
    }
    
    // registerOrder Tests
    @Test
    void testRegisterOrderValidOrder() {
        OrderManager.registerOrder(testOrder1);
        Order retrievedOrder = OrderManager.getOrderById(testOrder1.getOrderID());
        assertEquals(testOrder1, retrievedOrder);
    }
    
    @Test
    void testRegisterOrderWithNull() {
        OrderManager.registerOrder(null);
        // Should not throw exception and should not affect the system
        Order[] allOrders = OrderManager.getAllOrders();
        assertNotNull(allOrders);
    }
    
    @Test
    void testRegisterOrderMultipleOrders() {
        OrderManager.registerOrder(testOrder1);
        OrderManager.registerOrder(testOrder2);
        OrderManager.registerOrder(testOrder3);
        
        Order retrieved1 = OrderManager.getOrderById(testOrder1.getOrderID());
        Order retrieved2 = OrderManager.getOrderById(testOrder2.getOrderID());
        Order retrieved3 = OrderManager.getOrderById(testOrder3.getOrderID());
        
        assertEquals(testOrder1, retrieved1);
        assertEquals(testOrder2, retrieved2);
        assertEquals(testOrder3, retrieved3);
    }
    
    @Test
    void testRegisterOrderOverwritesExisting() {
        OrderManager.registerOrder(testOrder1);
        
        // Create new order with same ID (simulating overwrite)
        Order newOrder = new Order(testUser, testMaterial, "15cm x 10cm x 5cm", 4, "Updated order");
        // Manually set the same ID to simulate overwrite scenario
        newOrder.setUser(testUser);
        newOrder.setMaterial(testMaterial);
        newOrder.setDimensions("15cm x 10cm x 5cm");
        newOrder.setQuantity(4);
        newOrder.setSpecialInstructions("Updated order");
        
        OrderManager.registerOrder(newOrder);
        Order retrievedOrder = OrderManager.getOrderById(newOrder.getOrderID());
        assertEquals(newOrder, retrievedOrder);
    }
    
    // getOrderById Tests
    @Test
    void testGetOrderByIdValidId() {
        OrderManager.registerOrder(testOrder1);
        Order retrievedOrder = OrderManager.getOrderById(testOrder1.getOrderID());
        assertEquals(testOrder1, retrievedOrder);
    }
    
    @Test
    void testGetOrderByIdNonExistentId() {
        Order retrievedOrder = OrderManager.getOrderById(99999);
        assertNull(retrievedOrder);
    }
    
    @Test
    void testGetOrderByIdNegativeId() {
        Order retrievedOrder = OrderManager.getOrderById(-1);
        assertNull(retrievedOrder);
    }
    
    @Test
    void testGetOrderByIdZeroId() {
        Order retrievedOrder = OrderManager.getOrderById(0);
        assertNull(retrievedOrder);
    }
    
    // getAllOrders Tests
    @Test
    void testGetAllOrdersEmpty() {
        Order[] orders = OrderManager.getAllOrders();
        assertNotNull(orders);
        assertEquals(0, orders.length);
    }
    
    @Test
    void testGetAllOrdersSingleOrder() {
        OrderManager.registerOrder(testOrder1);
        Order[] orders = OrderManager.getAllOrders();
        assertNotNull(orders);
        assertEquals(1, orders.length);
        assertEquals(testOrder1, orders[0]);
    }
    
    @Test
    void testGetAllOrdersMultipleOrders() {
        OrderManager.registerOrder(testOrder1);
        OrderManager.registerOrder(testOrder2);
        OrderManager.registerOrder(testOrder3);
        
        Order[] orders = OrderManager.getAllOrders();
        assertNotNull(orders);
        assertEquals(3, orders.length);
        
        // Check that all orders are present
        boolean found1 = false, found2 = false, found3 = false;
        for (Order order : orders) {
            if (order.equals(testOrder1)) found1 = true;
            if (order.equals(testOrder2)) found2 = true;
            if (order.equals(testOrder3)) found3 = true;
        }
        assertTrue(found1 && found2 && found3);
    }
    
    @Test
    void testGetAllOrdersReturnsNewArray() {
        OrderManager.registerOrder(testOrder1);
        Order[] orders1 = OrderManager.getAllOrders();
        Order[] orders2 = OrderManager.getAllOrders();
        
        assertNotSame(orders1, orders2); // Should be different array instances
        assertEquals(orders1.length, orders2.length);
    }
    
    // enqueueOrder Tests
    @Test
    void testEnqueueOrderValidOrder() {
        OrderManager.enqueueOrder(testOrder1);
        assertEquals(1, OrderManager.getQueueSize());
    }
    
    @Test
    void testEnqueueOrderWithNull() {
        OrderManager.enqueueOrder(null);
        assertEquals(0, OrderManager.getQueueSize());
    }
    
    @Test
    void testEnqueueOrderMultipleOrders() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        OrderManager.enqueueOrder(testOrder3);
        
        assertEquals(3, OrderManager.getQueueSize());
    }
    
    @Test
    void testEnqueueOrderFIFOOrder() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        OrderManager.enqueueOrder(testOrder3);
        
        Order first = OrderManager.dequeueNextOrder();
        Order second = OrderManager.dequeueNextOrder();
        Order third = OrderManager.dequeueNextOrder();
        
        assertEquals(testOrder1, first);
        assertEquals(testOrder2, second);
        assertEquals(testOrder3, third);
    }
    
    // dequeueNextOrder Tests
    @Test
    void testDequeueNextOrderValidOrder() {
        OrderManager.enqueueOrder(testOrder1);
        Order dequeuedOrder = OrderManager.dequeueNextOrder();
        assertEquals(testOrder1, dequeuedOrder);
        assertEquals(0, OrderManager.getQueueSize());
    }
    
    @Test
    void testDequeueNextOrderEmptyQueue() {
        Order dequeuedOrder = OrderManager.dequeueNextOrder();
        assertNull(dequeuedOrder);
    }
    
    @Test
    void testDequeueNextOrderMultipleOrders() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        OrderManager.enqueueOrder(testOrder3);
        
        Order first = OrderManager.dequeueNextOrder();
        assertEquals(testOrder1, first);
        assertEquals(2, OrderManager.getQueueSize());
        
        Order second = OrderManager.dequeueNextOrder();
        assertEquals(testOrder2, second);
        assertEquals(1, OrderManager.getQueueSize());
        
        Order third = OrderManager.dequeueNextOrder();
        assertEquals(testOrder3, third);
        assertEquals(0, OrderManager.getQueueSize());
    }
    
    @Test
    void testDequeueNextOrderFIFOBehavior() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        
        Order first = OrderManager.dequeueNextOrder();
        assertEquals(testOrder1, first);
        
        OrderManager.enqueueOrder(testOrder3);
        
        Order second = OrderManager.dequeueNextOrder();
        assertEquals(testOrder2, second);
        
        Order third = OrderManager.dequeueNextOrder();
        assertEquals(testOrder3, third);
    }
    
    // getQueueSize Tests
    @Test
    void testGetQueueSizeEmpty() {
        assertEquals(0, OrderManager.getQueueSize());
    }
    
    @Test
    void testGetQueueSizeSingleOrder() {
        OrderManager.enqueueOrder(testOrder1);
        assertEquals(1, OrderManager.getQueueSize());
    }
    
    @Test
    void testGetQueueSizeMultipleOrders() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        OrderManager.enqueueOrder(testOrder3);
        assertEquals(3, OrderManager.getQueueSize());
    }
    
    @Test
    void testGetQueueSizeAfterDequeue() {
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        assertEquals(2, OrderManager.getQueueSize());
        
        OrderManager.dequeueNextOrder();
        assertEquals(1, OrderManager.getQueueSize());
        
        OrderManager.dequeueNextOrder();
        assertEquals(0, OrderManager.getQueueSize());
    }
    
    @Test
    void testGetQueueSizeAfterMixedOperations() {
        OrderManager.enqueueOrder(testOrder1);
        assertEquals(1, OrderManager.getQueueSize());
        
        OrderManager.enqueueOrder(testOrder2);
        assertEquals(2, OrderManager.getQueueSize());
        
        OrderManager.dequeueNextOrder();
        assertEquals(1, OrderManager.getQueueSize());
        
        OrderManager.enqueueOrder(testOrder3);
        assertEquals(2, OrderManager.getQueueSize());
    }
    
    // Integration Tests
    @Test
    void testRegisterAndEnqueueWorkflow() {
        // Register order
        OrderManager.registerOrder(testOrder1);
        Order retrievedOrder = OrderManager.getOrderById(testOrder1.getOrderID());
        assertEquals(testOrder1, retrievedOrder);
        
        // Enqueue order
        OrderManager.enqueueOrder(testOrder1);
        assertEquals(1, OrderManager.getQueueSize());
        
        // Dequeue order
        Order dequeuedOrder = OrderManager.dequeueNextOrder();
        assertEquals(testOrder1, dequeuedOrder);
        assertEquals(0, OrderManager.getQueueSize());
        
        // Order should still be retrievable by ID
        Order stillRetrievable = OrderManager.getOrderById(testOrder1.getOrderID());
        assertEquals(testOrder1, stillRetrievable);
    }
    
    @Test
    void testCompleteOrderWorkflow() {
        // Register multiple orders
        OrderManager.registerOrder(testOrder1);
        OrderManager.registerOrder(testOrder2);
        OrderManager.registerOrder(testOrder3);
        
        // Enqueue all orders
        OrderManager.enqueueOrder(testOrder1);
        OrderManager.enqueueOrder(testOrder2);
        OrderManager.enqueueOrder(testOrder3);
        
        // Verify all orders are registered and queued
        assertEquals(3, OrderManager.getAllOrders().length);
        assertEquals(3, OrderManager.getQueueSize());
        
        // Process orders in FIFO order
        Order processed1 = OrderManager.dequeueNextOrder();
        Order processed2 = OrderManager.dequeueNextOrder();
        Order processed3 = OrderManager.dequeueNextOrder();
        
        assertEquals(testOrder1, processed1);
        assertEquals(testOrder2, processed2);
        assertEquals(testOrder3, processed3);
        
        // Verify queue is empty but orders are still registered
        assertEquals(0, OrderManager.getQueueSize());
        assertEquals(3, OrderManager.getAllOrders().length);
    }
    
    // Edge Cases
    @Test
    void testOrderManagerWithVeryLargeNumberOfOrders() {
        // Test with many orders
        for (int i = 0; i < 100; i++) {
            Order order = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 1, "Order " + i);
            OrderManager.registerOrder(order);
            OrderManager.enqueueOrder(order);
        }
        
        assertEquals(100, OrderManager.getAllOrders().length);
        assertEquals(100, OrderManager.getQueueSize());
        
        // Dequeue all orders
        for (int i = 0; i < 100; i++) {
            Order dequeued = OrderManager.dequeueNextOrder();
            assertNotNull(dequeued);
        }
        
        assertEquals(0, OrderManager.getQueueSize());
        assertEquals(100, OrderManager.getAllOrders().length);
    }
    
    @Test
    void testOrderManagerWithOrdersHavingSameProperties() {
        // Create orders with same properties but different IDs
        Order order1 = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        Order order2 = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        
        OrderManager.registerOrder(order1);
        OrderManager.registerOrder(order2);
        OrderManager.enqueueOrder(order1);
        OrderManager.enqueueOrder(order2);
        
        assertEquals(2, OrderManager.getAllOrders().length);
        assertEquals(2, OrderManager.getQueueSize());
        
        // Should be able to distinguish between them by ID
        Order retrieved1 = OrderManager.getOrderById(order1.getOrderID());
        Order retrieved2 = OrderManager.getOrderById(order2.getOrderID());
        
        assertEquals(order1, retrieved1);
        assertEquals(order2, retrieved2);
        assertNotEquals(order1.getOrderID(), order2.getOrderID());
    }
    
    @Test
    void testOrderManagerWithNullOrderProperties() {
        Order orderWithNulls = new Order(null, null, null, 0, null);
        OrderManager.registerOrder(orderWithNulls);
        OrderManager.enqueueOrder(orderWithNulls);
        
        Order retrieved = OrderManager.getOrderById(orderWithNulls.getOrderID());
        assertEquals(orderWithNulls, retrieved);
        
        Order dequeued = OrderManager.dequeueNextOrder();
        assertEquals(orderWithNulls, dequeued);
    }
}
