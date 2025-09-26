package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utility class for displaying alerts and dialogs throughout the application.
 * Provides consistent error, info, warning, and confirmation dialogs.
 */
public class AlertUtil {
    
    /**
     * Shows an error alert with the specified title and message.
     * 
     * @param title The title of the alert
     * @param message The error message to display
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    
    /**
     * Shows an information alert with the specified title and message.
     * 
     * @param title The title of the alert
     * @param message The information message to display
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    
    /**
     * Shows a warning alert with the specified title and message.
     * 
     * @param title The title of the alert
     * @param message The warning message to display
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }
    
    /**
     * Shows a confirmation dialog and returns the user's choice.
     * 
     * @param title The title of the dialog
     * @param message The confirmation message to display
     * @return true if the user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Shows a custom alert with the specified type, title, and message.
     * 
     * @param alertType The type of alert (ERROR, INFORMATION, WARNING, etc.)
     * @param title The title of the alert
     * @param message The message to display
     */
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Center the alert on the primary stage if available
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        alert.showAndWait();
    }
    
    /**
     * Shows a validation error with a standard title.
     * 
     * @param message The validation error message
     */
    public static void showValidationError(String message) {
        showError("Validation Error", message);
    }
    
    /**
     * Shows a success message with a standard title.
     * 
     * @param message The success message
     */
    public static void showSuccess(String message) {
        showInfo("Success", message);
    }
}
