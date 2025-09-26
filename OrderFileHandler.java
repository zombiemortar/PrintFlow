import java.util.*;

/**
 * Handles file operations for Order objects.
 * Provides methods to save and load orders to/from files.
 */
public class OrderFileHandler {
    
    private static final String ORDERS_FILENAME = "orders.txt";
    private static final String ORDER_QUEUE_FILENAME = "order_queue.txt";
    
    /**
     * Saves all orders from OrderManager to a file.
     * @return true if save was successful
     */
    public static boolean saveOrders() {
        Order[] orders = OrderManager.getAllOrders();
        if (orders.length == 0) {
            return DataFileManager.writeToFile(ORDERS_FILENAME, "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Order Data Export\n");
        data.append("# Format: orderID|username|email|role|materialName|materialCostPerGram|materialPrintTemp|materialColor|dimensions|quantity|specialInstructions|status|priority|estimatedPrintHours\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Order order : orders) {
            data.append(serializeOrder(order)).append("\n");
        }
        
        return DataFileManager.writeToFile(ORDERS_FILENAME, data.toString());
    }
    
    /**
     * Loads orders from file and registers them with OrderManager.
     * @return true if load was successful
     */
    public static boolean loadOrders() {
        String data = DataFileManager.readFromFile(ORDERS_FILENAME);
        if (data == null || data.trim().isEmpty()) {
            return true; // No data to load is not an error
        }
        
        try {
            String[] lines = data.split("\n");
            int loadedCount = 0;
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                Order order = deserializeOrder(line);
                if (order != null) {
                    OrderManager.registerOrder(order);
                    loadedCount++;
                }
            }
            
            System.out.println("Loaded " + loadedCount + " orders from file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading orders: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Saves the current print queue to a file.
     * @return true if save was successful
     */
    public static boolean saveOrderQueue() {
        // Get all orders in queue (this is a simplified approach)
        // In a real implementation, we'd need to track queue order separately
        Order[] allOrders = OrderManager.getAllOrders();
        List<Order> queueOrders = new ArrayList<>();
        
        // Filter orders that are likely in queue (pending status)
        for (Order order : allOrders) {
            if ("pending".equals(order.getStatus()) || "processing".equals(order.getStatus())) {
                queueOrders.add(order);
            }
        }
        
        if (queueOrders.isEmpty()) {
            return DataFileManager.writeToFile(ORDER_QUEUE_FILENAME, "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Order Queue Export\n");
        data.append("# Format: orderID\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Order order : queueOrders) {
            data.append(order.getOrderID()).append("\n");
        }
        
        return DataFileManager.writeToFile(ORDER_QUEUE_FILENAME, data.toString());
    }
    
    /**
     * Loads order queue from file and enqueues orders.
     * @return true if load was successful
     */
    public static boolean loadOrderQueue() {
        String data = DataFileManager.readFromFile(ORDER_QUEUE_FILENAME);
        if (data == null || data.trim().isEmpty()) {
            return true; // No queue data to load is not an error
        }
        
        try {
            String[] lines = data.split("\n");
            int enqueuedCount = 0;
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                try {
                    int orderId = Integer.parseInt(line);
                    Order order = OrderManager.getOrderById(orderId);
                    if (order != null) {
                        OrderManager.enqueueOrder(order);
                        enqueuedCount++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid order ID in queue file: " + line);
                }
            }
            
            System.out.println("Enqueued " + enqueuedCount + " orders from queue file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading order queue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Serializes an Order object to a string format.
     * @param order The order to serialize
     * @return Serialized string representation
     */
    private static String serializeOrder(Order order) {
        StringBuilder sb = new StringBuilder();
        
        // Order basic info
        sb.append(order.getOrderID()).append("|");
        
        // User info
        User user = order.getUser();
        if (user != null) {
            sb.append(escapeString(user.getUsername())).append("|");
            sb.append(escapeString(user.getEmail())).append("|");
            sb.append(escapeString(user.getRole())).append("|");
        } else {
            sb.append("|||"); // Empty user fields
        }
        
        // Material info
        Material material = order.getMaterial();
        if (material != null) {
            sb.append(escapeString(material.getName())).append("|");
            sb.append(material.getCostPerGram()).append("|");
            sb.append(material.getPrintTemp()).append("|");
            sb.append(escapeString(material.getColor())).append("|");
        } else {
            sb.append("|||"); // Empty material fields
        }
        
        // Order details
        sb.append(escapeString(order.getDimensions())).append("|");
        sb.append(order.getQuantity()).append("|");
        sb.append(escapeString(order.getSpecialInstructions())).append("|");
        sb.append(escapeString(order.getStatus())).append("|");
        sb.append(escapeString(order.getPriority())).append("|");
        sb.append(order.getEstimatedPrintHours());
        
        return sb.toString();
    }
    
    /**
     * Deserializes a string to an Order object.
     * @param data The serialized string data
     * @return The deserialized Order object, or null if parsing failed
     */
    private static Order deserializeOrder(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 14) {
                System.err.println("Invalid order data format: " + data);
                return null;
            }
            
            // Create user
            User user = null;
            if (!parts[1].isEmpty() && !parts[2].isEmpty()) {
                user = new User(
                    unescapeString(parts[1]),
                    unescapeString(parts[2]),
                    unescapeString(parts[3]),
                    "DefaultPass123!"
                );
            }
            
            // Create material
            Material material = null;
            if (!parts[4].isEmpty()) {
                material = new Material(
                    unescapeString(parts[4]),
                    Double.parseDouble(parts[5]),
                    Integer.parseInt(parts[6]),
                    unescapeString(parts[7])
                );
            }
            
            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setMaterial(material);
            order.setDimensions(unescapeString(parts[8]));
            order.setQuantity(Integer.parseInt(parts[9]));
            order.setSpecialInstructions(unescapeString(parts[10]));
            order.updateStatus(unescapeString(parts[11]));
            order.setPriority(unescapeString(parts[12]));
            
            // Set estimated print hours
            if (parts.length > 13 && !parts[13].isEmpty()) {
                try {
                    // Note: We can't directly set estimatedPrintHours as it's private
                    // This is a limitation of the current Order class design
                    Double.parseDouble(parts[13]);
                } catch (NumberFormatException e) {
                    // Ignore parsing error for estimated hours
                }
            }
            
            return order;
        } catch (Exception e) {
            System.err.println("Error deserializing order: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Escapes special characters in strings for file storage.
     * @param str The string to escape
     * @return The escaped string
     */
    private static String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("|", "\\|").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    /**
     * Unescapes special characters from file storage.
     * @param str The escaped string
     * @return The unescaped string
     */
    private static String unescapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\|", "|").replace("\\n", "\n").replace("\\r", "\r");
    }
    
    /**
     * Creates a backup of the orders file.
     * @return true if backup was successful
     */
    public static boolean backupOrders() {
        return DataFileManager.createBackup(ORDERS_FILENAME);
    }
    
    /**
     * Creates a backup of the order queue file.
     * @return true if backup was successful
     */
    public static boolean backupOrderQueue() {
        return DataFileManager.createBackup(ORDER_QUEUE_FILENAME);
    }
    
    /**
     * Restores orders from a backup file.
     * @param backupFilename The backup filename to restore from
     * @return true if restore was successful
     */
    public static boolean restoreOrders(String backupFilename) {
        boolean restored = DataFileManager.restoreFromBackup(backupFilename, ORDERS_FILENAME);
        if (restored) {
            // Reload orders after restore
            return loadOrders();
        }
        return false;
    }
    
    /**
     * Restores orders from the most recent backup.
     * @return true if restore was successful
     */
    public static boolean restoreOrdersFromLatestBackup() {
        boolean restored = DataFileManager.restoreFromLatestBackup(ORDERS_FILENAME);
        if (restored) {
            // Reload orders after restore
            return loadOrders();
        }
        return false;
    }
    
    /**
     * Restores order queue from a backup file.
     * @param backupFilename The backup filename to restore from
     * @return true if restore was successful
     */
    public static boolean restoreOrderQueue(String backupFilename) {
        boolean restored = DataFileManager.restoreFromBackup(backupFilename, ORDER_QUEUE_FILENAME);
        if (restored) {
            // Reload order queue after restore
            return loadOrderQueue();
        }
        return false;
    }
    
    /**
     * Restores order queue from the most recent backup.
     * @return true if restore was successful
     */
    public static boolean restoreOrderQueueFromLatestBackup() {
        boolean restored = DataFileManager.restoreFromLatestBackup(ORDER_QUEUE_FILENAME);
        if (restored) {
            // Reload order queue after restore
            return loadOrderQueue();
        }
        return false;
    }
    
    /**
     * Lists all available order backups.
     * @return Array of backup filenames for orders
     */
    public static String[] listOrderBackups() {
        return DataFileManager.listBackupsForFile(ORDERS_FILENAME);
    }
    
    /**
     * Lists all available order queue backups.
     * @return Array of backup filenames for order queue
     */
    public static String[] listOrderQueueBackups() {
        return DataFileManager.listBackupsForFile(ORDER_QUEUE_FILENAME);
    }
}
