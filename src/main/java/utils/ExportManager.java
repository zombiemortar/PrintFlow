package utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simplified export manager for CSV and JSON export functionality.
 * Consolidates export operations with reduced code duplication.
 */
public class ExportManager {
    
    private static final String EXPORTS_DIRECTORY = "exports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Ensures the exports directory exists.
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureExportsDirectory() {
        return FileManager.ensureDirectory(EXPORTS_DIRECTORY);
    }
    
    /**
     * Exports data to CSV format.
     * @param data The data to export
     * @param filename The filename (if null, generates timestamp-based name)
     * @param headers The CSV headers
     * @return true if export was successful
     */
    public static boolean exportToCSV(List<String[]> data, String filename, String[] headers) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "export_" + timestamp + ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            // Write headers
            if (headers != null && headers.length > 0) {
                writer.println(escapeCSVLine(headers));
            }
            
            // Write data
            for (String[] row : data) {
                writer.println(escapeCSVLine(row));
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports data to JSON format.
     * @param data The data to export
     * @param filename The filename (if null, generates timestamp-based name)
     * @param rootKey The root key for the JSON object
     * @return true if export was successful
     */
    public static boolean exportToJSON(List<Map<String, Object>> data, String filename, String rootKey) {
        if (data == null) {
            return false;
        }
        
        if (!ensureExportsDirectory()) {
            return false;
        }
        
        if (filename == null) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            filename = "export_" + timestamp + ".json";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(EXPORTS_DIRECTORY + File.separator + filename))) {
            writer.println("{");
            writer.println("  \"" + rootKey + "\": [");
            
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> item = data.get(i);
                writer.println("    {");
                
                int fieldIndex = 0;
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    writer.print("      \"" + entry.getKey() + "\": ");
                    
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        writer.print("\"" + escapeJsonString((String) value) + "\"");
                    } else if (value instanceof Number || value instanceof Boolean) {
                        writer.print(value);
                    } else {
                        writer.print("\"" + escapeJsonString(value.toString()) + "\"");
                    }
                    
