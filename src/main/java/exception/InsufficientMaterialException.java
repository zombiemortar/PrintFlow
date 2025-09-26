package exception;

/**
 * Exception thrown when there is insufficient material available for an order.
 * Used for handling material shortage scenarios.
 */
public class InsufficientMaterialException extends Exception {
    
    /**
     * Constructor with a message describing the error.
     * @param message The error message
     */
    public InsufficientMaterialException(String message) {
        super(message);
    }
    
    /**
     * Constructor with a message and cause.
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public InsufficientMaterialException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with material name and required quantity.
     * @param materialName The name of the material that is insufficient
     * @param requiredQuantity The quantity required
     * @param availableQuantity The quantity available
     */
    public InsufficientMaterialException(String materialName, int requiredQuantity, int availableQuantity) {
        super(String.format("Insufficient material: %s. Required: %d, Available: %d", 
                          materialName, requiredQuantity, availableQuantity));
    }
    
    /**
     * Default constructor.
     */
    public InsufficientMaterialException() {
        super("Insufficient material available");
    }
}

