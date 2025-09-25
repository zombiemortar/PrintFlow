import java.util.*;

/**
 * Handles file operations for Inventory data.
 * Provides methods to save and load inventory stock levels to/from files.
 */
public class InventoryFileHandler {
    
    private static final String INVENTORY_FILENAME = "inventory.txt";
    private static final Map<String, Integer> inventoryData = new HashMap<>();
    
    /**
     * Saves all inventory data to a file.
     * @return true if save was successful
     */
    public static boolean saveInventory() {
        if (inventoryData.isEmpty()) {
            return DataFileManager.writeToFile(INVENTORY_FILENAME, "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Inventory Data Export\n");
        data.append("# Format: materialName|stockInGrams\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Map.Entry<String, Integer> entry : inventoryData.entrySet()) {
            data.append(escapeString(entry.getKey())).append("|").append(entry.getValue()).append("\n");
        }
        
        return DataFileManager.writeToFile(INVENTORY_FILENAME, data.toString());
    }
    
    /**
     * Loads inventory data from file.
     * @return true if load was successful
     */
    public static boolean loadInventory() {
        String data = DataFileManager.readFromFile(INVENTORY_FILENAME);
        if (data == null || data.trim().isEmpty()) {
            return true; // No data to load is not an error
        }
        
        try {
            String[] lines = data.split("\n");
            int loadedCount = 0;
            
            // Clear existing inventory data
            inventoryData.clear();
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String materialName = unescapeString(parts[0]);
                    int stockGrams = Integer.parseInt(parts[1]);
                    inventoryData.put(materialName, stockGrams);
                    loadedCount++;
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
     * Sets stock level for a material.
     * @param materialName The name of the material
     * @param stockGrams The stock level in grams
     * @return true if stock was set successfully
     */
    public static boolean setStock(String materialName, int stockGrams) {
        if (materialName == null || materialName.trim().isEmpty() || stockGrams < 0) {
            return false;
        }
        
        inventoryData.put(materialName.trim(), stockGrams);
        return true;
    }
    
    /**
     * Gets stock level for a material.
     * @param materialName The name of the material
     * @return The stock level in grams, or 0 if not found
     */
    public static int getStock(String materialName) {
        if (materialName == null || materialName.trim().isEmpty()) {
            return 0;
        }
        
        Integer stock = inventoryData.get(materialName.trim());
        return stock != null ? stock : 0;
    }
    
    /**
     * Checks if there is sufficient stock for a material.
     * @param materialName The name of the material
     * @param gramsNeeded The grams needed
     * @return true if sufficient stock is available
     */
    public static boolean hasSufficientStock(String materialName, int gramsNeeded) {
        return getStock(materialName) >= gramsNeeded;
    }
    
    /**
     * Consumes stock for a material.
     * @param materialName The name of the material
     * @param gramsToConsume The grams to consume
     * @return true if stock was consumed successfully
     */
    public static boolean consumeStock(String materialName, int gramsToConsume) {
        if (materialName == null || materialName.trim().isEmpty() || gramsToConsume <= 0) {
            return false;
        }
        
        String trimmedName = materialName.trim();
        int currentStock = getStock(trimmedName);
        
        if (currentStock < gramsToConsume) {
            return false;
        }
        
        inventoryData.put(trimmedName, currentStock - gramsToConsume);
        return true;
    }
    
    /**
     * Adds stock for a material.
     * @param materialName The name of the material
     * @param gramsToAdd The grams to add
     * @return true if stock was added successfully
     */
    public static boolean addStock(String materialName, int gramsToAdd) {
        if (materialName == null || materialName.trim().isEmpty() || gramsToAdd <= 0) {
            return false;
        }
        
        String trimmedName = materialName.trim();
        int currentStock = getStock(trimmedName);
        inventoryData.put(trimmedName, currentStock + gramsToAdd);
        return true;
    }
    
    /**
     * Gets all inventory items.
     * @return Map of material names to stock levels
     */
    public static Map<String, Integer> getAllInventory() {
        return new HashMap<>(inventoryData);
    }
    
    /**
     * Clears all inventory data.
     */
    public static void clearInventory() {
        inventoryData.clear();
    }
    
    /**
     * Gets the number of inventory items.
     * @return The count of inventory items
     */
    public static int getInventoryItemCount() {
        return inventoryData.size();
    }
    
    /**
     * Gets materials with low stock (below threshold).
     * @param threshold The stock threshold in grams
     * @return Map of materials with low stock
     */
    public static Map<String, Integer> getLowStockMaterials(int threshold) {
        Map<String, Integer> lowStock = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : inventoryData.entrySet()) {
            if (entry.getValue() < threshold) {
                lowStock.put(entry.getKey(), entry.getValue());
            }
        }
        
        return lowStock;
    }
    
    /**
     * Gets total inventory value based on material costs.
     * @param materialHandler The MaterialFileHandler to get costs from
     * @return The total inventory value
     */
    public static double getTotalInventoryValue(MaterialFileHandler materialHandler) {
        double totalValue = 0.0;
        
        for (Map.Entry<String, Integer> entry : inventoryData.entrySet()) {
            String materialName = entry.getKey();
            int stockGrams = entry.getValue();
            
            Material material = null;
            if (materialHandler != null) {
                material = materialHandler.getMaterialByName(materialName);
            } else {
                material = MaterialFileHandler.getMaterialByName(materialName);
            }
            if (material != null) {
                totalValue += material.getCostPerGram() * stockGrams;
            }
        }
        
        return totalValue;
    }
    
    /**
     * Synchronizes inventory with MaterialFileHandler.
     * Ensures all materials in MaterialFileHandler have inventory entries.
     * @param materialHandler The MaterialFileHandler to sync with
     * @param defaultStock The default stock level for new materials
     */
    public static void syncWithMaterials(MaterialFileHandler materialHandler, int defaultStock) {
        Material[] materials = MaterialFileHandler.getAllMaterials();
        
        for (Material material : materials) {
            String materialName = material.getName();
            if (!inventoryData.containsKey(materialName)) {
                inventoryData.put(materialName, defaultStock);
            }
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
     * Creates a backup of the inventory file.
     * @return true if backup was successful
     */
    public static boolean backupInventory() {
        return DataFileManager.createBackup(INVENTORY_FILENAME);
    }
    
    /**
     * Exports inventory to a formatted text report.
     * @param materialHandler The MaterialFileHandler to get material details from
     * @return The formatted report as a string
     */
    public static String exportInventoryReport(MaterialFileHandler materialHandler) {
        if (inventoryData.isEmpty()) {
            return "No inventory items found.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== INVENTORY REPORT ===\n");
        report.append("Generated: ").append(new Date()).append("\n");
        report.append("Total Items: ").append(inventoryData.size()).append("\n\n");
        
        double totalValue = 0.0;
        
        for (Map.Entry<String, Integer> entry : inventoryData.entrySet()) {
            String materialName = entry.getKey();
            int stockGrams = entry.getValue();
            
            Material material = null;
            if (materialHandler != null) {
                material = materialHandler.getMaterialByName(materialName);
            } else {
                material = MaterialFileHandler.getMaterialByName(materialName);
            }
            double itemValue = 0.0;
            
            if (material != null) {
                itemValue = material.getCostPerGram() * stockGrams;
                totalValue += itemValue;
                
                report.append("Material: ").append(materialName).append("\n");
                report.append("  Stock: ").append(stockGrams).append(" grams\n");
                report.append("  Cost per gram: $").append(String.format("%.2f", material.getCostPerGram())).append("\n");
                report.append("  Total value: $").append(String.format("%.2f", itemValue)).append("\n");
                report.append("  Print temp: ").append(material.getPrintTemp()).append("°C\n");
                report.append("  Color: ").append(material.getColor()).append("\n\n");
            } else {
                report.append("Material: ").append(materialName).append(" (details not found)\n");
                report.append("  Stock: ").append(stockGrams).append(" grams\n\n");
            }
        }
        
        report.append("Total Inventory Value: $").append(String.format("%.2f", totalValue)).append("\n");
        
        return report.toString();
    }
}
