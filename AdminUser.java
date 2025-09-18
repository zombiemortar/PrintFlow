/**
 * AdminUser subclass that extends User with administrative privileges.
 * Provides methods for system management and oversight.
 */
public class AdminUser extends User {
    
    /**
     * Constructor for creating a new admin user.
     * @param username The unique username for the admin
     * @param email The admin's email address
     */
    public AdminUser(String username, String email) {
        super(username, email, "admin");
    }
    
    /**
     * Default constructor for admin user.
     */
    public AdminUser() {
        super("", "", "admin");
    }
    
    /**
     * Adds a new material to the system's available materials.
     * @param name The name of the material
     * @param costPerGram The cost per gram of the material
     * @param printTemp The recommended printing temperature
     * @param color The available color of the material
     * @return The created Material object
     */
    public Material addMaterial(String name, double costPerGram, int printTemp, String color) {
        if (name == null || name.trim().isEmpty() || costPerGram <= 0 || printTemp <= 0 || color == null || color.trim().isEmpty()) {
            return null;
        }
        Material material = new Material(name.trim(), costPerGram, printTemp, color.trim());
        // For simplicity, when adding a material, seed some stock in inventory
        Inventory.setStock(material, 1000); // 1000 grams default
        return material;
    }
    
    /**
     * Views all orders in the system for administrative oversight.
     * @return Array of all Order objects in the system
     */
    public Order[] viewAllOrders() {
        return OrderManager.getAllOrders();
    }
    
    /**
     * Modifies pricing constants used in the pricing algorithm.
     * @param electricityCost The new electricity cost per hour
     * @param machineTimeCost The new machine time cost per hour
     * @param baseSetupCost The new base setup cost
     * @return true if pricing constants were successfully updated
     */
    public boolean modifyPricingConstants(double electricityCost, double machineTimeCost, double baseSetupCost) {
        try {
            SystemConfig.setElectricityCostPerHour(electricityCost);
            SystemConfig.setMachineTimeCostPerHour(machineTimeCost);
            SystemConfig.setBaseSetupCost(baseSetupCost);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the admin user's role (always returns "admin").
     * @return The role string "admin"
     */
    @Override
    public String getRole() {
        return "admin";
    }
    
    /**
     * Sets the admin user's role (admin users cannot change their role).
     * @param role The role to set (ignored for admin users)
     */
    @Override
    public void setRole(String role) {
        // Admin users cannot change their role
        // This method is overridden to prevent role changes
    }
    
    @Override
    public String toString() {
        return "AdminUser{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='admin'" +
                '}';
    }
}
