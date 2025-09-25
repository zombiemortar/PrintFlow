import java.util.*;

/**
 * Central manager for all file handling operations.
 * Provides integrated save/load functionality for the entire system.
 */
public class FileHandlingManager {
    
    /**
     * Saves all system data to files.
     * @return true if all saves were successful
     */
    public static boolean saveAllData() {
        System.out.println("Saving all system data...");
        
        boolean allSuccess = true;
        
        // Save materials
        boolean materialsSaved = MaterialFileHandler.saveMaterials();
        System.out.println("Materials saved: " + materialsSaved);
        allSuccess &= materialsSaved;
        
        // Save users
        boolean usersSaved = UserFileHandler.saveUsers();
        System.out.println("Users saved: " + usersSaved);
        allSuccess &= usersSaved;
        
        // Save inventory
        boolean inventorySaved = InventoryFileHandler.saveInventory();
        System.out.println("Inventory saved: " + inventorySaved);
        allSuccess &= inventorySaved;
        
        // Save orders
        boolean ordersSaved = OrderFileHandler.saveOrders();
        System.out.println("Orders saved: " + ordersSaved);
        allSuccess &= ordersSaved;
        
        // Save order queue
        boolean queueSaved = OrderFileHandler.saveOrderQueue();
        System.out.println("Order queue saved: " + queueSaved);
        allSuccess &= queueSaved;
        
        System.out.println("All data save operation completed: " + (allSuccess ? "SUCCESS" : "FAILED"));
        return allSuccess;
    }
    
    /**
     * Loads all system data from files.
     * @return true if all loads were successful
     */
    public static boolean loadAllData() {
        System.out.println("Loading all system data...");
        
        boolean allSuccess = true;
        
        // Load materials first (needed for other operations)
        boolean materialsLoaded = MaterialFileHandler.loadMaterials();
        System.out.println("Materials loaded: " + materialsLoaded);
        allSuccess &= materialsLoaded;
        
        // Load users
        boolean usersLoaded = UserFileHandler.loadUsers();
        System.out.println("Users loaded: " + usersLoaded);
        allSuccess &= usersLoaded;
        
        // Load inventory
        boolean inventoryLoaded = InventoryFileHandler.loadInventory();
        System.out.println("Inventory loaded: " + inventoryLoaded);
        allSuccess &= inventoryLoaded;
        
        // Load orders
        boolean ordersLoaded = OrderFileHandler.loadOrders();
        System.out.println("Orders loaded: " + ordersLoaded);
        allSuccess &= ordersLoaded;
        
        // Load order queue
        boolean queueLoaded = OrderFileHandler.loadOrderQueue();
        System.out.println("Order queue loaded: " + queueLoaded);
        allSuccess &= queueLoaded;
        
        System.out.println("All data load operation completed: " + (allSuccess ? "SUCCESS" : "FAILED"));
        return allSuccess;
    }
    
    /**
     * Creates backups of all system files.
     * @return true if all backups were successful
     */
    public static boolean backupAllData() {
        System.out.println("Creating backups of all system data...");
        
        boolean allSuccess = true;
        
        // Backup materials
        boolean materialsBackedUp = MaterialFileHandler.backupMaterials();
        System.out.println("Materials backup: " + materialsBackedUp);
        allSuccess &= materialsBackedUp;
        
        // Backup users
        boolean usersBackedUp = UserFileHandler.backupUsers();
        System.out.println("Users backup: " + usersBackedUp);
        allSuccess &= usersBackedUp;
        
        // Backup inventory
        boolean inventoryBackedUp = InventoryFileHandler.backupInventory();
        System.out.println("Inventory backup: " + inventoryBackedUp);
        allSuccess &= inventoryBackedUp;
        
        // Backup orders
        boolean ordersBackedUp = OrderFileHandler.backupOrders();
        System.out.println("Orders backup: " + ordersBackedUp);
        allSuccess &= ordersBackedUp;
        
        // Backup order queue
        boolean queueBackedUp = OrderFileHandler.backupOrderQueue();
        System.out.println("Order queue backup: " + queueBackedUp);
        allSuccess &= queueBackedUp;
        
        System.out.println("All backup operations completed: " + (allSuccess ? "SUCCESS" : "FAILED"));
        return allSuccess;
    }
    
    /**
     * Clears all system data from memory.
     */
    public static void clearAllData() {
        System.out.println("Clearing all system data from memory...");
        
        MaterialFileHandler.clearMaterials();
        UserFileHandler.clearUsers();
        InventoryFileHandler.clearInventory();
        OrderManager.clearAllOrders();
        
        System.out.println("All system data cleared from memory");
    }
    
    /**
     * Exports a comprehensive system report.
     * @return The formatted report as a string
     */
    public static String exportSystemReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== COMPREHENSIVE SYSTEM REPORT ===\n");
        report.append("Generated: ").append(new Date()).append("\n\n");
        
        // Materials report
        report.append(MaterialFileHandler.exportMaterialsReport()).append("\n");
        
        // Users report
        report.append(UserFileHandler.exportUsersReport()).append("\n");
        
        // Inventory report
        report.append(InventoryFileHandler.exportInventoryReport(null)).append("\n");
        
        // Orders summary
        Order[] orders = OrderManager.getAllOrders();
        report.append("=== ORDERS SUMMARY ===\n");
        report.append("Total Orders: ").append(orders.length).append("\n");
        report.append("Queue Size: ").append(OrderManager.getQueueSize()).append("\n\n");
        
        // Order details
        for (int i = 0; i < Math.min(orders.length, 10); i++) {
            Order order = orders[i];
            report.append("Order #").append(order.getOrderID()).append(": ");
            report.append(order.getStatus()).append(" - ");
            report.append(order.getQuantity()).append(" items - ");
            report.append("$").append(String.format("%.2f", order.calculatePrice())).append("\n");
        }
        
