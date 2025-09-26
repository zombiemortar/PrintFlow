package gui.controllers;

import gui.AlertUtil;
import gui.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Map;

public class LoginController implements SceneNavigator.WithParams {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private SceneNavigator navigator;

    public void setNavigator(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void onLogin() {
        // Stub: accept any non-empty credentials for now
        String user = usernameField.getText();
        String password = passwordField.getText();
        
        if (user == null || user.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a username.");
            usernameField.requestFocus();
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            AlertUtil.showValidationError("Please enter a password.");
            passwordField.requestFocus();
            return;
        }
        
        // For now, accept any non-empty credentials
        AlertUtil.showSuccess("Login successful! Welcome, " + user + ".");
        
        // Navigate to dashboard with username parameter
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("username", user);
        navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
    }

    @Override
    public void setParams(Map<String, Object> params) {
        // no-op for now
    }
}


