# PrintFlow - 3D Printing Service Management System

## What This Program Does

PrintFlow is a Java-based business management system for 3D printing service providers. It automates the complete workflow from customer orders to invoice delivery.

### Key Features
- **Customer Management**: Secure user accounts and authentication
- **Order Processing**: Submit orders with material specifications and pricing
- **Material Management**: Track 3D printing materials and inventory
- **Automatic Pricing**: Calculate costs based on materials, time, and discounts
- **Invoice Generation**: Create professional invoices with export options
- **Data Management**: Save/load all business data with backup protection
- **Admin Controls**: Manage users, materials, and system settings

### How It Works
1. **Customers** create accounts and submit 3D printing orders
2. **System** calculates pricing based on materials, quantity, and special requirements
3. **Orders** are queued for processing with automatic status tracking
4. **Invoices** are generated with detailed cost breakdowns
5. **Admins** can manage materials, view all orders, and modify system settings

## Quick Start

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6 or higher

### Running the JavaFX GUI Application (Windows)

#### Method 1: Using Maven (Recommended)
1. Open Command Prompt or PowerShell in the project directory
2. Compile and run the application:
   ```cmd
   mvn clean javafx:run
   ```

#### Method 2: Using Maven with JavaFX Plugin
1. Compile the project:
   ```cmd
   mvn clean compile
   ```
2. Run the application:
   ```cmd
   mvn javafx:run
   ```

