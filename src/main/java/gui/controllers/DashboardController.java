package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import gui.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import service.DataManager;
import model.User;
import util.ExportManager;

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
    @FXML private Button factoryResetButton;
    @FXML private Button themeToggleButton;
    @FXML private Button exportSystemReportCsvButton;
    @FXML private Button exportSystemReportJsonButton;

    private SceneNavigator navigator;
    private String currentUser;
    private ThemeManager themeManager;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
        this.themeManager = ThemeManager.getInstance();
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

    @FXML
    public void onThemeToggle() {
        if (themeManager != null) {
            themeManager.toggleTheme();
            themeToggleButton.setText(themeManager.getToggleButtonText());
            // Apply theme to current scene with background color
            themeManager.applyThemeToCurrentScene(navigator.getPrimaryStage().getScene());
            statusLabel.setText("Theme switched to " + themeManager.getCurrentTheme());
        }
    }

    @FXML
    public void onExportSystemReportCSV() {
        try {
            statusLabel.setText("Exporting system report to CSV...");
            String filename = "system_report_" + ExportManager.getCurrentTimestamp() + ".csv";
            boolean success = ExportManager.exportSystemReportToCSV(filename);
            
            if (success) {
                AlertUtil.showInfo("Export Successful", "System report exported to CSV successfully.\nFile: " + filename);
                statusLabel.setText("System report exported to CSV");
            } else {
                AlertUtil.showError("Export Failed", "Failed to export system report to CSV.");
                statusLabel.setText("Export failed");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Error exporting system report to CSV: " + e.getMessage());
            statusLabel.setText("Export error");
        }
    }

    @FXML
    public void onExportSystemReportJSON() {
        try {
            statusLabel.setText("Exporting system report to JSON...");
            String filename = "system_report_" + ExportManager.getCurrentTimestamp() + ".json";
            boolean success = ExportManager.exportSystemReportToJSON(filename);
            
            if (success) {
                AlertUtil.showInfo("Export Successful", "System report exported to JSON successfully.\nFile: " + filename);
                statusLabel.setText("System report exported to JSON");
            } else {
                AlertUtil.showError("Export Failed", "Failed to export system report to JSON.");
                statusLabel.setText("Export failed");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Error exporting system report to JSON: " + e.getMessage());
            statusLabel.setText("Export error");
        }
    }

    @FXML
    public void onFactoryReset() {
        // Show warning dialog with detailed information
        String warningMessage = """
            WARNING: FACTORY RESET
        
            This action will PERMANENTLY DELETE ALL DATA:
            • All user accounts (except default admin)
            • All materials and inventory
            • All orders and order queue
            • All pricing configurations
            • All system settings
        
            A default admin account will be created:
            • Username: admin
            • Password: admin
        
            This action CANNOT be undone!
        
            Are you absolutely sure you want to proceed?
            """;
        
        if (AlertUtil.showConfirmation("Factory Reset Warning", warningMessage)) {
            // Second confirmation for extra safety
            if (AlertUtil.showConfirmation("Final Confirmation", 
                "This is your final warning. All data will be lost forever.\n\nType 'CONFIRM' to proceed:")) {
                
                statusLabel.setText("Performing factory reset...");
                
                // Perform the factory reset
                boolean success = DataManager.performFactoryReset();
                
                if (success) {
                    AlertUtil.showInfo("Factory Reset Complete", 
                        "Factory reset completed successfully.\n\n" +
                        "Default admin account created:\n" +
                        "Username: admin\n" +
                        "Password: admin\n\n" +
                        "You will be redirected to the login screen.");
                    
                    // Navigate back to login screen
                    navigator.navigate("/fxml/LoginView.fxml", "PrintFlow - Login");
                } else {
                    AlertUtil.showError("Factory Reset Failed", 
                        "Factory reset failed. Please check the console for error details.\n" +
                        "Some data may have been partially reset.");
                    statusLabel.setText("Factory reset failed");
                }
            } else {
                statusLabel.setText("Factory reset cancelled");
            }
        } else {
            statusLabel.setText("Factory reset cancelled");
        }
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
            welcomeLabel.setText("Welcome, " + currentUser);
            
            // Show factory reset button only for admin users
            User user = DataManager.getUserByUsername(currentUser);
            if (user != null && "admin".equals(user.getRole())) {
                factoryResetButton.setVisible(true);
            } else {
                factoryResetButton.setVisible(false);
            }
        } else {
            welcomeLabel.setText("Welcome, User");
            factoryResetButton.setVisible(false);
        }
        
        // Initialize theme toggle button
        if (themeManager != null && themeToggleButton != null) {
            themeToggleButton.setText(themeManager.getToggleButtonText());
        }
    }
}
