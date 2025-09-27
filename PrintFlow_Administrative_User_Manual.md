# PrintFlow Administrative User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [Dashboard Overview](#dashboard-overview)
4. [User Management](#user-management)
5. [Inventory Management](#inventory-management)
6. [Order Management](#order-management)
7. [Pricing Configuration](#pricing-configuration)
8. [Invoice Management](#invoice-management)
9. [System Administration](#system-administration)
10. [Data Management](#data-management)
11. [Theme Management](#theme-management)
12. [Export and Reporting](#export-and-reporting)
13. [Security Features](#security-features)
14. [Troubleshooting](#troubleshooting)
15. [Best Practices](#best-practices)

---

## Introduction

**PrintFlow** is a comprehensive 3D printing service management system designed to streamline business operations from customer orders to invoice delivery. This manual is specifically written for administrative users who need to manage the system, configure settings, and oversee daily operations.

### What This Manual Covers
This manual focuses exclusively on administrative functions available through the graphical user interface (GUI). It does not cover technical development or command-line operations.

### Administrative Capabilities
As an administrative user, you have access to:
- Complete system oversight and management
- User account creation and management with role-based access
- Material inventory control and stock management
- Order queue management and status tracking
- Advanced pricing configuration and business rules
- Professional invoice generation and export
- System configuration and maintenance
- Theme management (light/dark mode)
- Comprehensive data export and reporting
- Factory reset functionality
- Security management and session control

---

## Getting Started

### System Requirements
- **Operating System**: Windows 10 or later
- **Java Runtime**: Java 17 or higher (if using JAR version)
- **Memory**: Minimum 4GB RAM recommended
- **Storage**: 100MB free disk space

### First-Time Setup
1. **Launch PrintFlow**: Double-click the PrintFlow executable or JAR file
2. **Default Admin Account**: 
   - Username: `admin`
   - Password: `admin`
3. **Change Default Password**: Immediately change the default password for security

### Login Process
1. Enter your username and password
2. Check "Remember Me" if you want automatic login on future sessions (7-day session validity)
3. Click "Login" to access the system
4. If you don't have an account, click "Create Account" to open the account creation window
5. The system supports secure session management with automatic timeout

---

## Dashboard Overview

The Dashboard is your central command center for managing the PrintFlow system.

### Main Navigation
The Dashboard provides access to all major system functions:

- **New Order**: Create orders on behalf of customers
- **Order Queue**: Manage the print job pipeline
- **Inventory**: Control material stock and availability
- **Pricing/Config**: Configure system pricing and business rules
- **Invoices**: Generate and manage customer invoices
- **Logout**: Securely exit the system

### Additional Features
- **Theme Toggle**: Switch between light and dark themes
- **Export System Reports**: Generate comprehensive CSV and JSON reports
- **Factory Reset**: Admin-only complete system reset (visible only to admin users)

### Status Information
- **Welcome Message**: Shows your current username
- **Status Bar**: Displays current system status and operation feedback
- **Real-time Updates**: Status messages update as you perform actions

---

## User Management

### Creating New User Accounts
1. From the login screen, click "Create Account" to open the account creation window
2. Fill in the account creation form:
   - **Account Type**: Select from User, Customer, VIP, or Admin
   - **Username**: Must be unique (3-20 characters)
   - **Email**: Valid email address format
   - **Password**: Must meet security requirements
   - **Confirm Password**: Must match the password
3. The system provides real-time password strength feedback
4. Click "Create Account" to submit
5. The window closes automatically upon successful creation

### Password Requirements
All passwords must meet these security standards:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character
- Cannot be a common/weak password

### Account Types
- **User**: Basic users with standard order capabilities
- **Customer**: Standard users with basic order capabilities
- **VIP**: Premium users with priority processing and 10% discount on orders
- **Admin**: Full system access and management capabilities

### User Account Management
- View all user accounts through the system
- Monitor user activity and order history
- Manage user permissions and roles

---

## Inventory Management

The Inventory section allows you to manage all 3D printing materials and stock levels.

### Viewing Current Inventory
1. Click "Inventory" from the Dashboard
2. View the materials table showing:
   - Material name and type
   - Cost per gram
   - Print temperature
   - Available colors
   - Current stock levels

### Adding New Materials
1. Click "Add Material" button to open the material creation dialog
2. Fill in the material details:
   - **Brand**: Material manufacturer (e.g., Overture, Hatchbox)
   - **Type**: Material type (PLA, ABS, PETG, etc.)
   - **Cost per Gram**: Pricing information (e.g., 0.02)
   - **Print Temperature**: Recommended printing temperature in Celsius
   - **Color**: Available color options (e.g., Black, White, Red)
3. All fields are required and validated in real-time
4. Click "Add" to add the material to inventory
5. New materials are automatically stocked with 1000 grams

### Updating Stock Levels
1. In the inventory table, click "Update Stock" for any material
2. Enter the new stock amount in grams
3. Click "Update" to save changes
4. Stock levels are automatically updated across the system

### Removing Materials
1. Click "Remove" button next to any material
2. Confirm the removal in the dialog box
3. **Warning**: Removing materials will affect existing orders

### Inventory Best Practices
- Regularly update stock levels after material usage
- Monitor low stock alerts
- Keep material costs current for accurate pricing
- Maintain accurate print temperature settings

---

## Order Management

### Creating Orders
1. Click "New Order" from the Dashboard
2. Fill in the order form:
   - **Material**: Select from available materials (organized by Type → Brand → Color)
   - **Dimensions**: Enter length × width × height in centimeters (e.g., 10x5x3)
   - **Quantity**: Number of items to print (validated against system limits)
   - **Material Grams**: Estimated material usage per item in grams
   - **Priority**: Normal, Rush, or VIP (if rush orders are enabled)
   - **Special Instructions**: Any specific requirements or notes
3. The system provides real-time material information and order summary
4. Review the order summary with estimated pricing
5. Click "Submit Order" to add to the queue
6. Material stock is automatically updated upon order submission

### Managing the Order Queue
1. Click "Order Queue" from the Dashboard
2. View all orders in the system with:
   - Order ID and customer information
   - Material and quantity details
   - Current status (pending, processing, completed)
   - Priority level (normal, rush, vip)
   - Estimated print hours
3. Queue status summary shows total, pending, processing, and completed orders

### Order Status Management
- **Set Processing**: Change order status to processing
- **Set Completed**: Mark order as completed
- **Set Rush**: Upgrade order priority to rush (if enabled)
- **Refresh Queue**: Update the display with latest information
- **Selection Required**: Select an order from the table before performing actions

### Order Processing Workflow
1. Orders are automatically queued in FIFO (First In, First Out) order
2. Rush orders are prioritized when enabled
3. VIP customers may receive priority processing
4. Orders consume inventory automatically when submitted

---

## Pricing Configuration

The Pricing/Config section allows you to manage all business rules and pricing parameters.

### Accessing Configuration
1. Click "Pricing/Config" from the Dashboard
2. View current system configuration values
3. Modify settings as needed for your business

### Configurable Parameters
- **Electricity Cost**: Cost per hour for machine operation
- **Machine Time Cost**: Labor cost per hour
- **Base Setup Cost**: Fixed cost for order setup
- **Tax Rate**: Sales tax percentage (0-100%)
- **Currency**: Currency symbol (e.g., USD, EUR)
- **Rush Order Surcharge**: Additional cost percentage for rush orders
- **Allow Rush Orders**: Enable/disable rush order functionality
- **Maximum Order Quantity**: Order size limits
- **Maximum Order Value**: Maximum total order value

### Modifying Configuration
1. Change any parameter value in the form
2. Real-time validation provides visual feedback (green/red borders)
3. Click "Save" to apply changes to both memory and file
4. Click "Load" to reload configuration from file
5. Click "Refresh" to update form with current memory values
6. Click "Reset" to restore default values (with confirmation)
7. Changes take effect immediately for new orders

### Configuration Best Practices
- Review pricing regularly to maintain profitability
- Test configuration changes with sample orders
- Document any custom pricing rules
- Backup configuration before major changes

---

## Invoice Management

### Generating Invoices
1. Click "Invoices" from the Dashboard
2. Search for orders by Order ID or click "Show All Orders"
3. Select an order from the table and click "Generate Invoice"
4. Review the invoice details and pricing breakdown
5. The invoice displays comprehensive order and cost information

### Invoice Features
- **Order Details**: Complete order specifications including customer information
- **Pricing Breakdown**: Detailed cost analysis including:
  - Material costs (based on actual usage)
  - Labor costs (machine time)
  - Electricity costs
  - Setup fees
  - Taxes and discounts (VIP, bulk quantity)
- **Professional Formatting**: Clean, business-ready invoice layout
- **Invoice ID**: Unique identifier for each invoice
- **Date Issued**: Timestamp of invoice generation

### Export Options
- **CSV Export**: For spreadsheet integration with timestamped filenames
- **JSON Export**: For data exchange and integration with timestamped filenames
- **Clear**: Reset the invoice display
- **Hide Orders**: Hide the orders table to focus on invoice

### Invoice Management Best Practices
- Generate invoices promptly after order completion
- Review pricing accuracy before sending to customers
- Maintain organized invoice records
- Use export features for accounting integration

---

## System Administration

### Factory Reset Functionality
**⚠️ WARNING: This action is irreversible and will delete all data!**

The Factory Reset feature restores the system to its original state:
- Resets all pricing to default values
- Clears all materials from inventory
- Removes all user accounts except default admin
- Clears all orders and order queue data
- Resets system configuration to defaults
- Creates a new default admin account (username: admin, password: admin)

#### Performing a Factory Reset
1. Access the Factory Reset function (admin-only, visible only to admin users)
2. Read the comprehensive warning message carefully
3. Confirm the action in the first dialog
4. Provide final confirmation in the second dialog
5. System will reset and redirect to login screen with default settings

### Data Backup and Recovery
- **Automatic Backups**: System creates timestamped backups automatically
- **Backup Location**: `backups/` directory with date/time stamps
- **Manual Backup**: Use system export functions for additional backups

### System Monitoring
- Monitor order queue status
- Track inventory levels
- Review user activity
- Check system performance

---

## Data Management

### Automatic Data Persistence
PrintFlow automatically saves all data:
- **User accounts** and authentication data
- **Material inventory** and stock levels
- **Order history** and queue status
- **System configuration** and pricing rules
- **Invoice records** and export data

### Data Files
All data is stored in the `data/` directory:
- `users.txt`: User account information with hashed passwords
- `materials.txt`: Material definitions and properties
- `orders.txt`: Order history and status
- `inventory.txt`: Current stock levels
- `system_config.txt`: Business rules and pricing
- `order_queue.txt`: Active order queue
- `user_session.txt`: Session data for "Remember Me" functionality

### Backup Management
- **Automatic Backups**: Created with timestamps in `backups/` directory
- **Manual Exports**: Use export functions for additional data protection
- **Recovery**: Restore from backup files if needed

---

## Theme Management

### Light and Dark Themes
PrintFlow supports both light and dark themes for improved user experience:

- **Theme Toggle**: Available on the Dashboard
- **Automatic Application**: Theme changes apply immediately to the current scene
- **Persistent Settings**: Theme preference is maintained during the session
- **Visual Feedback**: Button text updates to show current theme and next action

### Using Themes
1. Click the theme toggle button on the Dashboard
2. The interface switches between light and dark modes
3. Button text changes to indicate the next theme (🌙 Dark / ☀️ Light)
4. Status message confirms the theme change

---

## Export and Reporting

### System Reports
Generate comprehensive system reports from the Dashboard:

- **CSV Export**: Complete system data in spreadsheet format
- **JSON Export**: Structured data for integration and analysis
- **Timestamped Files**: Automatic filename generation with current date/time
- **Comprehensive Data**: Includes users, materials, orders, inventory, and configuration

### Invoice Exports
Individual invoice exports with multiple formats:

- **CSV Format**: Spreadsheet-compatible invoice data
- **JSON Format**: Structured invoice information
- **Professional Layout**: Business-ready invoice formatting
- **Unique Filenames**: Automatic timestamp-based naming

---

## Security Features

### Password Security
- **BCrypt Hashing**: Secure password storage with salt
- **Strength Validation**: Real-time password strength assessment
- **Common Password Detection**: Rejects easily guessable passwords
- **Character Requirements**: Enforces complexity rules

### Session Management
- **Secure Sessions**: 32-character session IDs
- **Automatic Timeout**: 30-minute inactivity timeout
- **Session Limits**: Maximum 3 concurrent sessions per user
- **Remember Me**: 7-day session validity for convenience

### Authentication
- **Role-Based Access**: Different permissions for user types
- **Admin Controls**: Factory reset and system management
- **Session Validation**: Continuous authentication verification
- **Secure Logout**: Complete session cleanup

---

## Troubleshooting

### Common Issues and Solutions

#### Login Problems
- **Forgotten Password**: Contact system administrator for password reset
- **Account Locked**: Check with administrator for account status
- **Invalid Credentials**: Verify username and password accuracy
- **Session Expired**: Re-login if session has timed out (30 minutes)
- **Remember Me Not Working**: Check if session file exists and is valid

#### Order Issues
- **Order Not Submitting**: Check material availability and order limits
- **Pricing Errors**: Verify system configuration settings
- **Queue Problems**: Refresh the order queue display
- **Material Not Available**: Ensure material is in inventory with sufficient stock
- **Validation Errors**: Check all form fields meet requirements

#### Inventory Problems
- **Stock Not Updating**: Refresh inventory display and check data files
- **Material Not Available**: Verify material is properly added to system
- **Cost Discrepancies**: Check material cost settings
- **Material Not Showing**: Ensure material was saved properly after creation
- **Stock Display Issues**: Check if inventory file is corrupted

#### System Performance
- **Slow Response**: Check system resources and restart if needed
- **Data Not Saving**: Verify file permissions and disk space
- **Display Issues**: Refresh displays and restart application
- **Theme Not Applying**: Restart application if theme changes don't take effect
- **Export Failures**: Check disk space and file permissions

### Getting Help
- Check system status messages for guidance
- Review error dialogs for specific information
- Contact system administrator for technical issues
- Refer to system logs for detailed error information

---

## Best Practices

### Daily Operations
1. **Start of Day**:
   - Check order queue status
   - Review inventory levels
   - Verify system configuration

2. **During Operations**:
   - Update order statuses regularly
   - Monitor stock levels
   - Process orders in queue order

3. **End of Day**:
   - Complete pending orders
   - Update inventory levels
   - Generate invoices for completed orders

### Security Best Practices
- Change default passwords immediately
- Use strong, unique passwords meeting all requirements
- Log out when finished to end sessions
- Don't share login credentials
- Regular password updates
- Use "Remember Me" only on trusted devices
- Monitor session activity

### Data Management
- Regular system backups using export functions
- Monitor disk space for data and backup files
- Keep data files organized in the data/ directory
- Test restore procedures from backups
- Export system reports regularly for analysis

### Customer Service
- Process orders promptly
- Communicate delays to customers
- Maintain accurate pricing
- Provide detailed invoices

### System Maintenance
- Regular configuration reviews and updates
- Monitor system performance and resource usage
- Update material information and costs
- Review pricing accuracy and profitability
- Test theme changes and UI responsiveness
- Verify export functionality regularly

---

## Conclusion

PrintFlow provides comprehensive tools for managing a 3D printing service business. This manual covers all administrative functions available through the GUI interface. For technical support or advanced configuration, contact your system administrator.

Remember to:
- Keep your password secure and change default passwords
- Regular backup your data using export functions
- Monitor system performance and resource usage
- Maintain accurate inventory and material information
- Process orders efficiently and update statuses
- Use theme management for better user experience
- Leverage export features for business analysis

**PrintFlow - Streamlining Your 3D Printing Business Operations**

---

*This manual covers PrintFlow version 1.0.0. For updates and additional features, check with your system administrator.*
