package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Map;

public class DashboardController implements SceneNavigator.WithParams {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Button newOrderButton;
    @FXML private Button queueButton;
    @FXML private Button inventoryButton;
    @FXML private Button pricingButton;
    @FXML private Button invoicesButton;
    @FXML private Button logoutButton;

    private SceneNavigator navigator;
    private String currentUser;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void onNewOrder() {
        statusLabel.setText("Opening New Order form...");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/OrderFormView.fxml", "PrintFlow - New Order", params);
    }

    @FXML
    public void onQueue() {
        statusLabel.setText("Opening Order Queue...");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/OrderQueueView.fxml", "PrintFlow - Order Queue", params);
    }

    @FXML
    public void onInventory() {
        statusLabel.setText("Opening Inventory...");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/InventoryView.fxml", "PrintFlow - Inventory", params);
    }

    @FXML
    public void onPricing() {
        statusLabel.setText("Opening Pricing/Config...");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/PricingConfigView.fxml", "PrintFlow - Pricing Configuration", params);
    }

    @FXML
    public void onInvoices() {
        statusLabel.setText("Opening Invoices...");
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/InvoiceView.fxml", "PrintFlow - Invoice Management", params);
    }

    @FXML
    public void onLogout() {
        if (AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?")) {
            statusLabel.setText("Logging out...");
            // Navigate back to login screen
            navigator.navigate("/fxml/LoginView.fxml", "PrintFlow - Login");
        }
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
            welcomeLabel.setText("Welcome, " + currentUser);
        } else {
            welcomeLabel.setText("Welcome, User");
        }
    }
}
