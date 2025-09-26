import java.util.*;

/**
 * Handles file operations for Material objects.
 * Provides methods to save and load materials to/from files.
 */
public class MaterialFileHandler {
    
    private static final String MATERIALS_FILENAME = "materials.txt";
    private static final List<Material> materialRegistry = new ArrayList<>();
    
    /**
     * Saves all materials to a file.
     * @return true if save was successful
     */
    public static boolean saveMaterials() {
        if (materialRegistry.isEmpty()) {
            return DataFileManager.writeToFile(MATERIALS_FILENAME, "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# Material Data Export\n");
        data.append("# Format: name|costPerGram|printTemp|color\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (Material material : materialRegistry) {
            data.append(serializeMaterial(material)).append("\n");
        }
        
        return DataFileManager.writeToFile(MATERIALS_FILENAME, data.toString());
    }
    
    /**
     * Loads materials from file.
     * @return true if load was successful
     */
    public static boolean loadMaterials() {
        String data = DataFileManager.readFromFile(MATERIALS_FILENAME);
        if (data == null || data.trim().isEmpty()) {
            return true; // No data to load is not an error
        }
        
        try {
            String[] lines = data.split("\n");
            int loadedCount = 0;
            
            // Clear existing registry
            materialRegistry.clear();
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                Material material = deserializeMaterial(line);
                if (material != null) {
                    materialRegistry.add(material);
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
     * Adds a material to the registry.
     * @param material The material to add
     * @return true if material was added successfully
     */
    public static boolean addMaterial(Material material) {
        if (material == null) {
            return false;
        }
        
        // Check if material with same name already exists
        for (Material existing : materialRegistry) {
            if (existing.getName().equals(material.getName())) {
                System.err.println("Material with name '" + material.getName() + "' already exists");
                return false;
            }
        }
        
        materialRegistry.add(material);
        return true;
    }
    
    /**
     * Gets a material by name.
     * @param name The name of the material to find
     * @return The material if found, null otherwise
     */
    public static Material getMaterialByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        for (Material material : materialRegistry) {
            if (material.getName().equals(name.trim())) {
                return material;
            }
        }
        
        return null;
    }
    
    /**
     * Gets all materials in the registry.
     * @return Array of all materials
     */
    public static Material[] getAllMaterials() {
        return materialRegistry.toArray(new Material[0]);
    }
    
    /**
     * Removes a material from the registry.
     * @param name The name of the material to remove
     * @return true if material was removed successfully
     */
    public static boolean removeMaterial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        Iterator<Material> iterator = materialRegistry.iterator();
        while (iterator.hasNext()) {
            Material material = iterator.next();
            if (material.getName().equals(name.trim())) {
                iterator.remove();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Updates an existing material in the registry.
     * @param name The name of the material to update
     * @param updatedMaterial The updated material data
     * @return true if material was updated successfully
     */
    public static boolean updateMaterial(String name, Material updatedMaterial) {
        if (name == null || name.trim().isEmpty() || updatedMaterial == null) {
            return false;
        }
        
        for (int i = 0; i < materialRegistry.size(); i++) {
            Material material = materialRegistry.get(i);
            if (material.getName().equals(name.trim())) {
                materialRegistry.set(i, updatedMaterial);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Clears all materials from the registry.
     */
    public static void clearMaterials() {
        materialRegistry.clear();
    }
    
    /**
     * Gets the number of materials in the registry.
     * @return The count of materials
     */
    public static int getMaterialCount() {
        return materialRegistry.size();
    }
    
    /**
     * Serializes a Material object to a string format.
     * @param material The material to serialize
     * @return Serialized string representation
     */
    private static String serializeMaterial(Material material) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeString(material.getName())).append("|");
        sb.append(material.getCostPerGram()).append("|");
        sb.append(material.getPrintTemp()).append("|");
        sb.append(escapeString(material.getColor()));
        
        return sb.toString();
    }
    
    /**
     * Deserializes a string to a Material object.
     * @param data The serialized string data
     * @return The deserialized Material object, or null if parsing failed
     */
    private static Material deserializeMaterial(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 4) {
                System.err.println("Invalid material data format: " + data);
                return null;
            }
            
            String name = unescapeString(parts[0]);
            double costPerGram = Double.parseDouble(parts[1]);
            int printTemp = Integer.parseInt(parts[2]);
            String color = unescapeString(parts[3]);
            
            return new Material(name, costPerGram, printTemp, color);
        } catch (Exception e) {
            System.err.println("Error deserializing material: " + e.getMessage());
            return null;
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
     * Creates a backup of the materials file.
     * @return true if backup was successful
     */
    public static boolean backupMaterials() {
        return DataFileManager.createBackup(MATERIALS_FILENAME);
    }
    
    /**
     * Restores materials from a backup file.
     * @param backupFilename The backup filename to restore from
     * @return true if restore was successful
     */
    public static boolean restoreMaterials(String backupFilename) {
        boolean restored = DataFileManager.restoreFromBackup(backupFilename, MATERIALS_FILENAME);
        if (restored) {
            // Reload materials after restore
            return loadMaterials();
        }
        return false;
    }
    
    /**
     * Restores materials from the most recent backup.
     * @return true if restore was successful
     */
    public static boolean restoreMaterialsFromLatestBackup() {
        boolean restored = DataFileManager.restoreFromLatestBackup(MATERIALS_FILENAME);
        if (restored) {
            // Reload materials after restore
            return loadMaterials();
        }
        return false;
    }
    
    /**
     * Lists all available material backups.
     * @return Array of backup filenames for materials
     */
    public static String[] listMaterialBackups() {
        return DataFileManager.listBackupsForFile(MATERIALS_FILENAME);
    }
    
    /**
     * Exports materials to a formatted text report.
     * @return The formatted report as a string
     */
    public static String exportMaterialsReport() {
        if (materialRegistry.isEmpty()) {
            return "No materials found in registry.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== MATERIAL REGISTRY REPORT ===\n");
        report.append("Generated: ").append(new Date()).append("\n");
        report.append("Total Materials: ").append(materialRegistry.size()).append("\n\n");
        
        for (int i = 0; i < materialRegistry.size(); i++) {
            Material material = materialRegistry.get(i);
            report.append("Material #").append(i + 1).append(":\n");
            report.append(material.getMaterialInfo()).append("\n\n");
        }
        
        return report.toString();
    }
}
