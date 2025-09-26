package util;

import java.util.regex.Pattern;
import model.User;
import model.Order;

/**
 * Enhanced input validation system to protect against injection attacks and malicious input.
 * Provides comprehensive validation for various input types with security-focused checks.
 */
public class InputValidator {
    
    // Common injection patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union\\s+select|insert\\s+into|update\\s+set|delete\\s+from|drop\\s+table|create\\s+table|alter\\s+table|exec\\s+|execute\\s+|script\\s+|javascript\\s*:|vbscript\\s*:|onload\\s*=|onerror\\s*=|onclick\\s*=)"
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|javascript:|vbscript:|onload=|onerror=|onclick=|<iframe|<object|<embed|<link|<meta)"
    );
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(\\.\\./|\\.\\.\\\\|%2e%2e%2f|%2e%2e%5c)"
    );
    
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i)(;\\s*\\||\\|\\s*&|`\\s*\\$|\\(\\s*\\$|\\)\\s*\\[|\\]\\s*\\{|\\}\\s*<|>\\s*\\*|\\*\\s*\\?|\\?\\s*~|~\\s*!|!\\s*@|@\\s*#|#\\s*%|%\\s*\\^|\\^\\s*\\+|\\+\\s*=|=\\s*\\{|\\}\\s*\\[|\\]\\s*\\\\|\\\\\\s*/|/\\s*:|\"\\s*'|'\\s*\\s)"
    );
    
    // Input length limits
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MAX_ROLE_LENGTH = 20;
    private static final int MAX_MATERIAL_NAME_LENGTH = 100;
    private static final int MAX_DIMENSIONS_LENGTH = 200;
    private static final int MAX_INSTRUCTIONS_LENGTH = 1000;
    private static final int MAX_ORDER_QUANTITY = 1000;
    
    // Character restrictions
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern ROLE_PATTERN = Pattern.compile("^(customer|admin|vip)$");
    private static final Pattern MATERIAL_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-]{1,100}$");
    private static final Pattern DIMENSIONS_PATTERN = Pattern.compile("^[0-9xX\\s.,-]{1,200}$");
    
    // Dangerous characters that should be escaped or rejected
    private static final String[] DANGEROUS_CHARS = {
        "<", ">", "\"", "'", "&", ";", "|", "`", "$", "(", ")", "[", "]", "{", "}",
        "\\", "/", "*", "?", "~", "!", "@", "#", "%", "^", "+", "=", ":", "\n", "\r", "\t"
    };
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods only.
     */
    private InputValidator() {
        // Utility class - no instances needed
    }
    
    /**
     * Validates username input with security checks.
     * @param username The username to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateUsername(String username) {
        ValidationResult result = new ValidationResult();
        
        if (username == null) {
            result.addError("Username cannot be null");
            return result;
        }
        
        if (username.trim().isEmpty()) {
            result.addError("Username cannot be empty");
            return result;
        }
        
        if (username.length() > MAX_USERNAME_LENGTH) {
            result.addError("Username cannot exceed " + MAX_USERNAME_LENGTH + " characters");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            result.addError("Username can only contain letters, numbers, underscores, and hyphens");
        }
        
        if (containsInjectionPatterns(username)) {
            result.addError("Username contains potentially malicious content");
        }
        
        if (containsDangerousCharacters(username)) {
            result.addError("Username contains dangerous characters");
        }
        
        return result;
    }
    
    /**
     * Validates email input with security checks.
     * @param email The email to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        if (email == null) {
            result.addError("Email cannot be null");
            return result;
        }
        
        if (email.trim().isEmpty()) {
            result.addError("Email cannot be empty");
            return result;
        }
        
        if (email.length() > MAX_EMAIL_LENGTH) {
            result.addError("Email cannot exceed " + MAX_EMAIL_LENGTH + " characters");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("Invalid email format");
        }
        
        if (containsInjectionPatterns(email)) {
            result.addError("Email contains potentially malicious content");
        }
        
        return result;
    }
    
    /**
     * Validates password input with security checks.
     * @param password The password to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();
        
        if (password == null) {
            result.addError("Password cannot be null");
            return result;
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addError("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        
        if (containsInjectionPatterns(password)) {
            result.addError("Password contains potentially malicious content");
        }
        
        // Additional password strength validation
        service.PasswordValidationResult passwordValidation = service.SecurityManager.validatePasswordStrength(password);
        if (!passwordValidation.isValid()) {
            for (String error : passwordValidation.getErrors()) {
                result.addError(error);
            }
        }
        
        return result;
    }
    
    /**
     * Validates role input with security checks.
     * @param role The role to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateRole(String role) {
        ValidationResult result = new ValidationResult();
        
        if (role == null) {
            result.addError("Role cannot be null");
            return result;
        }
        
        if (role.trim().isEmpty()) {
            result.addError("Role cannot be empty");
            return result;
        }
        
        if (role.length() > MAX_ROLE_LENGTH) {
            result.addError("Role cannot exceed " + MAX_ROLE_LENGTH + " characters");
        }
        
        if (!ROLE_PATTERN.matcher(role.toLowerCase()).matches()) {
            result.addError("Role must be one of: customer, admin, vip");
        }
        
        if (containsInjectionPatterns(role)) {
            result.addError("Role contains potentially malicious content");
        }
        
        return result;
    }
    
    /**
     * Validates material name input with security checks.
     * @param materialName The material name to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateMaterialName(String materialName) {
        ValidationResult result = new ValidationResult();
        
        if (materialName == null) {
            result.addError("Material name cannot be null");
            return result;
        }
        
        if (materialName.trim().isEmpty()) {
            result.addError("Material name cannot be empty");
            return result;
        }
        
        if (materialName.length() > MAX_MATERIAL_NAME_LENGTH) {
            result.addError("Material name cannot exceed " + MAX_MATERIAL_NAME_LENGTH + " characters");
        }
        
        if (!MATERIAL_NAME_PATTERN.matcher(materialName).matches()) {
            result.addError("Material name can only contain letters, numbers, spaces, and hyphens");
        }
        
        if (containsInjectionPatterns(materialName)) {
            result.addError("Material name contains potentially malicious content");
        }
        
        return result;
    }
    
    /**
     * Validates dimensions input with security checks.
     * @param dimensions The dimensions to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateDimensions(String dimensions) {
        ValidationResult result = new ValidationResult();
        
        if (dimensions == null) {
            result.addError("Dimensions cannot be null");
            return result;
        }
        
        if (dimensions.trim().isEmpty()) {
            result.addError("Dimensions cannot be empty");
            return result;
        }
        
        if (dimensions.length() > MAX_DIMENSIONS_LENGTH) {
            result.addError("Dimensions cannot exceed " + MAX_DIMENSIONS_LENGTH + " characters");
        }
        
        if (!DIMENSIONS_PATTERN.matcher(dimensions).matches()) {
            result.addError("Dimensions can only contain numbers, x, spaces, commas, periods, and hyphens");
        }
        
        if (containsInjectionPatterns(dimensions)) {
            result.addError("Dimensions contain potentially malicious content");
        }
        
        return result;
    }
    
    /**
     * Validates special instructions input with security checks.
     * @param instructions The instructions to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateInstructions(String instructions) {
        ValidationResult result = new ValidationResult();
        
        if (instructions == null) {
            result.addError("Instructions cannot be null");
            return result;
        }
        
        if (instructions.length() > MAX_INSTRUCTIONS_LENGTH) {
            result.addError("Instructions cannot exceed " + MAX_INSTRUCTIONS_LENGTH + " characters");
        }
        
        if (containsInjectionPatterns(instructions)) {
            result.addError("Instructions contain potentially malicious content");
        }
        
        if (containsXSSPatterns(instructions)) {
            result.addError("Instructions contain potentially malicious script content");
        }
        
        return result;
    }
    
    /**
     * Validates order quantity input with security checks.
     * @param quantity The quantity to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateQuantity(int quantity) {
        ValidationResult result = new ValidationResult();
        
        if (quantity <= 0) {
            result.addError("Quantity must be greater than 0");
        }
        
        if (quantity > MAX_ORDER_QUANTITY) {
            result.addError("Quantity cannot exceed " + MAX_ORDER_QUANTITY);
        }
        
        return result;
    }
    
    /**
     * Validates numeric input with security checks.
     * @param value The numeric value to validate
     * @param minValue Minimum allowed value
     * @param maxValue Maximum allowed value
     * @param fieldName Name of the field being validated
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateNumeric(double value, double minValue, double maxValue, String fieldName) {
        ValidationResult result = new ValidationResult();
        
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            result.addError(fieldName + " must be a valid number");
            return result;
        }
        
        if (value < minValue) {
            result.addError(fieldName + " must be at least " + minValue);
        }
        
        if (value > maxValue) {
            result.addError(fieldName + " must be no more than " + maxValue);
        }
        
        return result;
    }
    
    /**
     * Sanitizes input by removing or escaping dangerous characters.
     * @param input The input to sanitize
     * @return Sanitized input string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = input;
        
        // Remove dangerous characters
        for (String dangerousChar : DANGEROUS_CHARS) {
            sanitized = sanitized.replace(dangerousChar, "");
        }
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        return sanitized;
    }
    
    /**
     * Escapes HTML special characters to prevent XSS attacks.
     * @param input The input to escape
     * @return HTML-escaped string
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;");
    }
    
    /**
     * Escapes SQL special characters to prevent SQL injection.
     * @param input The input to escape
     * @return SQL-escaped string
     */
    public static String escapeSql(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("'", "''")
                   .replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Validates file path to prevent path traversal attacks.
     * @param filePath The file path to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateFilePath(String filePath) {
        ValidationResult result = new ValidationResult();
        
        if (filePath == null) {
            result.addError("File path cannot be null");
            return result;
        }
        
        if (filePath.trim().isEmpty()) {
            result.addError("File path cannot be empty");
            return result;
        }
        
        if (PATH_TRAVERSAL_PATTERN.matcher(filePath).find()) {
            result.addError("File path contains potentially dangerous path traversal sequences");
        }
        
        if (containsInjectionPatterns(filePath)) {
            result.addError("File path contains potentially malicious content");
        }
        
        return result;
    }
    
    /**
     * Checks if input contains injection patterns.
     * @param input The input to check
     * @return true if injection patterns are found
     */
    private static boolean containsInjectionPatterns(String input) {
        if (input == null) {
            return false;
        }
        
        return SQL_INJECTION_PATTERN.matcher(input).find() ||
               COMMAND_INJECTION_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains XSS patterns.
     * @param input The input to check
     * @return true if XSS patterns are found
     */
    private static boolean containsXSSPatterns(String input) {
        if (input == null) {
            return false;
        }
        
        return XSS_PATTERN.matcher(input).find();
    }
    
    /**
     * Checks if input contains dangerous characters.
     * @param input The input to check
     * @return true if dangerous characters are found
     */
    private static boolean containsDangerousCharacters(String input) {
        if (input == null) {
            return false;
        }
        
        for (String dangerousChar : DANGEROUS_CHARS) {
            if (input.contains(dangerousChar)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Validates a complete user object with all security checks.
     * @param user The user to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateUser(User user) {
        ValidationResult result = new ValidationResult();
        
        if (user == null) {
            result.addError("User cannot be null");
            return result;
        }
        
        // Validate username
        ValidationResult usernameResult = validateUsername(user.getUsername());
        if (!usernameResult.isValid()) {
            result.addErrors(usernameResult.getErrors());
        }
        
        // Validate email
        ValidationResult emailResult = validateEmail(user.getEmail());
        if (!emailResult.isValid()) {
            result.addErrors(emailResult.getErrors());
        }
        
        // Validate role
        ValidationResult roleResult = validateRole(user.getRole());
        if (!roleResult.isValid()) {
            result.addErrors(roleResult.getErrors());
        }
        
        return result;
    }
    
    /**
     * Validates a complete order object with all security checks.
     * @param order The order to validate
     * @return ValidationResult with validation status and details
     */
    public static ValidationResult validateOrder(Order order) {
        ValidationResult result = new ValidationResult();
        
        if (order == null) {
            result.addError("Order cannot be null");
            return result;
        }
        
        // Validate user
        ValidationResult userResult = validateUser(order.getUser());
        if (!userResult.isValid()) {
            result.addErrors(userResult.getErrors());
        }
        
        // Validate material
        if (order.getMaterial() != null) {
            ValidationResult materialResult = validateMaterialName(order.getMaterial().getName());
            if (!materialResult.isValid()) {
                result.addErrors(materialResult.getErrors());
            }
        }
        
        // Validate dimensions
        ValidationResult dimensionsResult = validateDimensions(order.getDimensions());
        if (!dimensionsResult.isValid()) {
            result.addErrors(dimensionsResult.getErrors());
        }
        
        // Validate quantity
        ValidationResult quantityResult = validateQuantity(order.getQuantity());
        if (!quantityResult.isValid()) {
            result.addErrors(quantityResult.getErrors());
        }
        
        // Validate special instructions
        if (order.getSpecialInstructions() != null) {
            ValidationResult instructionsResult = validateInstructions(order.getSpecialInstructions());
            if (!instructionsResult.isValid()) {
                result.addErrors(instructionsResult.getErrors());
            }
        }
        
        return result;
    }
}
