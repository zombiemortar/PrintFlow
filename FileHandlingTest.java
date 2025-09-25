/**
 * Comprehensive test class for file handling functionality.
 * Tests all file handling classes and their integration.
 */
public class FileHandlingTest {
    
    
    /**
     * Runs all file handling tests.
     * @return true if all tests pass
     */
    public static boolean runAllTests() {
        System.out.println("=== FILE HANDLING TESTS ===");
        
        boolean allPassed = true;
        
        // Test DataFileManager
        allPassed &= testDataFileManager();
        
        // Test MaterialFileHandler
        allPassed &= testMaterialFileHandler();
        
        // Test UserFileHandler
        allPassed &= testUserFileHandler();
        
        // Test InventoryFileHandler
        allPassed &= testInventoryFileHandler();
        
        // Test OrderFileHandler
        allPassed &= testOrderFileHandler();
        
        // Test Integration
        allPassed &= testIntegration();
        
        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("All tests passed: " + (allPassed ? "YES" : "NO"));
        
        return allPassed;
    }
    
    /**
     * Tests DataFileManager functionality.
     * @return true if tests pass
     */
    private static boolean testDataFileManager() {
        System.out.println("\n--- Testing DataFileManager ---");
        boolean passed = true;
        
        try {
            // Test directory creation
            boolean dirCreated = DataFileManager.ensureDataDirectory();
            System.out.println("Data directory created: " + dirCreated);
            passed &= dirCreated;
            
            // Test file writing and reading
            String testData = "Test data for file operations";
            boolean writeSuccess = DataFileManager.writeToFile("test.txt", testData);
            System.out.println("File write success: " + writeSuccess);
            passed &= writeSuccess;
            
            String readData = DataFileManager.readFromFile("test.txt");
            boolean readSuccess = testData.equals(readData);
            System.out.println("File read success: " + readSuccess);
            passed &= readSuccess;
            
            // Test file existence
            boolean fileExists = DataFileManager.fileExists("test.txt");
            System.out.println("File exists check: " + fileExists);
            passed &= fileExists;
            
            // Test backup creation
            boolean backupCreated = DataFileManager.createBackup("test.txt");
            System.out.println("Backup created: " + backupCreated);
            passed &= backupCreated;
            
            // Clean up
            DataFileManager.deleteFile("test.txt");
            
        } catch (Exception e) {
            System.err.println("DataFileManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("DataFileManager tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests MaterialFileHandler functionality.
     * @return true if tests pass
     */
    private static boolean testMaterialFileHandler() {
        System.out.println("\n--- Testing MaterialFileHandler ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            MaterialFileHandler.clearMaterials();
            
            // Test adding materials
            Material material1 = new Material("PLA", 0.05, 200, "Blue");
            Material material2 = new Material("ABS", 0.08, 250, "Red");
            
            boolean add1 = MaterialFileHandler.addMaterial(material1);
            boolean add2 = MaterialFileHandler.addMaterial(material2);
            System.out.println("Materials added: " + add1 + ", " + add2);
            passed &= add1 && add2;
            
            // Test getting materials
            Material retrieved = MaterialFileHandler.getMaterialByName("PLA");
            boolean retrievedSuccess = retrieved != null && retrieved.getName().equals("PLA");
            System.out.println("Material retrieval: " + retrievedSuccess);
            passed &= retrievedSuccess;
            
            // Test material count
            int count = MaterialFileHandler.getMaterialCount();
            boolean countSuccess = count == 2;
            System.out.println("Material count: " + count + " (expected: 2)");
            passed &= countSuccess;
            
            // Test saving and loading
            boolean saveSuccess = MaterialFileHandler.saveMaterials();
            System.out.println("Materials saved: " + saveSuccess);
            passed &= saveSuccess;
            
            MaterialFileHandler.clearMaterials();
            boolean loadSuccess = MaterialFileHandler.loadMaterials();
            System.out.println("Materials loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            Material loadedMaterial = MaterialFileHandler.getMaterialByName("PLA");
            boolean verifySuccess = loadedMaterial != null && loadedMaterial.getName().equals("PLA");
            System.out.println("Loaded material verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("MaterialFileHandler test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("MaterialFileHandler tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests UserFileHandler functionality.
     * @return true if tests pass
     */
    private static boolean testUserFileHandler() {
        System.out.println("\n--- Testing UserFileHandler ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            UserFileHandler.clearUsers();
            
            // Test adding users
            User user1 = new User("john_doe", "john@example.com", "customer");
            User user2 = new User("admin_user", "admin@example.com", "admin");
            
            boolean add1 = UserFileHandler.addUser(user1);
            boolean add2 = UserFileHandler.addUser(user2);
            System.out.println("Users added: " + add1 + ", " + add2);
            passed &= add1 && add2;
            
            // Test getting users
            User retrieved = UserFileHandler.getUserByUsername("john_doe");
            boolean retrievedSuccess = retrieved != null && retrieved.getUsername().equals("john_doe");
            System.out.println("User retrieval: " + retrievedSuccess);
            passed &= retrievedSuccess;
            
            // Test user validation
            User invalidUser = new User("", "invalid-email", "");
            boolean validationSuccess = !UserFileHandler.validateUser(invalidUser);
            System.out.println("User validation (invalid): " + validationSuccess);
            passed &= validationSuccess;
            
            // Test getting users by role
            User[] customers = UserFileHandler.getUsersByRole("customer");
            boolean roleFilterSuccess = customers.length == 1 && customers[0].getUsername().equals("john_doe");
            System.out.println("Role filtering: " + roleFilterSuccess);
            passed &= roleFilterSuccess;
            
            // Test saving and loading
            boolean saveSuccess = UserFileHandler.saveUsers();
            System.out.println("Users saved: " + saveSuccess);
            passed &= saveSuccess;
            
            UserFileHandler.clearUsers();
            boolean loadSuccess = UserFileHandler.loadUsers();
            System.out.println("Users loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            User loadedUser = UserFileHandler.getUserByUsername("john_doe");
            boolean verifySuccess = loadedUser != null && loadedUser.getUsername().equals("john_doe");
            System.out.println("Loaded user verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("UserFileHandler test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("UserFileHandler tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests InventoryFileHandler functionality.
     * @return true if tests pass
     */
    private static boolean testInventoryFileHandler() {
        System.out.println("\n--- Testing InventoryFileHandler ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            InventoryFileHandler.clearInventory();
            
            // Test setting stock
            boolean setStock1 = InventoryFileHandler.setStock("PLA", 1000);
            boolean setStock2 = InventoryFileHandler.setStock("ABS", 500);
            System.out.println("Stock set: " + setStock1 + ", " + setStock2);
            passed &= setStock1 && setStock2;
            
            // Test getting stock
            int plaStock = InventoryFileHandler.getStock("PLA");
            boolean getStockSuccess = plaStock == 1000;
            System.out.println("PLA stock: " + plaStock + " (expected: 1000)");
            passed &= getStockSuccess;
            
            // Test stock consumption
            boolean consumeSuccess = InventoryFileHandler.consumeStock("PLA", 100);
            int remainingStock = InventoryFileHandler.getStock("PLA");
            boolean consumeVerify = consumeSuccess && remainingStock == 900;
            System.out.println("Stock consumption: " + consumeVerify);
            passed &= consumeVerify;
            
            // Test insufficient stock
            boolean insufficientStock = !InventoryFileHandler.consumeStock("PLA", 1000);
            System.out.println("Insufficient stock check: " + insufficientStock);
            passed &= insufficientStock;
            
            // Test adding stock
            boolean addStockSuccess = InventoryFileHandler.addStock("PLA", 200);
            int finalStock = InventoryFileHandler.getStock("PLA");
            boolean addStockVerify = addStockSuccess && finalStock == 1100;
            System.out.println("Stock addition: " + addStockVerify);
            passed &= addStockVerify;
            
            // Test saving and loading
            boolean saveSuccess = InventoryFileHandler.saveInventory();
            System.out.println("Inventory saved: " + saveSuccess);
            passed &= saveSuccess;
            
            InventoryFileHandler.clearInventory();
            boolean loadSuccess = InventoryFileHandler.loadInventory();
            System.out.println("Inventory loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            int loadedStock = InventoryFileHandler.getStock("PLA");
            boolean verifySuccess = loadedStock == 1100;
            System.out.println("Loaded inventory verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("InventoryFileHandler test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("InventoryFileHandler tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests OrderFileHandler functionality.
     * @return true if tests pass
     */
    private static boolean testOrderFileHandler() {
        System.out.println("\n--- Testing OrderFileHandler ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            OrderManager.clearAllOrders();
            
            // Create test data
            User user = new User("test_customer", "test@example.com", "customer");
            Material material = new Material("PLA", 0.05, 200, "Blue");
            Order order = new Order(user, material, "10x10x5cm", 2, "Test order");
            
            // Register order
            OrderManager.registerOrder(order);
            OrderManager.enqueueOrder(order);
            
            // Test saving orders
            boolean saveSuccess = OrderFileHandler.saveOrders();
            System.out.println("Orders saved: " + saveSuccess);
            passed &= saveSuccess;
            
            // Test saving queue
            boolean saveQueueSuccess = OrderFileHandler.saveOrderQueue();
            System.out.println("Order queue saved: " + saveQueueSuccess);
            passed &= saveQueueSuccess;
            
            // Clear and reload
            OrderManager.clearAllOrders();
            boolean loadSuccess = OrderFileHandler.loadOrders();
            System.out.println("Orders loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded order
            Order loadedOrder = OrderManager.getOrderById(order.getOrderID());
            boolean verifySuccess = loadedOrder != null && loadedOrder.getQuantity() == 2;
            System.out.println("Loaded order verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("OrderFileHandler test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("OrderFileHandler tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests integration between file handlers.
     * @return true if tests pass
     */
    private static boolean testIntegration() {
        System.out.println("\n--- Testing Integration ---");
        boolean passed = true;
        
        try {
            // Clear all data
            MaterialFileHandler.clearMaterials();
            UserFileHandler.clearUsers();
            InventoryFileHandler.clearInventory();
            OrderManager.clearAllOrders();
            
            // Create integrated test data
            Material material = new Material("TestPLA", 0.06, 210, "Green");
            User user = new User("integration_user", "integration@example.com", "customer");
            
            // Add to handlers
            MaterialFileHandler.addMaterial(material);
            UserFileHandler.addUser(user);
            InventoryFileHandler.setStock("TestPLA", 2000);
            
            // Create order
            Order order = new Order(user, material, "5x5x5cm", 1, "Integration test");
            OrderManager.registerOrder(order);
            
            // Save all data
            boolean saveAll = MaterialFileHandler.saveMaterials() &&
                             UserFileHandler.saveUsers() &&
                             InventoryFileHandler.saveInventory() &&
                             OrderFileHandler.saveOrders();
            System.out.println("All data saved: " + saveAll);
            passed &= saveAll;
            
            // Clear and reload all
            MaterialFileHandler.clearMaterials();
            UserFileHandler.clearUsers();
            InventoryFileHandler.clearInventory();
            OrderManager.clearAllOrders();
            
            boolean loadAll = MaterialFileHandler.loadMaterials() &&
                             UserFileHandler.loadUsers() &&
                             InventoryFileHandler.loadInventory() &&
                             OrderFileHandler.loadOrders();
            System.out.println("All data loaded: " + loadAll);
            passed &= loadAll;
            
            // Verify integration
            Material loadedMaterial = MaterialFileHandler.getMaterialByName("TestPLA");
            User loadedUser = UserFileHandler.getUserByUsername("integration_user");
            int loadedStock = InventoryFileHandler.getStock("TestPLA");
            Order loadedOrder = OrderManager.getOrderById(order.getOrderID());
            
            boolean integrationSuccess = loadedMaterial != null &&
                                        loadedUser != null &&
                                        loadedStock == 2000 &&
                                        loadedOrder != null;
            System.out.println("Integration verification: " + integrationSuccess);
            passed &= integrationSuccess;
            
        } catch (Exception e) {
            System.err.println("Integration test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("Integration tests passed: " + passed);
        return passed;
    }
    
    /**
     * Main method to run tests.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        boolean success = runAllTests();
        System.exit(success ? 0 : 1);
    }
}
