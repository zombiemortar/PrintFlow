# Software Bill of Materials (SBOM)

## Project Information
- **Project Name**: 3D Printing Service Management System
- **Version**: 1.0.0
- **Language**: Java
- **SBOM Generated**: 2024

## Dependencies

### Core Java Runtime
- **Name**: Java Development Kit (JDK)
- **Version**: 8 or higher
- **Type**: Runtime Dependency
- **License**: Oracle Binary Code License Agreement (BCLA) or OpenJDK GPL v2
- **Purpose**: Core Java runtime and development environment

### Java Standard Library
- **Name**: java.util
- **Version**: Included with JDK
- **Type**: Built-in Library
- **License**: Same as JDK
- **Components Used**:
  - `HashMap<Integer, Order>` - For order lookup by ID
  - `Queue<Order>` (LinkedList implementation) - For FIFO print queue
  - `Map` interface - For inventory management
  - `LocalDateTime` - For invoice timestamps
  - `DateTimeFormatter` - For date/time formatting

### Java Time API
- **Name**: java.time
- **Version**: Included with JDK 8+
- **Type**: Built-in Library
- **License**: Same as JDK
- **Components Used**:
  - `LocalDateTime` - For invoice date tracking
  - `DateTimeFormatter` - For date formatting

## External Dependencies
*None - This project uses only Java standard library components*

## Build Dependencies
- **Java Compiler (javac)**: Included with JDK
- **No external build tools required** (Maven, Gradle, etc.)

## Runtime Dependencies
- **Java Virtual Machine (JVM)**: Version 8 or higher
- **No external JAR files required**

## Security Considerations
- All dependencies are part of the Java standard library
- No third-party libraries with potential security vulnerabilities
- Regular JDK updates recommended for security patches

## License Summary
- **Project License**: Not specified (assumed proprietary)
- **Dependencies**: Oracle BCLA or OpenJDK GPL v2 (depending on JDK distribution)
- **Risk Level**: Low (only standard library dependencies)

## Notes
- This is a standalone Java application with no external dependencies
- All functionality is implemented using Java standard library components
- No package management system (Maven/Gradle) is used
- Compilation requires only `javac` and execution requires only `java` command

## File Structure
```
Project/
├── AdminUser.java
├── InsufficientMaterialException.java
├── InvalidMaterialException.java
├── InvalidOrderException.java
├── Invoice.java
├── Material.java
├── Order.java
├── OrderManager.java
├── Inventory.java
├── SystemConfig.java
├── Tests.java
├── User.java
├── README.md
├── To-Do.md
└── SBOM.md
```

## Verification
- All source files compile without external dependencies
- No import statements reference external libraries
- All functionality uses Java standard library APIs only
