package gui.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import util.ValidationResult;
import util.SystemConfig;
import util.InputValidator;

/**
 * Unit tests for controller validation logic.
 * Tests validation methods without UI dependencies.
 */
public class ControllerValidationTest {

    @Test
    @DisplayName("Should validate material names correctly")
    void testMaterialNameValidation() {
        ValidationResult validResult = InputValidator.validateMaterialName("PLA");
        assertTrue(validResult.isValid(), "Valid material name should pass validation");
        
        ValidationResult invalidResult = InputValidator.validateMaterialName("");
        assertFalse(invalidResult.isValid(), "Empty material name should fail validation");
    }

    @Test
    @DisplayName("Should validate dimensions correctly")
    void testDimensionsValidation() {
        ValidationResult validResult = InputValidator.validateDimensions("10x5x3");
        assertTrue(validResult.isValid(), "Valid dimensions should pass validation");
        
        ValidationResult invalidResult = InputValidator.validateDimensions("invalid");
        assertFalse(invalidResult.isValid(), "Invalid dimensions should fail validation");
    }

    @Test
    @DisplayName("Should validate quantities correctly")
    void testQuantityValidation() {
        ValidationResult validResult = InputValidator.validateQuantity(5);
        assertTrue(validResult.isValid(), "Valid quantity should pass validation");
        
        ValidationResult invalidResult = InputValidator.validateQuantity(0);
        assertFalse(invalidResult.isValid(), "Zero quantity should fail validation");
        
        ValidationResult negativeResult = InputValidator.validateQuantity(-1);
        assertFalse(negativeResult.isValid(), "Negative quantity should fail validation");
    }

    @Test
    @DisplayName("Should validate usernames correctly")
    void testUsernameValidation() {
        ValidationResult validResult = InputValidator.validateUsername("testuser");
        assertTrue(validResult.isValid(), "Valid username should pass validation");
        
        ValidationResult invalidResult = InputValidator.validateUsername("");
        assertFalse(invalidResult.isValid(), "Empty username should fail validation");
    }

    @Test
    @DisplayName("Should validate email addresses correctly")
    void testEmailValidation() {
        ValidationResult validResult = InputValidator.validateEmail("test@example.com");
        assertTrue(validResult.isValid(), "Valid email should pass validation");
        
        ValidationResult invalidResult = InputValidator.validateEmail("invalid-email");
        assertFalse(invalidResult.isValid(), "Invalid email should fail validation");
    }

    @Test
    @DisplayName("Should validate instructions correctly")
    void testInstructionsValidation() {
        ValidationResult validResult = InputValidator.validateInstructions("Test instructions");
        assertTrue(validResult.isValid(), "Valid instructions should pass validation");
        
        // Empty instructions should be valid (optional field)
        ValidationResult emptyResult = InputValidator.validateInstructions("");
        assertTrue(emptyResult.isValid(), "Empty instructions should be valid");
    }

    @Test
    @DisplayName("Should handle SystemConfig validation")
    void testSystemConfigValidation() {
        // Test that SystemConfig can be reset to defaults
        SystemConfig.resetToDefaults();
        
        // Verify default values are reasonable
        assertTrue(SystemConfig.getBaseSetupCost() > 0, "Base setup cost should be positive");
        assertTrue(SystemConfig.getTaxRate() >= 0, "Tax rate should be non-negative");
        assertTrue(SystemConfig.getMaxOrderQuantity() > 0, "Max order quantity should be positive");
    }

    @Test
    @DisplayName("Should handle validation result aggregation")
    void testValidationResultAggregation() {
        ValidationResult result1 = new ValidationResult();
        result1.addError("Error 1");
        
        ValidationResult result2 = new ValidationResult();
        result2.addError("Error 2");
        
        result1.addErrors(result2.getErrors());
        
        assertFalse(result1.isValid(), "Combined result should be invalid");
        assertEquals(2, result1.getErrors().size(), "Should have 2 errors");
        assertTrue(result1.getErrors().contains("Error 1"));
        assertTrue(result1.getErrors().contains("Error 2"));
    }

    @Test
    @DisplayName("Should handle edge cases in validation")
    void testValidationEdgeCases() {
        // Test null inputs
        ValidationResult nullResult = InputValidator.validateMaterialName(null);
        assertFalse(nullResult.isValid(), "Null input should fail validation");
        
        // Test very long inputs
        String longString = "a".repeat(1000);
        ValidationResult longResult = InputValidator.validateMaterialName(longString);
        // Should either pass or fail gracefully, not crash
        assertNotNull(longResult, "Long input should not cause null result");
    }
}
