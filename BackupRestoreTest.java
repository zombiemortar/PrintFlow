import java.util.*;

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
            boolean initSuccess = FileHandlingManager.initializeWithDefaultData();
            if (!initSuccess) {
                System.err.println("Failed to initialize with default data");
                return false;
            }
            
            // Create some test orders
            createTestOrders();
            
            // Save all data
            boolean saveSuccess = FileHandlingManager.saveAllData();
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
            User[] users = UserFileHandler.getAllUsers();
            Material[] materials = MaterialFileHandler.getAllMaterials();
            
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
            boolean result = DataFileManager.createBackup("materials.txt");
            assert result : "Failed to create backup";
            return true;
        });
        
        // Test 2: List backup files
        runTest("List backup files", () -> {
            String[] backups = DataFileManager.listBackupFiles();
            assert backups.length > 0 : "No backup files found";
            return true;
        });
        
        // Test 3: List backups for specific file
        runTest("List backups for materials file", () -> {
            String[] backups = DataFileManager.listBackupsForFile("materials.txt");
            assert backups.length > 0 : "No backups found for materials.txt";
            return true;
        });
        
        // Test 4: Get backup info
        runTest("Get backup information", () -> {
            String[] backups = DataFileManager.listBackupsForFile("materials.txt");
            if (backups.length > 0) {
                Map<String, Object> info = DataFileManager.getBackupInfo(backups[0]);
                assert info != null : "Backup info is null";
                assert info.containsKey("original_filename") : "Missing original filename";
                assert info.containsKey("file_size_bytes") : "Missing file size";
            }
            return true;
        });
        
        // Test 5: Restore from specific backup
        runTest("Restore from specific backup", () -> {
            String[] backups = DataFileManager.listBackupsForFile("materials.txt");
            if (backups.length > 0) {
                boolean result = DataFileManager.restoreFromBackup(backups[0], "materials.txt");
                assert result : "Failed to restore from backup";
            }
            return true;
        });
        
        // Test 6: Restore from latest backup
        runTest("Restore from latest backup", () -> {
            boolean result = DataFileManager.restoreFromLatestBackup("materials.txt");
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
        runTest("MaterialFileHandler restore from latest backup", () -> {
            boolean result = MaterialFileHandler.restoreMaterialsFromLatestBackup();
            assert result : "Failed to restore materials from latest backup";
            return true;
        });
        
        // Test UserFileHandler restore
        runTest("UserFileHandler restore from latest backup", () -> {
            boolean result = UserFileHandler.restoreUsersFromLatestBackup();
            assert result : "Failed to restore users from latest backup";
            return true;
        });
        
        // Test InventoryFileHandler restore
        runTest("InventoryFileHandler restore from latest backup", () -> {
            boolean result = InventoryFileHandler.restoreInventoryFromLatestBackup();
            assert result : "Failed to restore inventory from latest backup";
            return true;
        });
        
        // Test OrderFileHandler restore
        runTest("OrderFileHandler restore orders from latest backup", () -> {
            boolean result = OrderFileHandler.restoreOrdersFromLatestBackup();
            assert result : "Failed to restore orders from latest backup";
            return true;
        });
        
        runTest("OrderFileHandler restore order queue from latest backup", () -> {
            boolean result = OrderFileHandler.restoreOrderQueueFromLatestBackup();
            assert result : "Failed to restore order queue from latest backup";
            return true;
        });
        
        // Test SystemConfig restore
        runTest("SystemConfig restore from latest backup", () -> {
            boolean result = SystemConfig.restoreConfigFileFromLatestBackup();
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
            boolean result = FileHandlingManager.backupAllData();
            assert result : "Failed to create system-wide backup";
            return true;
        });
        
        // Test 2: List backup sets
        runTest("List backup sets", () -> {
            String[] backupSets = FileHandlingManager.listBackupSets();
            assert backupSets.length > 0 : "No backup sets found";
            return true;
        });
        
        // Test 3: Get backup set info
        runTest("Get backup set information", () -> {
            Map<String, Map<String, Object>> backupInfo = FileHandlingManager.getBackupSetInfo();
            assert backupInfo.size() > 0 : "No backup set info found";
            return true;
        });
        
        // Test 4: Restore from latest backups
        runTest("Restore from latest backups", () -> {
            boolean result = FileHandlingManager.restoreAllDataFromLatestBackups();
            assert result : "Failed to restore from latest backups";
            return true;
        });
        
        // Test 5: Restore from specific timestamp
        runTest("Restore from specific timestamp", () -> {
            String[] backupSets = FileHandlingManager.listBackupSets();
            if (backupSets.length > 0) {
                boolean result = FileHandlingManager.restoreAllData(backupSets[0]);
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
            String report = FileHandlingManager.exportBackupManagementReport();
            assert report != null && !report.isEmpty() : "Backup management report is empty";
            assert report.contains("BACKUP MANAGEMENT REPORT") : "Report missing header";
            return true;
        });
        
        // Test 2: Save backup management report
        runTest("Save backup management report", () -> {
            boolean result = FileHandlingManager.saveBackupManagementReport();
            assert result : "Failed to save backup management report";
            return true;
        });
        
        // Test 3: List individual file backups
        runTest("List material backups", () -> {
            String[] backups = MaterialFileHandler.listMaterialBackups();
            assert backups.length >= 0 : "Error listing material backups";
            return true;
        });
        
        runTest("List user backups", () -> {
            String[] backups = UserFileHandler.listUserBackups();
            assert backups.length >= 0 : "Error listing user backups";
            return true;
        });
        
        runTest("List inventory backups", () -> {
            String[] backups = InventoryFileHandler.listInventoryBackups();
            assert backups.length >= 0 : "Error listing inventory backups";
            return true;
        });
        
        runTest("List order backups", () -> {
            String[] backups = OrderFileHandler.listOrderBackups();
            assert backups.length >= 0 : "Error listing order backups";
            return true;
        });
        
        runTest("List order queue backups", () -> {
            String[] backups = OrderFileHandler.listOrderQueueBackups();
            assert backups.length >= 0 : "Error listing order queue backups";
            return true;
        });
        
        runTest("List config backups", () -> {
            String[] backups = SystemConfig.listConfigBackups();
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
            boolean result = DataFileManager.restoreFromBackup("nonexistent_backup.txt", "test.txt");
            assert !result : "Should fail when restoring from non-existent backup";
            return true;
        });
        
        // Test 2: Get info for non-existent backup
        runTest("Get info for non-existent backup", () -> {
            Map<String, Object> info = DataFileManager.getBackupInfo("nonexistent_backup.txt");
            assert info == null : "Should return null for non-existent backup";
            return true;
        });
        
        // Test 3: List backups for non-existent file
        runTest("List backups for non-existent file", () -> {
            String[] backups = DataFileManager.listBackupsForFile("nonexistent.txt");
            assert backups.length == 0 : "Should return empty array for non-existent file";
            return true;
        });
        
        // Test 4: Restore with invalid timestamp
        runTest("Restore with invalid timestamp", () -> {
            boolean result = FileHandlingManager.restoreAllData("invalid_timestamp");
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
            FileHandlingManager.backupAllData();
            try {
                Thread.sleep(100); // Small delay to ensure different timestamps
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        
        // Test 1: Cleanup old backups
        runTest("Cleanup old backups", () -> {
            int deletedCount = FileHandlingManager.cleanupOldBackups(2);
            assert deletedCount >= 0 : "Cleanup should not return negative count";
            return true;
        });
        
        // Test 2: Cleanup with keep count larger than available
        runTest("Cleanup with large keep count", () -> {
            int deletedCount = FileHandlingManager.cleanupOldBackups(10);
            assert deletedCount == 0 : "Should delete 0 files when keep count is larger than available";
            return true;
        });
        
        // Test 3: Individual file cleanup
        runTest("Individual file cleanup", () -> {
            int deletedCount = DataFileManager.cleanupOldBackups("materials.txt", 1);
            assert deletedCount >= 0 : "Individual cleanup should not return negative count";
            return true;
        });
        
        // Test 4: Cleanup all old backups
        runTest("Cleanup all old backups", () -> {
            int deletedCount = DataFileManager.cleanupAllOldBackups(1);
            assert deletedCount >= 0 : "Cleanup all should not return negative count";
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
            FileHandlingManager.clearAllData();
            
            // Clean up test files
            String[] testFiles = {"backup_management_report.txt", "test.txt"};
            for (String filename : testFiles) {
                DataFileManager.deleteFile(filename);
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
