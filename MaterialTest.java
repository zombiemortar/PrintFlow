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
    
    // Constructor Tests
    @Test
    void testParameterizedConstructor() {
        Material material = new Material("ABS", 0.025, 230, "Black");
        assertEquals("ABS", material.getName());
        assertEquals(0.025, material.getCostPerGram());
        assertEquals(230, material.getPrintTemp());
        assertEquals("Black", material.getColor());
    }
    
    @Test
    void testDefaultConstructor() {
        Material material = new Material();
        assertEquals("", material.getName());
        assertEquals(0.0, material.getCostPerGram());
        assertEquals(0, material.getPrintTemp());
        assertEquals("", material.getColor());
    }
    
    @Test
    void testConstructorWithNullValues() {
        Material material = new Material(null, 0.02, 200, null);
        assertNull(material.getName());
        assertEquals(0.02, material.getCostPerGram());
        assertEquals(200, material.getPrintTemp());
        assertNull(material.getColor());
    }
    
    @Test
    void testConstructorWithZeroValues() {
        Material material = new Material("Test", 0.0, 0, "Color");
        assertEquals("Test", material.getName());
        assertEquals(0.0, material.getCostPerGram());
        assertEquals(0, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    @Test
    void testConstructorWithNegativeValues() {
        Material material = new Material("Test", -0.02, -200, "Color");
        assertEquals("Test", material.getName());
        assertEquals(-0.02, material.getCostPerGram());
        assertEquals(-200, material.getPrintTemp());
        assertEquals("Color", material.getColor());
    }
    
    // Getter and Setter Tests
    @Test
    void testNameGetterAndSetter() {
        testMaterial.setName("NewPLA");
        assertEquals("NewPLA", testMaterial.getName());
    }
    
    @Test
    void testCostPerGramGetterAndSetter() {
        testMaterial.setCostPerGram(0.03);
        assertEquals(0.03, testMaterial.getCostPerGram());
    }
    
    @Test
    void testPrintTempGetterAndSetter() {
        testMaterial.setPrintTemp(220);
        assertEquals(220, testMaterial.getPrintTemp());
    }
    
    @Test
    void testColorGetterAndSetter() {
        testMaterial.setColor("Red");
        assertEquals("Red", testMaterial.getColor());
    }
    
    @Test
    void testSetNameWithNull() {
        testMaterial.setName(null);
        assertNull(testMaterial.getName());
    }
    
    @Test
    void testSetCostPerGramWithNegativeValue() {
        testMaterial.setCostPerGram(-0.01);
        assertEquals(-0.01, testMaterial.getCostPerGram());
    }
    
    @Test
    void testSetPrintTempWithNegativeValue() {
        testMaterial.setPrintTemp(-100);
        assertEquals(-100, testMaterial.getPrintTemp());
    }
    
    @Test
    void testSetColorWithNull() {
        testMaterial.setColor(null);
        assertNull(testMaterial.getColor());
    }
    
    @Test
    void testSetCostPerGramWithZero() {
        testMaterial.setCostPerGram(0.0);
        assertEquals(0.0, testMaterial.getCostPerGram());
    }
    
    @Test
    void testSetPrintTempWithZero() {
        testMaterial.setPrintTemp(0);
        assertEquals(0, testMaterial.getPrintTemp());
    }
    
    // getMaterialInfo Tests
    @Test
    void testGetMaterialInfo() {
        String info = testMaterial.getMaterialInfo();
        assertTrue(info.contains("PLA"));
        assertTrue(info.contains("0.02"));
        assertTrue(info.contains("200"));
        assertTrue(info.contains("White"));
        assertTrue(info.contains("Material:"));
        assertTrue(info.contains("Cost per gram:"));
        assertTrue(info.contains("Print temperature:"));
        assertTrue(info.contains("Color:"));
    }
    
    @Test
    void testGetMaterialInfoWithNullValues() {
        Material material = new Material(null, 0.02, 200, null);
        String info = material.getMaterialInfo();
        assertTrue(info.contains("null"));
        assertTrue(info.contains("0.02"));
        assertTrue(info.contains("200"));
    }
    
    @Test
    void testGetMaterialInfoWithEmptyValues() {
        Material material = new Material("", 0.0, 0, "");
        String info = material.getMaterialInfo();
        assertTrue(info.contains("Cost per gram: $0.00"));
        assertTrue(info.contains("Print temperature: 0°C"));
    }
    
    @Test
    void testGetMaterialInfoWithSpecialCharacters() {
        Material material = new Material("PLA+", 0.025, 220, "Black/White");
        String info = material.getMaterialInfo();
        assertTrue(info.contains("PLA+"));
        assertTrue(info.contains("Black/White"));
    }
    
    @Test
    void testGetMaterialInfoWithHighPrecisionCost() {
        Material material = new Material("Test", 0.123456789, 200, "Color");
        String info = material.getMaterialInfo();
        assertTrue(info.contains("0.12")); // Should be rounded to 2 decimal places
    }
    
    // toString Tests
    @Test
    void testToString() {
        String result = testMaterial.toString();
        assertTrue(result.contains("PLA"));
        assertTrue(result.contains("0.02"));
        assertTrue(result.contains("200"));
        assertTrue(result.contains("White"));
        assertTrue(result.contains("Material{"));
    }
    
    @Test
    void testToStringWithNullValues() {
        Material material = new Material(null, 0.02, 200, null);
        String result = material.toString();
        assertTrue(result.contains("null"));
        assertTrue(result.contains("0.02"));
        assertTrue(result.contains("200"));
    }
    
    @Test
    void testToStringWithEmptyValues() {
        Material material = new Material("", 0.0, 0, "");
        String result = material.toString();
        assertTrue(result.contains("name=''"));
        assertTrue(result.contains("costPerGram=0.0"));
        assertTrue(result.contains("printTemp=0"));
        assertTrue(result.contains("color=''"));
    }
    
    // Edge Cases
    @Test
    void testMaterialWithVeryLongName() {
        String longName = "Very Long Material Name That Exceeds Normal Length And Contains Many Characters";
        Material material = new Material(longName, 0.02, 200, "White");
        assertEquals(longName, material.getName());
        assertTrue(material.getMaterialInfo().contains(longName));
    }
    
    @Test
    void testMaterialWithVeryHighCost() {
        Material material = new Material("Expensive", 999.99, 200, "Gold");
        assertEquals(999.99, material.getCostPerGram());
        assertTrue(material.getMaterialInfo().contains("999.99"));
    }
    
    @Test
    void testMaterialWithVeryHighTemperature() {
        Material material = new Material("HighTemp", 0.02, 1000, "Black");
        assertEquals(1000, material.getPrintTemp());
        assertTrue(material.getMaterialInfo().contains("1000°C"));
    }
    
    @Test
    void testMaterialWithVeryLowTemperature() {
        Material material = new Material("LowTemp", 0.02, -100, "Blue");
        assertEquals(-100, material.getPrintTemp());
        assertTrue(material.getMaterialInfo().contains("-100°C"));
    }
    
    @Test
    void testMaterialWithSpecialCharactersInName() {
        Material material = new Material("PLA+", 0.02, 200, "White");
        assertEquals("PLA+", material.getName());
    }
    
    @Test
    void testMaterialWithSpecialCharactersInColor() {
        Material material = new Material("ABS", 0.02, 200, "Black/White");
        assertEquals("Black/White", material.getColor());
    }
    
    @Test
    void testMaterialWithUnicodeCharacters() {
        Material material = new Material("PLA™", 0.02, 200, "Blå");
        assertEquals("PLA™", material.getName());
        assertEquals("Blå", material.getColor());
    }
    
    @Test
    void testMaterialWithWhitespaceInName() {
        Material material = new Material("  PLA  ", 0.02, 200, "White");
        assertEquals("  PLA  ", material.getName()); // Should preserve whitespace
    }
    
    @Test
    void testMaterialWithWhitespaceInColor() {
        Material material = new Material("ABS", 0.02, 200, "  Black  ");
        assertEquals("  Black  ", material.getColor()); // Should preserve whitespace
    }
    
    @Test
    void testMaterialWithDecimalPrecision() {
        Material material = new Material("Test", 0.123456789, 200, "Color");
        assertEquals(0.123456789, material.getCostPerGram());
    }
    
    @Test
    void testMaterialWithScientificNotation() {
        Material material = new Material("Test", 1.23E-2, 200, "Color");
        assertEquals(1.23E-2, material.getCostPerGram());
    }
}
