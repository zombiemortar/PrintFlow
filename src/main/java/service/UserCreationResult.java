package service;

import model.User;

/**
 * Result of user creation attempt.
 */
public class UserCreationResult {
    private final boolean success;
    private final String message;
    private final User user;
    
    /**
     * Creates a user creation result.
     * @param success Whether creation was successful
     * @param message The result message
     * @param user The created user (if successful)
     */
    public UserCreationResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }
    
    /**
     * Checks if creation was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the result message.
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the created user.
     * @return The user (null if creation failed)
     */
    public User getUser() {
        return user;
    }
}
