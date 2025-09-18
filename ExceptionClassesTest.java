import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for all exception classes.
 * Tests constructors, message handling, and inheritance.
 */
public class ExceptionClassesTest {
    
    // InvalidOrderException Tests
    @Test
    void testInvalidOrderExceptionDefaultConstructor() {
        InvalidOrderException exception = new InvalidOrderException();
        assertNotNull(exception);
        assertEquals("Invalid order", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithMessage() {
        String message = "Order quantity exceeds maximum allowed";
        InvalidOrderException exception = new InvalidOrderException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithMessageAndCause() {
        String message = "Order validation failed";
        Throwable cause = new IllegalArgumentException("Invalid input");
        InvalidOrderException exception = new InvalidOrderException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithNullMessage() {
        InvalidOrderException exception = new InvalidOrderException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithEmptyMessage() {
        InvalidOrderException exception = new InvalidOrderException("");
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithWhitespaceMessage() {
        InvalidOrderException exception = new InvalidOrderException("   ");
        assertNotNull(exception);
        assertEquals("   ", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidOrderExceptionWithSpecialCharacters() {
        String message = "Order contains special characters: !@#$%^&*()";
        InvalidOrderException exception = new InvalidOrderException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithUnicodeCharacters() {
        String message = "Order contains unicode: ñáéíóú";
        InvalidOrderException exception = new InvalidOrderException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionWithVeryLongMessage() {
        String message = "This is a very long error message that contains many details about why the order is invalid and what specific validation rules were violated";
        InvalidOrderException exception = new InvalidOrderException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidOrderExceptionInheritance() {
        InvalidOrderException exception = new InvalidOrderException("Test message");
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    // InsufficientMaterialException Tests
    @Test
    void testInsufficientMaterialExceptionDefaultConstructor() {
        InsufficientMaterialException exception = new InsufficientMaterialException();
        assertNotNull(exception);
        assertEquals("Insufficient material available", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMessage() {
        String message = "Not enough PLA material available";
        InsufficientMaterialException exception = new InsufficientMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMessageAndCause() {
        String message = "Material shortage due to supplier delay";
        Throwable cause = new RuntimeException("Supplier error");
        InsufficientMaterialException exception = new InsufficientMaterialException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMaterialDetails() {
        String materialName = "PLA";
        int requiredQuantity = 500;
        int availableQuantity = 200;
        InsufficientMaterialException exception = new InsufficientMaterialException(materialName, requiredQuantity, availableQuantity);
        assertNotNull(exception);
        String expectedMessage = String.format("Insufficient material: %s. Required: %d, Available: %d", 
                                              materialName, requiredQuantity, availableQuantity);
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMaterialDetailsZeroRequired() {
        String materialName = "ABS";
        int requiredQuantity = 0;
        int availableQuantity = 100;
        InsufficientMaterialException exception = new InsufficientMaterialException(materialName, requiredQuantity, availableQuantity);
        assertNotNull(exception);
        String expectedMessage = String.format("Insufficient material: %s. Required: %d, Available: %d", 
                                              materialName, requiredQuantity, availableQuantity);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMaterialDetailsZeroAvailable() {
        String materialName = "PETG";
        int requiredQuantity = 100;
        int availableQuantity = 0;
        InsufficientMaterialException exception = new InsufficientMaterialException(materialName, requiredQuantity, availableQuantity);
        assertNotNull(exception);
        String expectedMessage = String.format("Insufficient material: %s. Required: %d, Available: %d", 
                                              materialName, requiredQuantity, availableQuantity);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMaterialDetailsNegativeValues() {
        String materialName = "TPU";
        int requiredQuantity = -100;
        int availableQuantity = -50;
        InsufficientMaterialException exception = new InsufficientMaterialException(materialName, requiredQuantity, availableQuantity);
        assertNotNull(exception);
        String expectedMessage = String.format("Insufficient material: %s. Required: %d, Available: %d", 
                                              materialName, requiredQuantity, availableQuantity);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithMaterialDetailsLargeValues() {
        String materialName = "PLA";
        int requiredQuantity = 1000000;
        int availableQuantity = 999999;
        InsufficientMaterialException exception = new InsufficientMaterialException(materialName, requiredQuantity, availableQuantity);
        assertNotNull(exception);
        String expectedMessage = String.format("Insufficient material: %s. Required: %d, Available: %d", 
                                              materialName, requiredQuantity, availableQuantity);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithNullMessage() {
        InsufficientMaterialException exception = new InsufficientMaterialException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithEmptyMessage() {
        InsufficientMaterialException exception = new InsufficientMaterialException("");
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithSpecialCharacters() {
        String message = "Material shortage: !@#$%^&*()";
        InsufficientMaterialException exception = new InsufficientMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionWithUnicodeCharacters() {
        String message = "Material shortage: ñáéíóú";
        InsufficientMaterialException exception = new InsufficientMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInsufficientMaterialExceptionInheritance() {
        InsufficientMaterialException exception = new InsufficientMaterialException("Test message");
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    // InvalidMaterialException Tests
    @Test
    void testInvalidMaterialExceptionDefaultConstructor() {
        InvalidMaterialException exception = new InvalidMaterialException();
        assertNotNull(exception);
        assertEquals("Invalid material", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMessage() {
        String message = "Material temperature is too high";
        InvalidMaterialException exception = new InvalidMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMessageAndCause() {
        String message = "Material validation failed";
        Throwable cause = new IllegalArgumentException("Invalid temperature");
        InvalidMaterialException exception = new InvalidMaterialException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationError() {
        String materialName = "PLA";
        String validationError = "Temperature exceeds maximum limit";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorSpecialCharacters() {
        String materialName = "PLA+";
        String validationError = "Temperature exceeds maximum limit!";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorUnicode() {
        String materialName = "PLA™";
        String validationError = "Temperature exceeds maximum limit";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorEmptyName() {
        String materialName = "";
        String validationError = "Name cannot be empty";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorNullName() {
        String materialName = null;
        String validationError = "Name cannot be null";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorNullError() {
        String materialName = "PLA";
        String validationError = null;
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithMaterialNameAndValidationErrorEmptyError() {
        String materialName = "PLA";
        String validationError = "";
        InvalidMaterialException exception = new InvalidMaterialException(materialName, validationError);
        assertNotNull(exception);
        String expectedMessage = String.format("Invalid material '%s': %s", materialName, validationError);
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithNullMessage() {
        InvalidMaterialException exception = new InvalidMaterialException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithEmptyMessage() {
        InvalidMaterialException exception = new InvalidMaterialException("");
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithWhitespaceMessage() {
        InvalidMaterialException exception = new InvalidMaterialException("   ");
        assertNotNull(exception);
        assertEquals("   ", exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidMaterialExceptionWithSpecialCharacters() {
        String message = "Material contains special characters: !@#$%^&*()";
        InvalidMaterialException exception = new InvalidMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithUnicodeCharacters() {
        String message = "Material contains unicode: ñáéíóú";
        InvalidMaterialException exception = new InvalidMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionWithVeryLongMessage() {
        String message = "This is a very long error message that contains many details about why the material is invalid and what specific validation rules were violated";
        InvalidMaterialException exception = new InvalidMaterialException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testInvalidMaterialExceptionInheritance() {
        InvalidMaterialException exception = new InvalidMaterialException("Test message");
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    // Exception Behavior Tests
    @Test
    void testExceptionThrowingAndCatching() {
        try {
            throw new InvalidOrderException("Test order error");
        } catch (InvalidOrderException e) {
            assertEquals("Test order error", e.getMessage());
        }
    }
    
    @Test
    void testExceptionThrowingAndCatchingWithCause() {
        try {
            Throwable cause = new IllegalArgumentException("Root cause");
            throw new InsufficientMaterialException("Material error", cause);
        } catch (InsufficientMaterialException e) {
            assertEquals("Material error", e.getMessage());
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
    
    @Test
    void testExceptionThrowingAndCatchingWithMaterialDetails() {
        try {
            throw new InsufficientMaterialException("PLA", 500, 200);
        } catch (InsufficientMaterialException e) {
            assertTrue(e.getMessage().contains("PLA"));
            assertTrue(e.getMessage().contains("500"));
            assertTrue(e.getMessage().contains("200"));
        }
    }
    
    @Test
    void testExceptionThrowingAndCatchingWithValidationError() {
        try {
            throw new InvalidMaterialException("PLA", "Temperature too high");
        } catch (InvalidMaterialException e) {
            assertTrue(e.getMessage().contains("PLA"));
            assertTrue(e.getMessage().contains("Temperature too high"));
        }
    }
    
    // Exception Stack Trace Tests
    @Test
    void testExceptionStackTrace() {
        try {
            throw new InvalidOrderException("Test error");
        } catch (InvalidOrderException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertNotNull(stackTrace);
            assertTrue(stackTrace.length > 0);
        }
    }
    
    @Test
    void testExceptionStackTraceWithCause() {
        try {
            Throwable cause = new RuntimeException("Root cause");
            throw new InvalidMaterialException("Test error", cause);
        } catch (InvalidMaterialException e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertNotNull(stackTrace);
            assertTrue(stackTrace.length > 0);
            
            assertNotNull(e.getCause());
            StackTraceElement[] causeStackTrace = e.getCause().getStackTrace();
            assertNotNull(causeStackTrace);
        }
    }
    
    // Exception Equality Tests
    @Test
    void testExceptionEquality() {
        InvalidOrderException exception1 = new InvalidOrderException("Test message");
        InvalidOrderException exception2 = new InvalidOrderException("Test message");
        
        // Exceptions are not equal by default (no equals override)
        assertNotEquals(exception1, exception2);
    }
    
    @Test
    void testExceptionHashCode() {
        InvalidOrderException exception1 = new InvalidOrderException("Test message");
        InvalidOrderException exception2 = new InvalidOrderException("Test message");
        
        // Hash codes may or may not be equal (depends on implementation)
        // Just test that they don't throw exceptions
        assertDoesNotThrow(() -> exception1.hashCode());
        assertDoesNotThrow(() -> exception2.hashCode());
    }
    
    // Exception String Representation Tests
    @Test
    void testExceptionToString() {
        InvalidOrderException exception = new InvalidOrderException("Test message");
        String stringRepresentation = exception.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("InvalidOrderException"));
        assertTrue(stringRepresentation.contains("Test message"));
    }
    
    @Test
    void testExceptionToStringWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        InvalidMaterialException exception = new InvalidMaterialException("Test message", cause);
        String stringRepresentation = exception.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("InvalidMaterialException"));
        assertTrue(stringRepresentation.contains("Test message"));
    }
}
