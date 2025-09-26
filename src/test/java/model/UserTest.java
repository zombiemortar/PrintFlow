package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the User class.
 * Tests all methods, constructors, and edge cases.
 */
public class UserTest {
    
    private User testUser;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        util.SystemConfig.resetToDefaults();
        
        // Create test user
        testUser = new User("testuser", "test@example.com", "customer", "password123");
        
        // Create test material
        testMaterial = new Material("PLA", 0.02, 200, "White");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testUser = null;
        testMaterial = null;
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    void testUserConstructorWithAllParameters() {
        User user = new User("john", "john@test.com", "customer", "password123");
        
        assertEquals("john", user.getUsername());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("customer", user.getRole());
        assertNotNull(user.getPasswordHash());
        assertTrue(user.getPasswordHash().length() > 0);
    }
    
    @Test
    void testUserDefaultConstructor() {
        User user = new User();
        
        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("customer", user.getRole());
        assertNull(user.getPasswordHash());
    }
    
    @Test
    void testUserConstructorWithNullValues() {
        User user = new User(null, null, null, null);
        
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getRole());
        assertNull(user.getPasswordHash());
    }
    
    // ==================== SETTER TESTS ====================
    
    @Test
    void testSetUsername() {
        testUser.setUsername("newuser");
        assertEquals("newuser", testUser.getUsername());
    }
    
    @Test
    void testSetEmail() {
        testUser.setEmail("new@email.com");
        assertEquals("new@email.com", testUser.getEmail());
    }
    
    @Test
    void testSetRole() {
        testUser.setRole("admin");
        assertEquals("admin", testUser.getRole());
    }
    
    @Test
    void testSetPassword() {
        String newPassword = "newpassword123";
        testUser.setPassword(newPassword);
        
        // Password should be hashed, not stored as plain text
        assertNotEquals(newPassword, testUser.getPasswordHash());
        assertNotNull(testUser.getPasswordHash());
        assertTrue(testUser.getPasswordHash().length() > 0);
    }
    
    // ==================== VALIDATION TESTS ====================
    
    @Test
    void testHasPasswordWithValidUser() {
        assertTrue(testUser.hasPassword());
    }
    
    @Test
    void testHasPasswordWithNoPassword() {
        User user = new User();
        assertFalse(user.hasPassword());
    }
    
    @Test
    void testHasPasswordWithEmptyPassword() {
        testUser.setPasswordHash("");
        assertFalse(testUser.hasPassword());
    }
    
    // ==================== PASSWORD TESTS ====================
    
    @Test
    void testPasswordVerification() {
        String password = "TestPassword123!";
        testUser.setPassword(password);
        
        assertTrue(testUser.verifyPassword(password));
        assertFalse(testUser.verifyPassword("wrongpassword"));
    }
    
    @Test
    void testPasswordVerificationWithNullPassword() {
        assertFalse(testUser.verifyPassword(null));
    }
    
    @Test
    void testPasswordVerificationWithEmptyPassword() {
        assertFalse(testUser.verifyPassword(""));
    }
    
    // ==================== EQUALS AND HASHCODE TESTS ====================
    
    @Test
    void testEqualsWithSameUser() {
        User user1 = new User("test", "test@test.com", "customer", "password");
        User user2 = new User("test", "test@test.com", "customer", "password");
        
        // Note: User class doesn't override equals, so objects are compared by reference
        assertNotEquals(user1, user2);
    }
    
    @Test
    void testEqualsWithDifferentUsername() {
        User user1 = new User("test1", "test@test.com", "customer", "password");
        User user2 = new User("test2", "test@test.com", "customer", "password");
        
        assertNotEquals(user1, user2);
    }
    
    @Test
    void testEqualsWithNull() {
        assertNotEquals(testUser, null);
    }
    
    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(testUser, "not a user");
    }
    
    @Test
    void testHashCodeConsistency() {
        User user1 = new User("test", "test@test.com", "customer", "password");
        User user2 = new User("test", "test@test.com", "customer", "password");
        
        // Note: User class doesn't override hashCode, so different objects have different hash codes
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }
    
    // ==================== TOSTRING TESTS ====================
    
    @Test
    void testToString() {
        String result = testUser.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("customer"));
        // Should not contain password hash for security
        assertFalse(result.contains(testUser.getPasswordHash()));
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testUserWithVeryLongUsername() {
        String longUsername = "a".repeat(1000);
        testUser.setUsername(longUsername);
        
        assertEquals(longUsername, testUser.getUsername());
    }
    
    @Test
    void testUserWithSpecialCharactersInUsername() {
        testUser.setUsername("user@#$%");
        assertEquals("user@#$%", testUser.getUsername());
    }
    
    @Test
    void testUserWithUnicodeCharacters() {
        testUser.setUsername("用户");
        testUser.setEmail("用户@测试.com");
        
        assertEquals("用户", testUser.getUsername());
        assertEquals("用户@测试.com", testUser.getEmail());
    }
    
    @Test
    void testUserWithWhitespaceInUsername() {
        testUser.setUsername("  testuser  ");
        assertEquals("  testuser  ", testUser.getUsername());
    }
    
    @Test
    void testUserWithMultipleRoles() {
        // Test valid roles
        String[] validRoles = {"customer", "admin", "employee"};
        
        for (String role : validRoles) {
            testUser.setRole(role);
            assertEquals(role, testUser.getRole());
        }
    }
}
