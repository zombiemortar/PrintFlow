package exception;

/**
 * Exception thrown when a material is invalid or cannot be used.
 * Used for handling material validation errors.
 */
public class InvalidMaterialException extends Exception {
    
    /**
     * Constructor with a message describing the error.
     * @param message The error message
     */
    public InvalidMaterialException(String message) {
        super(message);
    }
    
    /**
     * Constructor with a message and cause.
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public InvalidMaterialException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with material name and specific validation error.
     * @param materialName The name of the invalid material
     * @param validationError The specific validation error
     */
    public InvalidMaterialException(String materialName, String validationError) {
        super(String.format("Invalid material '%s': %s", materialName, validationError));
    }
    
    /**
     * Default constructor.
     */
    public InvalidMaterialException() {
        super("Invalid material");
    }
}

