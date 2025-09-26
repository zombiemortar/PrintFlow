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
- Java Development Kit (JDK) 8 or higher

### Installation
1. Download all Java files to a directory
2. Compile: `javac *.java`
3. Run: `java Tests`

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

🔄 **Next Phase**: GUI development with JavaFX

## Business Value

PrintFlow streamlines 3D printing service operations by:
- Automating complex pricing calculations
- Managing customer orders and inventory
- Generating professional invoices
- Providing administrative oversight
- Protecting business data with backups
- Supporting business growth and scaling

Perfect for small to medium 3D printing service businesses looking to professionalize their operations.
