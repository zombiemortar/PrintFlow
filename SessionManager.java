/**
 * Session management system for user authentication and authorization.
 * Provides secure session handling with timeout and role-based access control.
 */
import java.util.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SessionManager {
    // Session configuration
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final int MAX_SESSIONS_PER_USER = 3;
    private static final int SESSION_ID_LENGTH = 32;
    
    // Session storage
    private static final Map<String, UserSession> activeSessions = new HashMap<>();
    private static final Map<String, List<String>> userSessions = new HashMap<>();
    
    // Security settings
    private static final SecureRandom random = new SecureRandom();
    private static boolean sessionManagementEnabled = true;
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods only.
     */
    private SessionManager() {
        // Utility class - no instances needed
    }
    
    /**
     * Authenticates a user and creates a session.
     * @param username The username to authenticate
     * @param password The password to verify
     * @return SessionResult containing session information or error details
     */
    public static SessionResult authenticateUser(String username, String password) {
        if (!sessionManagementEnabled) {
            return new SessionResult(false, "Session management is disabled", null);
        }
        
        if (username == null || password == null) {
            return new SessionResult(false, "Username and password are required", null);
        }
        
        // Find user in registry
        User user = UserFileHandler.getUserByUsername(username);
        if (user == null) {
            return new SessionResult(false, "Invalid username or password", null);
        }
        
        // Verify password
        if (!user.verifyPassword(password)) {
            return new SessionResult(false, "Invalid username or password", null);
        }
        
        // Check if user already has maximum sessions
        List<String> userSessionIds = userSessions.get(username);
        if (userSessionIds != null && userSessionIds.size() >= MAX_SESSIONS_PER_USER) {
            // Remove oldest session
            String oldestSessionId = userSessionIds.get(0);
            invalidateSession(oldestSessionId);
        }
        
        // Create new session
        String sessionId = generateSessionId();
        UserSession session = new UserSession(sessionId, user, LocalDateTime.now());
        
        activeSessions.put(sessionId, session);
        
        // Track user sessions
        if (userSessionIds == null) {
            userSessionIds = new ArrayList<>();
            userSessions.put(username, userSessionIds);
        }
        userSessionIds.add(sessionId);
        
        return new SessionResult(true, "Authentication successful", sessionId);
    }
    
    /**
     * Validates a session and returns the associated user.
     * @param sessionId The session ID to validate
     * @return User if session is valid, null otherwise
     */
    public static User validateSession(String sessionId) {
        if (!sessionManagementEnabled || sessionId == null) {
            return null;
        }
        
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        // Check if session has expired
        if (isSessionExpired(session)) {
            invalidateSession(sessionId);
            return null;
        }
        
        // Update last activity
        session.setLastActivity(LocalDateTime.now());
        
        return session.getUser();
    }
    
    /**
     * Checks if a user has admin privileges in the current session.
     * @param sessionId The session ID to check
     * @return true if user is admin, false otherwise
     */
    public static boolean isAdminSession(String sessionId) {
        User user = validateSession(sessionId);
        return user != null && "admin".equals(user.getRole());
    }
    
    /**
     * Invalidates a session.
     * @param sessionId The session ID to invalidate
     * @return true if session was invalidated, false if session didn't exist
     */
    public static boolean invalidateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            // Remove from user sessions tracking
            String username = session.getUser().getUsername();
            List<String> userSessionIds = userSessions.get(username);
            if (userSessionIds != null) {
                userSessionIds.remove(sessionId);
                if (userSessionIds.isEmpty()) {
                    userSessions.remove(username);
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Invalidates all sessions for a specific user.
     * @param username The username whose sessions should be invalidated
     * @return Number of sessions invalidated
     */
    public static int invalidateAllUserSessions(String username) {
        if (username == null) {
            return 0;
        }
        
        List<String> userSessionIds = userSessions.get(username);
        if (userSessionIds == null) {
            return 0;
        }
        
        int invalidatedCount = 0;
        for (String sessionId : new ArrayList<>(userSessionIds)) {
            if (invalidateSession(sessionId)) {
                invalidatedCount++;
            }
        }
        
        return invalidatedCount;
    }
    
    /**
     * Cleans up expired sessions.
     * @return Number of sessions cleaned up
     */
    public static int cleanupExpiredSessions() {
        if (!sessionManagementEnabled) {
            return 0;
        }
        
        List<String> expiredSessions = new ArrayList<>();
        
        for (Map.Entry<String, UserSession> entry : activeSessions.entrySet()) {
            if (isSessionExpired(entry.getValue())) {
                expiredSessions.add(entry.getKey());
            }
        }
        
        int cleanedCount = 0;
        for (String sessionId : expiredSessions) {
            if (invalidateSession(sessionId)) {
                cleanedCount++;
            }
        }
        
        return cleanedCount;
    }
    
    /**
     * Gets session information for a session ID.
     * @param sessionId The session ID
     * @return SessionInfo object or null if session doesn't exist
     */
    public static SessionInfo getSessionInfo(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        return new SessionInfo(sessionId, session.getUser(), 
                             session.getCreatedAt(), session.getLastActivity(),
                             isSessionExpired(session));
    }
    
    /**
     * Gets all active sessions for a user.
     * @param username The username
     * @return List of session IDs for the user
     */
    public static List<String> getUserSessions(String username) {
        if (username == null) {
            return new ArrayList<>();
        }
        
        List<String> userSessionIds = userSessions.get(username);
        if (userSessionIds == null) {
            return new ArrayList<>();
        }
        
        // Return a copy to prevent external modification
        return new ArrayList<>(userSessionIds);
    }
    
    /**
     * Gets statistics about current sessions.
     * @return SessionStatistics object with current session data
     */
    public static SessionStatistics getSessionStatistics() {
        int totalSessions = activeSessions.size();
        int expiredSessions = 0;
        int adminSessions = 0;
        
        for (UserSession session : activeSessions.values()) {
            if (isSessionExpired(session)) {
                expiredSessions++;
            }
            if ("admin".equals(session.getUser().getRole())) {
                adminSessions++;
            }
        }
        
        return new SessionStatistics(totalSessions, expiredSessions, adminSessions, 
                                   userSessions.size(), sessionManagementEnabled);
    }
    
    /**
     * Enables or disables session management.
     * @param enabled Whether to enable session management
     */
    public static void setSessionManagementEnabled(boolean enabled) {
        sessionManagementEnabled = enabled;
        
        if (!enabled) {
            // Clear all sessions when disabling
            activeSessions.clear();
            userSessions.clear();
        }
    }
    
    /**
     * Checks if session management is enabled.
     * @return true if enabled, false otherwise
     */
    public static boolean isSessionManagementEnabled() {
        return sessionManagementEnabled;
    }
    
    /**
     * Generates a secure random session ID.
     * @return A random session ID string
     */
    private static String generateSessionId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sessionId = new StringBuilder();
        
        for (int i = 0; i < SESSION_ID_LENGTH; i++) {
            sessionId.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sessionId.toString();
    }
    
    /**
     * Checks if a session has expired.
     * @param session The session to check
     * @return true if expired, false otherwise
     */
    private static boolean isSessionExpired(UserSession session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastActivity = session.getLastActivity();
        
        long minutesSinceLastActivity = ChronoUnit.MINUTES.between(lastActivity, now);
        return minutesSinceLastActivity > SESSION_TIMEOUT_MINUTES;
    }
    
    /**
     * Clears all sessions (for testing purposes).
     */
    public static void clearAllSessions() {
        activeSessions.clear();
        userSessions.clear();
    }
}

/**
 * Represents a user session with timing and user information.
 */
class UserSession {
    private final String sessionId;
    private final User user;
    private final LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    
    /**
     * Creates a new user session.
     * @param sessionId The unique session ID
     * @param user The user associated with this session
     * @param createdAt When the session was created
     */
    public UserSession(String sessionId, User user, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.user = user;
        this.createdAt = createdAt;
        this.lastActivity = createdAt;
    }
    
    /**
     * Gets the session ID.
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Gets the user associated with this session.
     * @return The user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Gets when the session was created.
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Gets the last activity timestamp.
     * @return The last activity timestamp
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    /**
     * Sets the last activity timestamp.
     * @param lastActivity The new last activity timestamp
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}

/**
 * Result of an authentication attempt.
 */
class SessionResult {
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

/**
 * Information about a session.
 */
class SessionInfo {
    private final String sessionId;
    private final User user;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastActivity;
    private final boolean expired;
    
    /**
     * Creates session information.
     * @param sessionId The session ID
     * @param user The user
     * @param createdAt When created
     * @param lastActivity Last activity time
     * @param expired Whether expired
     */
    public SessionInfo(String sessionId, User user, LocalDateTime createdAt, 
                      LocalDateTime lastActivity, boolean expired) {
        this.sessionId = sessionId;
        this.user = user;
        this.createdAt = createdAt;
        this.lastActivity = lastActivity;
        this.expired = expired;
    }
    
    /**
     * Gets the session ID.
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Gets the user.
     * @return The user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Gets the creation time.
     * @return The creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Gets the last activity time.
     * @return The last activity time
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    /**
     * Checks if the session is expired.
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return expired;
    }
}

/**
 * Statistics about current sessions.
 */
class SessionStatistics {
    private final int totalSessions;
    private final int expiredSessions;
    private final int adminSessions;
    private final int uniqueUsers;
    private final boolean sessionManagementEnabled;
    
    /**
     * Creates session statistics.
     * @param totalSessions Total number of sessions
     * @param expiredSessions Number of expired sessions
     * @param adminSessions Number of admin sessions
     * @param uniqueUsers Number of unique users with sessions
     * @param sessionManagementEnabled Whether session management is enabled
     */
    public SessionStatistics(int totalSessions, int expiredSessions, int adminSessions, 
                           int uniqueUsers, boolean sessionManagementEnabled) {
        this.totalSessions = totalSessions;
        this.expiredSessions = expiredSessions;
        this.adminSessions = adminSessions;
        this.uniqueUsers = uniqueUsers;
        this.sessionManagementEnabled = sessionManagementEnabled;
    }
    
    /**
     * Gets total sessions.
     * @return Total sessions
     */
    public int getTotalSessions() {
        return totalSessions;
    }
    
    /**
     * Gets expired sessions.
     * @return Expired sessions
     */
    public int getExpiredSessions() {
        return expiredSessions;
    }
    
    /**
     * Gets admin sessions.
     * @return Admin sessions
     */
    public int getAdminSessions() {
        return adminSessions;
    }
    
    /**
     * Gets unique users.
     * @return Unique users
     */
    public int getUniqueUsers() {
        return uniqueUsers;
    }
    
    /**
     * Checks if session management is enabled.
     * @return true if enabled, false otherwise
     */
    public boolean isSessionManagementEnabled() {
        return sessionManagementEnabled;
    }
    
    /**
     * Gets a formatted summary of session statistics.
     * @return Formatted statistics string
     */
    public String getSummary() {
        return String.format("""
                SESSION STATISTICS
                ==================
                Total Sessions: %d
                Expired Sessions: %d
                Admin Sessions: %d
                Unique Users: %d
                Session Management: %s
                """,
                totalSessions, expiredSessions, adminSessions, uniqueUsers,
                sessionManagementEnabled ? "Enabled" : "Disabled");
    }
}
