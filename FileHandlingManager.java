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
        
        // Save configuration first (affects all other operations)
        boolean configSaved = SystemConfig.saveToFile();
        System.out.println("Configuration saved: " + configSaved);
        allSuccess &= configSaved;
        
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
        
        // Load configuration first (affects all other operations)
        boolean configLoaded = SystemConfig.loadFromFile();
        System.out.println("Configuration loaded: " + configLoaded);
        allSuccess &= configLoaded;
        
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
        
        // Backup configuration first
        boolean configBackedUp = SystemConfig.backupConfigFile();
        System.out.println("Configuration backup: " + configBackedUp);
        allSuccess &= configBackedUp;
        
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
     * Restores all system data from backups.
     * @param backupTimestamp Optional timestamp to restore from specific backup set
     * @return true if all restores were successful
     */
    public static boolean restoreAllData(String backupTimestamp) {
        System.out.println("Restoring all system data from backups...");
        
        boolean allSuccess = true;
        
        // Restore configuration first
        boolean configRestored = false;
        if (backupTimestamp != null) {
            String configBackup = "system_config_" + backupTimestamp + ".txt";
            configRestored = SystemConfig.restoreConfigFile(configBackup);
        } else {
            configRestored = SystemConfig.restoreConfigFileFromLatestBackup();
        }
        System.out.println("Configuration restore: " + configRestored);
        allSuccess &= configRestored;
        
        // Restore materials
        boolean materialsRestored = false;
        if (backupTimestamp != null) {
            String materialsBackup = "materials_" + backupTimestamp + ".txt";
            materialsRestored = MaterialFileHandler.restoreMaterials(materialsBackup);
        } else {
            materialsRestored = MaterialFileHandler.restoreMaterialsFromLatestBackup();
        }
        System.out.println("Materials restore: " + materialsRestored);
        allSuccess &= materialsRestored;
        
        // Restore users
        boolean usersRestored = false;
        if (backupTimestamp != null) {
            String usersBackup = "users_" + backupTimestamp + ".txt";
            usersRestored = UserFileHandler.restoreUsers(usersBackup);
        } else {
            usersRestored = UserFileHandler.restoreUsersFromLatestBackup();
        }
        System.out.println("Users restore: " + usersRestored);
        allSuccess &= usersRestored;
        
        // Restore inventory
        boolean inventoryRestored = false;
        if (backupTimestamp != null) {
            String inventoryBackup = "inventory_" + backupTimestamp + ".txt";
            inventoryRestored = InventoryFileHandler.restoreInventory(inventoryBackup);
        } else {
            inventoryRestored = InventoryFileHandler.restoreInventoryFromLatestBackup();
        }
        System.out.println("Inventory restore: " + inventoryRestored);
        allSuccess &= inventoryRestored;
        
        // Restore orders
        boolean ordersRestored = false;
        if (backupTimestamp != null) {
            String ordersBackup = "orders_" + backupTimestamp + ".txt";
            ordersRestored = OrderFileHandler.restoreOrders(ordersBackup);
        } else {
            ordersRestored = OrderFileHandler.restoreOrdersFromLatestBackup();
        }
        System.out.println("Orders restore: " + ordersRestored);
        allSuccess &= ordersRestored;
        
        // Restore order queue
        boolean queueRestored = false;
        if (backupTimestamp != null) {
            String queueBackup = "order_queue_" + backupTimestamp + ".txt";
            queueRestored = OrderFileHandler.restoreOrderQueue(queueBackup);
        } else {
            queueRestored = OrderFileHandler.restoreOrderQueueFromLatestBackup();
        }
        System.out.println("Order queue restore: " + queueRestored);
        allSuccess &= queueRestored;
        
        System.out.println("All restore operations completed: " + (allSuccess ? "SUCCESS" : "FAILED"));
        return allSuccess;
    }
    
    /**
     * Restores all system data from the most recent backups.
     * @return true if all restores were successful
     */
    public static boolean restoreAllDataFromLatestBackups() {
        return restoreAllData(null);
    }
    
    /**
     * Lists all available backup sets (by timestamp).
     * @return Array of backup timestamps
     */
    public static String[] listBackupSets() {
        String[] allBackups = DataFileManager.listBackupFiles();
        java.util.Set<String> timestamps = new java.util.HashSet<>();
        
        for (String backup : allBackups) {
            // Extract timestamp from backup filename
            // Format: filename_YYYYMMDD_HHMMSS.txt
            int lastUnderscore = backup.lastIndexOf('_');
            if (lastUnderscore > 0) {
                String timestamp = backup.substring(lastUnderscore + 1).replace(".txt", "");
                // Validate timestamp format (YYYYMMDD_HHMMSS)
                if (timestamp.matches("\\d{8}_\\d{6}")) {
                    timestamps.add(timestamp);
                }
            }
        }
        
        return timestamps.stream().sorted().toArray(String[]::new);
    }
    
    /**
     * Gets detailed information about all backup sets.
     * @return Map with timestamp as key and backup info as value
     */
    public static java.util.Map<String, java.util.Map<String, Object>> getBackupSetInfo() {
        String[] timestamps = listBackupSets();
        java.util.Map<String, java.util.Map<String, Object>> backupInfo = new java.util.HashMap<>();
        
        for (String timestamp : timestamps) {
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            
            // Check which files exist for this timestamp
            String[] fileTypes = {"system_config", "materials", "users", "inventory", "orders", "order_queue"};
            int existingFiles = 0;
            
            for (String fileType : fileTypes) {
                String backupFilename = fileType + "_" + timestamp + ".txt";
                java.util.Map<String, Object> fileInfo = DataFileManager.getBackupInfo(backupFilename);
                if (fileInfo != null) {
                    existingFiles++;
                    info.put(fileType + "_size", fileInfo.get("file_size_bytes"));
                }
            }
            
            info.put("existing_files", existingFiles);
            info.put("total_files", fileTypes.length);
            info.put("complete", existingFiles == fileTypes.length);
            
            backupInfo.put(timestamp, info);
        }
        
        return backupInfo;
    }
    
    /**
     * Cleans up old backup files, keeping only the most recent ones.
     * @param keepCount Number of recent backup sets to keep
     * @return Number of backup files deleted
     */
    public static int cleanupOldBackups(int keepCount) {
        System.out.println("Cleaning up old backup files (keeping " + keepCount + " most recent sets)...");
        
        String[] timestamps = listBackupSets();
        if (timestamps.length <= keepCount) {
            System.out.println("No cleanup needed - only " + timestamps.length + " backup sets exist");
            return 0;
        }
        
        int totalDeleted = 0;
        // Delete oldest backup sets (first in sorted array)
        for (int i = 0; i < timestamps.length - keepCount; i++) {
            String timestamp = timestamps[i];
            System.out.println("Deleting backup set: " + timestamp);
            
            String[] fileTypes = {"system_config", "materials", "users", "inventory", "orders", "order_queue"};
            for (String fileType : fileTypes) {
                String backupFilename = fileType + "_" + timestamp + ".txt";
                try {
                    java.nio.file.Path backupPath = java.nio.file.Paths.get("backups", backupFilename);
                    if (java.nio.file.Files.exists(backupPath)) {
                        java.nio.file.Files.delete(backupPath);
                        totalDeleted++;
                        System.out.println("  Deleted: " + backupFilename);
                    }
                } catch (java.io.IOException e) {
                    System.err.println("  Error deleting " + backupFilename + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("Cleanup completed: " + totalDeleted + " backup files deleted");
        return totalDeleted;
    }
    
    /**
     * Exports a backup management report.
     * @return Formatted report about backup status and management
     */
    public static String exportBackupManagementReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BACKUP MANAGEMENT REPORT ===\n");
        report.append("Generated: ").append(new Date()).append("\n\n");
        
        // List all backup sets
        String[] timestamps = listBackupSets();
        report.append("Total Backup Sets: ").append(timestamps.length).append("\n\n");
        
        if (timestamps.length > 0) {
            java.util.Map<String, java.util.Map<String, Object>> backupInfo = getBackupSetInfo();
            
            report.append("Backup Set Details:\n");
            report.append("==================\n");
            
            for (String timestamp : timestamps) {
                java.util.Map<String, Object> info = backupInfo.get(timestamp);
                report.append("Timestamp: ").append(timestamp).append("\n");
                report.append("  Files: ").append(info.get("existing_files")).append("/").append(info.get("total_files")).append("\n");
                report.append("  Complete: ").append(info.get("complete")).append("\n");
                
                // Show file sizes
                String[] fileTypes = {"system_config", "materials", "users", "inventory", "orders", "order_queue"};
                for (String fileType : fileTypes) {
                    Object size = info.get(fileType + "_size");
                    if (size != null) {
                        report.append("  ").append(fileType).append(": ").append(size).append(" bytes\n");
                    }
                }
                report.append("\n");
            }
        }
        
        // Show individual file backups
        report.append("Individual File Backups:\n");
        report.append("=======================\n");
        
        String[] dataFiles = DataFileManager.listDataFiles();
        for (String filename : dataFiles) {
            String[] backups = DataFileManager.listBackupsForFile(filename);
            report.append(filename).append(": ").append(backups.length).append(" backups\n");
            
            if (backups.length > 0) {
                // Show most recent backup info
                String latestBackup = backups[backups.length - 1];
                java.util.Map<String, Object> info = DataFileManager.getBackupInfo(latestBackup);
                if (info != null) {
                    report.append("  Latest: ").append(info.get("formatted_date")).append(" (")
                          .append(String.format("%.1f", info.get("file_size_kb"))).append(" KB)\n");
                }
            }
        }
        
        return report.toString();
    }
    
    /**
     * Saves the backup management report to a file.
     * @return true if save was successful
     */
    public static boolean saveBackupManagementReport() {
        String report = exportBackupManagementReport();
        return DataFileManager.writeToFile("backup_management_report.txt", report);
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
     * Exports system report to CSV format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportSystemReportToCSV(String filename) {
        return ExportManager.exportSystemReportToCSV(filename);
    }
    
    /**
     * Exports system report to CSV format with auto-generated filename.
     * @return true if export was successful
     */
    public static boolean exportSystemReportToCSV() {
        return ExportManager.exportSystemReportToCSV(null);
    }
    
    /**
     * Exports system report to JSON format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportSystemReportToJSON(String filename) {
        return ExportManager.exportSystemReportToJSON(filename);
    }
    
    /**
     * Exports system report to JSON format with auto-generated filename.
     * @return true if export was successful
     */
    public static boolean exportSystemReportToJSON() {
        return ExportManager.exportSystemReportToJSON(null);
    }
    
    /**
     * Exports all orders to CSV format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportOrdersToCSV(String filename) {
        return ExportManager.exportOrdersToCSV(filename);
    }
    
    /**
     * Exports all orders to CSV format with auto-generated filename.
     * @return true if export was successful
     */
    public static boolean exportOrdersToCSV() {
        return ExportManager.exportOrdersToCSV(null);
    }
    
    /**
     * Exports all orders to JSON format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportOrdersToJSON(String filename) {
        return ExportManager.exportOrdersToJSON(filename);
    }
    
    /**
     * Exports all orders to JSON format with auto-generated filename.
     * @return true if export was successful
     */
    public static boolean exportOrdersToJSON() {
        return ExportManager.exportOrdersToJSON(null);
    }
    
    /**
     * Exports all invoices to CSV format.
     * @param invoices Array of invoices to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoicesToCSV(Invoice[] invoices, String filename) {
        return ExportManager.exportInvoicesToCSV(invoices, filename);
    }
    
    /**
     * Exports all invoices to CSV format with auto-generated filename.
     * @param invoices Array of invoices to export
     * @return true if export was successful
     */
    public static boolean exportInvoicesToCSV(Invoice[] invoices) {
        return ExportManager.exportInvoicesToCSV(invoices, null);
    }
    
    /**
     * Exports all invoices to JSON format.
     * @param invoices Array of invoices to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoicesToJSON(Invoice[] invoices, String filename) {
        return ExportManager.exportInvoicesToJSON(invoices, filename);
    }
    
    /**
     * Exports all invoices to JSON format with auto-generated filename.
     * @param invoices Array of invoices to export
     * @return true if export was successful
     */
    public static boolean exportInvoicesToJSON(Invoice[] invoices) {
        return ExportManager.exportInvoicesToJSON(invoices, null);
    }
    
    /**
     * Lists all exported files.
     * @return Array of exported filenames
     */
    public static String[] listExportedFiles() {
        return ExportManager.listExportedFiles();
    }
    
    /**
     * Initializes the system with default data if no data exists.
     * @return true if initialization was successful
     */
    public static boolean initializeWithDefaultData() {
        System.out.println("Initializing system with default data...");
        
        try {
            // Initialize configuration first
            boolean configInitialized = SystemConfig.createDefaultConfigFile();
            System.out.println("Configuration initialized: " + configInitialized);
            
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
            User admin = new User("admin", "admin@printflow.com", "admin", "AdminPass123!");
            User customer1 = new User("john_doe", "john@example.com", "customer", "CustomerPass123!");
            User customer2 = new User("jane_smith", "jane@example.com", "vip", "VipPass123!");
            
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
        
        // Check configuration
        boolean configExists = SystemConfig.configFileExists();
        boolean configValid = SystemConfig.validateConfigFile();
        System.out.println("Configuration file: " + (configExists ? "EXISTS" : "MISSING") + 
                          " / " + (configValid ? "VALID" : "INVALID"));
        healthy &= configExists && configValid;
        
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
        
        // Test backup management
        System.out.println("\n=== BACKUP MANAGEMENT TEST ===");
        
        // List backup sets
        String[] backupSets = listBackupSets();
        System.out.println("Available backup sets: " + backupSets.length);
        for (String timestamp : backupSets) {
            System.out.println("  " + timestamp);
        }
        
        // Test backup management report
        boolean backupReportSaved = saveBackupManagementReport();
        System.out.println("Backup management report saved: " + backupReportSaved);
        
        // Test restore functionality (if backups exist)
        if (backupSets.length > 0) {
            System.out.println("\n=== RESTORE FUNCTIONALITY TEST ===");
            
            // Test individual file restore
            String[] materialBackups = MaterialFileHandler.listMaterialBackups();
            if (materialBackups.length > 0) {
                System.out.println("Testing material restore from: " + materialBackups[materialBackups.length - 1]);
                boolean materialRestored = MaterialFileHandler.restoreMaterialsFromLatestBackup();
                System.out.println("Material restore: " + (materialRestored ? "SUCCESS" : "FAILED"));
            }
            
            // Test full system restore
            System.out.println("Testing full system restore from latest backups...");
            boolean restoreSuccess = restoreAllDataFromLatestBackups();
            System.out.println("Full system restore: " + (restoreSuccess ? "SUCCESS" : "FAILED"));
        }
        
        // Test cleanup (keep only 2 most recent backup sets)
        System.out.println("\n=== BACKUP CLEANUP TEST ===");
        int deletedCount = cleanupOldBackups(2);
        System.out.println("Backup cleanup: " + deletedCount + " files deleted");
    }
}
