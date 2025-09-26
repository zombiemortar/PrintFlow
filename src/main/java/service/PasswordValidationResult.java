package service;

import java.util.*;

/**
 * Result class for password validation containing errors, warnings, and strength information.
 */
public class PasswordValidationResult {
    private boolean isValid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private int strengthScore = 0;
    private String strengthLevel = "Unknown";
    
    /**
     * Adds an error message.
     * @param error The error message
     */
    public void addError(String error) {
        errors.add(error);
        isValid = false;
    }
    
    /**
     * Adds a warning message.
     * @param warning The warning message
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }
    
    /**
     * Checks if the password is valid.
     * @return true if valid, false if there are errors
     */
    public boolean isValid() {
        return isValid;
    }
    
    /**
     * Gets all error messages.
     * @return List of error messages
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Gets all warning messages.
     * @return List of warning messages
     */
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    /**
     * Sets the strength score.
     * @param score The strength score (0-100)
     */
    public void setStrengthScore(int score) {
        this.strengthScore = Math.max(0, Math.min(100, score));
    }
    
    /**
     * Gets the strength score.
     * @return The strength score
     */
    public int getStrengthScore() {
        return strengthScore;
    }
    
    /**
     * Sets the strength level.
     * @param level The strength level (Weak, Medium, Strong, etc.)
     */
    public void setStrengthLevel(String level) {
        this.strengthLevel = level;
    }
    
    /**
     * Gets the strength level.
     * @return The strength level
     */
    public String getStrengthLevel() {
        return strengthLevel;
    }
}

