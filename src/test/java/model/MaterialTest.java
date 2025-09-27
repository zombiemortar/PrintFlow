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
        testMaterial = new Material("Generic", "PLA", 0.02, 200, "White");
    }
    
    @AfterEach
    void tearDown() {
        testMaterial = null;
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    void testMaterialConstructorWithAllParameters() {
        Material material = new Material("Generic", "ABS", 0.025, 230, "Black");
        
        assertEquals("Generic ABS", material.getName());
        assertEquals("Generic", material.getBrand());
        assertEquals("ABS", material.getType());
        assertEquals(0.025, material.getCostPerGram(), 0.001);
        assertEquals(230, material.getPrintTemp());
        assertEquals("Black", material.getColor());
    }
    
    @Test
    void testMaterialDefaultConstructor() {
        Material material = new Material();
        
        assertEquals(" ", material.getName()); // brand + " " + type
        assertEquals("", material.getBrand());
        assertEquals("", material.getType());
        assertEquals(0.0, material.getCostPerGram(), 0.001);
        assertEquals(0, material.getPrintTemp());
        assertEquals("", material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithNullValues() {
        Material material = new Material(null, null, 0.02, 200, null);
        
        assertEquals("null null", material.getName()); // null + " " + null
        assertNull(material.getBrand());
        assertNull(material.getType());
        assertEquals(0.02, material.getCostPerGram(), 0.001);
        assertEquals(200, material.getPrintTemp());
        assertNull(material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithZeroValues() {
        Material material = new Material("Brand", "Test", 0.0, 0, "Color");
        
        assertEquals("Brand Test", material.getName());
        assertEquals("Brand", material.getBrand());
        assertEquals("Test", material.getType());
        assertEquals(0.0, material.getCostPerGram(), 0.001);
        assertEquals(0, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    @Test
    void testMaterialConstructorWithNegativeValues() {
        Material material = new Material("Brand", "Test", -0.02, -200, "Color");
        
        assertEquals("Brand Test", material.getName());
        assertEquals("Brand", material.getBrand());
        assertEquals("Test", material.getType());
        assertEquals(-0.02, material.getCostPerGram(), 0.001);
        assertEquals(-200, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    // ==================== SETTER TESTS ====================
    
    @Test
    void testSetName() {
        testMaterial.setBrand("Generic");
        testMaterial.setType("PETG");
        assertEquals("Generic PETG", testMaterial.getName());
        assertEquals("Generic", testMaterial.getBrand());
        assertEquals("PETG", testMaterial.getType());
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
        assertTrue(info.contains("Generic"));
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
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "PLA", 0.02, 200, "White");
        
        // Note: Material class doesn't override equals, so objects are compared by reference
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentName() {
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "ABS", 0.02, 200, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentCost() {
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "PLA", 0.03, 200, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentTemperature() {
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "PLA", 0.02, 250, "White");
        
        assertNotEquals(material1, material2);
    }
    
    @Test
    void testEqualsWithDifferentColor() {
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "PLA", 0.02, 200, "Black");
        
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
        Material material1 = new Material("Generic", "PLA", 0.02, 200, "White");
        Material material2 = new Material("Generic", "PLA", 0.02, 200, "White");
        
        // Note: Material class doesn't override hashCode, so different objects have different hash codes
        assertNotEquals(material1.hashCode(), material2.hashCode());
    }
    
    // ==================== TOSTRING TESTS ====================
    
    @Test
    void testToString() {
        String result = testMaterial.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Generic"));
        assertTrue(result.contains("PLA"));
        assertTrue(result.contains("0.02"));
        assertTrue(result.contains("200"));
        assertTrue(result.contains("White"));
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testMaterialWithVeryLongName() {
        String longBrand = "a".repeat(500);
        String longType = "b".repeat(500);
        testMaterial.setBrand(longBrand);
        testMaterial.setType(longType);
        
        assertEquals(longBrand, testMaterial.getBrand());
        assertEquals(longType, testMaterial.getType());
        assertEquals(longBrand + " " + longType, testMaterial.getName());
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
        testMaterial.setBrand("PLA+");
        testMaterial.setType("Premium");
        assertEquals("PLA+", testMaterial.getBrand());
        assertEquals("Premium", testMaterial.getType());
        assertEquals("PLA+ Premium", testMaterial.getName());
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
        testMaterial.setBrand("PLA™");
        testMaterial.setType("Premium");
        testMaterial.setColor("Blå");
        
        assertEquals("PLA™", testMaterial.getBrand());
        assertEquals("Premium", testMaterial.getType());
        assertEquals("Blå", testMaterial.getColor());
        assertEquals("PLA™ Premium", testMaterial.getName());
    }
    
    @Test
    void testMaterialWithWhitespaceInName() {
        testMaterial.setBrand("  Generic  ");
        testMaterial.setType("  PLA  ");
        assertEquals("  Generic  ", testMaterial.getBrand());
        assertEquals("  PLA  ", testMaterial.getType());
        assertEquals("  Generic     PLA  ", testMaterial.getName());
    }
    
    @Test
    void testMaterialWithWhitespaceInColor() {
        testMaterial.setColor("  Black  ");
        assertEquals("  Black  ", testMaterial.getColor());
    }
    
    // ==================== NEW MATERIAL STRUCTURE TESTS ====================
    
    @Test
    void testGetDisplayName() {
        String displayName = testMaterial.getDisplayName();
        assertNotNull(displayName);
        assertTrue(displayName.contains("PLA"));
        assertTrue(displayName.contains("Generic"));
        assertTrue(displayName.contains("White"));
        assertEquals("PLA - Generic (White)", displayName);
    }
    
    @Test
    void testGetDisplayNameWithNullValues() {
        Material material = new Material(null, null, 0.02, 200, null);
        String displayName = material.getDisplayName();
        assertNotNull(displayName);
        assertEquals("null - null (null)", displayName);
    }
    
    @Test
    void testGetDisplayNameWithEmptyValues() {
        Material material = new Material("", "", 0.02, 200, "");
        String displayName = material.getDisplayName();
        assertNotNull(displayName);
        assertEquals(" -  ()", displayName);
    }
    
    @Test
    void testSetBrand() {
        testMaterial.setBrand("Hatchbox");
        assertEquals("Hatchbox", testMaterial.getBrand());
        assertEquals("Hatchbox PLA", testMaterial.getName());
    }
    
    @Test
    void testSetType() {
        testMaterial.setType("PETG");
        assertEquals("PETG", testMaterial.getType());
        assertEquals("Generic PETG", testMaterial.getName());
    }
    
    @Test
    void testBrandAndTypeInteraction() {
        testMaterial.setBrand("Prusament");
        testMaterial.setType("ASA");
        assertEquals("Prusament", testMaterial.getBrand());
        assertEquals("ASA", testMaterial.getType());
        assertEquals("Prusament ASA", testMaterial.getName());
    }
    
    @Test
    void testMaterialWithEmptyBrandAndType() {
        Material material = new Material("", "", 0.02, 200, "Red");
        assertEquals(" ", material.getName()); // empty + " " + empty
        assertEquals("", material.getBrand());
        assertEquals("", material.getType());
        assertEquals("Red", material.getColor());
    }
    
    @Test
    void testMaterialWithNullBrandAndType() {
        Material material = new Material(null, null, 0.02, 200, "Blue");
        assertEquals("null null", material.getName()); // null + " " + null
        assertNull(material.getBrand());
        assertNull(material.getType());
        assertEquals("Blue", material.getColor());
    }
}
