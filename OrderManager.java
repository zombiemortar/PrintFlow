import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Central manager for orders and the print queue.
 * Maintains a HashMap for fast lookup and a FIFO Queue for processing.
 */
public class OrderManager {
    private static final Map<Integer, Order> orderIdToOrder = new HashMap<>();
    private static final Queue<Order> printQueue = new LinkedList<>();

    private OrderManager() {
        // Utility holder
    }

    public static void registerOrder(Order order) {
        if (order == null) {
            return;
        }
        orderIdToOrder.put(order.getOrderID(), order);
    }

    public static Order getOrderById(int orderId) {
        return orderIdToOrder.get(orderId);
    }

    public static Order[] getAllOrders() {
        return orderIdToOrder.values().toArray(new Order[0]);
    }

    public static void enqueueOrder(Order order) {
        if (order == null) {
            return;
        }
        printQueue.offer(order);
    }

    public static Order dequeueNextOrder() {
        return printQueue.poll();
    }

    public static int getQueueSize() {
        return printQueue.size();
    }
    
    /**
     * Clears all orders from the system (for testing purposes).
     * This method should only be used in test scenarios.
     */
    public static void clearAllOrders() {
        orderIdToOrder.clear();
        printQueue.clear();
    }
}





