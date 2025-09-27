package gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.prefs.Preferences;

/**
 * ThemeManager handles switching between light and dark themes
 * and persists the user's theme preference.
 */
public class ThemeManager {
    
    private static final String THEME_PREF_KEY = "theme_preference";
    private static final String LIGHT_THEME = "light";
    private static final String DARK_THEME = "dark";
    
    private static final String LIGHT_THEME_PATH = "/css/light-theme.css";
    private static final String DARK_THEME_PATH = "/css/dark-theme.css";
    
    private static ThemeManager instance;
    private String currentTheme;
    private Preferences preferences;
    
    private ThemeManager() {
        preferences = Preferences.userNodeForPackage(ThemeManager.class);
        currentTheme = preferences.get(THEME_PREF_KEY, LIGHT_THEME); // Default to light theme
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Apply the current theme to a scene
     */
    public void applyTheme(Scene scene) {
        if (scene == null) return;
        
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Apply current theme
        String themePath = currentTheme.equals(LIGHT_THEME) ? LIGHT_THEME_PATH : DARK_THEME_PATH;
        scene.getStylesheets().add(getClass().getResource(themePath).toExternalForm());
        
        // Set scene background color
        if (isLightTheme()) {
            scene.setFill(javafx.scene.paint.Color.web("#FAFAFA"));
        } else {
            scene.setFill(javafx.scene.paint.Color.web("#121212"));
        }
    }
    
    /**
     * Apply the current theme to a stage (applies to its scene)
     */
    public void applyTheme(Stage stage) {
        if (stage != null && stage.getScene() != null) {
            applyTheme(stage.getScene());
        }
    }
    
    /**
     * Switch to light theme
     */
    public void setLightTheme() {
        currentTheme = LIGHT_THEME;
        preferences.put(THEME_PREF_KEY, currentTheme);
    }
    
    /**
     * Switch to dark theme
     */
    public void setDarkTheme() {
        currentTheme = DARK_THEME;
        preferences.put(THEME_PREF_KEY, currentTheme);
    }
    
    /**
     * Toggle between light and dark themes
     */
    public void toggleTheme() {
        if (currentTheme.equals(LIGHT_THEME)) {
            setDarkTheme();
        } else {
            setLightTheme();
        }
    }
    
    /**
     * Apply theme to current scene and refresh it
     */
    public void applyThemeToCurrentScene(Scene currentScene) {
        if (currentScene != null) {
            applyTheme(currentScene);
        }
    }
    
    /**
     * Get the current theme name
     */
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Check if currently using light theme
     */
    public boolean isLightTheme() {
        return LIGHT_THEME.equals(currentTheme);
    }
    
    /**
     * Check if currently using dark theme
     */
    public boolean isDarkTheme() {
        return DARK_THEME.equals(currentTheme);
    }
    
    /**
     * Get the theme toggle button text
     */
    public String getToggleButtonText() {
        return isLightTheme() ? "🌙 Dark" : "☀️ Light";
    }
}
