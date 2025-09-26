package util;

import java.util.*;

/**
 * Result of input validation containing errors and warnings.
 */
public class ValidationResult {
    private boolean isValid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    
    /**
     * Adds an error message.
     * @param error The error message
     */
    public void addError(String error) {
        errors.add(error);
        isValid = false;
    }
    
    /**
     * Adds multiple error messages.
     * @param errors The error messages
     */
    public void addErrors(List<String> errors) {
        this.errors.addAll(errors);
        if (!errors.isEmpty()) {
            isValid = false;
        }
    }
    
    /**
     * Adds a warning message.
     * @param warning The warning message
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }
    
    /**
     * Checks if the input is valid.
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
     * Gets a formatted summary of the validation result.
     * @return Formatted string with all validation information
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Input Validation Result:\n");
        summary.append("Valid: ").append(isValid ? "Yes" : "No").append("\n");
        
        if (!errors.isEmpty()) {
            summary.append("Errors:\n");
            for (String error : errors) {
                summary.append("  - ").append(error).append("\n");
            }
        }
        
        if (!warnings.isEmpty()) {
            summary.append("Warnings:\n");
            for (String warning : warnings) {
                summary.append("  - ").append(warning).append("\n");
            }
        }
        
        return summary.toString();
    }
}

