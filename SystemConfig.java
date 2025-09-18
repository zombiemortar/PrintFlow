/**
 * Manages system configuration including pricing constants, tax rates, and other settings.
 * Provides centralized access to system parameters that can be modified by administrators.
 */
public class SystemConfig {
    // Pricing constants
    private static double electricityCostPerHour = 0.15; // $0.15 per kWh
    private static double machineTimeCostPerHour = 2.50; // $2.50 per hour
    private static double baseSetupCost = 5.00; // $5.00 base setup cost
    
    // Tax rates
    private static double taxRate = 0.08; // 8% tax rate
    private static String currency = "USD"; // Default currency
    
    // System settings
    private static int maxOrderQuantity = 100; // Maximum items per order
    private static double maxOrderValue = 1000.00; // Maximum order value
    private static boolean allowRushOrders = true; // Whether rush orders are allowed
    private static double rushOrderSurcharge = 0.25; // 25% surcharge for rush orders
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods and variables.
     */
    private SystemConfig() {
        // Utility class - no instances needed
    }
    
    // Pricing constant getters and setters
    public static double getElectricityCostPerHour() {
        return electricityCostPerHour;
    }
    
    public static void setElectricityCostPerHour(double cost) {
        if (cost >= 0) {
            electricityCostPerHour = cost;
        }
    }
    
    public static double getMachineTimeCostPerHour() {
        return machineTimeCostPerHour;
    }
    
    public static void setMachineTimeCostPerHour(double cost) {
        if (cost >= 0) {
            machineTimeCostPerHour = cost;
        }
    }
    
    public static double getBaseSetupCost() {
        return baseSetupCost;
    }
    
    public static void setBaseSetupCost(double cost) {
        if (cost >= 0) {
            baseSetupCost = cost;
        }
    }
    
    // Tax rate getters and setters
    public static double getTaxRate() {
        return taxRate;
    }
    
    public static void setTaxRate(double rate) {
        if (rate >= 0 && rate <= 1) {
            taxRate = rate;
        }
    }
    
    public static String getCurrency() {
        return currency;
    }
    
    public static void setCurrency(String newCurrency) {
        if (newCurrency != null && !newCurrency.trim().isEmpty()) {
            currency = newCurrency.trim().toUpperCase();
        }
    }
    
    // System setting getters and setters
    public static int getMaxOrderQuantity() {
        return maxOrderQuantity;
    }
    
    public static void setMaxOrderQuantity(int maxQuantity) {
        if (maxQuantity > 0) {
            maxOrderQuantity = maxQuantity;
        }
    }
    
    public static double getMaxOrderValue() {
        return maxOrderValue;
    }
    
    public static void setMaxOrderValue(double maxValue) {
        if (maxValue > 0) {
            maxOrderValue = maxValue;
        }
    }
    
    public static boolean isAllowRushOrders() {
        return allowRushOrders;
    }
    
    public static void setAllowRushOrders(boolean allow) {
        allowRushOrders = allow;
    }
    
    public static double getRushOrderSurcharge() {
        return rushOrderSurcharge;
    }
    
    public static void setRushOrderSurcharge(double surcharge) {
        if (surcharge >= 0) {
            rushOrderSurcharge = surcharge;
        }
    }
    
    /**
     * Gets a summary of all current system configuration.
     * @return A formatted string containing all configuration values
     */
    public static String getConfigurationSummary() {
        return String.format("""
                SYSTEM CONFIGURATION SUMMARY
                =============================
                
                PRICING CONSTANTS:
                - Electricity Cost: $%.2f per hour
                - Machine Time Cost: $%.2f per hour
                - Base Setup Cost: $%.2f
                
                TAX & CURRENCY:
                - Tax Rate: %.1f%%
                - Currency: %s
                
                ORDER LIMITS:
                - Max Order Quantity: %d items
                - Max Order Value: $%.2f
                
                RUSH ORDER SETTINGS:
                - Rush Orders Allowed: %s
                - Rush Order Surcharge: %.1f%%
                """,
                electricityCostPerHour,
                machineTimeCostPerHour,
                baseSetupCost,
                taxRate * 100,
                currency,
                maxOrderQuantity,
                maxOrderValue,
                allowRushOrders ? "Yes" : "No",
                rushOrderSurcharge * 100
        );
    }
    
    /**
     * Resets all configuration values to their defaults.
     */
    public static void resetToDefaults() {
        electricityCostPerHour = 0.15;
        machineTimeCostPerHour = 2.50;
        baseSetupCost = 5.00;
        taxRate = 0.08;
        currency = "USD";
        maxOrderQuantity = 100;
        maxOrderValue = 1000.00;
        allowRushOrders = true;
        rushOrderSurcharge = 0.25;
    }
}
