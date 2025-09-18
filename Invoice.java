import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an invoice for a 3D printing order.
 * Contains billing information and methods for generating and exporting invoices.
 */
public class Invoice {
    private int invoiceID;
    private Order order;
    private double totalCost;
    private LocalDateTime dateIssued;
    
    // Static counter for generating unique invoice IDs
    private static int nextInvoiceID = 2000;
    
    /**
     * Constructor for creating a new invoice.
     * @param order The order this invoice is for
     * @param totalCost The total cost of the order
     */
    public Invoice(Order order, double totalCost) {
        this.invoiceID = nextInvoiceID++;
        this.order = order;
        this.totalCost = totalCost;
        this.dateIssued = LocalDateTime.now();
    }
    
    /**
     * Default constructor.
     */
    public Invoice() {
        this.invoiceID = nextInvoiceID++;
        this.order = null;
        this.totalCost = 0.0;
        this.dateIssued = LocalDateTime.now();
    }
    
    /**
     * Generates a summary of the invoice.
     * @return A formatted string containing invoice summary
     */
    public String generateSummary() {
        if (order == null) {
            return "Invoice #" + invoiceID + " - No order associated";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateIssued.format(formatter);
        
        return String.format("""
                INVOICE SUMMARY
                ===============
                Invoice ID: %d
                Date Issued: %s
                Order ID: %d
                Customer: %s
                Material: %s
                Quantity: %d
                Total Cost: $%.2f
                Status: %s
                """,
                invoiceID,
                formattedDate,
                order.getOrderID(),
                order.getUser() != null ? order.getUser().getUsername() : "Unknown",
                order.getMaterial() != null ? order.getMaterial().getName() : "Unknown",
                order.getQuantity(),
                totalCost,
                order.getStatus()
        );
    }
    
    /**
     * Exports the invoice to a string format.
     * @return A formatted string representation of the complete invoice
     */
    public String exportInvoice() {
        if (order == null) {
            return "Invoice #" + invoiceID + " - No order associated";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateIssued.format(formatter);
        
        return String.format("""
                ========================================
                3D PRINTING SERVICE INVOICE
                ========================================
                
                Invoice ID: %d
                Date Issued: %s
                
                ORDER DETAILS:
                --------------
                Order ID: %d
                Customer: %s
                Email: %s
                Material: %s
                Dimensions: %s
                Quantity: %d
                Special Instructions: %s
                
                COST BREAKDOWN:
                ---------------
                Material Cost: $%.2f
                Base Setup Cost: $%.2f
                Total Cost: $%.2f
                
                ORDER STATUS: %s
                
                Thank you for choosing our 3D printing service!
                ========================================
                """,
                invoiceID,
                formattedDate,
                order.getOrderID(),
                order.getUser() != null ? order.getUser().getUsername() : "Unknown",
                order.getUser() != null ? order.getUser().getEmail() : "Unknown",
                order.getMaterial() != null ? order.getMaterial().getName() : "Unknown",
                order.getDimensions(),
                order.getQuantity(),
                order.getSpecialInstructions(),
                order.getMaterial() != null ? order.getMaterial().getCostPerGram() * order.getQuantity() * 10 : 0.0,
                SystemConfig.getBaseSetupCost(),
                totalCost,
                order.getStatus()
        );
    }
    
    // Getters and Setters
    public int getInvoiceID() {
        return invoiceID;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public LocalDateTime getDateIssued() {
        return dateIssued;
    }
    
    public void setDateIssued(LocalDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }
    
    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + invoiceID +
                ", order=" + (order != null ? order.getOrderID() : "null") +
                ", totalCost=" + totalCost +
                ", dateIssued=" + dateIssued +
                '}';
    }
}
