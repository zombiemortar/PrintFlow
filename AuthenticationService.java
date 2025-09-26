/**
 * Authentication service that provides secure user authentication and authorization.
 * Integrates password security and session management for comprehensive access control.
 */
public class AuthenticationService {
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods only.
     */
    private AuthenticationService() {
        // Utility class - no instances needed
    }
    
    /**
     * Authenticates a user with username and password.
     * @param username The username
     * @param password The password
     * @return AuthenticationResult containing session information or error details
     */
    public static AuthenticationResult authenticate(String username, String password) {
        if (username == null || password == null) {
            return new AuthenticationResult(false, "Username and password are required", null, null);
        }
        
        // Use session manager for authentication
        SessionResult sessionResult = SessionManager.authenticateUser(username, password);
        
        if (sessionResult.isSuccess()) {
            User user = UserFileHandler.getUserByUsername(username);
            return new AuthenticationResult(true, sessionResult.getMessage(), 
                                          sessionResult.getSessionId(), user);
        } else {
            return new AuthenticationResult(false, sessionResult.getMessage(), null, null);
        }
    }
    
    /**
     * Validates a session and returns the authenticated user.
     * @param sessionId The session ID to validate
     * @return AuthenticationResult with user information if valid
     */
    public static AuthenticationResult validateSession(String sessionId) {
        if (sessionId == null) {
            return new AuthenticationResult(false, "Session ID is required", null, null);
        }
        
        User user = SessionManager.validateSession(sessionId);
        
        if (user != null) {
            return new AuthenticationResult(true, "Session is valid", sessionId, user);
        } else {
            return new AuthenticationResult(false, "Invalid or expired session", null, null);
        }
    }
    
    /**
     * Checks if a session has admin privileges.
     * @param sessionId The session ID to check
     * @return true if the session belongs to an admin user
     */
    public static boolean isAdminSession(String sessionId) {
        return SessionManager.isAdminSession(sessionId);
    }
    
    /**
     * Logs out a user by invalidating their session.
     * @param sessionId The session ID to invalidate
     * @return true if logout was successful
     */
    public static boolean logout(String sessionId) {
        return SessionManager.invalidateSession(sessionId);
    }
    
    /**
     * Logs out all sessions for a specific user.
     * @param username The username whose sessions should be invalidated
     * @return Number of sessions invalidated
     */
    public static int logoutAllSessions(String username) {
        return SessionManager.invalidateAllUserSessions(username);
    }
    
    /**
     * Creates a new user account with secure password.
     * @param username The username
     * @param email The email address
     * @param role The user role
     * @param password The plain text password
     * @return UserCreationResult with success status and user information
     */
    public static UserCreationResult createUser(String username, String email, String role, String password) {
        if (username == null || email == null || role == null || password == null) {
            return new UserCreationResult(false, "All fields are required", null);
        }
        
        // Validate password strength
        PasswordValidationResult passwordValidation = PasswordSecurity.validatePasswordStrength(password);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("Password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("  - ").append(error).append("\n");
            }
            return new UserCreationResult(false, errorMsg.toString(), null);
        }
        
        // Check if user already exists
        if (UserFileHandler.getUserByUsername(username) != null) {
            return new UserCreationResult(false, "Username already exists", null);
        }
        
        // Create user
        User user;
        if ("admin".equals(role)) {
            user = new AdminUser(username, email, password);
        } else {
            user = new User(username, email, role, password);
        }
        
        // Add to registry
        boolean added = UserFileHandler.addUser(user);
        if (!added) {
            return new UserCreationResult(false, "Failed to add user to system", null);
        }
        
        // Save to file
        boolean saved = UserFileHandler.saveUsers();
        if (!saved) {
            // Remove from registry if save failed
            UserFileHandler.removeUser(username);
            return new UserCreationResult(false, "Failed to save user data", null);
        }
        
