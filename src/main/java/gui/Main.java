package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import gui.controllers.LoginController;
import gui.ThemeManager;
import service.DataManager;
import java.util.Map;
import java.io.InputStream;

public class Main extends Application {
    
    @Override
    public void init() throws Exception {
        super.init();
    }
    
    
    @Override
    public void start(Stage primaryStage) {
        // Set application icon
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/printer-3d.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
                System.out.println("Successfully set application icon from printer-3d.png");
            } else {
                System.out.println("Warning: Could not load printer-3d.png icon file");
            }
        } catch (Exception e) {
            System.out.println("Error setting application icon: " + e.getMessage());
            e.printStackTrace();
        }
        
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
        
        // Apply saved theme preference
        ThemeManager themeManager = ThemeManager.getInstance();
        
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