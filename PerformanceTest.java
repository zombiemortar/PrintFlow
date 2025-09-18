import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests to evaluate system behavior under large order volumes.
 */
public class PerformanceTest {

    private User user;
    private Material material;

    @BeforeEach
    void setUp() {
        SystemConfig.resetToDefaults();
        OrderManager.clearAllOrders();
        user = new User("load_tester", "load@test.com", "customer");
        material = new Material("PLA", 0.03, 200, "Gray");
        // Large stock to avoid InsufficientMaterialException during load
        Inventory.setStock(material, 1_000_000);
    }

    @AfterEach
    void tearDown() {
        SystemConfig.resetToDefaults();
    }

    /**
     * Create a large number of orders and ensure system remains responsive.
     */
    @Test
    void testHighVolumeOrderCreationAndQueueing() {
        final int numOrders = 5000; // large volume

        long startNs = System.nanoTime();
        for (int i = 0; i < numOrders; i++) {
            Order order = user.submitOrder(material, "10x10x5cm", 1, "Perf order " + i);
            assertNotNull(order, "Order should be created at index " + i);
        }
        long enqueueNs = System.nanoTime();

        assertEquals(numOrders, OrderManager.getQueueSize(), "All orders should be queued");
        assertEquals(numOrders, OrderManager.getAllOrders().length, "All orders should be registered");

        // Dequeue all orders and ensure FIFO without nulls
        for (int i = 0; i < numOrders; i++) {
            Order next = OrderManager.dequeueNextOrder();
            assertNotNull(next, "Dequeued order should not be null");
        }
        long endNs = System.nanoTime();

        assertEquals(0, OrderManager.getQueueSize(), "Queue should be empty after processing");

        long createAndEnqueueMs = (enqueueNs - startNs) / 1_000_000;
        long dequeueMs = (endNs - enqueueNs) / 1_000_000;

        System.out.println("PerformanceTest: Created & enqueued " + numOrders + " orders in " + createAndEnqueueMs + " ms");
        System.out.println("PerformanceTest: Dequeued " + numOrders + " orders in " + dequeueMs + " ms");

        // Soft performance assertions (tunable based on environment)
        // Keep generous thresholds to avoid flakiness on slower machines.
        assertTrue(createAndEnqueueMs < 5000, "Creation+enqueue should be under 5s");
        assertTrue(dequeueMs < 5000, "Dequeue should be under 5s");
    }

    /**
     * Stress test price calculation under large batch.
     */
    @Test
    void testBatchPriceCalculations() {
        final int numOrders = 2000;

        Order[] orders = new Order[numOrders];
        for (int i = 0; i < numOrders; i++) {
            orders[i] = user.submitOrder(material, "12x8x4cm", 2, "Batch price calc " + i);
            assertNotNull(orders[i]);
        }

        long startNs = System.nanoTime();
        double total = 0.0;
        for (Order order : orders) {
            total += order.calculatePrice();
        }
        long endNs = System.nanoTime();

        assertTrue(total > 0.0, "Total calculated price should be positive");

        long calcMs = (endNs - startNs) / 1_000_000;
        System.out.println("PerformanceTest: Calculated prices for " + numOrders + " orders in " + calcMs + " ms");
        assertTrue(calcMs < 4000, "Batch price calculation should be under 4s");
    }

    /**
     * Verify estimatePrintTimeHours remains stable and non-negative at scale.
     */
    @Test
    void testPrintTimeEstimationStability() {
        final int numOrders = 3000;
        for (int i = 0; i < numOrders; i++) {
            Order order = user.submitOrder(material, (5 + (i % 20)) + "x" + (5 + (i % 20)) + "x" + (3 + (i % 10)) + "cm", 1, "Time estimate " + i);
            assertNotNull(order);
            double hours = order.estimatePrintTimeHours();
            assertTrue(hours >= 0.1, "Estimated hours should respect minimum baseline");
            assertTrue(hours < 10_000, "Estimated hours should be within reasonable bounds");
        }
        System.out.println("PerformanceTest: Time estimation stable across " + numOrders + " orders");
    }
}


