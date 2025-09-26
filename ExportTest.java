import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Comprehensive test suite for CSV/JSON export functionality.
 * Tests all export methods in ExportManager and integration with existing classes.
 */
public class ExportTest {
    
    private User testUser;
    private Material testMaterial;
    private Order testOrder;
    private Invoice testInvoice;
    
    @BeforeEach
    void setUp() {
        // Clear all data before each test
        FileHandlingManager.clearAllData();
        OrderManager.clearAllOrders();
        
        // Create test data
        testUser = new User("test_user", "test@example.com", "customer", "testpassword123");
        testMaterial = new Material("Test PLA", 0.05, 200, "Blue");
        testOrder = new Order(testUser, testMaterial, "10x10x5cm", 2, "Test order");
        testInvoice = new Invoice(testOrder, testOrder.calculatePrice());
        
        // Register order
        OrderManager.registerOrder(testOrder);
        
        // Add material and user to file handlers
        MaterialFileHandler.addMaterial(testMaterial);
        UserFileHandler.addUser(testUser);
        InventoryFileHandler.setStock("Test PLA", 1000);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up exported files
        try {
            String[] exportedFiles = ExportManager.listExportedFiles();
            for (String filename : exportedFiles) {
                Files.deleteIfExists(Paths.get(ExportManager.getExportFilePath(filename)));
            }
        } catch (IOException e) {
            // Ignore cleanup errors
        }
        
        // Clear all data after each test
        FileHandlingManager.clearAllData();
        OrderManager.clearAllOrders();
    }
    
    @Test
    void testExportDirectoryCreation() {
        // Test that exports directory is created
        assertTrue(ExportManager.ensureExportsDirectory());
        
        // Verify directory exists
        assertTrue(Files.exists(Paths.get("exports")));
        assertTrue(Files.isDirectory(Paths.get("exports")));
    }
    
