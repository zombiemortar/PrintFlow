import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Central manager for CSV and JSON export functionality.
 * Provides methods to export invoices, reports, and order summaries in various formats.
 */
public class ExportManager {
    
    private static final String EXPORTS_DIRECTORY = "exports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Ensures the exports directory exists, creating it if necessary.
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureExportsDirectory() {
        try {
            Path exportsPath = Paths.get(EXPORTS_DIRECTORY);
            if (!Files.exists(exportsPath)) {
                Files.createDirectories(exportsPath);
            }
            return Files.exists(exportsPath) && Files.isDirectory(exportsPath);
        } catch (IOException e) {
            System.err.println("Error creating exports directory: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports a single invoice to CSV format.
     * @param invoice The invoice to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoiceToCSV(Invoice invoice, String filename) {
        if (invoice == null) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "invoice_" + invoice.getInvoiceID() + "_" + timestamp + ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            // CSV Header
            writer.println("Invoice ID,Order ID,Customer Username,Customer Email,Material Name,Material Cost Per Gram,Material Print Temp,Material Color,Dimensions,Quantity,Special Instructions,Status,Priority,Estimated Print Hours,Total Cost,Date Issued");
            
            // CSV Data
            Order order = invoice.getOrder();
            if (order != null) {
                writer.printf("%d,%d,%s,%s,%s,%.4f,%d,%s,%s,%d,%s,%s,%s,%.2f,%.2f,%s%n",
                    invoice.getInvoiceID(),
                    order.getOrderID(),
                    escapeCSV(order.getUser() != null ? order.getUser().getUsername() : ""),
                    escapeCSV(order.getUser() != null ? order.getUser().getEmail() : ""),
                    escapeCSV(order.getMaterial() != null ? order.getMaterial().getName() : ""),
                    order.getMaterial() != null ? order.getMaterial().getCostPerGram() : 0.0,
                    order.getMaterial() != null ? order.getMaterial().getPrintTemp() : 0,
                    escapeCSV(order.getMaterial() != null ? order.getMaterial().getColor() : ""),
                    escapeCSV(order.getDimensions()),
                    order.getQuantity(),
                    escapeCSV(order.getSpecialInstructions()),
                    escapeCSV(order.getStatus()),
                    escapeCSV(order.getPriority()),
                    order.getEstimatedPrintHours(),
                    invoice.getTotalCost(),
                    invoice.getDateIssued().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting invoice to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports a single invoice to JSON format.
     * @param invoice The invoice to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoiceToJSON(Invoice invoice, String filename) {
        if (invoice == null) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "invoice_" + invoice.getInvoiceID() + "_" + timestamp + ".json";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            writer.println("{");
            writer.println("  \"invoice\": {");
            writer.printf("    \"invoiceID\": %d,%n", invoice.getInvoiceID());
            writer.printf("    \"totalCost\": %.2f,%n", invoice.getTotalCost());
            writer.printf("    \"dateIssued\": \"%s\",%n", invoice.getDateIssued().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            Order order = invoice.getOrder();
            if (order != null) {
                writer.println("    \"order\": {");
                writer.printf("      \"orderID\": %d,%n", order.getOrderID());
                writer.printf("      \"dimensions\": \"%s\",%n", escapeJSON(order.getDimensions()));
                writer.printf("      \"quantity\": %d,%n", order.getQuantity());
                writer.printf("      \"specialInstructions\": \"%s\",%n", escapeJSON(order.getSpecialInstructions()));
                writer.printf("      \"status\": \"%s\",%n", escapeJSON(order.getStatus()));
                writer.printf("      \"priority\": \"%s\",%n", escapeJSON(order.getPriority()));
                writer.printf("      \"estimatedPrintHours\": %.2f,%n", order.getEstimatedPrintHours());
                
                if (order.getUser() != null) {
                    writer.println("      \"user\": {");
                    writer.printf("        \"username\": \"%s\",%n", escapeJSON(order.getUser().getUsername()));
                    writer.printf("        \"email\": \"%s\",%n", escapeJSON(order.getUser().getEmail()));
                    writer.printf("        \"role\": \"%s\"%n", escapeJSON(order.getUser().getRole()));
                    writer.println("      },");
                }
                
                if (order.getMaterial() != null) {
                    writer.println("      \"material\": {");
                    writer.printf("        \"name\": \"%s\",%n", escapeJSON(order.getMaterial().getName()));
                    writer.printf("        \"costPerGram\": %.4f,%n", order.getMaterial().getCostPerGram());
                    writer.printf("        \"printTemp\": %d,%n", order.getMaterial().getPrintTemp());
                    writer.printf("        \"color\": \"%s\"%n", escapeJSON(order.getMaterial().getColor()));
                    writer.println("      }");
                }
                
                writer.println("    }");
            } else {
                writer.println("    \"order\": null");
            }
            
            writer.println("  }");
            writer.println("}");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting invoice to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports all orders to CSV format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportOrdersToCSV(String filename) {
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "orders_export_" + timestamp + ".csv";
        }
        
        Order[] orders = OrderManager.getAllOrders();
        if (orders.length == 0) {
            return DataFileManager.writeToFile(filename, "");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            // CSV Header
            writer.println("Order ID,Username,Email,Role,Material Name,Material Cost Per Gram,Material Print Temp,Material Color,Dimensions,Quantity,Special Instructions,Status,Priority,Estimated Print Hours,Calculated Price");
            
            // CSV Data
            for (Order order : orders) {
                writer.printf("%d,%s,%s,%s,%s,%.4f,%d,%s,%s,%d,%s,%s,%s,%.2f,%.2f%n",
                    order.getOrderID(),
                    escapeCSV(order.getUser() != null ? order.getUser().getUsername() : ""),
                    escapeCSV(order.getUser() != null ? order.getUser().getEmail() : ""),
                    escapeCSV(order.getUser() != null ? order.getUser().getRole() : ""),
                    escapeCSV(order.getMaterial() != null ? order.getMaterial().getName() : ""),
                    order.getMaterial() != null ? order.getMaterial().getCostPerGram() : 0.0,
                    order.getMaterial() != null ? order.getMaterial().getPrintTemp() : 0,
                    escapeCSV(order.getMaterial() != null ? order.getMaterial().getColor() : ""),
                    escapeCSV(order.getDimensions()),
                    order.getQuantity(),
                    escapeCSV(order.getSpecialInstructions()),
                    escapeCSV(order.getStatus()),
                    escapeCSV(order.getPriority()),
                    order.getEstimatedPrintHours(),
                    order.calculatePrice()
                );
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting orders to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports all orders to JSON format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportOrdersToJSON(String filename) {
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "orders_export_" + timestamp + ".json";
        }
        
        Order[] orders = OrderManager.getAllOrders();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            writer.println("{");
            writer.println("  \"orders\": [");
            
            for (int i = 0; i < orders.length; i++) {
                Order order = orders[i];
                writer.println("    {");
                writer.printf("      \"orderID\": %d,%n", order.getOrderID());
                writer.printf("      \"dimensions\": \"%s\",%n", escapeJSON(order.getDimensions()));
                writer.printf("      \"quantity\": %d,%n", order.getQuantity());
                writer.printf("      \"specialInstructions\": \"%s\",%n", escapeJSON(order.getSpecialInstructions()));
                writer.printf("      \"status\": \"%s\",%n", escapeJSON(order.getStatus()));
                writer.printf("      \"priority\": \"%s\",%n", escapeJSON(order.getPriority()));
                writer.printf("      \"estimatedPrintHours\": %.2f,%n", order.getEstimatedPrintHours());
                writer.printf("      \"calculatedPrice\": %.2f,%n", order.calculatePrice());
                
                if (order.getUser() != null) {
                    writer.println("      \"user\": {");
                    writer.printf("        \"username\": \"%s\",%n", escapeJSON(order.getUser().getUsername()));
                    writer.printf("        \"email\": \"%s\",%n", escapeJSON(order.getUser().getEmail()));
                    writer.printf("        \"role\": \"%s\"%n", escapeJSON(order.getUser().getRole()));
                    writer.println("      },");
                }
                
                if (order.getMaterial() != null) {
                    writer.println("      \"material\": {");
                    writer.printf("        \"name\": \"%s\",%n", escapeJSON(order.getMaterial().getName()));
                    writer.printf("        \"costPerGram\": %.4f,%n", order.getMaterial().getCostPerGram());
                    writer.printf("        \"printTemp\": %d,%n", order.getMaterial().getPrintTemp());
                    writer.printf("        \"color\": \"%s\"%n", escapeJSON(order.getMaterial().getColor()));
                    writer.println("      }");
                }
                
                if (i < orders.length - 1) {
                    writer.println("    },");
                } else {
                    writer.println("    }");
                }
            }
            
            writer.println("  ]");
            writer.println("}");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting orders to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports system report to CSV format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportSystemReportToCSV(String filename) {
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "system_report_" + timestamp + ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            // System Statistics Header
            writer.println("Report Type,Value,Description");
            
            Map<String, Object> stats = FileHandlingManager.getSystemStatistics();
            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                writer.printf("%s,%s,%s%n",
                    escapeCSV(entry.getKey()),
                    escapeCSV(entry.getValue().toString()),
                    escapeCSV(getStatDescription(entry.getKey()))
                );
            }
            
            // Materials Summary
            writer.println();
            writer.println("Material Name,Stock Grams,Cost Per Gram,Print Temperature,Color");
            Material[] materials = MaterialFileHandler.getAllMaterials();
            for (Material material : materials) {
                int stock = InventoryFileHandler.getStock(material.getName());
                writer.printf("%s,%d,%.4f,%d,%s%n",
                    escapeCSV(material.getName()),
                    stock,
                    material.getCostPerGram(),
                    material.getPrintTemp(),
                    escapeCSV(material.getColor())
                );
            }
            
            // Orders Summary
            writer.println();
            writer.println("Order ID,Status,Quantity,Priority,Estimated Hours,Price");
            Order[] orders = OrderManager.getAllOrders();
            for (Order order : orders) {
                writer.printf("%d,%s,%d,%s,%.2f,%.2f%n",
                    order.getOrderID(),
                    escapeCSV(order.getStatus()),
                    order.getQuantity(),
                    escapeCSV(order.getPriority()),
                    order.getEstimatedPrintHours(),
                    order.calculatePrice()
                );
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting system report to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports system report to JSON format.
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportSystemReportToJSON(String filename) {
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "system_report_" + timestamp + ".json";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            writer.println("{");
            writer.printf("  \"reportGenerated\": \"%s\",%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // System Statistics
            writer.println("  \"systemStatistics\": {");
            Map<String, Object> stats = FileHandlingManager.getSystemStatistics();
            boolean first = true;
            for (Map.Entry<String, Object> entry : stats.entrySet()) {
                if (!first) writer.println(",");
                writer.printf("    \"%s\": %s", escapeJSON(entry.getKey()), formatJSONValue(entry.getValue()));
                first = false;
            }
            writer.println();
            writer.println("  },");
            
            // Materials
            writer.println("  \"materials\": [");
            Material[] materials = MaterialFileHandler.getAllMaterials();
            for (int i = 0; i < materials.length; i++) {
                Material material = materials[i];
                int stock = InventoryFileHandler.getStock(material.getName());
                writer.println("    {");
                writer.printf("      \"name\": \"%s\",%n", escapeJSON(material.getName()));
                writer.printf("      \"costPerGram\": %.4f,%n", material.getCostPerGram());
                writer.printf("      \"printTemp\": %d,%n", material.getPrintTemp());
                writer.printf("      \"color\": \"%s\",%n", escapeJSON(material.getColor()));
                writer.printf("      \"stockGrams\": %d%n", stock);
                if (i < materials.length - 1) {
                    writer.println("    },");
                } else {
                    writer.println("    }");
                }
            }
            writer.println("  ],");
            
            // Orders
            writer.println("  \"orders\": [");
            Order[] orders = OrderManager.getAllOrders();
            for (int i = 0; i < orders.length; i++) {
                Order order = orders[i];
                writer.println("    {");
                writer.printf("      \"orderID\": %d,%n", order.getOrderID());
                writer.printf("      \"status\": \"%s\",%n", escapeJSON(order.getStatus()));
                writer.printf("      \"quantity\": %d,%n", order.getQuantity());
                writer.printf("      \"priority\": \"%s\",%n", escapeJSON(order.getPriority()));
                writer.printf("      \"estimatedPrintHours\": %.2f,%n", order.getEstimatedPrintHours());
                writer.printf("      \"calculatedPrice\": %.2f%n", order.calculatePrice());
                if (i < orders.length - 1) {
                    writer.println("    },");
                } else {
                    writer.println("    }");
                }
            }
            writer.println("  ]");
            
            writer.println("}");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting system report to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports all invoices to CSV format.
     * @param invoices Array of invoices to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoicesToCSV(Invoice[] invoices, String filename) {
        if (invoices == null || invoices.length == 0) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "invoices_export_" + timestamp + ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            // CSV Header
            writer.println("Invoice ID,Order ID,Customer Username,Customer Email,Material Name,Material Cost Per Gram,Material Print Temp,Material Color,Dimensions,Quantity,Special Instructions,Status,Priority,Estimated Print Hours,Total Cost,Date Issued");
            
            // CSV Data
            for (Invoice invoice : invoices) {
                Order order = invoice.getOrder();
                if (order != null) {
                    writer.printf("%d,%d,%s,%s,%s,%.4f,%d,%s,%s,%d,%s,%s,%s,%.2f,%.2f,%s%n",
                        invoice.getInvoiceID(),
                        order.getOrderID(),
                        escapeCSV(order.getUser() != null ? order.getUser().getUsername() : ""),
                        escapeCSV(order.getUser() != null ? order.getUser().getEmail() : ""),
                        escapeCSV(order.getMaterial() != null ? order.getMaterial().getName() : ""),
                        order.getMaterial() != null ? order.getMaterial().getCostPerGram() : 0.0,
                        order.getMaterial() != null ? order.getMaterial().getPrintTemp() : 0,
                        escapeCSV(order.getMaterial() != null ? order.getMaterial().getColor() : ""),
                        escapeCSV(order.getDimensions()),
                        order.getQuantity(),
                        escapeCSV(order.getSpecialInstructions()),
                        escapeCSV(order.getStatus()),
                        escapeCSV(order.getPriority()),
                        order.getEstimatedPrintHours(),
                        invoice.getTotalCost(),
                        invoice.getDateIssued().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    );
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting invoices to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports all invoices to JSON format.
     * @param invoices Array of invoices to export
     * @param filename Optional filename (if null, generates timestamp-based name)
     * @return true if export was successful
     */
    public static boolean exportInvoicesToJSON(Invoice[] invoices, String filename) {
        if (invoices == null || invoices.length == 0) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "invoices_export_" + timestamp + ".json";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            writer.println("{");
            writer.println("  \"invoices\": [");
            
            for (int i = 0; i < invoices.length; i++) {
                Invoice invoice = invoices[i];
                writer.println("    {");
                writer.printf("      \"invoiceID\": %d,%n", invoice.getInvoiceID());
                writer.printf("      \"totalCost\": %.2f,%n", invoice.getTotalCost());
                writer.printf("      \"dateIssued\": \"%s\",%n", invoice.getDateIssued().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                Order order = invoice.getOrder();
                if (order != null) {
                    writer.println("      \"order\": {");
                    writer.printf("        \"orderID\": %d,%n", order.getOrderID());
                    writer.printf("        \"dimensions\": \"%s\",%n", escapeJSON(order.getDimensions()));
                    writer.printf("        \"quantity\": %d,%n", order.getQuantity());
                    writer.printf("        \"specialInstructions\": \"%s\",%n", escapeJSON(order.getSpecialInstructions()));
                    writer.printf("        \"status\": \"%s\",%n", escapeJSON(order.getStatus()));
                    writer.printf("        \"priority\": \"%s\",%n", escapeJSON(order.getPriority()));
                    writer.printf("        \"estimatedPrintHours\": %.2f,%n", order.getEstimatedPrintHours());
                    
                    if (order.getUser() != null) {
                        writer.println("        \"user\": {");
                        writer.printf("          \"username\": \"%s\",%n", escapeJSON(order.getUser().getUsername()));
                        writer.printf("          \"email\": \"%s\",%n", escapeJSON(order.getUser().getEmail()));
                        writer.printf("          \"role\": \"%s\"%n", escapeJSON(order.getUser().getRole()));
                        writer.println("        },");
                    }
                    
                    if (order.getMaterial() != null) {
                        writer.println("        \"material\": {");
                        writer.printf("          \"name\": \"%s\",%n", escapeJSON(order.getMaterial().getName()));
                        writer.printf("          \"costPerGram\": %.4f,%n", order.getMaterial().getCostPerGram());
                        writer.printf("          \"printTemp\": %d,%n", order.getMaterial().getPrintTemp());
                        writer.printf("          \"color\": \"%s\"%n", escapeJSON(order.getMaterial().getColor()));
                        writer.println("        }");
                    }
                    
                    writer.println("      }");
                } else {
                    writer.println("      \"order\": null");
                }
                
                if (i < invoices.length - 1) {
                    writer.println("    },");
                } else {
                    writer.println("    }");
                }
            }
            
            writer.println("  ]");
            writer.println("}");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting invoices to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lists all exported files in the exports directory.
     * @return Array of exported filenames
     */
    public static String[] listExportedFiles() {
        if (!ensureExportsDirectory()) {
            return new String[0];
        }
        
        try {
            Path exportsPath = Paths.get(EXPORTS_DIRECTORY);
            return Files.list(exportsPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.err.println("Error listing exported files: " + e.getMessage());
            return new String[0];
        }
    }
    
    /**
     * Gets the full path to an exported file.
     * @param filename The name of the exported file
     * @return The full path as a string
     */
    public static String getExportFilePath(String filename) {
        return EXPORTS_DIRECTORY + File.separator + filename;
    }
    
    // Helper methods
    
    /**
     * Escapes a string for CSV format.
     * @param value The string to escape
     * @return The escaped string
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Escapes a string for JSON format.
     * @param value The string to escape
     * @return The escaped string
     */
    private static String escapeJSON(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Formats a value for JSON output.
     * @param value The value to format
     * @return The formatted JSON value
     */
    private static String formatJSONValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escapeJSON((String) value) + "\"";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return "\"" + escapeJSON(value.toString()) + "\"";
    }
    
    /**
     * Gets a description for a system statistic key.
     * @param key The statistic key
     * @return A description of the statistic
     */
    private static String getStatDescription(String key) {
        switch (key) {
            case "materials_count": return "Total number of materials in system";
            case "users_count": return "Total number of users in system";
            case "inventory_items_count": return "Total number of inventory items";
            case "orders_count": return "Total number of orders";
            case "queue_size": return "Number of orders in print queue";
            case "total_inventory_value": return "Total value of inventory in dollars";
            case "customer_count": return "Number of customer users";
            case "admin_count": return "Number of admin users";
            case "vip_count": return "Number of VIP users";
            default: return "System statistic";
        }
    }
    
    /**
     * Main method for testing the export manager.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== EXPORT MANAGER TEST ===");
        
        // Initialize system with test data
        FileHandlingManager.initializeWithDefaultData();
        
        // Create some test orders and invoices
        User testUser = new User("test_user", "test@example.com", "customer", "TestPass123!");
        Material testMaterial = new Material("Test PLA", 0.05, 200, "Blue");
        Order testOrder = new Order(testUser, testMaterial, "10x10x5cm", 2, "Test order");
        Invoice testInvoice = new Invoice(testOrder, testOrder.calculatePrice());
        
        OrderManager.registerOrder(testOrder);
        
        // Test CSV exports
        System.out.println("Testing CSV exports...");
        boolean csvInvoice = exportInvoiceToCSV(testInvoice, "test_invoice.csv");
        System.out.println("Invoice CSV export: " + (csvInvoice ? "SUCCESS" : "FAILED"));
        
        boolean csvOrders = exportOrdersToCSV("test_orders.csv");
        System.out.println("Orders CSV export: " + (csvOrders ? "SUCCESS" : "FAILED"));
        
        boolean csvReport = exportSystemReportToCSV("test_report.csv");
        System.out.println("System report CSV export: " + (csvReport ? "SUCCESS" : "FAILED"));
        
        // Test JSON exports
        System.out.println("Testing JSON exports...");
        boolean jsonInvoice = exportInvoiceToJSON(testInvoice, "test_invoice.json");
        System.out.println("Invoice JSON export: " + (jsonInvoice ? "SUCCESS" : "FAILED"));
        
        boolean jsonOrders = exportOrdersToJSON("test_orders.json");
        System.out.println("Orders JSON export: " + (jsonOrders ? "SUCCESS" : "FAILED"));
        
        boolean jsonReport = exportSystemReportToJSON("test_report.json");
        System.out.println("System report JSON export: " + (jsonReport ? "SUCCESS" : "FAILED"));
        
        // List exported files
        String[] exportedFiles = listExportedFiles();
        System.out.println("\nExported files:");
        for (String file : exportedFiles) {
            System.out.println("  " + file);
        }
        
        System.out.println("\nExport Manager test completed!");
    }
}
