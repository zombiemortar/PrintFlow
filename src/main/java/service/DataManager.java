package service;

import java.util.*;
import model.User;
import model.Material;
import model.Order;
import util.FileManager;
import util.Inventory;
import util.SystemConfig;

/**
 * Simplified data management for the 3D printing service system.
 * Consolidates all data persistence operations for orders, materials, users, and inventory.
 */
public class DataManager {
    
    // Static collections for in-memory data management
    private static final Map<String, User> users = new HashMap<>();
    public static final Map<String, Material> materials = new HashMap<>();
    private static final Map<Integer, Order> orders = new HashMap<>();
    private static final Map<String, Integer> inventory = new HashMap<>();
    
    // User management methods
    public static void addUser(User user) {
        if (user != null && user.getUsername() != null) {
            users.put(user.getUsername(), user);
        }
    }
    
    public static User getUserByUsername(String username) {
        return users.get(username);
    }
    
    public static User[] getAllUsers() {
        return users.values().toArray(new User[0]);
    }
    
    public static boolean userExists(String username) {
        return users.containsKey(username);
    }
    
    public static void removeUser(String username) {
        users.remove(username);
    }
    
    public static int getUserCount() {
        return users.size();
    }
    
    // Material management methods
    public static void addMaterial(Material material) {
        if (material != null) {
            String uniqueKey = material.getBrand() + "|" + material.getType() + "|" + material.getColor();
            materials.put(uniqueKey, material);
        }
    }
    
    public static Material getMaterialByName(String name) {
        // For backward compatibility, try to find by name first
        for (Material material : materials.values()) {
            if (material.getName().equals(name)) {
                return material;
            }
        }
        return null;
    }
    
    public static Material getMaterialByDisplayName(String displayName) {
        for (Material material : materials.values()) {
            if (material.getDisplayName().equals(displayName)) {
                return material;
            }
        }
        return null;
    }
    
    public static Material[] getAllMaterials() {
        return materials.values().toArray(new Material[0]);
    }
    
