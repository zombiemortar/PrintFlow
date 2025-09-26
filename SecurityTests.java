import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified security tests for password security, session management, and input validation.
 * Consolidates SecurityFeaturesTest functionality.
 */
public class SecurityTests {
    
    private User testUser;
    private Order testOrder;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        testUser = new User("securityuser", "security@example.com", "customer", "SecurityPass123!");
        
        // Initialize test material and order
        testMaterial = new Material("PLA Plastic", 0.05, 200, "White");
        testOrder = new Order(testUser, testMaterial, "10x10x5", 1, "Please print carefully");
        
        // Enable security features
        SecurityManager.setSecurityEnabled(true);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up sessions
        SecurityManager.invalidateAllUserSessions("securityuser");
        SecurityManager.invalidateAllUserSessions("securityadmin");
    }
    
    // ==================== PASSWORD SECURITY TESTS ====================
    
    @Test
    void testPasswordHashing() {
        String password = "TestPassword123!";
        String hash = SecurityManager.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.length() > 20); // BCrypt hashes are longer
        
        // Test password verification
        assertTrue(SecurityManager.verifyPassword(password, hash));
        assertFalse(SecurityManager.verifyPassword("WrongPassword", hash));
        assertFalse(SecurityManager.verifyPassword(password, "WrongHash"));
    }
    
    @Test
    void testPasswordStrengthValidation() {
        // Test strong password
        PasswordValidationResult strongResult = SecurityManager.validatePasswordStrength("StrongPass123!");
        assertTrue(strongResult.isValid());
        assertTrue(strongResult.getStrengthScore() >= 60);
        assertEquals("Strong", strongResult.getStrengthLevel());
        
        // Test weak password
        PasswordValidationResult weakResult = SecurityManager.validatePasswordStrength("weak");
        assertFalse(weakResult.isValid());
        assertTrue(weakResult.getErrors().size() > 0);
        assertTrue(weakResult.getStrengthScore() < 40);
    }
    
    @Test
    void testPasswordRequirements() {
        // Test minimum length
        PasswordValidationResult shortResult = SecurityManager.validatePasswordStrength("Short1!");
        assertFalse(shortResult.isValid());
        assertTrue(shortResult.getErrors().stream().anyMatch(e -> e.contains("at least")));
        
        // Test missing uppercase
        PasswordValidationResult noUpperResult = SecurityManager.validatePasswordStrength("nouppercase123!");
        assertFalse(noUpperResult.isValid());
        assertTrue(noUpperResult.getErrors().stream().anyMatch(e -> e.contains("uppercase")));
        
        // Test missing lowercase
        PasswordValidationResult noLowerResult = SecurityManager.validatePasswordStrength("NOLOWERCASE123!");
        assertFalse(noLowerResult.isValid());
        assertTrue(noLowerResult.getErrors().stream().anyMatch(e -> e.contains("lowercase")));
        
        // Test missing digit
        PasswordValidationResult noDigitResult = SecurityManager.validatePasswordStrength("NoDigits!");
        assertFalse(noDigitResult.isValid());
        assertTrue(noDigitResult.getErrors().stream().anyMatch(e -> e.contains("digit")));
        
        // Test missing special character
        PasswordValidationResult noSpecialResult = SecurityManager.validatePasswordStrength("NoSpecial123");
        assertFalse(noSpecialResult.isValid());
        assertTrue(noSpecialResult.getErrors().stream().anyMatch(e -> e.contains("special")));
    }
    
    @Test
    void testCommonPasswordRejection() {
        String[] commonPasswords = {"password", "123456", "qwerty", "abc123", "admin"};
        
        for (String commonPassword : commonPasswords) {
            PasswordValidationResult result = SecurityManager.validatePasswordStrength(commonPassword);
            assertFalse(result.isValid());
            assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("common")));
        }
    }
    
    @Test
    void testSecurePasswordGeneration() {
        String generatedPassword = SecurityManager.generateSecurePassword(12);
        
        assertNotNull(generatedPassword);
        assertEquals(12, generatedPassword.length());
        
        // Generated password should be valid
        PasswordValidationResult result = SecurityManager.validatePasswordStrength(generatedPassword);
        assertTrue(result.isValid());
    }
    
    @Test
    void testUserPasswordOperations() {
        // Test password verification
        assertTrue(testUser.verifyPassword("SecurityPass123!"));
        assertFalse(testUser.verifyPassword("WrongPassword"));
        
        // Test password change
        boolean changed = testUser.changePassword("SecurityPass123!", "NewSecurityPass456!");
        assertTrue(changed);
        
        // Verify new password works
        assertTrue(testUser.verifyPassword("NewSecurityPass456!"));
        assertFalse(testUser.verifyPassword("SecurityPass123!"));
        
        // Test password setting
        boolean set = testUser.setPassword("AnotherNewPass789!");
        assertTrue(set);
        assertTrue(testUser.verifyPassword("AnotherNewPass789!"));
    }
    
    // ==================== SESSION MANAGEMENT TESTS ====================
    
    @Test
    void testUserAuthentication() {
        // Test successful authentication
        SessionResult result = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(result.isSuccess());
        assertNotNull(result.getSessionId());
        assertEquals("Authentication successful", result.getMessage());
        
        // Test failed authentication
        SessionResult failResult = SecurityManager.authenticateUser("securityuser", "WrongPassword");
        assertFalse(failResult.isSuccess());
        assertNull(failResult.getSessionId());
        assertEquals("Invalid username or password", failResult.getMessage());
    }
    
    @Test
    void testSessionValidation() {
        // Authenticate user
        SessionResult authResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        // Validate session
        User user = SecurityManager.validateSession(sessionId);
        assertNotNull(user);
        assertEquals("securityuser", user.getUsername());
        
        // Test invalid session
        User invalidUser = SecurityManager.validateSession("invalid_session_id");
        assertNull(invalidUser);
    }
    
    @Test
    void testAdminSessionPrivileges() {
        // Authenticate admin
        SessionResult adminResult = SecurityManager.authenticateUser("securityadmin", "AdminPass123!");
        assertTrue(adminResult.isSuccess());
        String adminSessionId = adminResult.getSessionId();
        
        // Check admin privileges
        assertTrue(SecurityManager.isAdminSession(adminSessionId));
        
        // Authenticate regular user
        SessionResult userResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(userResult.isSuccess());
        String userSessionId = userResult.getSessionId();
        
        // Check user privileges
        assertFalse(SecurityManager.isAdminSession(userSessionId));
    }
    
    @Test
    void testSessionInvalidation() {
        // Authenticate user
        SessionResult authResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        // Validate session
        User user = SecurityManager.validateSession(sessionId);
        assertNotNull(user);
        
        // Invalidate session
        boolean invalidated = SecurityManager.invalidateSession(sessionId);
        assertTrue(invalidated);
        
        // Try to validate invalidated session
        User invalidUser = SecurityManager.validateSession(sessionId);
        assertNull(invalidUser);
    }
    
    @Test
    void testMultipleSessionsPerUser() {
        // Create multiple sessions for same user
        SessionResult session1 = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        SessionResult session2 = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        SessionResult session3 = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        
        assertTrue(session1.isSuccess());
        assertTrue(session2.isSuccess());
        assertTrue(session3.isSuccess());
        
        // All sessions should be valid
        assertNotNull(SecurityManager.validateSession(session1.getSessionId()));
        assertNotNull(SecurityManager.validateSession(session2.getSessionId()));
        assertNotNull(SecurityManager.validateSession(session3.getSessionId()));
        
        // Test session limit (should invalidate oldest when limit exceeded)
        SessionResult session4 = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(session4.isSuccess());
        
        // First session should be invalidated
        assertNull(SecurityManager.validateSession(session1.getSessionId()));
    }
    
    @Test
    void testSessionStatistics() {
        // Authenticate users
        SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        SecurityManager.authenticateUser("securityadmin", "AdminPass123!");
        
        // Get statistics
        SessionStatistics stats = SecurityManager.getSessionStatistics();
        assertNotNull(stats);
        assertTrue(stats.getTotalSessions() >= 2);
        assertTrue(stats.getAdminSessions() >= 1);
        assertTrue(stats.getUniqueUsers() >= 2);
    }
    
    // ==================== AUTHENTICATION SERVICE TESTS ====================
    
    @Test
    void testUserCreation() {
        // Test successful user creation
        UserCreationResult result = SecurityManager.createUser("newuser", "new@example.com", "customer", "NewUserPass123!");
        assertTrue(result.isSuccess());
        assertNotNull(result.getUser());
        assertEquals("newuser", result.getUser().getUsername());
        
        // Test duplicate username
        UserCreationResult duplicateResult = SecurityManager.createUser("newuser", "duplicate@example.com", "customer", "DuplicatePass123!");
        assertFalse(duplicateResult.isSuccess());
        assertTrue(duplicateResult.getMessage().contains("already exists"));
        
        // Test weak password
        UserCreationResult weakResult = SecurityManager.createUser("weakuser", "weak@example.com", "customer", "weak");
        assertFalse(weakResult.isSuccess());
        assertTrue(weakResult.getMessage().contains("security requirements"));
    }
    
    @Test
    void testPasswordChange() {
        // Authenticate user
        SessionResult authResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        // Change password
        PasswordChangeResult changeResult = SecurityManager.changePassword(sessionId, "SecurityPass123!", "NewSecurityPass456!");
        assertTrue(changeResult.isSuccess());
        
        // Test with wrong current password
        PasswordChangeResult wrongResult = SecurityManager.changePassword(sessionId, "WrongPassword", "NewPassword123!");
        assertFalse(wrongResult.isSuccess());
        assertTrue(wrongResult.getMessage().contains("incorrect"));
        
        // Test with weak new password
        PasswordChangeResult weakResult = SecurityManager.changePassword(sessionId, "SecurityPass123!", "weak");
        assertFalse(weakResult.isSuccess());
        assertTrue(weakResult.getMessage().contains("security requirements"));
    }
    
    @Test
    void testAdminPasswordReset() {
        // Authenticate admin
        SessionResult adminResult = SecurityManager.authenticateUser("securityadmin", "AdminPass123!");
        assertTrue(adminResult.isSuccess());
        String adminSessionId = adminResult.getSessionId();
        
        // Reset user password
        PasswordResetResult resetResult = SecurityManager.resetPassword(adminSessionId, "securityuser", "ResetPassword123!");
        assertTrue(resetResult.isSuccess());
        
        // Test with non-admin session
        SessionResult userResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(userResult.isSuccess());
        String userSessionId = userResult.getSessionId();
        
        PasswordResetResult nonAdminResult = SecurityManager.resetPassword(userSessionId, "securityuser", "NewPassword123!");
        assertFalse(nonAdminResult.isSuccess());
        assertTrue(nonAdminResult.getMessage().contains("Admin privileges"));
    }
    
    // ==================== INPUT VALIDATION TESTS ====================
    
    @Test
    void testUsernameValidation() {
        // Test valid usernames
        ValidationResult validResult = InputValidator.validateUsername("validuser123");
        assertTrue(validResult.isValid());
        
        // Test invalid usernames
        ValidationResult invalidResult = InputValidator.validateUsername("user<script>alert('xss')</script>");
        assertFalse(invalidResult.isValid());
        assertTrue(invalidResult.getErrors().stream().anyMatch(e -> e.contains("script")));
    }
    
    @Test
    void testEmailValidation() {
        // Test valid emails
        ValidationResult validResult = InputValidator.validateEmail("user@example.com");
        assertTrue(validResult.isValid());
        
        // Test invalid emails
        ValidationResult invalidResult = InputValidator.validateEmail("invalid-email");
        assertFalse(invalidResult.isValid());
    }
    
    @Test
    void testMaterialNameValidation() {
        // Test valid material names
        ValidationResult validResult = InputValidator.validateMaterialName("PLA Plastic");
        assertTrue(validResult.isValid());
        
        // Test invalid material names
        ValidationResult invalidResult = InputValidator.validateMaterialName("Material<script>alert('xss')</script>");
        assertFalse(invalidResult.isValid());
    }
    
    @Test
    void testDimensionsValidation() {
        // Test valid dimensions
        ValidationResult validResult = InputValidator.validateDimensions("10x10x5");
        assertTrue(validResult.isValid());
        
        // Test invalid dimensions
        ValidationResult invalidResult = InputValidator.validateDimensions("10x10x5<script>alert('xss')</script>");
        assertFalse(invalidResult.isValid());
    }
    
    @Test
    void testInstructionsValidation() {
        // Test valid instructions
        ValidationResult validResult = InputValidator.validateInstructions("Please print carefully");
        assertTrue(validResult.isValid());
        
        // Test invalid instructions
        ValidationResult invalidResult = InputValidator.validateInstructions("Instructions<script>alert('xss')</script>");
        assertFalse(invalidResult.isValid());
    }
    
    @Test
    void testInputSanitization() {
        // Test HTML sanitization
        String sanitized = InputValidator.sanitizeInput("Hello<script>alert('xss')</script>World");
        assertFalse(sanitized.contains("<script>"));
        assertTrue(sanitized.contains("Hello"));
        assertTrue(sanitized.contains("World"));
        
        // Test HTML escaping
        String escaped = InputValidator.escapeHtml("<b>Bold</b>");
        assertTrue(escaped.contains("&lt;"));
        assertTrue(escaped.contains("&gt;"));
        
        // Test SQL escaping
        String sqlEscaped = InputValidator.escapeSql("Hello 'World'");
        assertTrue(sqlEscaped.contains("''"));
    }
    
    @Test
    void testCompleteObjectValidation() {
        // Test valid user validation
        ValidationResult userResult = InputValidator.validateUser(testUser);
        assertTrue(userResult.isValid());
        
        // Test valid order validation
        ValidationResult orderResult = InputValidator.validateOrder(testOrder);
        assertTrue(orderResult.isValid());
        
        // Test material name validation (since validateMaterial doesn't exist)
        ValidationResult materialNameResult = InputValidator.validateMaterialName(testMaterial.getName());
        assertTrue(materialNameResult.isValid());
    }
    
    // ==================== SECURITY CONFIGURATION TESTS ====================
    
    @Test
    void testSecurityEnabledDisabled() {
        // Test with security enabled
        assertTrue(SecurityManager.isSecurityEnabled());
        
        SessionResult enabledResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertTrue(enabledResult.isSuccess());
        
        // Disable security
        SecurityManager.setSecurityEnabled(false);
        assertFalse(SecurityManager.isSecurityEnabled());
        
        // Authentication should fail when security is disabled
        SessionResult disabledResult = SecurityManager.authenticateUser("securityuser", "SecurityPass123!");
        assertFalse(disabledResult.isSuccess());
        assertTrue(disabledResult.getMessage().contains("disabled"));
        
        // Re-enable security
        SecurityManager.setSecurityEnabled(true);
        assertTrue(SecurityManager.isSecurityEnabled());
    }
    
    @Test
    void testSecurityIntegration() {
        // Test complete security workflow
        UserCreationResult createResult = SecurityManager.createUser("integrationuser", "integration@example.com", "customer", "IntegrationPass123!");
        assertTrue(createResult.isSuccess());
        
        SessionResult authResult = SecurityManager.authenticateUser("integrationuser", "IntegrationPass123!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        User user = SecurityManager.validateSession(sessionId);
        assertNotNull(user);
        assertEquals("integrationuser", user.getUsername());
        
        PasswordChangeResult changeResult = SecurityManager.changePassword(sessionId, "IntegrationPass123!", "NewIntegrationPass456!");
        assertTrue(changeResult.isSuccess());
        
        boolean invalidated = SecurityManager.invalidateSession(sessionId);
        assertTrue(invalidated);
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testNullInputs() {
        // Test null password
        String nullHash = SecurityManager.hashPassword(null);
        assertNull(nullHash);
        
        // Test null password verification
        assertFalse(SecurityManager.verifyPassword(null, "hash"));
        assertFalse(SecurityManager.verifyPassword("password", null));
        
        // Test null session validation
        assertNull(SecurityManager.validateSession(null));
        
        // Test null authentication
        SessionResult nullResult = SecurityManager.authenticateUser(null, "password");
        assertFalse(nullResult.isSuccess());
        
        SessionResult nullResult2 = SecurityManager.authenticateUser("user", null);
        assertFalse(nullResult2.isSuccess());
    }
    
    @Test
    void testEmptyInputs() {
        // Test empty password
        String emptyHash = SecurityManager.hashPassword("");
        assertNull(emptyHash);
        
        // Test empty username
        SessionResult emptyResult = SecurityManager.authenticateUser("", "password");
        assertFalse(emptyResult.isSuccess());
        
        // Test empty session ID
        assertNull(SecurityManager.validateSession(""));
    }
    
    @Test
    void testSpecialCharacters() {
        // Test password with special characters
        String specialPassword = "Special!@#$%^&*()_+-=[]{}|;:,.<>?Pass123";
        String hash = SecurityManager.hashPassword(specialPassword);
        assertNotNull(hash);
        assertTrue(SecurityManager.verifyPassword(specialPassword, hash));
        
        // Test username with special characters
        ValidationResult specialResult = InputValidator.validateUsername("user@#$%");
        assertTrue(specialResult.isValid());
    }
}
