package org.junit.jupiter.api;

public final class Assertions {
    private Assertions() {}

    public static void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Expected condition to be true");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("Expected condition to be false");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }

    public static void assertNull(Object obj) {
        if (obj != null) throw new AssertionError("Expected null but was non-null");
    }
    public static void assertNull(Object obj, String message) {
        if (obj != null) throw new AssertionError(message);
    }

    public static void assertNotNull(Object obj) {
        if (obj == null) throw new AssertionError("Expected non-null but was null");
    }
    public static void assertNotNull(Object obj, String message) {
        if (obj == null) throw new AssertionError(message);
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }
    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " [Expected: " + expected + ", Actual: " + actual + "]");
        }
    }

    public static void assertEquals(double expected, double actual, double delta) {
        if (Double.isNaN(expected) || Double.isNaN(actual)) {
            if (!(Double.isNaN(expected) && Double.isNaN(actual))) {
                throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
            }
            return;
        }
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual + ", Delta: " + delta);
        }
    }
    public static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " [Expected: " + expected + ", Actual: " + actual + "]");
        }
    }
    public static void assertEquals(String expected, String actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " [Expected: " + expected + ", Actual: " + actual + "]");
        }
    }

    public static void assertNotEquals(double unexpected, double actual) {
        if (!Double.isNaN(unexpected) && !Double.isNaN(actual) && Double.compare(unexpected, actual) == 0) {
            throw new AssertionError("Did not expect: " + unexpected);
        }
    }

    public static void assertNotEquals(Object unexpected, Object actual) {
        if (unexpected == null ? actual == null : unexpected.equals(actual)) {
            throw new AssertionError("Did not expect: " + unexpected);
        }
    }
    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        if (unexpected == null ? actual == null : unexpected.equals(actual)) {
            throw new AssertionError(message);
        }
    }

    public static void assertNotSame(Object unexpected, Object actual) {
        if (unexpected == actual) {
            throw new AssertionError("Did not expect same object reference");
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    public static <T extends Throwable> void assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) return;
            throw new AssertionError("Expected exception: " + expectedType.getName() + ", but got: " + t.getClass().getName(), t);
        }
        throw new AssertionError("Expected exception: " + expectedType.getName() + ", but nothing was thrown");
    }

    public static void assertDoesNotThrow(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            throw new AssertionError("Expected no exception, but got: " + t.getClass().getName(), t);
        }
    }

    public interface Executable {
        void execute() throws Throwable;
    }
}