#### Method 3: Manual JavaFX Module Path (Alternative)
If Maven JavaFX plugin doesn't work, you can run manually:
1. Download JavaFX SDK 21.0.4 from [OpenJFX](https://openjfx.io/)
2. Extract to a directory (e.g., `C:\javafx-sdk-21.0.4`)
3. Compile the project:
   ```cmd
   mvn clean compile
   ```
4. Run with module path:
   ```cmd
   java --module-path "C:\javafx-sdk-21.0.4\lib" --add-modules javafx.controls,javafx.fxml -cp "target\classes" gui.Main
   ```

### Running the CLI Version (Legacy)
1. Compile: `javac *.java`
2. Run demo: `java Tests`
3. Run CLI (interactive): `java MainCLI`
4. Run CLI (single command):
   - Add material: `java MainCLI add-material PLA 0.02 200 White`
   - Place order: `java MainCLI place-order john_doe PLA 2 "10cm x 5cm x 3cm" rush`

### What You'll See
The program demonstrates:
- User account creation
- Material management
- Order submission and pricing
- Invoice generation
- Admin functions

## System Components

### Core Classes
- **User**: Customer accounts with order submission
- **AdminUser**: Administrative users with system management
- **Material**: 3D printing materials (PLA, ABS, etc.)
- **Order**: Customer orders with specifications and pricing
- **Invoice**: Professional invoices with cost breakdowns
- **SystemConfig**: Business rules and pricing settings

### Data Management
- **DataManager**: Handles saving/loading all business data
- **FileManager**: Basic file operations and directory management
- **ExportManager**: Export invoices and reports to CSV/JSON

### Security Features
- **SecurityManager**: Password protection and user authentication
- **InputValidator**: Protection against malicious input

## Configuration

The system uses external configuration files for business settings:
- Pricing constants (material costs, setup fees, electricity rates)
- Tax rates and currency settings
- Order limits and business rules
- Rush order surcharges and discount policies

Configuration files are stored in the `data/` directory and can be modified by administrators.

## Data Storage

All business data is automatically saved to files:
- `data/users.txt` - User accounts
- `data/materials.txt` - Material definitions
- `data/orders.txt` - Order history
- `data/inventory.txt` - Material stock levels
- `data/system_config.txt` - System settings

Backups are automatically created in the `backups/` directory with timestamps.

## Export Features

The system can export data in multiple formats:
- **CSV Export**: For spreadsheets and accounting systems
- **JSON Export**: For data exchange and integration
- **Invoice Export**: Professional invoice formatting
- **System Reports**: Comprehensive business analytics

## Project Status

✅ **Phase 1-5 Complete**: All core functionality implemented
- User management and authentication
- Order processing and pricing
- Material and inventory management
- Data persistence and backup
- Security and input validation
- Export and reporting capabilities

✅ **JavaFX GUI Complete**: Full graphical user interface implemented
- Modern JavaFX interface with FXML layouts
- Complete user workflow from login to invoice generation
- Theme support (light/dark mode)
- Data persistence and factory reset functionality
- Export capabilities (CSV/JSON)
- Responsive design with proper error handling

## Business Value

PrintFlow streamlines 3D printing service operations by:
- Automating complex pricing calculations
- Managing customer orders and inventory
- Generating professional invoices
- Providing administrative oversight
- Protecting business data with backups
- Supporting business growth and scaling

Perfect for small to medium 3D printing service businesses looking to professionalize their operations.

## Troubleshooting

### Common Issues on Windows

**JavaFX Module Not Found Error:**
- Ensure you're using JDK 17 or higher
- Use the Maven JavaFX plugin: `mvn javafx:run`
- If that fails, download JavaFX SDK and use the manual module path method

**Maven Not Found:**
- Install Maven from [Apache Maven](https://maven.apache.org/download.cgi)
- Add Maven to your system PATH
- Verify installation: `mvn --version`

**Application Won't Start:**
- Check that all dependencies are installed: `mvn dependency:resolve`
- Ensure the `data/` directory exists with proper permissions
- Check console output for specific error messages

**GUI Not Displaying:**
- Verify JavaFX dependencies in `pom.xml`
- Try running with: `mvn clean javafx:run`
- Check that your system supports JavaFX (most modern Windows systems do)

### Default Login Credentials
- **Admin Account**: username: `admin`, password: `admin`
- **Factory Reset**: Available from admin dashboard to restore default settings

## Software Bill of Materials (SBOM)

### Project Information
- **Project Name**: PrintFlow
- **Version**: 1.0.0
- **Language**: Java
- **Build System**: Apache Maven
- **Java Version**: 17+

### Dependencies

#### Runtime Dependencies
| Component | Version | License | Purpose |
|-----------|---------|---------|---------|
| **JavaFX Controls** | 21.0.4 | GPL v2 with Classpath Exception | GUI components and controls |
| **JavaFX FXML** | 21.0.4 | GPL v2 with Classpath Exception | FXML layout and scene management |

#### Development Dependencies
| Component | Version | License | Purpose |
|-----------|---------|---------|---------|
| **JUnit Jupiter Engine** | 5.10.0 | EPL 2.0 | Test execution engine |
| **JUnit Jupiter API** | 5.10.0 | EPL 2.0 | Test framework API |

#### Build Tools
| Component | Version | License | Purpose |
|-----------|---------|---------|---------|
| **Maven Compiler Plugin** | 3.11.0 | Apache 2.0 | Java compilation |
| **JavaFX Maven Plugin** | 0.0.8 | Apache 2.0 | JavaFX application packaging |
| **Maven Surefire Plugin** | 3.1.2 | Apache 2.0 | Test execution |

#### Java Standard Library
- **java.util** - Collections, data structures, utilities
- **java.io** - File I/O operations
- **java.security** - Security and cryptography
- **java.time** - Date and time handling
- **java.lang** - Core language features

### Security Considerations
- **Password Security**: BCrypt-style hashing implemented
- **Input Validation**: Comprehensive validation against injection attacks
- **Session Management**: Secure session handling with timeouts
- **Data Protection**: Automatic backup and restore functionality

### License Summary
- **Primary License**: GPL v2 with Classpath Exception (JavaFX components)
- **Test Framework**: EPL 2.0 (JUnit)
- **Build Tools**: Apache 2.0 (Maven plugins)
- **Custom Code**: Proprietary (PrintFlow application code)

### Vulnerability Management
- All dependencies are regularly updated to latest stable versions
- Security patches applied through Maven dependency management
- No known critical vulnerabilities in current dependency versions

---

## Copyright

© 2025 Joseph M. Sparks. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or modification is strictly prohibited.