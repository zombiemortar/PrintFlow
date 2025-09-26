package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;
import java.util.List;

import util.InputValidator;
import util.SystemConfig;
import util.ValidationResult;


public class OrderFormController implements SceneNavigator.WithParams {

    @FXML private Label userLabel;
    @FXML private ComboBox<String> materialComboBox;
    @FXML private TextField dimensionsField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private TextArea instructionsField;
    @FXML private Label materialInfoLabel;
    @FXML private Label orderSummaryLabel;
    @FXML private Button submitButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SceneNavigator navigator;
    private String currentUser;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        // Initialize priority options
        ObservableList<String> priorityOptions = FXCollections.observableArrayList(
            "normal", "rush", "vip"
        );
        priorityComboBox.setItems(priorityOptions);
        priorityComboBox.setValue("normal");

        // Initialize materials (simplified for now)
        ObservableList<String> materials = FXCollections.observableArrayList(
            "Overture PLA (Black)", "PETG (Blue)", "ABS (Red)", "TPU (Clear)"
        );
        materialComboBox.setItems(materials);

        // Add listeners for real-time updates
        materialComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateMaterialInfo());
        dimensionsField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        priorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        instructionsField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
    }

    private void updateMaterialInfo() {
        String selected = materialComboBox.getValue();
        if (selected != null) {
            // Simplified material info based on selection
            String info = "";
            if (selected.contains("PLA")) {
                info = "Material: PLA\nCost per gram: $0.02\nPrint temperature: 210°C\nColor: Black";
            } else if (selected.contains("PETG")) {
                info = "Material: PETG\nCost per gram: $0.025\nPrint temperature: 240°C\nColor: Blue";
            } else if (selected.contains("ABS")) {
                info = "Material: ABS\nCost per gram: $0.03\nPrint temperature: 250°C\nColor: Red";
            } else if (selected.contains("TPU")) {
                info = "Material: TPU\nCost per gram: $0.04\nPrint temperature: 220°C\nColor: Clear";
            }
            materialInfoLabel.setText(info);
        } else {
            materialInfoLabel.setText("Select a material to see details");
        }
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        String material = materialComboBox.getValue();
        String dimensions = dimensionsField.getText();
        String quantityText = quantityField.getText();
        String priority = priorityComboBox.getValue();

        if (material == null || dimensions == null || dimensions.trim().isEmpty() || 
            quantityText == null || quantityText.trim().isEmpty()) {
            orderSummaryLabel.setText("Fill in the form to see order details");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                orderSummaryLabel.setText("Quantity must be greater than 0");
                return;
            }

            // Simplified price calculation
            double costPerGram = 0.02; // Default to PLA price
            if (material.contains("PETG")) costPerGram = 0.025;
            else if (material.contains("ABS")) costPerGram = 0.03;
            else if (material.contains("TPU")) costPerGram = 0.04;

            double gramsUsed = quantity * 10.0; // 10g per item assumption
            double materialCost = costPerGram * gramsUsed;
            double baseCost = SystemConfig.getBaseSetupCost();
            double estimatedPrice = (materialCost + baseCost) * (1 + SystemConfig.getTaxRate());

            // Rush surcharge
            if ("rush".equals(priority)) {
                estimatedPrice *= (1 + SystemConfig.getRushOrderSurcharge());
            }

            String summary = String.format(
                "Material: %s\n" +
                "Dimensions: %s cm\n" +
                "Quantity: %d\n" +
                "Priority: %s\n" +
                "Estimated Total Price: $%.2f",
                material, dimensions, quantity, priority, estimatedPrice
            );

            orderSummaryLabel.setText(summary);
        } catch (NumberFormatException e) {
            orderSummaryLabel.setText("Please enter a valid quantity");
        }
    }

    @FXML
    public void onSubmitOrder() {
        statusLabel.setText("Validating order...");

        // Collect form data
        String material = materialComboBox.getValue();
        String dimensions = dimensionsField.getText();
        String quantityText = quantityField.getText();
        String priority = priorityComboBox.getValue();
        String instructions = instructionsField.getText();

        // Validate all inputs using InputValidator and SystemConfig
        ValidationResult validationResult = validateOrderInputs(material, dimensions, quantityText, priority, instructions);
        
        if (!validationResult.isValid()) {
            // Show all validation errors
            List<String> errors = validationResult.getErrors();
            StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n\n");
            for (int i = 0; i < errors.size(); i++) {
                errorMessage.append((i + 1)).append(". ").append(errors.get(i)).append("\n");
            }
            
            AlertUtil.showValidationError(errorMessage.toString());
            statusLabel.setText("Ready");
            return;
        }

        // Parse validated quantity
        int quantity = Integer.parseInt(quantityText);

        // Simulate order submission
        statusLabel.setText("Submitting order...");
        
        // Generate a fake order ID
        int orderID = (int) (Math.random() * 9000) + 1000;
        
        // Calculate price for display using SystemConfig values
        double costPerGram = 0.02;
        if (material.contains("PETG")) costPerGram = 0.025;
        else if (material.contains("ABS")) costPerGram = 0.03;
        else if (material.contains("TPU")) costPerGram = 0.04;
        
        double gramsUsed = quantity * 10.0;
        double materialCost = costPerGram * gramsUsed;
        double baseCost = SystemConfig.getBaseSetupCost();
        double estimatedPrice = (materialCost + baseCost) * (1 + SystemConfig.getTaxRate());
        
        // Apply rush surcharge if applicable
        if ("rush".equals(priority)) {
            estimatedPrice *= (1 + SystemConfig.getRushOrderSurcharge());
        }
        
        AlertUtil.showSuccess("Order submitted successfully!\nOrder ID: " + orderID + 
                             "\nEstimated Price: $" + String.format("%.2f", estimatedPrice));
        onClearForm();
        statusLabel.setText("Ready");
    }

    /**
     * Validates all order inputs using InputValidator and SystemConfig limits.
     * @param material Selected material
     * @param dimensions Dimensions string
     * @param quantityText Quantity as string
     * @param priority Selected priority
     * @param instructions Special instructions
     * @return ValidationResult containing all validation errors
     */
    private ValidationResult validateOrderInputs(String material, String dimensions, String quantityText, String priority, String instructions) {
        ValidationResult result = new ValidationResult();

        // Validate material selection
        if (material == null || material.trim().isEmpty()) {
            result.addError("Please select a material.");
        } else {
            // Extract material name for validation (remove color info in parentheses)
            String materialName = material.split("\\(")[0].trim();
            ValidationResult materialValidation = InputValidator.validateMaterialName(materialName);
            if (!materialValidation.isValid()) {
                result.addErrors(materialValidation.getErrors());
            }
        }

        // Validate dimensions
        if (dimensions == null || dimensions.trim().isEmpty()) {
            result.addError("Please enter dimensions in the format LxWxH (e.g., 10x5x3).");
        } else {
            ValidationResult dimensionsValidation = InputValidator.validateDimensions(dimensions);
            if (!dimensionsValidation.isValid()) {
                result.addErrors(dimensionsValidation.getErrors());
            }
        }

        // Validate quantity
        if (quantityText == null || quantityText.trim().isEmpty()) {
            result.addError("Please enter a quantity.");
        } else {
            try {
                int quantity = Integer.parseInt(quantityText);
                ValidationResult quantityValidation = InputValidator.validateQuantity(quantity);
                if (!quantityValidation.isValid()) {
                    result.addErrors(quantityValidation.getErrors());
                }
                
                // Check SystemConfig limits
                if (quantity > SystemConfig.getMaxOrderQuantity()) {
                    result.addError("Quantity cannot exceed " + SystemConfig.getMaxOrderQuantity() + " items (system limit).");
                }
            } catch (NumberFormatException e) {
                result.addError("Please enter a valid quantity (whole number).");
            }
        }

        // Validate priority selection
        if (priority == null || priority.trim().isEmpty()) {
            result.addError("Please select a priority level.");
        } else {
            if ("rush".equals(priority) && !SystemConfig.isAllowRushOrders()) {
                result.addError("Rush orders are currently not allowed by system configuration.");
            }
        }

        // Validate special instructions (optional field)
        if (instructions != null && !instructions.trim().isEmpty()) {
            ValidationResult instructionsValidation = InputValidator.validateInstructions(instructions);
            if (!instructionsValidation.isValid()) {
                result.addErrors(instructionsValidation.getErrors());
            }
        }

        return result;
    }

    @FXML
    public void onClearForm() {
        materialComboBox.setValue(null);
        dimensionsField.clear();
        quantityField.clear();
        priorityComboBox.setValue("normal");
        instructionsField.clear();
        materialInfoLabel.setText("Select a material to see details");
        orderSummaryLabel.setText("Fill in the form to see order details");
        statusLabel.setText("Form cleared");
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
            currentUser = "Guest";
            userLabel.setText("User: Guest");
        }
    }
}