        if (orders.length > 10) {
            report.append("... and ").append(orders.length - 10).append(" more orders\n");
        }
        
        return report.toString();
    }
    
    /**
     * Saves the system report to a file.
     * @return true if save was successful
     */
    public static boolean saveSystemReport() {
        String report = exportSystemReport();
        return DataFileManager.writeToFile("system_report.txt", report);
    }
    
    /**
     * Initializes the system with default data if no data exists.
     * @return true if initialization was successful
     */
    public static boolean initializeWithDefaultData() {
        System.out.println("Initializing system with default data...");
        
        try {
            // Check if data already exists
            if (MaterialFileHandler.getMaterialCount() > 0 || 
                UserFileHandler.getUserCount() > 0) {
                System.out.println("System already has data, skipping initialization");
                return true;
            }
            
            // Create default materials
            Material pla = new Material("PLA", 0.05, 200, "Blue");
            Material abs = new Material("ABS", 0.08, 250, "Red");
            Material petg = new Material("PETG", 0.07, 240, "Clear");
            
            MaterialFileHandler.addMaterial(pla);
            MaterialFileHandler.addMaterial(abs);
            MaterialFileHandler.addMaterial(petg);
            
            // Create default users
            User admin = new User("admin", "admin@printflow.com", "admin");
            User customer1 = new User("john_doe", "john@example.com", "customer");
            User customer2 = new User("jane_smith", "jane@example.com", "vip");
            
            UserFileHandler.addUser(admin);
            UserFileHandler.addUser(customer1);
            UserFileHandler.addUser(customer2);
            
            // Set default inventory
            InventoryFileHandler.setStock("PLA", 5000);
            InventoryFileHandler.setStock("ABS", 3000);
            InventoryFileHandler.setStock("PETG", 2000);
            
            // Save default data
            boolean saveSuccess = saveAllData();
            System.out.println("Default data initialization: " + (saveSuccess ? "SUCCESS" : "FAILED"));
            
            return saveSuccess;
            
        } catch (Exception e) {
            System.err.println("Error initializing default data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Performs system health check.
     * @return true if system is healthy
     */
    public static boolean performHealthCheck() {
        System.out.println("Performing system health check...");
        
        boolean healthy = true;
        
        // Check data directories
        boolean dataDirExists = DataFileManager.ensureDataDirectory();
        System.out.println("Data directory: " + (dataDirExists ? "OK" : "MISSING"));
        healthy &= dataDirExists;
        
        // Check material data
        int materialCount = MaterialFileHandler.getMaterialCount();
        System.out.println("Materials: " + materialCount + " (expected: >0)");
        healthy &= materialCount > 0;
        
        // Check user data
        int userCount = UserFileHandler.getUserCount();
        System.out.println("Users: " + userCount + " (expected: >0)");
        healthy &= userCount > 0;
        
        // Check inventory data
        int inventoryCount = InventoryFileHandler.getInventoryItemCount();
        System.out.println("Inventory items: " + inventoryCount + " (expected: >0)");
        healthy &= inventoryCount > 0;
        
        // Check for low stock
        Map<String, Integer> lowStock = InventoryFileHandler.getLowStockMaterials(500);
        if (!lowStock.isEmpty()) {
            System.out.println("WARNING: Low stock items: " + lowStock.size());
            for (Map.Entry<String, Integer> entry : lowStock.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " grams");
            }
        }
        
        // Check orders
        Order[] orders = OrderManager.getAllOrders();
        System.out.println("Orders: " + orders.length);
        
        int queueSize = OrderManager.getQueueSize();
        System.out.println("Queue size: " + queueSize);
        
        System.out.println("System health check: " + (healthy ? "HEALTHY" : "ISSUES FOUND"));
        return healthy;
    }
    
    /**
     * Gets system statistics.
     * @return Map containing system statistics
     */
    public static Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("materials_count", MaterialFileHandler.getMaterialCount());
        stats.put("users_count", UserFileHandler.getUserCount());
        stats.put("inventory_items_count", InventoryFileHandler.getInventoryItemCount());
        stats.put("orders_count", OrderManager.getAllOrders().length);
        stats.put("queue_size", OrderManager.getQueueSize());
        
        // Calculate total inventory value
        double totalValue = InventoryFileHandler.getTotalInventoryValue(null);
        stats.put("total_inventory_value", totalValue);
        
        // Count users by role
        User[] customers = UserFileHandler.getUsersByRole("customer");
        User[] admins = UserFileHandler.getUsersByRole("admin");
        User[] vips = UserFileHandler.getUsersByRole("vip");
        
        stats.put("customer_count", customers.length);
        stats.put("admin_count", admins.length);
        stats.put("vip_count", vips.length);
        
        return stats;
    }
    
    /**
     * Main method for testing the file handling manager.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== FILE HANDLING MANAGER TEST ===");
        
        // Test initialization
        boolean initSuccess = initializeWithDefaultData();
        System.out.println("Initialization: " + (initSuccess ? "SUCCESS" : "FAILED"));
        
        // Test health check
        boolean healthCheck = performHealthCheck();
        System.out.println("Health check: " + (healthCheck ? "PASSED" : "FAILED"));
        
        // Test system statistics
        Map<String, Object> stats = getSystemStatistics();
        System.out.println("\nSystem Statistics:");
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Test report generation
        boolean reportSaved = saveSystemReport();
        System.out.println("\nSystem report saved: " + reportSaved);
        
        // Test backup
        boolean backupSuccess = backupAllData();
        System.out.println("Backup operation: " + (backupSuccess ? "SUCCESS" : "FAILED"));
    }
}
