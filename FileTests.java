import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Simplified file operations tests for data persistence, backup/restore, and file handling.
 * Consolidates FileHandlingTest and BackupRestoreTest functionality.
 */
public class FileTests {
    
    private User testUser;
    private Material testMaterial;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        testUser = new User("fileuser", "file@example.com", "customer", "FilePass123!");
        testMaterial = new Material("FilePLA", 0.05, 200, "Blue");
        testOrder = testUser.submitOrder(testMaterial, "10x10x5", 2, "File test order");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test files
        FileManager.deleteFile("test_materials.txt");
        FileManager.deleteFile("test_users.txt");
        FileManager.deleteFile("test_orders.txt");
        FileManager.deleteFile("test_inventory.txt");
    }
    
    // ==================== BASIC FILE OPERATIONS ====================
    
    @Test
    void testFileManagerBasicOperations() {
        // Test directory creation
        assertTrue(FileManager.ensureDirectory("test_data"));
        assertTrue(FileManager.ensureDirectory("test_backups"));
        assertTrue(FileManager.ensureDirectory("test_exports"));
        
        // Test file writing and reading
        String testData = "Test file content\nLine 2\nLine 3";
        assertTrue(FileManager.writeToFile("test_file.txt", testData, "test_data"));
        
        String readData = FileManager.readFromFile("test_file.txt", "test_data");
        assertEquals(testData, readData);
        
        // Test file existence
        assertTrue(FileManager.fileExists("test_file.txt", "test_data"));
        
        // Test file size
        long fileSize = FileManager.getFileSize("test_file.txt", "test_data");
        assertTrue(fileSize > 0);
        
        // Test file listing
        String[] files = FileManager.listFiles("test_data");
        assertTrue(files.length > 0);
        
        // Test file deletion
        assertTrue(FileManager.deleteFile("test_file.txt", "test_data"));
        assertFalse(FileManager.fileExists("test_file.txt", "test_data"));
    }
    
    @Test
    void testFileManagerErrorHandling() {
        // Test null inputs
        assertFalse(FileManager.writeToFile(null, "data", "test_data"));
        assertFalse(FileManager.writeToFile("test.txt", null, "test_data"));
        assertFalse(FileManager.writeToFile("test.txt", "data", null));
        
        // Test reading non-existent file
        String data = FileManager.readFromFile("nonexistent.txt", "test_data");
        assertNull(data);
        
        // Test file operations on non-existent directory
        assertFalse(FileManager.writeToFile("test.txt", "data", "nonexistent_dir"));
    }
    
    // ==================== DATA PERSISTENCE TESTS ====================
    
    @Test
    void testDataManagerSaveLoad() {
        // Test saving all data
        boolean saved = DataManager.saveAllData();
        assertTrue(saved);
        
        // Test loading all data
        boolean loaded = DataManager.loadAllData();
        assertTrue(loaded);
    }
    
    @Test
    void testMaterialsPersistence() {
        // Add test material
        DataManager.addMaterial(testMaterial);
        
        // Save materials
        boolean saved = DataManager.saveMaterials();
        assertTrue(saved);
        
        // Clear materials
        DataManager.clearAllData();
        
        // Load materials
        boolean loaded = DataManager.loadMaterials();
        assertTrue(loaded);
        
        // Verify material was loaded
        Material[] materials = DataManager.getAllMaterials();
        assertTrue(materials.length > 0);
    }
    
    @Test
    void testUsersPersistence() {
        // Add test user
        DataManager.addUser(testUser);
        
        // Save users
        boolean saved = DataManager.saveUsers();
        assertTrue(saved);
        
        // Clear users
        DataManager.clearAllData();
        
        // Load users
        boolean loaded = DataManager.loadUsers();
        assertTrue(loaded);
        
        // Verify user was loaded
        User[] users = DataManager.getAllUsers();
        assertTrue(users.length > 0);
    }
    
    @Test
    void testOrdersPersistence() {
        // Register test order
        OrderManager.registerOrder(testOrder);
        
        // Save orders
        boolean saved = DataManager.saveOrders();
        assertTrue(saved);
        
        // Clear orders
        OrderManager.clearAllOrders();
        
        // Load orders
        boolean loaded = DataManager.loadOrders();
        assertTrue(loaded);
        
        // Verify order was loaded
        Order[] orders = OrderManager.getAllOrders();
        assertTrue(orders.length > 0);
    }
    
    @Test
    void testInventoryPersistence() {
        // Set inventory
        Inventory.setStock(testMaterial, 500);
        
        // Save inventory
        boolean saved = DataManager.saveInventory();
        assertTrue(saved);
        
        // Clear inventory
        Inventory.setStock(testMaterial, 0);
        
        // Load inventory
        boolean loaded = DataManager.loadInventory();
        assertTrue(loaded);
        
        // Verify inventory was loaded
        int stock = Inventory.getStock(testMaterial);
        assertTrue(stock > 0);
    }
    
    // ==================== BACKUP AND RESTORE TESTS ====================
    
    @Test
    void testBackupCreation() {
        // Create test file
        String testData = "Backup test data";
        assertTrue(FileManager.writeToFile("backup_test.txt", testData));
        
        // Create backup
        boolean backedUp = FileManager.createBackup("backup_test.txt");
        assertTrue(backedUp);
        
        // Verify backup exists
        String[] backups = FileManager.listFiles(FileManager.getBackupDirectory());
        assertTrue(backups.length > 0);
    }
    
    @Test
    void testSystemBackup() {
        // Add test data
        DataManager.addMaterial(testMaterial);
        DataManager.addUser(testUser);
        OrderManager.registerOrder(testOrder);
        
        // Create system backup
        boolean backedUp = DataManager.backupAllData();
        assertTrue(backedUp);
        
        // Verify backup files exist
        String[] backups = FileManager.listFiles(FileManager.getBackupDirectory());
        assertTrue(backups.length > 0);
    }
    
    @Test
    void testIndividualFileBackup() {
        // Test material backup
        DataManager.addMaterial(testMaterial);
        boolean materialBackup = DataManager.backupMaterials();
        assertTrue(materialBackup);
        
        // Test user backup
        DataManager.addUser(testUser);
        boolean userBackup = DataManager.backupUsers();
        assertTrue(userBackup);
        
        // Test order backup
        OrderManager.registerOrder(testOrder);
        boolean orderBackup = DataManager.backupOrders();
        assertTrue(orderBackup);
    }
    
    @Test
    void testBackupFileFormat() {
        // Create test file with specific content
        String testContent = "Test content for backup format verification";
        assertTrue(FileManager.writeToFile("format_test.txt", testContent));
        
        // Create backup
        assertTrue(FileManager.createBackup("format_test.txt"));
        
        // Find backup file
        String[] backups = FileManager.listFiles(FileManager.getBackupDirectory());
        String backupFile = null;
        for (String backup : backups) {
            if (backup.contains("format_test")) {
                backupFile = backup;
                break;
            }
        }
        
        assertNotNull(backupFile);
        
        // Verify backup content
        String backupContent = FileManager.readFromFile(backupFile, FileManager.getBackupDirectory());
        assertEquals(testContent, backupContent);
    }
    
    // ==================== EXPORT FUNCTIONALITY TESTS ====================
    
    @Test
    void testCSVExport() {
        // Create test data
        List<String[]> testData = new ArrayList<>();
        testData.add(new String[]{"Header1", "Header2", "Header3"});
        testData.add(new String[]{"Value1", "Value2", "Value3"});
        testData.add(new String[]{"Data1", "Data2", "Data3"});
        
        String[] headers = {"Header1", "Header2", "Header3"};
        
        // Export to CSV
        boolean exported = ExportManager.exportToCSV(testData, "test_export.csv", headers);
        assertTrue(exported);
        
        // Verify export file exists
        assertTrue(FileManager.fileExists("test_export.csv", ExportManager.getExportsDirectory()));
    }
    
    @Test
    void testJSONExport() {
        // Create test data
        List<Map<String, Object>> testData = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Test Item 1");
        item1.put("value", 100);
        item1.put("active", true);
        testData.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Test Item 2");
        item2.put("value", 200);
        item2.put("active", false);
        testData.add(item2);
        
        // Export to JSON
        boolean exported = ExportManager.exportToJSON(testData, "test_export.json", "testItems");
        assertTrue(exported);
        
        // Verify export file exists
        assertTrue(FileManager.fileExists("test_export.json", ExportManager.getExportsDirectory()));
    }
    
    @Test
    void testInvoiceExport() {
        // Create test invoice
        Invoice testInvoice = new Invoice(testOrder, 25.50);
        
        // Export single invoice to CSV
        boolean csvExported = ExportManager.exportInvoicesToCSV(Arrays.asList(testInvoice), "test_invoices.csv");
        assertTrue(csvExported);
        
        // Export single invoice to JSON
        boolean jsonExported = ExportManager.exportInvoicesToJSON(Arrays.asList(testInvoice), "test_invoices.json");
        assertTrue(jsonExported);
        
        // Verify export files exist
        assertTrue(FileManager.fileExists("test_invoices.csv", ExportManager.getExportsDirectory()));
        assertTrue(FileManager.fileExists("test_invoices.json", ExportManager.getExportsDirectory()));
    }
    
    @Test
    void testOrderExport() {
        // Register test order
        OrderManager.registerOrder(testOrder);
        
        // Export orders to CSV
        boolean csvExported = ExportManager.exportOrdersToCSV(Arrays.asList(testOrder), "test_orders.csv");
        assertTrue(csvExported);
        
        // Export orders to JSON
        boolean jsonExported = ExportManager.exportOrdersToJSON(Arrays.asList(testOrder), "test_orders.json");
        assertTrue(jsonExported);
        
        // Verify export files exist
        assertTrue(FileManager.fileExists("test_orders.csv", ExportManager.getExportsDirectory()));
        assertTrue(FileManager.fileExists("test_orders.json", ExportManager.getExportsDirectory()));
    }
    
    @Test
    void testSystemReportExport() {
        // Export system report to CSV
        boolean csvExported = ExportManager.exportSystemReportToCSV("test_system_report.csv");
        assertTrue(csvExported);
        
        // Export system report to JSON
        boolean jsonExported = ExportManager.exportSystemReportToJSON("test_system_report.json");
        assertTrue(jsonExported);
        
        // Verify export files exist
        assertTrue(FileManager.fileExists("test_system_report.csv", ExportManager.getExportsDirectory()));
        assertTrue(FileManager.fileExists("test_system_report.json", ExportManager.getExportsDirectory()));
    }
    
    // ==================== ERROR HANDLING TESTS ====================
    
    @Test
    void testFileOperationErrors() {
        // Test writing to invalid directory
        assertFalse(FileManager.writeToFile("test.txt", "data", "/invalid/path"));
        
        // Test reading from invalid file
        String data = FileManager.readFromFile("nonexistent.txt");
        assertNull(data);
        
        // Test backup of non-existent file
        assertFalse(FileManager.createBackup("nonexistent.txt"));
    }
    
    @Test
    void testDataManagerErrorHandling() {
        // Test with null data
        assertFalse(ExportManager.exportToCSV(null, "test.csv", new String[]{"header"}));
        assertFalse(ExportManager.exportToJSON(null, "test.json", "root"));
        
        // Test with empty data
        assertFalse(ExportManager.exportToCSV(new ArrayList<>(), "test.csv", new String[]{"header"}));
    }
    
    @Test
    void testExportErrorHandling() {
        // Test export with null filename (should generate timestamp-based name)
        List<String[]> testData = new ArrayList<>();
        testData.add(new String[]{"test", "data"});
        
        boolean exported = ExportManager.exportToCSV(testData, null, new String[]{"header"});
        assertTrue(exported);
        
        // Test export with invalid characters in filename
        boolean exported2 = ExportManager.exportToCSV(testData, "test<>file.csv", new String[]{"header"});
        assertTrue(exported2);
    }
    
    // ==================== PERFORMANCE TESTS ====================
    
    @Test
    void testLargeFileOperations() {
        // Create large test data
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeData.append("Line ").append(i).append(": Test data for performance testing\n");
        }
        
        long startTime = System.currentTimeMillis();
        
        // Write large file
        boolean written = FileManager.writeToFile("large_test.txt", largeData.toString());
        assertTrue(written);
        
        // Read large file
        String readData = FileManager.readFromFile("large_test.txt");
        assertNotNull(readData);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 5000, "Large file operations took too long: " + duration + "ms");
        
        // Clean up
        FileManager.deleteFile("large_test.txt");
    }
    
    @Test
    void testMultipleFileOperations() {
        long startTime = System.currentTimeMillis();
        
        // Create multiple files
        for (int i = 0; i < 100; i++) {
            String filename = "multi_test_" + i + ".txt";
            String data = "Test data for file " + i;
            assertTrue(FileManager.writeToFile(filename, data));
        }
        
        // List files
        String[] files = FileManager.listFiles(FileManager.getDataDirectory());
        assertTrue(files.length >= 100);
        
        // Delete files
        for (int i = 0; i < 100; i++) {
            String filename = "multi_test_" + i + ".txt";
            assertTrue(FileManager.deleteFile(filename));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 10000, "Multiple file operations took too long: " + duration + "ms");
    }
}
