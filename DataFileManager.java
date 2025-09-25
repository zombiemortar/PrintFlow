import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Central file management utility for the 3D printing service system.
 * Provides common file operations, directory management, and error handling.
 */
public class DataFileManager {
    
    private static final String DATA_DIRECTORY = "data";
    private static final String BACKUP_DIRECTORY = "backups";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Ensures the data directory exists, creating it if necessary.
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            return Files.exists(dataPath) && Files.isDirectory(dataPath);
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ensures the backup directory exists, creating it if necessary.
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureBackupDirectory() {
        try {
            Path backupPath = Paths.get(BACKUP_DIRECTORY);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            return Files.exists(backupPath) && Files.isDirectory(backupPath);
        } catch (IOException e) {
            System.err.println("Error creating backup directory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Writes data to a file with error handling.
     * @param filename The name of the file to write to
     * @param data The data to write
     * @return true if write was successful
     */
    public static boolean writeToFile(String filename, String data) {
        if (!ensureDataDirectory()) {
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + File.separator + filename))) {
            writer.print(data);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reads data from a file with error handling.
     * @param filename The name of the file to read from
     * @return The file contents as a string, or null if read failed
     */
    public static String readFromFile(String filename) {
        if (!ensureDataDirectory()) {
            return null;
        }
        
        try {
            Path filePath = Paths.get(DATA_DIRECTORY, filename);
            if (!Files.exists(filePath)) {
                return null;
            }
            
            return Files.readString(filePath);
        } catch (IOException e) {
            System.err.println("Error reading from file " + filename + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates a backup of a file with timestamp.
     * @param filename The name of the file to backup
     * @return true if backup was successful
     */
    public static boolean createBackup(String filename) {
        if (!ensureBackupDirectory()) {
            return false;
        }
        
        try {
            Path sourcePath = Paths.get(DATA_DIRECTORY, filename);
            if (!Files.exists(sourcePath)) {
                return false;
            }
            
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String backupFilename = filename.replace(".txt", "_" + timestamp + ".txt");
            Path backupPath = Paths.get(BACKUP_DIRECTORY, backupFilename);
            
            Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup for " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a file exists in the data directory.
     * @param filename The name of the file to check
     * @return true if file exists
     */
    public static boolean fileExists(String filename) {
        if (!ensureDataDirectory()) {
            return false;
        }
        
        Path filePath = Paths.get(DATA_DIRECTORY, filename);
        return Files.exists(filePath);
    }
    
    /**
     * Deletes a file from the data directory.
     * @param filename The name of the file to delete
     * @return true if deletion was successful
     */
    public static boolean deleteFile(String filename) {
        if (!ensureDataDirectory()) {
            return false;
        }
        
        try {
            Path filePath = Paths.get(DATA_DIRECTORY, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Error deleting file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the full path to a file in the data directory.
     * @param filename The name of the file
     * @return The full path as a string
     */
    public static String getFilePath(String filename) {
        return DATA_DIRECTORY + File.separator + filename;
    }
    
    /**
     * Lists all files in the data directory.
     * @return Array of filenames, or empty array if directory doesn't exist
     */
    public static String[] listDataFiles() {
        if (!ensureDataDirectory()) {
            return new String[0];
        }
        
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            return Files.list(dataPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error listing data files: " + e.getMessage());
            return new String[0];
        }
    }
}