                    if (fieldIndex < item.size() - 1) {
                        writer.print(",");
                    }
                    writer.println();
                    fieldIndex++;
                }
                
                writer.print("    }");
                if (i < data.size() - 1) {
                    writer.print(",");
                }
                writer.println();
            }
            
            writer.println("  ]");
            writer.println("}");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exports a single invoice to CSV format.
     * @param invoice The invoice to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoiceToCSV(Invoice invoice, String filename) {
        if (invoice == null) {
            return false;
        }
        
        List<Invoice> invoices = Arrays.asList(invoice);
        return exportInvoicesToCSV(invoices, filename);
    }
    
    /**
     * Exports a single invoice to JSON format.
     * @param invoice The invoice to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoiceToJSON(Invoice invoice, String filename) {
        if (invoice == null) {
            return false;
        }
        
        List<Invoice> invoices = Arrays.asList(invoice);
        return exportInvoicesToJSON(invoices, filename);
    }
    
    /**
     * Exports invoices to CSV format.
     * @param invoices The invoices to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoicesToCSV(List<Invoice> invoices, String filename) {
        if (invoices == null || invoices.isEmpty()) {
            return false;
        }
        
        String[] headers = {
            "Invoice ID", "Order ID", "Customer", "Email", "Material", "Dimensions", 
            "Quantity", "Total Cost", "Date Issued", "Status"
        };
        
        List<String[]> data = new ArrayList<>();
        for (Invoice invoice : invoices) {
            Order order = invoice.getOrder();
            String[] row = {
                String.valueOf(invoice.getInvoiceID()),
                String.valueOf(order.getOrderID()),
                order.getUser().getUsername(),
                order.getUser().getEmail(),
                order.getMaterial().getName(),
                order.getDimensions(),
                String.valueOf(order.getQuantity()),
                String.valueOf(invoice.getTotalCost()),
                invoice.getDateIssued().toString(),
                order.getStatus()
            };
            data.add(row);
        }
        
        return exportToCSV(data, filename, headers);
    }
    
    /**
     * Exports invoices to JSON format.
     * @param invoices The invoices to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoicesToJSON(List<Invoice> invoices, String filename) {
        if (invoices == null) {
            return false;
        }
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Invoice invoice : invoices) {
            Order order = invoice.getOrder();
            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("invoiceID", invoice.getInvoiceID());
            invoiceData.put("orderID", order.getOrderID());
            invoiceData.put("customer", order.getUser().getUsername());
            invoiceData.put("email", order.getUser().getEmail());
            invoiceData.put("material", order.getMaterial().getName());
            invoiceData.put("dimensions", order.getDimensions());
            invoiceData.put("quantity", order.getQuantity());
            invoiceData.put("totalCost", invoice.getTotalCost());
            invoiceData.put("dateIssued", invoice.getDateIssued().toString());
            invoiceData.put("status", order.getStatus());
            data.add(invoiceData);
        }
        
        return exportToJSON(data, filename, "invoices");
    }
    
    /**
     * Exports invoices to CSV format (overload for Invoice array).
     * @param invoices The invoices to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoicesToCSV(Invoice[] invoices, String filename) {
        if (invoices == null || invoices.length == 0) {
            return false;
        }
        
        List<Invoice> invoiceList = Arrays.asList(invoices);
        return exportInvoicesToCSV(invoiceList, filename);
    }
    
    /**
     * Exports invoices to JSON format (overload for Invoice array).
     * @param invoices The invoices to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportInvoicesToJSON(Invoice[] invoices, String filename) {
        if (invoices == null || invoices.length == 0) {
            return false;
        }
        
        List<Invoice> invoiceList = Arrays.asList(invoices);
        return exportInvoicesToJSON(invoiceList, filename);
    }
    
    /**
     * Exports orders to CSV format.
     * @param orders The orders to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportOrdersToCSV(List<Order> orders, String filename) {
        if (orders == null || orders.isEmpty()) {
            return false;
        }
        
        String[] headers = {
            "Order ID", "Customer", "Email", "Role", "Material", "Cost Per Gram",
            "Print Temp", "Color", "Dimensions", "Quantity", "Instructions", 
            "Status", "Priority", "Estimated Hours"
        };
        
        List<String[]> data = new ArrayList<>();
        for (Order order : orders) {
            String[] row = {
                String.valueOf(order.getOrderID()),
                order.getUser().getUsername(),
                order.getUser().getEmail(),
                order.getUser().getRole(),
                order.getMaterial().getName(),
                String.valueOf(order.getMaterial().getCostPerGram()),
                String.valueOf(order.getMaterial().getPrintTemp()),
                order.getMaterial().getColor(),
                order.getDimensions(),
                String.valueOf(order.getQuantity()),
                order.getSpecialInstructions(),
                order.getStatus(),
                order.getPriority(),
                String.valueOf(order.estimatePrintTimeHours())
            };
            data.add(row);
        }
        
        return exportToCSV(data, filename, headers);
    }
    
    /**
     * Exports orders to JSON format.
     * @param orders The orders to export
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportOrdersToJSON(List<Order> orders, String filename) {
        if (orders == null) {
            return false;
        }
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderID", order.getOrderID());
            orderData.put("customer", order.getUser().getUsername());
            orderData.put("email", order.getUser().getEmail());
            orderData.put("role", order.getUser().getRole());
            orderData.put("material", order.getMaterial().getName());
            orderData.put("costPerGram", order.getMaterial().getCostPerGram());
            orderData.put("printTemp", order.getMaterial().getPrintTemp());
            orderData.put("color", order.getMaterial().getColor());
            orderData.put("dimensions", order.getDimensions());
            orderData.put("quantity", order.getQuantity());
            orderData.put("instructions", order.getSpecialInstructions());
            orderData.put("status", order.getStatus());
            orderData.put("priority", order.getPriority());
            orderData.put("estimatedHours", order.estimatePrintTimeHours());
            data.add(orderData);
        }
        
        return exportToJSON(data, filename, "orders");
    }
    
    /**
     * Exports all orders to CSV format (overload with filename only).
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportOrdersToCSV(String filename) {
        Order[] orders = OrderManager.getAllOrders();
        List<Order> orderList = Arrays.asList(orders);
        return exportOrdersToCSV(orderList, filename);
    }
    
    /**
     * Exports all orders to JSON format (overload with filename only).
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportOrdersToJSON(String filename) {
        Order[] orders = OrderManager.getAllOrders();
        List<Order> orderList = Arrays.asList(orders);
        return exportOrdersToJSON(orderList, filename);
    }
    
    /**
     * Exports system report to CSV format.
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportSystemReportToCSV(String filename) {
        String[] headers = {
            "Metric", "Value", "Description"
        };
        
        List<String[]> data = new ArrayList<>();
        
        // System statistics
        Order[] orders = OrderManager.getAllOrders();
        data.add(new String[]{"Total Orders", String.valueOf(orders.length), "Total number of orders in system"});
        
        List<Material> materials = Arrays.asList(DataManager.getAllMaterials());
        data.add(new String[]{"Total Materials", String.valueOf(materials.size()), "Number of available materials"});
        
        List<User> users = Arrays.asList(DataManager.getAllUsers());
        data.add(new String[]{"Total Users", String.valueOf(users.size()), "Number of registered users"});
        
        // Calculate totals
        double totalRevenue = 0;
        int pendingOrders = 0;
        int processingOrders = 0;
        int completedOrders = 0;
        
        for (Order order : orders) {
            totalRevenue += order.calculatePrice();
            switch (order.getStatus()) {
                case "pending":
                    pendingOrders++;
                    break;
                case "processing":
                    processingOrders++;
                    break;
                case "completed":
                    completedOrders++;
                    break;
            }
        }
        
        data.add(new String[]{"Total Revenue", String.valueOf(totalRevenue), "Total revenue from all orders"});
        data.add(new String[]{"Pending Orders", String.valueOf(pendingOrders), "Orders waiting to be processed"});
        data.add(new String[]{"Processing Orders", String.valueOf(processingOrders), "Orders currently being processed"});
        data.add(new String[]{"Completed Orders", String.valueOf(completedOrders), "Orders that have been completed"});
        
        return exportToCSV(data, filename, headers);
    }
    
    /**
     * Exports system report to JSON format.
     * @param filename Optional filename
     * @return true if export was successful
     */
    public static boolean exportSystemReportToJSON(String filename) {
        Map<String, Object> reportData = new HashMap<>();
        
        // System statistics
        Order[] orders = OrderManager.getAllOrders();
        List<Material> materials = Arrays.asList(DataManager.getAllMaterials());
        List<User> users = Arrays.asList(DataManager.getAllUsers());
        
        reportData.put("totalOrders", orders.length);
        reportData.put("totalMaterials", materials.size());
        reportData.put("totalUsers", users.size());
        
        // Calculate totals
        double totalRevenue = 0;
        int pendingOrders = 0;
        int processingOrders = 0;
        int completedOrders = 0;
        
        for (Order order : orders) {
            totalRevenue += order.calculatePrice();
            switch (order.getStatus()) {
                case "pending":
                    pendingOrders++;
                    break;
                case "processing":
                    processingOrders++;
                    break;
                case "completed":
                    completedOrders++;
                    break;
            }
        }
        
        reportData.put("totalRevenue", totalRevenue);
        reportData.put("pendingOrders", pendingOrders);
        reportData.put("processingOrders", processingOrders);
        reportData.put("completedOrders", completedOrders);
        reportData.put("generatedAt", LocalDateTime.now().toString());
        
        List<Map<String, Object>> data = Arrays.asList(reportData);
        return exportToJSON(data, filename, "systemReport");
    }
    
    /**
     * Escapes a CSV line by properly handling commas and quotes.
     * @param fields The fields to escape
     * @return The escaped CSV line
     */
    private static String escapeCSVLine(String[] fields) {
        StringBuilder line = new StringBuilder();
        
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i] != null ? fields[i] : "";
            
            // Escape quotes by doubling them
            field = field.replace("\"", "\"\"");
            
            // Wrap in quotes if field contains comma, quote, or newline
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                field = "\"" + field + "\"";
            }
            
            line.append(field);
            if (i < fields.length - 1) {
                line.append(",");
            }
        }
        
        return line.toString();
    }
    
    /**
     * Escapes a JSON string by properly handling quotes and special characters.
     * @param str The string to escape
     * @return The escaped JSON string
     */
    private static String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\b", "\\b")
                 .replace("\f", "\\f")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * Gets the exports directory path.
     * @return Exports directory path
     */
    public static String getExportsDirectory() {
        return EXPORTS_DIRECTORY;
    }
    
    /**
     * Lists all export files.
     * @return Array of export filenames
     */
    public static String[] listExportFiles() {
        return FileManager.listFiles(EXPORTS_DIRECTORY);
    }
    
    /**
     * Lists all exported files (alias for listExportFiles).
     * @return Array of export filenames
     */
    public static String[] listExportedFiles() {
        return listExportFiles();
    }
    
    /**
     * Gets the full file path for an export file.
     * @param filename The filename
     * @return Full file path
     */
    public static String getExportFilePath(String filename) {
        return EXPORTS_DIRECTORY + File.separator + filename;
    }
    
    /**
     * Gets the current timestamp formatted for filenames.
     * @return Formatted timestamp string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
}