    public static boolean materialExists(String name) {
        for (Material material : materials.values()) {
            if (material.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static void removeMaterial(String name) {
        // Find and remove by name
        String keyToRemove = null;
        for (Map.Entry<String, Material> entry : materials.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                keyToRemove = entry.getKey();
                break;
            }
        }
        if (keyToRemove != null) {
            materials.remove(keyToRemove);
        }
    }
    
    public static int getMaterialCount() {
        return materials.size();
    }
    
    // Order management methods
    public static void addOrder(Order order) {
        if (order != null) {
            orders.put(order.getOrderID(), order);
        }
    }
    
    public static Order getOrderById(int orderId) {
        return orders.get(orderId);
    }
    
    public static Order[] getAllOrders() {
        return orders.values().toArray(new Order[0]);
    }
    
    public static boolean orderExists(int orderId) {
        return orders.containsKey(orderId);
    }
    
    public static void removeOrder(int orderId) {
        orders.remove(orderId);
    }
    
    public static int getOrderCount() {
        return orders.size();
    }
    
    // Inventory management methods
    public static void setStock(String materialName, int quantity) {
        if (materialName != null) {
            inventory.put(materialName, quantity);
        }
    }
    
    public static int getStock(String materialName) {
        return inventory.getOrDefault(materialName, 0);
    }
    
    public static boolean hasSufficient(String materialName, int required) {
        return getStock(materialName) >= required;
    }
    
    public static boolean consume(String materialName, int quantity) {
        int currentStock = getStock(materialName);
        if (currentStock >= quantity) {
            inventory.put(materialName, currentStock - quantity);
            return true;
        }
        return false;
    }
    
    public static Map<String, Integer> getAllInventory() {
        return new HashMap<>(inventory);
    }
    
    public static void clearAllData() {
        users.clear();
        materials.clear();
        orders.clear();
        inventory.clear();
    }
    
    /**
     * Performs a factory reset of the entire system.
     * This method:
     * - Resets all price baselines to system defaults
     * - Clears all materials from inventory
     * - Removes all user accounts except the default admin account
     * - Creates a single default admin account with username "admin" and password "admin"
     * - Clears all orders and order queue data
     * - Resets system configuration to factory defaults
     * 
     * @return true if factory reset was successful
     */
    public static boolean performFactoryReset() {
        System.out.println("Performing factory reset...");
        
        try {
            // 1. Reset system configuration to defaults
            SystemConfig.resetToDefaults();
            System.out.println("System configuration reset to defaults");
            
            // 2. Clear all data from memory
            clearAllData();
            System.out.println("All data cleared from memory");
            
            // 3. Create default admin account
            User defaultAdmin = new User("admin", "admin@printflow.com", "admin", "admin");
            addUser(defaultAdmin);
            System.out.println("Default admin account created");
            
            // 4. Clear all files by writing empty content
            boolean allFilesCleared = true;
            
            // Clear materials file
            allFilesCleared &= FileManager.writeToFile("materials.txt", "");
            System.out.println("Materials file cleared: " + allFilesCleared);
            
            // Clear users file (will be overwritten with admin account)
            allFilesCleared &= FileManager.writeToFile("users.txt", "");
            System.out.println("Users file cleared: " + allFilesCleared);
            
            // Clear inventory file
            allFilesCleared &= FileManager.writeToFile("inventory.txt", "");
            System.out.println("Inventory file cleared: " + allFilesCleared);
            
            // Clear orders file
            allFilesCleared &= FileManager.writeToFile("orders.txt", "");
            System.out.println("Orders file cleared: " + allFilesCleared);
            
            // Clear order queue file
            allFilesCleared &= FileManager.writeToFile("order_queue.txt", "");
            System.out.println("Order queue file cleared: " + allFilesCleared);
            
            // 5. Save the default admin account and system config
            boolean adminSaved = saveUsers();
            boolean configSaved = SystemConfig.saveToFile();
            
            System.out.println("Admin account saved: " + adminSaved);
            System.out.println("System config saved: " + configSaved);
            
            boolean success = allFilesCleared && adminSaved && configSaved;
            System.out.println("Factory reset completed: " + (success ? "SUCCESS" : "FAILED"));
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Error during factory reset: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
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
        boolean materialsSaved = saveMaterials();
        System.out.println("Materials saved: " + materialsSaved);
        allSuccess &= materialsSaved;
        
        // Save users
        boolean usersSaved = saveUsers();
        System.out.println("Users saved: " + usersSaved);
        allSuccess &= usersSaved;
        
        // Save inventory
        boolean inventorySaved = saveInventory();
        System.out.println("Inventory saved: " + inventorySaved);
        allSuccess &= inventorySaved;
        
        // Save orders
        boolean ordersSaved = saveOrders();
        System.out.println("Orders saved: " + ordersSaved);
        allSuccess &= ordersSaved;
        
        // Save order queue
        boolean queueSaved = saveOrderQueue();
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
        boolean materialsLoaded = loadMaterials();
        System.out.println("Materials loaded: " + materialsLoaded);
        allSuccess &= materialsLoaded;
        
        // Load users
        boolean usersLoaded = loadUsers();
        System.out.println("Users loaded: " + usersLoaded);
        allSuccess &= usersLoaded;
        
        // Load inventory
        boolean inventoryLoaded = loadInventory();
        System.out.println("Inventory loaded: " + inventoryLoaded);
        allSuccess &= inventoryLoaded;
        
        // Load orders
        boolean ordersLoaded = loadOrders();
        System.out.println("Orders loaded: " + ordersLoaded);
        allSuccess &= ordersLoaded;
        
        // Load order queue
        boolean queueLoaded = loadOrderQueue();
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
        boolean materialsBackedUp = backupMaterials();
        System.out.println("Materials backup: " + materialsBackedUp);
        allSuccess &= materialsBackedUp;
        
        // Backup users
        boolean usersBackedUp = backupUsers();
        System.out.println("Users backup: " + usersBackedUp);
        allSuccess &= usersBackedUp;
        
        // Backup inventory
        boolean inventoryBackedUp = backupInventory();
        System.out.println("Inventory backup: " + inventoryBackedUp);
        allSuccess &= inventoryBackedUp;
        
        // Backup orders
        boolean ordersBackedUp = backupOrders();
        System.out.println("Orders backup: " + ordersBackedUp);
        allSuccess &= ordersBackedUp;
        
        // Backup order queue
        boolean queueBackedUp = backupOrderQueue();
        System.out.println("Order queue backup: " + queueBackedUp);
        allSuccess &= queueBackedUp;
        
        System.out.println("All backup operations completed: " + (allSuccess ? "SUCCESS" : "FAILED"));
        return allSuccess;
    }
    
    /**
     * Saves materials to file.
     * @return true if save was successful
     */
    public static boolean saveMaterials() {
        // Get all materials from static storage
        Material[] materials = getAllMaterials();
        if (materials.length == 0) {
            return FileManager.writeToFile("materials.txt", "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Material Data Export\n");
        data.append("# Format: brand|type|costPerGram|printTemp|color\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Material material : materials) {
            data.append(serializeMaterial(material)).append("\n");
        }
        
        return FileManager.writeToFile("materials.txt", data.toString());
    }
    
    /**
     * Loads materials from file.
     * @return true if load was successful
     */
    public static boolean loadMaterials() {
        String data = FileManager.readFromFile("materials.txt");
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
                
                Material material = deserializeMaterial(line);
                if (material != null) {
                    addMaterial(material);
                    loadedCount++;
                }
            }
            
            System.out.println("Loaded " + loadedCount + " materials from file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading materials: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Saves users to file.
     * @return true if save was successful
     */
    public static boolean saveUsers() {
        User[] users = getAllUsers();
        if (users.length == 0) {
            return FileManager.writeToFile("users.txt", "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# User Data Export\n");
        data.append("# Format: username|email|role|passwordHash\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (User user : users) {
            data.append(serializeUser(user)).append("\n");
        }
        
        return FileManager.writeToFile("users.txt", data.toString());
    }
    
    /**
     * Loads users from file.
     * @return true if load was successful
     */
    public static boolean loadUsers() {
        String data = FileManager.readFromFile("users.txt");
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
                
                User user = deserializeUser(line);
                if (user != null) {
                    addUser(user);
                    loadedCount++;
                }
            }
            
            System.out.println("Loaded " + loadedCount + " users from file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Saves inventory to file.
     * @return true if save was successful
     */
    public static boolean saveInventory() {
        StringBuilder data = new StringBuilder();
        data.append("# Inventory Data Export\n");
        data.append("# Format: materialName|stockInGrams\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        // Get all materials and their stock levels
        Material[] materials = getAllMaterials();
        boolean hasData = false;
        
        for (Material material : materials) {
            int stock = Inventory.getStock(material);
            if (stock > 0) {
                data.append(material.getName()).append("|").append(stock).append("\n");
                hasData = true;
            }
        }
        
        if (!hasData) {
            return FileManager.writeToFile("inventory.txt", "");
        }
        
        return FileManager.writeToFile("inventory.txt", data.toString());
    }
    
    /**
     * Loads inventory from file.
     * @return true if load was successful
     */
    public static boolean loadInventory() {
        String data = FileManager.readFromFile("inventory.txt");
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
                
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String materialName = parts[0].trim();
                    int stock = Integer.parseInt(parts[1].trim());
                    
                    // Find the material and set its stock
                    Material[] materials = getAllMaterials();
                    for (Material material : materials) {
                        if (material.getName().equals(materialName)) {
                            Inventory.setStock(material, stock);
                            loadedCount++;
                            break;
                        }
                    }
                }
            }
            
            System.out.println("Loaded " + loadedCount + " inventory items from file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Saves orders to file.
     * @return true if save was successful
     */
    public static boolean saveOrders() {
        Order[] orders = OrderManager.getAllOrders();
        if (orders.length == 0) {
            return FileManager.writeToFile("orders.txt", "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Order Data Export\n");
        data.append("# Format: orderID|username|email|role|materialName|materialCostPerGram|materialPrintTemp|materialColor|dimensions|quantity|specialInstructions|status|priority|estimatedPrintHours\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Order order : orders) {
            data.append(serializeOrder(order)).append("\n");
        }
        
        return FileManager.writeToFile("orders.txt", data.toString());
    }
    
    /**
     * Loads orders from file.
     * @return true if load was successful
     */
    public static boolean loadOrders() {
        String data = FileManager.readFromFile("orders.txt");
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
     * Saves order queue to file.
     * @return true if save was successful
     */
    public static boolean saveOrderQueue() {
        Order[] allOrders = OrderManager.getAllOrders();
        List<Order> queueOrders = new ArrayList<>();
        
        // Filter orders that are likely in queue (pending status)
        for (Order order : allOrders) {
            if ("pending".equals(order.getStatus()) || "processing".equals(order.getStatus())) {
                queueOrders.add(order);
            }
        }
        
        if (queueOrders.isEmpty()) {
            return FileManager.writeToFile("order_queue.txt", "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Order Queue Export\n");
        data.append("# Format: orderID\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Order order : queueOrders) {
            data.append(order.getOrderID()).append("\n");
        }
        
        return FileManager.writeToFile("order_queue.txt", data.toString());
    }
    
    /**
     * Loads order queue from file.
     * @return true if load was successful
     */
    public static boolean loadOrderQueue() {
        String data = FileManager.readFromFile("order_queue.txt");
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
                
                try {
                    int orderID = Integer.parseInt(line);
                    Order order = OrderManager.getOrderById(orderID);
                    if (order != null) {
                        // Re-enqueue the order
                        OrderManager.enqueueOrder(order);
                        loadedCount++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid order ID in queue: " + line);
                }
            }
            
            System.out.println("Loaded " + loadedCount + " orders into queue from file");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading order queue: " + e.getMessage());
            return false;
        }
    }
    
    // Backup methods
    public static boolean backupMaterials() {
        return FileManager.createBackup("materials.txt");
    }
    
    public static boolean backupUsers() {
        return FileManager.createBackup("users.txt");
    }
    
    public static boolean backupInventory() {
        return FileManager.createBackup("inventory.txt");
    }
    
    public static boolean backupOrders() {
        return FileManager.createBackup("orders.txt");
    }
    
    public static boolean backupOrderQueue() {
        return FileManager.createBackup("order_queue.txt");
    }
    
    // Serialization helper methods
    private static String serializeMaterial(Material material) {
        return material.getBrand() + "|" + 
               material.getType() + "|" + 
               material.getCostPerGram() + "|" + 
               material.getPrintTemp() + "|" + 
               material.getColor();
    }
    
    private static Material deserializeMaterial(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length == 5) {
                // New format: brand|type|costPerGram|printTemp|color
                String brand = parts[0].trim();
                String type = parts[1].trim();
                double costPerGram = Double.parseDouble(parts[2].trim());
                int printTemp = Integer.parseInt(parts[3].trim());
                String color = parts[4].trim();
                return new Material(brand, type, costPerGram, printTemp, color);
            } else if (parts.length == 4) {
                // Legacy format: name|costPerGram|printTemp|color (for backward compatibility)
                String name = parts[0].trim();
                double costPerGram = Double.parseDouble(parts[1].trim());
                int printTemp = Integer.parseInt(parts[2].trim());
                String color = parts[3].trim();
                // Try to parse brand and type from name (assume "Brand Type" format)
                String[] nameParts = name.split(" ", 2);
                String brand = nameParts.length > 0 ? nameParts[0] : "Unknown";
                String type = nameParts.length > 1 ? nameParts[1] : "Unknown";
                return new Material(brand, type, costPerGram, printTemp, color);
            }
        } catch (Exception e) {
            System.err.println("Error deserializing material: " + line);
        }
        return null;
    }
    
    private static String serializeUser(User user) {
        return user.getUsername() + "|" + 
               user.getEmail() + "|" + 
               user.getRole() + "|" + 
               (user.getPasswordHash() != null ? user.getPasswordHash() : "");
    }
    
    private static User deserializeUser(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length == 4) {
                String username = parts[0].trim();
                String email = parts[1].trim();
                String role = parts[2].trim();
                String passwordHash = parts[3].trim();
                
                if (passwordHash.isEmpty()) {
                    return new User(username, email, role, "");
                } else {
                    return new User(username, email, role, passwordHash, true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deserializing user: " + line);
        }
        return null;
    }
    
    private static String serializeOrder(Order order) {
        return order.getOrderID() + "|" + 
               order.getUser().getUsername() + "|" + 
               order.getUser().getEmail() + "|" + 
               order.getUser().getRole() + "|" + 
               order.getMaterial().getName() + "|" + 
               order.getMaterial().getCostPerGram() + "|" + 
               order.getMaterial().getPrintTemp() + "|" + 
               order.getMaterial().getColor() + "|" + 
               order.getDimensions() + "|" + 
               order.getQuantity() + "|" + 
               order.getSpecialInstructions() + "|" + 
               order.getStatus() + "|" + 
               order.getPriority() + "|" + 
               order.estimatePrintTimeHours();
    }
    
    private static Order deserializeOrder(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 14) {
                int orderID = Integer.parseInt(parts[0].trim());
                String username = parts[1].trim();
                String email = parts[2].trim();
                String role = parts[3].trim();
                String materialName = parts[4].trim();
                double materialCost = Double.parseDouble(parts[5].trim());
                int materialTemp = Integer.parseInt(parts[6].trim());
                String materialColor = parts[7].trim();
                String dimensions = parts[8].trim();
                int quantity = Integer.parseInt(parts[9].trim());
                String instructions = parts[10].trim();
                String status = parts[11].trim();
                String priority = parts[12].trim();
                
                // Create user and material objects
                User user = new User(username, email, role, "");
                // Parse material name to extract brand and type (legacy format)
                String[] nameParts = materialName.split(" ", 2);
                String brand = nameParts.length > 0 ? nameParts[0] : "Unknown";
                String type = nameParts.length > 1 ? nameParts[1] : "Unknown";
                Material material = new Material(brand, type, materialCost, materialTemp, materialColor);
                
                // Create order (using default material grams for legacy orders)
                double materialGrams = quantity * 10.0; // Default assumption
                Order order = new Order(user, material, dimensions, quantity, instructions, materialGrams);
                order.setOrderID(orderID); // Set the original order ID
                order.updateStatus(status);
                order.setPriority(priority);
                
                return order;
            }
        } catch (Exception e) {
            System.err.println("Error deserializing order: " + line);
        }
        return null;
    }
}

