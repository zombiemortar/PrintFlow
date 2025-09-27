package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Order;
import service.OrderManager;
import util.SystemConfig;

import java.util.Map;

public class OrderQueueController implements SceneNavigator.WithParams {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> idColumn;
    @FXML private TableColumn<Order, String> userColumn;
    @FXML private TableColumn<Order, String> materialColumn;
    @FXML private TableColumn<Order, Integer> quantityColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> priorityColumn;
    @FXML private TableColumn<Order, Double> estHoursColumn;
    
    @FXML private Label queueStatusLabel;
    @FXML private Label statusLabel;
    @FXML private Button refreshButton;
    @FXML private Button setProcessingButton;
    @FXML private Button setCompletedButton;
    @FXML private Button setRushButton;
    @FXML private Button backButton;

    private SceneNavigator navigator;
    private String currentUser;
    private ObservableList<Order> orderList;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        userColumn.setCellValueFactory(cellData -> {
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
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        estHoursColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedPrintHours"));
        
        // Initialize observable list
        orderList = FXCollections.observableArrayList();
        orderTable.setItems(orderList);
        
        // Add selection listener
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates(newSelection);
        });
        
        // Load initial data
        loadOrderData();
    }

    private void loadOrderData() {
        try {
            statusLabel.setText("Loading orders...");
            
            // Get all orders from OrderManager
            Order[] orders = OrderManager.getAllOrders();
            
            // Clear and populate the list
            orderList.clear();
            orderList.addAll(orders);
            
            // Update status
            int totalOrders = orders.length;
            int pendingOrders = 0;
            int processingOrders = 0;
            int completedOrders = 0;
            
            for (Order order : orders) {
                switch (order.getStatus().toLowerCase()) {
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
            
            queueStatusLabel.setText(String.format("Total: %d | Pending: %d | Processing: %d | Completed: %d", 
                totalOrders, pendingOrders, processingOrders, completedOrders));
            
            statusLabel.setText("Ready");
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load order data: " + e.getMessage());
            statusLabel.setText("Error loading data");
        }
    }

    private void updateButtonStates(Order selectedOrder) {
        boolean hasSelection = selectedOrder != null;
        
        setProcessingButton.setDisable(!hasSelection);
        setCompletedButton.setDisable(!hasSelection);
        setRushButton.setDisable(!hasSelection || !SystemConfig.isAllowRushOrders());
    }

    @FXML
    public void onRefresh() {
        loadOrderData();
    }

    @FXML
    public void onSetProcessing() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order first.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Update Status", 
            "Set order #" + selectedOrder.getOrderID() + " to 'processing'?")) {
            
            if (selectedOrder.updateStatus("processing")) {
                loadOrderData(); // Refresh the table
                AlertUtil.showInfo("Success", "Order status updated to processing.");
            } else {
                AlertUtil.showError("Error", "Failed to update order status.");
            }
        }
    }

    @FXML
    public void onSetCompleted() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order first.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Update Status", 
            "Set order #" + selectedOrder.getOrderID() + " to 'completed'?")) {
            
            if (selectedOrder.updateStatus("completed")) {
                loadOrderData(); // Refresh the table
                AlertUtil.showInfo("Success", "Order status updated to completed.");
            } else {
                AlertUtil.showError("Error", "Failed to update order status.");
            }
        }
    }

    @FXML
    public void onSetRush() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Selection", "Please select an order first.");
            return;
        }
        
        if (!SystemConfig.isAllowRushOrders()) {
            AlertUtil.showWarning("Rush Orders Disabled", "Rush orders are currently disabled in system configuration.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Set Rush Priority", 
            "Set order #" + selectedOrder.getOrderID() + " to 'rush' priority?\n" +
            "This will add a " + (SystemConfig.getRushOrderSurcharge() * 100) + "% surcharge.")) {
            
            selectedOrder.setPriority("rush");
            loadOrderData(); // Refresh the table
            AlertUtil.showInfo("Success", "Order priority updated to rush.");
        }
    }

    @FXML
    public void onBack() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
        } else {
            currentUser = "Unknown User";
        }
    }
}
