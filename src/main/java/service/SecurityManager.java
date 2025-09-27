package service;

import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import model.User;
import model.Order;
import util.InputValidator;
import util.ValidationResult;

/**
 * Simplified security manager for the 3D printing service system.
 * Consolidates password security, session management, and authentication operations.
 */
public class SecurityManager {
    
    // Password security constants
    private static final int BCRYPT_ROUNDS = 12;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    
    // Session management constants
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final int MAX_SESSIONS_PER_USER = 3;
    private static final int SESSION_ID_LENGTH = 32;
    
    // Regex patterns for password strength validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    
    // Common weak passwords to reject
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "1234567890", "password1",
        "123123", "dragon", "master", "hello", "freedom", "whatever", "qazwsx",
        "trustno1", "654321", "jordan23", "harley", "password1", "jordan"
    };
    
    // Session storage
    private static final Map<String, UserSession> activeSessions = new HashMap<>();
    private static final Map<String, List<String>> userSessions = new HashMap<>();
    
    // Security settings
    private static final SecureRandom random = new SecureRandom();
    private static boolean securityEnabled = true;
    
    /**
     * Validates an order using the input validator.
     * @param order The order to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateOrder(Order order) {
        return InputValidator.validateOrder(order);
    }
    
    /**
     * Validates a user using the input validator.
     * @param user The user to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateUser(User user) {
        return InputValidator.validateUser(user);
    }
    
    /**
     * Validates a material name using the input validator.
     * @param materialName The material name to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateMaterialName(String materialName) {
        return InputValidator.validateMaterialName(materialName);
    }
    
    // ==================== PASSWORD SECURITY ====================
    
    /**
     * Hashes a password using BCrypt algorithm.
     * @param plainPassword The plain text password to hash
     * @return The hashed password string, or null if hashing failed
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Generate a random salt
            String salt = generateSalt();
            
            // Hash the password with the salt using BCrypt
            return BCrypt.hashpw(plainPassword, salt);
        } catch (Exception e) {
            System.err.println("Password hashing failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies a password against its hash.
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The stored hashed password
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Password verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates password strength according to security requirements.
     * @param password The password to validate
     * @return PasswordValidationResult containing validation status and messages
     */
    public static PasswordValidationResult validatePasswordStrength(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null) {
            result.addError("Password cannot be null");
            return result;
        }
        
        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addError("Password must be no more than " + MAX_PASSWORD_LENGTH + " characters long");
        }
        
        // Check for common passwords
        if (isCommonPassword(password)) {
            result.addError("Password is too common and easily guessable");
        }
        
        // Check character requirements
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one uppercase letter");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one lowercase letter");
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one digit");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one special character");
        }
        
        // Calculate strength score
        int score = calculatePasswordStrengthScore(password);
        result.setStrengthScore(score);
        
        if (score >= 80) {
            result.setStrengthLevel("Very Strong");
        } else if (score >= 60) {
            result.setStrengthLevel("Strong");
        } else if (score >= 40) {
            result.setStrengthLevel("Medium");
        } else if (score >= 20) {
            result.setStrengthLevel("Weak");
        } else {
            result.setStrengthLevel("Very Weak");
        }
        
        return result;
    }
    
    /**
     * Generates a secure random password.
     * @param length The desired password length
     * @return A secure random password
     */
    public static String generateSecurePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    // ==================== SESSION MANAGEMENT ====================
    
    /**
     * Authenticates a user and creates a session.
     * @param username The username to authenticate
     * @param password The password to verify
     * @return SessionResult containing session information or error details
     */
    public static SessionResult authenticateUser(String username, String password) {
        if (!securityEnabled) {
            return new SessionResult(false, "Security is disabled", null);
        }
        
        if (username == null || password == null) {
            return new SessionResult(false, "Username and password are required", null);
        }
        
        // Find user in registry
        User user = DataManager.getUserByUsername(username);
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
        if (!securityEnabled || sessionId == null) {
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
        
        // Update last access time
        session.setLastActivity(LocalDateTime.now());
        
        return session.getUser();
    }
    
    /**
     * Checks if a session has admin privileges.
     * @param sessionId The session ID to check
     * @return true if the session belongs to an admin user
     */
    public static boolean isAdminSession(String sessionId) {
        User user = validateSession(sessionId);
        return user != null && "admin".equals(user.getRole());
    }
    
    /**
     * Invalidates a session.
     * @param sessionId The session ID to invalidate
     * @return true if session was invalidated successfully
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
     * Gets session statistics.
     * @return SessionStatistics object with current session information
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
                                   userSessions.size(), securityEnabled);
    }
    
    // ==================== AUTHENTICATION SERVICE ====================
    
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
        PasswordValidationResult passwordValidation = validatePasswordStrength(password);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("Password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("- ").append(error).append("\n");
            }
            return new UserCreationResult(false, errorMsg.toString(), null);
        }
        
        // Check if user already exists
        if (DataManager.getUserByUsername(username) != null) {
            return new UserCreationResult(false, "Username already exists", null);
        }
        
        // Create user
        User user = new User(username, email, role, password);
        DataManager.addUser(user);
        
        return new UserCreationResult(true, "User created successfully", user);
    }
    
    /**
     * Changes a user's password.
     * @param sessionId The session ID of the user changing the password
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return PasswordChangeResult with success status
     */
    public static PasswordChangeResult changePassword(String sessionId, String currentPassword, String newPassword) {
        if (sessionId == null || currentPassword == null || newPassword == null) {
            return new PasswordChangeResult(false, "All fields are required");
        }
        
        User user = validateSession(sessionId);
        if (user == null) {
            return new PasswordChangeResult(false, "Invalid or expired session");
        }
        
        // Verify current password
        if (!user.verifyPassword(currentPassword)) {
            return new PasswordChangeResult(false, "Current password is incorrect");
        }
        
        // Validate new password strength
        PasswordValidationResult passwordValidation = validatePasswordStrength(newPassword);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("New password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("- ").append(error).append("\n");
            }
            return new PasswordChangeResult(false, errorMsg.toString());
        }
        
        // Change password
        boolean success = user.changePassword(currentPassword, newPassword);
        if (success) {
            return new PasswordChangeResult(true, "Password changed successfully");
        } else {
            return new PasswordChangeResult(false, "Failed to change password");
        }
    }
    
    /**
     * Resets a user's password (admin only).
     * @param adminSessionId The session ID of the admin performing the reset
     * @param targetUsername The username whose password should be reset
     * @param newPassword The new password
     * @return PasswordResetResult with success status
     */
    public static PasswordResetResult resetPassword(String adminSessionId, String targetUsername, String newPassword) {
        if (adminSessionId == null || targetUsername == null || newPassword == null) {
            return new PasswordResetResult(false, "All fields are required");
        }
        
        // Verify admin session
        if (!isAdminSession(adminSessionId)) {
            return new PasswordResetResult(false, "Admin privileges required");
        }
        
        // Find target user
        User targetUser = DataManager.getUserByUsername(targetUsername);
        if (targetUser == null) {
            return new PasswordResetResult(false, "User not found");
        }
        
        // Validate new password strength
        PasswordValidationResult passwordValidation = validatePasswordStrength(newPassword);
        if (!passwordValidation.isValid()) {
            StringBuilder errorMsg = new StringBuilder("New password does not meet security requirements:\n");
            for (String error : passwordValidation.getErrors()) {
                errorMsg.append("- ").append(error).append("\n");
            }
            return new PasswordResetResult(false, errorMsg.toString());
        }
        
        // Reset password
        boolean success = targetUser.setPassword(newPassword);
        if (success) {
            // Invalidate all sessions for the target user
            invalidateAllUserSessions(targetUsername);
            return new PasswordResetResult(true, "Password reset successfully");
        } else {
            return new PasswordResetResult(false, "Failed to reset password");
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Generates a random salt for password hashing.
     * @return A random salt string
     */
    private static String generateSalt() {
        return BCrypt.gensalt(BCRYPT_ROUNDS);
    }
    
    /**
     * Generates a secure session ID.
     * @return A secure session ID string
     */
    private static String generateSessionId() {
        StringBuilder sessionId = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < SESSION_ID_LENGTH; i++) {
            sessionId.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sessionId.toString();
    }
    
    /**
     * Checks if a password is in the list of common weak passwords.
     * @param password The password to check
     * @return true if the password is common and weak
     */
    private static boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        for (String commonPassword : COMMON_PASSWORDS) {
            if (lowerPassword.equals(commonPassword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculates a password strength score.
     * @param password The password to score
     * @return A strength score from 0 to 100
     */
    private static int calculatePasswordStrengthScore(String password) {
        int score = 0;
        
        // Length score (0-40 points)
        if (password.length() >= MIN_PASSWORD_LENGTH) {
            score += Math.min(40, (password.length() - MIN_PASSWORD_LENGTH) * 2);
        }
        
        // Character variety score (0-60 points)
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 15;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 15;
        if (DIGIT_PATTERN.matcher(password).find()) score += 15;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 15;
        
        return Math.min(100, score);
    }
    
    /**
     * Checks if a session has expired.
     * @param session The session to check
     * @return true if the session has expired
     */
    private static boolean isSessionExpired(UserSession session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastAccess = session.getLastActivity();
        return ChronoUnit.MINUTES.between(lastAccess, now) > SESSION_TIMEOUT_MINUTES;
    }
    
    /**
     * Enables or disables security features.
     * @param enabled true to enable security, false to disable
     */
    public static void setSecurityEnabled(boolean enabled) {
        securityEnabled = enabled;
        if (!enabled) {
            // Clear all sessions when disabling security
            activeSessions.clear();
            userSessions.clear();
        }
    }
    
    /**
     * Checks if security features are enabled.
     * @return true if security is enabled
     */
    public static boolean isSecurityEnabled() {
        return securityEnabled;
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
}

/**
 * Simple BCrypt implementation for password hashing.
 * This is a basic implementation for educational purposes.
 * In production, use a well-tested library like jBCrypt.
 */
class BCrypt {
    private static final String SALT_PREFIX = "$2a$";
    private static final java.security.SecureRandom random = new java.security.SecureRandom();
    
    /**
     * Generates a salt for BCrypt hashing.
     * @param rounds The number of rounds (cost factor)
     * @return A BCrypt salt string
     */
    public static String gensalt(int rounds) {
        if (rounds < 4 || rounds > 31) {
            rounds = 10; // Default rounds
        }
        
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        StringBuilder saltString = new StringBuilder(SALT_PREFIX);
        saltString.append(String.format("%02d", rounds));
        saltString.append("$");
        
        // Convert salt to base64
        String base64Salt = java.util.Base64.getEncoder().encodeToString(salt);
        saltString.append(base64Salt.substring(0, 22)); // BCrypt uses 22 chars
        
        return saltString.toString();
    }
    
    /**
     * Hashes a password with a salt.
     * @param password The plain text password
     * @param salt The BCrypt salt
     * @return The hashed password
     */
    public static String hashpw(String password, String salt) {
        // This is a simplified implementation
        // In production, use a proper BCrypt library
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            String combined = password + salt;
            byte[] hash = md.digest(combined.getBytes("UTF-8"));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return salt + hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Checks a password against a hash.
     * @param password The plain text password
     * @param hash The stored hash
     * @return true if password matches hash
     */
    public static boolean checkpw(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        
        try {
            // Extract salt from hash
            String salt = hash.substring(0, 29); // BCrypt salt is 29 chars
            
            // Hash the password with the extracted salt
            String hashedPassword = hashpw(password, salt);
            
            // Compare the hashes
            return hashedPassword.equals(hash);
        } catch (Exception e) {
            return false;
        }
    }
}
