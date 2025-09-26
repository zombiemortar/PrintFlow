/**
 * Represents a 3D printing order in the system.
 * Contains all necessary information for processing and pricing a print job.
 */
public class Order {
    private int orderID;
    private User user;
    private Material material;
    private String dimensions;
    private int quantity;
    private String specialInstructions;
    private String status;
    private String priority; // normal, rush, vip
    private double estimatedPrintHours;
    
    // Static counter for generating unique order IDs
    private static int nextOrderID = 1000;
    
    /**
     * Constructor for creating a new order.
     * @param user The user placing the order
     * @param material The material to be used for printing
     * @param dimensions The dimensions of the print object
     * @param quantity The quantity of items to print
     * @param specialInstructions Any special instructions for the print job
     */
    public Order(User user, Material material, String dimensions, int quantity, String specialInstructions) {
        this.orderID = nextOrderID++;
        this.user = user;
        this.material = material;
        this.dimensions = dimensions;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
        this.status = "pending";
        this.priority = "normal";
        this.estimatedPrintHours = estimatePrintTimeHours();
    }
    
    /**
     * Resets the static order ID counter to its initial value.
     * Intended for test isolation.
     */
    public static void resetOrderIdCounter() {
        nextOrderID = 1000;
    }
    
    /**
     * Default constructor.
     */
    public Order() {
        this.orderID = nextOrderID++;
        this.user = null;
        this.material = null;
        this.dimensions = "";
        this.quantity = 0;
        this.specialInstructions = "";
        this.status = "pending";
        this.priority = "normal";
        this.estimatedPrintHours = 0.0;
    }
    
    /**
     * Calculates the total price for the order.
     * @return The calculated total price
     */
    public double calculatePrice() {
        if (material == null || quantity <= 0) {
            return 0.0;
        }
        // grams usage assumption: 10g per item
        double gramsUsed = quantity * 10.0;
        double materialCost = material.getCostPerGram() * gramsUsed;
        double baseCost = SystemConfig.getBaseSetupCost();
        double machineHours = estimatePrintTimeHours();
        double electricityCost = SystemConfig.getElectricityCostPerHour() * machineHours;
        double machineTimeCost = SystemConfig.getMachineTimeCostPerHour() * machineHours;
        double subtotal = materialCost + baseCost + electricityCost + machineTimeCost;

        // simple discounts: 5% off for quantity >= 10
        if (quantity >= 10) {
            subtotal *= 0.95;
        }

        // VIP discount if user role is vip
        if (user != null && "vip".equalsIgnoreCase(user.getRole())) {
            subtotal *= 0.90; // extra 10% off
        }

        // rush surcharge
        if ("rush".equalsIgnoreCase(priority) && SystemConfig.isAllowRushOrders()) {
            subtotal *= (1.0 + SystemConfig.getRushOrderSurcharge());
        }

        // tax
        double totalWithTax = subtotal * (1.0 + SystemConfig.getTaxRate());
        return totalWithTax;
    }
    
    /**
     * Updates the status of the order.
     * @param newStatus The new status to set
     * @return true if status was successfully updated
     */
    public boolean updateStatus(String newStatus) {
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return false;
        }
        
        this.status = newStatus.trim();
        return true;
    }

    /**
     * Estimates print time in hours based on a naive function of dimensions and quantity.
     * For simplicity, parse numbers in dimensions formatted like "LxWxHcm" and use volume proxy.
     */
    public double estimatePrintTimeHours() {
        double length = 10;
        double width = 10;
        double height = 10;
        try {
            String dims = dimensions.toLowerCase().replace("cm", "").replace(" ", "");
            String[] parts = dims.split("x");
            if (parts.length == 3) {
                length = Double.parseDouble(parts[0]);
                width = Double.parseDouble(parts[1]);
                height = Double.parseDouble(parts[2]);
            }
        } catch (Exception ignored) {
        }
        double volumeProxy = length * width * height; // cubic cm proxy
        double timePerItemHours = Math.max(0.1, volumeProxy / 1000.0); // 1000 cc -> 1 hour baseline
        this.estimatedPrintHours = timePerItemHours * Math.max(1, quantity);
        return estimatedPrintHours;
    }
    
    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            return;
        }
        this.priority = priority.trim().toLowerCase();
    }

    public double getEstimatedPrintHours() {
        return estimatedPrintHours;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", material=" + (material != null ? material.getName() : "null") +
                ", dimensions='" + dimensions + '\'' +
                ", quantity=" + quantity +
                ", specialInstructions='" + specialInstructions + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", estHours=" + estimatedPrintHours +
                '}';
    }
}
