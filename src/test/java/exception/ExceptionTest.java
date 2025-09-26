package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for custom exception classes.
 * Tests all exception types and their functionality.
 */
public class ExceptionTest {
    
    // ==================== INVALID ORDER EXCEPTION TESTS ====================
    
    @Test
    void testInvalidOrderExceptionCreation() {
        InvalidOrderException exception = new InvalidOrderException("Test invalid order message");
        
        assertNotNull(exception);
        assertEquals("Test invalid order message", exception.getMessage());
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInvalidOrderExceptionWithNullMessage() {
        InvalidOrderException exception = new InvalidOrderException(null);
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithEmptyMessage() {
        InvalidOrderException exception = new InvalidOrderException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithLongMessage() {
        String longMessage = "This is a very long error message that describes in detail what went wrong with the order validation process and why it failed";
        InvalidOrderException exception = new InvalidOrderException(longMessage);
        
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithSpecialCharacters() {
        String specialMessage = "Error: Order #12345 failed validation! @#$%^&*()";
        InvalidOrderException exception = new InvalidOrderException(specialMessage);
        
        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithUnicodeCharacters() {
        String unicodeMessage = "错误：订单验证失败！";
        InvalidOrderException exception = new InvalidOrderException(unicodeMessage);
        
        assertNotNull(exception);
        assertEquals(unicodeMessage, exception.getMessage());
    }
    
    // ==================== INVALID MATERIAL EXCEPTION TESTS ====================
    
    @Test
    void testInvalidMaterialExceptionCreation() {
        InvalidMaterialException exception = new InvalidMaterialException("Test invalid material message");
        
        assertNotNull(exception);
        assertEquals("Test invalid material message", exception.getMessage());
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInvalidMaterialExceptionWithNullMessage() {
        InvalidMaterialException exception = new InvalidMaterialException(null);
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithEmptyMessage() {
        InvalidMaterialException exception = new InvalidMaterialException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithLongMessage() {
        String longMessage = "This is a very long error message that describes in detail what went wrong with the material validation process and why it failed";
        InvalidMaterialException exception = new InvalidMaterialException(longMessage);
        
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithSpecialCharacters() {
        String specialMessage = "Error: Material 'PLA+' failed validation! @#$%^&*()";
        InvalidMaterialException exception = new InvalidMaterialException(specialMessage);
        
        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithUnicodeCharacters() {
        String unicodeMessage = "错误：材料验证失败！";
        InvalidMaterialException exception = new InvalidMaterialException(unicodeMessage);
        
        assertNotNull(exception);
        assertEquals(unicodeMessage, exception.getMessage());
    }
    
    // ==================== INSUFFICIENT MATERIAL EXCEPTION TESTS ====================
    
    @Test
    void testInsufficientMaterialExceptionCreation() {
        InsufficientMaterialException exception = new InsufficientMaterialException("Test insufficient material message");
        
        assertNotNull(exception);
        assertEquals("Test insufficient material message", exception.getMessage());
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInsufficientMaterialExceptionWithNullMessage() {
        InsufficientMaterialException exception = new InsufficientMaterialException(null);
        
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithEmptyMessage() {
        InsufficientMaterialException exception = new InsufficientMaterialException("");
        
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithLongMessage() {
        String longMessage = "This is a very long error message that describes in detail what went wrong with the material availability check and why there is insufficient material";
        InsufficientMaterialException exception = new InsufficientMaterialException(longMessage);
        
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithSpecialCharacters() {
        String specialMessage = "Error: Insufficient 'PLA' material! Need 100g, have 50g. @#$%^&*()";
        InsufficientMaterialException exception = new InsufficientMaterialException(specialMessage);
        
        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithUnicodeCharacters() {
        String unicodeMessage = "错误：材料不足！";
        InsufficientMaterialException exception = new InsufficientMaterialException(unicodeMessage);
        
        assertNotNull(exception);
        assertEquals(unicodeMessage, exception.getMessage());
    }
    
    // ==================== EXCEPTION INHERITANCE TESTS ====================
    
    @Test
    void testInvalidOrderExceptionInheritance() {
        InvalidOrderException exception = new InvalidOrderException("Test message");
        
        assertTrue(exception instanceof Exception);
        // InvalidOrderException extends Exception, not RuntimeException
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInvalidMaterialExceptionInheritance() {
        InvalidMaterialException exception = new InvalidMaterialException("Test message");
        
        assertTrue(exception instanceof Exception);
        // InvalidMaterialException extends Exception, not RuntimeException
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInsufficientMaterialExceptionInheritance() {
        InsufficientMaterialException exception = new InsufficientMaterialException("Test message");
        
        assertTrue(exception instanceof Exception);
        // InsufficientMaterialException extends Exception, not RuntimeException
        assertTrue(exception instanceof Exception);
    }
    
    // ==================== EXCEPTION CHAINING TESTS ====================
    
    @Test
    void testInvalidOrderExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        InvalidOrderException exception = new InvalidOrderException("Test message", cause);
        
        assertNotNull(exception);
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        InvalidMaterialException exception = new InvalidMaterialException("Test message", cause);
        
        assertNotNull(exception);
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        InsufficientMaterialException exception = new InsufficientMaterialException("Test message", cause);
        
        assertNotNull(exception);
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    // ==================== EXCEPTION STACK TRACE TESTS ====================
    
    @Test
    void testInvalidOrderExceptionStackTrace() {
        InvalidOrderException exception = new InvalidOrderException("Test message");
        
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
    
    @Test
    void testInvalidMaterialExceptionStackTrace() {
        InvalidMaterialException exception = new InvalidMaterialException("Test message");
        
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
    
    @Test
    void testInsufficientMaterialExceptionStackTrace() {
        InsufficientMaterialException exception = new InsufficientMaterialException("Test message");
        
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }
    
    // ==================== EXCEPTION THROWING TESTS ====================
    
    @Test
    void testThrowInvalidOrderException() {
        assertThrows(InvalidOrderException.class, () -> {
            throw new InvalidOrderException("Test exception");
        });
    }
    
    @Test
    void testThrowInvalidMaterialException() {
        assertThrows(InvalidMaterialException.class, () -> {
            throw new InvalidMaterialException("Test exception");
        });
    }
    
    @Test
    void testThrowInsufficientMaterialException() {
        assertThrows(InsufficientMaterialException.class, () -> {
            throw new InsufficientMaterialException("Test exception");
        });
    }
    
    // ==================== EXCEPTION MESSAGE FORMATTING TESTS ====================
    
    @Test
    void testInvalidOrderExceptionWithFormattedMessage() {
        String material = "PLA";
        int required = 100;
        int available = 50;
        String message = String.format("Insufficient %s material: required %d, available %d", material, required, available);
        
        InvalidOrderException exception = new InvalidOrderException(message);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithFormattedMessage() {
        String material = "ABS";
        double cost = 0.025;
        String message = String.format("Invalid material %s with cost %.3f", material, cost);
        
        InvalidMaterialException exception = new InvalidMaterialException(message);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithFormattedMessage() {
        String material = "PETG";
        double required = 150.5;
        double available = 75.0;
        String message = String.format("Insufficient %s material: required %.1f, available %.1f", material, required, available);
        
        InsufficientMaterialException exception = new InsufficientMaterialException(message);
        assertEquals(message, exception.getMessage());
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testExceptionWithVeryLongMessage() {
        String veryLongMessage = "a".repeat(10000);
        
        InvalidOrderException orderException = new InvalidOrderException(veryLongMessage);
        InvalidMaterialException materialException = new InvalidMaterialException(veryLongMessage);
        InsufficientMaterialException insufficientException = new InsufficientMaterialException(veryLongMessage);
        
        assertEquals(veryLongMessage, orderException.getMessage());
        assertEquals(veryLongMessage, materialException.getMessage());
        assertEquals(veryLongMessage, insufficientException.getMessage());
    }
    
    @Test
    void testExceptionWithWhitespaceMessage() {
        String whitespaceMessage = "   \t\n\r   ";
        
        InvalidOrderException orderException = new InvalidOrderException(whitespaceMessage);
        InvalidMaterialException materialException = new InvalidMaterialException(whitespaceMessage);
        InsufficientMaterialException insufficientException = new InsufficientMaterialException(whitespaceMessage);
        
        assertEquals(whitespaceMessage, orderException.getMessage());
        assertEquals(whitespaceMessage, materialException.getMessage());
        assertEquals(whitespaceMessage, insufficientException.getMessage());
    }
    
    @Test
    void testExceptionWithNewlineMessage() {
        String newlineMessage = "Line 1\nLine 2\nLine 3";
        
        InvalidOrderException orderException = new InvalidOrderException(newlineMessage);
        InvalidMaterialException materialException = new InvalidMaterialException(newlineMessage);
        InsufficientMaterialException insufficientException = new InsufficientMaterialException(newlineMessage);
        
        assertEquals(newlineMessage, orderException.getMessage());
        assertEquals(newlineMessage, materialException.getMessage());
        assertEquals(newlineMessage, insufficientException.getMessage());
    }
}
