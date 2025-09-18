# PrintFlow

## Project Introduction and Purpose

This Java-based system provides a comprehensive solution for managing a 3D printing service business. The system handles the complete workflow from order submission to invoice generation, including user management, material tracking, pricing calculations, and administrative oversight.

### Key Features
- **User Management**: Support for both regular customers and administrative users
- **Order Processing**: Complete order lifecycle management with status tracking
- **Material Management**: Flexible material system with cost and property tracking
- **Pricing Engine**: Automated cost calculation based on materials, quantity, and system configuration
- **Invoice Generation**: Professional invoice creation and export functionality
- **System Configuration**: Centralized management of pricing constants and business rules
- **Exception Handling**: Robust error handling with custom exception classes

### Business Value
- Streamlines 3D printing service operations
- Automates pricing calculations and invoice generation
- Provides administrative oversight and system management
- Ensures consistent order processing and customer service
- Scales to handle multiple users and complex order scenarios

## Technical Architecture Overview

The system follows object-oriented design principles with a modular architecture that separates concerns and promotes maintainability.

### Design Patterns Used
- **Inheritance**: `AdminUser` extends `User` for role-based functionality
- **Singleton-like**: `SystemConfig` provides centralized configuration management
- **Factory Pattern**: Order and Invoice creation with automatic ID generation
- **Exception Handling**: Custom exception classes for specific error scenarios

### System Components
1. **User Management Layer**: Handles user authentication and role-based access
2. **Order Processing Layer**: Manages order creation, validation, and status updates
3. **Pricing Engine**: Calculates costs based on materials and system configuration
4. **Invoice System**: Generates and exports professional invoices
5. **Configuration Management**: Centralized system settings and business rules

## Class Structure and Relationships

### Core Classes

#### User Hierarchy
```
User (Base Class)
├── username: String
├── email: String
├── role: String
├── submitOrder(): Order
└── viewInvoice(): Invoice

AdminUser (Extends User)
├── addMaterial(): Material
├── viewAllOrders(): Order[]
└── modifyPricingConstants(): boolean
```

#### Order Management
```
Order
├── orderID: int (auto-generated)
├── user: User
├── material: Material
├── dimensions: String
├── quantity: int
├── specialInstructions: String
├── status: String
├── calculatePrice(): double
└── updateStatus(): boolean
```

#### Material System
```
Material
├── name: String
├── costPerGram: double
├── printTemp: int
├── color: String
└── getMaterialInfo(): String
```

#### Invoice System
```
Invoice
├── invoiceID: int (auto-generated)
├── order: Order
├── totalCost: double
├── dateIssued: LocalDateTime
├── generateSummary(): String
└── exportInvoice(): String
```

#### Configuration Management
```
SystemConfig (Static Utility Class)
├── Pricing Constants (electricity, machine time, setup costs)
├── Tax Rates and Currency Settings
├── Order Limits and Business Rules
└── Configuration Management Methods
```

#### Exception Classes
```
InvalidOrderException
InsufficientMaterialException
InvalidMaterialException
```

### Class Relationships
- `User` creates `Order` objects through `submitOrder()`
- `Order` references `User` and `Material` objects
- `Invoice` contains a reference to an `Order`
- `AdminUser` can modify `SystemConfig` settings
- All classes use `SystemConfig` for configuration values

## Setup and Running Instructions

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, VS Code) or command line

### Installation
1. **Clone or Download** the project files to your local machine
2. **Ensure all Java files** are in the same directory:
   - `User.java`
   - `AdminUser.java`
   - `Material.java`
   - `Order.java`
   - `Invoice.java`
   - `SystemConfig.java`
   - `InvalidOrderException.java`
   - `InsufficientMaterialException.java`
   - `InvalidMaterialException.java`
   - `Tests.java`

### Compilation
```bash
# Compile all Java files
javac *.java
```

### Running the Application
```bash
# Run the tests to see the system in action
java Tests
```

