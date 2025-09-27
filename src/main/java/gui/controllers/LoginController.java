package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.SecurityManager;
import service.SessionResult;

import java.util.Map;

public class LoginController implements SceneNavigator.WithParams {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;

    private SceneNavigator navigator;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a username.");
            usernameField.requestFocus();
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a password.");
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate user via SecurityManager
        SessionResult result = SecurityManager.authenticateUser(username, password);
        
        if (result.isSuccess()) {
            // Handle remember me functionality
            if (rememberMeCheckbox.isSelected()) {
                saveUserSession(username, result.getSessionId());
            }
            
            AlertUtil.showSuccess("Login successful! Welcome, " + username + ".");
            
            // Navigate to dashboard with username and session ID
            Map<String, Object> params = new java.util.HashMap<>();
            params.put("username", username);
            params.put("sessionId", result.getSessionId());
            navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
        } else {
            AlertUtil.showError("Login Failed", result.getMessage());
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    public void onOpenAccountCreation() {
        try {
            // Load the account creation FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccountCreationView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and set the stage
            AccountCreationController controller = loader.getController();
            Stage accountCreationStage = new Stage();
            controller.setStage(accountCreationStage);
            
            // Configure the stage
            accountCreationStage.setTitle("Create New Account - PrintFlow");
            accountCreationStage.setScene(scene);
            accountCreationStage.setResizable(false);
            accountCreationStage.sizeToScene();
            
            // Center the window on the parent stage
            if (navigator != null && navigator.getPrimaryStage() != null) {
                accountCreationStage.initOwner(navigator.getPrimaryStage());
                accountCreationStage.centerOnScreen();
            }
            
            // Show the window
            accountCreationStage.showAndWait();
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to open account creation window: " + e.getMessage());
            System.err.println("Error opening account creation window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setParams(Map<String, Object> params) {
        // Check for auto-login parameters
        if (params != null && params.containsKey("autoLogin")) {
            String savedUsername = (String) params.get("savedUsername");
            if (savedUsername != null) {
                usernameField.setText(savedUsername);
                rememberMeCheckbox.setSelected(true);
                passwordField.requestFocus();
            }
        }
    }
    
    /**
     * Saves user session data for remember me functionality.
     * @param username The username to save
     * @param sessionId The session ID to save
     */
    private void saveUserSession(String username, String sessionId) {
        // For now, we'll save to a simple text file
        // In a real application, this would be more secure
        try {
            java.io.File sessionFile = new java.io.File("data/user_session.txt");
            sessionFile.getParentFile().mkdirs();
            
            java.io.PrintWriter writer = new java.io.PrintWriter(sessionFile);
            writer.println(username);
            writer.println(sessionId);
            writer.println(System.currentTimeMillis());
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to save user session: " + e.getMessage());
        }
    }
    
    /**
     * Loads saved user session data for auto-login.
     * @return Map containing session data or null if no valid session
     */
    public static Map<String, Object> loadUserSession() {
        try {
            java.io.File sessionFile = new java.io.File("data/user_session.txt");
            if (!sessionFile.exists()) {
                return null;
            }
            
            java.util.Scanner scanner = new java.util.Scanner(sessionFile);
            String username = scanner.nextLine();
            String sessionId = scanner.nextLine();
            long timestamp = Long.parseLong(scanner.nextLine());
            scanner.close();
            
            // Check if session is still valid (within 7 days)
            long currentTime = System.currentTimeMillis();
            long sessionAge = currentTime - timestamp;
            long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;
            
            if (sessionAge > sevenDaysInMillis) {
                // Session expired, delete the file
                sessionFile.delete();
                return null;
            }
            
            // Validate session with SecurityManager
            if (SecurityManager.validateSession(sessionId) != null) {
                Map<String, Object> sessionData = new java.util.HashMap<>();
                sessionData.put("savedUsername", username);
                sessionData.put("sessionId", sessionId);
                sessionData.put("autoLogin", true);
                return sessionData;
            } else {
                // Session invalid, delete the file
                sessionFile.delete();
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load user session: " + e.getMessage());
            return null;
        }
    }
}


