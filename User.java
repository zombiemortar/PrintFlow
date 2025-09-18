/**
 * Base class representing a user in the 3D printing service system.
 * Provides basic functionality for order submission and invoice viewing.
 */
public class User {
    private String username;
    private String email;
    private String role;
    
    /**
     * Constructor for creating a new user.
     * @param username The unique username for the user
     * @param email The user's email address
     * @param role The user's role in the system
     */
    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
    
    /**
     * Default constructor.
     */
    public User() {
        this.username = "";
        this.email = "";
        this.role = "customer";
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
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
