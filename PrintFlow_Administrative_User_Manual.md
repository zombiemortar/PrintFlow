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
11. [Troubleshooting](#troubleshooting)
12. [Best Practices](#best-practices)

---

## Introduction

**PrintFlow** is a comprehensive 3D printing service management system designed to streamline business operations from customer orders to invoice delivery. This manual is specifically written for administrative users who need to manage the system, configure settings, and oversee daily operations.

### What This Manual Covers
This manual focuses exclusively on administrative functions available through the graphical user interface (GUI). It does not cover technical development or command-line operations.

### Administrative Capabilities
As an administrative user, you have access to:
- Complete system oversight and management
- User account creation and management
- Material inventory control
- Order queue management
- Pricing configuration
- Invoice generation and export
- System configuration and maintenance

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
2. Check "Remember Me" if you want automatic login on future sessions
3. Click "Login" to access the system
4. If you don't have an account, click "Create Account" (admin approval may be required)

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

### Status Information
- **Welcome Message**: Shows your current username
- **Status Bar**: Displays current system status and operation feedback

---

## User Management

### Creating New User Accounts
1. From the login screen, click "Create Account"
2. Fill in the account creation form:
   - **Account Type**: Select from Customer, VIP, or Admin
   - **Username**: Must be unique (3-20 characters)
   - **Email**: Valid email address format
   - **Password**: Must meet security requirements
   - **Confirm Password**: Must match the password
3. Click "Create Account" to submit

### Password Requirements
All passwords must meet these security standards:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character
- Cannot be a common/weak password

### Account Types
- **Customer**: Standard users with basic order capabilities
- **VIP**: Premium users with priority processing and potential discounts
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
1. Click "Add Material" button
2. Fill in the material details:
   - **Brand**: Material manufacturer
   - **Type**: Material type (PLA, ABS, PETG, etc.)
   - **Cost per Gram**: Pricing information
   - **Print Temperature**: Recommended printing temperature
   - **Color**: Available color options
3. Click "Add" to add the material to inventory
4. New materials are automatically stocked with 1000 grams

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
   - **Material**: Select from available materials
   - **Dimensions**: Enter length × width × height in centimeters
   - **Quantity**: Number of items to print
   - **Material Grams**: Estimated material usage per item
   - **Priority**: Normal or Rush (if rush orders are enabled)
   - **Special Instructions**: Any specific requirements
3. Review the order summary
4. Click "Submit Order" to add to the queue

### Managing the Order Queue
1. Click "Order Queue" from the Dashboard
2. View all orders in the system with:
   - Order ID and customer information
   - Material and quantity details
   - Current status (pending, processing, completed)
   - Priority level
   - Estimated completion time

### Order Status Management
- **Set Status**: Change order status between pending, processing, and completed
- **Change Priority**: Modify order priority (normal to rush, if allowed)
- **Refresh Queue**: Update the display with latest information

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
- **Tax Rate**: Sales tax percentage
- **Rush Order Surcharge**: Additional cost for rush orders
- **VIP Discount**: Discount percentage for VIP customers
- **Bulk Order Discount**: Discount for large quantity orders
- **Maximum Order Quantity**: Order size limits

### Modifying Configuration
1. Change any parameter value in the form
2. Click "Save" to apply changes
3. Changes take effect immediately for new orders
4. Click "Reset" to restore default values (with confirmation)

### Configuration Best Practices
- Review pricing regularly to maintain profitability
- Test configuration changes with sample orders
- Document any custom pricing rules
- Backup configuration before major changes

---

## Invoice Management

### Generating Invoices
1. Click "Invoices" from the Dashboard
2. Search for orders by Order ID
3. Click "Generate Invoice" to create an invoice
4. Review the invoice details and pricing breakdown

### Invoice Features
- **Order Details**: Complete order specifications
- **Pricing Breakdown**: Detailed cost analysis including:
  - Material costs
  - Labor costs
  - Electricity costs
  - Setup fees
  - Taxes and discounts
- **Professional Formatting**: Clean, business-ready invoice layout

### Export Options
- **CSV Export**: For spreadsheet integration
- **JSON Export**: For data exchange and integration
- **Print**: Direct printing capability

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

#### Performing a Factory Reset
1. Access the Factory Reset function (admin-only)
2. Read the warning message carefully
3. Confirm the action in the dialog
4. System will reset and restart with default settings

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
- `users.txt`: User account information
- `materials.txt`: Material definitions and properties
- `orders.txt`: Order history and status
- `inventory.txt`: Current stock levels
- `system_config.txt`: Business rules and pricing

### Backup Management
- **Automatic Backups**: Created with timestamps in `backups/` directory
- **Manual Exports**: Use export functions for additional data protection
- **Recovery**: Restore from backup files if needed

---

## Troubleshooting

### Common Issues and Solutions

#### Login Problems
- **Forgotten Password**: Contact system administrator for password reset
- **Account Locked**: Check with administrator for account status
- **Invalid Credentials**: Verify username and password accuracy

#### Order Issues
- **Order Not Submitting**: Check material availability and order limits
- **Pricing Errors**: Verify system configuration settings
- **Queue Problems**: Refresh the order queue display

#### Inventory Problems
- **Stock Not Updating**: Refresh inventory display and check data files
- **Material Not Available**: Verify material is properly added to system
- **Cost Discrepancies**: Check material cost settings

#### System Performance
- **Slow Response**: Check system resources and restart if needed
- **Data Not Saving**: Verify file permissions and disk space
- **Display Issues**: Refresh displays and restart application

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
- Use strong, unique passwords
- Log out when finished
- Don't share login credentials
- Regular password updates

### Data Management
- Regular system backups
- Monitor disk space
- Keep data files organized
- Test restore procedures

### Customer Service
- Process orders promptly
- Communicate delays to customers
- Maintain accurate pricing
- Provide detailed invoices

### System Maintenance
- Regular configuration reviews
- Monitor system performance
- Update material information
- Review pricing accuracy

---

## Conclusion

PrintFlow provides comprehensive tools for managing a 3D printing service business. This manual covers all administrative functions available through the GUI interface. For technical support or advanced configuration, contact your system administrator.

Remember to:
- Keep your password secure
- Regular backup your data
- Monitor system performance
- Maintain accurate inventory
- Process orders efficiently

**PrintFlow - Streamlining Your 3D Printing Business Operations**

---

*This manual covers PrintFlow version 1.0.0. For updates and additional features, check with your system administrator.*
