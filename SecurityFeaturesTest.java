/**
 * Comprehensive test suite for Phase 5 security features.
 * Tests password hashing, session management, and enhanced input validation.
 */
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityFeaturesTest {
    
    private User testUser;
    private AdminUser testAdmin;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Clear all sessions before each test
        SessionManager.clearAllSessions();
        
        // Create test user
        testUser = new User("testuser", "test@example.com", "customer", "SecurePass123!");
        
        // Create test admin
        testAdmin = new AdminUser("testadmin", "admin@example.com", "AdminPass456!");
        
        // Create test material
        testMaterial = new Material("PLA", 0.05, 200, "Blue");
        
        // Ensure session management is enabled
        SessionManager.setSessionManagementEnabled(true);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        SessionManager.clearAllSessions();
    }
    
    // Password Security Tests
    
    @Test
    @DisplayName("Test admin user creation and authentication")
    void testAdminUserCreationAndAuthentication() {
        // Test admin user creation
        assertNotNull(testAdmin, "Test admin should be created");
        assertEquals("admin", testAdmin.getRole(), "Admin should have admin role");
        assertTrue(testAdmin.hasPassword(), "Admin should have password");
        
        // Test admin authentication
        SessionResult authResult = SessionManager.authenticateUser("testadmin", "AdminPass456!");
        assertTrue(authResult.isSuccess(), "Admin authentication should succeed");
        assertTrue(SessionManager.isAdminSession(authResult.getSessionId()), "Admin session should have admin privileges");
    }
    
    @Test
    @DisplayName("Test password hashing functionality")
    void testPasswordHashing() {
        String password = "TestPassword123!";
        String hash = PasswordSecurity.hashPassword(password);
        
        assertNotNull(hash, "Password hash should not be null");
        assertNotEquals(password, hash, "Hash should be different from original password");
        assertTrue(PasswordSecurity.isValidHashFormat(hash), "Hash should be in valid format");
        
        // Test verification
        assertTrue(PasswordSecurity.verifyPassword(password, hash), "Password verification should succeed");
        assertFalse(PasswordSecurity.verifyPassword("WrongPassword", hash), "Wrong password should fail verification");
    }
    
    @Test
    @DisplayName("Test password strength validation")
    void testPasswordStrengthValidation() {
        // Test weak password
        PasswordValidationResult weakResult = PasswordSecurity.validatePasswordStrength("123");
        assertFalse(weakResult.isValid(), "Weak password should be invalid");
        assertTrue(weakResult.getErrors().contains("Password must be at least 8 characters long"));
        
        // Test strong password
        PasswordValidationResult strongResult = PasswordSecurity.validatePasswordStrength("StrongPass123!");
        assertTrue(strongResult.isValid(), "Strong password should be valid");
        assertEquals("Strong", strongResult.getStrengthLevel());
        assertTrue(strongResult.getStrengthScore() >= 7);
        
        // Test common password
        PasswordValidationResult commonResult = PasswordSecurity.validatePasswordStrength("password");
        assertFalse(commonResult.isValid(), "Common password should be invalid");
        assertTrue(commonResult.getErrors().contains("Password is too common and easily guessable"));
    }
    
    @Test
    @DisplayName("Test secure password generation")
    void testSecurePasswordGeneration() {
        String generatedPassword = PasswordSecurity.generateSecurePassword(12);
        
        assertNotNull(generatedPassword, "Generated password should not be null");
        assertEquals(12, generatedPassword.length(), "Generated password should have correct length");
        
        PasswordValidationResult validation = PasswordSecurity.validatePasswordStrength(generatedPassword);
        assertTrue(validation.isValid(), "Generated password should be valid");
    }
    
    @Test
    @DisplayName("Test user password functionality")
    void testUserPasswordFunctionality() {
        // Test password verification
        assertTrue(testUser.verifyPassword("SecurePass123!"), "User should verify correct password");
        assertFalse(testUser.verifyPassword("WrongPassword"), "User should reject wrong password");
        
        // Test password change
        boolean changed = testUser.changePassword("SecurePass123!", "NewSecurePass456!");
        assertTrue(changed, "Password change should succeed");
        assertTrue(testUser.verifyPassword("NewSecurePass456!"), "New password should work");
        assertFalse(testUser.verifyPassword("SecurePass123!"), "Old password should not work");
        
        // Test password setting
        boolean set = testUser.setPassword("AnotherSecurePass789!");
        assertTrue(set, "Password setting should succeed");
        assertTrue(testUser.verifyPassword("AnotherSecurePass789!"), "Set password should work");
    }
    
    // Session Management Tests
    
    @Test
    @DisplayName("Test user authentication")
    void testUserAuthentication() {
        // Test successful authentication
        SessionResult result = SessionManager.authenticateUser("testuser", "SecurePass123!");
        assertTrue(result.isSuccess(), "Authentication should succeed");
        assertNotNull(result.getSessionId(), "Session ID should be provided");
        
        // Test failed authentication
        SessionResult failedResult = SessionManager.authenticateUser("testuser", "WrongPassword");
        assertFalse(failedResult.isSuccess(), "Authentication should fail with wrong password");
        assertNull(failedResult.getSessionId(), "No session ID should be provided");
        
        // Test non-existent user
        SessionResult noUserResult = SessionManager.authenticateUser("nonexistent", "password");
        assertFalse(noUserResult.isSuccess(), "Authentication should fail for non-existent user");
    }
    
    @Test
    @DisplayName("Test session validation")
    void testSessionValidation() {
        // Authenticate user
        SessionResult authResult = SessionManager.authenticateUser("testuser", "SecurePass123!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        // Validate session
        User user = SessionManager.validateSession(sessionId);
        assertNotNull(user, "Session should be valid");
        assertEquals("testuser", user.getUsername());
        
        // Test invalid session
        User invalidUser = SessionManager.validateSession("invalid_session_id");
        assertNull(invalidUser, "Invalid session should return null");
    }
    
    @Test
    @DisplayName("Test admin session privileges")
    void testAdminSessionPrivileges() {
        // Authenticate admin
        SessionResult authResult = SessionManager.authenticateUser("testadmin", "AdminPass456!");
        assertTrue(authResult.isSuccess());
        String sessionId = authResult.getSessionId();
        
        // Check admin privileges
        assertTrue(SessionManager.isAdminSession(sessionId), "Admin session should have admin privileges");
        
        // Authenticate regular user
        SessionResult userAuthResult = SessionManager.authenticateUser("testuser", "SecurePass123!");
        String userSessionId = userAuthResult.getSessionId();
        
        // Check regular user privileges
        assertFalse(SessionManager.isAdminSession(userSessionId), "Regular user session should not have admin privileges");
    }
    
    @Test
    @DisplayName("Test session invalidation")
    void testSessionInvalidation() {
        // Authenticate user
        SessionResult authResult = SessionManager.authenticateUser("testuser", "SecurePass123!");
        String sessionId = authResult.getSessionId();
        
        // Validate session exists
        assertNotNull(SessionManager.validateSession(sessionId));
        
        // Invalidate session
        boolean invalidated = SessionManager.invalidateSession(sessionId);
        assertTrue(invalidated, "Session invalidation should succeed");
        
        // Validate session is gone
        assertNull(SessionManager.validateSession(sessionId), "Invalidated session should be null");
    }
    
    @Test
    @DisplayName("Test session statistics")
    void testSessionStatistics() {
        // Authenticate multiple users
        SessionManager.authenticateUser("testuser", "SecurePass123!");
        SessionManager.authenticateUser("testadmin", "AdminPass456!");
        
        SessionStatistics stats = SessionManager.getSessionStatistics();
        assertEquals(2, stats.getTotalSessions(), "Should have 2 active sessions");
        assertEquals(2, stats.getUniqueUsers(), "Should have 2 unique users");
        assertEquals(1, stats.getAdminSessions(), "Should have 1 admin session");
        assertTrue(stats.isSessionManagementEnabled(), "Session management should be enabled");
    }
    
    // Input Validation Tests
    
    @Test
    @DisplayName("Test username validation")
    void testUsernameValidation() {
        // Valid username
        ValidationResult validResult = InputValidator.validateUsername("validuser123");
        assertTrue(validResult.isValid(), "Valid username should pass validation");
        
        // Invalid usernames
        ValidationResult emptyResult = InputValidator.validateUsername("");
        assertFalse(emptyResult.isValid(), "Empty username should fail validation");
        
        ValidationResult shortResult = InputValidator.validateUsername("ab");
        assertFalse(shortResult.isValid(), "Short username should fail validation");
        
        ValidationResult specialResult = InputValidator.validateUsername("user@name");
        assertFalse(specialResult.isValid(), "Username with special chars should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateUsername("user'; DROP TABLE users; --");
        assertFalse(injectionResult.isValid(), "Username with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test email validation")
    void testEmailValidation() {
        // Valid email
        ValidationResult validResult = InputValidator.validateEmail("user@example.com");
        assertTrue(validResult.isValid(), "Valid email should pass validation");
        
        // Invalid emails
        ValidationResult invalidResult = InputValidator.validateEmail("invalid-email");
        assertFalse(invalidResult.isValid(), "Invalid email should fail validation");
        
        ValidationResult emptyResult = InputValidator.validateEmail("");
        assertFalse(emptyResult.isValid(), "Empty email should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateEmail("user@example.com'; DROP TABLE users; --");
        assertFalse(injectionResult.isValid(), "Email with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test password validation")
    void testPasswordValidation() {
        // Valid password
        ValidationResult validResult = InputValidator.validatePassword("SecurePass123!");
        assertTrue(validResult.isValid(), "Valid password should pass validation");
        
        // Invalid passwords
        ValidationResult shortResult = InputValidator.validatePassword("123");
        assertFalse(shortResult.isValid(), "Short password should fail validation");
        
        ValidationResult injectionResult = InputValidator.validatePassword("pass'; DROP TABLE users; --");
        assertFalse(injectionResult.isValid(), "Password with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test material name validation")
    void testMaterialNameValidation() {
        // Valid material name
        ValidationResult validResult = InputValidator.validateMaterialName("PLA Plastic");
        assertTrue(validResult.isValid(), "Valid material name should pass validation");
        
        // Invalid material names
        ValidationResult emptyResult = InputValidator.validateMaterialName("");
        assertFalse(emptyResult.isValid(), "Empty material name should fail validation");
        
        ValidationResult specialResult = InputValidator.validateMaterialName("PLA<script>");
        assertFalse(specialResult.isValid(), "Material name with special chars should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateMaterialName("PLA'; DROP TABLE materials; --");
        assertFalse(injectionResult.isValid(), "Material name with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test dimensions validation")
    void testDimensionsValidation() {
        // Valid dimensions
        ValidationResult validResult = InputValidator.validateDimensions("10x10x5");
        assertTrue(validResult.isValid(), "Valid dimensions should pass validation");
        
        // Invalid dimensions
        ValidationResult emptyResult = InputValidator.validateDimensions("");
        assertFalse(emptyResult.isValid(), "Empty dimensions should fail validation");
        
        ValidationResult specialResult = InputValidator.validateDimensions("10x10x5<script>");
        assertFalse(specialResult.isValid(), "Dimensions with special chars should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateDimensions("10x10x5'; DROP TABLE orders; --");
        assertFalse(injectionResult.isValid(), "Dimensions with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test instructions validation")
    void testInstructionsValidation() {
        // Valid instructions
        ValidationResult validResult = InputValidator.validateInstructions("Please print carefully");
        assertTrue(validResult.isValid(), "Valid instructions should pass validation");
        
        // Invalid instructions
        ValidationResult xssResult = InputValidator.validateInstructions("<script>alert('xss')</script>");
        assertFalse(xssResult.isValid(), "Instructions with XSS should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateInstructions("Print this'; DROP TABLE orders; --");
        assertFalse(injectionResult.isValid(), "Instructions with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test quantity validation")
    void testQuantityValidation() {
        // Valid quantity
        ValidationResult validResult = InputValidator.validateQuantity(5);
        assertTrue(validResult.isValid(), "Valid quantity should pass validation");
        
        // Invalid quantities
        ValidationResult zeroResult = InputValidator.validateQuantity(0);
        assertFalse(zeroResult.isValid(), "Zero quantity should fail validation");
        
        ValidationResult negativeResult = InputValidator.validateQuantity(-1);
        assertFalse(negativeResult.isValid(), "Negative quantity should fail validation");
        
        ValidationResult largeResult = InputValidator.validateQuantity(1001);
        assertFalse(largeResult.isValid(), "Too large quantity should fail validation");
    }
    
    @Test
    @DisplayName("Test numeric validation")
    void testNumericValidation() {
        // Valid numeric value
        ValidationResult validResult = InputValidator.validateNumeric(10.5, 0.0, 100.0, "Test Value");
        assertTrue(validResult.isValid(), "Valid numeric value should pass validation");
        
        // Invalid numeric values
        ValidationResult belowMinResult = InputValidator.validateNumeric(-1.0, 0.0, 100.0, "Test Value");
        assertFalse(belowMinResult.isValid(), "Value below minimum should fail validation");
        
        ValidationResult aboveMaxResult = InputValidator.validateNumeric(101.0, 0.0, 100.0, "Test Value");
        assertFalse(aboveMaxResult.isValid(), "Value above maximum should fail validation");
        
        ValidationResult nanResult = InputValidator.validateNumeric(Double.NaN, 0.0, 100.0, "Test Value");
        assertFalse(nanResult.isValid(), "NaN value should fail validation");
    }
    
    @Test
    @DisplayName("Test input sanitization")
    void testInputSanitization() {
        String dangerousInput = "Hello<script>alert('xss')</script>World";
        String sanitized = InputValidator.sanitizeInput(dangerousInput);
        assertEquals("HelloalertxssWorld", sanitized, "Dangerous characters should be removed");
        
        String htmlInput = "Hello <b>World</b>";
        String htmlEscaped = InputValidator.escapeHtml(htmlInput);
        assertEquals("Hello &lt;b&gt;World&lt;/b&gt;", htmlEscaped, "HTML should be escaped");
        
        String sqlInput = "Hello 'World'";
        String sqlEscaped = InputValidator.escapeSql(sqlInput);
        assertEquals("Hello ''World''", sqlEscaped, "SQL should be escaped");
    }
    
    @Test
    @DisplayName("Test file path validation")
    void testFilePathValidation() {
        // Valid file path
        ValidationResult validResult = InputValidator.validateFilePath("data/users.txt");
        assertTrue(validResult.isValid(), "Valid file path should pass validation");
        
        // Invalid file paths
        ValidationResult traversalResult = InputValidator.validateFilePath("../../../etc/passwd");
        assertFalse(traversalResult.isValid(), "Path traversal should fail validation");
        
        ValidationResult injectionResult = InputValidator.validateFilePath("data/users.txt'; DROP TABLE users; --");
        assertFalse(injectionResult.isValid(), "File path with injection should fail validation");
    }
    
    @Test
    @DisplayName("Test complete user validation")
    void testCompleteUserValidation() {
        // Valid user
        User validUser = new User("validuser", "user@example.com", "customer", "SecurePass123!");
        ValidationResult validResult = InputValidator.validateUser(validUser);
        assertTrue(validResult.isValid(), "Valid user should pass validation");
        
        // Invalid user
        User invalidUser = new User("", "invalid-email", "invalid-role", "123");
        ValidationResult invalidResult = InputValidator.validateUser(invalidUser);
        assertFalse(invalidResult.isValid(), "Invalid user should fail validation");
        assertTrue(invalidResult.getErrors().size() > 0, "Should have validation errors");
    }
    
    @Test
    @DisplayName("Test complete order validation")
    void testCompleteOrderValidation() {
        // Valid order
        Order validOrder = new Order(testUser, testMaterial, "10x10x5", 1, "Please print carefully");
        ValidationResult validResult = InputValidator.validateOrder(validOrder);
        assertTrue(validResult.isValid(), "Valid order should pass validation");
        
        // Invalid order
        User invalidUser = new User("", "invalid-email", "invalid-role", "123");
        Order invalidOrder = new Order(invalidUser, testMaterial, "", 0, "<script>alert('xss')</script>");
        ValidationResult invalidResult = InputValidator.validateOrder(invalidOrder);
        assertFalse(invalidResult.isValid(), "Invalid order should fail validation");
        assertTrue(invalidResult.getErrors().size() > 0, "Should have validation errors");
    }
    
    // Authentication Service Tests
    
    @Test
    @DisplayName("Test authentication service")
    void testAuthenticationService() {
        // Test user creation
        UserCreationResult createResult = AuthenticationService.createUser("newuser", "new@example.com", "customer", "NewPass123!");
        assertTrue(createResult.isSuccess(), "User creation should succeed");
        assertNotNull(createResult.getUser(), "Created user should not be null");
        
        // Test authentication
        AuthenticationResult authResult = AuthenticationService.authenticate("newuser", "NewPass123!");
        assertTrue(authResult.isSuccess(), "Authentication should succeed");
        assertNotNull(authResult.getSessionId(), "Session ID should be provided");
        assertNotNull(authResult.getUser(), "User should be provided");
        
        // Test session validation
        AuthenticationResult sessionResult = AuthenticationService.validateSession(authResult.getSessionId());
        assertTrue(sessionResult.isSuccess(), "Session validation should succeed");
        
        // Test logout
        boolean logoutResult = AuthenticationService.logout(authResult.getSessionId());
        assertTrue(logoutResult, "Logout should succeed");
        
        // Test session validation after logout
        AuthenticationResult invalidSessionResult = AuthenticationService.validateSession(authResult.getSessionId());
        assertFalse(invalidSessionResult.isSuccess(), "Session should be invalid after logout");
    }
    
    @Test
    @DisplayName("Test password change functionality")
    void testPasswordChangeFunctionality() {
        // Create and authenticate user
        AuthenticationService.createUser("changepassuser", "change@example.com", "customer", "OldPass123!");
        AuthenticationResult authResult = AuthenticationService.authenticate("changepassuser", "OldPass123!");
        assertTrue(authResult.isSuccess());
        
        // Change password
        PasswordChangeResult changeResult = AuthenticationService.changePassword(
            authResult.getSessionId(), "OldPass123!", "NewPass456!");
        assertTrue(changeResult.isSuccess(), "Password change should succeed");
        
        // Test old password no longer works
        AuthenticationResult oldPassResult = AuthenticationService.authenticate("changepassuser", "OldPass123!");
        assertFalse(oldPassResult.isSuccess(), "Old password should not work");
        
        // Test new password works
        AuthenticationResult newPassResult = AuthenticationService.authenticate("changepassuser", "NewPass456!");
        assertTrue(newPassResult.isSuccess(), "New password should work");
    }
    
    @Test
    @DisplayName("Test admin password reset")
    void testAdminPasswordReset() {
        // Create regular user
        AuthenticationService.createUser("resetuser", "reset@example.com", "customer", "OriginalPass123!");
        
        // Authenticate admin
        AuthenticationResult adminAuthResult = AuthenticationService.authenticate("testadmin", "AdminPass456!");
        assertTrue(adminAuthResult.isSuccess());
        
        // Reset user password
        PasswordResetResult resetResult = AuthenticationService.resetPassword(
            adminAuthResult.getSessionId(), "resetuser", "ResetPass789!");
        assertTrue(resetResult.isSuccess(), "Password reset should succeed");
        
        // Test new password works
        AuthenticationResult newPassResult = AuthenticationService.authenticate("resetuser", "ResetPass789!");
        assertTrue(newPassResult.isSuccess(), "Reset password should work");
        
        // Test old password no longer works
        AuthenticationResult oldPassResult = AuthenticationService.authenticate("resetuser", "OriginalPass123!");
        assertFalse(oldPassResult.isSuccess(), "Original password should not work");
    }
    
    @Test
    @DisplayName("Test authentication statistics")
    void testAuthenticationStatistics() {
        // Create some users
        AuthenticationService.createUser("statsuser1", "stats1@example.com", "customer", "StatsPass123!");
        AuthenticationService.createUser("statsuser2", "stats2@example.com", "customer", "StatsPass456!");
        
        // Authenticate users
        AuthenticationService.authenticate("statsuser1", "StatsPass123!");
        AuthenticationService.authenticate("statsuser2", "StatsPass456!");
        AuthenticationService.authenticate("testadmin", "AdminPass456!");
        
        AuthenticationStatistics stats = AuthenticationService.getAuthenticationStatistics();
        assertTrue(stats.getTotalUsers() >= 3, "Should have at least 3 users");
        assertTrue(stats.getUsersWithPasswords() >= 3, "Should have at least 3 users with passwords");
        assertTrue(stats.getAdminUsers() >= 1, "Should have at least 1 admin user");
        assertTrue(stats.getActiveSessions() >= 3, "Should have at least 3 active sessions");
        assertTrue(stats.getLoggedInUsers() >= 3, "Should have at least 3 logged-in users");
        assertTrue(stats.isAuthenticationEnabled(), "Authentication should be enabled");
    }
    
    @Test
    @DisplayName("Test security integration with existing classes")
    void testSecurityIntegrationWithExistingClasses() {
        // Test that User class uses enhanced validation
        User user = new User("integrationuser", "integration@example.com", "customer", "IntegrationPass123!");
        assertTrue(user.hasPassword(), "User should have password");
        assertTrue(user.verifyPassword("IntegrationPass123!"), "User should verify password");
        
        // Test that AdminUser class uses enhanced validation
        AdminUser admin = new AdminUser("integrationadmin", "integrationadmin@example.com", "IntegrationAdminPass123!");
        assertTrue(admin.hasPassword(), "Admin should have password");
        assertTrue(admin.verifyPassword("IntegrationAdminPass123!"), "Admin should verify password");
        
        // Test that order submission uses enhanced validation
        Order order = user.submitOrder(testMaterial, "10x10x5", 1, "Integration test order");
        assertNotNull(order, "Order should be created successfully");
        
        // Test that material addition uses enhanced validation
        Material material = admin.addMaterial("Integration Material", 0.1, 250, "Red");
        assertNotNull(material, "Material should be created successfully");
    }
    
    @Test
    @DisplayName("Test security features disabled state")
    void testSecurityFeaturesDisabledState() {
        // Disable authentication
        AuthenticationService.setAuthenticationEnabled(false);
        assertFalse(AuthenticationService.isAuthenticationEnabled(), "Authentication should be disabled");
        
        // Test that authentication fails when disabled
        AuthenticationResult authResult = AuthenticationService.authenticate("testuser", "SecurePass123!");
        assertFalse(authResult.isSuccess(), "Authentication should fail when disabled");
        
        // Re-enable authentication
        AuthenticationService.setAuthenticationEnabled(true);
        assertTrue(AuthenticationService.isAuthenticationEnabled(), "Authentication should be enabled");
    }
}
