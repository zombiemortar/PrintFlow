import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Comprehensive JUnit test cases for the Invoice class.
 * Tests all methods, constructors, invoice generation, and edge cases.
 */
public class InvoiceTest {
    
    private Invoice testInvoice;
    private Order testOrder;
    private User testUser;
    private Material testMaterial;
    
    @BeforeEach
    void setUp() {
        // Reset system state before each test
        SystemConfig.resetToDefaults();
        
        // Create test objects
        testUser = new User("testuser", "test@example.com", "customer");
        testMaterial = new Material("PLA", 0.02, 200, "White");
        testOrder = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        testInvoice = new Invoice(testOrder, 25.50);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        testInvoice = null;
        testOrder = null;
        testUser = null;
        testMaterial = null;
    }
    
    // Constructor Tests
    @Test
    void testParameterizedConstructor() {
        Invoice invoice = new Invoice(testOrder, 30.00);
        assertEquals(testOrder, invoice.getOrder());
        assertEquals(30.00, invoice.getTotalCost());
        assertNotNull(invoice.getDateIssued());
        assertTrue(invoice.getInvoiceID() > 0);
    }
    
    @Test
    void testDefaultConstructor() {
        Invoice invoice = new Invoice();
        assertNull(invoice.getOrder());
        assertEquals(0.0, invoice.getTotalCost());
        assertNotNull(invoice.getDateIssued());
        assertTrue(invoice.getInvoiceID() > 0);
    }
    
    @Test
    void testConstructorWithNullOrder() {
        Invoice invoice = new Invoice(null, 25.50);
        assertNull(invoice.getOrder());
        assertEquals(25.50, invoice.getTotalCost());
        assertNotNull(invoice.getDateIssued());
    }
    
    @Test
    void testConstructorWithZeroCost() {
        Invoice invoice = new Invoice(testOrder, 0.0);
        assertEquals(testOrder, invoice.getOrder());
        assertEquals(0.0, invoice.getTotalCost());
        assertNotNull(invoice.getDateIssued());
    }
    
    @Test
    void testConstructorWithNegativeCost() {
        Invoice invoice = new Invoice(testOrder, -10.0);
        assertEquals(testOrder, invoice.getOrder());
        assertEquals(-10.0, invoice.getTotalCost());
        assertNotNull(invoice.getDateIssued());
    }
    
    @Test
    void testInvoiceIDIncrement() {
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Invoice invoice3 = new Invoice();
        
        assertTrue(invoice2.getInvoiceID() > invoice1.getInvoiceID());
        assertTrue(invoice3.getInvoiceID() > invoice2.getInvoiceID());
    }
    
    // Getter and Setter Tests
    @Test
    void testOrderGetterAndSetter() {
        Order newOrder = new Order();
        testInvoice.setOrder(newOrder);
        assertEquals(newOrder, testInvoice.getOrder());
    }
    
    @Test
    void testTotalCostGetterAndSetter() {
        testInvoice.setTotalCost(50.00);
        assertEquals(50.00, testInvoice.getTotalCost());
    }
    
    @Test
    void testDateIssuedGetterAndSetter() {
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        testInvoice.setDateIssued(newDate);
        assertEquals(newDate, testInvoice.getDateIssued());
    }
    
    @Test
    void testSetOrderWithNull() {
        testInvoice.setOrder(null);
        assertNull(testInvoice.getOrder());
    }
    
    @Test
    void testSetTotalCostWithZero() {
        testInvoice.setTotalCost(0.0);
        assertEquals(0.0, testInvoice.getTotalCost());
    }
    
    @Test
    void testSetTotalCostWithNegativeValue() {
        testInvoice.setTotalCost(-15.50);
        assertEquals(-15.50, testInvoice.getTotalCost());
    }
    
    @Test
    void testSetDateIssuedWithNull() {
        testInvoice.setDateIssued(null);
        assertNull(testInvoice.getDateIssued());
    }
    
