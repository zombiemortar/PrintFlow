/**
 * Password security utility class for secure password hashing and validation.
 * Provides methods for password hashing using BCrypt algorithm and password strength validation.
 */
import java.security.SecureRandom;
import java.util.regex.Pattern;

public class PasswordSecurity {
    // BCrypt work factor (cost factor) - higher is more secure but slower
    private static final int BCRYPT_ROUNDS = 12;
    
    // Password strength requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    
    // Regex patterns for password strength validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    
    // Common weak passwords to reject
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "1234567890", "password1",
        "123123", "dragon", "master", "hello", "freedom", "whatever", "qazwsx",
        "trustno1", "654321", "jordan23", "harley", "password1", "jordan"
    };
    
    /**
     * Private constructor to prevent instantiation.
     * This class uses static methods only.
     */
    private PasswordSecurity() {
        // Utility class - no instances needed
    }
    
    /**
     * Hashes a password using BCrypt algorithm.
     * @param plainPassword The plain text password to hash
     * @return The hashed password string, or null if hashing failed
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Generate a random salt
            String salt = generateSalt();
            
            // Hash the password with the salt using BCrypt
            return BCrypt.hashpw(plainPassword, salt);
        } catch (Exception e) {
            System.err.println("Password hashing failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies a password against its hash.
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The stored hashed password
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Password verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates password strength according to security requirements.
     * @param password The password to validate
     * @return PasswordValidationResult containing validation status and messages
     */
    public static PasswordValidationResult validatePasswordStrength(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null) {
            result.addError("Password cannot be null");
            return result;
        }
        
        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addError("Password must be no more than " + MAX_PASSWORD_LENGTH + " characters long");
        }
        
        // Check for common passwords
        String lowerPassword = password.toLowerCase();
        for (String commonPassword : COMMON_PASSWORDS) {
            if (lowerPassword.equals(commonPassword) || lowerPassword.contains(commonPassword)) {
                result.addError("Password is too common and easily guessable");
                break;
            }
        }
        
        // Check character requirements
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).find();
        
        if (!hasUppercase) {
            result.addWarning("Password should contain at least one uppercase letter");
        }
        
        if (!hasLowercase) {
            result.addWarning("Password should contain at least one lowercase letter");
        }
        
        if (!hasDigit) {
            result.addWarning("Password should contain at least one digit");
        }
        
        if (!hasSpecialChar) {
            result.addWarning("Password should contain at least one special character");
        }
        
        // Calculate strength score
        int strengthScore = 0;
        if (password.length() >= 12) strengthScore += 2;
        else if (password.length() >= 8) strengthScore += 1;
        
        if (hasUppercase) strengthScore += 1;
        if (hasLowercase) strengthScore += 1;
        if (hasDigit) strengthScore += 1;
        if (hasSpecialChar) strengthScore += 1;
        
        if (password.length() >= 16) strengthScore += 1;
        
        result.setStrengthScore(strengthScore);
        
        // Determine strength level
        if (strengthScore >= 7) {
            result.setStrengthLevel("Strong");
        } else if (strengthScore >= 5) {
            result.setStrengthLevel("Medium");
        } else {
            result.setStrengthLevel("Weak");
        }
        
        return result;
    }
    
    /**
     * Generates a random salt for password hashing.
     * @return A random salt string
     */
    private static String generateSalt() {
        try {
            return BCrypt.gensalt(BCRYPT_ROUNDS);
        } catch (Exception e) {
            System.err.println("Salt generation failed: " + e.getMessage());
            // Fallback to a simple random salt
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            return "$2a$" + BCRYPT_ROUNDS + "$" + bytesToHex(saltBytes);
        }
    }
    
    /**
     * Converts byte array to hexadecimal string.
     * @param bytes The byte array to convert
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Generates a secure random password meeting strength requirements.
     * @param length The desired password length (8-32 characters)
     * @return A randomly generated secure password
     */
    public static String generateSecurePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH || length > 32) {
            length = 12; // Default length
        }
        
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String allChars = uppercase + lowercase + digits + specialChars;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        String result = password.toString();
        char[] chars = result.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }
    
    /**
     * Checks if a password hash is valid BCrypt format.
     * @param hash The hash to validate
     * @return true if the hash format is valid
     */
    public static boolean isValidHashFormat(String hash) {
        if (hash == null || hash.trim().isEmpty()) {
            return false;
        }
        
        // Our simplified BCrypt hashes start with $2a$ followed by rounds and salt
        return hash.matches("\\$2a\\$\\d{2}\\$[A-Za-z0-9./]+");
    }
    
    /**
     * Gets the BCrypt work factor from a hash.
     * @param hash The BCrypt hash
     * @return The work factor, or -1 if invalid hash
     */
    public static int getWorkFactor(String hash) {
        if (!isValidHashFormat(hash)) {
            return -1;
        }
        
        try {
            String[] parts = hash.split("\\$");
            if (parts.length >= 3) {
                return Integer.parseInt(parts[2]);
            }
        } catch (NumberFormatException e) {
            // Invalid format
        }
        
        return -1;
    }
}

