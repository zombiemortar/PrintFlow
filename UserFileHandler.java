import java.util.*;

/**
 * Handles file operations for User objects.
 * Provides methods to save and load user data to/from files.
 */
public class UserFileHandler {
    
    private static final String USERS_FILENAME = "users.txt";
    private static final Map<String, User> userRegistry = new HashMap<>();
    
    /**
     * Saves all users to a file.
     * @return true if save was successful
     */
    public static boolean saveUsers() {
        if (userRegistry.isEmpty()) {
            return DataFileManager.writeToFile(USERS_FILENAME, "");
        }
        
        StringBuilder data = new StringBuilder();
        data.append("# User Data Export\n");
        data.append("# Format: username|email|role\n");
        data.append("# Generated: ").append(new Date()).append("\n\n");
        
        for (User user : userRegistry.values()) {
            data.append(serializeUser(user)).append("\n");
        }
        
        return DataFileManager.writeToFile(USERS_FILENAME, data.toString());
    }
    
    /**
     * Loads users from file.
     * @return true if load was successful
     */
    public static boolean loadUsers() {
        String data = DataFileManager.readFromFile(USERS_FILENAME);
        if (data == null || data.trim().isEmpty()) {
            return true; // No data to load is not an error
        }
        
        try {
            String[] lines = data.split("\n");
            int loadedCount = 0;
            
            // Clear existing registry
            userRegistry.clear();
            
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                User user = deserializeUser(line);
                if (user != null) {
                    userRegistry.put(user.getUsername(), user);
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
     * Adds a user to the registry.
     * @param user The user to add
     * @return true if user was added successfully
     */
    public static boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        
        String username = user.getUsername().trim();
        
        // Check if user with same username already exists
        if (userRegistry.containsKey(username)) {
            System.err.println("User with username '" + username + "' already exists");
            return false;
        }
        
        userRegistry.put(username, user);
        return true;
    }
    
    /**
     * Gets a user by username.
     * @param username The username to find
     * @return The user if found, null otherwise
     */
    public static User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        return userRegistry.get(username.trim());
    }
    
    /**
     * Gets all users in the registry.
     * @return Array of all users
     */
    public static User[] getAllUsers() {
        return userRegistry.values().toArray(new User[0]);
    }
    
    /**
     * Removes a user from the registry.
     * @param username The username of the user to remove
     * @return true if user was removed successfully
     */
    public static boolean removeUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        return userRegistry.remove(username.trim()) != null;
    }
    
    /**
     * Updates an existing user in the registry.
     * @param username The username of the user to update
     * @param updatedUser The updated user data
     * @return true if user was updated successfully
     */
    public static boolean updateUser(String username, User updatedUser) {
        if (username == null || username.trim().isEmpty() || updatedUser == null) {
            return false;
        }
        
        String trimmedUsername = username.trim();
        if (!userRegistry.containsKey(trimmedUsername)) {
            return false;
        }
        
        userRegistry.put(trimmedUsername, updatedUser);
        return true;
    }
    
    /**
     * Clears all users from the registry.
     */
    public static void clearUsers() {
        userRegistry.clear();
    }
    
    /**
     * Gets the number of users in the registry.
     * @return The count of users
     */
    public static int getUserCount() {
        return userRegistry.size();
    }
    
    /**
     * Checks if a user exists in the registry.
     * @param username The username to check
     * @return true if user exists
     */
    public static boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        return userRegistry.containsKey(username.trim());
    }
    
    /**
     * Gets users by role.
     * @param role The role to filter by
     * @return Array of users with the specified role
     */
    public static User[] getUsersByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return new User[0];
        }
        
        List<User> usersWithRole = new ArrayList<>();
        for (User user : userRegistry.values()) {
            if (role.equals(user.getRole())) {
                usersWithRole.add(user);
            }
        }
        
        return usersWithRole.toArray(new User[0]);
    }
    
    /**
     * Validates user data before adding to registry.
     * @param user The user to validate
     * @return true if user data is valid
     */
    public static boolean validateUser(User user) {
        if (user == null) {
            return false;
        }
        
        String username = user.getUsername();
        String email = user.getEmail();
        String role = user.getRole();
        
        // Check username
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username cannot be empty");
            return false;
        }
        
        // Check email format (basic validation)
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            System.err.println("Invalid email format");
            return false;
        }
        
        // Check role
        if (role == null || role.trim().isEmpty()) {
            System.err.println("Role cannot be empty");
            return false;
        }
        
        return true;
    }
    
    /**
     * Serializes a User object to a string format.
     * @param user The user to serialize
     * @return Serialized string representation
     */
    private static String serializeUser(User user) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeString(user.getUsername())).append("|");
        sb.append(escapeString(user.getEmail())).append("|");
        sb.append(escapeString(user.getRole()));
        
        return sb.toString();
    }
    
    /**
     * Deserializes a string to a User object.
     * @param data The serialized string data
     * @return The deserialized User object, or null if parsing failed
     */
    private static User deserializeUser(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length < 3) {
                System.err.println("Invalid user data format: " + data);
                return null;
            }
            
            String username = unescapeString(parts[0]);
            String email = unescapeString(parts[1]);
            String role = unescapeString(parts[2]);
            
            return new User(username, email, role);
        } catch (Exception e) {
            System.err.println("Error deserializing user: " + e.getMessage());
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
     * Creates a backup of the users file.
     * @return true if backup was successful
     */
    public static boolean backupUsers() {
        return DataFileManager.createBackup(USERS_FILENAME);
    }
    
    /**
     * Exports users to a formatted text report.
     * @return The formatted report as a string
     */
    public static String exportUsersReport() {
        if (userRegistry.isEmpty()) {
            return "No users found in registry.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== USER REGISTRY REPORT ===\n");
        report.append("Generated: ").append(new Date()).append("\n");
        report.append("Total Users: ").append(userRegistry.size()).append("\n\n");
        
        // Group users by role
        Map<String, List<User>> usersByRole = new HashMap<>();
        for (User user : userRegistry.values()) {
            String role = user.getRole();
            usersByRole.computeIfAbsent(role, k -> new ArrayList<>()).add(user);
        }
        
        for (Map.Entry<String, List<User>> entry : usersByRole.entrySet()) {
            String role = entry.getKey();
            List<User> users = entry.getValue();
            
            report.append("Role: ").append(role).append(" (").append(users.size()).append(" users)\n");
            for (User user : users) {
                report.append("  - ").append(user.getUsername()).append(" (").append(user.getEmail()).append(")\n");
            }
            report.append("\n");
        }
        
        return report.toString();
    }
}