        return new UserCreationResult(true, "User created successfully", user);
    }
    
    /**
     * Changes a user's password.
     * @param sessionId The session ID of the user changing password
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return PasswordChangeResult with success status and details
     */
    public static PasswordChangeResult changePassword(String sessionId, String oldPassword, String newPassword) {
        if (sessionId == null || oldPassword == null || newPassword == null) {
            return new PasswordChangeResult(false, "All fields are required");
        }
        
        // Validate session
        User user = SessionManager.validateSession(sessionId);
        if (user == null) {
            return new PasswordChangeResult(false, "Invalid or expired session");
        }
        
        // Validate new password strength
        PasswordValidationResult passwordValidation = PasswordSecurity.validatePasswordStrength(newPassword);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("New password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("  - ").append(error).append("\n");
            }
            return new PasswordChangeResult(false, errorMsg.toString());
        }
        
        // Change password
        boolean changed = user.changePassword(oldPassword, newPassword);
        if (!changed) {
            return new PasswordChangeResult(false, "Failed to change password. Please verify your current password.");
        }
        
        // Save user data
        boolean saved = UserFileHandler.saveUsers();
        if (!saved) {
            return new PasswordChangeResult(false, "Password changed but failed to save. Please try again.");
        }
        
        return new PasswordChangeResult(true, "Password changed successfully");
    }
    
    /**
     * Resets a user's password (admin function).
     * @param adminSessionId The admin session ID
     * @param targetUsername The username whose password to reset
     * @param newPassword The new password
     * @return PasswordResetResult with success status and details
     */
    public static PasswordResetResult resetPassword(String adminSessionId, String targetUsername, String newPassword) {
        if (adminSessionId == null || targetUsername == null || newPassword == null) {
            return new PasswordResetResult(false, "All fields are required");
        }
        
        // Verify admin session
        if (!SessionManager.isAdminSession(adminSessionId)) {
            return new PasswordResetResult(false, "Admin privileges required");
        }
        
        // Get target user
        User targetUser = UserFileHandler.getUserByUsername(targetUsername);
        if (targetUser == null) {
            return new PasswordResetResult(false, "User not found");
        }
        
        // Validate new password strength
        PasswordValidationResult passwordValidation = PasswordSecurity.validatePasswordStrength(newPassword);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("New password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("  - ").append(error).append("\n");
            }
            return new PasswordResetResult(false, errorMsg.toString());
        }
        
        // Set new password
        boolean set = targetUser.setPassword(newPassword);
        if (!set) {
            return new PasswordResetResult(false, "Failed to set new password");
        }
        
        // Save user data
        boolean saved = UserFileHandler.saveUsers();
        if (!saved) {
            return new PasswordResetResult(false, "Password reset but failed to save. Please try again.");
        }
        
        // Invalidate all sessions for the target user
        int invalidatedSessions = SessionManager.invalidateAllUserSessions(targetUsername);
        
        return new PasswordResetResult(true, "Password reset successfully. " + 
                                       invalidatedSessions + " sessions invalidated.");
    }
    
    /**
     * Gets authentication statistics.
     * @return AuthenticationStatistics object with current authentication data
     */
    public static AuthenticationStatistics getAuthenticationStatistics() {
        SessionStatistics sessionStats = SessionManager.getSessionStatistics();
        User[] allUsers = UserFileHandler.getAllUsers();
        
        int totalUsers = allUsers.length;
        int usersWithPasswords = 0;
        int adminUsers = 0;
        
        for (User user : allUsers) {
            if (user.hasPassword()) {
                usersWithPasswords++;
            }
            if ("admin".equals(user.getRole())) {
                adminUsers++;
            }
        }
        
        return new AuthenticationStatistics(totalUsers, usersWithPasswords, adminUsers, 
                                          sessionStats.getTotalSessions(), 
                                          sessionStats.getUniqueUsers(),
                                          SessionManager.isSessionManagementEnabled());
    }
    
    /**
     * Generates a secure password for a user.
     * @param length The desired password length (8-32 characters)
     * @return A randomly generated secure password
     */
    public static String generateSecurePassword(int length) {
        return PasswordSecurity.generateSecurePassword(length);
    }
    
    /**
     * Validates password strength.
     * @param password The password to validate
     * @return PasswordValidationResult with validation details
     */
    public static PasswordValidationResult validatePasswordStrength(String password) {
        return PasswordSecurity.validatePasswordStrength(password);
    }
    
    /**
     * Enables or disables authentication features.
     * @param enabled Whether to enable authentication
     */
    public static void setAuthenticationEnabled(boolean enabled) {
        SessionManager.setSessionManagementEnabled(enabled);
    }
    
    /**
     * Checks if authentication is enabled.
     * @return true if enabled, false otherwise
     */
    public static boolean isAuthenticationEnabled() {
        return SessionManager.isSessionManagementEnabled();
    }
}