/**
 * Result class for password validation containing errors, warnings, and strength information.
 */
class PasswordValidationResult {
    private boolean isValid = true;
    private java.util.List<String> errors = new java.util.ArrayList<>();
    private java.util.List<String> warnings = new java.util.ArrayList<>();
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
    public java.util.List<String> getErrors() {
        return new java.util.ArrayList<>(errors);
    }
    
    /**
     * Gets all warning messages.
     * @return List of warning messages
     */
    public java.util.List<String> getWarnings() {
        return new java.util.ArrayList<>(warnings);
    }
    
    /**
     * Sets the strength score.
     * @param score The strength score (0-10)
     */
    public void setStrengthScore(int score) {
        this.strengthScore = Math.max(0, Math.min(10, score));
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
     * @param level The strength level (Weak, Medium, Strong)
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
    
    /**
     * Gets a formatted summary of the validation result.
     * @return Formatted string with all validation information
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Password Validation Result:\n");
        summary.append("Valid: ").append(isValid ? "Yes" : "No").append("\n");
        summary.append("Strength: ").append(strengthLevel).append(" (").append(strengthScore).append("/10)\n");
        
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

/**
 * Simple BCrypt implementation for password hashing.
 * This is a basic implementation for educational purposes.
 * In production, use a well-tested library like jBCrypt.
 */
class BCrypt {
    private static final String SALT_PREFIX = "$2a$";
    
    /**
     * Generates a salt for BCrypt hashing.
     * @param rounds The number of rounds (cost factor)
     * @return A BCrypt salt string
     */
    public static String gensalt(int rounds) {
        if (rounds < 4 || rounds > 31) {
            rounds = 10; // Default rounds
        }
        
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        StringBuilder saltStr = new StringBuilder();
        saltStr.append(SALT_PREFIX);
        saltStr.append(String.format("%02d", rounds));
        saltStr.append("$");
        saltStr.append(base64Encode(salt));
        
        return saltStr.toString();
    }
    
    /**
     * Hashes a password with a salt.
     * @param password The plain text password
     * @param salt The BCrypt salt
     * @return The hashed password
     */
    public static String hashpw(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }
        
        // This is a simplified implementation
        // In production, use a proper BCrypt library
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            String combined = password + salt;
            byte[] hash = md.digest(combined.getBytes("UTF-8"));
            
            // Create a BCrypt-like format
            StringBuilder result = new StringBuilder();
            result.append(salt);
            result.append(base64Encode(hash));
            
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Verifies a password against a hash.
     * @param password The plain text password
     * @param hash The stored hash
     * @return true if the password matches
     */
    public static boolean checkpw(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        
        try {
            // Extract salt from hash
            String salt = hash.substring(0, 29); // BCrypt salt is 29 characters
            String expectedHash = hashpw(password, salt);
            return expectedHash.equals(hash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Base64 encoding for BCrypt format.
     * @param data The data to encode
     * @return Base64 encoded string
     */
    private static String base64Encode(byte[] data) {
        String chars = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < data.length) {
            int b1 = data[i++] & 0xFF;
            result.append(chars.charAt(b1 & 0x3F));
            
            if (i < data.length) {
                int b2 = data[i++] & 0xFF;
                result.append(chars.charAt(((b1 >> 6) & 0x03) | ((b2 << 2) & 0x3C)));
                
                if (i < data.length) {
                    int b3 = data[i++] & 0xFF;
                    result.append(chars.charAt(((b2 >> 4) & 0x0F) | ((b3 << 4) & 0x30)));
                    result.append(chars.charAt((b3 >> 2) & 0x3F));
                } else {
                    result.append(chars.charAt((b2 >> 4) & 0x0F));
                }
            } else {
                result.append(chars.charAt((b1 >> 6) & 0x03));
            }
        }
        
        return result.toString();
    }
}
