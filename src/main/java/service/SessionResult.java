package service;

/**
 * Result of an authentication attempt.
 */
public class SessionResult {
    private final boolean success;
    private final String message;
    private final String sessionId;
    
    /**
     * Creates a session result.
     * @param success Whether authentication was successful
     * @param message The result message
     * @param sessionId The session ID (if successful)
     */
    public SessionResult(boolean success, String message, String sessionId) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
    }
    
    /**
     * Checks if authentication was successful.
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
     * Gets the session ID.
     * @return The session ID (null if authentication failed)
     */
    public String getSessionId() {
        return sessionId;
    }
}