### IDE Setup
1. **Create a new Java project** in your IDE
2. **Import all Java files** into the project
3. **Set the main class** to `Tests` for testing
4. **Run the project** to see the system demonstration

## Testing Information

### Test Driver: Tests.java
The `Tests` class exercises core and extended functionality of the system:

#### Test Coverage
1. **User Management Testing**
   - Regular user creation and display
   - Admin user creation and role verification

2. **Material System Testing**
   - Material creation with properties
   - Material information display

3. **System Configuration Testing**
   - Configuration summary display
   - Pricing constant modification

4. **Order Processing & Queue**
   - Order creation and submission
   - Registration in `OrderManager` and enqueuing in FIFO print queue
   - Dequeue next order and verify queue size
   - Status updates

5. **Invoice System Testing**
   - Invoice creation
   - Summary generation
   - Full invoice export

6. **Exception Handling Testing**
   - Custom exception creation and handling
   - Error message verification

7. **Inventory & Materials**
   - Admin seeds inventory via `AdminUser.addMaterial()`
   - Stock lookup and consumption on order submission

8. **Pricing, Priority, and Estimates**
   - Pricing includes base setup, material, electricity, and machine time costs
   - Quantity discount (>= 10), optional VIP discount, rush surcharge
   - Tax application via `SystemConfig`
   - Naive time estimation from dimensions/quantity (`Order.estimatePrintTimeHours()`)

9. **Admin Views Orders**
   - `AdminUser.viewAllOrders()` retrieves all registered orders

### Running Tests
```bash
# Execute the test suite
java Tests
```

### Expected Output
The test driver will output:
- User creation and display
- Material information
- System configuration details
- Order processing workflow
- Invoice generation
- Exception handling demonstrations

### Manual Testing
You can also create your own test scenarios by:
1. Creating `User` and `AdminUser` instances
2. Defining `Material` objects
3. Submitting orders through users
4. Generating invoices
5. Modifying system configuration

## Implementation Details

### Key Implementation Features

#### Automatic ID Generation
- **Order IDs**: Start at 1000 and increment automatically
- **Invoice IDs**: Start at 2000 and increment automatically
- **Thread-safe**: Uses static counters for unique ID generation

#### Pricing Algorithm
```java
// Basic pricing formula implemented in Order.calculatePrice()
double materialCost = material.getCostPerGram() * quantity * 10; // 10g per item
double baseCost = SystemConfig.getBaseSetupCost();
double totalCost = materialCost + baseCost;
```

#### Status Management
- Orders start with "pending" status
- Status can be updated through `updateStatus()` method
- Supports custom status values for workflow tracking

#### Configuration Management
- **Centralized Settings**: All business rules in `SystemConfig`
- **Runtime Modification**: Admin users can update pricing constants
- **Validation**: Input validation for configuration changes
- **Default Values**: System can reset to default configuration

#### Exception Handling Strategy
- **Custom Exceptions**: Specific exception types for different error scenarios
- **Graceful Degradation**: System continues operation despite errors
- **Informative Messages**: Detailed error information for debugging

### Future Enhancements Planned
1. **Data Persistence**: File-based storage for orders and users
2. **Advanced Pricing**: More sophisticated cost calculations
3. **Inventory Management**: Material stock tracking
4. **User Interface**: Graphical user interface development
5. **Reporting**: Analytics and business intelligence features
6. **Security**: User authentication and authorization

### Performance Considerations
- **Memory Efficient**: Minimal object creation and cleanup
- **Fast Lookups**: Direct object references for order processing
- **Scalable Design**: Modular architecture supports future enhancements
- **Resource Management**: Proper exception handling prevents resource leaks

### Code Quality Standards
- **Documentation**: Comprehensive JavaDoc comments
- **Naming Conventions**: Consistent Java naming standards
- **Error Handling**: Robust exception management
- **Modularity**: Clear separation of concerns
- **Maintainability**: Clean, readable code structure
