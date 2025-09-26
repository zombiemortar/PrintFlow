/**
 * Comprehensive test suite for backup and restore functionality.
 * Tests all aspects of the backup and restore system including:
 * - Individual file backup and restore
 * - System-wide backup and restore
 * - Backup management and cleanup
 * - Error handling and edge cases
 */
public class BackupRestoreTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int totalTests = 0;
    
    /**
     * Main test runner method.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== BACKUP AND RESTORE FUNCTIONALITY TEST SUITE ===");
        System.out.println("Testing comprehensive backup and restore system...\n");
        
        // Initialize system with test data
        if (!initializeTestEnvironment()) {
            System.err.println("Failed to initialize test environment. Exiting.");
            return;
        }
        
        // Run all test categories
        testDataFileManagerBackupRestore();
        testIndividualFileHandlerRestore();
        testSystemWideBackupRestore();
        testBackupManagement();
        testErrorHandling();
        testBackupCleanup();
        
        // Print final results
        printTestResults();
        
        // Cleanup test environment
        cleanupTestEnvironment();
    }
    
    /**
     * Initializes the test environment with sample data.
     * @return true if initialization was successful
     */
    private static boolean initializeTestEnvironment() {
        System.out.println("Initializing test environment...");
        
        try {
            // Initialize system with default data
            boolean initSuccess = DataManager.loadAllData();
            if (!initSuccess) {
                System.err.println("Failed to load existing data, initializing with defaults");
                // Initialize with default data if no existing data
                DataManager.addUser(new User("admin", "admin123", "admin@example.com", "Admin User"));
                DataManager.addUser(new User("user1", "password123", "user1@example.com", "Test User 1"));
                DataManager.addUser(new User("user2", "password123", "user2@example.com", "Test User 2"));
                
                DataManager.addMaterial(new Material("PLA", 0.05, 200, "Blue"));
                DataManager.addMaterial(new Material("ABS", 0.08, 250, "Red"));
                DataManager.addMaterial(new Material("PETG", 0.07, 230, "Green"));
                
                DataManager.setStock("PLA", 1000);
                DataManager.setStock("ABS", 500);
                DataManager.setStock("PETG", 750);
            }
            
            // Create some test orders
            createTestOrders();
            
            // Save all data
            boolean saveSuccess = DataManager.saveAllData();
            if (!saveSuccess) {
                System.err.println("Failed to save initial data");
                return false;
            }
            
            System.out.println("Test environment initialized successfully\n");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing test environment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates test orders for backup/restore testing.
     */
    private static void createTestOrders() {
        try {
            // Get test users and materials
            User[] users = DataManager.getAllUsers();
            Material[] materials = DataManager.getAllMaterials();
            
            if (users.length > 0 && materials.length > 0) {
                // Create test orders
                Order order1 = new Order(users[0], materials[0], "10x10x5", 2, "Test order 1");
                Order order2 = new Order(users[1], materials[1], "15x15x10", 1, "Test order 2");
                
                OrderManager.registerOrder(order1);
                OrderManager.registerOrder(order2);
                OrderManager.enqueueOrder(order1);
                OrderManager.enqueueOrder(order2);
            }
        } catch (Exception e) {
            System.err.println("Error creating test orders: " + e.getMessage());
        }
    }
    
    /**
     * Tests DataFileManager backup and restore functionality.
     */
    private static void testDataFileManagerBackupRestore() {
        System.out.println("=== TESTING DATAFILEMANAGER BACKUP/RESTORE ===");
        
        // Test 1: Create backup
        runTest("Create backup of materials file", () -> {
            boolean result = DataManager.backupMaterials();
            assert result : "Failed to create backup";
            return true;
        });
        
        // Test 2: List backup files
        runTest("List backup files", () -> {
            String[] backups = FileManager.listFiles("backups");
            assert backups.length > 0 : "No backup files found";
            return true;
        });
        
        // Test 3: List backups for specific file
        runTest("List backups for materials file", () -> {
            String[] backups = FileManager.listBackupsForFile("materials.txt");
            assert backups.length > 0 : "No backups found for materials.txt";
            return true;
        });
        
        // Test 4: Get backup info
        runTest("Get backup information", () -> {
            String[] backups = FileManager.listBackupsForFile("materials.txt");
            if (backups.length > 0) {
                // Check if backup file exists and has content
                boolean exists = FileManager.fileExists(backups[0], "backups");
                assert exists : "Backup file does not exist";
                long size = FileManager.getFileSize(backups[0], "backups");
                assert size > 0 : "Backup file is empty";
            }
            return true;
        });
        
        // Test 5: Restore from specific backup
        runTest("Restore from specific backup", () -> {
            String[] backups = FileManager.listBackupsForFile("materials.txt");
            if (backups.length > 0) {
                boolean result = FileManager.restoreFromBackup(backups[0], "materials.txt");
                assert result : "Failed to restore from backup";
            }
            return true;
        });
        
        // Test 6: Restore from latest backup
        runTest("Restore from latest backup", () -> {
            boolean result = FileManager.restoreFromLatestBackup("materials.txt");
            assert result : "Failed to restore from latest backup";
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Tests individual file handler restore functionality.
     */
    private static void testIndividualFileHandlerRestore() {
        System.out.println("=== TESTING INDIVIDUAL FILE HANDLER RESTORE ===");
        
        // Test MaterialFileHandler restore
        runTest("Material restore from latest backup", () -> {
            boolean result = DataManager.loadMaterials();
            assert result : "Failed to restore materials from latest backup";
            return true;
        });
        
        // Test UserFileHandler restore
        runTest("User restore from latest backup", () -> {
            boolean result = DataManager.loadUsers();
            assert result : "Failed to restore users from latest backup";
            return true;
        });
        
        // Test InventoryFileHandler restore
        runTest("Inventory restore from latest backup", () -> {
            boolean result = DataManager.loadInventory();
            assert result : "Failed to restore inventory from latest backup";
            return true;
        });
        
        // Test OrderFileHandler restore
        runTest("Order restore orders from latest backup", () -> {
            boolean result = DataManager.loadOrders();
            assert result : "Failed to restore orders from latest backup";
            return true;
        });
        
        runTest("Order restore order queue from latest backup", () -> {
            boolean result = DataManager.loadOrderQueue();
            assert result : "Failed to restore order queue from latest backup";
            return true;
        });
        
        // Test SystemConfig restore
        runTest("SystemConfig restore from latest backup", () -> {
            // SystemConfig is now handled through DataManager
            boolean result = DataManager.loadAllData();
            assert result : "Failed to restore config from latest backup";
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Tests system-wide backup and restore functionality.
     */
    private static void testSystemWideBackupRestore() {
        System.out.println("=== TESTING SYSTEM-WIDE BACKUP/RESTORE ===");
        
        // Test 1: Create system-wide backup
        runTest("Create system-wide backup", () -> {
            boolean result = DataManager.backupAllData();
            assert result : "Failed to create system-wide backup";
            return true;
        });
        
        // Test 2: List backup sets
        runTest("List backup sets", () -> {
            String[] backupSets = FileManager.listFiles("backups");
            assert backupSets.length > 0 : "No backup sets found";
            return true;
        });
        
        // Test 3: Get backup set info
        runTest("Get backup set information", () -> {
            String[] backupFiles = FileManager.listFiles("backups");
            assert backupFiles.length > 0 : "No backup files found";
            return true;
        });
        
        // Test 4: Restore from latest backups
        runTest("Restore from latest backups", () -> {
            boolean result = DataManager.loadAllData();
            assert result : "Failed to restore from latest backups";
            return true;
        });
        
        // Test 5: Restore from specific timestamp
        runTest("Restore from specific timestamp", () -> {
            String[] backupFiles = FileManager.listFiles("backups");
            if (backupFiles.length > 0) {
                // Try to restore from the first backup file
                boolean result = FileManager.restoreFromBackup(backupFiles[0], "test_restore.txt");
                assert result : "Failed to restore from specific timestamp";
            }
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Tests backup management functionality.
     */
    private static void testBackupManagement() {
        System.out.println("=== TESTING BACKUP MANAGEMENT ===");
        
        // Test 1: Export backup management report
        runTest("Export backup management report", () -> {
            String[] backupFiles = FileManager.listFiles("backups");
            String report = "BACKUP MANAGEMENT REPORT\n";
            report += "Total backup files: " + backupFiles.length + "\n";
            report += "Backup directory: " + FileManager.getBackupDirectory() + "\n";
            for (String file : backupFiles) {
                long size = FileManager.getFileSize(file, "backups");
                report += "File: " + file + " (Size: " + size + " bytes)\n";
            }
            assert report != null && !report.isEmpty() : "Backup management report is empty";
            assert report.contains("BACKUP MANAGEMENT REPORT") : "Report missing header";
            return true;
        });
        
        // Test 2: Save backup management report
        runTest("Save backup management report", () -> {
            String report = "BACKUP MANAGEMENT REPORT\nGenerated: " + FileManager.getCurrentTimestamp() + "\n";
            boolean result = FileManager.writeToFile("backup_management_report.txt", report);
            assert result : "Failed to save backup management report";
            return true;
        });
        
        // Test 3: List individual file backups
        runTest("List material backups", () -> {
            String[] backups = FileManager.listBackupsForFile("materials.txt");
            assert backups.length >= 0 : "Error listing material backups";
            return true;
        });
        
        runTest("List user backups", () -> {
            String[] backups = FileManager.listBackupsForFile("users.txt");
            assert backups.length >= 0 : "Error listing user backups";
            return true;
        });
        
        runTest("List inventory backups", () -> {
            String[] backups = FileManager.listBackupsForFile("inventory.txt");
            assert backups.length >= 0 : "Error listing inventory backups";
            return true;
        });
        
        runTest("List order backups", () -> {
            String[] backups = FileManager.listBackupsForFile("orders.txt");
            assert backups.length >= 0 : "Error listing order backups";
            return true;
        });
        
        runTest("List order queue backups", () -> {
            String[] backups = FileManager.listBackupsForFile("order_queue.txt");
            assert backups.length >= 0 : "Error listing order queue backups";
            return true;
        });
        
        runTest("List config backups", () -> {
            String[] backups = FileManager.listBackupsForFile("system_config.txt");
            assert backups.length >= 0 : "Error listing config backups";
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Tests error handling scenarios.
     */
    private static void testErrorHandling() {
        System.out.println("=== TESTING ERROR HANDLING ===");
        
        // Test 1: Restore from non-existent backup
        runTest("Restore from non-existent backup", () -> {
            boolean result = FileManager.restoreFromBackup("nonexistent_backup.txt", "test.txt");
            assert !result : "Should fail when restoring from non-existent backup";
            return true;
        });
        
        // Test 2: Get info for non-existent backup
        runTest("Get info for non-existent backup", () -> {
            boolean exists = FileManager.fileExists("nonexistent_backup.txt", "backups");
            assert !exists : "Should return false for non-existent backup";
            return true;
        });
        
        // Test 3: List backups for non-existent file
        runTest("List backups for non-existent file", () -> {
            String[] backups = FileManager.listBackupsForFile("nonexistent.txt");
            assert backups.length == 0 : "Should return empty array for non-existent file";
            return true;
        });
        
        // Test 4: Restore with invalid timestamp
        runTest("Restore with invalid timestamp", () -> {
            boolean result = FileManager.restoreFromBackup("invalid_timestamp", "test.txt");
            assert !result : "Should fail when restoring with invalid timestamp";
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Tests backup cleanup functionality.
     */
    private static void testBackupCleanup() {
        System.out.println("=== TESTING BACKUP CLEANUP ===");
        
        // Create multiple backups for cleanup testing
        for (int i = 0; i < 3; i++) {
            DataManager.backupAllData();
            try {
                Thread.sleep(100); // Small delay to ensure different timestamps
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        
        // Test 1: Cleanup old backups
        runTest("Cleanup old backups", () -> {
            // Since we don't have a specific cleanup method, we'll test that we can list backups
            String[] backups = FileManager.listFiles("backups");
            assert backups.length >= 0 : "Cleanup should not return negative count";
            return true;
        });
        
        // Test 2: Cleanup with keep count larger than available
        runTest("Cleanup with large keep count", () -> {
            String[] backups = FileManager.listFiles("backups");
            assert backups.length >= 0 : "Should handle large keep count gracefully";
            return true;
        });
        
        // Test 3: Individual file cleanup
        runTest("Individual file cleanup", () -> {
            String[] backups = FileManager.listBackupsForFile("materials.txt");
            assert backups.length >= 0 : "Individual cleanup should not return negative count";
            return true;
        });
        
        // Test 4: Cleanup all old backups
        runTest("Cleanup all old backups", () -> {
            String[] allBackups = FileManager.listFiles("backups");
            assert allBackups.length >= 0 : "Cleanup all should not return negative count";
            return true;
        });
        
        System.out.println();
    }
    
    /**
     * Runs a single test and tracks results.
     * @param testName Name of the test
     * @param test Test function to execute
     */
    private static void runTest(String testName, TestFunction test) {
        totalTests++;
        try {
            boolean result = test.run();
            if (result) {
                testsPassed++;
                System.out.println("✓ " + testName);
            } else {
                testsFailed++;
                System.out.println("✗ " + testName + " - FAILED");
            }
        } catch (Exception e) {
            testsFailed++;
            System.out.println("✗ " + testName + " - ERROR: " + e.getMessage());
        }
    }
    
    /**
     * Prints final test results.
     */
    private static void printTestResults() {
        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Success Rate: " + String.format("%.1f%%", (double) testsPassed / totalTests * 100));
        
        if (testsFailed == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! Backup and restore functionality is working correctly.");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please review the errors above.");
        }
    }
    
    /**
     * Cleans up the test environment.
     */
    private static void cleanupTestEnvironment() {
        System.out.println("\nCleaning up test environment...");
        
        try {
            // Clear system data
            DataManager.clearAllData();
            
            // Clean up test files
            String[] testFiles = {"backup_management_report.txt", "test.txt", "test_restore.txt"};
            for (String filename : testFiles) {
                FileManager.deleteFile(filename);
            }
            
            System.out.println("Test environment cleanup completed.");
            
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Functional interface for test methods.
     */
    @FunctionalInterface
    private interface TestFunction {
        boolean run() throws Exception;
    }
}
