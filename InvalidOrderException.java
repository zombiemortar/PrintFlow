/**
 * Exception thrown when an order is invalid or cannot be processed.
 * Used for handling order validation errors.
 */
public class InvalidOrderException extends Exception {
    
    /**
     * Constructor with a message describing the error.
     * @param message The error message
     */
    public InvalidOrderException(String message) {
        super(message);
    }
    
    /**
     * Constructor with a message and cause.
     * @param message The error message
     * @param cause The underlying cause of the exception
     */
    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Default constructor.
     */
    public InvalidOrderException() {
        super("Invalid order");
    }
}
