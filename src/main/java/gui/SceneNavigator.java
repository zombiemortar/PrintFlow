package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class SceneNavigator {
    private final Stage primaryStage;

    public SceneNavigator(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public <T> T navigate(String fxmlPath, String title) {
        return navigate(fxmlPath, title, null);
    }

    public <T> T navigate(String fxmlPath, String title, Map<String, Object> params) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalArgumentException("FXML not found: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Object controller = loader.getController();
            
            // Inject navigator into controllers that need it
            if (controller instanceof gui.controllers.LoginController) {
                ((gui.controllers.LoginController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.DashboardController) {
                ((gui.controllers.DashboardController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.OrderFormController) {
                ((gui.controllers.OrderFormController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.OrderQueueController) {
                ((gui.controllers.OrderQueueController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.InventoryController) {
                ((gui.controllers.InventoryController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.PricingConfigController) {
                ((gui.controllers.PricingConfigController) controller).setNavigator(this);
            } else if (controller instanceof gui.controllers.InvoiceController) {
                ((gui.controllers.InvoiceController) controller).setNavigator(this);
            }
            
            if (controller instanceof WithParams) {
                ((WithParams) controller).setParams(params);
            }
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
            @SuppressWarnings("unchecked")
            T typed = (T) controller;
            return typed;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public interface WithParams {
        void setParams(Map<String, Object> params);
    }
}


