import org.junit.platform.console.ConsoleLauncher;

/**
 * Comprehensive test runner for all JUnit test cases.
 * This class provides a main method to run all test classes.
 */
public class ComprehensiveTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE JUNIT TEST SUITE ===");
        System.out.println("Running all test classes for the 3D Printing Service System");
        System.out.println();
        
        // List of all test classes
        String[] testClasses = {
            "UserTest",
            "AdminUserTest", 
            "MaterialTest",
            "OrderTest",
            "InvoiceTest",
            "OrderManagerTest",
            "InventoryTest",
            "SystemConfigTest",
            "ExceptionClassesTest",
            "IntegrationTest"
        };
        
        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        
        for (String testClass : testClasses) {
            System.out.println("Running " + testClass + "...");
            try {
                // Use reflection to run each test class
                Class<?> clazz = Class.forName(testClass);
                java.lang.reflect.Method mainMethod = clazz.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{});
                System.out.println("✓ " + testClass + " completed successfully");
                passedTests++;
            } catch (Exception e) {
                System.out.println("✗ " + testClass + " failed: " + e.getMessage());
                failedTests++;
            }
            totalTests++;
        }
        
        System.out.println();
        System.out.println("=== TEST SUMMARY ===");
        System.out.println("Total test classes: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success rate: " + (passedTests * 100 / totalTests) + "%");
        
        if (failedTests == 0) {
            System.out.println("🎉 All tests passed successfully!");
        } else {
            System.out.println("⚠️  Some tests failed. Please review the output above.");
        }
    }
}
