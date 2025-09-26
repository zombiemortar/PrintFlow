import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified exception handling tests for custom exception classes.
 * Consolidates ExceptionClassesTest functionality.
 */
public class ExceptionTests {
    
    // ==================== INVALID ORDER EXCEPTION TESTS ====================
    
    @Test
    void testInvalidOrderExceptionCreation() {
        InvalidOrderException exception = new InvalidOrderException("Test invalid order message");
        
        assertNotNull(exception);
        assertEquals("Test invalid order message", exception.getMessage());
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testInvalidOrderExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        InvalidOrderException exception = new InvalidOrderException("Invalid order", cause);
        
        assertNotNull(exception);
        assertEquals("Invalid order", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionDefaultMessage() {
        InvalidOrderException exception = new InvalidOrderException();
        
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionThrowing() {
        assertThrows(InvalidOrderException.class, () -> {
            throw new InvalidOrderException("Test exception");
        });
    }
    
    @Test
    void testInvalidOrderExceptionCatching() {
        try {
            throw new InvalidOrderException("Test exception");
        } catch (InvalidOrderException e) {
            assertEquals("Test exception", e.getMessage());
        }
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
    void testInsufficientMaterialExceptionWithCause() {
        Exception cause = new IllegalStateException("Root cause");
        InsufficientMaterialException exception = new InsufficientMaterialException("Insufficient material", cause);
        
        assertNotNull(exception);
        assertEquals("Insufficient material", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionDefaultMessage() {
        InsufficientMaterialException exception = new InsufficientMaterialException();
        
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionThrowing() {
        assertThrows(InsufficientMaterialException.class, () -> {
            throw new InsufficientMaterialException("Test exception");
        });
    }
    
    @Test
    void testInsufficientMaterialExceptionCatching() {
        try {
            throw new InsufficientMaterialException("Test exception");
        } catch (InsufficientMaterialException e) {
            assertEquals("Test exception", e.getMessage());
        }
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
    void testInvalidMaterialExceptionWithCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        InvalidMaterialException exception = new InvalidMaterialException("Invalid material", cause);
        
        assertNotNull(exception);
        assertEquals("Invalid material", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionDefaultMessage() {
        InvalidMaterialException exception = new InvalidMaterialException();
        
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionThrowing() {
        assertThrows(InvalidMaterialException.class, () -> {
            throw new InvalidMaterialException("Test exception");
        });
    }
    
    @Test
    void testInvalidMaterialExceptionCatching() {
        try {
            throw new InvalidMaterialException("Test exception");
        } catch (InvalidMaterialException e) {
            assertEquals("Test exception", e.getMessage());
        }
    }
    
    // ==================== EXCEPTION INHERITANCE TESTS ====================
    
    @Test
    void testExceptionInheritance() {
        // All custom exceptions should extend Exception
        assertTrue(InvalidOrderException.class.getSuperclass() == Exception.class);
        assertTrue(InsufficientMaterialException.class.getSuperclass() == Exception.class);
        assertTrue(InvalidMaterialException.class.getSuperclass() == Exception.class);
    }
    
    @Test
    void testExceptionPolymorphism() {
        // Test that exceptions can be caught as their parent type
        Exception[] exceptions = {
            new InvalidOrderException("Order error"),
            new InsufficientMaterialException("Material error"),
            new InvalidMaterialException("Material error")
        };
        
        for (Exception exception : exceptions) {
            assertNotNull(exception);
            assertTrue(exception instanceof Exception);
        }
    }
    
    // ==================== EXCEPTION MESSAGE TESTS ====================
    
    @Test
    void testExceptionMessageVariations() {
        String[] messages = {
            "Simple message",
            "Message with numbers: 123",
            "Message with special chars: !@#$%^&*()",
            "Message with unicode: 测试 🎯",
            "Very long message that contains multiple words and should be handled properly by the exception system",
            ""
        };
        
        for (String message : messages) {
            InvalidOrderException exception = new InvalidOrderException(message);
            assertEquals(message, exception.getMessage());
        }
    }
    
    @Test
    void testExceptionMessageWithNull() {
        // Test behavior with null message
        InvalidOrderException exception = new InvalidOrderException((String) null);
        assertNotNull(exception);
        // Message might be null or converted to string representation
    }
    
    // ==================== EXCEPTION CHAINING TESTS ====================
    
    @Test
    void testExceptionChaining() {
        // Create a chain of exceptions
        IllegalArgumentException rootCause = new IllegalArgumentException("Root cause");
        InvalidOrderException intermediate = new InvalidOrderException("Intermediate cause", rootCause);
        Exception topLevel = new Exception("Top level", intermediate);
        
        assertNotNull(topLevel.getCause());
        assertNotNull(topLevel.getCause().getCause());
        assertEquals(rootCause, topLevel.getCause().getCause());
    }
    
    @Test
    void testExceptionChainingWithCustomExceptions() {
        // Test chaining custom exceptions
        InvalidMaterialException materialException = new InvalidMaterialException("Material error");
        InsufficientMaterialException insufficientException = new InsufficientMaterialException("Insufficient material", materialException);
        InvalidOrderException orderException = new InvalidOrderException("Order error", insufficientException);
        
        assertNotNull(orderException.getCause());
        assertNotNull(orderException.getCause().getCause());
        assertEquals(materialException, orderException.getCause().getCause());
    }
    
    // ==================== EXCEPTION STACK TRACE TESTS ====================
    
    @Test
    void testExceptionStackTrace() {
        InvalidOrderException exception = new InvalidOrderException("Test exception");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        
        // The first element should be this test method
        assertTrue(stackTrace[0].getMethodName().contains("testExceptionStackTrace"));
    }
    
    @Test
    void testExceptionStackTraceWithCause() {
        IllegalArgumentException cause = new IllegalArgumentException("Cause");
        InvalidOrderException exception = new InvalidOrderException("Exception", cause);
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        
        StackTraceElement[] causeStackTrace = cause.getStackTrace();
        assertNotNull(causeStackTrace);
        assertTrue(causeStackTrace.length > 0);
    }
    
    // ==================== EXCEPTION SERIALIZATION TESTS ====================
    
    @Test
    void testExceptionSerialization() {
        InvalidOrderException original = new InvalidOrderException("Serialization test");
        
        // Test that exception can be converted to string
        String exceptionString = original.toString();
        assertNotNull(exceptionString);
        assertTrue(exceptionString.contains("InvalidOrderException"));
        assertTrue(exceptionString.contains("Serialization test"));
    }
    
    @Test
    void testExceptionEquality() {
        InvalidOrderException exception1 = new InvalidOrderException("Same message");
        InvalidOrderException exception2 = new InvalidOrderException("Same message");
        InvalidOrderException exception3 = new InvalidOrderException("Different message");
        
        // Exceptions with same message should not be equal (different instances)
        assertNotEquals(exception1, exception2);
        assertNotEquals(exception1, exception3);
        assertNotEquals(exception2, exception3);
    }
    
    // ==================== EXCEPTION PERFORMANCE TESTS ====================
    
    @Test
    void testExceptionCreationPerformance() {
        long startTime = System.currentTimeMillis();
        
        // Create many exceptions
        for (int i = 0; i < 1000; i++) {
            new InvalidOrderException("Performance test " + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 1000, "Exception creation took too long: " + duration + "ms");
    }
    
    @Test
    void testExceptionThrowingPerformance() {
        long startTime = System.currentTimeMillis();
        
        // Throw and catch many exceptions
        for (int i = 0; i < 1000; i++) {
            try {
                throw new InvalidOrderException("Performance test " + i);
            } catch (InvalidOrderException e) {
                // Exception caught successfully
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time
        assertTrue(duration < 2000, "Exception throwing took too long: " + duration + "ms");
    }
    
    // ==================== EXCEPTION EDGE CASE TESTS ====================
    
    @Test
    void testExceptionWithVeryLongMessage() {
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("This is a very long message part ").append(i).append(". ");
        }
        
        InvalidOrderException exception = new InvalidOrderException(longMessage.toString());
        assertNotNull(exception);
        assertEquals(longMessage.toString(), exception.getMessage());
    }
    
    @Test
    void testExceptionWithSpecialCharacters() {
        String specialMessage = "Message with special chars: !@#$%^&*()_+-=[]{}|;:'\",.<>?/\\`~";
        InvalidOrderException exception = new InvalidOrderException(specialMessage);
        assertNotNull(exception);
        assertEquals(specialMessage, exception.getMessage());
    }
    
    @Test
    void testExceptionWithUnicodeCharacters() {
        String unicodeMessage = "Message with unicode: 测试 🎯 émojis 中文";
        InvalidOrderException exception = new InvalidOrderException(unicodeMessage);
        assertNotNull(exception);
        assertEquals(unicodeMessage, exception.getMessage());
    }
    
    @Test
    void testExceptionWithNewlines() {
        String multilineMessage = "Line 1\nLine 2\nLine 3";
        InvalidOrderException exception = new InvalidOrderException(multilineMessage);
        assertNotNull(exception);
        assertEquals(multilineMessage, exception.getMessage());
    }
    
    @Test
    void testExceptionWithTabs() {
        String tabMessage = "Message\twith\ttabs";
        InvalidOrderException exception = new InvalidOrderException(tabMessage);
        assertNotNull(exception);
        assertEquals(tabMessage, exception.getMessage());
    }
    
    // ==================== EXCEPTION INTEGRATION TESTS ====================
    
    @Test
    void testExceptionInBusinessLogic() {
        // Test that exceptions can be used in business logic
        try {
            // Simulate business logic that might throw exceptions
            if (true) { // Simulate error condition
                throw new InvalidOrderException("Business logic error");
            }
        } catch (InvalidOrderException e) {
            assertEquals("Business logic error", e.getMessage());
        }
    }
    
    @Test
    void testExceptionPropagation() {
        // Test that exceptions propagate correctly
        assertThrows(InvalidOrderException.class, () -> {
            try {
                methodThatThrowsInvalidOrderException();
            } catch (InvalidOrderException e) {
                throw e;
            }
        });
    }
    
    private void methodThatThrowsInvalidOrderException() throws InvalidOrderException {
        throw new InvalidOrderException("Propagated exception");
    }
    
    @Test
    void testExceptionHandlingInTryCatch() {
        boolean exceptionCaught = false;
        
        try {
            throw new InsufficientMaterialException("Test exception");
        } catch (InsufficientMaterialException e) {
            exceptionCaught = true;
            assertEquals("Test exception", e.getMessage());
        }
        
        assertTrue(exceptionCaught);
    }
    
    @Test
    void testMultipleExceptionTypes() {
        // Test handling multiple exception types
        Exception[] exceptions = {
            new InvalidOrderException("Order error"),
            new InsufficientMaterialException("Material error"),
            new InvalidMaterialException("Material error")
        };
        
        for (Exception exception : exceptions) {
            assertNotNull(exception);
            assertTrue(exception instanceof Exception);
        }
    }
}
