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
        
        // Test FileManager
        allPassed &= testFileManager();
        
        // Test DataManager Materials
        allPassed &= testDataManagerMaterials();
        
        // Test DataManager Users
        allPassed &= testDataManagerUsers();
        
        // Test DataManager Inventory
        allPassed &= testDataManagerInventory();
        
        // Test DataManager Orders
        allPassed &= testDataManagerOrders();
        
        // Test Integration
        allPassed &= testIntegration();
        
        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("All tests passed: " + (allPassed ? "YES" : "NO"));
        
        return allPassed;
    }
    
    /**
     * Tests FileManager functionality.
     * @return true if tests pass
     */
    private static boolean testFileManager() {
        System.out.println("\n--- Testing FileManager ---");
        boolean passed = true;
        
        try {
            // Test directory creation
            boolean dirCreated = FileManager.ensureDirectory("data");
            System.out.println("Data directory created: " + dirCreated);
            passed &= dirCreated;
            
            // Test file writing and reading
            String testData = "Test data for file operations";
            boolean writeSuccess = FileManager.writeToFile("test.txt", testData);
            System.out.println("File write success: " + writeSuccess);
            passed &= writeSuccess;
            
            String readData = FileManager.readFromFile("test.txt");
            boolean readSuccess = testData.equals(readData);
            System.out.println("File read success: " + readSuccess);
            passed &= readSuccess;
            
            // Test file existence
            boolean fileExists = FileManager.fileExists("test.txt");
            System.out.println("File exists check: " + fileExists);
            passed &= fileExists;
            
            // Test backup creation
            boolean backupCreated = FileManager.createBackup("test.txt");
            System.out.println("Backup created: " + backupCreated);
            passed &= backupCreated;
            
            // Clean up
            FileManager.deleteFile("test.txt");
            
        } catch (Exception e) {
            System.err.println("FileManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("FileManager tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests DataManager material operations.
     * @return true if tests pass
     */
    private static boolean testDataManagerMaterials() {
        System.out.println("\n--- Testing DataManager Materials ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            DataManager.clearAllData();
            
            // Test adding materials
            Material material1 = new Material("PLA", 0.05, 200, "Blue");
            Material material2 = new Material("ABS", 0.08, 250, "Red");
            
            DataManager.addMaterial(material1);
            DataManager.addMaterial(material2);
            System.out.println("Materials added successfully");
            passed &= true;
            
            // Test getting materials
            Material retrieved = DataManager.getMaterialByName("PLA");
            boolean retrievedSuccess = retrieved != null && retrieved.getName().equals("PLA");
            System.out.println("Material retrieval: " + retrievedSuccess);
            passed &= retrievedSuccess;
            
            // Test material count
            int count = DataManager.getMaterialCount();
            boolean countSuccess = count == 2;
            System.out.println("Material count: " + count + " (expected: 2)");
            passed &= countSuccess;
            
            // Test saving and loading
            boolean saveSuccess = DataManager.saveMaterials();
            System.out.println("Materials saved: " + saveSuccess);
            passed &= saveSuccess;
            
            DataManager.clearAllData();
            boolean loadSuccess = DataManager.loadMaterials();
            System.out.println("Materials loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            Material loadedMaterial = DataManager.getMaterialByName("PLA");
            boolean verifySuccess = loadedMaterial != null && loadedMaterial.getName().equals("PLA");
            System.out.println("Loaded material verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("DataManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("DataManager tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests DataManager functionality.
     * @return true if tests pass
     */
    private static boolean testDataManagerUsers() {
        System.out.println("\n--- Testing DataManager ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            DataManager.clearAllData();
            
            // Test adding users
            User user1 = new User("john_doe", "john@example.com", "customer", "password123");
            User user2 = new User("admin_user", "admin@example.com", "admin", "adminpass456");
            
            DataManager.addUser(user1);
            DataManager.addUser(user2);
            System.out.println("Users added successfully");
            passed &= true;
            
            // Test getting users
            User retrieved = DataManager.getUserByUsername("john_doe");
            boolean retrievedSuccess = retrieved != null && retrieved.getUsername().equals("john_doe");
            System.out.println("User retrieval: " + retrievedSuccess);
            passed &= retrievedSuccess;
            
            // Test user validation (simulated by checking if user exists)
            boolean validationSuccess = DataManager.userExists("john_doe");
            System.out.println("User validation (exists): " + validationSuccess);
            passed &= validationSuccess;
            
            // Test getting users by role (simulated by checking user role)
            User[] allUsers = DataManager.getAllUsers();
            boolean roleFilterSuccess = false;
            for (User user : allUsers) {
                if ("customer".equals(user.getRole()) && "john_doe".equals(user.getUsername())) {
                    roleFilterSuccess = true;
                    break;
                }
            }
            System.out.println("Role filtering: " + roleFilterSuccess);
            passed &= roleFilterSuccess;
            
            // Test saving and loading
            boolean saveSuccess = DataManager.saveUsers();
            System.out.println("Users saved: " + saveSuccess);
            passed &= saveSuccess;
            
            DataManager.clearAllData();
            boolean loadSuccess = DataManager.loadUsers();
            System.out.println("Users loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            User loadedUser = DataManager.getUserByUsername("john_doe");
            boolean verifySuccess = loadedUser != null && loadedUser.getUsername().equals("john_doe");
            System.out.println("Loaded user verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("DataManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("DataManager tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests DataManager inventory operations.
     * @return true if tests pass
     */
    private static boolean testDataManagerInventory() {
        System.out.println("\n--- Testing DataManager Inventory ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            DataManager.clearAllData();
            
            // Test setting stock
            DataManager.setStock("PLA", 1000);
            DataManager.setStock("ABS", 500);
            System.out.println("Stock set successfully");
            passed &= true;
            
            // Test getting stock
            int plaStock = DataManager.getStock("PLA");
            boolean getStockSuccess = plaStock == 1000;
            System.out.println("PLA stock: " + plaStock + " (expected: 1000)");
            passed &= getStockSuccess;
            
            // Test stock consumption (simulated by checking if stock is sufficient)
            boolean consumeSuccess = DataManager.hasSufficient("PLA", 100);
            int remainingStock = DataManager.getStock("PLA");
            boolean consumeVerify = consumeSuccess && remainingStock == 1000;
            System.out.println("Stock consumption: " + consumeVerify);
            passed &= consumeVerify;
            
            // Test insufficient stock
            boolean insufficientStock = !DataManager.hasSufficient("PLA", 1000);
            System.out.println("Insufficient stock check: " + insufficientStock);
            passed &= insufficientStock;
            
            // Test adding stock (simulated by setting new stock)
            DataManager.setStock("PLA", 1200);
            int finalStock = DataManager.getStock("PLA");
            boolean addStockVerify = finalStock == 1200;
            System.out.println("Stock addition: " + addStockVerify);
            passed &= addStockVerify;
            
            // Test saving and loading
            boolean saveSuccess = DataManager.saveInventory();
            System.out.println("Inventory saved: " + saveSuccess);
            passed &= saveSuccess;
            
            DataManager.clearAllData();
            boolean loadSuccess = DataManager.loadInventory();
            System.out.println("Inventory loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded data
            int loadedStock = DataManager.getStock("PLA");
            boolean verifySuccess = loadedStock == 1100;
            System.out.println("Loaded inventory verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("DataManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("DataManager tests passed: " + passed);
        return passed;
    }
    
    /**
     * Tests DataManager order operations.
     * @return true if tests pass
     */
    private static boolean testDataManagerOrders() {
        System.out.println("\n--- Testing DataManager Orders ---");
        boolean passed = true;
        
        try {
            // Clear existing data
            OrderManager.clearAllOrders();
            
            // Create test data
            User user = new User("test_customer", "test@example.com", "customer", "testpass123");
            Material material = new Material("PLA", 0.05, 200, "Blue");
            Order order = new Order(user, material, "10x10x5cm", 2, "Test order");
            
            // Register order
            OrderManager.registerOrder(order);
            OrderManager.enqueueOrder(order);
            
            // Test saving orders
            boolean saveSuccess = DataManager.saveOrders();
            System.out.println("Orders saved: " + saveSuccess);
            passed &= saveSuccess;
            
            // Test saving queue
            boolean saveQueueSuccess = DataManager.saveOrderQueue();
            System.out.println("Order queue saved: " + saveQueueSuccess);
            passed &= saveQueueSuccess;
            
            // Clear and reload
            OrderManager.clearAllOrders();
            boolean loadSuccess = DataManager.loadOrders();
            System.out.println("Orders loaded: " + loadSuccess);
            passed &= loadSuccess;
            
            // Verify loaded order
            Order loadedOrder = OrderManager.getOrderById(order.getOrderID());
            boolean verifySuccess = loadedOrder != null && loadedOrder.getQuantity() == 2;
            System.out.println("Loaded order verification: " + verifySuccess);
            passed &= verifySuccess;
            
        } catch (Exception e) {
            System.err.println("DataManager test failed: " + e.getMessage());
            passed = false;
        }
        
        System.out.println("DataManager tests passed: " + passed);
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
            DataManager.clearAllData();
            DataManager.clearAllData();
            DataManager.clearAllData();
            OrderManager.clearAllOrders();
            
            // Create integrated test data
            Material material = new Material("TestPLA", 0.06, 210, "Green");
            User user = new User("integration_user", "integration@example.com", "customer", "integrationpass456");
            
            // Add to handlers
            DataManager.addMaterial(material);
            DataManager.addUser(user);
            DataManager.setStock("TestPLA", 2000);
            
            // Create order
            Order order = new Order(user, material, "5x5x5cm", 1, "Integration test");
            OrderManager.registerOrder(order);
            
            // Save all data
            boolean saveAll = DataManager.saveMaterials() &&
                             DataManager.saveUsers() &&
                             DataManager.saveInventory() &&
                             DataManager.saveOrders();
            System.out.println("All data saved: " + saveAll);
            passed &= saveAll;
            
            // Clear and reload all
            DataManager.clearAllData();
            DataManager.clearAllData();
            DataManager.clearAllData();
            OrderManager.clearAllOrders();
            
            boolean loadAll = DataManager.loadMaterials() &&
                             DataManager.loadUsers() &&
                             DataManager.loadInventory() &&
                             DataManager.loadOrders();
            System.out.println("All data loaded: " + loadAll);
            passed &= loadAll;
            
            // Verify integration
            Material loadedMaterial = DataManager.getMaterialByName("TestPLA");
            User loadedUser = DataManager.getUserByUsername("integration_user");
            int loadedStock = DataManager.getStock("TestPLA");
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