    @Test
    void testExportInvoiceToCSV() {
        // Test CSV export with custom filename
        String filename = "test_invoice.csv";
        assertTrue(ExportManager.exportInvoiceToCSV(testInvoice, filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test CSV export with auto-generated filename
        assertTrue(ExportManager.exportInvoiceToCSV(testInvoice, null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportInvoiceToJSON() {
        // Test JSON export with custom filename
        String filename = "test_invoice.json";
        assertTrue(ExportManager.exportInvoiceToJSON(testInvoice, filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test JSON export with auto-generated filename
        assertTrue(ExportManager.exportInvoiceToJSON(testInvoice, null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportOrdersToCSV() {
        // Test CSV export with custom filename
        String filename = "test_orders.csv";
        assertTrue(ExportManager.exportOrdersToCSV(filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test CSV export with auto-generated filename
        assertTrue(ExportManager.exportOrdersToCSV(null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportOrdersToJSON() {
        // Test JSON export with custom filename
        String filename = "test_orders.json";
        assertTrue(ExportManager.exportOrdersToJSON(filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test JSON export with auto-generated filename
        assertTrue(ExportManager.exportOrdersToJSON(null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportSystemReportToCSV() {
        // Test CSV export with custom filename
        String filename = "test_report.csv";
        assertTrue(ExportManager.exportSystemReportToCSV(filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test CSV export with auto-generated filename
        assertTrue(ExportManager.exportSystemReportToCSV(null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportSystemReportToJSON() {
        // Test JSON export with custom filename
        String filename = "test_report.json";
        assertTrue(ExportManager.exportSystemReportToJSON(filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test JSON export with auto-generated filename
        assertTrue(ExportManager.exportSystemReportToJSON(null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportInvoicesToCSV() {
        Invoice[] invoices = {testInvoice};
        
        // Test CSV export with custom filename
        String filename = "test_invoices.csv";
        assertTrue(ExportManager.exportInvoicesToCSV(invoices, filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test CSV export with auto-generated filename
        assertTrue(ExportManager.exportInvoicesToCSV(invoices, null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testExportInvoicesToJSON() {
        Invoice[] invoices = {testInvoice};
        
        // Test JSON export with custom filename
        String filename = "test_invoices.json";
        assertTrue(ExportManager.exportInvoicesToJSON(invoices, filename));
        
        // Verify file was created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath(filename))));
        
        // Test JSON export with auto-generated filename
        assertTrue(ExportManager.exportInvoicesToJSON(invoices, null));
        
        // Verify file was created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 1);
    }
    
    @Test
    void testInvoiceExportMethods() {
        // Test Invoice class export methods
        assertTrue(testInvoice.exportToCSV("invoice_test.csv"));
        assertTrue(testInvoice.exportToJSON("invoice_test.json"));
        assertTrue(testInvoice.exportToCSV());
        assertTrue(testInvoice.exportToJSON());
        
        // Verify files were created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 4);
    }
    
    @Test
    void testFileHandlingManagerIntegration() {
        // Test FileHandlingManager integration methods
        assertTrue(FileHandlingManager.exportSystemReportToCSV("fhm_report.csv"));
        assertTrue(FileHandlingManager.exportSystemReportToJSON("fhm_report.json"));
        assertTrue(FileHandlingManager.exportOrdersToCSV("fhm_orders.csv"));
        assertTrue(FileHandlingManager.exportOrdersToJSON("fhm_orders.json"));
        
        Invoice[] invoices = {testInvoice};
        assertTrue(FileHandlingManager.exportInvoicesToCSV(invoices, "fhm_invoices.csv"));
        assertTrue(FileHandlingManager.exportInvoicesToJSON(invoices, "fhm_invoices.json"));
        
        // Test auto-generated filenames
        assertTrue(FileHandlingManager.exportSystemReportToCSV());
        assertTrue(FileHandlingManager.exportSystemReportToJSON());
        assertTrue(FileHandlingManager.exportOrdersToCSV());
        assertTrue(FileHandlingManager.exportOrdersToJSON());
        assertTrue(FileHandlingManager.exportInvoicesToCSV(invoices));
        assertTrue(FileHandlingManager.exportInvoicesToJSON(invoices));
        
        // Verify files were created
        String[] exportedFiles = FileHandlingManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 12);
    }
    
    @Test
    void testCSVContentValidation() {
        String filename = "content_test.csv";
        assertTrue(ExportManager.exportInvoiceToCSV(testInvoice, filename));
        
        try {
            String content = Files.readString(Paths.get(ExportManager.getExportFilePath(filename)));
            
            // Check CSV header
            assertTrue(content.contains("Invoice ID,Order ID,Customer Username"));
            
            // Check data content
            assertTrue(content.contains(String.valueOf(testInvoice.getInvoiceID())));
            assertTrue(content.contains(String.valueOf(testOrder.getOrderID())));
            assertTrue(content.contains(testUser.getUsername()));
            assertTrue(content.contains(testUser.getEmail()));
            assertTrue(content.contains(testMaterial.getName()));
            
        } catch (IOException e) {
            fail("Failed to read exported CSV file: " + e.getMessage());
        }
    }
    
    @Test
    void testJSONContentValidation() {
        String filename = "content_test.json";
        assertTrue(ExportManager.exportInvoiceToJSON(testInvoice, filename));
        
        try {
            String content = Files.readString(Paths.get(ExportManager.getExportFilePath(filename)));
            
            // Check JSON structure
            assertTrue(content.contains("\"invoice\":"));
            assertTrue(content.contains("\"invoiceID\":"));
            assertTrue(content.contains("\"totalCost\":"));
            assertTrue(content.contains("\"order\":"));
            
            // Check data content
            assertTrue(content.contains(String.valueOf(testInvoice.getInvoiceID())));
            assertTrue(content.contains(String.valueOf(testOrder.getOrderID())));
            assertTrue(content.contains(testUser.getUsername()));
            assertTrue(content.contains(testUser.getEmail()));
            assertTrue(content.contains(testMaterial.getName()));
            
        } catch (IOException e) {
            fail("Failed to read exported JSON file: " + e.getMessage());
        }
    }
    
    @Test
    void testNullHandling() {
        // Test null invoice handling
        assertFalse(ExportManager.exportInvoiceToCSV(null, "test.csv"));
        assertFalse(ExportManager.exportInvoiceToJSON(null, "test.json"));
        
        // Test null invoices array handling
        assertFalse(ExportManager.exportInvoicesToCSV(null, "test.csv"));
        assertFalse(ExportManager.exportInvoicesToJSON(null, "test.json"));
        
        // Test empty invoices array handling
        Invoice[] emptyInvoices = {};
        assertFalse(ExportManager.exportInvoicesToCSV(emptyInvoices, "test.csv"));
        assertFalse(ExportManager.exportInvoicesToJSON(emptyInvoices, "test.json"));
    }
    
    @Test
    void testSpecialCharactersHandling() {
        // Create order with special characters
        User specialUser = new User("user,with\"commas\"", "test@example.com", "customer", "testpassword123");
        Material specialMaterial = new Material("Material\nwith\nnewlines", 0.05, 200, "Color\"with\"quotes");
        Order specialOrder = new Order(specialUser, specialMaterial, "10x10x5cm", 1, "Instructions\nwith\nnewlines");
        Invoice specialInvoice = new Invoice(specialOrder, specialOrder.calculatePrice());
        
        OrderManager.registerOrder(specialOrder);
        
        // Test CSV export with special characters
        assertTrue(ExportManager.exportInvoiceToCSV(specialInvoice, "special_chars.csv"));
        
        // Test JSON export with special characters
        assertTrue(ExportManager.exportInvoiceToJSON(specialInvoice, "special_chars.json"));
        
        // Verify files were created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath("special_chars.csv"))));
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath("special_chars.json"))));
    }
    
    @Test
    void testMultipleExports() {
        // Create multiple orders and invoices
        User user2 = new User("user2", "user2@example.com", "vip", "testpassword123");
        Material material2 = new Material("ABS", 0.08, 250, "Red");
        Order order2 = new Order(user2, material2, "15x15x10cm", 3, "Rush order");
        Invoice invoice2 = new Invoice(order2, order2.calculatePrice());
        
        OrderManager.registerOrder(order2);
        
        // Export multiple invoices
        Invoice[] invoices = {testInvoice, invoice2};
        assertTrue(ExportManager.exportInvoicesToCSV(invoices, "multiple_invoices.csv"));
        assertTrue(ExportManager.exportInvoicesToJSON(invoices, "multiple_invoices.json"));
        
        // Export all orders
        assertTrue(ExportManager.exportOrdersToCSV("all_orders.csv"));
        assertTrue(ExportManager.exportOrdersToJSON("all_orders.json"));
        
        // Verify files were created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 4);
    }
    
    @Test
    void testExportFilePathGeneration() {
        String filename = "test_file.csv";
        String expectedPath = "exports" + File.separator + filename;
        assertEquals(expectedPath, ExportManager.getExportFilePath(filename));
    }
    
    @Test
    void testListExportedFiles() {
        // Initially no files
        String[] initialFiles = ExportManager.listExportedFiles();
        assertEquals(0, initialFiles.length);
        
        // Export some files
        ExportManager.exportInvoiceToCSV(testInvoice, "test1.csv");
        ExportManager.exportInvoiceToJSON(testInvoice, "test2.json");
        ExportManager.exportOrdersToCSV("test3.csv");
        
        // Check files are listed
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 3);
        
        // Check specific files are present
        boolean foundTest1 = false, foundTest2 = false, foundTest3 = false;
        for (String file : exportedFiles) {
            if (file.equals("test1.csv")) foundTest1 = true;
            if (file.equals("test2.json")) foundTest2 = true;
            if (file.equals("test3.csv")) foundTest3 = true;
        }
        
        assertTrue(foundTest1, "test1.csv should be in exported files list");
        assertTrue(foundTest2, "test2.json should be in exported files list");
        assertTrue(foundTest3, "test3.csv should be in exported files list");
    }
    
    @Test
    void testSystemReportContent() {
        // Export system report
        assertTrue(ExportManager.exportSystemReportToCSV("system_report.csv"));
        assertTrue(ExportManager.exportSystemReportToJSON("system_report.json"));
        
        // Verify files were created
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath("system_report.csv"))));
        assertTrue(Files.exists(Paths.get(ExportManager.getExportFilePath("system_report.json"))));
        
        try {
            // Check CSV content
            String csvContent = Files.readString(Paths.get(ExportManager.getExportFilePath("system_report.csv")));
            assertTrue(csvContent.contains("Report Type,Value,Description"));
            assertTrue(csvContent.contains("materials_count"));
            assertTrue(csvContent.contains("orders_count"));
            
            // Check JSON content
            String jsonContent = Files.readString(Paths.get(ExportManager.getExportFilePath("system_report.json")));
            assertTrue(jsonContent.contains("\"systemStatistics\":"));
            assertTrue(jsonContent.contains("\"materials\":"));
            assertTrue(jsonContent.contains("\"orders\":"));
            
        } catch (IOException e) {
            fail("Failed to read system report files: " + e.getMessage());
        }
    }
    
    @Test
    void testConcurrentExports() {
        // Test multiple concurrent exports
        List<Thread> threads = new ArrayList<>();
        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                boolean result = ExportManager.exportInvoiceToCSV(testInvoice, "concurrent_" + index + ".csv");
                results.add(result);
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread interrupted: " + e.getMessage());
            }
        }
        
        // Verify all exports succeeded
        assertEquals(5, results.size());
        for (Boolean result : results) {
            assertTrue(result, "All concurrent exports should succeed");
        }
        
        // Verify files were created
        String[] exportedFiles = ExportManager.listExportedFiles();
        assertTrue(exportedFiles.length >= 5);
    }
}
