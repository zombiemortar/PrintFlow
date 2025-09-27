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
import model.User;
import model.Material;
import model.Order;
import service.DataManager;


public class OrderFormController implements SceneNavigator.WithParams {

    @FXML private Label userLabel;
    @FXML private ComboBox<String> materialComboBox;
    @FXML private TextField dimensionsField;
    @FXML private TextField quantityField;
    @FXML private TextField materialGramsField;
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
    private User user;

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

        // Initialize materials from DataManager
        loadMaterials();

        // Add listeners for real-time updates
        materialComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateMaterialInfo());
        dimensionsField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        materialGramsField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        priorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
        instructionsField.textProperty().addListener((obs, oldVal, newVal) -> updateOrderSummary());
    }

    private void loadMaterials() {
        try {
            // Load materials from file first
            DataManager.loadMaterials();
            // Get all materials from DataManager
            Material[] materials = DataManager.getAllMaterials();
            
            // Group materials by Type -> Brand -> Color
            Map<String, Map<String, List<Material>>> organizedMaterials = new java.util.TreeMap<>();
            
            for (Material material : materials) {
                String type = material.getType();
                String brand = material.getBrand();
                
                organizedMaterials.computeIfAbsent(type, k -> new java.util.TreeMap<>())
                    .computeIfAbsent(brand, k -> new java.util.ArrayList<>())
                    .add(material);
            }
            
            ObservableList<String> materialNames = FXCollections.observableArrayList();
            
            // Build organized list: Type -> Brand -> Color
            for (Map.Entry<String, Map<String, List<Material>>> typeEntry : organizedMaterials.entrySet()) {
                Map<String, List<Material>> brands = typeEntry.getValue();
                
                for (Map.Entry<String, List<Material>> brandEntry : brands.entrySet()) {
                    List<Material> materialsOfBrand = brandEntry.getValue();
                    
                    for (Material material : materialsOfBrand) {
                        materialNames.add(material.getDisplayName());
                    }
                }
            }
            
            materialComboBox.setItems(materialNames);
        } catch (Exception e) {
            // Fallback to empty list if loading fails
            materialComboBox.setItems(FXCollections.observableArrayList());
            System.err.println("Failed to load materials: " + e.getMessage());
        }
    }

    private void updateMaterialInfo() {
        String selected = materialComboBox.getValue();
        if (selected != null) {
            // Find the actual Material object from DataManager
            Material material = findMaterialFromSelection(selected);
            if (material != null) {
                String info = material.getMaterialInfo();
                materialInfoLabel.setText(info);
            } else {
                materialInfoLabel.setText("Material information not available");
            }
        } else {
            materialInfoLabel.setText("Select a material to see details");
        }
        updateOrderSummary();
    }

    private Material findMaterialFromSelection(String selection) {
        if (selection == null || selection.trim().isEmpty()) {
            return null;
        }
        
        // Find material by display name using DataManager method
        return DataManager.getMaterialByDisplayName(selection);
    }

    private void updateOrderSummary() {
        String material = materialComboBox.getValue();
        String dimensions = dimensionsField.getText();
        String quantityText = quantityField.getText();
        String materialGramsText = materialGramsField.getText();
        String priority = priorityComboBox.getValue();

        if (material == null || dimensions == null || dimensions.trim().isEmpty() || 
            quantityText == null || quantityText.trim().isEmpty() ||
            materialGramsText == null || materialGramsText.trim().isEmpty()) {
            orderSummaryLabel.setText("Fill in the form to see order details");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                orderSummaryLabel.setText("Quantity must be greater than 0");
                return;
            }
            
            double materialGrams = Double.parseDouble(materialGramsText);
            if (materialGrams <= 0) {
                orderSummaryLabel.setText("Material usage must be greater than 0");
                return;
            }

            // Get actual material cost from DataManager
            Material materialObj = findMaterialFromSelection(material);
            double costPerGram = materialObj != null ? materialObj.getCostPerGram() : 0.02;

            double materialCost = costPerGram * materialGrams;
            double baseCost = SystemConfig.getBaseSetupCost();
            double estimatedPrice = (materialCost + baseCost) * (1 + SystemConfig.getTaxRate());

            // Rush surcharge
            if ("rush".equals(priority)) {
                estimatedPrice *= (1 + SystemConfig.getRushOrderSurcharge());
            }

            double totalMaterialGrams = quantity * materialGrams;
            String summary = String.format(
                "Material: %s\n" +
                "Dimensions: %s cm\n" +
                "Quantity: %d\n" +
                "Material Usage: %.1f grams per part\n" +
                "Total Material: %.1f grams\n" +
                "Priority: %s\n" +
                "Estimated Total Price: $%.2f",
                material, dimensions, quantity, materialGrams, totalMaterialGrams, priority, estimatedPrice
            );

            orderSummaryLabel.setText(summary);
        } catch (NumberFormatException e) {
            orderSummaryLabel.setText("Please enter valid numbers for quantity and material usage per part");
        }
    }

    @FXML
    public void onSubmitOrder() {
        statusLabel.setText("Validating order...");

        // Collect form data
        String material = materialComboBox.getValue();
        String dimensions = dimensionsField.getText();
        String quantityText = quantityField.getText();
        String materialGramsText = materialGramsField.getText();
        String priority = priorityComboBox.getValue();
        String instructions = instructionsField.getText();

        // Validate all inputs using InputValidator and SystemConfig
        ValidationResult validationResult = validateOrderInputs(material, dimensions, quantityText, materialGramsText, priority, instructions);
        
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

        // Parse validated quantity and material grams
        int quantity = Integer.parseInt(quantityText);
        double materialGrams = Double.parseDouble(materialGramsText);

        // Submit order using proper User.submitOrder() method
        statusLabel.setText("Submitting order...");
        
        try {
            // Get Material object from DataManager
            Material materialObj = findMaterialFromSelection(material);
            if (materialObj == null) {
                AlertUtil.showError("Error", "Selected material not found in inventory.");
                statusLabel.setText("Ready");
                return;
            }
            
            // Submit order using User.submitOrder() method
            Order order = user.submitOrder(materialObj, dimensions, quantity, instructions, materialGrams);
            
            if (order == null) {
                AlertUtil.showError("Order Failed", "Failed to submit order. Please check your inputs and try again.");
                statusLabel.setText("Ready");
                return;
            }
            
            // Set priority if specified
            if (!"normal".equals(priority)) {
                order.setPriority(priority);
            }
            
            // Calculate and display price
            double totalPrice = order.calculatePrice();
            
            AlertUtil.showSuccess("Order submitted successfully!\n" +
                                 "Order ID: " + order.getOrderID() + "\n" +
                                 "Estimated Price: $" + String.format("%.2f", totalPrice) + "\n" +
                                 "Status: " + order.getStatus() + "\n" +
                                 "Priority: " + order.getPriority() + "\n\n" +
                                 "Material stock has been updated in inventory.");
            
            onClearForm();
            statusLabel.setText("Order submitted successfully - Inventory updated");
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "An error occurred while submitting the order: " + e.getMessage());
            statusLabel.setText("Error submitting order");
        }
    }

    /**
     * Validates all order inputs using InputValidator and SystemConfig limits.
     * @param material Selected material
     * @param dimensions Dimensions string
     * @param quantityText Quantity as string
     * @param materialGramsText Material grams as string
     * @param priority Selected priority
     * @param instructions Special instructions
     * @return ValidationResult containing all validation errors
     */
    private ValidationResult validateOrderInputs(String material, String dimensions, String quantityText, String materialGramsText, String priority, String instructions) {
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

        // Validate material grams
        if (materialGramsText == null || materialGramsText.trim().isEmpty()) {
            result.addError("Please enter estimated material usage per part in grams.");
        } else {
            try {
                double materialGrams = Double.parseDouble(materialGramsText);
                if (materialGrams <= 0) {
                    result.addError("Material usage per part must be greater than 0 grams.");
                } else if (materialGrams > 10000) {
                    result.addError("Material usage per part cannot exceed 10,000 grams (10 kg).");
                }
            } catch (NumberFormatException e) {
                result.addError("Please enter a valid number for material usage per part (grams).");
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
        materialGramsField.clear();
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
            
            // Load the User object from DataManager
            user = DataManager.getUserByUsername(currentUser);
            if (user == null) {
                // Create a default user if not found
                user = new User(currentUser, currentUser + "@example.com", "customer", "password");
                DataManager.addUser(user);
            }
        } else {
            currentUser = "Guest";
            userLabel.setText("User: Guest");
            // Create a guest user
            user = new User("Guest", "guest@example.com", "customer", "password");
        }
    }
}