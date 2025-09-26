/**
 * Simple test runner for Phase 5 security features.
 * Tests password hashing, session management, and enhanced input validation without JUnit dependencies.
 */
public class SecurityTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== Phase 5 Security Features Test Runner ===\n");
        
        int testsPassed = 0;
        int testsFailed = 0;
        
        // Test password hashing
        System.out.println("Testing Password Security...");
        try {
            testPasswordHashing();
            System.out.println("✓ Password hashing tests passed");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Password hashing tests failed: " + e.getMessage());
            testsFailed++;
        }
        
        // Test session management
        System.out.println("\nTesting Session Management...");
        try {
            testSessionManagement();
            System.out.println("✓ Session management tests passed");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Session management tests failed: " + e.getMessage());
            testsFailed++;
        }
        
        // Test input validation
        System.out.println("\nTesting Input Validation...");
        try {
            testInputValidation();
            System.out.println("✓ Input validation tests passed");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Input validation tests failed: " + e.getMessage());
            testsFailed++;
        }
        
        // Test authentication service
        System.out.println("\nTesting Authentication Service...");
        try {
            testAuthenticationService();
            System.out.println("✓ Authentication service tests passed");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Authentication service tests failed: " + e.getMessage());
            testsFailed++;
        }
        
        // Test integration
        System.out.println("\nTesting Integration...");
        try {
            testIntegration();
            System.out.println("✓ Integration tests passed");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("✗ Integration tests failed: " + e.getMessage());
            testsFailed++;
        }
        
        // Summary
        System.out.println("\n=== Test Summary ===");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\n🎉 All Phase 5 security features are working correctly!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please check the implementation.");
        }
    }
    
    private static void testPasswordHashing() throws Exception {
        // Test password hashing
        String password = "TestPassword123!";
        String hash = PasswordSecurity.hashPassword(password);
        
        if (hash == null) {
            throw new Exception("Password hash should not be null");
        }
        
        if (password.equals(hash)) {
            throw new Exception("Hash should be different from original password");
        }
        
        if (!PasswordSecurity.isValidHashFormat(hash)) {
            throw new Exception("Hash should be in valid format");
        }
        
        // Test verification
        if (!PasswordSecurity.verifyPassword(password, hash)) {
            throw new Exception("Password verification should succeed");
        }
        
        if (PasswordSecurity.verifyPassword("WrongPassword", hash)) {
            throw new Exception("Wrong password should fail verification");
        }
        
        // Test password strength validation
        PasswordValidationResult weakResult = PasswordSecurity.validatePasswordStrength("123");
        if (weakResult.isValid()) {
            throw new Exception("Weak password should be invalid");
        }
        
        PasswordValidationResult strongResult = PasswordSecurity.validatePasswordStrength("StrongPass123!");
        if (!strongResult.isValid()) {
            throw new Exception("Strong password should be valid");
        }
        
        // Test secure password generation
        String generatedPassword = PasswordSecurity.generateSecurePassword(12);
        if (generatedPassword == null || generatedPassword.length() != 12) {
            throw new Exception("Generated password should have correct length");
        }
        
        System.out.println("  - Password hashing: ✓");
        System.out.println("  - Password verification: ✓");
        System.out.println("  - Password strength validation: ✓");
        System.out.println("  - Secure password generation: ✓");
    }
    
    private static void testSessionManagement() throws Exception {
        // Clear any existing sessions
        SessionManager.clearAllSessions();
        
        // Test session management disabled state
        SessionManager.setSessionManagementEnabled(false);
        if (SessionManager.isSessionManagementEnabled()) {
            throw new Exception("Session management should be disabled");
        }
        
        // Re-enable session management
        SessionManager.setSessionManagementEnabled(true);
        if (!SessionManager.isSessionManagementEnabled()) {
            throw new Exception("Session management should be enabled");
        }
        
        // Test session statistics
        SessionStatistics stats = SessionManager.getSessionStatistics();
        if (stats.getTotalSessions() != 0) {
            throw new Exception("Should have no active sessions initially");
        }
        
        System.out.println("  - Session management enable/disable: ✓");
        System.out.println("  - Session statistics: ✓");
        System.out.println("  - Session cleanup: ✓");
    }
    
    private static void testInputValidation() throws Exception {
        // Test username validation
        ValidationResult validUsername = InputValidator.validateUsername("validuser123");
        if (!validUsername.isValid()) {
            throw new Exception("Valid username should pass validation");
        }
        
        ValidationResult invalidUsername = InputValidator.validateUsername("");
        if (invalidUsername.isValid()) {
            throw new Exception("Empty username should fail validation");
        }
        
        ValidationResult injectionUsername = InputValidator.validateUsername("user'; DROP TABLE users; --");
        if (injectionUsername.isValid()) {
            throw new Exception("Username with injection should fail validation");
        }
        
        // Test email validation
        ValidationResult validEmail = InputValidator.validateEmail("user@example.com");
        if (!validEmail.isValid()) {
            throw new Exception("Valid email should pass validation");
        }
        
        ValidationResult invalidEmail = InputValidator.validateEmail("invalid-email");
        if (invalidEmail.isValid()) {
            throw new Exception("Invalid email should fail validation");
        }
        
        // Test password validation
        ValidationResult validPassword = InputValidator.validatePassword("SecurePass123!");
        if (!validPassword.isValid()) {
            throw new Exception("Valid password should pass validation");
        }
        
        ValidationResult shortPassword = InputValidator.validatePassword("123");
        if (shortPassword.isValid()) {
            throw new Exception("Short password should fail validation");
        }
        
        // Test material name validation
        ValidationResult validMaterial = InputValidator.validateMaterialName("PLA Plastic");
        if (!validMaterial.isValid()) {
            throw new Exception("Valid material name should pass validation");
        }
        
        ValidationResult injectionMaterial = InputValidator.validateMaterialName("PLA'; DROP TABLE materials; --");
        if (injectionMaterial.isValid()) {
            throw new Exception("Material name with injection should fail validation");
        }
        
        // Test dimensions validation
        ValidationResult validDimensions = InputValidator.validateDimensions("10x10x5");
        if (!validDimensions.isValid()) {
            throw new Exception("Valid dimensions should pass validation");
        }
        
        // Test quantity validation
        ValidationResult validQuantity = InputValidator.validateQuantity(5);
        if (!validQuantity.isValid()) {
            throw new Exception("Valid quantity should pass validation");
        }
        
        ValidationResult invalidQuantity = InputValidator.validateQuantity(0);
        if (invalidQuantity.isValid()) {
            throw new Exception("Zero quantity should fail validation");
        }
        
        // Test numeric validation
        ValidationResult validNumeric = InputValidator.validateNumeric(10.5, 0.0, 100.0, "Test Value");
        if (!validNumeric.isValid()) {
            throw new Exception("Valid numeric value should pass validation");
        }
        
        // Test input sanitization
        String dangerousInput = "Hello<script>alert('xss')</script>World";
        String sanitized = InputValidator.sanitizeInput(dangerousInput);
        if (sanitized.contains("<") || sanitized.contains(">")) {
            throw new Exception("Dangerous characters should be removed");
        }
        
        // Test HTML escaping
        String htmlInput = "Hello <b>World</b>";
        String htmlEscaped = InputValidator.escapeHtml(htmlInput);
        if (!htmlEscaped.contains("&lt;") || !htmlEscaped.contains("&gt;")) {
            throw new Exception("HTML should be escaped");
        }
        
        // Test SQL escaping
        String sqlInput = "Hello 'World'";
        String sqlEscaped = InputValidator.escapeSql(sqlInput);
        if (!sqlEscaped.contains("''")) {
            throw new Exception("SQL should be escaped");
        }
        
        // Test file path validation
        ValidationResult validPath = InputValidator.validateFilePath("data/users.txt");
        if (!validPath.isValid()) {
            throw new Exception("Valid file path should pass validation");
        }
        
        ValidationResult traversalPath = InputValidator.validateFilePath("../../../etc/passwd");
        if (traversalPath.isValid()) {
            throw new Exception("Path traversal should fail validation");
        }
        
        System.out.println("  - Username validation: ✓");
        System.out.println("  - Email validation: ✓");
        System.out.println("  - Password validation: ✓");
        System.out.println("  - Material name validation: ✓");
        System.out.println("  - Dimensions validation: ✓");
        System.out.println("  - Quantity validation: ✓");
        System.out.println("  - Numeric validation: ✓");
        System.out.println("  - Input sanitization: ✓");
        System.out.println("  - HTML/SQL escaping: ✓");
        System.out.println("  - File path validation: ✓");
    }
    
    private static void testAuthenticationService() throws Exception {
        // Test authentication disabled state
        AuthenticationService.setAuthenticationEnabled(false);
        if (AuthenticationService.isAuthenticationEnabled()) {
            throw new Exception("Authentication should be disabled");
        }
        
        // Re-enable authentication
        AuthenticationService.setAuthenticationEnabled(true);
        if (!AuthenticationService.isAuthenticationEnabled()) {
            throw new Exception("Authentication should be enabled");
        }
        
        // Test password strength validation
        PasswordValidationResult passwordValidation = AuthenticationService.validatePasswordStrength("TestPass123!");
        if (!passwordValidation.isValid()) {
            throw new Exception("Password strength validation should work");
        }
        
        // Test secure password generation
        String generatedPassword = AuthenticationService.generateSecurePassword(12);
        if (generatedPassword == null || generatedPassword.length() != 12) {
            throw new Exception("Secure password generation should work");
        }
        
        // Test authentication statistics
        AuthenticationStatistics stats = AuthenticationService.getAuthenticationStatistics();
        if (stats.getTotalUsers() < 0) {
            throw new Exception("Authentication statistics should be valid");
        }
        
        System.out.println("  - Authentication enable/disable: ✓");
        System.out.println("  - Password strength validation: ✓");
        System.out.println("  - Secure password generation: ✓");
        System.out.println("  - Authentication statistics: ✓");
    }
    
    private static void testIntegration() throws Exception {
        // Test User class integration
        User user = new User("integrationuser", "integration@example.com", "customer", "IntegrationPass123!");
        if (!user.hasPassword()) {
            throw new Exception("User should have password");
        }
        
        if (!user.verifyPassword("IntegrationPass123!")) {
            throw new Exception("User should verify password");
        }
        
        // Test AdminUser class integration
        AdminUser admin = new AdminUser("integrationadmin", "integrationadmin@example.com", "IntegrationAdminPass123!");
        if (!admin.hasPassword()) {
            throw new Exception("Admin should have password");
        }
        
        if (!admin.verifyPassword("IntegrationAdminPass123!")) {
            throw new Exception("Admin should verify password");
        }
        
        // Test password change
        boolean changed = user.changePassword("IntegrationPass123!", "NewIntegrationPass456!");
        if (!changed) {
            throw new Exception("Password change should succeed");
        }
        
        if (!user.verifyPassword("NewIntegrationPass456!")) {
            throw new Exception("New password should work");
        }
        
        if (user.verifyPassword("IntegrationPass123!")) {
            throw new Exception("Old password should not work");
        }
        
        // Test password setting
        boolean set = user.setPassword("AnotherIntegrationPass789!");
        if (!set) {
            throw new Exception("Password setting should succeed");
        }
        
        if (!user.verifyPassword("AnotherIntegrationPass789!")) {
            throw new Exception("Set password should work");
        }
        
        System.out.println("  - User class integration: ✓");
        System.out.println("  - AdminUser class integration: ✓");
        System.out.println("  - Password change functionality: ✓");
        System.out.println("  - Password setting functionality: ✓");
    }
}
