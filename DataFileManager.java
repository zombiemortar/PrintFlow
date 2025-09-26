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
    
    /**
     * Lists all backup files in the backup directory.
     * @return Array of backup filenames, or empty array if directory doesn't exist
     */
    public static String[] listBackupFiles() {
        if (!ensureBackupDirectory()) {
            return new String[0];
        }
        
        try {
            Path backupPath = Paths.get(BACKUP_DIRECTORY);
            return Files.list(backupPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted() // Sort by filename (which includes timestamp)
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error listing backup files: " + e.getMessage());
            return new String[0];
        }
    }
    
    /**
     * Lists backup files for a specific data file.
     * @param filename The base filename to find backups for
     * @return Array of backup filenames for the specified file
     */
    public static String[] listBackupsForFile(String filename) {
        String[] allBackups = listBackupFiles();
        String baseName = filename.replace(".txt", "");
        
        return java.util.Arrays.stream(allBackups)
                .filter(backup -> backup.startsWith(baseName + "_") && backup.endsWith(".txt"))
                .sorted()
                .toArray(String[]::new);
    }
    
    /**
     * Restores a file from a backup.
     * @param backupFilename The backup filename to restore from
     * @param targetFilename The target filename to restore to (if null, uses original filename)
     * @return true if restore was successful
     */
    public static boolean restoreFromBackup(String backupFilename, String targetFilename) {
        if (!ensureBackupDirectory() || !ensureDataDirectory()) {
            return false;
        }
        
        try {
            Path backupPath = Paths.get(BACKUP_DIRECTORY, backupFilename);
            if (!Files.exists(backupPath)) {
                System.err.println("Backup file not found: " + backupFilename);
                return false;
            }
            
            // Determine target filename
            String actualTargetFilename = targetFilename;
            if (actualTargetFilename == null) {
                // Extract original filename from backup filename
                // Format: filename_YYYYMMDD_HHMMSS.txt -> filename.txt
                int lastUnderscore = backupFilename.lastIndexOf('_');
                if (lastUnderscore > 0) {
                    actualTargetFilename = backupFilename.substring(0, lastUnderscore) + ".txt";
                } else {
                    actualTargetFilename = backupFilename;
                }
            }
            
            Path targetPath = Paths.get(DATA_DIRECTORY, actualTargetFilename);
            
            // Create backup of current file before restoring
            if (Files.exists(targetPath)) {
                createBackup(actualTargetFilename);
            }
            
            // Copy backup to target location
            Files.copy(backupPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Restored " + backupFilename + " to " + actualTargetFilename);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error restoring from backup " + backupFilename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Restores a file from its most recent backup.
     * @param filename The filename to restore
     * @return true if restore was successful
     */
    public static boolean restoreFromLatestBackup(String filename) {
        String[] backups = listBackupsForFile(filename);
        if (backups.length == 0) {
            System.err.println("No backups found for file: " + filename);
            return false;
        }
        
        // Get the most recent backup (last in sorted array)
        String latestBackup = backups[backups.length - 1];
        return restoreFromBackup(latestBackup, filename);
    }
    
    /**
     * Gets information about a backup file.
     * @param backupFilename The backup filename
     * @return Map containing backup information, or null if file doesn't exist
     */
    public static java.util.Map<String, Object> getBackupInfo(String backupFilename) {
        if (!ensureBackupDirectory()) {
            return null;
        }
        
        try {
            Path backupPath = Paths.get(BACKUP_DIRECTORY, backupFilename);
            if (!Files.exists(backupPath)) {
                return null;
            }
            
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            
            // Extract timestamp from filename
            String baseName = backupFilename.replace(".txt", "");
            int lastUnderscore = baseName.lastIndexOf('_');
            if (lastUnderscore > 0) {
                String timestampStr = baseName.substring(lastUnderscore + 1);
                String originalFilename = baseName.substring(0, lastUnderscore) + ".txt";
                
                info.put("original_filename", originalFilename);
                info.put("timestamp", timestampStr);
                
                // Parse timestamp
                try {
                    LocalDateTime timestamp = LocalDateTime.parse(timestampStr, TIMESTAMP_FORMAT);
                    info.put("date_time", timestamp);
                    info.put("formatted_date", timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } catch (Exception e) {
                    info.put("date_time", null);
                    info.put("formatted_date", "Unknown");
                }
            }
            
            // Get file size
            long fileSize = Files.size(backupPath);
            info.put("file_size_bytes", fileSize);
            info.put("file_size_kb", fileSize / 1024.0);
            
            // Get last modified time
            java.nio.file.attribute.FileTime lastModified = Files.getLastModifiedTime(backupPath);
            info.put("last_modified", lastModified.toInstant());
            
            return info;
            
        } catch (IOException e) {
            System.err.println("Error getting backup info for " + backupFilename + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Deletes old backup files, keeping only the most recent ones.
     * @param filename The base filename to clean up backups for
     * @param keepCount Number of recent backups to keep
     * @return Number of backup files deleted
     */
    public static int cleanupOldBackups(String filename, int keepCount) {
        String[] backups = listBackupsForFile(filename);
        if (backups.length <= keepCount) {
            return 0; // No cleanup needed
        }
        
        int deletedCount = 0;
        // Delete oldest backups (first in sorted array)
        for (int i = 0; i < backups.length - keepCount; i++) {
            try {
                Path backupPath = Paths.get(BACKUP_DIRECTORY, backups[i]);
                Files.delete(backupPath);
                deletedCount++;
                System.out.println("Deleted old backup: " + backups[i]);
            } catch (IOException e) {
                System.err.println("Error deleting backup " + backups[i] + ": " + e.getMessage());
            }
        }
        
        return deletedCount;
    }
    
    /**
     * Deletes old backup files for all data files, keeping only the most recent ones.
     * @param keepCount Number of recent backups to keep per file
     * @return Total number of backup files deleted
     */
    public static int cleanupAllOldBackups(int keepCount) {
        String[] dataFiles = listDataFiles();
        int totalDeleted = 0;
        
        for (String filename : dataFiles) {
            totalDeleted += cleanupOldBackups(filename, keepCount);
        }
        
        return totalDeleted;
    }
}
