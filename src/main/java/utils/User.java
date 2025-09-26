package utils;

/**
 * Base class representing a user in the 3D printing service system.
 * Provides basic functionality for order submission and invoice viewing.
 */
public class User {
    private String username;
    private String email;
    private String role;
    private String passwordHash;
    
    /**
     * Constructor for creating a new user.
     * @param username The unique username for the user
     * @param email The user's email address
     * @param role The user's role in the system
     * @param password The plain text password (will be hashed)
     */
    public User(String username, String email, String role, String password) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.passwordHash = SecurityManager.hashPassword(password);
    }
    
    /**
     * Constructor for creating a new user with pre-hashed password.
     * @param username The unique username for the user
     * @param email The user's email address
     * @param role The user's role in the system
     * @param passwordHash The pre-hashed password
     * @param isHashed Flag indicating if password is already hashed
     */
    public User(String username, String email, String role, String passwordHash, boolean isHashed) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.passwordHash = isHashed ? passwordHash : SecurityManager.hashPassword(passwordHash);
    }
    
    /**
     * Default constructor.
     */
    public User() {
        this.username = "";
        this.email = "";
        this.role = "customer";
        this.passwordHash = null;
    }
    
    /**
     * Submits a new order to the system.
     * @param material The material to be used for printing
     * @param dimensions The dimensions of the print object
     * @param quantity The quantity of items to print
     * @param specialInstructions Any special instructions for the print job
     * @return The created Order object
     */
    public Order submitOrder(Material material, String dimensions, int quantity, String specialInstructions) {
        // Enhanced input validation
        ValidationResult validation = SecurityManager.validateOrder(new Order(this, material, dimensions, quantity, specialInstructions));
        if (!validation.isValid()) {
            System.err.println("Order validation failed:");
            for (String error : validation.getErrors()) {
                System.err.println("  - " + error);
            }
            return null;
        }
        
        // Basic validation
        if (material == null || dimensions == null || dimensions.trim().isEmpty() || quantity <= 0) {
            return null;
        }
        
        // Check order limits from SystemConfig
        if (quantity > SystemConfig.getMaxOrderQuantity()) {
            return null;
        }

        // Simple stock check: assume 10g per item
        int gramsNeeded = quantity * 10;
        if (!Inventory.hasSufficient(material, gramsNeeded)) {
            return null;
        }

        // Create order
        Order order = new Order(this, material, dimensions.trim(), quantity, specialInstructions);
        // Set rush priority if noted in instructions and rush orders are allowed
        if (specialInstructions != null && specialInstructions.toLowerCase().contains("rush") && SystemConfig.isAllowRushOrders()) {
            order.setPriority("rush");
        }

        // Register and enqueue
        OrderManager.registerOrder(order);
        OrderManager.enqueueOrder(order);

        // Consume stock
        Inventory.consume(material, gramsNeeded);

        return order;
    }
    
    /**
     * Views an invoice for a specific order.
     * @param orderID The ID of the order to view invoice for
     * @return The Invoice object if found, null otherwise
     */
    public Invoice viewInvoice(int orderID) {
        // This method will be implemented to retrieve and display invoices
        // For now, return null as placeholder
        return null;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the password hash (for internal use only).
     * @return The password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the password hash (for internal use only).
     * @param passwordHash The password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Verifies a password against the stored hash.
     * @param password The plain text password to verify
     * @return true if the password is correct, false otherwise
     */
    public boolean verifyPassword(String password) {
        if (password == null || passwordHash == null) {
            return false;
        }
        return SecurityManager.verifyPassword(password, passwordHash);
    }
    
    /**
     * Changes the user's password.
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword)) {
            return false;
        }
        
        PasswordValidationResult validation = SecurityManager.validatePasswordStrength(newPassword);
        if (!validation.isValid()) {
            System.err.println("New password does not meet security requirements:");
            for (String error : validation.getErrors()) {
                System.err.println("  - " + error);
            }
            return false;
        }
        
            this.passwordHash = SecurityManager.hashPassword(newPassword);
        return true;
    }
    
    /**
     * Sets a new password (for admin use or password reset).
     * @param newPassword The new password
     * @return true if password was set successfully, false otherwise
     */
    public boolean setPassword(String newPassword) {
        PasswordValidationResult validation = SecurityManager.validatePasswordStrength(newPassword);
        if (!validation.isValid()) {
            System.err.println("Password does not meet security requirements:");
            for (String error : validation.getErrors()) {
                System.err.println("  - " + error);
            }
            return false;
        }
        
            this.passwordHash = SecurityManager.hashPassword(newPassword);
        return true;
    }
    
    /**
     * Checks if the user has a password set.
     * @return true if password is set, false otherwise
     */
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
