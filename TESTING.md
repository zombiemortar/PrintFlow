# Comprehensive Unit Testing Documentation

## Overview

This document describes the comprehensive JUnit test suite implemented for the 3D Printing Service System. The test suite covers all classes and methods with extensive test cases including edge cases, error conditions, and integration scenarios.

## Test Classes

### 1. UserTest.java
- **Purpose**: Tests the base User class functionality
- **Coverage**: 
  - Constructor tests (parameterized and default)
  - Getter/setter methods
  - submitOrder() method with various scenarios
  - viewInvoice() method
  - toString() method
  - Edge cases with null values, invalid inputs, and boundary conditions

### 2. AdminUserTest.java
- **Purpose**: Tests the AdminUser subclass functionality
- **Coverage**:
  - Constructor tests and inheritance
  - Role management (admin role enforcement)
  - addMaterial() method with validation
  - viewAllOrders() method
  - modifyPricingConstants() method
  - toString() method
  - Edge cases and error conditions

### 3. MaterialTest.java
- **Purpose**: Tests the Material class functionality
- **Coverage**:
  - Constructor tests with various inputs
  - Getter/setter methods
  - getMaterialInfo() method formatting
  - toString() method
  - Edge cases with special characters, unicode, and extreme values

### 4. OrderTest.java
- **Purpose**: Tests the Order class functionality
- **Coverage**:
  - Constructor tests and order ID generation
  - Getter/setter methods
  - updateStatus() method validation
  - setPriority() method
  - calculatePrice() method with various scenarios (bulk discounts, VIP discounts, rush surcharges)
  - estimatePrintTimeHours() method with different dimension formats
  - toString() method
  - Edge cases and complex pricing scenarios

### 5. InvoiceTest.java
- **Purpose**: Tests the Invoice class functionality
- **Coverage**:
  - Constructor tests and invoice ID generation
  - Getter/setter methods
  - generateSummary() method formatting
  - exportInvoice() method with detailed formatting
  - toString() method
  - Edge cases with null orders, special characters, and unicode

### 6. OrderManagerTest.java
- **Purpose**: Tests the OrderManager static methods
- **Coverage**:
  - registerOrder() method
  - getOrderById() method
  - getAllOrders() method
  - enqueueOrder() and dequeueNextOrder() methods (FIFO behavior)
  - getQueueSize() method
  - Integration tests with complete workflows
  - Edge cases with large numbers of orders

### 7. InventoryTest.java
- **Purpose**: Tests the Inventory static methods
- **Coverage**:
  - setStock() method with validation
  - getStock() method with default values
  - hasSufficient() method with various scenarios
  - consume() method with stock management
  - Integration tests with complete workflows
  - Edge cases with special material names and unicode

### 8. SystemConfigTest.java
- **Purpose**: Tests the SystemConfig static methods
- **Coverage**:
  - All getter/setter methods for configuration values
  - Validation of input values (negative values, bounds checking)
  - getConfigurationSummary() method formatting
  - resetToDefaults() method
  - Edge cases with extreme values and precision

### 9. ExceptionClassesTest.java
- **Purpose**: Tests all exception classes
- **Coverage**:
  - InvalidOrderException constructors and message handling
  - InsufficientMaterialException with material details
  - InvalidMaterialException with validation errors
  - Exception inheritance and behavior
  - Edge cases with special characters and unicode

## Running the Tests

### Prerequisites
- Java 8 or higher
- JUnit 5 (junit-platform-console-standalone-1.10.1.jar)

### Method 1: Using JUnit Console Launcher
```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar --scan-classpath --details=verbose
```

### Method 2: Using the Custom Test Runner
```bash
java -cp .:lib/junit-platform-console-standalone-1.10.1.jar JUnitTestRunner
```

### Method 3: Running Individual Test Classes
```bash
java -cp .:lib/junit-platform-console-standalone-1.10.1.jar org.junit.platform.console.ConsoleLauncher --select-class=UserTest
```

## Test Coverage

The comprehensive test suite provides:

- **Constructor Testing**: All constructors with valid, invalid, and edge case inputs
- **Method Testing**: All public methods with various input scenarios
- **Validation Testing**: Input validation and error handling
- **Edge Case Testing**: Boundary conditions, null values, empty strings, special characters
- **Integration Testing**: Complete workflows and interactions between classes
- **Error Condition Testing**: Exception handling and error scenarios
- **Performance Testing**: Large data sets and stress scenarios

## Test Statistics

- **Total Test Classes**: 9
- **Estimated Test Methods**: 200+ individual test methods
- **Coverage Areas**: 
  - Unit tests for all classes
  - Integration tests for workflows
  - Edge case and error condition testing
  - Performance and stress testing

## Key Testing Features

1. **Comprehensive Coverage**: Every public method and constructor is tested
2. **Edge Case Handling**: Tests include null values, empty strings, extreme values
3. **Error Validation**: Tests verify proper error handling and validation
4. **Integration Testing**: Tests verify complete workflows from order to invoice
5. **Performance Testing**: Tests include scenarios with large data sets
6. **Unicode Support**: Tests include special characters and unicode text
7. **Boundary Testing**: Tests include minimum and maximum values

## Maintenance

The test suite is designed to be maintainable and extensible:

- Each test class is independent and can be run separately
- Tests use proper setup and teardown methods
- Test data is isolated and doesn't interfere between tests
- Clear naming conventions make tests easy to understand
- Comprehensive documentation makes tests easy to maintain

## Future Enhancements

The test suite can be extended with:

- Performance benchmarks
- Load testing scenarios
- Database integration tests
- API endpoint testing
- User interface testing
- Security testing scenarios
