import org.junit.platform.console.ConsoleLauncher;

/**
 * Simple JUnit test runner using the ConsoleLauncher.
 * This can be used to run all JUnit tests from the command line.
 */
public class JUnitTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== JUNIT TEST RUNNER ===");
        System.out.println("Running comprehensive test suite for 3D Printing Service System");
        System.out.println();
        
        // Use ConsoleLauncher to run all tests
        String[] launcherArgs = {
            "--scan-classpath",
            "--details=verbose",
            "--reports-dir=test-reports"
        };
        
        try {
            ConsoleLauncher.main(launcherArgs);
        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
