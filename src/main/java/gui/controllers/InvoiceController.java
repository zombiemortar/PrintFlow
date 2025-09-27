package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Invoice;
import model.Order;
import service.OrderManager;
import util.ExportManager;

import java.util.Map;

/**
 * Controller for the Invoice management screen.
 * Handles order search, invoice generation, and export functionality.
 */
public class InvoiceController implements SceneNavigator.WithParams {

    @FXML private Label userLabel;
    @FXML private TextField orderIdField;
    @FXML private Button searchButton;
    @FXML private Button showAllOrdersButton;
    
    @FXML private VBox invoiceDisplaySection;
    @FXML private ScrollPane invoiceScrollPane;
    @FXML private TextArea invoiceTextArea;
    @FXML private Button exportCsvButton;
    @FXML private Button exportJsonButton;
    @FXML private Button clearButton;
    
    @FXML private VBox allOrdersSection;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> customerColumn;
    @FXML private TableColumn<Order, String> materialColumn;
    @FXML private TableColumn<Order, Integer> quantityColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, Double> totalCostColumn;
    @FXML private Button generateInvoiceButton;
    @FXML private Button hideOrdersButton;
    
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SceneNavigator navigator;
    private String currentUser;
    private Invoice currentInvoice;
    private ObservableList<Order> ordersList;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // Initialize table columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        customerColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getUser() != null ? order.getUser().getUsername() : "Unknown"
            );
        });
        materialColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getMaterial() != null ? order.getMaterial().getName() : "Unknown"
            );
        });
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleDoubleProperty(order.calculatePrice()).asObject();
        });
        
        // Initialize orders list
        ordersList = FXCollections.observableArrayList();
        ordersTable.setItems(ordersList);
        
        // Set up table selection listener
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            generateInvoiceButton.setDisable(newSelection == null);
        });
        
        // Initially disable buttons
        generateInvoiceButton.setDisable(true);
        exportCsvButton.setDisable(true);
        exportJsonButton.setDisable(true);
    }

    @FXML
    public void onSearchOrder() {
        String orderIdText = orderIdField.getText().trim();
        
        if (orderIdText.isEmpty()) {
            AlertUtil.showError("Input Error", "Please enter an Order ID to search.");
            return;
        }
        
        // Simple validation for order ID
        try {
            int orderId = Integer.parseInt(orderIdText);
            if (orderId <= 0) {
                AlertUtil.showError("Input Error", "Order ID must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Error", "Please enter a valid numeric Order ID.");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(orderIdText);
            Order order = OrderManager.getOrderById(orderId);
            
            if (order == null) {
                AlertUtil.showError("Order Not Found", "No order found with ID: " + orderId);
                return;
            }
            
            // Generate invoice for the order
            generateAndDisplayInvoice(order);
            
        } catch (NumberFormatException e) {
            AlertUtil.showError("Input Error", "Please enter a valid numeric Order ID.");
        }
    }

    @FXML
    public void onShowAllOrders() {
        statusLabel.setText("Loading all orders...");
        
        // Get all orders from OrderManager
        Order[] allOrders = OrderManager.getAllOrders();
        
        if (allOrders.length == 0) {
            AlertUtil.showInfo("No Orders", "No orders found in the system.");
            statusLabel.setText("Ready");
            return;
        }
        
        // Update the orders list
        ordersList.clear();
        ordersList.addAll(allOrders);
        
        // Show the orders table and hide invoice display
        allOrdersSection.setVisible(true);
        invoiceDisplaySection.setVisible(false);
        
        statusLabel.setText("Loaded " + allOrders.length + " orders");
    }

    @FXML
    public void onGenerateInvoice() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        
        if (selectedOrder == null) {
            AlertUtil.showError("Selection Error", "Please select an order from the table.");
            return;
        }
        
        generateAndDisplayInvoice(selectedOrder);
    }

    @FXML
    public void onExportCSV() {
        if (currentInvoice == null) {
            AlertUtil.showError("Export Error", "No invoice to export. Please search for an order first.");
            return;
        }
        
        try {
            String filename = "invoice_" + currentInvoice.getInvoiceID() + "_" + ExportManager.getCurrentTimestamp() + ".csv";
            boolean success = ExportManager.exportInvoiceToCSV(currentInvoice, filename);
            if (success) {
                AlertUtil.showInfo("Export Successful", "Invoice exported to CSV successfully.\nFile: " + filename);
            } else {
                AlertUtil.showError("Export Failed", "Failed to export invoice to CSV.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Error exporting to CSV: " + e.getMessage());
        }
    }

    @FXML
    public void onExportJSON() {
        if (currentInvoice == null) {
            AlertUtil.showError("Export Error", "No invoice to export. Please search for an order first.");
            return;
        }
        
        try {
            String filename = "invoice_" + currentInvoice.getInvoiceID() + "_" + ExportManager.getCurrentTimestamp() + ".json";
            boolean success = ExportManager.exportInvoiceToJSON(currentInvoice, filename);
            if (success) {
                AlertUtil.showInfo("Export Successful", "Invoice exported to JSON successfully.\nFile: " + filename);
            } else {
                AlertUtil.showError("Export Failed", "Failed to export invoice to JSON.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Error exporting to JSON: " + e.getMessage());
        }
    }

    @FXML
    public void onClear() {
        invoiceTextArea.clear();
        orderIdField.clear();
        currentInvoice = null;
        invoiceDisplaySection.setVisible(false);
        exportCsvButton.setDisable(true);
        exportJsonButton.setDisable(true);
        statusLabel.setText("Ready");
    }

    @FXML
    public void onHideOrders() {
        allOrdersSection.setVisible(false);
        ordersTable.getSelectionModel().clearSelection();
        generateInvoiceButton.setDisable(true);
        statusLabel.setText("Ready");
    }

    @FXML
    public void onBackToDashboard() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
            userLabel.setText("User: " + currentUser);
        } else {
            userLabel.setText("User: Unknown");
        }
    }

    /**
     * Generates an invoice for the given order and displays it.
     * @param order The order to generate an invoice for
     */
    private void generateAndDisplayInvoice(Order order) {
        try {
            // Calculate total cost
            double totalCost = order.calculatePrice();
            
            // Create invoice
            currentInvoice = new Invoice(order, totalCost);
            
            // Display invoice summary
            String invoiceSummary = currentInvoice.generateSummary();
            invoiceTextArea.setText(invoiceSummary);
            
            // Show invoice display section and hide orders table
            invoiceDisplaySection.setVisible(true);
            allOrdersSection.setVisible(false);
            
            // Enable export buttons
            exportCsvButton.setDisable(false);
            exportJsonButton.setDisable(false);
            
            statusLabel.setText("Invoice generated for Order #" + order.getOrderID());
            
        } catch (Exception e) {
            AlertUtil.showError("Invoice Generation Error", "Error generating invoice: " + e.getMessage());
            statusLabel.setText("Error generating invoice");
        }
    }
}
