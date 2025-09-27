package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.controllers.LoginController;
import service.DataManager;
import java.util.Map;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Load all system data on startup
        System.out.println("Starting PrintFlow application...");
        boolean dataLoaded = DataManager.loadAllData();
        if (dataLoaded) {
            System.out.println("System data loaded successfully");
        } else {
            System.out.println("Warning: Some system data failed to load");
        }
        
        // Initialize the scene navigator
        SceneNavigator navigator = new SceneNavigator(primaryStage);
        
        // Check for auto-login session
        Map<String, Object> sessionData = LoginController.loadUserSession();
        if (sessionData != null && sessionData.containsKey("autoLogin")) {
            // Auto-login user
            String username = (String) sessionData.get("savedUsername");
            String sessionId = (String) sessionData.get("sessionId");
            
            System.out.println("Auto-login for user: " + username);
            
            // Navigate directly to dashboard with session data
            Map<String, Object> params = new java.util.HashMap<>();
            params.put("username", username);
            params.put("sessionId", sessionId);
            navigator.navigate("/fxml/DashboardView.fxml", "PrintFlow - Dashboard", params);
        } else {
            // Navigate to login screen
            navigator.navigate("/fxml/LoginView.fxml", "PrintFlow - Login");
        }
        
        // Set up shutdown hook to save data
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Shutting down PrintFlow application...");
            boolean dataSaved = DataManager.saveAllData();
            if (dataSaved) {
                System.out.println("System data saved successfully");
            } else {
                System.out.println("Warning: Some system data failed to save");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}