/**
 * Result of an authentication attempt.
 */
class AuthenticationResult {
    private final boolean success;
    private final String message;
    private final String sessionId;
    private final User user;
    
    /**
     * Creates an authentication result.
     * @param success Whether authentication was successful
     * @param message The result message
     * @param sessionId The session ID (if successful)
     * @param user The authenticated user (if successful)
     */
    public AuthenticationResult(boolean success, String message, String sessionId, User user) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
        this.user = user;
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
    
    /**
     * Gets the authenticated user.
     * @return The user (null if authentication failed)
     */
    public User getUser() {
        return user;
    }
}

/**
 * Result of user creation attempt.
 */
class UserCreationResult {
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

/**
 * Result of password change attempt.
 */
class PasswordChangeResult {
    private final boolean success;
    private final String message;
    
    /**
     * Creates a password change result.
     * @param success Whether change was successful
     * @param message The result message
     */
    public PasswordChangeResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Checks if change was successful.
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
}

/**
 * Result of password reset attempt.
 */
class PasswordResetResult {
    private final boolean success;
    private final String message;
    
    /**
     * Creates a password reset result.
     * @param success Whether reset was successful
     * @param message The result message
     */
    public PasswordResetResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    /**
     * Checks if reset was successful.
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
}

/**
 * Statistics about authentication system.
 */
class AuthenticationStatistics {
    private final int totalUsers;
    private final int usersWithPasswords;
    private final int adminUsers;
    private final int activeSessions;
    private final int loggedInUsers;
    private final boolean authenticationEnabled;
    
    /**
     * Creates authentication statistics.
     * @param totalUsers Total number of users
     * @param usersWithPasswords Users with passwords set
     * @param adminUsers Number of admin users
     * @param activeSessions Number of active sessions
     * @param loggedInUsers Number of logged-in users
     * @param authenticationEnabled Whether authentication is enabled
     */
    public AuthenticationStatistics(int totalUsers, int usersWithPasswords, int adminUsers, 
                                  int activeSessions, int loggedInUsers, boolean authenticationEnabled) {
        this.totalUsers = totalUsers;
        this.usersWithPasswords = usersWithPasswords;
        this.adminUsers = adminUsers;
        this.activeSessions = activeSessions;
        this.loggedInUsers = loggedInUsers;
        this.authenticationEnabled = authenticationEnabled;
    }
    
    /**
     * Gets total users.
     * @return Total users
     */
    public int getTotalUsers() {
        return totalUsers;
    }
    
    /**
     * Gets users with passwords.
     * @return Users with passwords
     */
    public int getUsersWithPasswords() {
        return usersWithPasswords;
    }
    
    /**
     * Gets admin users.
     * @return Admin users
     */
    public int getAdminUsers() {
        return adminUsers;
    }
    
    /**
     * Gets active sessions.
     * @return Active sessions
     */
    public int getActiveSessions() {
        return activeSessions;
    }
    
    /**
     * Gets logged-in users.
     * @return Logged-in users
     */
    public int getLoggedInUsers() {
        return loggedInUsers;
    }
    
    /**
     * Checks if authentication is enabled.
     * @return true if enabled, false otherwise
     */
    public boolean isAuthenticationEnabled() {
        return authenticationEnabled;
    }
    
    /**
     * Gets a formatted summary of authentication statistics.
     * @return Formatted statistics string
     */
    public String getSummary() {
        return String.format("""
                AUTHENTICATION STATISTICS
                =========================
                Total Users: %d
                Users with Passwords: %d
                Admin Users: %d
                Active Sessions: %d
                Logged-in Users: %d
                Authentication: %s
                """,
                totalUsers, usersWithPasswords, adminUsers, activeSessions, loggedInUsers,
                authenticationEnabled ? "Enabled" : "Disabled");
    }
}
