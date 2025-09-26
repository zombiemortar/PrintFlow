/**
 * AdminUser subclass that extends User with administrative privileges.
 * Provides methods for system management and oversight.
 */
public class AdminUser extends User {
    
    /**
     * Constructor for creating a new admin user.
     * @param username The unique username for the admin
     * @param email The admin's email address
     * @param password The plain text password (will be hashed)
     */
    public AdminUser(String username, String email, String password) {
        super(username, email, "admin", password);
    }
    
    /**
     * Constructor for creating a new admin user with pre-hashed password.
     * @param username The unique username for the admin
     * @param email The admin's email address
     * @param passwordHash The pre-hashed password
     * @param isHashed Flag indicating if password is already hashed
     */
    public AdminUser(String username, String email, String passwordHash, boolean isHashed) {
        super(username, email, "admin", passwordHash, isHashed);
    }
    
    /**
     * Default constructor for admin user.
     */
    public AdminUser() {
        super();
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
        // Enhanced input validation
        ValidationResult nameValidation = InputValidator.validateMaterialName(name);
        if (!nameValidation.isValid()) {
            System.err.println("Material name validation failed:");
            for (String error : nameValidation.getErrors()) {
                System.err.println("  - " + error);
            }
            return null;
        }
        
        ValidationResult costValidation = InputValidator.validateNumeric(costPerGram, 0.01, 1000.0, "Cost per gram");
        if (!costValidation.isValid()) {
            System.err.println("Cost validation failed:");
            for (String error : costValidation.getErrors()) {
                System.err.println("  - " + error);
            }
            return null;
        }
        
        ValidationResult tempValidation = InputValidator.validateNumeric(printTemp, 50, 500, "Print temperature");
        if (!tempValidation.isValid()) {
            System.err.println("Temperature validation failed:");
            for (String error : tempValidation.getErrors()) {
                System.err.println("  - " + error);
            }
            return null;
        }
        
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