    // generateSummary Tests
    @Test
    void testGenerateSummaryWithValidOrder() {
        String summary = testInvoice.generateSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("INVOICE SUMMARY"));
        assertTrue(summary.contains("Invoice ID:"));
        assertTrue(summary.contains("Date Issued:"));
        assertTrue(summary.contains("Order ID:"));
        assertTrue(summary.contains("Customer:"));
        assertTrue(summary.contains("Material:"));
        assertTrue(summary.contains("Quantity:"));
        assertTrue(summary.contains("Total Cost:"));
        assertTrue(summary.contains("Status:"));
        assertTrue(summary.contains("testuser"));
        assertTrue(summary.contains("PLA"));
        assertTrue(summary.contains("2"));
        assertTrue(summary.contains("25.50"));
        assertTrue(summary.contains("pending"));
    }
    
    @Test
    void testGenerateSummaryWithNullOrder() {
        Invoice invoice = new Invoice(null, 25.50);
        String summary = invoice.generateSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("No order associated"));
        assertTrue(summary.contains("Invoice #"));
    }
    
    @Test
    void testGenerateSummaryWithNullUser() {
        Order orderWithNullUser = new Order(null, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        Invoice invoice = new Invoice(orderWithNullUser, 25.50);
        String summary = invoice.generateSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("Unknown"));
    }
    
    @Test
    void testGenerateSummaryWithNullMaterial() {
        Order orderWithNullMaterial = new Order(testUser, null, "10cm x 5cm x 3cm", 2, "High quality");
        Invoice invoice = new Invoice(orderWithNullMaterial, 25.50);
        String summary = invoice.generateSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("Unknown"));
    }
    
    @Test
    void testGenerateSummaryDateFormat() {
        String summary = testInvoice.generateSummary();
        // Should contain date in format "yyyy-MM-dd HH:mm:ss"
        assertTrue(summary.matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"));
    }
    
    // exportInvoice Tests
    @Test
    void testExportInvoiceWithValidOrder() {
        String invoice = testInvoice.exportInvoice();
        assertNotNull(invoice);
        assertTrue(invoice.contains("3D PRINTING SERVICE INVOICE"));
        assertTrue(invoice.contains("Invoice ID:"));
        assertTrue(invoice.contains("Date Issued:"));
        assertTrue(invoice.contains("ORDER DETAILS:"));
        assertTrue(invoice.contains("Order ID:"));
        assertTrue(invoice.contains("Customer:"));
        assertTrue(invoice.contains("Email:"));
        assertTrue(invoice.contains("Material:"));
        assertTrue(invoice.contains("Dimensions:"));
        assertTrue(invoice.contains("Quantity:"));
        assertTrue(invoice.contains("Special Instructions:"));
        assertTrue(invoice.contains("COST BREAKDOWN:"));
        assertTrue(invoice.contains("Material Cost:"));
        assertTrue(invoice.contains("Base Setup Cost:"));
        assertTrue(invoice.contains("Total Cost:"));
        assertTrue(invoice.contains("ORDER STATUS:"));
        assertTrue(invoice.contains("Thank you for choosing our 3D printing service!"));
        assertTrue(invoice.contains("testuser"));
        assertTrue(invoice.contains("test@example.com"));
        assertTrue(invoice.contains("PLA"));
        assertTrue(invoice.contains("10cm x 5cm x 3cm"));
        assertTrue(invoice.contains("2"));
        assertTrue(invoice.contains("High quality"));
        assertTrue(invoice.contains("25.50"));
        assertTrue(invoice.contains("pending"));
    }
    
    @Test
    void testExportInvoiceWithNullOrder() {
        Invoice invoice = new Invoice(null, 25.50);
        String result = invoice.exportInvoice();
        assertNotNull(result);
        assertTrue(result.contains("No order associated"));
        assertTrue(result.contains("Invoice #"));
    }
    
    @Test
    void testExportInvoiceWithNullUser() {
        Order orderWithNullUser = new Order(null, testMaterial, "10cm x 5cm x 3cm", 2, "High quality");
        Invoice invoice = new Invoice(orderWithNullUser, 25.50);
        String result = invoice.exportInvoice();
        assertNotNull(result);
        assertTrue(result.contains("Unknown"));
    }
    
    @Test
    void testExportInvoiceWithNullMaterial() {
        Order orderWithNullMaterial = new Order(testUser, null, "10cm x 5cm x 3cm", 2, "High quality");
        Invoice invoice = new Invoice(orderWithNullMaterial, 25.50);
        String result = invoice.exportInvoice();
        assertNotNull(result);
        assertTrue(result.contains("Unknown"));
    }
    
    @Test
    void testExportInvoiceDateFormat() {
        String invoice = testInvoice.exportInvoice();
        // Should contain date in format "yyyy-MM-dd HH:mm:ss"
        assertTrue(invoice.matches(".*\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*"));
    }
    
    @Test
    void testExportInvoiceMaterialCostCalculation() {
        String invoice = testInvoice.exportInvoice();
        // Material cost should be calculated as: quantity * 10g per item * cost per gram
        // 2 * 10 * 0.02 = 0.40
        assertTrue(invoice.contains("0.40"));
    }
    
    @Test
    void testExportInvoiceBaseSetupCost() {
        String invoice = testInvoice.exportInvoice();
        // Base setup cost should be from SystemConfig
        assertTrue(invoice.contains("5.00"));
    }
    
    // toString Tests
    @Test
    void testToString() {
        String result = testInvoice.toString();
        assertTrue(result.contains("Invoice{"));
        assertTrue(result.contains("invoiceID="));
        assertTrue(result.contains("order="));
        assertTrue(result.contains("totalCost=25.5"));
        assertTrue(result.contains("dateIssued="));
    }
    
    @Test
    void testToStringWithNullOrder() {
        Invoice invoice = new Invoice(null, 25.50);
        String result = invoice.toString();
        assertTrue(result.contains("null"));
    }
    
    @Test
    void testToStringWithZeroCost() {
        Invoice invoice = new Invoice(testOrder, 0.0);
        String result = invoice.toString();
        assertTrue(result.contains("totalCost=0.0"));
    }
    
    // Edge Cases
    @Test
    void testInvoiceWithVeryHighCost() {
        Invoice invoice = new Invoice(testOrder, 99999.99);
        String summary = invoice.generateSummary();
        assertTrue(summary.contains("99999.99"));
    }
    
    @Test
    void testInvoiceWithVeryPreciseCost() {
        Invoice invoice = new Invoice(testOrder, 25.123456789);
        String summary = invoice.generateSummary();
        assertTrue(summary.contains("25.12")); // Should be rounded to 2 decimal places
    }
    
    @Test
    void testInvoiceWithNegativeCost() {
        Invoice invoice = new Invoice(testOrder, -10.50);
        String summary = invoice.generateSummary();
        assertTrue(summary.contains("-10.50"));
    }
    
    @Test
    void testInvoiceWithOrderHavingSpecialCharacters() {
        Order orderWithSpecialChars = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, "Special chars: !@#$%^&*()");
        Invoice invoice = new Invoice(orderWithSpecialChars, 25.50);
        String result = invoice.exportInvoice();
        assertTrue(result.contains("Special chars: !@#$%^&*()"));
    }
    
    @Test
    void testInvoiceWithOrderHavingUnicodeCharacters() {
        User userWithUnicode = new User("usér", "user@tëst.com", "customer");
        Order orderWithUnicode = new Order(userWithUnicode, testMaterial, "10cm x 5cm x 3cm", 2, "Special chars: ñáéíóú");
        Invoice invoice = new Invoice(orderWithUnicode, 25.50);
        String result = invoice.exportInvoice();
        assertTrue(result.contains("usér"));
        assertTrue(result.contains("user@tëst.com"));
        assertTrue(result.contains("Special chars: ñáéíóú"));
    }
    
    @Test
    void testInvoiceWithOrderHavingVeryLongInstructions() {
        String longInstructions = "This is a very long set of special instructions that contains many details about how the print should be completed with specific requirements and preferences that exceed normal length";
        Order orderWithLongInstructions = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 2, longInstructions);
        Invoice invoice = new Invoice(orderWithLongInstructions, 25.50);
        String result = invoice.exportInvoice();
        assertTrue(result.contains(longInstructions));
    }
    
    @Test
    void testInvoiceWithOrderHavingVeryLongDimensions() {
        String longDimensions = "100cm x 50cm x 30cm with very detailed specifications and additional notes";
        Order orderWithLongDimensions = new Order(testUser, testMaterial, longDimensions, 2, "High quality");
        Invoice invoice = new Invoice(orderWithLongDimensions, 25.50);
        String result = invoice.exportInvoice();
        assertTrue(result.contains(longDimensions));
    }
    
    @Test
    void testInvoiceWithOrderHavingZeroQuantity() {
        Order orderWithZeroQuantity = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 0, "High quality");
        Invoice invoice = new Invoice(orderWithZeroQuantity, 0.0);
        String result = invoice.exportInvoice();
        assertTrue(result.contains("Quantity: 0"));
        assertTrue(result.contains("Total Cost: $0.00"));
    }
    
    @Test
    void testInvoiceWithOrderHavingLargeQuantity() {
        Order orderWithLargeQuantity = new Order(testUser, testMaterial, "10cm x 5cm x 3cm", 1000, "High quality");
        Invoice invoice = new Invoice(orderWithLargeQuantity, 25000.00);
        String result = invoice.exportInvoice();
        assertTrue(result.contains("Quantity: 1000"));
        assertTrue(result.contains("Total Cost: $25000.00"));
    }
}
