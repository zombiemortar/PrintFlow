package gui.controllers;

import gui.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.SecurityManager;
import service.DataManager;
import service.UserCreationResult;
import service.PasswordValidationResult;
import util.InputValidator;
import util.ValidationResult;

public class AccountCreationController {

    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStrengthLabel;

    private Stage stage;

    @FXML
    public void initialize() {
        // Populate account type ComboBox
        accountTypeComboBox.setItems(FXCollections.observableArrayList("user", "customer", "vip", "admin"));
        accountTypeComboBox.setValue("user"); // Set default value
        
        // Add password strength monitoring
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrengthDisplay(newValue);
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onCreateAccount() {
        String accountType = accountTypeComboBox.getValue();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate all fields
        if (!validateInput(accountType, username, email, password, confirmPassword)) {
            return;
        }

        // Check if username already exists
        if (DataManager.userExists(username)) {
            AlertUtil.showError("Account Creation Failed", "Username already exists. Please choose a different username.");
            usernameField.requestFocus();
            return;
        }

        // Create user via SecurityManager
        UserCreationResult result = SecurityManager.createUser(username, email, accountType, password);

        if (result.isSuccess()) {
            AlertUtil.showSuccess("Account created successfully!\n" +
                    "Account Type: " + accountType + "\n" +
                    "Username: " + username + "\n" +
                    "You can now login with your new account.");
            
            // Close the window
            if (stage != null) {
                stage.close();
            }
        } else {
            AlertUtil.showError("Account Creation Failed", result.getMessage());
            passwordField.clear();
            confirmPasswordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    public void onCancel() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Validates all input fields for account creation.
     * @param accountType The selected account type
     * @param username The username
     * @param email The email address
     * @param password The password
     * @param confirmPassword The password confirmation
     * @return true if all validation passes, false otherwise
     */
    private boolean validateInput(String accountType, String username, String email, String password, String confirmPassword) {
        boolean isValid = true;

        // Validate account type
        if (accountType == null || accountType.trim().isEmpty()) {
            AlertUtil.showValidationError("Please select an account type.");
            accountTypeComboBox.requestFocus();
            isValid = false;
        }

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a username.");
            usernameField.requestFocus();
            isValid = false;
        } else {
            ValidationResult usernameValidation = InputValidator.validateUsername(username);
            if (!usernameValidation.isValid()) {
                AlertUtil.showValidationError("Invalid username: " + usernameValidation.getMessage());
                usernameField.requestFocus();
                isValid = false;
            }
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter an email address.");
            emailField.requestFocus();
            isValid = false;
        } else {
            ValidationResult emailValidation = InputValidator.validateEmail(email);
            if (!emailValidation.isValid()) {
                AlertUtil.showValidationError("Invalid email: " + emailValidation.getMessage());
                emailField.requestFocus();
                isValid = false;
            }
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a password.");
            passwordField.requestFocus();
            isValid = false;
        } else {
            PasswordValidationResult passwordValidation = SecurityManager.validatePasswordStrength(password);
            if (!passwordValidation.isValid()) {
                StringBuilder errorMsg = new StringBuilder("Password does not meet security requirements:\n");
                for (String error : passwordValidation.getErrors()) {
                    errorMsg.append("- ").append(error).append("\n");
                }
                AlertUtil.showValidationError(errorMsg.toString());
                passwordField.requestFocus();
                isValid = false;
            }
        }

        // Validate password confirmation
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            AlertUtil.showValidationError("Please confirm your password.");
            confirmPasswordField.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            AlertUtil.showValidationError("Passwords do not match. Please try again.");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Updates the password strength display based on the current password.
     * @param password The password to analyze
     */
    private void updatePasswordStrengthDisplay(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrengthLabel.setText("");
            return;
        }

        PasswordValidationResult result = SecurityManager.validatePasswordStrength(password);
        
        String strengthText = "Password Strength: " + result.getStrengthLevel();
        String strengthColor = getStrengthColor(result.getStrengthLevel());
        
        passwordStrengthLabel.setText(strengthText);
        passwordStrengthLabel.setStyle("-fx-text-fill: " + strengthColor + ";");
    }

    /**
     * Gets the color for password strength display.
     * @param strengthLevel The strength level
     * @return The color string for CSS
     */
    private String getStrengthColor(String strengthLevel) {
        switch (strengthLevel.toLowerCase()) {
            case "very weak":
                return "red";
            case "weak":
                return "orange";
            case "medium":
                return "yellow";
            case "strong":
                return "lightgreen";
            case "very strong":
                return "green";
            default:
                return "black";
        }
    }
}
