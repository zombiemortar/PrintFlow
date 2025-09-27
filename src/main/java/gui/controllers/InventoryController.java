package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Material;
import service.DataManager;
import util.Inventory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class InventoryController implements Initializable, SceneNavigator.WithParams {

    @FXML private TableView<Material> materialsTable;
    @FXML private TableColumn<Material, String> nameColumn;
    @FXML private TableColumn<Material, Double> costColumn;
    @FXML private TableColumn<Material, Integer> tempColumn;
    @FXML private TableColumn<Material, String> colorColumn;
    @FXML private TableColumn<Material, Integer> stockColumn;
    @FXML private TableColumn<Material, String> actionsColumn;
    @FXML private Button addMaterialButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private SceneNavigator navigator;
    private String currentUser;
    private ObservableList<Material> materialsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        nameColumn.setCellValueFactory(cellData -> {
            Material material = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(material.getDisplayName());
        });
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPerGram"));
        tempColumn.setCellValueFactory(new PropertyValueFactory<>("printTemp"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        
        // Custom cell factory for stock column
        stockColumn.setCellFactory(column -> new TableCell<Material, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Material material = getTableRow().getItem();
                    int stock = Inventory.getStock(material);
                    setText(String.valueOf(stock));
                }
            }
        });
        
        // Custom cell factory for actions column
        actionsColumn.setCellFactory(column -> new TableCell<Material, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Material material = getTableRow().getItem();
                    HBox buttonBox = new HBox(5);
                    
                    Button updateStockButton = new Button("Update Stock");
                    updateStockButton.setOnAction(e -> updateStock(material));
                    
                    Button removeButton = new Button("Remove");
                    removeButton.setOnAction(e -> removeMaterial(material));
                    removeButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                    
                    buttonBox.getChildren().addAll(updateStockButton, removeButton);
                    setGraphic(buttonBox);
                }
            }
        });

        // Initialize materials list
        materialsList = FXCollections.observableArrayList();
        materialsTable.setItems(materialsList);
        
        // Load materials
        loadMaterials();
    }

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params != null && params.containsKey("username")) {
            currentUser = (String) params.get("username");
        }
    }

    @FXML
    public void onBack() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", currentUser);
        navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
    }

    @FXML
    public void onRefresh() {
        statusLabel.setText("Refreshing inventory...");
        loadMaterials();
        statusLabel.setText("Inventory refreshed");
    }

    @FXML
    public void onAddMaterial() {
        // Show dialog to add new material
        showAddMaterialDialog();
    }

    private void loadMaterials() {
        try {
            materialsList.clear();
            // Load materials from file first
            DataManager.loadMaterials();
            // Then get all materials from DataManager
            Material[] materials = DataManager.getAllMaterials();
            if (materials != null) {
                materialsList.addAll(materials);
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load materials: " + e.getMessage());
        }
    }

    private void showAddMaterialDialog() {
        Dialog<Material> dialog = new Dialog<>();
        dialog.setTitle("Add New Material");
        dialog.setHeaderText("Enter material details:");

        // Create form fields
        TextField brandField = new TextField();
        brandField.setPromptText("Brand (e.g., Overture)");
        
        TextField typeField = new TextField();
        typeField.setPromptText("Type (e.g., PLA, PETG, ABS)");
        
        TextField costField = new TextField();
        costField.setPromptText("Cost per gram (e.g., 0.02)");
        
        TextField tempField = new TextField();
        tempField.setPromptText("Print temperature (e.g., 210)");
        
        TextField colorField = new TextField();
        colorField.setPromptText("Color (e.g., Black)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Brand:"), 0, 0);
        grid.add(brandField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeField, 1, 1);
        grid.add(new Label("Cost per gram:"), 0, 2);
        grid.add(costField, 1, 2);
        grid.add(new Label("Print temperature:"), 0, 3);
        grid.add(tempField, 1, 3);
        grid.add(new Label("Color:"), 0, 4);
        grid.add(colorField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Enable/disable add button based on input
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Validation
        brandField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(brandField.getText().trim().isEmpty() || 
                               typeField.getText().trim().isEmpty() ||
                               costField.getText().trim().isEmpty() || 
                               tempField.getText().trim().isEmpty() || 
                               colorField.getText().trim().isEmpty());
        });
        
        typeField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(brandField.getText().trim().isEmpty() || 
                               typeField.getText().trim().isEmpty() ||
                               costField.getText().trim().isEmpty() || 
                               tempField.getText().trim().isEmpty() || 
                               colorField.getText().trim().isEmpty());
        });
        
        costField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(brandField.getText().trim().isEmpty() || 
                               typeField.getText().trim().isEmpty() ||
                               costField.getText().trim().isEmpty() || 
                               tempField.getText().trim().isEmpty() || 
                               colorField.getText().trim().isEmpty());
        });
        
        tempField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(brandField.getText().trim().isEmpty() || 
                               typeField.getText().trim().isEmpty() ||
                               costField.getText().trim().isEmpty() || 
                               tempField.getText().trim().isEmpty() || 
                               colorField.getText().trim().isEmpty());
        });
        
        colorField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(brandField.getText().trim().isEmpty() || 
                               typeField.getText().trim().isEmpty() ||
                               costField.getText().trim().isEmpty() || 
                               tempField.getText().trim().isEmpty() || 
                               colorField.getText().trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String brand = brandField.getText().trim();
                    String type = typeField.getText().trim();
                    double cost = Double.parseDouble(costField.getText().trim());
                    int temp = Integer.parseInt(tempField.getText().trim());
                    String color = colorField.getText().trim();
                    
                    // Create material directly and add to DataManager
                    Material material = new Material(brand, type, cost, temp, color);
                    DataManager.addMaterial(material);
                    return material;
                } catch (NumberFormatException e) {
                    AlertUtil.showError("Error", "Please enter valid numbers for cost and temperature.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(material -> {
            if (material != null) {
                // Save materials and refresh
                try {
                    DataManager.saveMaterials();
                    loadMaterials();
                    statusLabel.setText("Material added successfully");
                } catch (Exception e) {
                    AlertUtil.showError("Error", "Failed to save material: " + e.getMessage());
                }
            }
        });
    }

    private void updateStock(Material material) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(Inventory.getStock(material)));
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update stock for " + material.getDisplayName());
        dialog.setContentText("Enter new stock amount (grams):");

        dialog.showAndWait().ifPresent(stockText -> {
            try {
                int newStock = Integer.parseInt(stockText.trim());
                if (newStock < 0) {
                    AlertUtil.showError("Error", "Stock cannot be negative.");
                    return;
                }
                
                Inventory.setStock(material, newStock);
                materialsTable.refresh(); // Refresh to show updated stock
                statusLabel.setText("Stock updated for " + material.getDisplayName());
            } catch (NumberFormatException e) {
                AlertUtil.showError("Error", "Please enter a valid number.");
            }
        });
    }

    private void removeMaterial(Material material) {
        if (AlertUtil.showConfirmation("Remove Material", 
                "Are you sure you want to remove " + material.getDisplayName() + "?")) {
            try {
                materialsList.remove(material);
                // Use the unique key for removal
                String uniqueKey = material.getBrand() + "|" + material.getType() + "|" + material.getColor();
                DataManager.materials.remove(uniqueKey);
                DataManager.saveMaterials();
                statusLabel.setText("Material removed: " + material.getDisplayName());
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to remove material: " + e.getMessage());
            }
        }
    }
}
