package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import util.SystemConfig;

import java.util.Map;

/**
 * Controller for the Pricing/Configuration screen.
 * Handles editing of SystemConfig values and file persistence operations.
 */
public class PricingConfigController implements SceneNavigator.WithParams {

    // Pricing constants fields
    @FXML private TextField electricityCostField;
    @FXML private TextField machineTimeCostField;
    @FXML private TextField baseSetupCostField;
    
    // Tax & currency fields
    @FXML private TextField taxRateField;
    @FXML private TextField currencyField;
    
    // Order limits fields
    @FXML private TextField maxOrderQuantityField;
    @FXML private TextField maxOrderValueField;
    
    // Rush order settings fields
    @FXML private CheckBox allowRushOrdersCheckBox;
    @FXML private TextField rushOrderSurchargeField;
    
    // Action buttons
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button resetButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    
    // Status labels
    @FXML private Label statusLabel;
    @FXML private Label configStatusLabel;

    private SceneNavigator navigator;
    private String currentUser;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // Load current values into the form
        refreshFormValues();
        
        // Add input validation listeners
        addInputValidation();
    }

    @FXML
    public void onBack() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
    }

    @FXML
    public void onLoad() {
        statusLabel.setText("Loading configuration from file...");
        
        try {
            boolean success = SystemConfig.loadFromFile();
            if (success) {
                refreshFormValues();
                configStatusLabel.setText("Configuration loaded from file");
                configStatusLabel.setStyle("-fx-text-fill: green;");
                AlertUtil.showInfo("Success", "Configuration loaded successfully from file.");
            } else {
                configStatusLabel.setText("Failed to load configuration");
                configStatusLabel.setStyle("-fx-text-fill: red;");
                AlertUtil.showError("Load Error", "Failed to load configuration from file. Using current values.");
            }
        } catch (Exception e) {
            configStatusLabel.setText("Error loading configuration");
            configStatusLabel.setStyle("-fx-text-fill: red;");
            AlertUtil.showError("Load Error", "Error loading configuration: " + e.getMessage());
        }
        
        statusLabel.setText("Ready");
    }

    @FXML
    public void onSave() {
        statusLabel.setText("Saving configuration to file...");
        
        try {
            // First, apply current form values to SystemConfig
            if (applyFormValuesToConfig()) {
                // Then save to file
                boolean success = SystemConfig.saveToFile();
                if (success) {
                    configStatusLabel.setText("Configuration saved to file");
                    configStatusLabel.setStyle("-fx-text-fill: green;");
                    AlertUtil.showInfo("Success", "Configuration saved successfully to file.");
                } else {
                    configStatusLabel.setText("Failed to save configuration");
                    configStatusLabel.setStyle("-fx-text-fill: red;");
                    AlertUtil.showError("Save Error", "Failed to save configuration to file.");
                }
            } else {
                configStatusLabel.setText("Invalid configuration values");
                configStatusLabel.setStyle("-fx-text-fill: red;");
                AlertUtil.showError("Validation Error", "Please check your input values and try again.");
            }
        } catch (Exception e) {
            configStatusLabel.setText("Error saving configuration");
            configStatusLabel.setStyle("-fx-text-fill: red;");
            AlertUtil.showError("Save Error", "Error saving configuration: " + e.getMessage());
        }
        
        statusLabel.setText("Ready");
    }

    @FXML
    public void onReset() {
        if (AlertUtil.showConfirmation("Reset Configuration", 
                "Are you sure you want to reset all configuration values to defaults? This action cannot be undone.")) {
            
            statusLabel.setText("Resetting to defaults...");
            
            try {
                SystemConfig.resetToDefaults();
                refreshFormValues();
                configStatusLabel.setText("Configuration reset to defaults");
                configStatusLabel.setStyle("-fx-text-fill: orange;");
                AlertUtil.showInfo("Success", "Configuration reset to default values.");
            } catch (Exception e) {
                configStatusLabel.setText("Error resetting configuration");
                configStatusLabel.setStyle("-fx-text-fill: red;");
                AlertUtil.showError("Reset Error", "Error resetting configuration: " + e.getMessage());
            }
            
            statusLabel.setText("Ready");
        }
    }

    @FXML
    public void onRefresh() {
        statusLabel.setText("Refreshing values...");
        refreshFormValues();
        configStatusLabel.setText("Values refreshed from current configuration");
        configStatusLabel.setStyle("-fx-text-fill: blue;");
        statusLabel.setText("Ready");
    }

    /**
     * Refreshes the form fields with current SystemConfig values.
     */
    private void refreshFormValues() {
        // Pricing constants
        electricityCostField.setText(String.format("%.2f", SystemConfig.getElectricityCostPerHour()));
        machineTimeCostField.setText(String.format("%.2f", SystemConfig.getMachineTimeCostPerHour()));
        baseSetupCostField.setText(String.format("%.2f", SystemConfig.getBaseSetupCost()));
        
        // Tax & currency
        taxRateField.setText(String.format("%.1f", SystemConfig.getTaxRate() * 100)); // Display as percentage
        currencyField.setText(SystemConfig.getCurrency());
        
        // Order limits
        maxOrderQuantityField.setText(String.valueOf(SystemConfig.getMaxOrderQuantity()));
        maxOrderValueField.setText(String.format("%.2f", SystemConfig.getMaxOrderValue()));
        
        // Rush order settings
        allowRushOrdersCheckBox.setSelected(SystemConfig.isAllowRushOrders());
        rushOrderSurchargeField.setText(String.format("%.1f", SystemConfig.getRushOrderSurcharge() * 100)); // Display as percentage
    }

    /**
     * Applies form values to SystemConfig.
     * @return true if all values were applied successfully
     */
    private boolean applyFormValuesToConfig() {
        try {
            // Validate and apply pricing constants
            double electricityCost = Double.parseDouble(electricityCostField.getText().trim());
            if (electricityCost < 0) {
                AlertUtil.showError("Validation Error", "Electricity cost must be non-negative.");
                return false;
            }
            SystemConfig.setElectricityCostPerHour(electricityCost);
            
            double machineTimeCost = Double.parseDouble(machineTimeCostField.getText().trim());
            if (machineTimeCost < 0) {
                AlertUtil.showError("Validation Error", "Machine time cost must be non-negative.");
                return false;
            }
            SystemConfig.setMachineTimeCostPerHour(machineTimeCost);
            
            double baseSetupCost = Double.parseDouble(baseSetupCostField.getText().trim());
            if (baseSetupCost < 0) {
                AlertUtil.showError("Validation Error", "Base setup cost must be non-negative.");
                return false;
            }
            SystemConfig.setBaseSetupCost(baseSetupCost);
            
            // Validate and apply tax & currency
            double taxRate = Double.parseDouble(taxRateField.getText().trim()) / 100.0; // Convert percentage to decimal
            if (taxRate < 0 || taxRate > 1) {
                AlertUtil.showError("Validation Error", "Tax rate must be between 0% and 100%.");
                return false;
            }
            SystemConfig.setTaxRate(taxRate);
            
            String currency = currencyField.getText().trim();
            if (currency.isEmpty()) {
                AlertUtil.showError("Validation Error", "Currency cannot be empty.");
                return false;
            }
            SystemConfig.setCurrency(currency);
            
            // Validate and apply order limits
            int maxOrderQuantity = Integer.parseInt(maxOrderQuantityField.getText().trim());
            if (maxOrderQuantity <= 0) {
                AlertUtil.showError("Validation Error", "Max order quantity must be positive.");
                return false;
            }
            SystemConfig.setMaxOrderQuantity(maxOrderQuantity);
            
            double maxOrderValue = Double.parseDouble(maxOrderValueField.getText().trim());
            if (maxOrderValue <= 0) {
                AlertUtil.showError("Validation Error", "Max order value must be positive.");
                return false;
            }
            SystemConfig.setMaxOrderValue(maxOrderValue);
            
            // Apply rush order settings
            SystemConfig.setAllowRushOrders(allowRushOrdersCheckBox.isSelected());
            
            double rushOrderSurcharge = Double.parseDouble(rushOrderSurchargeField.getText().trim()) / 100.0; // Convert percentage to decimal
            if (rushOrderSurcharge < 0) {
                AlertUtil.showError("Validation Error", "Rush order surcharge must be non-negative.");
                return false;
            }
            SystemConfig.setRushOrderSurcharge(rushOrderSurcharge);
            
            return true;
            
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validation Error", "Please enter valid numeric values for all fields.");
            return false;
        } catch (Exception e) {
            AlertUtil.showError("Validation Error", "Error applying configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds input validation to form fields.
     */
    private void addInputValidation() {
        // Add numeric validation to all numeric fields
        addNumericValidation(electricityCostField);
        addNumericValidation(machineTimeCostField);
        addNumericValidation(baseSetupCostField);
        addNumericValidation(taxRateField);
        addNumericValidation(maxOrderQuantityField);
        addNumericValidation(maxOrderValueField);
        addNumericValidation(rushOrderSurchargeField);
        
        // Add currency validation
        currencyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                currencyField.setStyle("-fx-border-color: green;");
            } else {
                currencyField.setStyle("-fx-border-color: red;");
            }
        });
    }

    /**
     * Adds numeric validation to a text field.
     * @param field The text field to validate
     */
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue.trim());
                    if (value >= 0) {
                        field.setStyle("-fx-border-color: green;");
                    } else {
                        field.setStyle("-fx-border-color: orange;");
                    }
                } catch (NumberFormatException e) {
                    field.setStyle("-fx-border-color: red;");
                }
            } else {
                field.setStyle("-fx-border-color: red;");
            }
        });
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
        }
    }
}
