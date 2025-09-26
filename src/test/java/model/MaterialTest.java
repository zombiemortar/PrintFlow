package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit test cases for the Material class.
 * Tests all methods, constructors, and edge cases.
 */
public class MaterialTest {
    
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        testMaterial = new Material("PLA", 0.02, 200, "White");
    }
    
    @AfterEach
    void tearDown() {
        testMaterial = null;
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    void testMaterialConstructorWithAllParameters() {
        Material material = new Material("ABS", 0.025, 230, "Black");
        
        assertEquals("ABS", material.getName());
        assertEquals(0.025, material.getCostPerGram(), 0.001);
        assertEquals(230, material.getPrintTemp());
        assertEquals("Black", material.getColor());
    }
    
    @Test
    void testMaterialDefaultConstructor() {
        Material material = new Material();
        
        assertEquals("", material.getName());
        assertEquals(0.0, material.getCostPerGram(), 0.001);
        assertEquals(0, material.getPrintTemp());
        assertEquals("", material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithNullValues() {
        Material material = new Material(null, 0.02, 200, null);
        
        assertNull(material.getName());
        assertEquals(0.02, material.getCostPerGram(), 0.001);
        assertEquals(200, material.getPrintTemp());
        assertNull(material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithZeroValues() {
        Material material = new Material("Test", 0.0, 0, "Color");
        
        assertEquals("Test", material.getName());
        assertEquals(0.0, material.getCostPerGram(), 0.001);
        assertEquals(0, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithNegativeValues() {
        Material material = new Material("Test", -0.02, -200, "Color");
        
        assertEquals("Test", material.getName());
        assertEquals(-0.02, material.getCostPerGram(), 0.001);
        assertEquals(-200, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    // ==================== SETTER TESTS ====================
    
    @Test
    void testSetName() {
        testMaterial.setName("PETG");
        assertEquals("PETG", testMaterial.getName());
    }
    
    @Test
    void testSetCostPerGram() {
        testMaterial.setCostPerGram(0.03);
        assertEquals(0.03, testMaterial.getCostPerGram(), 0.001);
    }
    
    @Test
    void testSetPrintTemp() {
        testMaterial.setPrintTemp(250);
        assertEquals(250, testMaterial.getPrintTemp());
    }
    
    @Test
    void testSetColor() {
        testMaterial.setColor("Red");
        assertEquals("Red", testMaterial.getColor());
    }
    
    // ==================== MATERIAL INFO TESTS ====================
    
    @Test
    void testGetMaterialInfo() {
        String info = testMaterial.getMaterialInfo();
        assertNotNull(info);
        assertTrue(info.contains("PLA"));
        assertTrue(info.contains("0.02"));
        assertTrue(info.contains("200"));
        assertTrue(info.contains("White"));
    }
    
    // ==================== CALCULATION TESTS ====================
    
    @Test
    void testCostCalculation() {
        double weight = 50.0; // 50 grams
        double expectedCost = 50.0 * 0.02; // weight * cost per gram
        
        // Manual calculation since Material doesn't have calculateCostForWeight method
        double actualCost = weight * testMaterial.getCostPerGram();
        assertEquals(expectedCost, actualCost, 0.001);
    }
    
    // ==================== EQUALS AND HASHCODE TESTS ====================
    
    @Test
    void testEqualsWithSameMaterial() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("PLA", 0.02, 200, "White");
        
        // Note: Material class doesn't override equals, so objects are compared by reference
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentName() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("ABS", 0.02, 200, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentCost() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("PLA", 0.03, 200, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentTemperature() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("PLA", 0.02, 250, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentColor() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("PLA", 0.02, 200, "Black");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithNull() {
        assertNotEquals(testMaterial, null);
    }
    
    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(testMaterial, "not a material");
    }
    
    @Test
    void testHashCodeConsistency() {
        Material material1 = new Material("PLA", 0.02, 200, "White");
        Material material2 = new Material("PLA", 0.02, 200, "White");
        
        // Note: Material class doesn't override hashCode, so different objects have different hash codes
        assertNotEquals(material1.hashCode(), material2.hashCode());
    }
    
    // ==================== TOSTRING TESTS ====================
    
    @Test
    void testToString() {
        String result = testMaterial.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("PLA"));
        assertTrue(result.contains("0.02"));
        assertTrue(result.contains("200"));
        assertTrue(result.contains("White"));
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testMaterialWithVeryLongName() {
        String longName = "a".repeat(1000);
        testMaterial.setName(longName);
        
        assertEquals(longName, testMaterial.getName());
    }
    
    @Test
    void testMaterialWithVeryHighCost() {
        testMaterial.setCostPerGram(999.99);
        assertEquals(999.99, testMaterial.getCostPerGram(), 0.001);
    }
    
    @Test
    void testMaterialWithVeryHighTemperature() {
        testMaterial.setPrintTemp(1000);
        assertEquals(1000, testMaterial.getPrintTemp());
    }
    
    @Test
    void testMaterialWithVeryLowTemperature() {
        testMaterial.setPrintTemp(-100);
        assertEquals(-100, testMaterial.getPrintTemp());
    }
    
    @Test
    void testMaterialWithSpecialCharactersInName() {
        testMaterial.setName("PLA+");
        assertEquals("PLA+", testMaterial.getName());
    }
    
    @Test
    void testMaterialWithDecimalPrecision() {
        testMaterial.setCostPerGram(0.123456789);
        assertEquals(0.123456789, testMaterial.getCostPerGram(), 0.000000001);
    }
    
    @Test
    void testMaterialWithScientificNotation() {
        testMaterial.setCostPerGram(1.23E-2);
        assertEquals(0.0123, testMaterial.getCostPerGram(), 0.0001);
    }
    
    @Test
    void testMaterialWithUnicodeCharacters() {
        testMaterial.setName("PLA™");
        testMaterial.setColor("Blå");
        
        assertEquals("PLA™", testMaterial.getName());
        assertEquals("Blå", testMaterial.getColor());
    }
    
    @Test
    void testMaterialWithWhitespaceInName() {
        testMaterial.setName("  PLA  ");
        assertEquals("  PLA  ", testMaterial.getName());
    }
    
    @Test
    void testMaterialWithWhitespaceInColor() {
        testMaterial.setColor("  Black  ");
        assertEquals("  Black  ", testMaterial.getColor());
    }
}
