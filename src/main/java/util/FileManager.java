package util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simplified file management utility for the 3D printing service system.
 * Consolidates all file operations, directory management, and error handling.
 */
public class FileManager {
    
    private static final String DATA_DIRECTORY = "data";
    private static final String BACKUP_DIRECTORY = "backups";
    private static final String EXPORTS_DIRECTORY = "exports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    
    /**
     * Ensures a directory exists, creating it if necessary.
     * @param directoryName The name of the directory
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureDirectory(String directoryName) {
        try {
            Path path = Paths.get(directoryName);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return Files.exists(path) && Files.isDirectory(path);
        } catch (IOException e) {
            System.err.println("Error creating directory " + directoryName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Writes data to a file with error handling.
     * @param filename The name of the file to write to
     * @param data The data to write
     * @param directory The directory to write to (data, backups, exports)
     * @return true if write was successful
     */
    public static boolean writeToFile(String filename, String data, String directory) {
        if (!ensureDirectory(directory)) {
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(directory + File.separator + filename))) {
            writer.print(data);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Writes data to a file in the data directory.
     * @param filename The name of the file to write to
     * @param data The data to write
     * @return true if write was successful
     */
    public static boolean writeToFile(String filename, String data) {
        return writeToFile(filename, data, DATA_DIRECTORY);
    }
    
    /**
     * Reads data from a file with error handling.
     * @param filename The name of the file to read from
     * @param directory The directory to read from
     * @return The file contents as a string, or null if read failed
     */
    public static String readFromFile(String filename, String directory) {
        if (!ensureDirectory(directory)) {
            return null;
        }
        
        try {
            Path filePath = Paths.get(directory, filename);
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
     * Reads data from a file in the data directory.
     * @param filename The name of the file to read from
     * @return The file contents as a string, or null if read failed
     */
    public static String readFromFile(String filename) {
        return readFromFile(filename, DATA_DIRECTORY);
    }
    
    /**
     * Creates a backup of a file with timestamp.
     * @param filename The name of the file to backup
     * @return true if backup was successful
     */
    public static boolean createBackup(String filename) {
        if (!ensureDirectory(BACKUP_DIRECTORY)) {
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
     * Checks if a file exists in the specified directory.
     * @param filename The name of the file to check
     * @param directory The directory to check in
     * @return true if file exists
     */
    public static boolean fileExists(String filename, String directory) {
        if (!ensureDirectory(directory)) {
            return false;
        }
        
        Path filePath = Paths.get(directory, filename);
        return Files.exists(filePath);
    }
    
    /**
     * Checks if a file exists in the data directory.
     * @param filename The name of the file to check
     * @return true if file exists
     */
    public static boolean fileExists(String filename) {
        return fileExists(filename, DATA_DIRECTORY);
    }
    
    /**
     * Lists all files in a directory.
     * @param directory The directory to list files from
     * @return Array of filenames, or empty array if directory doesn't exist
     */
    public static String[] listFiles(String directory) {
        try {
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                return new String[0];
            }
            
            return Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error listing files in " + directory + ": " + e.getMessage());
            return new String[0];
        }
    }
    
    /**
     * Gets the size of a file in bytes.
     * @param filename The name of the file
     * @param directory The directory containing the file
     * @return File size in bytes, or -1 if file doesn't exist
     */
    public static long getFileSize(String filename, String directory) {
        try {
            Path filePath = Paths.get(directory, filename);
            if (Files.exists(filePath)) {
                return Files.size(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error getting file size for " + filename + ": " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Gets the size of a file in the data directory.
     * @param filename The name of the file
     * @return File size in bytes, or -1 if file doesn't exist
     */
    public static long getFileSize(String filename) {
        return getFileSize(filename, DATA_DIRECTORY);
    }
    
    /**
     * Deletes a file from the specified directory.
     * @param filename The name of the file to delete
     * @param directory The directory containing the file
     * @return true if deletion was successful
     */
    public static boolean deleteFile(String filename, String directory) {
        try {
            Path filePath = Paths.get(directory, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error deleting file " + filename + ": " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Deletes a file from the data directory.
     * @param filename The name of the file to delete
     * @return true if deletion was successful
     */
    public static boolean deleteFile(String filename) {
        return deleteFile(filename, DATA_DIRECTORY);
    }
    
    /**
     * Gets the current timestamp formatted for filenames.
     * @return Formatted timestamp string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
    
    /**
     * Gets the data directory path.
     * @return Data directory path
     */
    public static String getDataDirectory() {
        return DATA_DIRECTORY;
    }
    
    /**
     * Gets the backup directory path.
     * @return Backup directory path
     */
    public static String getBackupDirectory() {
        return BACKUP_DIRECTORY;
    }
    
    /**
     * Gets the exports directory path.
     * @return Exports directory path
     */
    public static String getExportsDirectory() {
        return EXPORTS_DIRECTORY;
    }
    
    /**
     * Gets the full file path for a file in the data directory.
     * @param filename The name of the file
     * @return Full file path
     */
    public static String getFilePath(String filename) {
        return DATA_DIRECTORY + File.separator + filename;
    }
    
    /**
     * Restores a file from a backup.
     * @param backupFilename The backup filename to restore from
     * @param targetFilename The target filename to restore to
     * @return true if restore was successful
     */
    public static boolean restoreFromBackup(String backupFilename, String targetFilename) {
        try {
            Path backupPath = Paths.get(BACKUP_DIRECTORY, backupFilename);
            Path targetPath = Paths.get(DATA_DIRECTORY, targetFilename);
            
            if (!Files.exists(backupPath)) {
                return false;
            }
            
            ensureDirectory(DATA_DIRECTORY);
            Files.copy(backupPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error restoring from backup " + backupFilename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Restores a file from the most recent backup.
     * @param targetFilename The target filename to restore to
     * @return true if restore was successful
     */
    public static boolean restoreFromLatestBackup(String targetFilename) {
        String[] backups = listFiles(BACKUP_DIRECTORY);
        if (backups.length == 0) {
            return false;
        }
        
        // Find the most recent backup for this file
        String latestBackup = null;
        for (String backup : backups) {
            if (backup.startsWith(targetFilename.replace(".txt", "_"))) {
                if (latestBackup == null || backup.compareTo(latestBackup) > 0) {
                    latestBackup = backup;
                }
            }
        }
        
        if (latestBackup != null) {
            return restoreFromBackup(latestBackup, targetFilename);
        }
        
        return false;
    }
    
    /**
     * Lists all backup files for a specific file.
     * @param filename The filename to find backups for
     * @return Array of backup filenames
     */
    public static String[] listBackupsForFile(String filename) {
        String[] allBackups = listFiles(BACKUP_DIRECTORY);
        String baseName = filename.replace(".txt", "_");
        
        return java.util.Arrays.stream(allBackups)
                .filter(backup -> backup.startsWith(baseName))
                .toArray(String[]::new);
    }
}

